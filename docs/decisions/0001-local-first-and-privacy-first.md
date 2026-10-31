# ADR 0001: Local-First and Privacy-First Architecture

- **Status:** Accepted baseline
- **Date:** 2026-10-31

## Context

AIC is intended to provide system-level intelligence without making personal data dependent on cloud services.

## Decision

- Core inference runs on-device.
- Core functionality must work offline.
- No automatic personal-data transmission to AI services.
- Memory and history are user-controlled.
- Data access and actions are mediated by trusted services.
- Inference is isolated from `system_server`.
- AI does not receive direct kernel access or unrestricted system privileges.

## Consequences

Positive:
- Reduced external data exposure.
- Offline capability.
- Clear trust boundaries.
- Replaceable model backends.

Trade-offs:
- Model capability is constrained by device resources.
- Model packaging and updates require integrity controls.
- Local inference adds battery, thermal, and storage costs.
- Some tasks may be slower or unavailable compared with cloud systems.

## Revisit when

A concrete requirement cannot be met locally. Any proposed network exception must be optional, separately permissioned, visible, and must not transmit personal context by default.
