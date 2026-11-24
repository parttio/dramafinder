# Finding components

Find a component the way a user finds it: by the label next to it, the text on
it, or the heading of the dialog it lives in. Fall back to ids and test ids only
for components without a user-visible name.

The recommended way to find a component is the typed query builder,
`ElementQueries.find`: start with the element class, add a filter, and finish
with `single()`. Add the static import once per test class:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;
```

The element classes also have static factories such as
`TextFieldElement.getByLabel(page, "Name")`. They are shortcuts for the same
queries — `getByLabel(page, label)` is exactly
`find(page, X.class).withLabel(label).single()` — and keep working, but `find`
gives every component the same grammar, combines filters and scopes a search,
so the examples in this documentation use it.

## By visible label

`withLabel` matches the **visible label** — the text the component shows in
its own label slot (`new TextField("Name")`, `setLabel("Name")`):

```java
TextFieldElement name = find(page, TextFieldElement.class).withLabel("Name").single();
ComboBoxElement country = find(page, ComboBoxElement.class).withLabel("Country").single();
CheckboxElement terms = find(page, CheckboxElement.class).withLabel("I accept the terms").single();
RadioButtonGroupElement plan = find(page, RadioButtonGroupElement.class).withLabel("Plan").single();
```

A field added to a `FormLayout` with an item label
(`formLayout.addFormItem(new TextField(), "Name")`) has no label of its own; it is
found by the form item's label, the caption shown next to it. A field that does
have its own label is found by that one only.

The match is **exact**: the whole label, case-sensitively. A partial label finds
nothing, and two components with the same label raise a Playwright strict-mode
error instead of silently picking the first one:

```java
find(page, TextFieldElement.class).withLabel("name").single();      // no match: case differs
find(page, TextFieldElement.class).withLabel("First").single();     // no match: "First name" is longer
```

Use `withLabelContaining` for a long or generated label. It matches a
case-insensitive substring — and still fails if several components match:

```java
TextAreaElement notes = find(page, TextAreaElement.class).withLabelContaining("additional notes").single();
```

!!! note "A label is not an `aria-label` or a placeholder"

    `withLabel` never matches `setAriaLabel("Search")` or
    `setPlaceholder("Street")`. Use another filter:
    `find(page, TextFieldElement.class).withAccessibleName("Search")` or
    `.withPlaceholder("Street")`. Components that have no visible label (menu
    bar, list box, popover, breadcrumbs) are found by accessible name the same
    way.

## By text

Components identified by what they show use a text filter. On a button,
`withAccessibleName` matches its text, or the `aria-label` of an icon-only
button, as the `ButtonElement.getByText` shortcut does:

```java
ButtonElement save = find(page, ButtonElement.class).withAccessibleName("Save").single();
ButtonElement close = find(page, ButtonElement.class).withAccessibleName("Close").single(); // also an icon button with aria-label "Close"
ButtonElement anySave = find(page, ButtonElement.class).withAccessibleNameContaining("save").single(); // "Save draft", "Save"... must be unique
BreadcrumbsItemElement home = find(page, BreadcrumbsItemElement.class).withText("Home").single();
```

A dialog's header is its accessible name, and an avatar's name is an
attribute, so the same grammar finds them:

```java
DialogElement dialog = find(page, DialogElement.class).withAccessibleName("Delete user?").single();
ConfirmDialogElement confirm = find(page, ConfirmDialogElement.class).withAccessibleName("Discard changes?").single();
AvatarElement avatar = find(page, AvatarElement.class).withAttribute("name", "Ada Lovelace").single();
```

`DialogElement.getByHeaderText` and `ConfirmDialogElement.getByHeaderText` are
shortcuts for the first two. A few components are found by a caption that no
filter covers, so they keep a factory named after it:

```java
DetailsElement shipping = DetailsElement.getBySummaryText(page, "Shipping address");
CardElement card = CardElement.getByTitle(page, "Monthly revenue");
UploadElement upload = UploadElement.getByButtonText(page, "Upload invoice");
```

A notification's content is free-form text, not a label, so a fragment is the
natural key. `withTextContaining` matches a case-insensitive substring of it:

```java
find(page, NotificationElement.class).withTextContaining("saved").single().assertOpen(); // "Contact saved successfully"
```

Use `withText` for the whole text. A few factories also match free-form content
as a case-insensitive substring, but take the **first** match instead of
failing on several: `BadgeElement.getByText`, `TooltipElement.getByText` and
`VirtualListElement.getItemByText`.

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

Every element has `find(..)`, which searches inside it, and `find` also takes a
`Locator`, so lookups compose:

```java
DialogElement dialog = find(page, DialogElement.class).withAccessibleName("New contact").single();
TextFieldElement name = dialog.find(TextFieldElement.class).withLabel("Name").single();
dialog.find(ButtonElement.class).withAccessibleName("Save").single().click();

