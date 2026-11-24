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
    find(page, TextFieldElement.class).withLabel("Name").single().setValue("Ada Lovelace");
    find(page, EmailFieldElement.class).withLabel("Email").single().setValue("ada@example.com");
    find(page, ComboBoxElement.class).withLabel("Country").single().selectItem("United Kingdom");
    find(page, DatePickerElement.class).withLabel("Birthday").single().setValue(LocalDate.of(1815, 12, 10));
    find(page, CheckboxElement.class).withLabel("Subscribe to the newsletter").single().check();

    find(page, ButtonElement.class).withAccessibleName("Save").single().click();

    find(page, NotificationElement.class).withTextContaining("Saved Ada Lovelace").single().assertOpen();
}

@Test
void rejectsAnInvalidEmail() {
    EmailFieldElement email = find(page, EmailFieldElement.class).withLabel("Email").single();
    email.setValue("not-an-email");
    find(page, ButtonElement.class).withAccessibleName("Save").single().click();

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

    find(page, TextFieldElement.class).withLabel("Filter").single().setValue("ada");

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
    ButtonElement edit = find(page, ButtonElement.class).withAccessibleName("Edit").single();
    edit.assertDisabled();

    grid.select(0);
    grid.assertRowSelected(0);
    edit.assertEnabled();
    edit.click();

    DialogElement dialog = find(page, DialogElement.class).withAccessibleName("Edit person").single();
    dialog.assertOpen();
    TextFieldElement name = dialog.find(TextFieldElement.class).withLabel("Name").single();
    name.setValue("Ada King");
    dialog.find(ButtonElement.class).withAccessibleName("Save").single().click();

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
    find(page, ButtonElement.class).withAccessibleName("Delete").single().click();

    ConfirmDialogElement confirm = find(page, ConfirmDialogElement.class).withAccessibleName("Delete person?").single();
    confirm.assertMessage("This cannot be undone.");
    confirm.assertConfirmText("Delete");
    confirm.confirm();

    confirm.assertClosed();
    GridElement.get(page).assertRowCount(99);
}

@Test
void cancelKeepsThePerson() {
    find(page, ButtonElement.class).withAccessibleName("Delete").single().click();
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
    find(actions.getCellContentLocator(), ButtonElement.class).withAccessibleName("Details").single().click();
    find(page, DialogElement.class).withAccessibleName("Ada Lovelace").single().assertOpen();
}
```

## Navigate with the side navigation

```java
@Test
void sideNavOpensTheOrdersView() {
    SideNavigationElement nav = find(page, SideNavigationElement.class).withLabel("Main menu").single();
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

    find(sheet.getContentLocator(), TextFieldElement.class).withLabel("Card number").single().assertVisible();
}
```

## Upload a file

```java
@Test
void uploadsAnInvoice() {
    UploadElement upload = UploadElement.getByButtonText(page, "Upload invoice");
    upload.uploadFiles(Paths.get("src/test/resources/invoice.pdf"));

    upload.assertFileComplete("invoice.pdf");
    find(page, NotificationElement.class).withTextContaining("invoice.pdf received").single().assertOpen();
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
    find(page, NotificationElement.class).withTextContaining("Export started").single().assertOpen();
}
```

## Show a loading state

```java
@Test
void exportShowsProgressWhileRunning() {
    ButtonElement export = find(page, ButtonElement.class).withAccessibleName("Export").single();
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
    find(page, ButtonElement.class).withAccessibleName("Delete").single().assertEnabled();
}
```

See [Spring Boot and Spring Security](spring.md).
