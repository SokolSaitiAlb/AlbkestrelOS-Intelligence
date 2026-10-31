# AIC System Architecture

**Status:** Proposed baseline — Phase 0  
**Scope:** Android/AOSP integration with local inference and privacy-first controls.

## 1. Goals

- Provide local model inference without a mandatory network connection.
- Keep inference outside `system_server`.
- Expose narrow, versioned interfaces to system and first-party applications.
- Mediate all access to user data and system actions.
- Support deterministic operation when a model is unavailable.
- Keep inference engines replaceable behind a stable API.

## 2. Non-goals

- Putting an LLM in the Linux kernel.
- Giving the model root, shell, unrestricted Binder, or unrestricted filesystem access.
- Replacing Android's permission model, SELinux, app sandbox, or existing security boundaries.
- Depending on cloud inference for core functionality.
- Automatically training or fine-tuning models on personal data.

## 3. Logical layers

| Layer | Component | Responsibility |
|---|---|---|
| L0 | Linux Kernel | Processes, memory, scheduling, devices |
| L1 | Android HAL / native services | Hardware abstraction and native platform services |
| L2 | AIC Runtime | Isolated local model inference |
| L3 | Intelligence Services | Intent, context, planning, retrieval |
| L4 | Privacy & Permission Broker | Data authorization and action policy |
| L5 | Local Knowledge Store | User-approved memory and local knowledge |
| L6 | System Applications | User-facing assistant and settings |

These are logical layers, not a requirement to create seven operating-system processes.

## 4. Process boundaries

```text
Android Framework
└── AIC System Service (later AOSP phase)
    ├── Policy Engine
    ├── Permission Broker
    └── Action Registry

AIC Runtime (separate, isolated process)
├── Model Manager
├── Inference Backend
└── Context Adapter

AIC Storage (app-private initially)
├── User-approved memory
├── Preferences
└── Audit records

AIC Applications
├── Assistant
└── Intelligence Settings
```

The initial prototype should run as a normal application or isolated test process. Privileged integration is explicitly deferred until the threat model and runtime are validated.

## 5. Request lifecycle

1. Receive a user request.
2. Classify the request and identify the minimum context needed.
3. Ask the relevant provider for only the data covered by a valid permission.
4. Build a bounded prompt/context object.
5. Run local inference.
6. Parse output into a strict typed schema.
7. Validate the proposed action against deterministic policy.
8. Request user confirmation when required.
9. Execute through a narrow adapter.
10. Record a privacy-minimized audit event.

Model output is untrusted input. It is never executable code or authorization.

## 6. Runtime abstraction

Application-facing APIs must not depend on LiteRT, ONNX Runtime, or a specific model format. Define an inference interface and backend adapters. Backend selection is a Phase 1 decision based on Android compatibility, model support, memory use, latency, thermal behavior, license, and maintainability.

## 7. Data flow constraints

- Default network policy: no network capability for the inference process.
- Model installation/update may use a separate, explicit user-controlled workflow.
- No personal data leaves the device.
- Providers return purpose-limited data, not raw database access.
- Sensitive context is minimized and discarded after use unless the user explicitly saves it.
- Audit logs must not contain prompts, secrets, raw health data, or message contents by default.

## 8. Failure behavior

- Runtime crash must not crash Android or `system_server`.
- Model unavailable: use deterministic non-AI fallback or report unavailable.
- Permission denied: stop the operation; do not retry through another provider.
- Invalid structured output: reject it.
- Resource pressure: cancel or unload inference gracefully.
- Integrity failure: refuse to load the model.
