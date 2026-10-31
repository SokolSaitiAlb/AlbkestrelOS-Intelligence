# Pixel 8a (akita) Permission Audit — Initial Platform Pass

**Status:** Partial audit; not a platform sign-off.  
**Reviewed:** 2026-10-31  
**Target repository:** [LineageOS/android_device_google_akita](https://github.com/LineageOS/android_device_google_akita)  
**Reviewed ref:** `lineage-24.0`  
**Target product:** Pixel 8a / `akita`

## 1. Scope and evidence

The public device repository is accessible and its default branch is `lineage-24.0`. The following files/directories were inspected:

- `lineage_akita.mk`
- `aosp_akita.mk`
- `device-akita.mk`
- `akita/BoardConfig.mk`
- `akita/product.prop`
- `lineage.dependencies`
- `AndroidProducts.mk`
- `Android.bp`
- `sepolicy/vendor/file_contexts`
- `sepolicy/vendor/file.te`
- `sepolicy/system_ext/private/`
- `sepolicy/system_ext/public/`

The product makefiles identify `akita` as Pixel 8a and inherit common configuration from `device/google/zuma`. The target-specific makefile selects Linux kernel 6.1 and includes zuma common configuration. The board config imports zuma's common BoardConfig and declares vendor plus system_ext public/private SELinux policy directories. The Lineage product makefile sets Android 17 build properties on the reviewed branch.

**Important limitation:** this repository is the device-specific product tree, not the complete Android source manifest. Its existence does not establish the final merged permission policy, runtime permission behavior, provider implementation, app-op defaults, service manifest, or effective SELinux policy. The zuma common tree, framework/base, packages/providers, vendor tree, Health Connect package, and the user's exact local manifest/build fingerprint still need to be examined. No AIC platform permission has been granted or proven by this audit.

## 2. Security boundary confirmed by design, not yet runtime-tested

- AIC must remain outside `system_server`.
- The inference process must not receive contacts, calendar, health, location, notification, SMS, or call-log provider access.
- A trusted host/provider adapter performs Android permission and provider checks, then applies the independent AIC purpose/scope grant.
- Data is minimized and filtered before entering inference. D3 secrets never enter model context.
- Device-tree SELinux directories exist, but no AIC domain, type, allow rule, service label, or file label is proposed by this audit.
- Do not add privileged permissions or SELinux exceptions merely to make an AIC feature work.

## 3. Provider permission matrix — implementation baseline

This matrix records Android API-level mechanisms to validate in the full target source and on-device. It is not evidence that the current ROM grants these permissions to AIC.

| Data category | Android API / authorization surface | Baseline permission or gate | AIC rule / current status |
|---|---|---|---|
| Contacts read | Contacts Provider / `ContactsContract` | `READ_CONTACTS` runtime permission | D2. Host-side, query only requested fields/rows. Not available to isolated inference. |
| Contacts write | Contacts Provider / `ContactsContract` | `WRITE_CONTACTS` runtime permission | Separate write capability; require explicit confirmation for destructive or broad edits. Not part of read grant. |
| Calendar read | Calendar Provider / `CalendarContract` | `READ_CALENDAR` runtime permission | D2. Narrow time window and fields; verify provider and user/profile. |
| Calendar write | Calendar Provider / `CalendarContract` | `WRITE_CALENDAR` runtime permission | Separate action capability; confirm creation/update/deletion according to consequence. |
| Health | Health Connect APIs | Health Connect permission per record type, requested through Health Connect permission flow | D2. A generic Android permission is insufficient. Verify Health Connect availability, package/provider version, record-type grants, user/profile, and revocation. |
| Foreground location | Android location APIs | `ACCESS_COARSE_LOCATION` and/or `ACCESS_FINE_LOCATION` runtime permission | D2. Prefer one-shot/coarse access where sufficient. Never infer background access from foreground permission. |
| Background location | Android location APIs | `ACCESS_BACKGROUND_LOCATION` with platform-specific staged grant flow | D2. Disabled by default; separate feature review and explicit user-facing explanation required. |
| Notifications: post | Notification Manager | `POST_NOTIFICATIONS` runtime permission on Android 13 / API 33+ | Posting notifications is not permission to read notification content. Separate capability. |
| Notifications: read | Notification Listener Service | User-enabled notification-listener special access in Settings | Highly sensitive D2. No listener integration assumed. Must be a separate opt-in with package/source filtering and revocation handling. |
| SMS / message content | Telephony/SMS APIs and default-handler role/policy | Restricted SMS permissions and/or default SMS role; exact eligibility is policy-dependent | No generic AIC read path assumed. Prefer user-selected share/export. Do not request SMS permissions for a general assistant. |
| Call log / call metadata | Call Log Provider / Telecom | Restricted call-log permissions and role/policy requirements | No generic AIC read path assumed. Use explicit user-selected export or supported Telecom APIs for the actual call feature. |
| Phone actions | Telecom / dial intents / role APIs | Public intents or explicit dialer role for deeper integration | Action capability, not a data-read permission. Confirm user intent and keep call execution separate from inference. |

### API level notes

- Runtime dangerous-permission model applies from Android 6.0 / API 23.
- `POST_NOTIFICATIONS` is a runtime permission from Android 13 / API 33.
- Health Connect access is record-type scoped and mediated by Health Connect; device/package availability must be checked at runtime. Do not assume it is part of the device tree.
- Background location and restricted SMS/call-log access have additional platform and policy constraints beyond a manifest declaration.
- The reviewed device makefiles do not establish the final target SDK or the effective permission grants for an AIC package. Confirm these from the complete build and installed image before implementation.

## 4. Mandatory enforcement rules

1. Check the calling UID/package and Android user/profile at the trusted host boundary.
2. Check current Android runtime permission, app-op/provider enforcement, and any relevant role/special access immediately before each provider operation.
3. Check the AIC grant independently: provider, category, fields/record types, purpose ID, user/profile, expiry, and revocation state.
4. Apply provider-side filtering and redaction before constructing an inference request.
5. Re-check authorization for queued/long-running operations; revocation must deny subsequent reads/actions.
6. Treat missing API, unavailable provider, denied permission, ambiguous profile, stale grant, or app-op denial as a hard DENY. Never fall back to a broader API.
7. Do not pass provider URIs, arbitrary paths, raw Binder handles, or provider capabilities into the inference process.
8. Keep audit events metadata-only; never log raw prompts, contacts, health values, notification content, or message bodies.

## 5. Required follow-up before Issue #2 can close

- [ ] Pin the exact local `repo manifest` revision and target build fingerprint used by the user.
- [ ] Inspect `device/google/zuma` common configuration and the full product/vendor makefile inheritance.
- [ ] Inspect the merged framework permission definitions, provider manifests, app-op definitions/defaults, and relevant role requirements.
- [ ] Inspect Health Connect package/API availability and supported record types on the target image.
- [ ] Verify SELinux policy after full policy compilation; source directory presence alone is not proof of effective policy.
- [ ] Verify multi-user/work-profile behavior and permission revocation on the physical Pixel 8a.
- [ ] Build a provider-by-provider instrumentation test matrix for allow, deny, revoke, wrong UID, wrong user/profile, and scope overreach.

## 6. Decision

**Issue #2 remains open.** This pass verifies the public device-tree entry points and records the correct permission surfaces, but it does not yet prove the complete platform policy or runtime behavior. Do not implement privileged access, AIC SELinux policy, or provider access from the isolated inference process based on this partial review.
