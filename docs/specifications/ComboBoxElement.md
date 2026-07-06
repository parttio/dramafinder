# ComboBoxElement

> Full API (methods, signatures, descriptions): see [api-reference.md](../../skills/vaadin-playwright-test/api-reference.md). This note covers only behaviour that isn't obvious from the signatures.

## Lazy data providers behave identically

ComboBox supports both in-memory and lazy-loaded (server-side) data providers. The element API works the same way regardless of the loading strategy — `filterAndSelectItem` and `setFilter` narrow results from the server exactly as they do for in-memory data.

## Filter-then-select pattern

`filterAndSelectItem` types filter text into the input (triggering server/in-memory filtering) and then clicks the matching item in one step. Use `setFilter` + `assertItemCount` when you want to verify how many items a filter narrows to before selecting.

```java
// Filter and select in one step
comboBox.filterAndSelectItem("Apr", "Apricot");
comboBox.assertValue("Apricot");

// Or filter manually and inspect the narrowed list
comboBox.setFilter("Ban");
comboBox.assertItemCount(1);
comboBox.close();
```
