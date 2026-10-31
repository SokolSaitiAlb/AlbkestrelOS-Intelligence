# Phase 1 — Local AI Runtime Plan

**Status:** Planning baseline. Only the backend-neutral API contract and validation tests are currently implemented.

## Objective

Build an on-device inference runtime that can be initialized, queried, cancelled, unloaded, and recovered without privileged framework changes or a mandatory network connection.

## Work packages

### P1.1 — Inference API

- [x] Kotlin/JVM inference contract module
- [x] Typed task, request, result, model descriptor, and engine interface
- [x] Bounds validation for request IDs, input length, output tokens, deadlines, model size, and digest syntax
- [x] Unit tests and CI workflow
- [ ] Versioned AIDL contract for Android process boundary

### P1.2 — Model Manager

- [ ] Strict model manifest schema
- [x] SHA-256 digest verification utility
- [ ] Signature/provenance verification strategy
- [ ] Model staging, activation, rollback, and deletion
- [ ] File descriptor handoff to isolated service

### P1.3 — Isolated Android Runtime

- [ ] Android host app/service prototype
- [ ] Isolated runtime service (`isolatedProcess=true`, `exported=false`)
- [ ] Binder lifecycle and identity checks
- [ ] Bounded queue, single in-flight request initially
- [ ] Cancellation, timeout, process-death, and restart behavior
- [ ] No provider access and no network in inference process

### P1.4 — Backend adapter

- [ ] LiteRT-LM dependency and API compatibility spike
- [ ] Model license/provenance review
- [ ] CPU baseline
- [ ] GPU feasibility test on Tensor G3
- [ ] ONNX Runtime comparison where justified
- [ ] No backend lock-in in public API

### P1.5 — Device benchmark

- [ ] Baseline idle memory and thermal state
- [ ] Cold/warm load time
- [ ] Peak and steady-state RSS
- [ ] Time to first token and tokens/second
- [ ] CPU/GPU utilization where observable
- [ ] Battery/thermal impact
- [ ] Memory-pressure, cancellation, crash, and reload tests

## Phase 1 acceptance

- Works with network disabled.
- Model fails closed on digest/signature/format errors.
- Runtime process is isolated and cannot access personal providers directly.
- Runtime crash cannot crash the host or Android framework.
- Input/output/deadline limits are enforced.
- Device benchmarks are reproducible and include model/runtime/license identity.
- No automatic system action is executed by the inference process.
