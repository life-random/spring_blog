package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.UnAuthorizedException;
import com.tenco.spring_blog._core.util.Define;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final NaverApiClient naverApiClient;
    private final UserService userService;

    public String getAuthorizeUrl() {
        return naverApiClient.getAuthorizeUrl();
    }

    public SessionUserDto login(String code) {

        if (code == null) {
            throw new UnAuthorizedException("네이버 로그인이 취소되었습니다");
        }

        try {
            // 1. 인가 코드로 액세스 토큰 발급 (네이버 서버 통신)
            UserResponse.NaverOauthToken token = naverApiClient.requstAccessToken(code);

            // 2. 액세스 토큰으로 사용자 정보 조회 (네이버 서버)
            UserResponse.NaverUserInfo naverUserInfo = naverApiClient.requestProfile(token.getAccessToken());
//            UserResponse.KakaoUserInfo kakaoUserInfo = kakaoApiClient.requestProfile(oAuthToken.getAccessToken());

            return userService.naverLogin(naverUserInfo);
            // 3. 자동 회원가입 또는 로그인 (우리 DB, 트랜잭션은 UserService에서)

        } catch (HttpClientErrorException e) {
            log.warn("네이버 API 오류 : {}", e.getResponseBodyAsString());
            throw new UnAuthorizedException("네이버 로그인 실패했습니다. 다시 시도해 주세요");
        } catch (ResourceAccessException e) {
            log.warn("네이버 서버 연결 실패 : {}", e.getMessage());
            throw new UnAuthorizedException("네이버 로그인 실패했습니다. 잠시 후 다시 시도해 주세요");
        }
    }
}
