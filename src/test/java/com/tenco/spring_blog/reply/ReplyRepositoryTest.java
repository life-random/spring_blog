package com.tenco.spring_blog.reply;

import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReplyRepositoryTest {

    @Autowired
    private ReplyJpaRepository replyJpaRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findAllByBoardIdJoinUser_test() {
        // given - 테스트 데이터 준비

        // 실제 프로젝트의 생성자나 Builder에 맞게 수정
        User user = User.builder()
                .username("user1")
                .password("1234")
                .email("user1@test.com")
                .build();
        em.persist(user);

        Board board = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .user(user)
                .build();
        em.persist(board);

        Reply reply1 = Reply.builder()
                .comment("첫 번째 댓글")
                .user(user)
                .board(board)
                .build();

        Reply reply2 = Reply.builder()
                .comment("두 번째 댓글")
                .user(user)
                .board(board)
                .build();

        em.persist(reply1);
        em.persist(reply2);

        em.flush();
        em.clear();

        // when - 메서드 실행
        List<Reply> replies =
                replyJpaRepository.findAllByBoardIdJoinUser(board.getId());

        // then - 결과 검증
        assertThat(replies).hasSize(2);

        // 댓글이 ID 오름차순으로 정렬되었는지 확인
        assertThat(replies)
                .extracting(Reply::getId)
                .isSorted();

        // 댓글 작성자 정보가 조회되는지 확인
        assertThat(replies)
                .allSatisfy(reply -> {
                    assertThat(reply.getUser()).isNotNull();
                    assertThat(reply.getUser().getUsername())
                            .isEqualTo("user1");
                });
    }
}
