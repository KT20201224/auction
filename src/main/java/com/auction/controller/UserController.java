package com.auction.controller;

import com.auction.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 컨트롤러 - 회원가입 기능
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 페이지 이동
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }


    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           Model model) {
        try {
            userService.registerUser(email, password, name);
            return "redirect:/login"; // 회원가입 후 로그인 페이지로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // 오류 발생 시 다시 회원가입 페이지
        }
    }

    /**
     * 로그인 페이지 이동
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}