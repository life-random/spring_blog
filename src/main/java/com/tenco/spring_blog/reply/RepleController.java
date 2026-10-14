package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.error.Exception403;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog.user.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class RepleController {

    private final ReplyService replyService;

    // 댓글 작성
    @PostMapping("/reply/save")
    public String save(ReplyRequest.SaveDto saveDto, HttpSession session){
        saveDto.validate();
        if (session == null){
            throw new Exception403("로그인 하지 않은채 댓글 작성은 할 수 없습니다");
        }
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        replyService.save(saveDto, sessionUserDto);
        return "redirect:/board/" + saveDto.getBoardId();
    }

    // 댓글 삭제
    // 주소 설계 : http://localhost:8080/reply/{id}/delete
    @PostMapping("/reply/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(name = "boardId") Long boardId, HttpSession session){
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Define.SESSION_USER);
        replyService.deleteById(id, sessionUserDto);
        return "redirect:/board/" + boardId;
    }

}
