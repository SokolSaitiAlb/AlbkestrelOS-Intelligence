# Phase 0 Exit Checklist

**Status:** Design package prepared; maintainer review and formal sign-off pending.

## Architecture

- [x] Local-first / offline-first requirements documented.
- [x] Inference outside `system_server` established as a hard boundary.
- [x] Logical layers and trust boundaries documented.
- [x] Runtime prototype isolation strategy recorded in ADR 0003.
- [x] Pixel 8a runtime topology and backend evaluation criteria documented.
- [x] Typed inference/action API principles documented.
- [ ] Review and approve the exact Binder/AIDL request/response contract.

## Security and privacy

- [x] Initial threat model documented.
- [x] Security invariants and fail-closed behavior documented.
- [x] Data classification schema defined.
- [x] Permission and purpose model defined.
- [x] Model supply-chain and digest-verification requirements defined.
- [x] Memory, audit, and deletion requirements documented.
- [ ] Maintainer threat-model review completed.
- [ ] Confirm platform-specific permission/app-op/SELinux requirements against the actual akita ROM tree.

## Build and implementation baseline

- [x] Kotlin inference API contract module created.
- [x] Request and model metadata validation implemented.
- [x] Unit tests added for boundary validation.
- [x] CI workflow added to run the module tests.
- [ ] Build and tests verified on a clean local checkout.
- [ ] Build and tests verified on the physical Pixel 8a / target Android toolchain as applicable.

## Phase 0 exit criteria

Phase 0 can be marked **CLOSED** only when:
1. All architecture and security review items above are approved.
2. No unresolved high-severity threat permits model output to grant permissions or directly execute actions.
3. The runtime isolation and model-file handoff design has a test plan and no requirement for privileged `system_server` inference.
4. The Kotlin API module passes CI.
5. The maintainer records approval in this checklist and closes the Phase 0 review issue.

Until then the status remains **READY FOR REVIEW**, not completed or audited.
