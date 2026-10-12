
package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoardJpaRepositoryTest {

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("게시글 ID로 조회하면서 작성자도 함께 조회한다")
    void findByIdJoinUser_test() {
        // Given - 사용자와 게시글 준비
        User user = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@test.com")
                .build();

        userJpaRepository.save(user);

        Board board = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(user)
                .build();

        Board savedBoard = boardJpaRepository.save(board);

        // When - 게시글 ID로 조회
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(savedBoard.getId());

        // Then - 게시글과 작성자 검증
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("테스트 제목");
        assertThat(result.get().getUser().getUsername())
                .isEqualTo("ssar");
    }

    @Test
    @DisplayName("전체 게시글을 작성자 정보와 함께 조회한다")
    void findAllJoinUser_test() {
        // Given
        User user = User.builder()
                .username("ssar")
                .password("1234")
                .email("ssar@test.com")
                .build();

        userJpaRepository.save(user);

        Board board1 = Board.builder()
                .title("첫 번째 게시글")
                .content("첫 번째 내용")
                .user(user)
                .build();

        Board board2 = Board.builder()
                .title("두 번째 게시글")
                .content("두 번째 내용")
                .user(user)
                .build();

        boardJpaRepository.save(board1);
        boardJpaRepository.save(board2);

        // When
        List<Board> result =
                boardJpaRepository.findAllJoinUser();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Board::getTitle)
                .containsExactly("두 번째 게시글", "첫 번째 게시글");

        assertThat(result)
                .allSatisfy(b ->
                        assertThat(b.getUser().getUsername())
                                .isEqualTo("ssar"));
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID를 조회하면 결과가 없다")
    void findByIdJoinUser_notFound_test() {
        // Given
        Long notFoundId = 99999L;

        // When
        Optional<Board> result =
                boardJpaRepository.findByIdJoinUser(notFoundId);

        // Then
        assertThat(result).isEmpty();
    }
}
