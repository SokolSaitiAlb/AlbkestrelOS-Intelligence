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
- [ ] ADR: runtime process and backend selection
- [ ] Data classification schema
- [ ] Permission and purpose model
- [ ] Threat-model review
- [ ] Phase 0 review checklist

Exit criteria:
- Every trust boundary is documented.
- Every data category has an owner/provider and permission rule.
- No unresolved design allows model output to authorize itself.
- Runtime network policy is documented and testable.

## Phase 1 — Local AI Runtime

Deliverables:
- Runtime interface and backend adapter
- Model metadata and integrity verification
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
