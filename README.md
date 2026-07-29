# fido-validation-framework

This repository is the FIDO Validation Framework — an enterprise-grade multi-module Java project to validate FIDO2/WebAuthn artifacts for development, regression testing, CI and performance testing.

High-level modules:

- validator-common: DTOs, enums, utilities, shared types
- validator-core: validation interfaces and core validators
- validator-parser: CBOR/COSE/AuthenticatorData/Attestation parsers
- validator-metadata: MDS3 fetch & cache
- validator-api: Spring Boot REST API exposing validation endpoints
- playwright-tests: Playwright TypeScript tests (UI & API)
- docs: documentation and diagrams

What I did now

- Created the multi-module maven scaffold with module poms
- Added minimal shared DTOs and validation interface
- Added a Spring Boot starter module with application class
- Added Docker Compose, Dockerfile for api, Jenkinsfile and README

Next steps I will continue automatically unless you want changes:

1. Implement concrete validators in validator-core (ChallengeValidator, OriginValidator, etc.)
2. Implement CBOR/COSE parsers in validator-parser
3. Implement metadata MDS3 client in validator-metadata
4. Implement full REST controllers in validator-api with endpoints and Swagger
5. Add Playwright tests and Dockerfile for Playwright
6. Add comprehensive unit & integration tests

If you want me to proceed and push the full implementation (validators, parsers, tests) I will continue and push incremental commits so you can review progress.
