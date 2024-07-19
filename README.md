소개
---
견고하게 쌓아 올린 게시판.

기간
---
2024.02 - 현재 진행 중.

특징
---

App
* IP 주소 기반, 단위 시간당 허용 요청 수를 넘을 시, 블랙리스트 등록.
  - 낭비될 가능성이 있는 자원 절약.
* 쿠키를 활용한 게시물 조회수 관리.
  - 다양한 쿠키를 처리할 수 있도록, 쿠키 관련 Utility 클래스 작성.
* Spring Security, JWT를 활용한 인증, 인가
  - 사용자 권한 기반 인가 관리의 용이. (@PreAuthorize)
  - JWT를 통한 클라이언트 차원의 인증 상태 관리 용이.
* Filter 계층에서 발생하는 JWT 만료, 조작 등의 예외 처리
   - 앞선 Filter 계층에서 해당 예외 처리 역할의 클래스 작성을 통해 해결.
* @Scheduled 통해 데이터베이스에 존재하는 만료 토큰 삭제.
* @ControllerAdvice 통한 앱 수준의 전역적 예외 처리.
  - HTTP method에 따라 에러 페이지 혹은 status, message 필드를 가진 JSON 객체로 응답.
  - 발생 예외의 종류에 따라 INFO, WARN, ERROR 레벨 로깅.
* View, 정적 자원 제외한 요청은 REST API를 통해 상태 코드와 메시지 반환, 필요시 JSON 형태의 로우 데이터 반환.

Test
* 테스트 베이스 클래스 작성 및 상속을 통한 기반 데이터 공유 및 코드 중복 제거.
* Entity 생성 및 영속화를 테스트하기 위한 EntityManager 사용. 
  - EntityManager의 동작과 구조에 대해 알게 됐고, 이해를 기반으로 테스트를 진행.
* 서비스 클래스 중, Page, Slice의 content가 비어있다면 예외를 던지는 메서드 테스트 시, JpaRepository를 상속한 인터페이스가 반환하는 Page, Slice를 직접 구현해야 하는 조금 번거로운 문제가 발생.
  - 이를 해결하기 위해 @DataJpatTest, @Import(TargetService.class)를 적용, JPA가 반환하는 Page, Slice를 사용해 간결한 테스트 작성.
* Spring Security 인증이 필요한 ControllerTest, 간결한 테스트를 위해 @WithMockUser(roles = "역할") 사용.

Web Server
* Nginx를 통한 무차별 대입, 비정상 요청 필터링 및 정적 자원 반환 .
  - Spring web filter 계층에서 처리하던 로직을 위임.
    - 웹 서버 차원에서 비정상 요청을 거르므로, 로그 관리 용이성 증대.
    - Spring web filter 계층의 간소화 및 유지보수 용이성 증대.

Deploy
* 생각보다 잦은 프로젝트 업데이트에 단순 반복적 배포 작업을 자동화.
  - 배포 서버의 Shell Script를 통한 패치, 빌드, 배포. (git, gradle, jar)

Front-End
* history state 을 활용해 fetch로 얻어온 인가가 필요한 페이지의 교체.
* JavaScript 코드 관리의 어려움을 해결하기 위해
  - class를 도입해 기능, 모듈별로 관리.
* 회원 가입 시, 사용자 입력값 검증 및 submit 버튼 활성화를 위해
  - 입력값의 검증 정보를 담는 객체를 활용, 각 input 값의 변화에 따른 검증 결과를 submit 버튼 활성화 여부에 반영.
* Fetch API를 통해 얻은 데이터, DOM 조작하여 페이지에 반영.

해프닝
---

* 의도와 다르게 삭제된 배포 서버 데이터, MySQL binlog 기반 복구 경험.
  - 삭제 원인은 충분치 않은 테스트 케이스.
  - 이 경험을 통해 테스트 작성 시, 한 케이스의 반대 케이스의 중요성을 인식.


