# Development Roadmap

## Phase 0 — Architecture & Security Foundation

**Goal:** establish stable boundaries before writing privileged code.

Deliverables:
- [x] Initial repository scaffold
- [x] Architecture baseline
- [x] Initial security model
- [x] Initial threat model
- [x] Privacy requirements
- [x] Draft API contracts
- [x] ADR: runtime isolation decision; backend evaluation criteria documented
- [x] Data classification schema
- [x] Permission and purpose model
- [x] Pixel 8a runtime design baseline
- [x] Phase 0 exit checklist
- [ ] Maintainer threat-model review and formal Phase 0 sign-off

Exit criteria:
- Every trust boundary is documented.
- Every data category has an owner/provider and permission rule.
- No unresolved design allows model output to authorize itself.
- Runtime network policy is documented and testable.

## Phase 1 — Local AI Runtime

**Status:** Backend-neutral API contract, streaming file integrity verifier, validation tests, and CI workflow are committed. No test pass is claimed yet; Android service and model backend are not implemented.

Deliverables:
- [x] Initial Kotlin inference API interface and request/model validation
- [x] Backend-neutral Kotlin engine interface
- [ ] Concrete backend adapter
- [x] Streaming SHA-256 and declared-size verification utility
- [ ] Trusted signature/provenance verification
- [ ] Model staging, activation, unload, and rollback
- Local model loading and unload
- Token/time/memory budgets
- Offline inference test
- Crash isolation test
- Pixel 8a benchmark harness

Exit criteria:
- Inference works with network disabled.
- Invalid model digest is rejected.
- Runtime failure does not crash the host application.
- Memory and latency are measured on physical hardware.

## Phase 2 — Privacy Guardian

Deliverables:
- Permission broker prototype
- Purpose-bound data access
- Encrypted local persistence
- User memory controls
- Metadata-only audit events
- Permission denial tests

## Phase 3 — Intelligence Services

Deliverables:
- Intent classification
- Context assembly with minimization
- Local retrieval
- User-approved preferences
- Deterministic fallback

## Phase 4 — Action Orchestrator

Deliverables:
- Typed action registry
- Policy evaluator
- Confirmation flow
- Short-lived action-bound authorization
- Adapter execution and audit

## Phase 5 — AOSP Integration

Deliverables:
- AIC framework service
- Binder/AIDL contracts
- Isolated runtime service
- SELinux policy review
- System Settings integration

No privileged integration until security review approves the design.

## Phase 6 — Validation & Release

Deliverables:
- Offline/network audit
- Security and privacy test suites
- Battery, thermal, memory, and latency benchmarks
- Model provenance and rollback tests
- ROM integration documentation
