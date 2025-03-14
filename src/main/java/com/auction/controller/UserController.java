package com.auction.controller;

import com.auction.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 관련 컨트롤러
 * - 회원가입 및 로그인 처리
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 페이지 이동
     *
     * @return 회원가입 페이지 (register.html)
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    /**
     * 회원가입 처리
     *
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @param name     사용자 이름
     * @param model    오류 메시지를 전달하기 위한 모델 객체
     * @return 회원가입 성공 시 로그인 페이지로 이동, 실패 시 다시 회원가입 페이지
     */
    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           Model model) {
        try {
            userService.registerUser(email, password, name);
            return "redirect:/login"; // ✅ 회원가입 성공 시 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); // ✅ 오류 메시지 추가
            return "register"; // ✅ 회원가입 실패 시 다시 회원가입 페이지
        }
    }

    /**
     * 로그인 페이지 이동
     *
     * @return 로그인 페이지 (login.html)
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}