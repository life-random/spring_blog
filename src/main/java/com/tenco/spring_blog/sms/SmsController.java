package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.util.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RequiredArgsConstructor // DI
@Slf4j
@Controller // IoC (제어의 역전) 싱글톤 패턴으로 관리 됨.
public class SmsController {

    private final SmsService smsService;

    @GetMapping("/user/sms")
    public String kakaoLogin() {
        return "sms/sms-form";
    }

    @PostMapping("/api/sms/send")
    public ResponseEntity<?> sendSmsCode(@RequestBody SmsRequest.SmsDto smsDto, HttpSession session){
        smsDto.validate();

        smsService.sendVerificationCode(smsDto.getPhone(), session);
        return ResponseEntity.ok(Map.of("message", "인증 되었습니다"
                + Define.CODE_EXPIRE_MINUTES + "분 안에 입력하세요"));
    }

    @PostMapping("/api/sms/verify")
    public ResponseEntity<?> verifyCode(@RequestBody  SmsRequest.SmsDto smsDto,
                                        HttpSession session){
        smsDto.validateCode();
        boolean verified = smsService.verifyCode(smsDto.getPhone(), smsDto.getCode(), session);
        if (!verified){
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다"));
        }
        return ResponseEntity.ok().body(Map.of("message", "인증되었습니다"));
    }

    // 작업 중 ... 예외처리 확인
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<Map<String, String>> handleEx400(Exception400 e){
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    // 메일 발송 실패 등 그 밖의 오류 (500)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleEx500(RuntimeException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body((Map.of("message", "메시지 발송 중 오류가 발생했습니다")));
    }
}
