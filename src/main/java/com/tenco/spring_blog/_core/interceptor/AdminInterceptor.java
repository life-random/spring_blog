package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.error.UnAuthorizedException;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.SessionUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// 직접 구현 해보기
@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 확인만 해야 하므로 새로운 세션을 만들면 안됨. false 설정 필수!
        HttpSession session = request.getSession(false);
        SessionUserDto sessionUserDto = (session == null) ? null : (SessionUserDto) session.getAttribute((Define.SESSION_USER));

        // 1. 로그인 확인은 우리가  만들었던 LoginInterceptor 가 먼저 처리 (하지만 등록 순서가 바뀌면 오류가 발생한다)
        if (sessionUserDto == null){
            throw new UnAuthorizedException("회원 전용 서비스입니다");
        }

        // 2. 관리가 아니면 접근 거부
        if (!sessionUserDto.isAdmin()){
            throw new Exception403("해당 계정은 접근할 권한이 없습니다");
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
