# Finding components

Find a component the way a user finds it: by the label next to it, the text on
it, or the heading of the dialog it lives in. Fall back to ids and test ids only
for components without a user-visible name.

## By visible label

`getByLabel` matches the **visible label** — the text the component shows in
its own label slot (`new TextField("Name")`, `setLabel("Name")`):

```java
TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
ComboBoxElement country = ComboBoxElement.getByLabel(page, "Country");
CheckboxElement terms = CheckboxElement.getByLabel(page, "I accept the terms");
RadioButtonGroupElement plan = RadioButtonGroupElement.getByLabel(page, "Plan");
```

The match is **exact**: the whole label, case-sensitively. A partial label finds
nothing, and two components with the same label raise a Playwright strict-mode
error instead of silently picking the first one:

```java
TextFieldElement.getByLabel(page, "name");      // no match: case differs
TextFieldElement.getByLabel(page, "First");     // no match: "First name" is longer
```

Use `getByLabelContaining` for a long or generated label. It matches a
case-insensitive substring — and still fails if several components match:

```java
TextAreaElement notes = TextAreaElement.getByLabelContaining(page, "additional notes");
```

!!! note "A label is not an `aria-label` or a placeholder"

    `getByLabel` never matches `setAriaLabel("Search")` or
    `setPlaceholder("Street")`. Use the typed query below:
    `find(page, TextFieldElement.class).withAccessibleName("Search")` or
    `.withPlaceholder("Street")`. Components that have no visible label (menu
    bar, list box, popover, breadcrumbs) are found by accessible name the same
    way.

## By text

Components identified by what they show use `getByText`:

```java
ButtonElement save = ButtonElement.getByText(page, "Save");
ButtonElement close = ButtonElement.getByText(page, "Close"); // also an icon button with aria-label "Close"
ButtonElement anySave = ButtonElement.getByTextContaining(page, "save"); // "Save draft", "Save"... must be unique
BreadcrumbsItemElement home = BreadcrumbsItemElement.getByText(page, "Home");
```

Other components have a factory named after their caption:

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "Delete user?");
ConfirmDialogElement confirm = ConfirmDialogElement.getByHeaderText(page, "Discard changes?");
DetailsElement shipping = DetailsElement.getBySummaryText(page, "Shipping address");
CardElement card = CardElement.getByTitle(page, "Monthly revenue");
AvatarElement avatar = AvatarElement.getByName(page, "Ada Lovelace");
UploadElement upload = UploadElement.getByButtonText(page, "Upload invoice");
```

A few lookups deliberately match **free-form content** as a case-insensitive
substring and take the first match: `NotificationElement.getByText`,
`BadgeElement.getByText`, `TooltipElement.getByText` and
`VirtualListElement.getItemByText`. Their content is not a label, so a fragment
is the natural key:

```java
NotificationElement.getByText(page, "saved").assertOpen(); // "Contact saved successfully"
```

## The only one on the page

Containers that usually appear once have `get`:

```java
GridElement grid = GridElement.get(page);
AppLayoutElement layout = AppLayoutElement.get(page);
TabsElement tabs = TabsElement.get(page);
```

`get` returns the first match. When the page has several, scope the lookup
(below) or use an id.

## By id or test id

Every element has `getById` and `getByTestId`. They match exactly and are scoped
to the element's tag:

```java
// View
grid.setId("orders");
saveButton.setTestId("save-order"); // renders data-testid="save-order"
```

```java
GridElement orders = GridElement.getById(page, "orders");
ButtonElement save = ButtonElement.getByTestId(page, "save-order");
```

Prefer them for components with no user-visible name — a grid, a layout, an
icon without a label — or to tell apart components that look identical to the
user.

## Inside a container

Every factory has a `Locator` overload, and every element has `getLocator()`,
so lookups compose:

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "New contact");
TextFieldElement name = TextFieldElement.getByLabel(dialog.getLocator(), "Name");
ButtonElement.getByText(dialog.getLocator(), "Save").click();

FormLayoutElement billing = FormLayoutElement.getById(page, "billing");
TextFieldElement billingStreet = TextFieldElement.getByLabel(billing.getLocator(), "Street");
```

## Items inside a container

Options, tabs, menu items, side-nav items, accordion panels and checkboxes of a
group are **items**: you get them through their container, by their whole
label.