FormLayoutElement billing = FormLayoutElement.getById(page, "billing");
TextFieldElement billingStreet = billing.find(TextFieldElement.class).withLabel("Street").single();
```

## Items inside a container

Options, tabs, menu items, side-nav items, accordion panels and checkboxes of a
group are **items**: you get them through their container, by their whole
label.

```java
TabsElement.get(page).selectTab("Details");
find(page, SelectElement.class).withLabel("Size").single().selectItem("Large");
find(page, CheckboxGroupElement.class).withLabel("Toppings").single().selectByLabel("Cheese");
find(page, SideNavigationElement.class).withLabel("Main menu").single().clickItem("Orders");
MenuBarElement.getById(page, "actions").openSubMenu("File").getMenuItemElement("Export").click();
```

Item lookups match the whole label, case-sensitively, with no first-match
fallback: `selectByLabel("Option 1")` never picks `"Option 10"`, and a genuine
duplicate fails with a strict-mode error.

## The typed query builder in detail

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
| `withPlaceholder`, `withPlaceholderContaining` | input fields, select, date-time picker |
| `withText`, `withTextContaining` | buttons, badges, tabs, tooltips, menu items... (`ButtonQuery`, `TextQuery`, `TooltipQuery`) |
| `withAccessibleName`, `withAccessibleNameContaining` | every element |
| `withId`, `withTestId`, `withClassName`, `withAttribute`, `withTheme` | every element |

Every element reads back the name `withAccessibleName` matches with
`getAccessibleName()`, and asserts it with `assertAccessibleName(..)` (see
[VaadinElement](../specifications/VaadinElement.md#accessible-name)).

Filters combine, and each returns a new query:

```java
find(page, ButtonElement.class).withText("Delete").withClassName("danger").single();
find(page, TextFieldElement.class).withLabelContaining("name").withTestId("customer-name").single();
```

`withTheme("primary")` keeps components whose `theme` attribute holds that
variant as a whole word, whatever other variants they have.

`withId`, `withClassName`, `withAttribute`, `withTheme` and `withPlaceholder` /
`withPlaceholderContaining` (on fields that keep the placeholder on their host)
are plain CSS. Placed before any other
filter, they compile into one selector, e.g. `vaadin-grid[id="users"][class~="compact"]`,
which Playwright resolves on every use of the element without scanning the whole
page. After any other filter they still work, as an intersection. `withTestId`
uses Playwright's `getByTestId`, so it follows a test id attribute changed with
`Selectors.setTestIdAttribute`.

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

### Light DOM only, by default

A query searches the **light DOM**: an element inside a shadow root is left
out. Vaadin components render their internals there (every text field has a
`vaadin-input-container` in its shadow root, some components render buttons of
their own), so a query counts the components the application created, as the
server-side component tree does. Flow puts a component's children in the light
DOM, inside dialogs and other overlays too, so this changes nothing for them.
Content an application renders in a shadow root of its own, such as a Lit
template's, needs the opt-out:

```java
find(page, ButtonElement.class).includingShadowDom().withText("In template").single();
```

Call `includingShadowDom()` right after `find`, or after the plain CSS filters
above.

### Components without an element class

For an add-on, a custom component, a layout or a plain HTML element, query by
tag. The query has the label, text and placeholder filters next to the ones
every query has, and returns `GenericElement`s:

```java
GenericElement card = find(page, "x-card").withLabel("Card label").single();
List<GenericElement> chips = find(dialog.getLocator(), "my-chip").withTheme("small").all();
GenericElement box = VaadinElement.of(page.locator("#summary"));   // wrap any locator
```

`find(page, VaadinElement.ANY_TAG)` (`"*"`) searches every light-DOM element,
whatever its tag, for a lookup that only knows an id, a test id, a class, a theme
or an attribute:

```java
GenericElement billing = find(page, VaadinElement.ANY_TAG).withTestId("billing").single();
```

Tag names are case-insensitive and may contain `.`, `_` and `-`, as in
`getLightDomChildren(..)`.

A `GenericElement` has the `VaadinElement` API (`getText`, `getTagName`,
`getProperty`, `click`, visibility) plus CSS class, inline and computed style
(`getInlineStyle`, `getComputedStyle`), theme helpers, `focus()` / `blur()`
on the host, and `isEnabled()` / `assertEnabled()` / `assertDisabled()` read
from the host's `disabled` attribute, which Flow sets on any component disabled
with `setEnabled(false)`.

## Which lookup to use

1. `find(..)` with a filter on what the user sees:
   `find(page, TextFieldElement.class).withLabel("Name").single()`,
   `.withText(..)`, `.withAccessibleName(..)`, `.withPlaceholder(..)`. The
   static factories (`getByLabel`, `getByText`, `getByHeaderText`...) are
   shortcuts you can use instead.
2. An item through its container: `tabs.getTab("Details")`.
3. `withId` / `withTestId` (or `getById` / `getByTestId`) for components with
   no user-visible name.
4. A raw Playwright locator wrapped in an element: `new GridElement(page.locator(".orders vaadin-grid"))`.

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
