# DramaFinder

Playwright wrappers for Vaadin components: how a test finds a component, acts on
it and asserts its state.

## Finding a component

**Label**:
The visible text a component shows as its caption, in its own label slot. A
component without one has no label, whatever names it for assistive technology.
_Avoid_: name, caption, title (for this concept)

**Accessible name**:
The name assistive technology announces for a component: its label, or else an
`aria-label` or `aria-labelledby` text, or for a button or tab its own text.
_Avoid_: label (when an `aria-label` is meant), ARIA label

**Placeholder**:
Hint text a field shows while it is empty. Neither a label nor an accessible name.
_Avoid_: hint, prompt

**Item**:
One selectable entry inside a container component, such as an option of a select,
a tab, a checkbox of a group or a side-nav entry. Found through its container by
its whole text.
_Avoid_: option (for the general concept), child
