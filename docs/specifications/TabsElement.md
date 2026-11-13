# Tabs

`TabsElement` wraps `<vaadin-tabs>` (Flow: `Tabs`), the tab strip, and
`TabElement` wraps a single `<vaadin-tab>` (Flow: `Tab`). They let a test list
the tabs, select one by label or index, and assert the selection, the labels,
the count, the orientation and whether a tab is enabled. The tab strip of a
`TabSheet` is a `<vaadin-tabs>` too; see [Tab Sheet](TabSheetElement.md).

| | |
|---|---|
| Web component | `<vaadin-tabs>`, `<vaadin-tab>` |
| Flow component | `com.vaadin.flow.component.tabs.Tabs`, `Tab` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TabsElement`, `TabElement` |
| Typed query | `find(page, TabsElement.class)` → `ComponentQuery`; `find(page, TabElement.class)` → `TextQuery` |
| API | [TabsElement](../../skills/vaadin-playwright-test/api-reference.md#tabselement), [TabElement](../../skills/vaadin-playwright-test/api-reference.md#tabelement) |

## Finding the component

```java
TabsElement tabs = TabsElement.get(page);                    // first tab strip on the page
TabsElement inPanel = TabsElement.get(panel.getLocator());   // first inside a container
TabsElement order = TabsElement.getById(page, "order-tabs");
TabsElement byTestId = TabsElement.getByTestId(page, "order-tabs");
TabsElement scoped = TabsElement.getById(card.getLocator(), "order-tabs");

// Typed query: generic filters only
TabsElement styled = find(page, TabsElement.class).withClassName("order-tabs").single();
```

Tabs are found through the strip, or directly:

```java
TabElement payment = tabs.getTab("Payment");   // by label, exact
TabElement first = tabs.getTab(0);             // by zero-based index
TabElement selected = tabs.getSelectedTab();

// Static factories, scoped to a locator that contains the <vaadin-tabs>
TabElement shipping = TabElement.getTabByText(page.locator("#order-area"), "Shipping");
TabElement current = TabElement.getSelectedTab(page.locator("#order-area"));
TabElement byId = TabElement.getById(page, "payment-tab");
TabElement byTestId = TabElement.getByTestId(tabs.getLocator(), "payment-tab");

// Typed query: TextQuery filters on the tab's own text
TabElement home = find(page, TabElement.class).withText("Home").single();
TabElement anyHome = tabs.find(TabElement.class).withTextContaining("home").first();
```

`getTab(String)`, `getTabByText` and `withText` match the **whole** label,
case-sensitively: `"Tab 1"` never resolves to `"Tab 10"`, and two tabs with the
same label fail with a strict-mode error. `withTextContaining` is the
case-insensitive substring variant.

## Selecting tabs

```java
// View
Tabs tabs = new Tabs(new Tab("Details"), new Tab("Payment"), new Tab("Shipping"));
tabs.setId("order-tabs");
tabs.addSelectedChangeListener(event -> showSelectedPage(event.getSelectedTab()));
```

```java
// Test
TabsElement tabs = TabsElement.getById(page, "order-tabs");
tabs.assertSelectedTab("Details");

tabs.selectTab("Payment");
tabs.assertSelectedTab("Payment");
tabs.getTab("Details").assertNotSelected();
assertThat(page.locator("#order-content > div:not([hidden])")).hasText("This is the payment page");

tabs.selectTab(2);
tabs.assertSelectedTab(2);
tabs.assertSelectedTab("Shipping");
```

`selectTab` clicks the tab and waits for the server round-trip, so the
selected-change listener has run when it returns. A `TabElement` selects itself
too:

```java
TabElement shipping = tabs.getTab("Shipping");
shipping.select();
shipping.assertSelected();
```

To read the selection, `getSelectedTab()` returns the selected tab (the first
`vaadin-tab[selected]`) and `getSelectedIndex()` reads the component's
`selected` property, `-1` when nothing is selected:

```java
assertEquals("Payment", tabs.getSelectedTab().getLabel());
int index = tabs.getSelectedIndex();
boolean isSelected = tabs.getTab("Payment").isSelected();
```

## Tab labels and count

```java
tabs.assertTabCount(3);
tabs.assertTabLabels("Details", "Payment", "Shipping");   // exact, in order, retried

List<String> labels = tabs.getTabLabels();                 // read once
int count = tabs.getTabCount();
String label = tabs.getTab(0).getLabel();

