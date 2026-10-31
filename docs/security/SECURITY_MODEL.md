# Security Model

**Status:** Proposed baseline — must be refined before privileged integration.

## Security invariants

1. The model is untrusted.
2. Model output is data, never authority.
3. No direct model-to-kernel or model-to-shell path.
4. No unrestricted Binder access.
5. Every data provider enforces caller identity and user-granted permissions.
6. Every action is validated by deterministic policy outside the model.
7. Sensitive operations require explicit confirmation unless the user has configured a narrowly scoped, revocable authorization.
8. Runtime compromise must be contained by process isolation, SELinux, and least privilege.
9. No network access for inference by default.
10. Failure must be closed: uncertainty or validation failure means deny.

## Trust boundaries

- UI ↔ AIC service
- AIC service ↔ isolated runtime
- AIC service ↔ data providers
- AIC service ↔ action adapters
- Model package ↔ model loader
- AIC storage ↔ Android Keystore

## Authorization pipeline

`request → identity check → permission check → purpose check → policy decision → optional confirmation → adapter execution → audit`

The runtime must not be able to mint permissions, change policy, or mark an action as authorized.

## Model supply-chain requirements

- Verify cryptographic digest and trusted signature before activation.
- Keep model metadata (publisher, version, format, license, digest) with the package.
- Reject malformed or unsupported model files.
- Stage updates separately; activate only after validation.
- Support rollback to the last known-good model.
- Do not execute model package scripts.

## Logging

Default audit metadata may include action ID, provider ID, policy result, timestamp, and status. Do not log raw prompts, sensitive payloads, credentials, or model context by default. Provide retention and deletion controls.

## Android-specific controls

- Keep inference outside `system_server`.
- Use Android app sandboxing and SELinux domains.
- Use Binder/AIDL only for narrow typed contracts.
- Avoid broad privileged permissions.
- Do not weaken SELinux to make integration easier.
- Review exported components, PendingIntent mutability, file-provider exposure, and backup behavior.

## Open questions

- Exact isolated-process model and SELinux domain.
- Whether the first runtime is app-private or a dedicated native service.
- Key invalidation and data behavior after lock-screen credential changes.
- Multi-user and work-profile behavior.
- Secure model update and rollback mechanism.
