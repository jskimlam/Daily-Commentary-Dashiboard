# 긴 데일리 브리핑 저장 오류 해결: Apps Script POST 배포

## 현상
- 1~6번 상세 시황이 길어지면 GET URL이 16,000자를 넘어 저장 실패.
- GitHub Pages 프런트엔드는 URL이 긴 경우 POST로 전환하도록 수정됨.
- 기존 GET 방식의 짧은 저장 / 조회 / 삭제는 유지됨.

## 필수 작업: 기존 Apps Script 프로젝트에 doPost 추가

Google Apps Script 편집기에서 기존 `doGet(e)`를 그대로 유지하고 아래 함수를 추가:

```javascript
function doPost(e) {
  // x-www-form-urlencoded POST의 e.parameter.action, e.parameter.p를
  // 기존 GET 요청과 동일하게 처리. 기존 doGet 내부의 저장 로직 재사용.
  return doGet(e);
}
```

**주의:** 기존에 `doPost`가 이미 있다면 위 함수를 중복 선언하지 말고 기존 핸들러에 `action` / `p` 전달 로직을 통합해야 함.

### 배포 절차
1. 기존 GAS 프로젝트 열기 → 스크립트에 `doPost(e)` 추가 후 저장.
2. **배포 → 배포 관리 → 기존 웹앱 배포 수정 → 버전: 새 버전 → 배포**.
3. 기존 `/exec` URL이 동일한지 확인. 새 URL이면 GitHub `index.html` 상단 `GAS` 값을 맞춰야 함.
4. 대시보드를 새로고침하고 관리자 모드에서 저장 테스트.
5. 저장 후 실제 Google Sheet의 해당 날짜 행 / 5번 추가 정보 / 6번 AI 분석이 모두 표시되는지 확인.

## 브라우저에서 실패한 초안 복구
- 최신 대시보드 → 관리자 → 새 등록 화면 → 해당 품목 탭 → **↺ 로컬 백업 복구**.
- 복구된 내용 확인 후 **전체 저장**.
- 로컬 백업은 해당 브라우저와 사이트 데이터가 남아 있어야 복구 가능.

## 검사 포인트
- GET/POST 모두 `{action:'save' 또는 'edit', p: base64(JSON.stringify(payload))}` 규격 사용.
- `doGet(e)`가 `e.parameter.action`와 `e.parameter.p`를 읽는 기존 구조라면 위 어댑터로 호환.
- 앱스 스크립트 POST/CORS 접근 정책이 허용되는지는 실제 브라우저 테스트 전까지 미검증.
- UI 수정만으로 Apps Script 서버의 POST 수신 기능이 생기지는 않음.
