
package com.tenco.spring_blog.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("사용자명과 비밀번호로 사용자 조회")
    void findByUsernameAndPassword_test() {
        // Given - 테스트 데이터 준비
        User user = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@test.com")
                .build();

        userJpaRepository.save(user);

        // When - 테스트할 메서드 실행
        Optional<User> result =
                userJpaRepository.findByUsernameAndPassword(
                        "ssar", "1234"
                );

        // Then - 결과 검증
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
        assertThat(result.get().getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회")
    void findByUsername_test() {
        // Given
        User user = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@test.com")
                .build();

        userJpaRepository.save(user);

        // When
        Optional<User> result =
                userJpaRepository.findByUsername("ssar");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("ssar");
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail_test() {
        // Given
        User user = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@test.com")
                .build();

        userJpaRepository.save(user);

        // When
        Optional<User> result =
                userJpaRepository.findByEmail("ssar@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail())
                .isEqualTo("ssar@test.com");
    }
}