assertThat(tabs.getTabs()).hasText(new String[] {"Details", "Payment", "Shipping"});
```

A tab's **label** is what `getLabel()`, `getTabLabels()` and `getTab(String)`
agree on:

- the tab's own text, with whitespace collapsed and trimmed;
- children placed in a named slot (a tooltip, a suffix) are left out;
- a tab with no text, such as an icon-only tab, reads as its `aria-label`;
- a tab with both text and `aria-label` is labelled by its **text**.

```java
// View
Tab inbox = new Tab("Inbox");
inbox.setAriaLabel("Inbox, 3 unread");
Tab settings = new Tab(VaadinIcon.COG.create());
settings.setAriaLabel("Settings");
Tabs tabs = new Tabs(inbox, new Tab("Archive"), settings);
tabs.setId("labelled-tabs");
```

```java
// Test
TabsElement tabs = TabsElement.getById(page, "labelled-tabs");
tabs.assertTabLabels("Inbox", "Archive", "Settings");
tabs.selectTab("Settings");                 // icon-only tab, found by its aria-label
tabs.assertSelectedTab(2);
assertThat(tabs.getTab("Inbox, 3 unread").getLocator()).hasCount(0); // the text wins
```

Every label `getTabLabels()` returns selects its tab through `getTab(label)`.

## Disabled tabs

`TabElement` implements `HasEnabledElement`: a disabled tab sets
`aria-disabled` on its `tab` role, which Playwright understands.

```java
// View
Tab billing = new Tab("Billing");
billing.setEnabled(false);
Tabs tabs = new Tabs(new Tab("Profile"), billing, new Tab("Security"));
tabs.setId("account-tabs");
Button enable = new Button("Enable billing", e -> billing.setEnabled(true));
```

```java
// Test
TabsElement tabs = TabsElement.getById(page, "account-tabs");
tabs.getTab("Billing").assertDisabled();
tabs.getTab("Profile").assertEnabled();

assertThrows(IllegalStateException.class, () -> tabs.selectTab("Billing"));
tabs.assertSelectedTab("Profile");

ButtonElement.getByText(page, "Enable billing").click();
tabs.getTab("Billing").assertEnabled();
tabs.selectTab("Billing");
tabs.assertSelectedTab("Billing");
```

`select()` / `selectTab(…)` on a disabled tab throw `IllegalStateException`
(naming the tab) **at once** — the check is made once before clicking and not
retried, so the test fails immediately instead of running into Playwright's
action timeout. If the tab is enabled asynchronously, wait with
`assertEnabled()` first, as above.

## Orientation

```java
// View
Tabs tabs = new Tabs(new Tab("North"), new Tab("South"));
tabs.setOrientation(Tabs.Orientation.VERTICAL);
```

```java
// Test
TabsElement vertical = TabsElement.getById(page, "direction-tabs");
vertical.assertOrientation(TabsElement.ORIENTATION_VERTICAL);
TabsElement.getById(page, "order-tabs").assertOrientation(TabsElement.ORIENTATION_HORIZONTAL);

String orientation = vertical.getOrientation(); // "horizontal" when the attribute is absent
```

`assertOrientation(null)` asserts that no `orientation` attribute is set. The
web component reflects its orientation, so a default strip carries
`orientation="horizontal"` and does not pass `assertOrientation(null)`.

## Theme variants and CSS classes

```java
// View
tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
tabs.addClassName("order-tabs");
```

```java
// Test
tabs.assertTheme("minimal");
tabs.assertHasThemeVariant("minimal");
tabs.assertCssClass("order-tabs");
```

## Pitfalls

- `TabElement.getTabByText(Locator, …)` and `TabElement.getSelectedTab(Locator)`
  look for a `<vaadin-tabs>` **inside** the scope: pass a container of the strip,
  not the strip's own locator (for that, use `tabs.getTab(…)` /
  `tabs.getSelectedTab()`). `TabElement.getSelectedTab(Locator)` applies no
  `.first()`, so a scope holding several strips fails with a strict-mode error.
- `getTabs()`, `getTabCount()` and the count/label assertions see every
  `<vaadin-tab>` inside the strip; selecting a tab does not change which tabs
  exist, so `assertTabCount` reflects tabs added or removed on the server.
- `isSelected()`, `getSelectedIndex()` and `getTabLabels()` read once. Use the
  `assert…` variants right after an action.

## See also

- [Tab Sheet](TabSheetElement.md) — tabs with their content panels
- [Accordion](AccordionElement.md), [Side Navigation](SideNavigationElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [TabsViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TabsViewIT.java)
