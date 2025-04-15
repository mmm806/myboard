package com.example.myboard.controller;

import com.example.myboard.dto.LoginInfo;
import com.example.myboard.dto.User;
import com.example.myboard.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }


    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        userService.addUser(name, email, password);
        return "redirect:/welcome"; // 브라우저에게 자동의 http://localhost:8080/welcome 으로 이동
    }


    @GetMapping("welcome")
    public String welcome() {
        return "welcome";
    }


    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }


    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession // spring이 자동으로 session을 처리하는 HttpSession 객체를 넣어준다.
    ) {
        try{
            User user = userService.getUser(email);
            if (user.getPassword().equals(password)) {
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());

                // 권한정보를 읽어와서 loginInfo에 추가하기
                List<String> roles = userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);

                httpSession.setAttribute("loginInfo", loginInfo); // Session에 정보 저장
            }
            else {
                throw new RuntimeException("암호가 일치하지 않음.");
            }
        }catch (Exception ex) {
            return "redirect:/loginform?error=ture";
        }

        return "redirect:/";
    }


    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }
}
