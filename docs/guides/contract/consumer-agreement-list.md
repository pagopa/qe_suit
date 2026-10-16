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

The contract test validates:

- presence of expected elements
- page-level content
- approved filter labels
- initial filter state
- table structure
- table header content
- table row consistency
- presence of navigation and pagination controls

Layout, spacing, colors, fonts and visual rendering remain outside the scope of the contract and are validated manually.

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

The page exposes the following filters:

```text
E-Service
Producer
Agreement Status
```

The agreements table contains:

```text
E-Service
Provider
Agreement Status
Actions
```

---

# Contract Matrix

## CON-AGREE-01

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
Content
```

Expected Result

```text
Not blank
```

Implementation

```java
page.pageTitle().read()
```

Assertion

```text
isNotBlank()
```

---

## CON-AGREE-02

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
Content
```

Expected Result

```text
Not blank
```

Implementation

```java
page.pageDescription().read()
```

Assertion

```text
isNotBlank()
```

---

## CON-AGREE-03

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
E-Service filter label
```

Property

```text
Copy validation
```

Expected Result

```text
Cerca per e-service
```

Implementation

```java
page.eServiceFilterLabel().read()
```

---

## CON-AGREE-04

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
E-Service filter input
```

Property

```text
Initial state
```

Expected Result

```text
Empty value
```

Implementation

```java
page.eServiceFilterInput().read()
```

Assertion

```text
isEmpty()
```

---

## CON-AGREE-05

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Producer filter label
```

Property

```text
Copy validation
```

Expected Result

```text
Cerca per erogatore
```

Implementation

```java
page.producerFilterLabel().read()
```

---

## CON-AGREE-06

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Producer filter input
```

Property

```text
Initial state
```

Expected Result

```text
Empty value
```

Implementation

```java
page.producerFilterInput().read()
```

Assertion

```text
isEmpty()
```

---

## CON-AGREE-07

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Status filter label
```

Property

```text
Copy validation
```

Expected Result

```text
Stato della richiesta
```

Implementation

```java
page.statusFilterLabel().read()
```

---

## CON-AGREE-08

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Status filter input
```

Property

```text
Initial state
```

Expected Result

```text
Empty value
```

Implementation

```java
page.statusFilterInput().read()
```

Assertion

```text
isEmpty()
```

---

## CON-AGREE-09

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

Implementation

```java
page.agreementsTable().get().isPresent()
```

Assertion

```text
true
```

---

## CON-AGREE-10

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Table header
```

Property

```text
Header structure and copy
```

Expected Result

```text
Column 1 = E-service
Column 2 = Erogatore
Column 3 = Stato richiesta
```

Implementation

```java
TableRow header=
        page.agreementsTable()
        .tHead()
        .rows()
        .get(0);
```

Validations

```java
header.cells().get(0).read().equals("E-service")
        header.cells().get(1).read().equals("Erogatore")
        header.cells().get(2).read().equals("Stato richiesta")
```

---

## CON-AGREE-11

Context

```text
Authenticated user
Comune di Milano
Admin
```

Element

```text
Agreement table rows
```

Property

```text
Row structure and content validation
```

Expected Result

For every row:

```text
Column 1 contains a non-empty E-Service value
Column 2 contains a non-empty Provider value
Column 3 contains a non-empty Status value
Column 4 contains the text "Visualizza"
```

Implementation

```java
page.agreementsTable()
        .tBody()
        .rows()
```

Validation Rules

```text
cell[0] -> not blank
cell[1] -> not blank
cell[2] -> not blank
cell[3] -> equals "Visualizza"
```

Failure Reporting

```text
Each row is validated independently.

Error messages include the failing row index
to simplify defect identification.
```

Examples

```text
E-Service cell value at row 15
Provider cell value at row 15
Status cell value at row 15
Cell with detail button at row 15
```

---

## CON-AGREE-12

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

Implementation

```java
page.pagination().get().isPresent()
```

Assertion

```text
true
```

---

# Implemented Page Model

## ConsumerAgreementsListPage

Responsibilities:

- route definition
- assertLoaded()
- page title access
- page description access
- filter label access
- filter input access
- agreements table access
- pagination access

Current API:

```java
page.pageTitle()
        page.pageDescription()

        page.eServiceFilterLabel()
        page.eServiceFilterInput()

        page.producerFilterLabel()
        page.producerFilterInput()

        page.statusFilterLabel()
        page.statusFilterInput()

        page.agreementsTable()

        page.pagination()
```

---

# Table Component Hierarchy

The agreements table is validated through reusable infrastructure components.

```text
Table
 ├─ TableHead
 │   └─ TableRow
 │       └─ TableCell
 └─ TableBody
     └─ TableRow
         └─ TableCell
```

Header access:

```java
page.agreementsTable()
        .tHead()
        .rows()
```

Body access:

```java
page.agreementsTable()
        .tBody()
        .rows()
```
