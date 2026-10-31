# ADR 0003: Isolated Runtime Process and Pluggable Inference Backend

- **Status:** Accepted for prototype boundary; backend selection deferred
- **Date:** 2026-10-31

## Context

AIC must execute local inference without placing model loading or generation in the Android framework's critical `system_server` process. The Pixel 8a (`akita`) has 8 GB RAM and a Tensor G3 SoC, so memory, thermal, and accelerator behavior must be measured rather than assumed.

## Decision

### Runtime process boundary

- The first Android runtime prototype is a dedicated bound Android `Service` declared with `android:isolatedProcess="true"` and `android:exported="false"`.
- The service receives only a narrow typed Binder contract.
- The isolated runtime does not receive direct provider access, general filesystem access, broad Android permissions, or network capability.
- The trusted host/Model Manager validates model metadata and digest, opens the approved model file read-only, and passes a bounded file descriptor capability to the runtime when the platform API supports the required flow.
- User context is minimized by providers and passed per request; the runtime cannot query Contacts, Calendar, Health Connect, or other providers itself.
- No AIC service is added to `system_server` in Phase 0 or Phase 1.

Android documents `android:isolatedProcess` as running the service in a special process with no permissions of its own and communication through the Service API. The exact Binder/file-descriptor flow must be proven on the target Android build before production use.

### Backend boundary

- Define a backend-neutral `InferenceEngine` contract.
- Keep model format and backend dependencies behind adapters.
- Evaluate LiteRT-LM first for text-generation experiments and ONNX Runtime Mobile as a comparison candidate.
- Do not commit to either backend until model licensing, Tensor G3 compatibility, ABI requirements, memory, cold/warm load, time-to-first-token, token throughput, thermal, and battery measurements are recorded.
- CPU execution is the baseline. GPU/NNAPI or other acceleration must be benchmarked per model and device; no NPU support is assumed.

## Consequences

- Runtime crashes and resource pressure are more containable than inference in the framework process.
- Binder contracts, lifecycle management, and file-descriptor handling become security-critical.
- The runtime must tolerate process death and model reload.
- A second process and IPC add latency and complexity.
- The prototype can be tested without privileged ROM changes.

## Rejected alternatives

- Running inference inside `system_server`: rejected because it couples model instability and memory pressure to a critical framework process.
- Giving a normal app process direct access to every provider and system API: rejected because it collapses the intended trust boundaries.
- Selecting a backend based only on model size or marketing claims: rejected because performance is device/model/driver specific.

## Revisit when

On-device benchmark data is available for at least one supported model on the Pixel 8a and the Binder/file-descriptor isolation prototype has passed security tests.
