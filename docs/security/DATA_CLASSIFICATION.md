# AIC Data Classification Schema

**Status:** Phase 0 baseline — version 1.0  
**Applies to:** AIC services, providers, runtime requests, memory, logs, and action history.

## Classification levels

| Level | Name | Examples | Default handling |
|---|---|---|---|
| D0 | Public | Published docs, public model metadata, public app settings | May be used without personal-data grants |
| D1 | Internal | Non-personal device diagnostics, feature flags, aggregate performance metrics | Local use; do not publish raw diagnostics by default |
| D2 | Sensitive | Contacts, messages, calendar entries, location, health records, personal prompts, user memory | Purpose-specific permission; minimize; no raw prompt logging; ephemeral unless explicitly saved |
| D3 | Restricted | Passwords, authentication tokens, private keys, Keystore material, recovery secrets, authorization tokens | Never enter model context; never log or persist in AIC memory; handle only in dedicated security components |

A dataset inherits the highest classification of any field it contains. Combining data does not lower its classification.

## Data categories and provider ownership

| Category | Default class | Authoritative provider | AIC access rule |
|---|---|---|---|
| Contacts | D2 | Android Contacts Provider | Explicit contacts permission plus a narrow purpose and field scope |
| Calendar | D2 | Calendar Provider | Explicit calendar permission and event/time-range scope |
| Health | D2 | Health Connect / approved provider | Explicit health permission per record type and purpose |
| Location | D2 | Android Location APIs | Foreground/one-shot access by default; background access requires a separate review |
| Messages / call metadata | D2 | Platform APIs or user-selected app export | No privileged read path assumed; API availability and platform policy must be verified |
| User prompts | D2 | AIC UI/session | Process for the active request only; not retained by default |
| User memory | D2 | AIC Local Memory Store | Opt-in, inspectable, editable, and deletable |
| Model files and public model cards | D0/D1 | Model Manager / declared source | Verify provenance, digest, license, and package format |
| Authorization and signing secrets | D3 | Android Keystore / policy broker | Never expose to inference or user memory |

Provider availability is not a permission. Each integration must separately verify the Android API, runtime permission, app-op, role, and ROM-specific restrictions that apply.

## Required metadata for every context item

Each item passed between trusted AIC components must carry, directly or through an immutable envelope:

- `dataCategory` and `classification`;
- `providerId` and originating user/profile;
- `purposeId` and requesting feature;
- minimum required scope (fields, records, or time range);
- collection timestamp and expiry/retention rule;
- permission/grant reference;
- transformation state (raw, redacted, aggregated, or summarized).

The model must not be allowed to set or lower the classification, purpose, permission reference, or retention rule.

## Processing and retention rules

1. Collect the smallest data scope needed for the declared purpose.
2. Prefer provider-side filtering, redaction, and aggregation before data reaches the runtime.
3. D2 data is ephemeral by default. Persist it only through an explicit, separately authorized memory operation.
4. D3 data is excluded from prompt construction, model context, telemetry, analytics, and audit payloads.
5. Audit events contain identifiers and decision metadata, not raw prompts or personal records.
6. Export and backup behavior must be reviewed per data category; D2/D3 must not enter automatic cloud backup by default.
7. Deletion must cover active memory, indexes, caches, derived summaries, and retained copies according to the declared retention policy.

## Declassification

A transformation can reduce exposure only when it is deterministic, documented, and tested. Aggregation or redaction must occur in a trusted provider/adapter, not by asking the model to anonymize its own input. D3 cannot be declassified into model context.

## Review requirement

Every new provider integration must add a row to this schema and tests for:
- permission denied;
- wrong user/profile;
- over-broad scope;
- expired grant;
- retention and deletion;
- accidental logging;
- offline operation.
