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

Declare a field's total length limit on its DTO, not in feature headers:

```java
@Valid
@ConfigurableTextSize(max = 12)
@JsonAlias("suffisso")
private ConfigurableText instanceLabel;
```

The DataTable normalizer transfers this limit to the value resolver:

```gherkin
| alias | template | suffisso |
| EI-1  | TE-1     | Milano   |
```

This produces `Milano-` followed by five random alphanumeric characters, for a
total of 12 characters. The limit must be a positive integer and leave room for
the complete prefix, a hyphen and at least one random character; otherwise
validation fails explicitly. Literal values are preserved but must also fit
within the annotated limit. Without an annotation, the existing 15-character
random suffix is unchanged. The only supported modifiers are `(casuale)` and
`(letterale)`; length modifiers in feature headers are rejected.
Use `(letterale)` when a scenario requires an exact match.

`@Size` and `@Max` cannot be applied directly to `ConfigurableText`; use
`@ConfigurableTextSize` together with `@Valid` for nested validation.

The opt-in is the Java type `ConfigurableText`, so ordinary `String` fields remain
unchanged. Jackson's `@JsonAlias("nome")` still provides the canonical Gherkin
label; do not add `nome (letterale)` to `@JsonAlias`.

The `DataTableMapper` binds a `ConfigurableText(value, mode, maxLength)`. The command mapper
resolves that object to a `String` through `SharedMapper.resolveConfigurableText`.
Use `@Valid` on the DTO property so `@NotBlank` validates its nested `value`.
An optional field may be omitted; if present, its value cannot be blank.

Multiple column headers targeting the same DTO property, unknown modifiers on
configurable fields, and modifiers on ordinary fields are rejected. In particular,
`nome` and `nome (letterale)` must not appear together in one DataTable row.

No step, Journey, factory or application command changes are necessary.
