package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception403;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

// 특정 IP를 차단 하는 인터셉터를 구현해주세요 단 , 여러개 가능 조원들 IP
@Slf4j
@Component
public class IpBlockInterceptor implements HandlerInterceptor {

    private final List<String> blockedIp = new ArrayList<>(
            List.of(
                    "192.168.5.13",
                    "192.168.5.17",
                    "192.168.7.232"
            )
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();

        log.info("현재 접속 IP : {}", ip);
        log.info("차단 목록 : {}", blockedIp);
        log.info("차단 여부 : {}", blockedIp.contains(ip));


        if (blockedIp.contains(ip)) {
            throw new Exception403("해당 IP는 접근할 수 없습니다");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
