---
name: javafx-final-properties
description: Enforce the repository rule that JavaFX observable property methods must be final.
when_to_use: Use when adding or modifying JavaFX properties in controls, views, models, or other classes with property accessors like fooProperty(), getFoo(), isFoo(), and setFoo().
paths:
  - "**/*.java"
---

When creating observable JavaFX properties in this repository, make all methods related to those properties `final`.

## Rule

For each JavaFX property, declare the related public or protected methods as `final`:

- `fooProperty()`
- `getFoo()` or `isFoo()`
- `setFoo(...)`

Example:

```java
private final StringProperty name = new SimpleStringProperty(this, "name", "");

public final StringProperty nameProperty() {
    return name;
}

public final String getName() {
    return name.get();
}

public final void setName(String value) {
    name.set(value);
}
```

## Notes

- Apply this consistently to controls, views, and observable model classes.
- Constructors are not part of this rule.
- `@Override` methods are not property methods and do not need to be changed for this rule.
- Follow the repository's existing documentation convention: public/protected methods use Javadocs, private methods use regular comments only when needed.
