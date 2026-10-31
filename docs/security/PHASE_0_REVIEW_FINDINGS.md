# Phase 0 Security Review Findings

**Status:** BLOCKED — preliminary repository design/code review; no platform security audit performed.  
**Reviewed:** 2026-10-31  
**Scope:** Current `main` documentation and `core:inference-api` source. The local Pixel 8a/akita Android tree was not available for this review.

## Executive result

The architecture establishes useful security invariants: inference stays outside `system_server`; model output is untrusted; provider access and action authorization belong to trusted host components; failures should deny; and the runtime is intended to run in an isolated process.

This is not sufficient for Phase 0 sign-off. Several guarantees remain design statements without an implemented enforcement point or target-platform evidence. This review records the gaps; it does not certify the ROM or runtime as secure.

## Findings

### F0-01 — Model authenticity and signing-key lifecycle are unspecified

**Priority:** High  
**Evidence:** `ModelDescriptor` carries a SHA-256 digest and license identifier. `ModelIntegrityVerifier` verifies size and digest and explicitly delegates publisher/signature trust to Model Manager. The security documents require a trusted signature but do not define trust roots, key rotation, revocation, publisher identity, transparency/provenance, or rollback policy.

**Risk:** A digest proves that bytes match a declared digest; it does not establish who authorized that digest. If the manifest and model come from the same compromised or untrusted source, integrity verification alone is insufficient.

**Required work:** Define a signed manifest format, trusted key provisioning, signature algorithm and canonicalization, key rotation/revocation, version/rollback policy, and failure behavior. Add positive, tampered-signature, unknown-key, revoked-key, and rollback tests.

### F0-02 — Verified model bytes are not yet bound to the runtime file descriptor

**Priority:** High  
**Evidence:** The verifier accepts an `InputStream`; the runtime design separately says the host opens a read-only file descriptor and passes it to the isolated process. No implementation or test demonstrates that the descriptor refers to the exact object/bytes that were verified.

**Risk:** Verifying a path or stream and later reopening the path can create a time-of-check/time-of-use gap. Read-only access in the consumer does not by itself prove that the underlying file cannot be modified by another writer.

**Required work:** Design the flow around one opened file object: open with restrictive flags, verify bytes through that opened descriptor, rewind/duplicate that same descriptor, prevent mutation/replacement for the lifetime of inference, and pass only the constrained descriptor. Prove the behavior on target Android with tests for replacement, mutation, descriptor duplication, and read-only enforcement.

### F0-03 — Inference request schema conflicts with structured-context requirements

**Priority:** Medium  
**Evidence:** `API_CONTRACTS.md` says production requests must use structured content types. The current `InferenceRequest` exposes a single unrestricted `input: String`. It also lacks explicit context-item classification, provider provenance, expiry, and immutable purpose metadata. The conceptual API describes a deadline while the implementation uses a relative `timeoutMillis`.

**Risk:** A plain string makes it harder to enforce field-level minimization, classification inheritance, source provenance, and retention rules at the runtime boundary. Relative timeouts can also be mishandled if queue time is not included.

**Required work:** Define a versioned structured envelope with typed content segments, classification, source/provider identifier, purpose identifier from a trusted registry, user/profile scope, expiry, and strict size limits. Define whether deadlines are absolute monotonic timestamps or end-to-end budgets, including queue time. Keep authorization decisions outside the runtime; metadata sent to the runtime is descriptive, not authority.

### F0-04 — Authorization broker and one-time action-token enforcement are design-only

**Priority:** High  
**Evidence:** `PERMISSION_PURPOSE_MODEL.md` describes short-lived, single-use, action-bound authorization tokens, but no broker, token format, atomic consumption store, or action-adapter enforcement is present in the reviewed implementation.

**Risk:** A documented token model does not prevent replay, caller substitution, parameter substitution, stale policy use, or race conditions until the trusted issuer and consumer enforce it.

**Required work:** Specify token cryptographic binding and key ownership, caller/user/profile binding, canonical parameter digest, expiry clock, nonce uniqueness, atomic single-use consumption, policy-version invalidation, and adapter-side validation. Add replay, race, expiry, caller mismatch, parameter mismatch, and revocation tests before action execution is enabled.

