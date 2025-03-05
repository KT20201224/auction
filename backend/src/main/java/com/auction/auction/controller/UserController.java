package com.auction.auction.controller;

import com.auction.auction.entity.User;
import com.auction.auction.security.JwtUtil;
import com.auction.auction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 API
     * @param request 이메일, 비밀번호, 닉네임
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Map<String, String> request){

        String email = request.get("email");
        String password = request.get("password");
        String nickname = request.get("nickname");

        try{
            User user = userService.registerUser(email, password, nickname);
            return ResponseEntity.ok(Map.of("message", "회원가입 성공", "userId", user.getId()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 로그인 API
     * @param request 이메일과 비밀번호
     * @return JWT 토큰 또는 에러 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request){

        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userService.findByEmail(email);
        if(userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())){
            return ResponseEntity.status(401).body(Map.of("error", "이메일 또는 비밀번호가 잘못되었습니다."));
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * 사용자 정보 조회 API
     * @param token JWT 인증 토큰
     * @return 사용자 정보 (이메일, 닉네임) 또는 에러 메시지
     */
    @PostMapping("/me")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token){
        String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
        Optional<User> user = userService.findByEmail(email);
        return user.map(value -> ResponseEntity.ok(Map.of("eamil", value.getEmail(), "nickname", value.getNickname())))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "사용자를 찾울 수 없습니다.")));
    }
}
