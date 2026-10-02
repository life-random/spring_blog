package com.tenco.spring_blog.board;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BoardPersistRepository {

    private final EntityManager em;

    @Transactional
    public void updateById(Long id, BoardRequest.UpdateDto reqDto){
        Board boardEntity = em.find(Board.class, id);
        if (boardEntity == null) {
            throw new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다");
        }
        boardEntity.update(reqDto);
    }

    @Transactional
    public void deleteById(long id){
        Board boardEntity = em.find(Board.class, id);

        if(boardEntity == null) {
            throw new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다");
        }
        em.remove(boardEntity);
    }
}







