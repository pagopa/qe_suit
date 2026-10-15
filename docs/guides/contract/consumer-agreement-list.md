# WebConsumerAgreementsListPageContractTest

## Purpose

This document defines the contract matrix and implementation approach for the page:

```text
/fruizione/richieste
```

implemented by the frontend component:

```text
ConsumerAgreementsListPage
```

The objective is to detect regressions related to:

- presence of expected components
- absence of unexpected components
- approved copy
- initial component state
- visibility

without relying on screenshot comparison.

---

## References

Frontend component:

```text
ConsumerAgreementsListPage
```

Frontend route:

```text
/fruizione/richieste
```

Destination test:

```text
WebConsumerAgreementsListPageContractTest
```

Reference implementation:

```text
WebAgreementContractTest
```

Execution context:

```text
Tenant: Comune di Milano
Role: Admin
Session: Authenticated
Environment: QA
```

---

## Scope

The contract test must validate:

- presence of expected elements in the DOM
- absence of forbidden elements when explicitly required
- correctness of approved copy
- initial state of interactive controls
- visibility when explicitly required

Presence and visibility are considered different assertions and must be validated independently.

Layout, fonts, colors and visual appearance remain under manual verification and are out of scope.

---

## Page Structure

The page renders the following top-level sections:

```text
PageContainer
 ├─ Title
 ├─ Description
 ├─ Filters
 ├─ ConsumerAgreementsTable
 └─ Pagination
```

The page also defines three filters:

```text
E-Service
Producer
Agreement Status
```

and an agreements table containing:

```text
E-Service column
Provider column
Agreement Status column
Actions column
```

---

# Contract Matrix

## CON-AGRE-001

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Page title
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-002

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Page title
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-003

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Page description
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-004

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Page description
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-005

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Filters container
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-006

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Filters container
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-007

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
E-Service filter
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-008

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
E-Service filter
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-009

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Producer filter
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-010

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Producer filter
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-011

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement Status filter
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-012

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement Status filter
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-013

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Consumer agreements table
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-014

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Consumer agreements table
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-015

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Table column: E-Service
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

Source

```text
ConsumerAgreementsTable.headLabels
```

---

## CON-AGRE-016

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Table column: Provider
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

Source

```text
ConsumerAgreementsTable.headLabels
```

---

## CON-AGRE-017

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Table column: Agreement Status
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

Source

```text
ConsumerAgreementsTable.headLabels
```

---

## CON-AGRE-018

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Pagination component
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present
```

---

## CON-AGRE-019

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Pagination component
```

Property

```text
Visibility
```

Expected Result

```text
Visible
```

---

## CON-AGRE-020

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement action button
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present when at least one agreement row is available
```

Source

```text
ConsumerAgreementsTableRow
```

---

## CON-AGRE-021

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement Action Menu
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present when at least one agreement row is available
```

Source

```text
ConsumerAgreementsTableRow
```

---

## CON-AGRE-022

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement Status Chip
```

Property

```text
Presence in DOM
```

Expected Result

```text
Present when at least one agreement row is available
```

Source

```text
ConsumerAgreementsTableRow
```

---

## CON-AGRE-023

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
E-Service filter
```

Property

```text
Initial state
```

Expected Result

```text
Enabled
```

Rationale

```text
No frontend logic disables the component on page load.
```

---

## CON-AGRE-024

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Producer filter
```

Property

```text
Initial state
```

Expected Result

```text
Enabled
```

Rationale

```text
No frontend logic disables the component on page load.
```

---

## CON-AGRE-025

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement Status filter
```

Property

```text
Initial state
```

Expected Result

```text
Enabled
```

Rationale

```text
No frontend logic disables the component on page load.
```

---

# Proposed Scenarios

The contract implementation should create one WebScenario for each expectation.

Example:

```java
new WebScenario<>(
    "Title should be visible",
    page -> {},
    page -> Assertions.assertThat(
        page.title().isVisible()
    ).isTrue()
)
```

Example:

```java
new WebScenario<>(
    "Agreement status filter should be present",
    page -> {},
    page -> Assertions.assertThat(
        page.statusFilter().isPresent()
    ).isTrue()
)
```

Example:

```java
new WebScenario<>(
    "Pagination should be visible",
    page -> {},
    page -> Assertions.assertThat(
        page.pagination().isVisible()
    ).isTrue()
)
```

---

# Page Object Responsibilities

Create:

```text
ConsumerAgreementsListPage
```

Responsibilities:

- route definition
- assertLoaded()
- title access
- description access
- filters access
- table access
- pagination access
- column header access
- agreement row access

Suggested API:

```java
page.title()
page.description()

page.eserviceFilter()
page.producerFilter()
page.statusFilter()

page.agreementsTable()

page.eserviceColumn()
page.providerColumn()
page.statusColumn()

page.pagination()
```

---

# Known Limitations

The following validations are intentionally excluded because a definitive approved source has not yet been provided:

- page title copy
- page description copy
- filter labels copy
- table header copy
- empty state copy
- inspect/edit button copy
- tooltip copy
- delegated agreement labels
- notification badge behaviour
- absence checks for forbidden components

These controls may be added once the approved translations and TM checklist become available.

---

# Current Status

```text
READY FOR REVIEW

Page structure identified.
Contract matrix drafted.

Additional approved copy sources are required
before implementing copy-validation scenarios.
```