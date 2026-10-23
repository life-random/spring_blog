package com.tenco.spring_blog.sms;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog._core.util.MailUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SmsService {
    private final Map<String, LocalDateTime> lastSentMap =
            new ConcurrentHashMap<>();

    private final DefaultMessageService messageService;
    private final String solapiSender;

    public SmsService(
            @Value("${solapi.api-key}") String solapiApiKey,
            @Value("${solapi.api-secret}") String solapiApiSecret,
            @Value("${solapi.sender}") String solapiSender
    ) {
        this.messageService = SolapiClient.INSTANCE.createInstance(
                solapiApiKey,
                solapiApiSecret
        );
        this.solapiSender = solapiSender;
    }
    private record SmsCode(String code, LocalDateTime expireAt) implements Serializable {
        boolean isExpired(){
            return LocalDateTime.now().isAfter(expireAt);
        }
    }

    // 1. 인증번호 발송
    public void sendVerificationCode(String phone, HttpSession session) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSentAt = lastSentMap.get(phone);

        // 마지막 발송 후 60초가 지나지 않았다면 거부
        if (lastSentAt != null &&
                now.isBefore(lastSentAt.plusSeconds(60))) {
            throw new IllegalStateException(
                    "60초 후 다시 시도해 주세요."
            );
        }

        // 1-1. 6자자리 인증번호 생성
        String code = MailUtil.generateCode();

        Message message = new Message();
        message.setFrom(solapiSender);
        message.setTo(phone);
        message.setText("[스프링블로그] "
                + "작성자 최병권"
                +" 인증번호 " + code
                +"\n(5분 안에 입력)");

        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            messageService.send(message);
        } catch (SolapiMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        session.setAttribute(Define.MESSAGE_CODE_PREFIX + phone,
                new SmsCode(code, LocalDateTime.now().plusMinutes(Define.CODE_EXPIRE_MINUTES))); // 분

        lastSentMap.put(phone, LocalDateTime.now());
        log.info("인증번호 발송 완료 - 메시지 : {}", phone); // 인증 번호 자체는 로그에 안남기는 게 좋음
    }

    // 2. 인증번호 확인 : 일치하면 true
    public boolean verifyCode(String phone, String code, HttpSession session) {
        String key = Define.MESSAGE_CODE_PREFIX + phone;
        SmsService.SmsCode savedSmsCode = (SmsService.SmsCode) session.getAttribute(key);

        // emailCode_a@naver.com : 123456
        if (savedSmsCode == null) {
            throw new Exception400("인증번호를 먼저 요청해주세요");
        }
        if (savedSmsCode.isExpired()) {
            session.removeAttribute(key); // 만료된 인증 번호는 세션에서 삭제
            throw new Exception400("인증번호가 만료되었습니다. 다시 요청해주세요");
        }
        if (!savedSmsCode.code().equals(code)) {
            throw new Exception400("인증번호가 일치하지 않습니다");
        }
        // 성공 :  사용한 인증번호는 삭제 (한 번만 쓸 수 있게)
        session.removeAttribute(key);
        // 인증을 마친 이메일을 세션에 기록해야 함 (회원가입 시 서버가 확인) - 인증된 이메일이 아닌 변조해서 던질 수 있음
        session.setAttribute(Define.VERIFIED_EMAIL, phone);
        return true;
    }

}
