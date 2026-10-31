# AIC Privacy Requirements

This document defines engineering requirements, not a public legal privacy notice.

## Defaults

- Local processing only.
- No mandatory cloud connection.
- No AI telemetry enabled by default.
- No automatic model training on user data.
- No automatic retention of prompts or sensitive context.
- No personal data included in diagnostic logs by default.

## User controls

Users must be able to:
- inspect and revoke data-provider permissions;
- disable memory;
- inspect and delete saved memory;
- clear session and action history;
- inspect installed models and their provenance;
- remove models;
- disable optional integrations;
- inspect what data categories were used for an action.

## Data minimization

Each request must declare its purpose and request the smallest data scope that can satisfy it. Sensitive data should be transformed or aggregated by the provider where possible.

## Retention

Working memory is ephemeral. Session memory is temporary and bounded. Persistent user memory is opt-in, inspectable, and deletable. Audit history should retain only the minimum metadata needed for accountability, with user-configurable retention.

## Network

Inference must work offline. Any future network-capable model-management feature must be separate, explicit, visible, and never transmit personal prompts or context.
