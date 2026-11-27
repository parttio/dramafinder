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
find(page, ButtonElement.class).withAccessibleName("Save").single().click();
// The listener has run and the response has arrived
assertThat(page.locator("#status")).hasText("Saved");
```

This covers the actions that click, fill, press, check, select, hover,
focus/blur, upload files or set a DOM property: the server half of the exchange.
What the browser does **after** the response — an overlay animating open, a
grid fetching and rendering rows — is client-side, so check it with a
[retrying assertion](assertions.md):

```java
find(page, ButtonElement.class).withAccessibleName("Delete").single().click();
ConfirmDialogElement.get(page).assertOpen();  // retries while the overlay opens
```

The wait is one call to the browser. If Flow is still busy after 30 seconds,
it fails with a Playwright `TimeoutError`; the wait runs inside the page, so
`page.setDefaultTimeout(..)` does not change that limit.

## Observing an in-flight request

Sometimes the state *during* the request is the point of the test: a loading
indicator, a disabled button, a progress bar. Run the action without waiting:

```java
import org.vaadin.addons.dramafinder.AutoWait;

ButtonElement export = find(page, ButtonElement.class).withAccessibleName("Export").single();
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
find(page, TextFieldElement.class).withLabel("Name").single().setValue("Ada");          // types, fires change
find(page, ComboBoxElement.class).withLabel("Country").single().setValue("Finland");   // filters, picks the item labelled "Finland"
find(page, DatePickerElement.class).withLabel("Birthday").single().setValue("12/10/1815"); // types in the field's locale format
find(page, DatePickerElement.class).withLabel("Birthday").single().setValue(LocalDate.of(1815, 12, 10)); // typed overload
find(page, CheckboxElement.class).withLabel("Subscribe").single().check();
find(page, RadioButtonGroupElement.class).withLabel("Plan").single().selectByLabel("Pro");
find(page, SliderElement.class).withLabel("Volume").single().setValue(40);
```

Clear a field with `clear()`, not `setValue("")`: a combo box would look for an
item labelled `""`.

```java
find(page, ComboBoxElement.class).withLabel("Country").single().clear();
```

The component pages document how each field reads and writes its value, e.g.
[Date Picker](../specifications/DatePickerElement.md) for locale formats.

## Clicking

`click()` is available on every element and clicks the component host. Many
elements offer a more precise action — prefer it:

```java
find(page, ButtonElement.class).withAccessibleName("Save").single().click();
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
TextFieldElement search = find(page, TextFieldElement.class).withLabel("Search").single();
search.getInputLocator().press("Enter");
search.waitForVaadinIdle();
GridElement.get(page).assertRowCount(3);
```

## Navigation waits too

`navigate(View.class)` waits for Vaadin to finish loading the view; the base
classes do the same for the view of each test. See
[Navigation](navigation.md).

The same holds when an action loads a new page (a login form posting to Spring
Security, a link out of the view, `Page.setLocation` on the server): after the
load, the wait first lets Flow start the view's UI — Flow registers that UI's
client only after the page's `load` event — and then waits for it to go idle,
so the next read sees the new view rather than an empty page. A page without
Flow is idle at once; a page that runs Flow but shows no Flow UI (a Hilla view)
waits at most 3 seconds for it, once per page load.

To see such a load, the page has to be listening for it before the action
starts it. The first wait on a page sets that up for every document the page
loads afterwards; the base classes wait right after opening each test's view.
With your own setup, call `AutoWait.waitForVaadinIdle(page)` once after
`page.navigate(..)`, as in [Getting started](getting-started.md).

When an action's listener navigates on the server (`ui.navigate(..)`), Flow
updates the address bar only after it has handled the response. The action's
wait covers that too — in React mode, Vaadin's default — so a one-shot read of
the location right after it sees the new view's path:

```java
find(page, ButtonElement.class).withAccessibleName("Open profile").single().click();   // ui.navigate(ProfileView.class)
assertEquals("http://localhost:8080/profile", page.url());
```

It waits at most two seconds for the address bar, so a navigation that a
`BeforeLeaveEvent` postponed does not hold up every action.

## No sleeps

If a test only passes with a `Thread.sleep`, something client-side is still
changing after the server answered. Replace the sleep with the assertion of the
state you were waiting for:

```java
// ❌
find(page, ButtonElement.class).withAccessibleName("Load").single().click();
Thread.sleep(2000);
assertEquals(50, grid.getTotalRowCount());

// ✅
find(page, ButtonElement.class).withAccessibleName("Load").single().click();
grid.assertRowCount(50);
```
