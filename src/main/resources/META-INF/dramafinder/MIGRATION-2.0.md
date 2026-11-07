# Migrating to DramaFinder 2.0

DramaFinder 2.0 changes how a component is found by its label or name, in two ways:

1. **Lookups are exact by default** (issue #201). Every factory that finds a
   component by its label, text, header, title or name now matches the **whole**
   value (`"Name"` no longer finds a field labelled `"Nickname"`), is
   **case-sensitive** (`"save"` no longer finds a `"Save"` button) and applies
   **no `.first()`** (two components with the same label raise a Playwright
   strict-mode error instead of resolving to the first one in the DOM).
   Whitespace is still normalized.
2. **A label is the visible label.** `getByLabel` matches the text a component
   shows in its own label slot, and nothing else. An `aria-label` (or
   `aria-labelledby` text) is the component's **accessible name**, which the query
   builder finds with `withAccessibleName`; a placeholder is found with
   `withPlaceholder`. In 1.x `getByLabel` matched all three.

## Replacing a 1.x `getByLabel`

| The 1.x call relied on | Use in 2.0 |
|---|---|
| the whole visible label | `getByLabel(page, label)`, unchanged |
| part of the visible label, or another case | `getByLabelContaining(page, text)` |
| an `aria-label`, or you don't know which (the closest to 1.x) | `find(page, X.class).withAccessibleNameContaining(text).single()` |
| the placeholder | `find(page, X.class).withPlaceholder(placeholder).single()` |

`find` is `org.vaadin.addons.dramafinder.element.query.ElementQueries.find`; use
`find(locator, X.class)` for a scoped lookup. `withAccessibleNameContaining`
matches the label or `aria-label` case-insensitively as a substring, like 1.x
did, but it has no placeholder fallback and no `.first()`: an ambiguous
fragment fails instead of picking the first match. `withAccessibleName` is its
exact sibling.

A 1.x test suite still **compiles**, with three exceptions: a test class that
declares its own `navigate`, `assertAt`, `assertPath` or `assertQueryParameter`
(see "New methods on `AbstractBasePlaywrightIT`"), code that calls the removed
`AccessibleNameLocator` (see "Removed"), and the `getByLabel` factories of
components without a visible label, which are deprecated and compile with a
warning. Lookups that relied on the old matching fail at **runtime**.

## Why

With substring matching and `.first()`, a lookup could resolve to the wrong
component without any error. The assertion helpers re-run the same lookup, so the
test then passed against the wrong component. An exact lookup either finds the
component you named or fails loudly.

A label and an accessible name are different things: a sighted user reads the
label, a screen reader announces the accessible name. Keeping one word for one
concept also makes `getByLabel` and the query builder's `withLabel` find the same
components, which in 1.x they did not.

## How a broken lookup shows up

| Symptom | Cause | Fix |
|---|---|---|
| Timeout, `element(s) not found` from a `getByLabel` | The field is named by an `aria-label` or a placeholder, not a visible label | Use the replacement from the table above |
| Timeout, `element(s) not found` | The label passed is only part of the real label, or differs in case | Pass the whole label as the view renders it, or use the `…Containing` variant |
| `strict mode violation: … resolved to N elements` | Several components share the label, and 1.x silently picked the first | Scope the lookup to a container (`getByLabel(Locator, String)`), give the component an id, or use the query builder (`find(...).withLabel(...).withId(...)`) |

The quickest way to find the right lookups is `ViewOutline`. Run
`ViewOutline.write(page)` (or read `view-outline.txt` from a failed test's agent
report). It lists a lookup for every component on the view that is verified to
resolve to exactly that component, using `getByLabel` for a visible label and
`withAccessibleName` / `withPlaceholder` otherwise.

## Changed methods

"Partial" means a part of the label used to match; "case" means a
differently-cased label used to match; "duplicates" means `.first()` used to pick
among several matches; "name" means an `aria-label` used to match; "placeholder"
means the placeholder used to match.

### `getByLabel` on components with a visible label — 21 elements

`getByLabel(Page|Locator, String)` matches the whole visible label, and
`getByLabelContaining(Page|Locator, String)` part of it (case-insensitive, no
`.first()`). Both are the same lookup as the query builder's `withLabel` /
`withLabelContaining`.

| Element | Before |
|---|---|
| `BigDecimalFieldElement`, `ComboBoxElement`, `DatePickerElement`, `EmailFieldElement`, `IntegerFieldElement`, `MultiSelectComboBoxElement`, `NumberFieldElement`, `PasswordFieldElement`, `TextAreaElement`, `TextFieldElement`, `TimePickerElement` | partial, case, duplicates, name, placeholder |
| `DateTimePickerElement` | partial, case, duplicates, name, placeholder (of its date picker) |
| `SelectElement` | partial, case, duplicates, name, placeholder (see below) |
| `SliderElement` | partial, case, duplicates, name |
| `CheckboxElement`, `SwitchElement` | partial, case, duplicates, name |
| `CheckboxGroupElement`, `RadioButtonGroupElement`, `CustomFieldElement`, `SideNavigationElement` | partial, case, duplicates, name |
| `SideNavigationItemElement` | new in 2.0 |

New overloads, so every one of these elements has all four:
`PasswordFieldElement`, `SwitchElement`, `RadioButtonGroupElement` and
`SideNavigationElement` gain `getByLabel(Locator, String)` and
`getByLabelContaining(Locator, String)`; `CheckboxElement` gains
`getByLabelContaining(Locator, String)`; `SideNavigationItemElement` gains all four.

`SelectElement`: in 1.x the lookup read the value button's accessible name, which
also holds the selected value or the placeholder, so it stopped finding the
select once a value was picked. Its placeholder is found with
`find(page, SelectElement.class).withPlaceholder(..)`, which matches while no
value is selected.

`DateTimePickerElement`: the placeholder lives on its date picker
(`setDatePlaceholder`); `find(page, DateTimePickerElement.class).withPlaceholder(..)`
reads it there.

`CustomFieldElement.getField(Class, String)` finds a wrapped field by its whole
accessible name (`find(...).withAccessibleName(..)` inside the custom field). In
1.x it went through the wrapped type's `getByLabel` and matched part of the name.

### Deprecated: `getByLabel` on components without a visible label

These components have no label slot, so their `getByLabel` always matched the
accessible name. They keep that behaviour, now exact, and are deprecated for
removal in 3.0:

| Deprecated | Before | Replacement |
|---|---|---|
| `BreadcrumbsElement.getByLabel(Page, String)` / `…Containing` | partial, case, duplicates | `find(page, BreadcrumbsElement.class).withAccessibleName(..)` / `…Containing` |
| `ListBoxElement.getByLabel(Page, String)` / `…Containing` | partial, case, duplicates | `find(page, ListBoxElement.class).withAccessibleName(..)` / `…Containing` |
| `MenuBarElement.getByLabel(Page, String)` / `…Containing` | partial, case | `find(page, MenuBarElement.class).withAccessibleName(..)` / `…Containing` |
| `MenuElement.getByLabel(Page, String)` / `…Containing` | partial, case | `find(page, MenuElement.class).withAccessibleName(..)` / `…Containing` |
| `PopoverElement.getByLabel(Page, String)` / `…Containing` | partial, case | `find(page, PopoverElement.class).withAccessibleName(..)` / `…Containing` |
| `MenuItemElement.getByLabel(Locator, String)` | already exact | `find(locator, MenuItemElement.class).withAccessibleName(..)` |
| `ButtonElement.getByLabel(Page, String)` / `…Containing` | partial, case (alias of `getByText`) | `getByText` / `getByTextContaining` |

`MenuElement` can now be used with `find`.

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

## Removed

`org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator` (`find`,
`findContaining`, `findExact`). Custom elements built on it use the query
builder instead: `find(scope, X.class).withLabel(..)` for a visible label,
`.withAccessibleName(..)` for an accessible name.

## Unchanged

These lookups match free-form content rather than a label, and keep the 1.x
behaviour (case-insensitive substring, first match):
`BadgeElement.getByText`, `NotificationElement.getByText`,
`TooltipElement.getByText` and `VirtualListElement.getItemByText`.

Item lookups inside a container (`getCheckbox`, `getTab`, `selectItem`, `getItem`
and the like) were already exact since 1.2 and do not change.

## Rewriting a lookup

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

// 1.x: partial label
TextFieldElement.getByLabel(page, "Email");          // label "Email address"
// 2.0: whole label…
TextFieldElement.getByLabel(page, "Email address");
// …or an explicit partial match
TextFieldElement.getByLabelContaining(page, "email");

// 1.x: a field with only an aria-label (Flow: setAriaLabel("Search"))
TextFieldElement.getByLabel(page, "Search");
// 2.0
find(page, TextFieldElement.class).withAccessibleName("Search").single();

// 1.x: a field with only a placeholder
TextFieldElement.getByLabel(page, "Type to search");
// 2.0
find(page, TextFieldElement.class).withPlaceholder("Type to search").single();

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

## New methods on `AbstractBasePlaywrightIT`

`AbstractBasePlaywrightIT` gains five protected methods for navigation by route
class (issue #204): `navigate(Class<?>)`, `navigate(Class<?>, Object)`,
`assertAt(Class<?>)`, `assertPath(String)` and `assertQueryParameter(String, String)`.
A test class that already declares a method with one of these signatures now
overrides it. If its return type differs, or it is `private`, the class no longer
compiles. Rename the test's method, or use the inherited one.

## The Spring Boot base class moved into the library

Spring Boot projects copied a `SpringPlaywrightIT` from the skill's template. 2.0
ships it as `org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT` (issue #205):

1. Delete the project's copy.
2. Switch the imports to `org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT`.
3. Optionally drop `@SpringBootTest(webEnvironment = RANDOM_PORT)` from the tests:
   the base class carries it. Keep the annotation where a test sets properties or
   another web environment.

The library class also adds the servlet context path to `getUrl()`. The copy can
stay instead; nothing forces the switch. DramaFinder declares Spring as `provided`,
so projects without Spring see no change.

## `SliderElement.setValue` refuses a disabled or read-only slider

`SliderElement.setValue(double)` writes to the native input from a script, which
a disabled input does not block. In 1.x the value landed in the browser while the
server ignored it, so `assertValue` passed on a slider no user could move. 2.0
throws `IllegalStateException` for a disabled or read-only slider (issue #166). A
test that set a locked slider expecting nothing to happen should assert the lock
instead: `assertDisabled()` or `assertReadOnly()`, then `assertValue(...)` with
the unchanged value.

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

`AbstractBasePlaywrightIT` navigates by route class (issue #204):
`navigate(ProfileView.class)` instead of `page.navigate(getUrl() + "nav/profile")`,
with `assertAt(ProfileView.class)`, `assertPath(...)` and `assertQueryParameter(...)`
to check where the browser ended up.

Every element now also has `getById(Page|Locator, String)` and
`getByTestId(Page|Locator, String)` (issue #203), so
`new XElement(page.locator("#id"))` can become `XElement.getById(page, "id")`. The test
id is the `data-testid` attribute Flow's `setTestId(...)` sets. The five 1.x `getById`
methods (AppLayout, FormLayout, Grid, TreeGrid, Tabs) now also check the element's tag
and accept ids that are not valid CSS identifiers.