### F0-05 — Threat model lacks prioritization and several trust-compromise scenarios

**Priority:** Medium  
**Evidence:** `THREAT_MODEL.md` lists assets, actors, threats, mitigations, and abuse tests, but has no severity/likelihood ratings, explicit trust-boundary attack analysis, or documented scenarios for a compromised trusted host/policy broker, malicious or compromised OS/platform, signing-key compromise, malicious accessibility/overlay interaction, and physical access beyond the brief local-attacker entry.

**Risk:** Reviewers cannot consistently prioritize mitigations or determine which compromises are in/out of scope. A compromised trusted host is especially important because it owns provider access and policy decisions.

**Required work:** Add a threat register with unique IDs, assets, entry points, preconditions, impact, likelihood/severity, mitigation owner, test/evidence, residual risk, and acceptance owner. Explicitly state platform/root compromise assumptions and the limits of protection against them.

### F0-06 — Android isolation and network-denial claims are not validated on akita

**Priority:** High  
**Evidence:** ADR 0003 and runtime design describe `isolatedProcess=true`, `exported=false`, no provider access, no network capability, and a read-only model descriptor handoff. The actual Android manifest, service binding, UID, SELinux policy, Binder behavior, and network behavior were not part of this repository review.

**Risk:** Android behavior and ROM policy are platform-specific. A design statement cannot establish the actual UID/permissions, SELinux domain, service visibility, descriptor behavior, or network denial on the target build.

**Required work:** Keep this as a hard dependency on Issue #2. Inspect the exact akita tree and then build a minimal isolated-service prototype. Capture evidence for UID/permissions, exported/binding behavior, SELinux denials, provider isolation, network denial, process death, and FD handoff. Do not add privileged permissions or SELinux exceptions as a shortcut.

### F0-07 — Multi-user, backup, and deletion guarantees are incomplete

**Priority:** Medium  
**Evidence:** Data classification and permission documents require user/profile scoping, deletion of derived data, and exclusion of D2/D3 from automatic cloud backup. Security model lists multi-user and backup behavior as open questions; no concrete storage/backup configuration or deletion test is present.

**Risk:** Sensitive data could cross user/profile boundaries or survive deletion in indexes, caches, backups, or derived summaries.

**Required work:** Define per-user storage ownership, work-profile behavior, Android backup/data-extraction rules, Keystore key scope/invalidation, retention lifecycle, and deletion coverage for databases, indexes, caches, exports, logs, and model-side temporary files. Add tests for user switching, profile removal, backup exclusion, and deletion verification.

### F0-08 — API-level tests do not yet demonstrate runtime security properties

**Priority:** Medium  
**Evidence:** Current CI validates the pure Kotlin inference API module. It does not instantiate an Android isolated service or exercise Binder identity, SELinux, provider access, network denial, model FD handoff, cancellation under process death, or action authorization.

**Risk:** Passing unit tests establish input-validation behavior, not the Android security boundary described by the architecture.

**Required work:** Keep JVM contract tests, then add Android instrumentation/integration tests on an emulator and physical akita. Separate test evidence by layer and do not treat a successful JVM build as a platform security sign-off.

## Positive controls already present

- Inference is explicitly kept outside `system_server`.
- Model output is documented as untrusted data, never authorization or executable code.
- Permission denial is fail-closed and must not trigger a less restrictive fallback.
- Request and model metadata have bounded validation.
- SHA-256 verification streams data instead of loading the complete model into memory.
- The Gradle Wrapper and CI test/build path now pass on GitHub Actions.

## Gate decision

**Phase 0 remains READY FOR REVIEW / BLOCKED; do not mark it CLOSED.**

Required before sign-off:
1. Resolve or formally accept F0-01 through F0-05 with named evidence and an accountable maintainer.
2. Complete Issue #2 against the actual akita Android tree (F0-06).
3. Define and test storage, backup, multi-user, and deletion behavior (F0-07).
4. Add Android integration evidence beyond JVM CI (F0-08).
5. Approve the versioned Binder/AIDL contract and model-file capability handoff.
6. Record explicit maintainer approval in `PHASE_0_EXIT_CHECKLIST.md`.

This review is based on the repository files inspected on 2026-10-31. It is not a penetration test, code audit of the full Android tree, or security certification.
