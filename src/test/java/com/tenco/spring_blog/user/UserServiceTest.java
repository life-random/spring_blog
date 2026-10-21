package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception404;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void join_사용자명_중복_테스트() {
        // given: 이미 존재하는 사용자명을 조회하면 기존 회원이 반환되도록 준비한다.
        UserRequest.JoinDto joinDto =
                org.mockito.Mockito.mock(UserRequest.JoinDto.class);
        User existingUser = org.mockito.Mockito.mock(User.class);

        given(joinDto.getUsername()).willReturn("ssar");
        given(userJpaRepository.findByUsernameWithRoles("ssar"))
                .willReturn(Optional.of(existingUser));

        // when & then: 중복 사용자명으로 가입하면 400 예외와 메시지가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.join(joinDto))
                .isInstanceOf(Exception400.class)
                .hasMessage("이미 존재하는 사용자명입니다");

        // then: 중복 검사에서 종료되므로 저장은 호출되지 않아야 한다.
        verify(userJpaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void join_이메일_중복_테스트() {
        // given: 사용자명은 중복되지 않지만 이메일은 이미 사용 중인 상황을 준비한다.
        UserRequest.JoinDto joinDto =
                org.mockito.Mockito.mock(UserRequest.JoinDto.class);
        User existingUser = org.mockito.Mockito.mock(User.class);

        given(joinDto.getUsername()).willReturn("ssar");
        given(joinDto.getEmail()).willReturn("ssar@example.com");
        given(userJpaRepository.findByUsernameWithRoles("ssar"))
                .willReturn(Optional.empty());
        given(userJpaRepository.findByEmail("ssar@example.com"))
                .willReturn(Optional.of(existingUser));

        // when & then: 중복 이메일이면 400 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.join(joinDto))
                .isInstanceOf(Exception400.class)
                .hasMessage("이미 사용 중인 이메일입니다");

        // then: 이메일 중복 검사에서 종료되므로 저장은 호출되지 않아야 한다.
        verify(userJpaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void login_존재하지않는_사용자_테스트() {
        // given: 입력한 사용자명을 저장소에서 찾을 수 없도록 준비한다.
        UserRequest.LoginDto loginDto =
                org.mockito.Mockito.mock(UserRequest.LoginDto.class);

        given(loginDto.getUsername()).willReturn("unknown");
        given(userJpaRepository.findByUsernameWithRoles("unknown"))
                .willReturn(Optional.empty());

        // when & then: 사용자가 없으면 인증 실패 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(Exception400.class)
                .hasMessage("사용자명 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    void login_비밀번호_불일치_테스트() {
        // given: 사용자는 존재하지만 입력한 비밀번호가 틀린 상황을 준비한다.
        UserRequest.LoginDto loginDto =
                org.mockito.Mockito.mock(UserRequest.LoginDto.class);
        User user = org.mockito.Mockito.mock(User.class);

        given(loginDto.getUsername()).willReturn("ssar");
        given(loginDto.getPassword()).willReturn("wrong-password");
        given(userJpaRepository.findByUsernameWithRoles("ssar"))
                .willReturn(Optional.of(user));
        given(user.getPassword()).willReturn("encoded-password");
        given(passwordEncoder.matches("wrong-password", "encoded-password"))
                .willReturn(false);

        // when & then: 비밀번호 비교 결과가 false이면 400 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(Exception400.class)
                .hasMessage("사용자명 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    void findDetail_존재하지않는_사용자_테스트() {
        // given: ID로 사용자를 조회해도 결과가 없도록 준비한다.
        given(userJpaRepository.findById(999L))
                .willReturn(Optional.empty());

        // when & then: 사용자가 없으면 404 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.findDetail(999L))
                .isInstanceOf(Exception404.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }

    @Test
    void findUpdateForm_존재하지않는_사용자_테스트() {
        // given: 수정할 사용자 ID에 해당하는 회원이 없도록 준비한다.
        given(userJpaRepository.findById(999L))
                .willReturn(Optional.empty());

        // when & then: 수정 화면 조회에서도 404 예외가 발생하는지 확인한다.
        assertThatThrownBy(() -> userService.findUpdateForm(999L))
                .isInstanceOf(Exception404.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }
}