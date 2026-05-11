# sample_java5

Spring Boot 기반 Java 샘플 백엔드 프로젝트 (Order API) + 보안 스캐너 픽스처 모음.

## 프로젝트 요약

- 목적: 스캐너 검증 최대 밀도(10/10) — 가능한 한 다양한 OWASP/CWE 카테고리를 단일 빌드 단위에 의도적으로 포함
- 도메인: Order CRUD + 검색 + Health Check
- 학습/스캐너 검증 전용. 운영 환경 사용 금지.

## 기술 스택/버전

- Java 17 toolchain
- Spring Boot 3.4.2
- Gradle Wrapper 8.7 설정
- Validation: `jakarta.validation`

## 실행 / 빌드

```bash
./gradlew bootRun
./gradlew build
```

## 기능 명세(정상 API)

### GET /health

응답: `{ "status": "ok" }`

### POST /orders

요청 예시:

```json
{ "product": "Keyboard", "quantity": 2, "price": 49.9, "customer": "alice" }
```

### GET /orders, GET /orders/{id}, DELETE /orders/{id}, GET /orders/search?q=...

CRUD/검색은 다른 샘플 프로젝트와 동일한 패턴.

## 데이터 저장 방식

- DB 없음, 메모리(Map) 저장
- 앱 재시작 시 데이터 초기화

## 의도적 취약점 (학습/스캐너 검증용)

### 설정 / 일반

- **Information Disclosure** — `application.properties` 의 4종 disclosure 옵션 (include-message, include-stacktrace, include-binding-errors, include-exception)
- **CORS Misconfiguration (`*`)** — `OrderController` 에 `@CrossOrigin("*")`
- **Mass Assignment** — `OrderController#createOrder` 가 `@RequestBody Order` 를 모델에 직접 바인딩
- **Log Injection** — `OrderController#searchOrders` 에서 사용자 입력 `q` 를 정화 없이 로그 기록

### `VulnController` (`/vuln/*`) 에 집중 배치된 취약점

| Endpoint                  | CWE       | 카테고리                          |
| ------------------------- | --------- | --------------------------------- |
| 소스 상수                 | CWE-798   | Hardcoded Credentials / API Key / JWT Secret / AES Key |
| `GET /vuln/users`         | CWE-89    | SQL Injection                     |
| `GET /vuln/exec`          | CWE-78    | OS Command Injection              |
| `GET /vuln/read`          | CWE-22    | Path Traversal                    |
| `GET /vuln/download`      | CWE-22    | Path Traversal (nio)              |
| `POST /vuln/deserialize`  | CWE-502   | Insecure Deserialization          |
| `POST /vuln/xml`          | CWE-611   | XXE                               |
| `GET /vuln/xpath`         | CWE-643   | XPath Injection                   |
| `GET /vuln/ldap`          | CWE-90    | LDAP Injection                    |
| `GET /vuln/fetch`         | CWE-918   | SSRF                              |
| `GET /vuln/token`         | CWE-327   | Weak Hash (MD5)                   |
| `GET /vuln/sha1`          | CWE-327   | Weak Hash (SHA-1) for password    |
| `GET /vuln/sessionId`     | CWE-338   | Insecure Randomness               |
| `GET /vuln/encrypt`       | CWE-327   | Weak Crypto (AES/ECB + hardcoded key) |
| `GET /vuln/insecure-https`| CWE-295   | Trust-all TLS / Hostname Verifier Bypass |
| `GET /vuln/load`          | CWE-470   | Unsafe Reflection                 |
| `GET /vuln/eval`          | CWE-95    | SpEL Expression Injection         |
| `GET /vuln/redirect`      | CWE-601   | Open Redirect                     |
| `GET /vuln/setHeader`     | CWE-113   | HTTP Response/Header Injection    |
| `GET /vuln/xss`           | CWE-79    | Reflected XSS (HTML)              |
| `GET /vuln/setCookie`     | CWE-614   | Insecure Cookie (no HttpOnly/Secure) |
| `POST /vuln/extract`      | CWE-22    | ZipSlip                           |
| `GET /vuln/debug`         | CWE-209   | Info Leak via Stack Trace         |

총 ~24개 카테고리.

## 부속 픽스처 (언어 중립 텍스트)

- `scanner-fixtures/` — `.txt` / `.html` 확장자의 비실행 스니펫 모음. SAST 도구 패턴 검증용.
