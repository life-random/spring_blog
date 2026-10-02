package com.tenco.spring_blog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardPersistRepository boardPersistRepository;

    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        try {
            boardPersistRepository.deleteById(id);
            return ResponseEntity.ok("정상 삭제되었습니다");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다");
        }
    }
}
