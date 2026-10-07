package com.tenco.spring_blog.board;

import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserPersistRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class BoardPersistRepositoryTest {

    @Autowired
    private BoardPersistRepository boardPersistRepository;

    @Autowired
    private UserPersistRepository userPersistRepository;



    @Test
    public void delete_게시글_삭제_테스트() {

        // given
        // 1. 게시글 저장을 위한 User 객체 생성
        User user = new User(
                1L,
                "testuser",
                "1234",
                "a@naver.com",
                null
        );

        // 2. 삭제할 게시글 객체 생성
        Board board = Board.builder()
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .user(user)
                .build();

        // 3. 게시글 저장
        Board savedBoard = boardPersistRepository.save(board);

        // 4. 저장된 게시글의 기본키(ID) 가져오기
        Long boardId = savedBoard.getId();


        // when
        // 5. 게시글 삭제
        boardPersistRepository.deleteById(boardId);

        // then
        // 6. 삭제된 게시글을 다시 조회
        Board deletedBoard = boardPersistRepository.findById(boardId);

        // 7. 삭제된 게시글은 조회되지 않아야 함
        Assertions.assertThat(deletedBoard).isNull();

    }

}