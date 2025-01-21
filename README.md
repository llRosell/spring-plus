# Todo Management Application

## ⏰ 개발기간
- **2025.1.14 (화) - 2025.01.23 (목)**

---

## 🙍🏻‍♀️ 개발자 소개
- **김리은**: 스파르타 내일배움캠프 Spring 4기 8조 팀원

---

## 📖 프로젝트 개요

이 애플리케이션은 Todo 관리 시스템으로, 사용자는 할 일을 생성하고 관리할 수 있으며, 관리자 권한을 가진 사용자는 다른 사용자에게 할 일을 할당하고 관리할 수 있습니다. 이 애플리케이션은 Spring Boot, Spring Security, JPA, MySQL, Redis를 사용하여 개발되었습니다.

---

## 🎯 주요 기능

- **사용자 관리**: ADMIN과 USER 권한을 가진 사용자를 추가하고 관리합니다.
- **할 일 관리**: 사용자는 할 일을 생성, 조회, 수정, 삭제할 수 있습니다.
- **할 일 담당자 지정**: 사용자가 생성한 할 일에 담당자를 자동으로 지정합니다.
- **로그 기록**: 매니저 등록 요청 시 로그 테이블에 요청 로그를 남깁니다.
- **검색 기능**: 제목, 생성일, 담당자 닉네임으로 할 일을 검색할 수 있습니다.
- **JWT 인증**: 사용자 인증을 위한 JWT를 사용합니다.

---

## 🛠️ 기술 스택

- **프레임워크**: Spring Boot
- **데이터베이스**: MySQL
- **ORM**: JPA (Java Persistence API)
- **보안**: Spring Security
- **의존성 주입**: @Transactional

---

## 📦 프로젝트 구성

### 1. **API 엔드포인트 구성**

#### 1.1. 할 일 관리

- **POST /todos**: 새로운 할 일 추가
    - 요청 본문:
        ```json
        {
            "title": "새로운 할 일 제목",
            "contents": "내용",
            "weather": "맑음"
        }
        ```
    - 응답 예시:
        ```json
        {
            "id": 1,
            "title": "새로운 할 일 제목",
            "contents": "내용",
            "weather": "맑음",
            "user": {
                "id": 1,
                "email": "user@example.com"
            }
        }
        ```

- **GET /todos/{id}**: 특정 할 일 조회
    - URL 경로 변수: `id` (할 일 ID)
    - 응답 예시:
        ```json
        {
            "id": 1,
            "title": "할 일 제목",
            "contents": "할 일 내용",
            "weather": "맑음",
            "user": {
                "id": 1,
                "email": "user@example.com"
            },
            "createdAt": "2025-01-14T08:00:00",
            "modifiedAt": "2025-01-15T09:00:00"
        }
        ```

- **GET /todos/search**: 할 일 검색
    - 쿼리 파라미터:
        - `title` (제목으로 검색)
        - `weather` (날씨로 검색)
        - `startDate` (시작일)
        - `endDate` (종료일)
    - 응답 예시:
        ```json
        {
            "content": [
                {
                    "title": "할 일 제목",
                    "nicknameCount": 3,
                    "commentCount": 5
                }
            ]
        }
        ```

#### 1.2. 사용자 관리

- **POST /users**: 사용자 등록
    - 요청 본문:
        ```json
        {
            "username": "사용자이름",
            "password": "비밀번호",
            "nickname": "닉네임"
        }
        ```
    - 응답 예시:
        ```json
        {
            "bearerToken": "jwt-token-string"
        }
        ```

- **GET /users/{userId}**: 특정 사용자 조회
    - URL 경로 변수: `userId` (사용자 ID)
    - 응답 예시:
        ```json
        {
            "id": 1,
            "email": "user@example.com"
        }
        ```

#### 1.3. 관리자 기능

- **PATCH /admin/users/{userId}**: 사용자 역할 변경
    - 요청 본문:
        ```json
        {
            "role": "ADMIN"
        }
        ```
    - 응답: 없음 (상태 코드: 204)

---
