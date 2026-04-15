---
name: javafx-property-javadocs
description: Enforce the repository rule that JavaFX property Javadocs belong only on the property accessor method.
when_to_use: Use when adding or modifying JavaFX observable properties and their related methods such as colorProperty(), getColor(), isMuted(), and setColor(...).
paths:
  - "**/*.java"
---

When documenting JavaFX observable properties in this repository, add Javadocs only to the property accessor method such as `colorProperty()`.

## Rule

For a JavaFX property:

- add Javadocs to the property accessor method, for example `colorProperty()`
- do not add Javadocs to the getter method such as `getColor()` or `isColor()`
- do not add Javadocs to the setter method such as `setColor(...)`
- do not add Javadocs to the property field declaration itself

Example:

```java
private final StringProperty color = new SimpleStringProperty(this, "color", "");

/**
 * The color property.
 *
 * @return the color property
 */
public final StringProperty colorProperty() {
    return color;
}

public final String getColor() {
    return color.get();
}

public final void setColor(String value) {
    color.set(value);
}
```

## Notes

- Apply this consistently to controls, views, and observable model classes.
- This rule is specific to JavaFX property triplets and does not change how unrelated public/protected methods are documented.
- Combine this with the repository rule that JavaFX property-related methods are `final`.