```java
TabsElement.get(page).selectTab("Details");
SelectElement.getByLabel(page, "Size").selectItem("Large");
CheckboxGroupElement.getByLabel(page, "Toppings").selectByLabel("Cheese");
SideNavigationElement.getByLabel(page, "Main menu").clickItem("Orders");
MenuBarElement.getById(page, "actions").openSubMenu("File").getMenuItemElement("Export").click();
```

Item lookups match the whole label, case-sensitively, with no first-match
fallback: `selectByLabel("Option 1")` never picks `"Option 10"`, and a genuine
duplicate fails with a strict-mode error.

## The typed query builder

`ElementQueries.find` gives every element the same grammar. Start with the
element class, add filters, finish with a terminal:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

TextFieldElement name = find(page, TextFieldElement.class).withLabel("Name").single();
TextFieldElement search = find(page, TextFieldElement.class).withAccessibleName("Search").single();
TextFieldElement street = find(page, TextFieldElement.class).withPlaceholder("Street").single();
ButtonElement save = find(page, ButtonElement.class).withText("Save").single();
```

The filters available depend on the component, and a filter that cannot apply
does not compile:

```java
find(page, ButtonElement.class).withLabel("Save"); // compile error: buttons have text, not a label
```

| Filter | Available on |
|---|---|
| `withLabel`, `withLabelContaining` | fields with a visible label (`InputFieldQuery`, `FieldQuery`, `SelectQuery`...) |
| `withPlaceholder` | input fields, select, date-time picker |
| `withText`, `withTextContaining` | buttons, badges, tabs, tooltips, menu items... (`ButtonQuery`, `TextQuery`, `TooltipQuery`) |
| `withAccessibleName`, `withAccessibleNameContaining` | every element |
| `withId`, `withTestId`, `withClassName`, `withAttribute` | every element |

Filters combine, and each returns a new query:

```java
find(page, ButtonElement.class).withText("Delete").withClassName("danger").single();
find(page, TextFieldElement.class).withLabelContaining("name").withTestId("customer-name").single();
```

Search inside a component with `element.find(..)`, or pass a locator:

```java
DialogElement dialog = find(page, DialogElement.class).withAccessibleName("Delete account?").single();
dialog.find(ButtonElement.class).withText("Delete").single().click();

List<CheckboxElement> formCheckboxes = find(page.locator("#form"), CheckboxElement.class).all();
```

Terminals:

| Terminal | Returns |
|---|---|
| `single()` | the one match; acting on it fails with a strict-mode error if several match |
| `first()`, `get(int)` | a match by DOM position, without checking for others |
| `all()` | every element matching right now |
| `count()`, `exists()` | read immediately, without waiting |
| `getLocator()` | the locator over every match, e.g. `assertThat(q.getLocator()).hasCount(2)` |

```java
var checkboxes = find(page, CheckboxElement.class);
assertThat(checkboxes.getLocator()).hasCount(2);
checkboxes.get(1).assertLabel("Subscribe");
```

A subclass shares its parent's query: `find(page, EmailFieldElement.class)`
returns `EmailFieldElement` instances, and `find(page, TreeGridElement.class)`
returns tree grids typed as `GridElement`.

## Which lookup to use

1. The factory named after what the user sees: `getByLabel`, `getByText`,
   `getByHeaderText`...
2. An item through its container: `tabs.getTab("Details")`.
3. `find(..)` for an accessible name, a placeholder, combined filters or a
   scoped search.
4. `getById` / `getByTestId` for components with no user-visible name.
5. A raw Playwright locator wrapped in an element: `new GridElement(page.locator(".orders vaadin-grid"))`.

## Let the page tell you

Unsure what label a component really has? Write a throwaway test that dumps the
view:

```java
@Test
void outline() {
    ViewOutline.write(page); // org.vaadin.addons.dramafinder.agent
}
```

It writes `target/agent-report/view-outline.txt` with every Vaadin component on
the page and a lookup that reaches it, verified against the live page:

```
vaadin-text-field "Name" = "ada" (required)
    TextFieldElement.getByLabel(page, "Name")
vaadin-tabs #order-tabs
    TabsElement.getById(page, "order-tabs")
    items (full label, case-sensitive): "Details", "Payment"
```
