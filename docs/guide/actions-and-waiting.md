# Actions and waiting

A Vaadin Flow application runs on the server: a click in the browser sends a
request, a listener runs on the server, and the response updates the page.
DramaFinder makes every element action wait for that round-trip, so tests
never need a `Thread.sleep`.

## Every action waits for the server

When `click()`, `setValue(..)`, `check()`, `selectItem(..)`, `selectTab(..)`,
`uploadFiles(..)`... return, the request they triggered has been answered:

```java
// View
Button save = new Button("Save", e -> {
    service.save(contact);           // takes 800 ms
    status.setText("Saved");
});
```

```java
ButtonElement.getByText(page, "Save").click();
// The listener has run and the response has arrived
assertThat(page.locator("#status")).hasText("Saved");
```

This covers the actions that click, fill, press, check, select, hover,
focus/blur, upload files or set a DOM property: the server half of the exchange.
What the browser does **after** the response — an overlay animating open, a
grid fetching and rendering rows — is client-side, so check it with a
[retrying assertion](assertions.md):

```java
ButtonElement.getByText(page, "Delete").click();
ConfirmDialogElement.get(page).assertOpen();  // retries while the overlay opens
```

## Observing an in-flight request

Sometimes the state *during* the request is the point of the test: a loading
indicator, a disabled button, a progress bar. Run the action without waiting:

```java
import org.vaadin.addons.dramafinder.AutoWait;

ButtonElement export = ButtonElement.getByText(page, "Export");
AutoWait.withoutWaiting(export::click);

export.assertDisabled();                           // while the server works
ProgressBarElement.getById(page, "export-progress").assertVisible();

export.waitForVaadinIdle();                        // now wait explicitly
export.assertEnabled();
```

`withoutWaiting` also takes a `Supplier` and returns its value. Inside it,
`waitForVaadinIdle()` (on any element) or `AutoWait.waitForVaadinIdle(page)`
still waits.

To switch automatic waiting off for a whole run, pass
`-Ddramafinder.autoWait=false`.

## Setting values like a user

Each field's `setValue` behaves like a user filling it in, and the component
decides what the value becomes:

```java
TextFieldElement.getByLabel(page, "Name").setValue("Ada");          // types, fires change
ComboBoxElement.getByLabel(page, "Country").setValue("Finland");   // filters, picks the item labelled "Finland"
DatePickerElement.getByLabel(page, "Birthday").setValue("12/10/1815"); // types in the field's locale format
DatePickerElement.getByLabel(page, "Birthday").setValue(LocalDate.of(1815, 12, 10)); // typed overload
CheckboxElement.getByLabel(page, "Subscribe").check();
RadioButtonGroupElement.getByLabel(page, "Plan").selectByLabel("Pro");
SliderElement.getByLabel(page, "Volume").setValue(40);
```

Clear a field with `clear()`, not `setValue("")`: a combo box would look for an
item labelled `""`.

```java
ComboBoxElement.getByLabel(page, "Country").clear();
```

The component pages document how each field reads and writes its value, e.g.
[Date Picker](../specifications/DatePickerElement.md) for locale formats.

## Clicking

`click()` is available on every element and clicks the component host. Many
elements offer a more precise action — prefer it:

```java
ButtonElement.getByText(page, "Save").click();
GridElement.get(page).findRow(3).get().click();          // fires the grid's item click
GridElement.get(page).findRow(3).get().doubleClick();
TabsElement.get(page).selectTab("Payment");
DetailsElement.getBySummaryText(page, "Advanced").setOpen(true);
ConfirmDialogElement.get(page).confirm();
```

## Keyboard and other raw actions

For anything not wrapped, use Playwright on the element's locators. Use
`waitForVaadinIdle()` afterwards if the raw action talks to the server — raw
Playwright calls do not wait:

```java
TextFieldElement search = TextFieldElement.getByLabel(page, "Search");
search.getInputLocator().press("Enter");
search.waitForVaadinIdle();
GridElement.get(page).assertRowCount(3);
```

## Navigation waits too

`navigate(View.class)` waits for Vaadin to finish loading the view; the base
classes do the same for the view of each test. See
[Navigation](navigation.md).

## No sleeps

If a test only passes with a `Thread.sleep`, something client-side is still
changing after the server answered. Replace the sleep with the assertion of the
state you were waiting for:

```java
// ❌
ButtonElement.getByText(page, "Load").click();
Thread.sleep(2000);
assertEquals(50, grid.getTotalRowCount());

// ✅
ButtonElement.getByText(page, "Load").click();
grid.assertRowCount(50);
```
