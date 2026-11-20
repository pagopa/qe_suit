# Configurable text in Cucumber DataTables

For DTO properties declared as `ConfigurableText`, a normal column is randomized by default:

```gherkin
| alias | nome      | tecnologia |
| TE-1  | Anagrafe  | REST       |
```

To preserve the value verbatim in the application command, add `(letterale)` to the column header:

```gherkin
| alias | nome (letterale) | tecnologia |
| TE-1  | Anagrafe         | REST       |
```

`nome (casuale)` explicitly selects the default randomized behavior. The available
words are centralized in `ConfigurableTextHeaderSyntax.MODES`; the parenthesized
header syntax is implemented by that class. Add an alias there to change the
vocabulary, not in DTOs or mapper classes.

The opt-in is the Java type `ConfigurableText`, so ordinary `String` fields remain
unchanged. Jackson's `@JsonAlias("nome")` still provides the canonical Gherkin
label; do not add `nome (letterale)` to `@JsonAlias`.

The `DataTableMapper` binds a `ConfigurableText(value, mode)`. The command mapper
resolves that object to a `String` through `SharedMapper.resolveConfigurableText`.
Use `@Valid` on the DTO property so `@NotBlank` validates its nested `value`.
An optional field may be omitted; if present, its value cannot be blank.

Multiple column headers targeting the same DTO property, unknown modifiers on
configurable fields, and modifiers on ordinary fields are rejected. In particular,
`nome` and `nome (letterale)` must not appear together in one DataTable row.

No step, Journey, factory or application command changes are necessary.
