# Contributing

## Before changing code

1. Read the architecture and security documents.
2. Identify affected trust boundaries and data categories.
3. State whether the change works offline.
4. State whether it adds permissions, network access, persistence, or privileged operations.
5. Add tests for denial paths and malformed input.

## Pull request requirements

Every PR should include:
- problem statement and scope;
- design and alternatives;
- security/privacy impact;
- permission and data-flow impact;
- tests and device/environment details;
- benchmark evidence for performance-sensitive changes;
- migration or rollback notes.

## Rules

- Do not add cloud dependencies to core functionality.
- Do not log prompts or sensitive payloads by default.
- Do not accept model output as authorization.
- Do not add arbitrary command execution.
- Do not weaken Android sandboxing or SELinux to make a feature work.
- Keep modules independently testable.
