# AlbkestrelOS Intelligence Core (AIC)

**Local-first. Privacy-first. Offline-capable.**

AIC is the proposed on-device intelligence layer for AlbkestrelOS. It is designed to integrate with Android/AOSP through isolated runtimes, narrowly scoped system services, explicit permission checks, and auditable action APIs.

> **Project status: Architecture / Phase 0.** This repository is a scaffold and design baseline. It does not yet contain a production inference runtime, privileged Android service, or ROM integration.

## Non-negotiable principles

- No mandatory cloud dependency.
- No automatic transmission of personal data to AI services.
- Local inference and offline operation are first-class requirements.
- AI never receives unrestricted kernel or `system_server` access.
- All data access and system actions pass through explicit policy and authorization boundaries.
- Least privilege, process isolation, auditable decisions, and deterministic fallback.
- User control over models, memory, permissions, and action history.

## Architecture at a glance

```text
AlbkestrelOS
├── Android Framework
│   └── AIC System Service
│       ├── Policy Engine
│       ├── Permission Broker
│       └── Action Registry
├── AIC Runtime (isolated process)
│   ├── Model Manager
│   ├── Inference Engine
│   └── Context Manager
├── AIC Storage
│   ├── Local Memory
│   ├── User Preferences
│   └── Encrypted Index
└── System Applications
    ├── Assistant
    ├── Intelligence Settings
    ├── Automation
    └── Optional integrations
```

The model runs outside `system_server`. Android framework integration is a later phase, after the runtime and security boundaries are validated.

## Repository layout

```text
.
├── app/
│   ├── assistant/
│   └── intelligence-settings/
├── core/
│   ├── inference-api/ (initial Kotlin contracts + tests)
│   ├── ai-runtime/
│   ├── context-engine/
│   ├── inference-api/
│   └── model-manager/
├── docs/
│   ├── architecture/
│   ├── decisions/
│   ├── security/
│   └── development/
├── integrations/
│   ├── android-framework/
│   ├── automation/
│   └── health-connect/
├── security/
│   ├── permissions/
│   ├── selinux/
│   └── cryptography/
├── services/
│   ├── action-orchestrator/
│   ├── intelligence-service/
│   ├── local-memory/
│   ├── permission-broker/
│   ├── privacy-guardian/
│   └── system-intelligence/
└── tests/
    ├── integration/
    ├── performance/
    ├── privacy/
    ├── security/
    └── unit/
```

Most runtime and integration directories remain placeholders. The first executable Kotlin module is `core/inference-api`, with request/model validation and unit tests. See [BUILDING.md](docs/development/BUILDING.md) for local verification.

## Initial technology direction

| Area | Candidate | Status |
|---|---|---|
| Android-facing services | Kotlin / Java | Proposed |
| Native runtime boundary | C++ where justified | Proposed |
| Inference backend | LiteRT or ONNX Runtime | Evaluate in Phase 1 |
| IPC | Binder / AIDL | AOSP integration phase |
| Local persistence | SQLite / Room where appropriate | Evaluate per module |
| Key management | Android Keystore | Android implementation |
| Isolation | Android sandbox + SELinux | Mandatory design constraint |
| UI | Jetpack Compose | Proposed |

Do not treat this table as a final dependency lock. Select the inference backend only after a Pixel 8a (`akita`) benchmark and compatibility review.

## Roadmap

1. **Phase 0 — Architecture & Security Foundation:** threat model, data classification, API contracts, permission model, ADRs.
2. **Phase 1 — Local AI Runtime:** backend abstraction, model loading, local inference, resource limits, model integrity.
3. **Phase 2 — Privacy Guardian:** permission broker, encrypted storage, access audit, memory controls.
4. **Phase 3 — Intelligence Services:** intent recognition, context engine, local retrieval, preferences.
5. **Phase 4 — Action Orchestrator:** typed plans, policy validation, confirmations, safe execution.
6. **Phase 5 — AOSP Integration:** isolated runtime service, Binder/AIDL, framework integration, Settings UI.
7. **Phase 6 — Validation & Release:** offline, security, privacy, thermal, battery, and performance validation.

See [ROADMAP.md](docs/development/ROADMAP.md).

## Target device

Initial development target: Google Pixel 8a (`akita`), Android/AOSP-based, 8 GB RAM. Model size, quantization, acceleration, and memory budgets remain hypotheses until measured on-device. The initial model evaluation range is small quantized models; no model is selected yet.

## Security model

AIC follows this execution rule:

**AI proposes → policy evaluates → permission broker authorizes → constrained adapter executes → audit records the result.**

The model must not:
- access private app databases directly;
- invoke arbitrary shell commands;
- call arbitrary Binder services;
- modify kernel state;
- bypass Android permissions or SELinux;
- silently persist personal data;
- silently use network access.

See [SECURITY_MODEL.md](docs/security/SECURITY_MODEL.md) and [THREAT_MODEL.md](docs/security/THREAT_MODEL.md).

## Development status

The repository contains the initial pure-Kotlin inference API contract, request/model validation, unit tests, and CI workflow. It does not yet contain a production inference backend, Android runtime service, privileged framework integration, or ROM integration. The Phase 0 design package is ready for maintainer review; see [PHASE_0_EXIT_CHECKLIST.md](docs/development/PHASE_0_EXIT_CHECKLIST.md). Every implementation PR must include tests, threat-model impact, permission impact, and offline behavior notes.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Security-sensitive changes should follow [SECURITY.md](SECURITY.md).

## Repository metadata

- Suggested repository name: `AlbkestrelOS-Intelligence`
- Suggested description: `Privacy-first, local AI architecture and runtime for AlbkestrelOS / Android AOSP.`
- Suggested topics: `android`, `aosp`, `on-device-ai`, `privacy`, `kotlin`, `llm`, `offline-first`, `albkestrelos`
