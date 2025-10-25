# finalProject
25.09.10 ~ 25.10.20

## Geulbut – 온라인 서점 프로젝트
JSP, SpringBoot, Oracle, 외부 API를 활용한 실무형 온라인 서점 웹 서비스

### 프로젝트 개요
Geulbut은 Spring Boot 기반의 온라인 서점 웹 애플리케이션으로,
도서 검색부터 결제, 주문·환불까지의 전 과정을 구현하였습니다.
외부 Open API와 결제 시스템을 연동하여 실제 서비스에 가까운 흐름을 구현한 프로젝트입니다.

### 기획 의도
- 실무에서 사용하는 Spring + JSP + Oracle 기반 구조의 이해 및 구현
- 기획 → 설계 → 구현 → 테스트 전 과정을 통한 풀스택 개발 경험
- 기존 온라인 서점 서비스 분석을 통해 UX 중심의 UI 설계 및 기능 최적화
- 외부 공공·민간 API 연동을 통한 확장성 있는 서비스 개발

## 주요 기능

### 회원 관리
- 회원가입 / 로그인 / 소셜 로그인 (Google, Naver, Kakao)
- 아이디·비밀번호 찾기 및 임시 비밀번호 이메일 전송
- 마이페이지
- 회원정보 수정
- 환불 요청

### 도서 관리 및 검색
- 카테고리별 / 키워드별 / 해시태그 검색
- 정렬 기능 : 최신 출간순 / 인기순 / 할인율순
- 도서 상세 페이지 (작가, 출판사, 줄거리 등)
- 알라딘 오픈 API를 이용한 실시간 도서 데이터 연동

### 장바구니
- 도서 추가 / 삭제
- 수량 변경 시 실시간 총액 반영
- 선택 상품 주문

### 주문 및 결제
- 카드주문 및 결제
- 회원 / 비회원 주문
- 카카오페이·네이버페이 API를 이용한 결제 처리
- 주문 내역 조회 / 환불 요청

### 공공 데이터 연동
- 기상청 / 미세먼지 공공API 연동
- 헤더 영역에 지역별 날씨 및 미세먼지 상태 표시 (3초 간격 ticker)인

## 특징
- JPA 기반의 ORM 데이터 처리 (Mapper 미사용)
- API 병렬 호출 및 비동기 데이터 렌더링
- 반응형 UI (모바일 퍼스트)
- 소셜 로그인 및 결제 API 통합
- 관리자 페이지를 통한 회원·도서·주문 관리

## 기술 스택
| 분야 | 기술 |
|------|------|
| Frontend | HTML, CSS, JavaScript, JSP(서버 템플릿), Bootstrap |
| Backend | Java 17, Spring Boot, Spring Data JPA |
| Database | Oracle Database |
| Search / Infra | Elasticsearch |
| Build & Tool | Gradle, IntelliJ IDEA |
| VCS | Git, GitHub |
| API 연동 | 알라딘 Open API, KMA 공공기상청 API, 공공 미세먼지 API, KakaoPay/NaverPay, Google·Naver·Kakao OAuth2 |

#### 팀 구성
총 7명으로 구성된 팀 프로젝트로 기획, 백엔드, 프론트엔드, DB 설계 등 역할을 분담 하여
GitHub를 통한 브랜치별 협업 및 버전 관리를 수행하였습니다.

GIF
![Geulbut Demo](geulbut/src/main/webapp/images/geulbut2.gif)

🎬 시연 영상
[Geulbut Demo Video](https://github.com/YeonHaru/finalProject/releases/download/v1.0/1._._.mp4)

