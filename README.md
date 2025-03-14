# 경매 웹서비스 프로젝트

## 📌 소개
이 프로젝트는 사용자가 직접 경매 상품을 등록하고 입찰할 수 있는 웹 애플리케이션입니다.
Spring Boot와 Thymeleaf를 사용하여 개발되었으며, 관리자 기능을 포함하고 있습니다.

## 🛠 기술 스택
- **Back-end**: Spring Boot, Spring Security, JPA, Hibernate
- **Front-end**: Thymeleaf, Tailwind CSS
- **Database**: MySQL
- **Security**: Spring Security (BCrypt 암호화)
- **Email**: Spring Mail (Gmail SMTP)
- **Build Tool**: Maven

## 🚀 주요 기능
### 사용자 기능
- 회원가입 및 로그인 (Spring Security 적용)
- 포인트 충전 및 충전 내역 조회
- 경매 상품 등록 및 입찰 참여
- 마이페이지에서 입찰 내역, 낙찰 상품 확인

### 경매 기능
- 상품별 입찰 기능 (최고 입찰가 갱신)
- 경매 마감 시 자동 낙찰 처리
- 낙찰 후 구매 확정 및 판매자 포인트 지급
- 이메일을 통한 낙찰 안내

### 관리자 기능
- 사용자 계정 관리 (정지 및 정지 해제)
- 경매 상품 삭제 기능
- 관리자 페이지 제공

## 📄 프로젝트 구조
```
auction-webservice/
├── src/main/java/com/auction
│   ├── config        # Spring Security 설정
│   ├── controller    # 컨트롤러 (웹 요청 처리)
│   ├── domain        # 엔티티 (JPA 모델)
│   ├── repository    # 데이터 접근 계층 (JPA 리포지토리)
│   ├── service       # 비즈니스 로직 계층
│   ├── security      # 사용자 인증 관련 클래스
│   └── utils         # 유틸리티 클래스
├── src/main/resources/
│   ├── templates/    # Thymeleaf 템플릿 (HTML 페이지)
│   ├── application.properties  # 환경 설정 파일
├── pom.xml          # Maven 설정 파일
└── README.md        # 프로젝트 설명 파일
```

## 📧 이메일 설정 (SMTP)
이메일 전송을 위해 `application.properties` 파일에 아래 내용을 추가하세요:
```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## ✨ 기여 방법
이 프로젝트에 기여하고 싶다면 **이슈를 생성**하거나 **풀 리퀘스트(PR)를 보내주세요**! 😊

