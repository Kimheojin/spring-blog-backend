# Codex Skill 및 Agent 운용 구조 정리

- 작성 시각: 2026-07-29 15:35:53 KST

## 배경

- 전역 리팩토링 전용 skill과 테스트 코드 전용 skill을 추가했다.
- 처음에는 `Refactor Agent -> refactoring skill -> subagent`, `Test Agent -> testing skill -> subagent` 형태를 고려했다.
- 실제 Codex 운용 모델에서는 영구 agent 객체를 미리 만들어 두기보다, skill이 메인 지침 역할을 하고 필요할 때 subagent를 동적으로 생성하는 방식이 더 적합하다.

## 선택한 구조

- 전역 skill 중심 구조를 사용한다.
- skill 경로는 repo 종속이 아닌 사용자 전역 위치를 사용한다.
  - `/home/heojin/.codex/skills/refactor-code`
  - `/home/heojin/.codex/skills/test-code`
- 사용자에게 보이는 표시명은 agent처럼 이해하기 쉽게 둔다.
  - `refactor-code`의 표시명: `Refactor Agent`
  - `test-code`의 표시명: `Test Agent`
- 실제 subagent는 미리 생성하지 않는다.
- 각 skill의 `SKILL.md` 안에 subagent 역할 템플릿과 생성 조건을 적어 둔다.

## 선택 이유

- 작업마다 필요한 분석 범위, 수정 범위, 검증 기준이 달라 고정 subagent보다 동적 생성이 안전하다.
- 전역 skill은 여러 repo에서 재사용되어야 하므로 특정 프로젝트 구조에 강하게 묶지 않는 편이 낫다.
- `agents/openai.yaml`은 실제 agent 정의가 아니라 UI 표시명, 짧은 설명, 기본 프롬프트를 위한 메타데이터다.
- skill 본문을 한글로 작성하면 관리자가 의도를 읽고 수정하기 쉽다.
- skill frontmatter의 `name`, `description`은 트리거 품질을 위해 영어 식별자와 영어 설명을 사용한다.

## Skill 경계

- `refactor-code`
  - 목적: 동작을 유지하면서 코드 구조를 개선한다.
  - 담당: 중복 제거, 네이밍 개선, 함수/클래스 분리, 결합도 감소, 변경 이유와 트레이드오프 요약.
  - 제한: 외부 동작과 API 호환성을 유지하고, 구조 변경과 기능 변경을 섞지 않는다.
  - 테스트는 안전망으로 사용한다.
- `test-code`
  - 목적: 테스트 설계, 작성, 실행, 실패 분석에 집중한다.
  - 담당: 정상 케이스, 경계값, 예외 케이스 식별, 기존 테스트 스타일 준수, observable behavior 검증, flaky 방지, 실패 원인과 재현 방법 정리.
  - 제한: 운영 코드 구조를 리팩토링하지 않고 검증 품질 개선에 집중한다.

## Subagent 운용 기준

- 작은 작업은 subagent 없이 메인 agent가 처리한다.
- 중간 이상 규모이거나 위험이 있는 작업에서만 subagent를 사용한다.
- 기본은 2개 역할을 사용하고, 범위가 넓거나 실패 분석이 필요할 때 3번째 역할을 추가한다.
- 같은 작업을 여러 subagent에게 중복 위임하지 않고 역할별 범위를 분리한다.

## Refactor Agent 역할 템플릿

- `Structure Scout`
  - 중복, 네이밍 문제, 결합도, 추출 가능한 함수/클래스 후보를 찾는다.
  - 직접 수정하지 않고 후보와 근거를 요약한다.
- `Safety Reviewer`
  - 공개 API, 외부 동작, 에러 처리, 테스트 안전망 관점에서 리팩토링 위험을 검토한다.
  - 넓은 변경, 인증/권한/데이터 계약, 성능 경로에 영향을 줄 때 사용한다.
- `Integration Owner`
  - 메인 agent가 맡는다.
  - 최종 변경을 통합하고 테스트를 실행하며 변경 이유와 트레이드오프를 정리한다.

## Test Agent 역할 템플릿

- `Scenario Mapper`
  - 정상, 경계값, 예외, 회귀 케이스와 누락된 커버리지를 도출한다.
  - 운영 코드는 수정하지 않고 테스트 설계 후보를 요약한다.
- `Failure Analyst`
  - 실패 로그, 재현 명령, 환경 차이, flaky 가능성을 분석한다.
  - 테스트 실패나 불안정성이 있을 때 사용한다.
- `Test Integrator`
  - 메인 agent가 맡는다.
  - 최종 테스트를 작성/수정하고 실행 결과와 신뢰도를 정리한다.

## 권장 흐름

- 리팩토링 요청 시 `$refactor-code`를 사용한다.
- 리팩토링 중 테스트가 부족하면 `$test-code`를 먼저 사용하거나 테스트 시나리오 도출을 하위 단계로 위임한다.
- 테스트 작성, 테스트 실패 분석, flaky 의심 상황에서는 `$test-code`를 사용한다.
- skill 호출 후 메인 agent가 작업 규모와 위험도를 보고 subagent 생성 여부를 결정한다.
