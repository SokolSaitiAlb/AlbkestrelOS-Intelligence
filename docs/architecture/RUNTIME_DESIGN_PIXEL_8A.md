# AIC Runtime Design — Pixel 8a (`akita`)

**Status:** Phase 0 design baseline; not a production implementation.

## 1. Target constraints

- Device: Google Pixel 8a (`akita`)
- SoC: Google Tensor G3
- RAM: 8 GB
- Storage: 128 GB
- Target: Android/AOSP-derived AlbkestrelOS
- Initial prototype: application-hosted runtime service; no privileged framework integration

Do not infer available model memory from the physical 8 GB alone. Android, GPU allocations, system services, and foreground applications compete for RAM.

## 2. Process topology

```text
AIC UI / Trusted Host
  ├── Permission & Purpose Broker
  ├── Model Manager
  │     ├── manifest / signature / digest verification
  │     └── open approved model read-only
  ├── Context Providers
  │     └── return minimized, purpose-bound context
  └── Binder client
          │ typed request + bounded data + read-only model FD
          ▼
AIC Runtime Service (isolatedProcess=true, exported=false)
  ├── Runtime lifecycle controller
  ├── InferenceEngine interface
  └── Backend adapter (LiteRT-LM candidate / ORT candidate)
```

The isolated process is a capability boundary, not an authorization broker. It must not decide which personal data the user may access or which Android action may execute.

## 3. Service contract

The initial Binder/AIDL contract should expose only:
- runtime capability and version information;
- initialize with a validated model descriptor and read-only file descriptor;
- generate with a bounded request envelope;
- cancel by request ID;
- query request/runtime state;
- shutdown/unload.

Do not expose arbitrary file paths, raw Binder handles, shell commands, class names, provider URIs, or unrestricted JSON tool calls.

Each request includes a unique request ID, task type, bounded input, output-token limit, deadline, purpose ID, and context classification metadata. The host performs provider access and policy evaluation before invoking inference.

## 4. Lifecycle and resource control

1. Verify model provenance, format, size, and SHA-256 digest before activation.
2. Stage the model; never partially replace the active model.
3. Pass only a read-only file descriptor or an equally constrained model capability.
4. Initialize on a dedicated background executor, never the UI/main thread.
5. Use a single owner thread/executor for engine creation, generation, cancellation, and close unless the selected backend documents another safe model.
6. Bound concurrent requests to one initially; queue with a finite maximum and deadline.
7. Enforce input/output token budgets, wall-clock deadlines, and cancellation.
8. Monitor process memory and device thermal state from the trusted host; unload/cancel when configured resource policy is reached.
9. Treat process death as a failed request; never replay a user action automatically.
10. Close the engine exactly once and make shutdown idempotent at the host boundary.

The initial API caps are guardrails for request validation, not final Pixel 8a resource limits. Tune them with real device measurements.

## 5. Backend evaluation

### Candidate A — LiteRT-LM

Evaluate first for text LLMs. Current Android guidance documents a Kotlin API, CPU/GPU execution, filesystem-based model loading (not bundling multi-hundred-MB/GB model files in APK assets), background initialization, and single-thread ownership for engine/conversation lifecycle. The documented GPU path has native-library requirements. A compatible runtime does not imply Tensor G3 NPU support.

### Candidate B — ONNX Runtime Mobile

Keep as an alternative when the target model is available in ONNX or when supported execution providers offer better measured results. Android CPU is the baseline; XNNPACK and NNAPI require per-model/device measurement. Unsupported operator partitioning can make acceleration slower rather than faster.

### Evaluation record

For every candidate, record:
- model ID, exact revision, format, quantization, license, and cryptographic digest;
- runtime/backend version and ABI;
- clean build and installation procedure;
- cold/warm load time;
- peak and steady-state process RSS;
- time to first token and generation tokens/second;
- CPU/GPU/NPU use where observable;
- thermal status and battery impact;
- offline/network audit;
- cancellation, OOM, crash, and reload behavior.

No model is selected by this design document.

## 6. First implementation sequence

1. Compile and test the backend-neutral Kotlin API module.
2. Add model manifest parsing and SHA-256 verification.
3. Create an Android host prototype and isolated service with no model backend.
4. Prove Binder request/response, lifecycle, cancellation, and read-only model FD handling.
5. Add a LiteRT-LM adapter behind the API after the exact dependency and model are selected.
6. Measure the first approved model on physical Pixel 8a.
7. Compare an ONNX Runtime candidate only if the same task/model quality can be measured fairly.

## 7. Security tests before model inference

- Runtime UID is isolated and does not inherit app permissions.
- Service is not externally exported.
- Runtime cannot access user providers or host private files directly.
- Runtime has no network access.
- Host rejects wrong caller identity and malformed requests.
- Model FD is read-only and refers to the verified model object.
- Digest mismatch blocks initialization.
- Cancellation/deadline stops generation.
- Runtime crash does not crash the host or Android framework.
- No automatic action is executed from model output.
