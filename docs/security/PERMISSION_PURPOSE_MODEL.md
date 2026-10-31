# AIC Permission and Purpose Model

**Status:** Phase 0 baseline — version 1.0

## 1. Two independent authorization layers

AIC access requires both:

1. **Android/platform authorization:** runtime permission, app-op, role, provider permission, caller identity, and any applicable ROM policy.
2. **AIC authorization:** a current user grant for a specific data category, provider, scope, purpose, and operation.

An Android permission alone does not grant unrestricted AIC use. An AIC grant cannot bypass Android permissions, provider checks, SELinux, or platform policy.

## 2. Purpose is mandatory

Every read or action request declares a stable purpose identifier, feature identifier, requested operation, and minimum data scope. Initial purpose examples:

| Purpose ID | Meaning | Example |
|---|---|---|
| `assistant.answer` | Answer the active user request | Read one user-selected calendar range |
| `assistant.summarize` | Summarize selected content | Summarize text explicitly shared by the user |
| `memory.save` | Persist user-approved memory | Save an item after a separate confirmation |
| `automation.evaluate` | Evaluate a user-authored automation rule | Read only the signal needed by that rule |
| `action.execute` | Execute a typed system action | Change a supported setting after policy approval |

Purpose identifiers are not free-form strings from the model. They are defined in a trusted registry and versioned.

## 3. Grant structure

A grant is bound to:
- Android user/profile;
- calling package and UID (or trusted system component identity);
- provider and data category;
- allowed fields/record types and optional time/range limit;
- allowed purpose IDs and operations;
- issue time, expiry, revocation state, and policy version;
- user-visible grant source and confirmation record.

Use short-lived request grants by default. Any longer-lived grant must be narrowly scoped, visible in settings, revocable, and separately tested. Restricted D3 data is not grantable to the inference engine.

## 4. Authorization pipeline

`request → caller identity → Android permission/provider check → AIC grant → purpose match → scope minimization → deterministic policy → confirmation if required → execution → audit`

Any missing, ambiguous, expired, revoked, mismatched, or malformed element returns DENY. Do not retry through a less restrictive provider or alternate API after denial.

## 5. Action authorization tokens

After policy evaluation (and any required user confirmation), the trusted policy broker may mint a short-lived authorization token containing or cryptographically binding:

- unique nonce and single-use identifier;
- Android user/profile and caller identity;
- action type and canonical parameter digest;
- purpose ID and policy version;
- approved data scope;
- issue and expiry times;
- confirmation state.

The token is not model output. It is issued only by trusted code, validated by the action adapter, consumed once, and rejected on replay, expiry, caller mismatch, parameter mismatch, or policy-version change.

## 6. Confirmation policy

Always require explicit confirmation for:
- external communication or publication;
- deletion or irreversible changes;
- purchases, payments, or other financial operations;
- security/account changes;
- sensitive data sharing;
- any action whose scope or consequence is unclear.

Routine low-impact actions may later support user-configured, narrow, revocable authorization. No broad "AI can do anything" grant is permitted.

## 7. Revocation and audit

Revocation takes effect before the next provider/action operation. Running operations must re-check authorization at the point of use when their scope or lifetime allows it. Audit records capture grant ID, purpose, provider/action ID, decision, and timestamp; they do not store raw prompts or personal payloads.

## 8. Required tests

- no Android permission;
- Android permission exists but AIC purpose grant does not;
- grant has wrong purpose or provider;
- requested scope exceeds grant;
- grant expired or revoked;
- caller UID/user/profile mismatch;
- stale policy version;
- malformed or replayed action token;
- confirmation is missing;
- user revokes grant during a queued operation.
