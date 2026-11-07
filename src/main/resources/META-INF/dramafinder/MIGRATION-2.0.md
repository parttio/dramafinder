# Migrating to DramaFinder 2.0

DramaFinder 2.0 makes label lookups **exact by default** (issue #201). Every
factory that finds a component by its label, text, header, title or name now:

1. matches the **whole** value — `"Name"` no longer finds a field labelled
   `"Nickname"`;
2. is **case-sensitive** — `"save"` no longer finds a `"Save"` button;
3. applies **no `.first()`** — two components with the same label raise a
   Playwright strict-mode error instead of silently resolving to the first one
   in the DOM.

Whitespace is still normalized: leading, trailing and repeated spaces do not
matter.

No method was removed or renamed, so a 1.x test suite still **compiles**. Lookups
that relied on the old matching fail at **runtime**. Partial matching is still
available, but you now ask for it explicitly with `getByLabelContaining` /
`getByTextContaining`.

## Why

With substring matching and `.first()`, a lookup could resolve to the wrong
component without any error. The assertion helpers re-run the same lookup, so the
test then passed against the wrong component. An exact lookup either finds the
component you named or fails loudly.

## How a broken lookup shows up

| Symptom | Cause | Fix |
|---|---|---|
| Timeout, `element(s) not found`; the call log shows `setExact(true)` or `getByPlaceholder(..., setExact(true))` | The label passed is only part of the real label, or differs in case | Pass the whole label as the view renders it, or use the `…Containing` variant |
| `strict mode violation: … resolved to N elements` | Several components share the label, and 1.x silently picked the first | Scope the lookup to a container (`getByLabel(Locator, String)`), give the component an id, or use the query builder (`find(...).withLabel(...).withId(...)`) |

The quickest way to find the exact labels is `ViewOutline`. Run
`ViewOutline.write(page)` (or read `view-outline.txt` from a failed test's agent
report). It lists a lookup for every component on the view that is verified to
resolve to exactly that component.

## Changed methods

52 public factory methods change behaviour. "Partial" means a part of the label
used to match; "case" means a differently-cased label used to match; "duplicates"
means `.first()` used to pick among several matches.

### Field `getByLabel` — 27 methods

Matches a visible label, `aria-label` or, when the field has no label, the
placeholder. Before: partial, case and duplicates, for the label and the
placeholder alike. After: exact. Partial alternative:
`getByLabelContaining(Page|Locator, String)`.

| Element | `getByLabel(Page, String)` | `getByLabel(Locator, String)` |
|---|---|---|
| `BigDecimalFieldElement` | changed | changed |
| `ComboBoxElement` | changed | changed |
| `DatePickerElement` | changed | changed |
| `DateTimePickerElement` | changed | changed |
| `EmailFieldElement` | changed | changed |
| `IntegerFieldElement` | changed | changed |
| `MultiSelectComboBoxElement` | changed | changed |
| `NumberFieldElement` | changed | changed |
| `PasswordFieldElement` | changed | — |
| `SelectElement` | changed (see below) | changed (see below) |
| `SliderElement` | changed | changed |
| `TextAreaElement` | changed | changed |
| `TextFieldElement` | changed | changed |
| `TimePickerElement` | changed | changed |

`SelectElement.getByLabel` matches the **visible label or `aria-label`** of the
select. In 1.x it matched the value button's accessible name, which also contains
the selected value or the placeholder. That means a 1.x lookup stopped finding the
select once a value was picked. There is no placeholder fallback for a select any
more.

### Other `getByLabel` — 14 methods

Matched on the component's accessible name. Partial alternative:
`getByLabelContaining` with the same parameters.

| Method | Before |
|---|---|
| `BreadcrumbsElement.getByLabel(Page, String)` | partial, case, duplicates |
| `ButtonElement.getByLabel(Page, String)` | partial, case (alias of `getByText`) |
| `CheckboxElement.getByLabel(Page, String)` | partial, case, duplicates |
| `CheckboxGroupElement.getByLabel(Page, String)` | partial, case, duplicates |
| `CheckboxGroupElement.getByLabel(Locator, String)` | partial, case, duplicates |
| `CustomFieldElement.getByLabel(Page, String)` | partial, case, duplicates |
| `CustomFieldElement.getByLabel(Locator, String)` | partial, case, duplicates |
| `ListBoxElement.getByLabel(Page, String)` | partial, case, duplicates |
| `MenuBarElement.getByLabel(Page, String)` | partial, case |
| `MenuElement.getByLabel(Page, String)` | partial, case |
| `PopoverElement.getByLabel(Page, String)` | partial, case |
| `RadioButtonGroupElement.getByLabel(Page, String)` | partial, case, duplicates |
| `SideNavigationElement.getByLabel(Page, String)` | partial, case, duplicates |
| `SwitchElement.getByLabel(Page, String)` | partial, case, duplicates |

`CustomFieldElement.getField(Class, String)` calls the wrapped type's
`getByLabel(Locator, String)`, so it now needs the wrapped field's whole name too.

### Button text — 2 methods

| Method | Before |
|---|---|
| `ButtonElement.getByText(Page, String)` | partial, case |
| `ButtonElement.getByText(Locator, String)` | partial, case |

The name is the button's `aria-label` when it has one, otherwise its own text. A
tooltip on the button is never part of the name. In 1.x the name came from
Playwright's role name, which gains the tooltip text while the tooltip is open. A
1.x lookup could therefore stop matching after a hover. Partial alternative:
`getByTextContaining(Page|Locator, String)`. The overloads that take
`GetByRoleOptions` are unchanged and still use Playwright's role name.

### Headers, titles, summaries and button text — 7 methods

No `…Containing` variant. Pass the whole text, or use the query builder or a
`new XElement(locator)` for anything else.

| Method | Before |
|---|---|
| `DialogElement.getByHeaderText(Page, String)` | partial, case |
| `ConfirmDialogElement.getByHeaderText(Page, String)` | partial, case |
| `LoginOverlayElement.getByTitle(Page, String)` | partial, case |
| `CardElement.getByTitle(Page, String)` | partial, case |
| `CardElement.getByTitle(Locator, String)` | partial, case |
| `DetailsElement.getBySummaryText(Page, String)` | partial, case |
| `UploadElement.getByButtonText(Page, String)` | partial, case, duplicates |

`DetailsElement.getBySummaryText` matches the summary's accessible name. A summary
that holds components is named by all of their text: a summary made of
`"Summary Component"` plus a `"test"` button is `"Summary Component test"`.

### Avatar name — 2 methods

| Method | Before |
|---|---|
| `AvatarElement.getByName(Page, String)` | duplicates (the name was already exact) |
| `AvatarElement.getByName(Locator, String)` | duplicates (the name was already exact) |

### Utility class — 2 methods

`AccessibleNameLocator.find(Page, …)` and `AccessibleNameLocator.find(Locator, …)`
are now exact, with an exact placeholder fallback and no `.first()`.
`AccessibleNameLocator.findContaining(...)` keeps the 1.x behaviour, but without
`.first()`. This matters only to custom elements built on the helper.

## Unchanged

These lookups match free-form content rather than a label, and keep the 1.x
behaviour (case-insensitive substring, first match):
`BadgeElement.getByText`, `NotificationElement.getByText`,
`TooltipElement.getByText` and `VirtualListElement.getItemByText`.

Item lookups inside a container (`getCheckbox`, `getTab`, `selectItem`,
`getItem`, `MenuItemElement.getByLabel`, `CheckboxElement.getByLabel(Locator, …)`
and the like) were already exact since 1.2 and do not change.

## Rewriting a lookup

```java
// 1.x: partial label
TextFieldElement.getByLabel(page, "Email");          // label "Email address"
// 2.0: whole label…
TextFieldElement.getByLabel(page, "Email address");
// …or an explicit partial match
TextFieldElement.getByLabelContaining(page, "email");

// 1.x: case differs
ButtonElement.getByText(page, "save");               // button "Save"
// 2.0
ButtonElement.getByText(page, "Save");

// 1.x: two "Delete" buttons, the first one won
ButtonElement.getByText(page, "Delete");
// 2.0: scope it…
ButtonElement.getByText(dialog.getLocator(), "Delete");
// …or use the query builder (also new in 2.0)
find(page, ButtonElement.class).withText("Delete").withTestId("confirm-delete").single();
```

To find the lookups to review, search the test sources for the changed factories:

```
grep -rnE '\.(getByLabel|getByText|getByHeaderText|getByTitle|getBySummaryText|getByButtonText|getByName)\(' src/test
```

## Actions wait for the server round-trip

In 1.x only `setValue` and a few date, slider and checkbox-group methods waited
for the Vaadin server to answer. In 2.0 every element action does (issue #202):
`click`, `setValue`, `clear`, `check`/`uncheck`, `selectItem`, key presses,
`hoverTarget`, `focus`/`blur`, `upload`, `setProperty` and the rest return only
once Flow has no request in flight. Playwright still waits for the element to be
actionable before the action.

Most suites need no change; flaky reads right after an action get steadier. A test
that asserts the in-flight state — a loading indicator, a disabled-while-saving
button — now sees the final state instead. Wrap that action:

```java
import org.vaadin.addons.dramafinder.AutoWait;

AutoWait.withoutWaiting(() -> ButtonElement.getByText(page, "Save").click());
assertThat(page.locator(".spinner")).isVisible();
```

`-Ddramafinder.autoWait=false` turns the waiting off for a whole run.
`waitForVaadinIdle()` still waits explicitly, and is what to call after an action
on a raw Playwright `Locator`, which the element API does not see.

## Also new in 2.0

The typed query builder (issue #200) gives every element the same lookup grammar.
It only offers the filters that fit the component, so they are checked at compile
time:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

TextFieldElement name = find(page, TextFieldElement.class).withLabel("Name").single();
dialog.find(ButtonElement.class).withText("Delete").single().click();
```

It sits alongside the factories; nothing has to move to it. See "Typed queries"
in `META-INF/dramafinder/api-reference.md`.

Every element now also has `getById(Page|Locator, String)` and
`getByTestId(Page|Locator, String)` (issue #203), so
`new XElement(page.locator("#id"))` can become `XElement.getById(page, "id")`. The test
id is the `data-testid` attribute Flow's `setTestId(...)` sets. The five 1.x `getById`
methods (AppLayout, FormLayout, Grid, TreeGrid, Tabs) now also check the element's tag
and accept ids that are not valid CSS identifiers.
