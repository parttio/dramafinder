# Use Playwright's and Vaadin's standard APIs instead of reimplementing them

DramaFinder builds on Playwright's locators, selector engines and assertions
(`getByRole`, `getByLabel`, `getByText`, `filter`, `and`/`or`, `assertThat`) and
on the APIs the Vaadin components themselves expose. When one of them expresses
a rule, we call it rather than rewriting its logic in our own XPath, CSS or
JavaScript, even when the rewrite would be faster: a copy has to be kept in step
with Playwright or Vaadin by hand, and it drifts in ways no test catches until a
user hits them. A performance problem is fixed by using the standard API
differently (fewer calls, a narrower root, one pass instead of two), not by
replacing it.

## Considered Options

- **Reimplement for speed** (#289, closed): `withAccessibleName` rewrote
  Playwright's `getByLabel` rules as an XPath `self::` predicate, cutting a call
  on a 2,000-element page from ~9.4 ms to ~3.2 ms. Rejected: it already differed
  from Playwright (ids inside another shadow root not followed, ASCII-only case
  folding). The kept fix (#278) still calls `getByLabel`, once instead of twice,
  for ~4.7 ms.

## Consequences

- Our own selector logic is the exception, for rules no standard API expresses,
  and its Javadoc says why: a component's own light-DOM text without slotted
  children (`QueryText.ownTextIs`, pitfall 11) and CSS that only combines
  attributes (#277). Exact item lookups (`ItemLocator`) show the default: they
  are Playwright's `getByRole` / `getByText` with `setExact(true)`.
- Reading a component's state through its own API (e.g. the grid's
  `_getColumns()` or `_scrollToFlatIndex()`) is using the standard API, not
  reimplementing it.
