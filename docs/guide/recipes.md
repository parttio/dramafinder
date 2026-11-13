# Recipes

End-to-end examples of common test scenarios. Each one pairs the view code
with the test that exercises it. The [component pages](../specifications/index.md)
go deeper into each element.

## Fill in and submit a form

```java
// View
TextField name = new TextField("Name");
EmailField email = new EmailField("Email");
ComboBox<Country> country = new ComboBox<>("Country", countries);
DatePicker birthday = new DatePicker("Birthday");
Checkbox newsletter = new Checkbox("Subscribe to the newsletter");
Button save = new Button("Save", e -> {
    if (binder.writeBeanIfValid(person)) {
        Notification.show("Saved " + person.getName());
    }
});
```

```java
@Test
void savesAValidPerson() {
    TextFieldElement.getByLabel(page, "Name").setValue("Ada Lovelace");
    EmailFieldElement.getByLabel(page, "Email").setValue("ada@example.com");
    ComboBoxElement.getByLabel(page, "Country").selectItem("United Kingdom");
    DatePickerElement.getByLabel(page, "Birthday").setValue(LocalDate.of(1815, 12, 10));
    CheckboxElement.getByLabel(page, "Subscribe to the newsletter").check();

    ButtonElement.getByText(page, "Save").click();

    NotificationElement.getByText(page, "Saved Ada Lovelace").assertOpen();
}

@Test
void rejectsAnInvalidEmail() {
    EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");
    email.setValue("not-an-email");
    ButtonElement.getByText(page, "Save").click();

    email.assertInvalid();
    email.assertErrorMessage("Enter a valid email address");
}
```

## Filter a grid

```java
// View
TextField filter = new TextField("Filter");
filter.setValueChangeMode(ValueChangeMode.LAZY);
filter.addValueChangeListener(e -> grid.setItems(service.find(e.getValue())));
```

```java
@Test
void filterNarrowsTheGrid() {
    GridElement grid = GridElement.get(page);
    grid.assertRowCount(100);

    TextFieldElement.getByLabel(page, "Filter").setValue("ada");

    grid.assertRowCount(1);
    grid.assertCellContent(0, "Name", "Ada Lovelace");
}

@Test
void nameColumnIsSortable() {
    GridElement grid = GridElement.get(page);
    var name = grid.findHeaderCellByText("Name").orElseThrow();

    name.sort(GridElement.SortDirection.DESCENDING);

    name.assertSortDirection(GridElement.SortDirection.DESCENDING);
    grid.assertCellContent(0, "Name", "Zoe Zhang");
}
```

## Select a row and edit it in a dialog

```java
// View
grid.asSingleSelect().addValueChangeListener(e -> editButton.setEnabled(e.getValue() != null));
editButton.addClickListener(e -> openEditor(grid.asSingleSelect().getValue()));

private void openEditor(Person person) {
    Dialog dialog = new Dialog("Edit person");
    TextField name = new TextField("Name");
    dialog.add(name);
    dialog.getFooter().add(new Button("Cancel", e -> dialog.close()),
            new Button("Save", e -> { save(person); dialog.close(); }));
    dialog.open();
}
```

```java
@Test
void editsTheSelectedPerson() {
    GridElement grid = GridElement.get(page);
    ButtonElement edit = ButtonElement.getByText(page, "Edit");
    edit.assertDisabled();

    grid.select(0);
    grid.assertRowSelected(0);
    edit.assertEnabled();
    edit.click();

    DialogElement dialog = DialogElement.getByHeaderText(page, "Edit person");
    dialog.assertOpen();
    TextFieldElement name = TextFieldElement.getByLabel(dialog.getLocator(), "Name");
    name.setValue("Ada King");
    ButtonElement.getByText(dialog.getLocator(), "Save").click();

    dialog.assertClosed();
    grid.assertCellContent(0, "Name", "Ada King");
}
```

## Confirm a destructive action

```java
// View
ConfirmDialog confirm = new ConfirmDialog("Delete person?",
        "This cannot be undone.", "Delete", e -> delete(person));
confirm.setCancelable(true);
```

