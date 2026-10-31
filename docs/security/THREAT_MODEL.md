# AIC Threat Model

**Status:** Initial Phase 0 draft. This is not a completed security audit.

## Assets

- User prompts and context
- Contacts, health, calendar, messages, and other provider data
- User-approved memory and preferences
- Model files and metadata
- Permission grants and policy configuration
- Action history
- Android system integrity and availability

## Threat actors

- Malicious or compromised application
- Maliciously crafted user input or retrieved document
- Tampered or malicious model package
- Compromised inference runtime
- Local attacker with temporary device access
- Accidental misuse by a first-party integration

## Main threats and mitigations

| Threat | Mitigation |
|---|---|
| Prompt injection | Treat retrieved text and model output as untrusted; enforce policy outside model |
| Unauthorized data access | Provider-side permission checks and purpose limitation |
| Unauthorized action | Typed action schema, deterministic policy, confirmation |
| Model tampering | Signature and digest verification |
| Runtime compromise | Isolated process, least privilege, SELinux |
| Data leakage in logs | Redaction and metadata-only audit by default |
| Network exfiltration | No network permission for inference process; network audit |
| Resource exhaustion | Token, time, memory, CPU, and thermal budgets |
| Stale/replayed approval | Short-lived, action-bound authorization tokens |
| Cross-user leakage | User-scoped storage and caller identity validation |

## Required abuse-case tests

- Ask the model to read a provider without permission.
- Include prompt-injection text in a retrieved local document.
- Return malformed or extra fields in an action payload.
- Replay an old action approval.
- Load a model with a mismatched digest.
- Exhaust the token or memory budget.
- Kill the runtime during inference.
- Attempt network access from the inference process.
- Attempt to access another Android user's memory.
- Verify that denial does not trigger fallback to a less restrictive API.

## Residual risks

The model may produce incorrect, misleading, or unsafe recommendations even when system permissions are correctly enforced. User-facing responses must communicate uncertainty, and high-impact domains require domain-specific validation and confirmation.
