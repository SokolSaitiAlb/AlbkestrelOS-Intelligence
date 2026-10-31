# GitHub Issue Index

All implementation tasks are tracked as GitHub Issues. Keep acceptance criteria in the issue body and update the relevant roadmap/checklist when work is merged. Relative issue links remain valid if the repository is renamed.

## Phase 0 — Architecture and security review

1. [#1 Review threat model and approve architecture baseline](../../issues/1)
2. [#2 Verify AIC permission and purpose model against akita Android tree](../../issues/2)

Phase 0 remains **READY FOR REVIEW** until Issue #1 is completed, the platform-specific review is recorded, and the exit checklist is signed off.

## Phase 1 — Local AI Runtime

3. [#3 Define versioned AIDL contract for isolated AIC runtime](../../issues/3)
4. [#4 Implement isolated Android inference service prototype](../../issues/4)
5. [#5 Build Model Manager with integrity verification and rollback](../../issues/5)
6. [#6 Evaluate LiteRT-LM and ONNX Runtime on Tensor G3](../../issues/6)
7. [#7 Enforce inference lifecycle, deadlines, cancellation and resource budgets](../../issues/7)
8. [#8 Add Pixel 8a benchmark harness and baseline report](../../issues/8)
9. [#9 Add runtime security and offline integration test suite](../../issues/9)

## Build and CI

10. [#10 Add Gradle Wrapper and verify clean-checkout CI](../../issues/10)

## Working rules

- No issue is closed solely because documentation or code has been drafted; acceptance criteria and tests must pass.
- Keep model backend selection open until the benchmark report is reviewed.
- No privileged `system_server` integration before the security gate.
- Do not commit model weights, credentials, private prompts, health data, or other user data.