```java
@Test
void deletingAsksForConfirmation() {
    ButtonElement.getByText(page, "Delete").click();

    ConfirmDialogElement confirm = ConfirmDialogElement.getByHeaderText(page, "Delete person?");
    confirm.assertMessage("This cannot be undone.");
    confirm.assertConfirmText("Delete");
    confirm.confirm();

    confirm.assertClosed();
    GridElement.get(page).assertRowCount(99);
}

@Test
void cancelKeepsThePerson() {
    ButtonElement.getByText(page, "Delete").click();
    ConfirmDialogElement.get(page).cancel();
    GridElement.get(page).assertRowCount(100);
}
```

## Work with the cells of a grid row

```java
// View
grid.addColumn(Person::getName).setHeader("Name");
grid.addComponentColumn(p -> new Button("Details", e -> showDetails(p))).setHeader("Actions");
grid.setItemDetailsRenderer(new ComponentRenderer<>(p -> new Span(p.getBio())));
```

```java
@Test
void rowDetailsShowTheBio() {
    GridElement grid = GridElement.get(page);
    var row = grid.findRow(0).orElseThrow();

    row.openDetails();

    row.assertDetailsOpen();
    assertThat(row.getDetailsCell().getCellContentLocator()).hasText("Mathematician");
}

@Test
void buttonInACell() {
    GridElement grid = GridElement.get(page);
    var actions = grid.findCell(0, "Actions").orElseThrow();
    ButtonElement.getByText(actions.getCellContentLocator(), "Details").click();
    DialogElement.getByHeaderText(page, "Ada Lovelace").assertOpen();
}
```

## Navigate with the side navigation

```java
@Test
void sideNavOpensTheOrdersView() {
    SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Main menu");
    nav.clickItem("Orders");

    assertAt(OrdersView.class);
    nav.getItem("Orders").assertCurrent();
}
```

## Switch tabs

```java
@Test
void paymentTabShowsTheCardFields() {
    TabSheetElement sheet = TabSheetElement.get(page);
    sheet.selectTab("Payment");

    TextFieldElement.getByLabel(sheet.getContentLocator(), "Card number").assertVisible();
}
```

## Upload a file

```java
@Test
void uploadsAnInvoice() {
    UploadElement upload = UploadElement.getByButtonText(page, "Upload invoice");
    upload.uploadFiles(Paths.get("src/test/resources/invoice.pdf"));

    upload.assertFileComplete("invoice.pdf");
    NotificationElement.getByText(page, "invoice.pdf received").assertOpen();
}
```

Files built in memory work too, with Playwright's `FilePayload`:

```java
upload.uploadFiles(new FilePayload("notes.txt", "text/plain", "hello".getBytes(UTF_8)));
```

## Use a menu bar

```java
// View
MenuBar menu = new MenuBar();
menu.setAriaLabel("Actions");
var file = menu.addItem("File");
file.getSubMenu().addItem("Export", e -> export());
```

```java
@Test
void exportFromTheFileMenu() {
    MenuBarElement menu = find(page, MenuBarElement.class).withAccessibleName("Actions").single();
    menu.openSubMenu("File").getMenuItemElement("Export").click();
    NotificationElement.getByText(page, "Export started").assertOpen();
}
```

## Show a loading state

```java
@Test
void exportShowsProgressWhileRunning() {
    ButtonElement export = ButtonElement.getByText(page, "Export");
    AutoWait.withoutWaiting(export::click);

    ProgressBarElement.getById(page, "export-progress").assertVisible();

    export.waitForVaadinIdle();
    ProgressBarElement.getById(page, "export-progress").assertHidden();
}
```

## Test as a signed-in user

```java
@Test
@WithMockUser(username = "alice", roles = "ADMIN")
void adminCanDeleteUsers() {
    navigate(UsersView.class);
    ButtonElement.getByText(page, "Delete").assertEnabled();
}
```

See [Spring Boot and Spring Security](spring.md).
