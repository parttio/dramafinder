# A label is the visible label, not the accessible name

`getByLabel` and `withLabel` match only the text a component shows in its own label
slot; an `aria-label` (or `aria-labelledby` text) is the component's accessible name,
found with `withAccessibleName`, and a placeholder is found with `withPlaceholder`.
We chose this in 2.0 (#214) over Playwright's own `getByLabel`, which also matches
`aria-label`, because one word must mean one thing: in 1.x the factory matched label,
`aria-label` and placeholder while the query builder's `withLabel` matched the visible
label only, so the two lookups disagreed, and a test reads more truthfully when
"label" is what a sighted user sees.

## Considered Options

- **Label = accessible name everywhere** (Playwright's meaning): fewer migrations from
  1.x, but `withLabel` would then match text the user never sees, and the placeholder
  fallback would stay a special case.
- **Static `getByAccessibleName` factories on every element**: a rename-only migration
  for 1.x users, at the cost of ~100 more one-line static methods. Rejected; the
  query's `withAccessibleName(Containing)` is the single place that rule lives (Java
  cannot inherit static methods from an interface, so the factories could not be
  shared).

## Consequences

- Components without a visible label (Breadcrumbs, ListBox, MenuBar, Menu, MenuItem,
  Popover, Button) have no `getByLabel`; their 1.x factories are deprecated until 3.0.
- `LabelLookupParityTest` keeps `getByLabel` and `withLabel` on exactly the same
  elements, and `getByLabel` delegates to `withLabel`.
