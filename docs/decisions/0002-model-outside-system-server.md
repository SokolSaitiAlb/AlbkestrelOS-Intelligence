# ADR 0002: Keep Model Inference Outside `system_server`

- **Status:** Accepted baseline
- **Date:** 2026-10-31

## Context

Large or complex inference workloads can crash, block, or exhaust memory. `system_server` is a critical Android process.

## Decision

Run inference in a separate, constrained process. Framework integration may expose a narrow service API, but model loading and generation do not execute inside `system_server`.

## Consequences

- Runtime crashes are easier to contain.
- Memory and CPU budgets can be enforced independently.
- IPC contracts and lifecycle management are required.
- SELinux and process isolation must be designed before privileged integration.
