package com.example.myboard.controller;

import com.example.myboard.dto.Board;
import com.example.myboard.dto.LoginInfo;
import com.example.myboard.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// HTTP 요청을 받아서 응답하는 컴포넌트. 스프링 부트가 자동으로 Bean으로 생성한다.
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    // 게시물 목록을 보여준다
    // 컨트롤러의 메소드가 리턴하는 문자열은 템플릿이름이다.
    @GetMapping("/")
    public String List(@RequestParam(name = "page", defaultValue = "1") int page,
                       HttpSession session,
                       Model model) { // HttpSessio, Model은 Spring이 자동으로 넣어준다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); // 템플릿에게 객체를 넘김

        page = 1;
        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page);
        int pageCount = totalCount / 10;
        if (totalCount % 10 > 0) {
           pageCount++;
        }
        int currentPage = page;
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);


        return "list";
    }



    @GetMapping("/board")
    public String board(
            @RequestParam("boardId") int boardId,
            Model model
    ) {
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }



    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }

        model.addAttribute("loginInfo",loginInfo);

        return "writeForm";
    }



    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session,
            Model model
    ) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }

        boardService.addBoard(loginInfo.getUserId(), title, content);

        return "redirect:/";
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }

        List<String> roles = loginInfo.getRoles();
        if (roles.contains("ROLE_ADMIN")) {
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }
        return "redirect:/";
    }


    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") int boardId,
                             Model model,
                             HttpSession session) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }


    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
    ){

        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }

        Board board = boardService.getBoard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동한다.
        }
        // boardId에 해당하는 글의 제목과 내용을 수정한다.
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트한다.
    }


}
