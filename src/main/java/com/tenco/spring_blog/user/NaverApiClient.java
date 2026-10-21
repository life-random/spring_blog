package com.tenco.spring_blog.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NaverApiClient {

    @Value("${oauth.naver.client-id}")
    private String naverClientId;
    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;
    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    private static String AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize";
    private static String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    private final RestTemplate restTemplate = new RestTemplate();

    public UserResponse.NaverUserInfo requestProfile(String accessToken) {
        HttpHeaders accessHeaders = new HttpHeaders();
        accessHeaders.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(accessHeaders);

        ResponseEntity<UserResponse.NaverUserInfo> response = restTemplate.exchange(
                PROFILE_URL,
                HttpMethod.GET,
                request,
                UserResponse.NaverUserInfo.class
        );
        return response.getBody();
    }

    public String getAuthorizeUrl() {
        return AUTHORIZE_URL
                + "?client_id=" + naverClientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&state=" + "0";
    }

    public UserResponse.NaverOauthToken requstAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("state", "0");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<UserResponse.NaverOauthToken> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                tokenRequest,
                UserResponse.NaverOauthToken.class
        );

        log.info("네이버 엑세스 토큰 발급 완료");
        return response.getBody();
    }
}
