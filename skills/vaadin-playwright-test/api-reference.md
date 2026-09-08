# DramaFinder API Reference

> **Auto-generated from source — do not edit by hand.** Regenerate with `jbang tools/generate-api-reference.java`.
> DramaFinder 1.1.7-SNAPSHOT — 55 element wrappers.

Complete public API of every DramaFinder element wrapper. Each element lists the shared mixin interfaces it implements; those interfaces' methods are documented once under **Shared mixins** at the end (not repeated per element) — except where an element overrides one to change its behaviour, in which case the element lists it again with the behaviour that applies there. Method one-liners come from Javadoc.

**Do not decompile the DramaFinder jar to discover its API — it is all here.** This same file ships *inside* the jar, so it is readable with no network:

```
unzip -p ~/.m2/repository/org/vaadin/addons/dramafinder/*/dramafinder-*.jar \
  META-INF/dramafinder/api-reference.md
```

## Test setup

A DramaFinder test extends `AbstractBasePlaywrightIT`, which opens a Playwright `Page`, navigates it to `getUrl() + getView()`, waits for Vaadin to go idle, and closes it again. The `page` field is what every element factory below takes.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyViewIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return "http://localhost:" + port;
    }

    @Override
    public String getView() {
        return "/my-view"; // appended to getUrl() before each test
    }

    @Test
    public void filtersTheGrid() {
        TextFieldElement.getByLabel(page, "Name").setValue("ada");
        GridElement.get(page).assertRowCount(1);
    }
}
```

Notes:

- The browser runs headless unless the `headless` system property or the `HEADLESS` environment variable says otherwise.
- `setup()` / `cleanup()` are `@BeforeAll` / `@AfterAll` and `setupTest()` / `cleanupTest()` are `@BeforeEach` / `@AfterEach`. Overriding one means calling `super` — the `page` field is null otherwise.
- Playwright's own browser binaries must already be installed.

### AbstractBasePlaywrightIT

*abstract* **Implements:** HasTestView  

**Constants:** `String WAIT_FOR_VAADIN_SCRIPT = …`

**Inherited fields:** `Page page`

**Static methods:**

- `void cleanup()`
- `void setup()`
- `protected boolean isHeadless()`

**Methods:**

- `void setupTest()`
- `void cleanupTest()`
- `protected Page getPage()`
- `protected Browser getBrowser()`
- `protected Playwright getPlaywright()`

### HasTestView

**Methods:**

- `String getUrl()`
- `String getView()`

## Element index

Wrapped web-component tag, its wrapper class, and the wrapper's actual static factory methods. **Factory methods are not uniform** — an element only has the factories listed here (e.g. most fields use `getByLabel`, containers use `get`; `getById` exists on only a few). Do not assume a factory that isn't listed. Every wrapper is also constructible via `new <Name>(Locator)`.

| Web-component tag | Element wrapper | Static factory methods |
|---|---|---|
| `<vaadin-accordion>` | [AccordionElement](#accordionelement) | *constructor only* |
| `<vaadin-accordion-panel>` | [AccordionPanelElement](#accordionpanelelement) | `getAccordionPanelBySummary(Locator, String)`, `getOpenedAccordionPanel(Locator)` |
| `<vaadin-app-layout>` | [AppLayoutElement](#applayoutelement) | `get(Locator)`, `get(Page)`, `getById(Page, String)` |
| `<vaadin-avatar>` | [AvatarElement](#avatarelement) | `get(Locator)`, `get(Page)`, `getByName(Locator, String)`, `getByName(Page, String)` |
| `<vaadin-avatar-group>` | [AvatarGroupElement](#avatargroupelement) | `get(Locator)`, `get(Page)` |
| `<vaadin-badge>` | [BadgeElement](#badgeelement) | `get(Locator)`, `get(Page)`, `getByText(Locator, String)`, `getByText(Page, String)` |
| `<vaadin-big-decimal-field>` | [BigDecimalFieldElement](#bigdecimalfieldelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-breadcrumbs>` | [BreadcrumbsElement](#breadcrumbselement) | `get(Locator)`, `get(Page)`, `getByLabel(Page, String)` |
| `<vaadin-breadcrumbs-item>` | [BreadcrumbsItemElement](#breadcrumbsitemelement) | `getByText(Locator, String)`, `getByText(Page, String)` |
| `<vaadin-button>` | [ButtonElement](#buttonelement) | `getByLabel(Page, String)`, `getByText(Locator, Locator.GetByRoleOptions)`, `getByText(Locator, String)`, `getByText(Page, Page.GetByRoleOptions)`, `getByText(Page, String)` |
| `<vaadin-card>` | [CardElement](#cardelement) | `getByTitle(Locator, String)`, `getByTitle(Page, String)` |
| `<vaadin-checkbox>` | [CheckboxElement](#checkboxelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-checkbox-group>` | [CheckboxGroupElement](#checkboxgroupelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-combo-box>` | [ComboBoxElement](#comboboxelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-date-picker>` | [DatePickerElement](#datepickerelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-date-time-picker>` | [DateTimePickerElement](#datetimepickerelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-details>` | [DetailsElement](#detailselement) | `getBySummaryText(Page, String)` |
| `<vaadin-email-field>` | [EmailFieldElement](#emailfieldelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-grid>` | [GridElement](#gridelement) | `get(Locator)`, `get(Page)`, `getById(Page, String)` |
| `<vaadin-integer-field>` | [IntegerFieldElement](#integerfieldelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-list-box>` | [ListBoxElement](#listboxelement) | `getByLabel(Page, String)` |
| `<vaadin-login-form>` | [LoginFormElement](#loginformelement) | `get(Locator)`, `get(Page)` |
| `<vaadin-login-overlay>` | [LoginOverlayElement](#loginoverlayelement) | `get(Page)`, `getByTitle(Page, String)` |
| `<vaadin-markdown>` | [MarkdownElement](#markdownelement) | `get(Locator)`, `get(Page)` |
| `<vaadin-message-input>` | [MessageInputElement](#messageinputelement) | `get(Locator)`, `get(Page)` |
| `<vaadin-message-list>` | [MessageListElement](#messagelistelement) | `get(Locator)`, `get(Page)` |
| `<vaadin-multi-select-combo-box>` | [MultiSelectComboBoxElement](#multiselectcomboboxelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-number-field>` | [NumberFieldElement](#numberfieldelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-password-field>` | [PasswordFieldElement](#passwordfieldelement) | `getByLabel(Page, String)` |
| `<vaadin-progress-bar>` | [ProgressBarElement](#progressbarelement) | *constructor only* |
| `<vaadin-radio-group>` | [RadioButtonGroupElement](#radiobuttongroupelement) | `getByLabel(Page, String)` |
| `<vaadin-select>` | [SelectElement](#selectelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-side-nav>` | [SideNavigationElement](#sidenavigationelement) | `getByLabel(Page, String)` |
| `<vaadin-side-nav-item>` | [SideNavigationItemElement](#sidenavigationitemelement) | *constructor only* |
| `<vaadin-slider>` | [SliderElement](#sliderelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-split-layout>` | [SplitLayoutElement](#splitlayoutelement) | `get(Page)` |
| `<vaadin-switch>` | [SwitchElement](#switchelement) | `getByLabel(Page, String)` |
| `<vaadin-tab>` | [TabElement](#tabelement) | `getSelectedTab(Locator)`, `getTabByText(Locator, String)` |
| `<vaadin-tabs>` | [TabsElement](#tabselement) | `get(Locator)`, `get(Page)`, `getById(Page, String)` |
| `<vaadin-tabsheet>` | [TabSheetElement](#tabsheetelement) | `get(Page)` |
| `<vaadin-text-area>` | [TextAreaElement](#textareaelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-text-field>` | [TextFieldElement](#textfieldelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-time-picker>` | [TimePickerElement](#timepickerelement) | `getByLabel(Locator, String)`, `getByLabel(Page, String)` |
| `<vaadin-upload>` | [UploadElement](#uploadelement) | `getByButtonText(Page, String)` |
| `<vaadin-virtual-list>` | [VirtualListElement](#virtuallistelement) | `get(Page)` |
| — | [AbstractLoginElement](#abstractloginelement) | *constructor only* |
| — | [AbstractNumberFieldElement](#abstractnumberfieldelement) | *constructor only* |
| — | [ContextMenuElement](#contextmenuelement) | *constructor only* |
| — | [DialogElement](#dialogelement) | `getByHeaderText(Page, String)` |
| — | [MenuBarElement](#menubarelement) | `getByLabel(Page, String)` |
| — | [MenuElement](#menuelement) | `getByLabel(Page, String)` |
| — | [MenuItemElement](#menuitemelement) | `getByLabel(Locator, String)` |
| — | [NotificationElement](#notificationelement) | `getByText(Page, String)` |
| — | [PopoverElement](#popoverelement) | `getByLabel(Page, String)` |
| — | [TreeGridElement](#treegridelement) | `get(Locator)`, `get(Page)`, `getById(Page, String)` |

## Elements

[AbstractLoginElement](#abstractloginelement) · [AbstractNumberFieldElement](#abstractnumberfieldelement) · [AccordionElement](#accordionelement) · [AccordionPanelElement](#accordionpanelelement) · [AppLayoutElement](#applayoutelement) · [AvatarElement](#avatarelement) · [AvatarGroupElement](#avatargroupelement) · [BadgeElement](#badgeelement) · [BigDecimalFieldElement](#bigdecimalfieldelement) · [BreadcrumbsElement](#breadcrumbselement) · [BreadcrumbsItemElement](#breadcrumbsitemelement) · [ButtonElement](#buttonelement) · [CardElement](#cardelement) · [CheckboxElement](#checkboxelement) · [CheckboxGroupElement](#checkboxgroupelement) · [ComboBoxElement](#comboboxelement) · [ContextMenuElement](#contextmenuelement) · [DatePickerElement](#datepickerelement) · [DateTimePickerElement](#datetimepickerelement) · [DetailsElement](#detailselement) · [DialogElement](#dialogelement) · [EmailFieldElement](#emailfieldelement) · [GridElement](#gridelement) · [IntegerFieldElement](#integerfieldelement) · [ListBoxElement](#listboxelement) · [LoginFormElement](#loginformelement) · [LoginOverlayElement](#loginoverlayelement) · [MarkdownElement](#markdownelement) · [MenuBarElement](#menubarelement) · [MenuElement](#menuelement) · [MenuItemElement](#menuitemelement) · [MessageInputElement](#messageinputelement) · [MessageListElement](#messagelistelement) · [MultiSelectComboBoxElement](#multiselectcomboboxelement) · [NotificationElement](#notificationelement) · [NumberFieldElement](#numberfieldelement) · [PasswordFieldElement](#passwordfieldelement) · [PopoverElement](#popoverelement) · [ProgressBarElement](#progressbarelement) · [RadioButtonGroupElement](#radiobuttongroupelement) · [SelectElement](#selectelement) · [SideNavigationElement](#sidenavigationelement) · [SideNavigationItemElement](#sidenavigationitemelement) · [SliderElement](#sliderelement) · [SplitLayoutElement](#splitlayoutelement) · [SwitchElement](#switchelement) · [TabElement](#tabelement) · [TabSheetElement](#tabsheetelement) · [TabsElement](#tabselement) · [TextAreaElement](#textareaelement) · [TextFieldElement](#textfieldelement) · [TimePickerElement](#timepickerelement) · [TreeGridElement](#treegridelement) · [UploadElement](#uploadelement) · [VirtualListElement](#virtuallistelement)

### AbstractLoginElement

Base abstraction shared by <vaadin-login-form> and <vaadin-login-overlay>.

*abstract* **Extends:** VaadinElement  
**Implements:** HasEnabledElement, HasStyleElement, HasThemeElement  

**Constants:** `String USERNAME_FIELD_ID = "vaadinLoginUsername"`, `String PASSWORD_FIELD_ID = "vaadinLoginPassword"`, `String FORM_WRAPPER_TAG_NAME = "vaadin-login-form-wrapper"`

**Constructors:**

- `AbstractLoginElement(Locator locator)` — Creates a new AbstractLoginElement.

**Methods:**

- `TextFieldElement getUsernameField()` — Get the username field of the login form.
- `PasswordFieldElement getPasswordField()` — Get the password field of the login form.
- `ButtonElement getSubmitButton()` — Get the submit button of the login form.
- `ButtonElement getForgotPasswordButton()` — Get the forgot password button of the login form.
- `void login(String username, String password)` — Fill in the credentials and submit the form.
- `Locator getTitleLocator()` — Locator for the login form title.
- `void assertTitle(String title)` — Assert that the login form title matches the expected text.
- `Locator getErrorLocator()` — Locator for the error message container.
- `Locator getErrorTitleLocator()` — Locator for the error message title.
- `Locator getErrorMessageLocator()` — Locator for the error message description.
- `boolean isErrorVisible()` — Whether the error message is currently shown.
- `void assertErrorVisible()` — Assert that the error message is shown.
- `void assertNoError()` — Assert that no error message is shown.
- `void assertErrorTitle(String title)` — Assert that the error message title matches the expected text.
- `void assertErrorMessage(String message)` — Assert that the error message description matches the expected text.
- `Locator getFooterLocator()` — Locator for the footer, holding the additional information text.
- `void assertAdditionalInformation(String additionalInformation)` — Assert that the footer contains the expected additional information.
- `boolean isDisabled()` — Whether the login form is disabled.
- `Locator getEnabledLocator()` — The disabled property is not reflected to an attribute on the host, so the enabled state is asserted on the submit button, which mirrors it.

### AbstractNumberFieldElement

Base abstraction for Vaadin number-like fields.

*abstract* **Extends:** VaadinElement  
**Implements:** HasValidationPropertiesElement, HasInputFieldElement, HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement  

**Constructors:**

- `AbstractNumberFieldElement(Locator locator)` — Creates a new AbstractNumberFieldElement.

**Methods:**

- `boolean getHasControls()` — Whether the step controls (increase/decrease buttons) are visible.
- `void assertHasControls(boolean hasControls)` — Assert the visibility of step controls.
- `void clickIncreaseButton()` — Click the increase button.
- `void clickDecreaseButton()` — Click the decrease button.

### AccordionElement  `<vaadin-accordion>`

PlaywrightElement for <vaadin-accordion>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-accordion"`

**Constructors:**

- `AccordionElement(Locator locator)` — Create a new AccordionElement.

**Methods:**

- `void assertPanelCount(int count)` — Assert the number of panels present in the accordion.
- `AccordionPanelElement getPanel(String summary)` — Get a panel by its summary text.
- `void openPanel(String summary)` — Open a panel by its summary text.
- `void closePanel(String summary)` — Close a panel by its summary text.
- `boolean isPanelOpened(String summary)` — Whether the panel with the given summary is open.
- `void assertPanelOpened(String summary)` — Assert that the panel with the given summary is open.
- `void assertPanelClosed(String summary)` — Assert that the panel with the given summary is closed.
- `AccordionPanelElement getOpenedPanel()` — Get the currently opened panel.

### AccordionPanelElement  `<vaadin-accordion-panel>`

PlaywrightElement for <vaadin-accordion-panel>.

**Extends:** VaadinElement  
**Implements:** HasDisabledAttributeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-accordion-panel"`, `String FIELD_HEADING_TAG_NAME = "vaadin-accordion-heading"`

**Constructors:**

- `AccordionPanelElement(Locator locator)` — Create a new AccordionPanelElement.

**Static factory methods:**

- `AccordionPanelElement getAccordionPanelBySummary(Locator locator, String summary)` — Get an accordion panel by its summary text within a scope.
- `AccordionPanelElement getOpenedAccordionPanel(Locator locator)` — Get the currently opened accordion panel within a scope.

**Methods:**

- `void assertOpened()` — Assert that the panel is opened.
- `void assertClosed()` — Assert that the panel is closed.
- `boolean isOpen()` — Whether the panel is open.
- `void setOpen(boolean open)` — Set the open state by clicking the summary when needed.
- `Locator getSummaryLocator()` — Locator pointing to the summary heading.
- `String getSummaryText()` — Text content of the summary heading.
- `Locator getContentLocator()` — Locator pointing to the first non-slotted content element.
- `void assertContentVisible()` — Assert that the content area is visible.
- `void assertContentNotVisible()` — Assert that the content area is not visible.

### AppLayoutElement  `<vaadin-app-layout>`

PlaywrightElement for <vaadin-app-layout>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement, HasThemeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-app-layout"`, `String DRAWER_TOGGLE_TAG_NAME = "vaadin-drawer-toggle"`, `String PRIMARY_SECTION_NAVBAR = "navbar"`, `String PRIMARY_SECTION_DRAWER = "drawer"`

**Constructors:**

- `AppLayoutElement(Locator locator)` — Create a new AppLayoutElement.

**Static factory methods:**

- `AppLayoutElement get(Page page)` — Get the first AppLayoutElement on the page.
- `AppLayoutElement get(Locator parent)` — Get the first AppLayoutElement within a parent locator.
- `AppLayoutElement getById(Page page, String id)` — Get an AppLayoutElement by its id attribute.

**Methods:**

- `Locator getNavbarLocator()` — Locator for the content slotted into the top navbar.
- `Locator getBottomNavbarLocator()` — Locator for the content slotted into the bottom navbar.
- `Locator getDrawerLocator()` — Locator for the content slotted into the drawer.
- `Locator getContentLocator()` — Locator for the content area, that is, the children without a slot.
- `Locator getBackdropLocator()` — Locator for the backdrop shown behind an opened drawer in overlay mode.
- `ButtonElement getDrawerToggle()` — Get the drawer toggle button of this layout.
- `void toggleDrawer()` — Click the drawer toggle, flipping the drawer state.
- `boolean isDrawerOpened()` — Whether the drawer is opened.
- `void openDrawer()` — Open the drawer by clicking the drawer toggle, unless it is already opened.
- `void closeDrawer()` — Close the drawer, unless it is already closed.
- `void setDrawerOpened(boolean opened)` — Set the drawer state, opening or closing it as needed.
- `void clickBackdrop()` — Click the backdrop, which closes the drawer in overlay mode.
- `void closeDrawerWithEscape()` — Press Escape, which closes the drawer in overlay mode.
- `void assertDrawerOpened()` — Assert that the drawer is opened.
- `void assertDrawerClosed()` — Assert that the drawer is closed.
- `boolean isOverlayMode()` — Whether the drawer is displayed as an overlay on top of the content, which the component enables on small viewports.
- `void assertOverlayMode()` — Assert that the layout is in overlay mode.
- `void assertNotOverlayMode()` — Assert that the layout is not in overlay mode.
- `String getPrimarySection()` — Get the primary section, that is, the area that comes first visually.
- `void assertPrimarySection(String primarySection)` — Assert the primary section of the layout.
- `void assertHasDrawer()` — Assert that the layout has content slotted into the drawer.
- `void assertHasNavbar()` — Assert that the layout has content slotted into the navbar.

### AvatarElement  `<vaadin-avatar>`

PlaywrightElement for <vaadin-avatar>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasStyleElement, HasThemeElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-avatar"`

**Constructors:**

- `AvatarElement(Locator locator)` — Create a new AvatarElement.

**Static factory methods:**

- `AvatarElement get(Page page)` — Get the first AvatarElement on the page.
- `AvatarElement get(Locator locator)` — Get the first AvatarElement within a scope.
- `AvatarElement getByName(Page page, String name)` — Get an AvatarElement by its name attribute.
- `AvatarElement getByName(Locator locator, String name)` — Get an AvatarElement by its name attribute within a scope.

**Methods:**

- `String getName()` — Get the avatar's name.
- `void setName(String name)` — Set the avatar's name.
- `String getAbbreviation()` — Get the displayed abbreviation.
- `void setAbbreviation(String abbr)` — Set the abbreviation.
- `String getImage()` — Get the image URL.
- `void setImage(String img)` — Set the image URL.
- `Integer getColorIndex()` — Get the background color index.
- `void setColorIndex(int colorIndex)` — Set the background color index.
- `void assertName(String name)` — Assert the avatar's name property value.
- `void assertAbbreviation(String abbr)` — Assert the avatar's abbreviation.
- `void assertHasImage()` — Assert that the avatar has an image set.
- `void assertHasNoImage()` — Assert that the avatar has no image set.

### AvatarGroupElement  `<vaadin-avatar-group>`

PlaywrightElement for <vaadin-avatar-group>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement, HasThemeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-avatar-group"`, `String FIELD_MENU_TAG_NAME = "vaadin-avatar-group-menu"`, `String FIELD_MENU_ITEM_TAG_NAME = "vaadin-avatar-group-menu-item"`

**Constructors:**

- `AvatarGroupElement(Locator locator)` — Create a new AvatarGroupElement.

**Static factory methods:**

- `AvatarGroupElement get(Page page)` — Get the first AvatarGroupElement on the page.
- `AvatarGroupElement get(Locator locator)` — Get the first AvatarGroupElement within a scope.

**Methods:**

- `Locator getAvatarsLocator()` — Locator matching every visible avatar, in rendering order.
- `List<AvatarElement> getAvatars()` — Get the visible avatars, in rendering order.
- `AvatarElement getAvatar(int index)` — Get the avatar at the given position among the visible avatars.
- `int getVisibleCount()` — Get the number of visible avatars, excluding the overflow avatar.
- `List<String> getNames()` — Get the names of the visible avatars, in rendering order.
- `Integer getMaxItemsVisible()` — Get the maximum number of avatars the group displays before overflowing.
- `void setMaxItemsVisible(int maxItemsVisible)` — Set the maximum number of avatars to display before overflowing.
- `AvatarElement getOverflowAvatar()` — Get the overflow avatar, the one summarising the hidden items as +N.
- `boolean hasOverflow()` — Whether some avatars are hidden behind the overflow avatar.
- `void openOverflow()` — Open the overflow overlay by clicking the overflow avatar, and wait until it is open.
- `void closeOverflow()` — Close the overflow overlay with the Escape key, and wait until it is closed.
- `boolean isOverflowOpen()` — Whether the overflow overlay is currently open.
- `Locator getOverflowMenuLocator()` — Locator for the overflow overlay menu, which holds one menu item per hidden avatar.
- `Locator getOverflowAvatarsLocator()` — Locator matching every avatar inside the overflow overlay, in rendering order.
- `List<AvatarElement> getOverflowAvatars()` — Get the avatars hidden behind the overflow avatar, in rendering order.
- `List<String> getOverflowNames()` — Get the names of the avatars hidden behind the overflow avatar, in rendering order.
- `void assertNames(String... names)` — Assert the names of the visible avatars, in order.
- `void assertOverflowNames(String... names)` — Assert the names of the avatars hidden behind the overflow avatar, in order.
- `void assertVisibleCount(int count)` — Assert the number of visible avatars, excluding the overflow avatar.
- `void assertHasOverflow()` — Assert that some avatars are hidden behind the overflow avatar.
- `void assertHasNoOverflow()` — Assert that no avatar is hidden behind the overflow avatar.
- `void assertOverflowOpen()` — Assert that the overflow overlay is open.
- `void assertOverflowClosed()` — Assert that the overflow overlay is closed.

### BadgeElement  `<vaadin-badge>`

PlaywrightElement for <vaadin-badge>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-badge"`

**Constructors:**

- `BadgeElement(Locator locator)` — Create a new BadgeElement.

**Static factory methods:**

- `BadgeElement get(Page page)` — Get the first BadgeElement on the page.
- `BadgeElement get(Locator locator)` — Get the first BadgeElement within a scope.
- `BadgeElement getByText(Page page, String text)` — Get a BadgeElement by its text content.
- `BadgeElement getByText(Locator locator, String text)` — Get a BadgeElement by its text content within a scope.

**Methods:**

- `Locator getIconLocator()` — Locator for the content of the icon slot.
- `String getText()` — Get the badge's own text content.
- `Integer getNumber()` — Get the number displayed by the badge.
- `void assertText(String text)` — Assert the badge's text content.
- `void assertNumber(Integer number)` — Assert the badge's number.
- `void assertHasIcon()` — Assert that the badge has content in its icon slot.
- `void assertHasNoIcon()` — Assert that the badge has no content in its icon slot.

### BigDecimalFieldElement  `<vaadin-big-decimal-field>`

PlaywrightElement for <vaadin-big-decimal-field>.

**Extends:** VaadinElement  
**Implements:** HasValidationPropertiesElement, HasInputFieldElement, HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-big-decimal-field"`

**Constructors:**

- `BigDecimalFieldElement(Locator locator)` — Create a new BigDecimalFieldElement.

**Static factory methods:**

- `BigDecimalFieldElement getByLabel(Page page, String label)` — Get the BigDecimalFieldElement by its label.
- `BigDecimalFieldElement getByLabel(Locator locator, String label)` — Get the BigDecimalFieldElement by its label within a given scope.

### BreadcrumbsElement  `<vaadin-breadcrumbs>`

PlaywrightElement for <vaadin-breadcrumbs>.

**Extends:** VaadinElement  
**Implements:** HasAriaLabelElement, HasStyleElement, HasThemeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-breadcrumbs"`

**Constructors:**

- `BreadcrumbsElement(Locator locator)` — Create a new BreadcrumbsElement.

**Static factory methods:**

- `BreadcrumbsElement get(Page page)` — Get the first <vaadin-breadcrumbs> on the page.
- `BreadcrumbsElement get(Locator locator)` — Get the first <vaadin-breadcrumbs> within a locator scope.
- `BreadcrumbsElement getByLabel(Page page, String label)` — Get the <vaadin-breadcrumbs> by its accessible name, using the ARIA role navigation.

**Methods:**

- `Locator getItemsLocator()` — Locator for every <vaadin-breadcrumbs-item> of the trail, including the items collapsed into the overflow overlay.
- `List<BreadcrumbsItemElement> getItems()` — Get every item of the trail, in trail order, including the items collapsed into the overflow overlay.
- `BreadcrumbsItemElement getItem(int index)` — Get the item at the given position in the trail.
- `BreadcrumbsItemElement getItem(String text)` — Get the item showing the given text.
- `BreadcrumbsItemElement getCurrentItem()` — Get the item marked as the current page, that is, the last item of the trail when it has no path.
- `void assertItemCount(int count)` — Assert that the trail holds exactly the expected number of items.
- `void assertItemTexts(String... texts)` — Assert that the trail shows exactly the given texts, in trail order.
- `Locator getOverflowButtonLocator()` — Locator for the button that reveals the collapsed items.
- `boolean hasOverflow()` — Whether one or more items are collapsed into the overflow overlay.
- `void assertHasOverflow()` — Assert that items are collapsed into the overflow overlay.
- `void assertHasNoOverflow()` — Assert that the whole trail fits and nothing is collapsed.
- `Locator getOverflowItemsLocator()` — Locator for the items collapsed into the overflow overlay.
- `List<BreadcrumbsItemElement> getOverflowItems()` — Get the items collapsed into the overflow overlay, in trail order.
- `boolean isOverflowOpen()` — Whether the overflow overlay is open.
- `void openOverflow()` — Open the overflow overlay, revealing the collapsed items.
- `void closeOverflow()` — Close the overflow overlay by pressing Escape.
- `void assertOverflowOpen()` — Assert that the overflow overlay is open.
- `void assertOverflowClosed()` — Assert that the overflow overlay is closed.
- `void assertOverflowButtonAriaLabel(String label)` — Assert the accessible name of the overflow button, which comes from BreadcrumbsI18n.moreItems.

### BreadcrumbsItemElement  `<vaadin-breadcrumbs-item>`

PlaywrightElement for <vaadin-breadcrumbs-item>, a single entry of a BreadcrumbsElement trail.

**Extends:** VaadinElement  
**Implements:** HasDisabledAttributeElement, HasPrefixElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-breadcrumbs-item"`

**Constructors:**

- `BreadcrumbsItemElement(Locator locator)` — Create a new BreadcrumbsItemElement.

**Static factory methods:**

- `BreadcrumbsItemElement getByText(Page page, String text)` — Get the first <vaadin-breadcrumbs-item> with the given text on the page.
- `BreadcrumbsItemElement getByText(Locator locator, String text)` — Get the first <vaadin-breadcrumbs-item> with the given text within a locator scope.

**Methods:**

- `void assertText(String text)` — Assert that the item shows the expected text.
- `String getPath()` — Get the path the item links to.
- `void assertPath(String path)` — Assert the path the item links to.
- `Locator getLinkLocator()` — Locator for the <a part="link"> rendered when the item has a path.
- `boolean isLink()` — Whether the item renders as a link, which is the case when it has a path.
- `void assertLink()` — Assert that the item renders as a link.
- `void assertNotLink()` — Assert that the item renders as a non-link.
- `void click()` — Click the item.
- `boolean isCurrent()` — Whether the item represents the current page.
- `void assertCurrent()` — Assert that the item represents the current page, both through the current host attribute and aria-current="page" on its [part='nolink'] element.
- `void assertNotCurrent()` — Assert that the item does not represent the current page.
- `boolean hasPrefix()` — Whether the item has content in its prefix slot.
- `void assertHasPrefix()` — Assert that the item has content in its prefix slot.
- `void assertHasNoPrefix()` — Assert that the item has no content in its prefix slot.

### ButtonElement  `<vaadin-button>`

PlaywrightElement for <vaadin-button>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasEnabledElement, HasPrefixElement, HasStyleElement, HasSuffixElement, HasThemeElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-button"`

**Constructors:**

- `ButtonElement(Locator locator)` — Create a new ButtonElement.

**Static factory methods:**

- `ButtonElement getByText(Page page, String text)` — Get a ButtonElement by its accessible name or visible text.
- `ButtonElement getByText(Page page, Page.GetByRoleOptions options)` — Get a ButtonElement by its accessible name with custom role options.
- `ButtonElement getByText(Locator locator, String text)` — Get a ButtonElement by its accessible name or visible text within a scope.
- `ButtonElement getByText(Locator locator, Locator.GetByRoleOptions options)` — Get a ButtonElement by its accessible name with custom role options within a scope.
- `ButtonElement getByLabel(Page page, String text)` — Alias for #getByText(Page, String).

### CardElement  `<vaadin-card>`

PlaywrightElement for <vaadin-card>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-card"`

**Constructors:**

- `CardElement(Locator locator)` — Create a new CardElement.

**Static factory methods:**

- `CardElement getByTitle(Page page, String title)` — Get a card by title
- `CardElement getByTitle(Locator locator, String title)` — Get a card by title

**Methods:**

- `Locator getTitleLocator()` — Locator for the title slot.
- `Locator getSubtitleLocator()` — Locator for the subtitle slot.
- `Locator getHeaderLocator()` — Locator for the header slot.
- `Locator getHeaderPrefixLocator()` — Locator for the header prefix slot.
- `Locator getHeaderSuffixLocator()` — Locator for the header suffix slot.
- `Locator getMediaLocator()` — Locator for the media slot.
- `Locator getFooterLocator()` — Locator for the footer slot.
- `Locator getContentLocator()` — Locator for the default (content) slot.
- `void assertTitle(String title)` — Assert the card title text, or absence when null.
- `void assertSubtitle(String subtitle)` — Assert the card subtitle text, or absence when null.

### CheckboxElement  `<vaadin-checkbox>`

PlaywrightElement for <vaadin-checkbox>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement, HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-checkbox"`

**Constructors:**

- `CheckboxElement(Locator locator)` — Create a new CheckboxElement.

**Static factory methods:**

- `CheckboxElement getByLabel(Page page, String label)` — Get a CheckboxElement by its accessible label.
- `CheckboxElement getByLabel(Locator locator, String label)` — Get a CheckboxElement by its accessible label within a given scope.

**Methods:**

- `void isChecked(boolean checked)` *(deprecated)* — Check or uncheck the checkbox.
- `boolean isIndeterminate()` — Whether the checkbox is in indeterminate state.
- `void assertIndeterminate()` — Assert that the checkbox is indeterminate.
- `void assertNotIndeterminate()` — Assert that the checkbox is not indeterminate.
- `void setIndeterminate(boolean indeterminate)` — Set the indeterminate state.

### CheckboxGroupElement  `<vaadin-checkbox-group>`

PlaywrightElement for <vaadin-checkbox-group>.

**Extends:** VaadinElement  
**Implements:** HasLabelElement, HasHelperElement, HasValidationPropertiesElement, HasEnabledElement, HasThemeElement, HasStyleElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-checkbox-group"`

**Constructors:**

- `CheckboxGroupElement(Locator locator)` — Create a new CheckboxGroupElement.

**Static factory methods:**

- `CheckboxGroupElement getByLabel(Page page, String label)` — Get a CheckboxGroupElement by its accessible label.
- `CheckboxGroupElement getByLabel(Locator locator, String label)` — Get a CheckboxGroupElement by its accessible label within a given scope.

**Methods:**

- `List<CheckboxElement> getCheckboxes()` — Get all checkboxes of the group, in DOM order.
- `CheckboxElement getCheckbox(String label)` — Get a single checkbox of the group by its label.
- `int getCheckboxCount()` — Count the checkboxes of the group.
- `void assertCheckboxCount(int expected)` — Assert that the group contains exactly the expected number of checkboxes.
- `void selectByLabel(String... labels)` — Select the checkboxes matching the given labels.
- `void deselectByLabel(String... labels)` — Deselect the checkboxes matching the given labels.
- `void deselectAll()` — Deselect every checkbox of the group.
- `List<String> getSelectedValues()` — Get the labels of the currently selected checkboxes, in DOM order.
- `void assertSelected(String... labels)` — Assert that exactly the checkboxes with the given labels are selected.

### ComboBoxElement  `<vaadin-combo-box>`

PlaywrightElement for <vaadin-combo-box>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasInputFieldElement, HasPrefixElement, HasThemeElement, HasPlaceholderElement, HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement, HasClearButtonElement, HasAllowedCharPatternElement, HasReadOnlyElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-combo-box"`, `String FIELD_ITEM_TAG_NAME = "vaadin-combo-box-item"`

**Constructors:**

- `ComboBoxElement(Locator locator)` — Create a new ComboBoxElement.

**Static factory methods:**

- `ComboBoxElement getByLabel(Page page, String label)` — Get the ComboBoxElement by its label.
- `ComboBoxElement getByLabel(Locator locator, String label)` — Get the ComboBoxElement by its label within a given scope.

**Methods:**

- `String getValue()` — Get the selected value label.
- `void assertValue(String expected)` — Assert that the displayed value equals the expected string.
- `void setValue(String value)` — Select the item whose label equals value, by typing it as a filter and closing the overlay once the matching item is shown.
- `void selectItem(String item)` — Select an item by its visible label.
- `void filterAndSelectItem(String filter, String item)` — Type filter text into the input, then click the matching item.
- `void setFilter(String filter)` — Type into the input to trigger filtering.
- `String getFilter()` — Get the current filter value from the DOM property.
- `void open()` — Open the combo box overlay.
- `void close()` — Close the combo box overlay.
- `boolean isOpened()` — Whether the overlay is currently open.
- `void assertOpened()` — Assert that the combo box overlay is open.
- `void assertClosed()` — Assert that the combo box overlay is closed.
- `Locator getToggleButtonLocator()` — Locator for the toggle button part.
- `void clickToggleButton()` — Click the dropdown toggle button.
- `int getOverlayItemCount()` — Count visible overlay items.
- `void assertItemCount(int expected)` — Assert that the overlay contains exactly the expected number of items.

### ContextMenuElement

PlaywrightElement for context menu overlays <vaadin-context-menu-overlay>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-context-menu"`, `String FIELD_LIST_BOX_TAG_NAME = "vaadin-context-menu-list-box"`

**Constructors:**

- `ContextMenuElement(Page page)` — Create a ContextMenuElement from the page overlay.
- `ContextMenuElement(Locator locator)` — Create a ContextMenuElement from an existing locator.

**Static factory methods:**

- `void openOn(Locator target)` — Open the context menu by invoking a context-click on the provided target.

**Methods:**

- `void assertOpen()` — Assert that the context menu overlay is open.
- `void assertClosed()` — Assert that the context menu overlay is closed or hidden.
- `ContextMenuElement openSubMenu(String itemLabel)` — Open a submenu and return its overlay.
- `void selectItem(String itemLabel)` — Select a menu item by its accessible name.
- `void assertItemDisabled(String itemLabel)` — Assert that a menu item is disabled.
- `void assertItemEnabled(String itemLabel)` — Assert that a menu item is enabled.
- `Locator getListBoxLocator()` — Locator for the context menu list box.
- `void assertItemChecked(String itemLabel)` — Assert that a checkable menu item is checked.
- `void assertItemNotChecked(String itemLabel)` — Assert that a checkable menu item is not checked.

### DatePickerElement  `<vaadin-date-picker>`

PlaywrightElement for <vaadin-date-picker>.

**Extends:** VaadinElement  
**Implements:** HasInputFieldElement, HasValidationPropertiesElement, HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-date-picker"`

**Constructors:**

- `DatePickerElement(Locator locator)` — Create a new DatePickerElement.

**Static factory methods:**

- `DatePickerElement getByLabel(Page page, String label)` — Get the DatePickerElement by its label.
- `DatePickerElement getByLabel(Locator locator, String label)` — Get the DatePickerElement by its label within a given scope.

**Methods:**

- `void setValue(LocalDate date)` — Set the value using a LocalDate formatted as ISO-8601.
- `LocalDate getValueAsLocalDate()` — Get the current value as a LocalDate.
- `void setValue(String value)` — Set the value of the input.
- `void assertValue(String value)` — Assert that the input value equals the provided string.
- `void assertValue(LocalDate value)` — Assert that the value equals the provided date.

### DateTimePickerElement  `<vaadin-date-time-picker>`

PlaywrightElement for <vaadin-date-time-picker>.

**Extends:** VaadinElement  
**Implements:** HasInputFieldElement, HasValidationPropertiesElement, HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-date-time-picker"`, `DateTimeFormatter ISO_LOCAL_DATE_TIME = …`

**Constructors:**

- `DateTimePickerElement(Locator locator)` — Create a new DateTimePickerElement.

**Static factory methods:**

- `DateTimePickerElement getByLabel(Page page, String label)` — Get the DateTimePickerElement by its label.
- `DateTimePickerElement getByLabel(Locator locator, String label)` — Get the DateTimePickerElement by its label within a given scope.

**Methods:**

- `void setValue(LocalDateTime date)` — Set the value using a LocalDateTime.
- `LocalDateTime getValueAsLocalDateTime()` — Get the current value as a LocalDateTime.
- `void setValue(String value)` — Set the value of the input.
- `void assertValue(String value)` — Assert that the input value equals the provided string.
- `void assertValue(LocalDateTime value)` — Assert that the value equals the provided date-time.
- `void setDate(String date)` — Set only the date part (string input) and dispatch change events.
- `void setTime(String date)` — Set only the time part (string input) and dispatch change events.
- `void assertDateValue(String date)` — Assert the date sub-field value equals the expected string.
- `void assertTimeValue(String time)` — Assert the time sub-field value equals the expected string.

### DetailsElement  `<vaadin-details>`

PlaywrightElement for <vaadin-details>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement, HasThemeElement, HasTooltipElement, HasDisabledAttributeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-details"`

**Constructors:**

- `DetailsElement(Locator locator)` — Create a new DetailsElement.

**Static factory methods:**

- `DetailsElement getBySummaryText(Page page, String summary)` — Get a details component by its summary text.

**Methods:**

- `void assertOpened()` — Assert that the details is opened.
- `void assertClosed()` — Assert that the details is closed.
- `boolean isOpen()` — Whether the details is opened.
- `void setOpen(boolean open)` — Set the opened state by clicking the summary when necessary.
- `Locator getSummaryLocator()` — Locator for the summary element.
- `String getSummaryText()` — Text of the summary element.
- `Locator getContentLocator()` — Locator for the currently visible content container.
- `void assertContentVisible()` — Assert that the content is visible.
- `void assertContentNotVisible()` — Assert that the content is not visible.

### DialogElement

PlaywrightElement for <vaadin-dialog>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-dialog"`

**Constructors:**

- `DialogElement(Page page)` — Create a DialogElement by resolving the dialog with ARIA role.
- `DialogElement(Locator locator)` — Create a DialogElement from an existing locator.

**Static factory methods:**

- `DialogElement getByHeaderText(Page page, String summary)` — Get a dialog by its header text (accessible name).

**Methods:**

- `void closeWithEscape()` — Close the dialog using the Escape key.
- `Locator getOverlayLocator()` — Locator for the overlay rendered in the dialog's shadow DOM.
- `boolean isOpen()` — Whether the dialog is open (visible).
- `void assertOpen()` — Assert that the dialog is open.
- `boolean isVisible()` — Whether the dialog (its overlay) is visible.
- `void assertVisible()` — Assert that the dialog overlay is visible.
- `void assertHidden()` — Assert that the dialog overlay is hidden.
- `boolean isModal()` — Whether the dialog is modal (i.e. not modeless).
- `void assertModal()` — Assert that the dialog is modal.
- `void assertModeless()` — Assert that the dialog is modeless.
- `void assertClosed()` — Assert that the dialog is closed (its overlay is hidden).
- `String getHeaderText()` — Get the header text from the title slot.
- `void assertHeaderText(String headerText)` — Assert the header text matches.
- `Locator getHeaderLocator()` — Locator for the header content slot.
- `Locator getContentLocator()` — Locator for the dialog content (first non-slotted child).
- `Locator getFooterLocator()` — Locator for the footer slot.

### EmailFieldElement  `<vaadin-email-field>`

PlaywrightElement for <vaadin-email-field>.

**Extends:** TextFieldElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-email-field"`

**Constructors:**

- `EmailFieldElement(Locator locator)` — Create a new EmailFieldElement.

**Static factory methods:**

- `EmailFieldElement getByLabel(Page page, String label)` — Get the EmailFieldElement by its label.
- `EmailFieldElement getByLabel(Locator locator, String label)` — Get the EmailFieldElement by its label within a given scope.

### GridElement  `<vaadin-grid>`

PlaywrightElement for <vaadin-grid>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasStyleElement, HasThemeElement, HasEnabledElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-grid"`

**Constructors:**

- `GridElement(Locator locator)` — Create a new GridElement.

**Static factory methods:**

- `GridElement get(Page page)` — Get the first GridElement on the page.
- `GridElement get(Locator parent)` — Get the first GridElement within a parent locator.
- `GridElement getById(Page page, String id)` — Get a GridElement by its id attribute.

**Methods:**

- `int getRenderedRowCount()` — Get the number of rows currently rendered in the DOM.
- `int getTotalRowCount()` — Get the total number of rows (data items) in the grid.
- `int getColumnCount()` — Get the number of visible (non-hidden) columns.
- `boolean isAllRowsVisible()` — Whether the grid has allRowsVisible enabled.
- `boolean isMultiSort()` — Whether the grid has multi-sort enabled.
- `boolean isColumnReorderingAllowed()` — Whether column reordering is allowed.
- `Optional<HeaderCellElement> findHeaderCell(int columnIndex)` — Find a header cell by column index.
- `Optional<HeaderCellElement> findHeaderCell(int headerRowIndex, int columnIndex)` — Find a header cell by header row index and column index.
- `Optional<HeaderCellElement> findHeaderCellByText(String text)` — Find a header cell by its text content.
- `Optional<HeaderCellElement> findHeaderCellByText(int headerRowIndex, String text)` — Find a header cell by header row index and text content.
- `List<String> getHeaderCellContents()` — Get the text content of all visible header cells.
- `Optional<FooterCellElement> findFooterCell(int columnIndex)` — Find a footer cell by column index.
- `Optional<FooterCellElement> findFooterCell(int footerRowIndex, int columnIndex)` — Find a footer cell by footer row index and column index.
- `Optional<FooterCellElement> findFooterCellByText(String text)` — Find a footer cell by its text content.
- `Optional<FooterCellElement> findFooterCellByText(int footerRowIndex, String text)` — Find a footer cell by footer row index and text content.
- `List<String> getFooterCellContents()` — Get the text content of all visible footer cells.
- `Optional<CellElement> findCell(int row, int column)` — Find a body cell by row index and column index.
- `Optional<CellElement> findCell(int row, String columnHeaderText)` — Find a body cell by row index and column header text.
- `List<Integer> findRowIndexesWithColumnText(int columnIndex, String text)` — Find row indexes where the cell in the given column has the given text.
- `Optional<RowElement> findRow(int rowIndex)` — Find a row by its index.
- `void scrollToRow(int rowIndex)` — Scroll the grid so that the given row index becomes visible.
- `void scrollToStart()` — Scroll to the very beginning of the grid.
- `void scrollToEnd()` — Scroll to the very end of the grid.
- `void select(int rowIndex)` — Select a row by id.
- `void deselect(int rowIndex)` — Deselect a row by id.
- `int getSelectedItemCount()` — Get the number of currently selected items.
- `boolean isSelectAllChecked()` — Check if the select-all checkbox is checked.
- `boolean isSelectAllIndeterminate()` — Check if the select-all checkbox is indeterminate.
- `boolean isSelectAllUnchecked()` — Check if the select-all checkbox is unchecked.
- `void checkSelectAll()` — Check the select-all checkbox.
- `void uncheckSelectAll()` — Uncheck the select-all checkbox.
- `void waitForGridToStopLoading()` — Wait for the grid to finish loading after a scroll or other action that triggers loading of new rows.
- `boolean isRowInView(int rowIndex)` — Whether the row with the given index is currently scrolled into view (at least partially visible between the header and footer), without triggering any scrolling.
- `void assertRowCount(int expected)` — Assert that the grid has the given total number of rows (data items).
- `void assertEmpty()` — Assert that the grid has no rows.
- `void assertColumnCount(int expected)` — Assert that the grid has the given number of visible (non-hidden) columns.
- `void assertAllRowsVisible()` — Assert that allRowsVisible is enabled.
- `void assertNotAllRowsVisible()` — Assert that allRowsVisible is not enabled.
- `void assertMultiSort()` — Assert that multi-sort is enabled.
- `void assertNotMultiSort()` — Assert that multi-sort is not enabled.
- `void assertColumnReorderingAllowed()` — Assert that column reordering is allowed.
- `void assertColumnReorderingNotAllowed()` — Assert that column reordering is not allowed.
- `void assertCellContent(int row, int column, String expected)` — Assert that the body cell at the given row and column has the given text content.
- `void assertCellContent(int row, String columnHeaderText, String expected)` — Assert that the body cell at the given row and column header has the given text content.
- `void assertCellPresent(int row, int column)` — Assert that a cell exists at the given row and column.
- `void assertCellNotPresent(int row, int column)` — Assert that no cell exists at the given row and column.
- `void assertHeaderCellContents(String... expected)` — Assert that the visible header cells have exactly the given text contents, in order.
- `void assertHeaderCell(int column, String expected)` — Assert that the header cell at the given column has the given text content.
- `void assertColumnPresent(String headerText)` — Assert that a column with the given header text exists.
- `void assertColumnNotPresent(String headerText)` — Assert that no column with the given header text exists.
- `void assertFooterCellContents(String... expected)` — Assert that the visible footer cells have exactly the given text contents, in order.
- `void assertFooterCell(int column, String expected)` — Assert that the footer cell at the given column has the given text content.
- `void assertFooterPresent(String footerText)` — Assert that a footer cell with the given text exists.
- `void assertFooterNotPresent(String footerText)` — Assert that no footer cell with the given text exists.
- `void assertRowPresent(int rowIndex)` — Assert that a row exists at the given index (auto-scrolling if necessary).
- `void assertRowNotPresent(int rowIndex)` — Assert that no row exists at the given index.
- `void assertRowInView(int rowIndex)` — Assert that the row with the given index is currently scrolled into view.
- `void assertRowNotInView(int rowIndex)` — Assert that the row with the given index is not currently scrolled into view.
- `void assertRowIndexesWithColumnText(int column, String text, Integer... expected)` — Assert that the cells in the given column with the given text appear at exactly the given row indexes.
- `void assertSelectedItemCount(int expected)` — Assert that the given number of items are currently selected.
- `void assertRowSelected(int rowIndex)` — Assert that the row at the given index is selected.
- `void assertRowNotSelected(int rowIndex)` — Assert that the row at the given index is not selected.
- `void assertSelectAllChecked()` — Assert that the select-all checkbox is checked.
- `void assertSelectAllUnchecked()` — Assert that the select-all checkbox is unchecked.
- `void assertSelectAllIndeterminate()` — Assert that the select-all checkbox is indeterminate.
- `void assertDetailsOpen(int rowIndex)` — Assert that the details panel of the row at the given index is open.
- `void assertDetailsClosed(int rowIndex)` — Assert that the details panel of the row at the given index is closed.

#### GridElement.CellElement

Represents a cell in the grid, providing access to the table cell (td or th), the cell content (vaadin-grid-cell-content) and the column index.

- `Locator getTableCellLocator()` — Get the locator for the table cell (td or th).
- `int getColumnIndex()` — Get the column index (0-based) of this cell.
- `Locator getCellContentLocator()` — Get the locator for the cell content (vaadin-grid-cell-content) assigned to this cell.
- `String getText()` — Get the rendered text of the cell content.
- `void assertText(String expected)` — Assert that the cell content has the given text.
- `String getContentSlotName()` — Get the name of the slot used for the cell content.
- `void click()` — Click the cell content.

#### GridElement.HeaderCellElement

Represents a header cell in the grid, providing access to the table cell (th), the cell content and sorting.

- `boolean isSortable()` — Whether the header cell supports sorting.
- `void clickSort()` — Click the header cell sorter to sort the column.
- `boolean isSortAscending()` — Whether the column is currently sorted in ascending order.
- `boolean isSortDescending()` — Whether the column is currently sorted in descending order.
- `boolean isNotSorted()` — Whether the column is currently not sorted.
- `void assertSortAscending()` — Assert that the column is sorted in ascending order.
- `void assertSortDescending()` — Assert that the column is sorted in descending order.
- `void assertNotSorted()` — Assert that the column is not sorted.
- `void assertSortable()` — Assert that the header cell supports sorting.
- `void assertNotSortable()` — Assert that the header cell does not support sorting.

#### GridElement.FooterCellElement

Represents a footer cell in the grid, providing access to the table cell (td) and the cell content.


#### GridElement.RowElement

Represents a row in the grid, providing access to the row locator, row index, and methods for accessing cells and interacting with the row (selection, details).

- `Locator getRowLocator()` — Get the locator for the row (tr).
- `int getRowIndex()` — Get the row index (0-based) of this row.
- `CellElement getCell(int columnIndex)` — Get the cell element for the given column index in this row.
- `CellElement getCell(String columnHeaderText)` — Get the cell element for the given column header text in this row.
- `CellElement getDetailsCell()` — Get the cell element for the details column in this row.
- `boolean isSelected()` — Whether this row is selected.
- `void select()` — Select this row.
- `void deselect()` — Deselect this row.
- `void openDetails()` — Whether the details for this row are open.
- `void closeDetails()` — Close the details for this row.
- `boolean isDetailsOpen()` — Whether the details for this row are open.
- `void assertSelected()` — Assert that this row is selected.
- `void assertNotSelected()` — Assert that this row is not selected.
- `void assertDetailsOpen()` — Assert that this row's details panel is open.
- `void assertDetailsClosed()` — Assert that this row's details panel is closed.

### IntegerFieldElement  `<vaadin-integer-field>`

PlaywrightElement for <vaadin-integer-field>.

**Extends:** AbstractNumberFieldElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-integer-field"`

**Constructors:**

- `IntegerFieldElement(Locator locator)` — Creates a new IntegerFieldElement.

**Static factory methods:**

- `IntegerFieldElement getByLabel(Page page, String label)` — Get the IntegerFieldElement by its label.
- `IntegerFieldElement getByLabel(Locator locator, String label)` — Get the IntegerFieldElement by its label within a given scope.

**Methods:**

- `Integer getStep()` — Get the current step value.
- `void setStep(int step)` — Set the step value.
- `void assertStep(Integer step)` — Assert that the step attribute matches the expected value.
- `Integer getMin()` — Get the current min value.
- `void setMin(int min)` — Set the min value.
- `void assertMin(Integer min)` — Assert that the min attribute matches the expected value.
- `Integer getMax()` — Get the current max value.
- `void setMax(int max)` — Set the max value.
- `void assertMax(Integer max)` — Assert that the max attribute matches the expected value.

### ListBoxElement  `<vaadin-list-box>`

PlaywrightElement for <vaadin-list-box>.

**Extends:** VaadinElement  
**Implements:** HasAriaLabelElement, HasStyleElement, HasTooltipElement, HasDisabledAttributeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-list-box"`, `String FIELD_ITEM_TAG_NAME = "vaadin-item"`, `String MULTIPLE_ATTRIBUTE = "multiple"`

**Constructors:**

- `ListBoxElement(Locator locator)` — Create a new ListBoxElement.

**Static factory methods:**

- `ListBoxElement getByLabel(Page page, String label)` — Get the ListBoxElement by its label.

**Methods:**

- `void selectItem(String item)` — Select the item based on its text.
- `String getSingleSelectedValue()` — Get the selected value for single-select list boxes.
- `List<String> getSelectedValue()` — Get all selected values for multi-select list boxes.
- `void assertSelectedValue(String... expected)` — Assert that the selected values match the expected labels.
- `void assertItemEnabled(String item)` — Assert that a specific item is enabled.
- `void assertItemDisabled(String item)` — Assert that a specific item is disabled.
- `boolean isMultiple()`
- `void assertMultiple()` — Assert that multiple selection is enabled.
- `void assertSingle()` — Assert that single selection mode is enabled.

### LoginFormElement  `<vaadin-login-form>`

PlaywrightElement for <vaadin-login-form>.

**Extends:** AbstractLoginElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-login-form"`

**Constructors:**

- `LoginFormElement(Locator locator)` — Creates a new LoginFormElement.

**Static factory methods:**

- `LoginFormElement get(Page page)` — Get the first LoginFormElement on the page.
- `LoginFormElement get(Locator locator)` — Get the first LoginFormElement within a scope.

### LoginOverlayElement  `<vaadin-login-overlay>`

PlaywrightElement for <vaadin-login-overlay>.

**Extends:** AbstractLoginElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-login-overlay"`, `String OVERLAY_TAG_NAME = "vaadin-login-overlay-wrapper"`

**Constructors:**

- `LoginOverlayElement(Page page)` — Create a LoginOverlayElement by resolving the overlay with its ARIA role.
- `LoginOverlayElement(Locator locator)` — Creates a new LoginOverlayElement.

**Static factory methods:**

- `LoginOverlayElement get(Page page)` — Get the first LoginOverlayElement on the page.
- `LoginOverlayElement getByTitle(Page page, String title)` — Get a LoginOverlayElement by its application title, which is the overlay's accessible name.

**Methods:**

- `Locator getOverlayLocator()` — Locator for the overlay rendered in the component's shadow DOM.
- `boolean isOpen()` — Whether the overlay is open (visible).
- `void assertOpen()` — Assert that the overlay is open.
- `void assertClosed()` — Assert that the overlay is closed (its content is no longer visible).
- `boolean isVisible()` — Whether the overlay (its content) is visible.
- `void assertVisible()` — Assert that the overlay content is visible.
- `void assertHidden()` — Assert that the overlay content is hidden.
- `Locator getHeaderTitleLocator()` — Locator for the application title shown in the branding area.
- `void assertHeaderTitle(String title)` — Assert that the application title matches the expected text.
- `Locator getDescriptionLocator()` — Locator for the application description shown in the branding area.
- `void assertDescription(String description)` — Assert that the application description matches the expected text.

### MarkdownElement  `<vaadin-markdown>`

PlaywrightElement for <vaadin-markdown>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-markdown"`

**Constructors:**

- `MarkdownElement(Locator locator)` — Create a new MarkdownElement.

**Static factory methods:**

- `MarkdownElement get(Page page)` — Get the first <vaadin-markdown> on the page.
- `MarkdownElement get(Locator locator)` — Get the first <vaadin-markdown> within a locator scope.

**Methods:**

- `Locator getRenderedLocator()` — Locator for the root of the rendered Markdown output.
- `String getText()` — Get the text of the rendered Markdown, with leading and trailing whitespace removed.
- `void assertContainsText(String expected)` — Assert that the rendered Markdown contains the given text.
- `void assertText(String expected)` — Assert the full text of the rendered Markdown.
- `Locator getHeadings()` — Locator for every rendered heading (h1–h6), in document order.
- `Locator getHeadings(int level)` — Locator for the rendered headings of a single level, in document order.
- `Locator getHeading(int index)` — Locator for the rendered heading at the given index.
- `void assertHeadingCount(int count)` — Assert that the rendered output contains exactly the expected number of headings.
- `void assertHeading(int index, String expected)` — Assert the text of the rendered heading at the given index.
- `Locator getLinks()` — Locator for every rendered link, in document order.
- `Locator getLink(String text)` — Locator for the rendered link with the given accessible name.
- `void assertLinkCount(int count)` — Assert that the rendered output contains exactly the expected number of links.
- `void assertLink(String text, String href)` — Assert that the rendered link with the given text points to the given target.
- `Locator getCodeBlocks()` — Locator for every rendered fenced or indented code block, in document order.
- `Locator getCodeBlock(int index)` — Locator for the rendered code block at the given index.
- `void assertCodeBlockCount(int count)` — Assert that the rendered output contains exactly the expected number of code blocks.
- `void assertCodeBlock(int index, String expected)` — Assert the text of the rendered code block at the given index.
- `void assertCodeBlockLanguage(int index, String language)` — Assert the language of the rendered code block at the given index.

### MenuBarElement

PlaywrightElement for <vaadin-menu-bar>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement, HasAriaLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-menu-bar"`

**Constructors:**

- `MenuBarElement(Page page)` — Create a MenuBarElement from the page.
- `MenuBarElement(Locator locator)` — Create a MenuBarElement from an existing locator.

**Static factory methods:**

- `MenuBarElement getByLabel(Page page, String label)` — Get a menu bar by its accessible label.

**Methods:**

- `MenuItemElement getMenuItemElement(String name)` — Get a menu item by visible label.
- `MenuElement openSubMenu(String name)` — Click a menu item to open its submenu and return the submenu overlay.

### MenuElement

PlaywrightElement for the menu overlay list <vaadin-menu-bar-list-box>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement, HasAriaLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-menu-bar-list-box"`

**Constructors:**

- `MenuElement(Page page)` — Create a MenuElement from the page.
- `MenuElement(Locator locator)` — Create a MenuElement from an existing locator.

**Static factory methods:**

- `MenuElement getByLabel(Page page, String label)` — Get a menu overlay by its accessible label.

**Methods:**

- `MenuItemElement getMenuItemElement(String name)` — Get a menu item by its visible label within this menu.
- `MenuElement openSubMenu(String name)` — Click a menu item to open its submenu and return the next overlay.

### MenuItemElement

PlaywrightElement for individual menu items <vaadin-menu-bar-button>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement, HasAriaLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-menu-bar-button"`

**Constructors:**

- `MenuItemElement(Locator locator)` — Create a MenuItemElement from an existing locator.

**Static factory methods:**

- `MenuItemElement getByLabel(Locator locator, String label)` — Get a menu item by its accessible label within a scope.

**Methods:**

- `void assertExpanded()` — Assert that the menu item is expanded (shows submenu).
- `void assertCollapsed()` — Assert that the menu item is collapsed.

### MessageInputElement  `<vaadin-message-input>`

PlaywrightElement for <vaadin-message-input>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasEnabledElement, HasStyleElement, HasThemeElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-message-input"`

**Constructors:**

- `MessageInputElement(Locator locator)` — Create a new MessageInputElement.

**Static factory methods:**

- `MessageInputElement get(Page page)` — Get the first <vaadin-message-input> on the page.
- `MessageInputElement get(Locator locator)` — Get the first <vaadin-message-input> within a locator scope.

**Methods:**

- `Locator getTextAreaLocator()` — Locator for the internal <vaadin-text-area>.
- `Locator getTextAreaInputLocator()` — Locator for the native textarea inside the text area (slot="textarea").
- `Locator getSendButtonLocator()` — Locator for the internal send button (<vaadin-message-input-button>).
- `String getValue()` — Get the current text area value.
- `void setValue(String value)` — Set the message text by filling the internal textarea input.
- `void clear()` — Clear the text area.
- `void assertValue(String value)` — Assert that the text area input has the expected value.
- `void submit()` — Click the send button to submit the message.
- `void submitByEnter()` — Press Enter on the text area to submit the message.
- `void typeAndSubmit(String message)` — Set a message value and then click the send button.
- `void assertSendButtonVisible()` — Assert that the send button is visible.
- `void assertSendButtonHidden()` — Assert that the send button is hidden.
- `void assertSendButtonEnabled()` — Assert that the send button is enabled.
- `void assertSendButtonDisabled()` — Assert that the send button is disabled.
- `String getMessagePlaceholder()` — Get the placeholder text on the text area.
- `void assertMessagePlaceholder(String expected)` — Assert that the text area placeholder matches the expected text.
- `String getSendButtonText()` — Get the send button text content.
- `void assertSendButtonText(String expected)` — Assert that the send button text matches the expected text.

### MessageListElement  `<vaadin-message-list>`

PlaywrightElement for <vaadin-message-list>.

**Extends:** VaadinElement  
**Implements:** HasStyleElement, HasThemeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-message-list"`, `String FIELD_MESSAGE_TAG_NAME = "vaadin-message"`

**Constructors:**

- `MessageListElement(Locator locator)` — Create a new MessageListElement.

**Static factory methods:**

- `MessageListElement get(Page page)` — Get the first <vaadin-message-list> on the page.
- `MessageListElement get(Locator locator)` — Get the first <vaadin-message-list> within a locator scope.

**Methods:**

- `Locator getMessages()` — Locator for all <vaadin-message> children.
- `Locator getMessage(int index)` — Locator for a single message by index.
- `Locator getMessageByUserName(String userName)` — Locator for the first message whose author name contains the given text.
- `String getMessageText(int index)` — Get the text content of the message at the given index.
- `String getMessageUserName(int index)` — Get the user name of the message at the given index.
- `String getMessageTime(int index)` — Get the time of the message at the given index.
- `void assertMessageCount(int count)` — Assert that the list contains exactly the expected number of messages.
- `void assertEmpty()` — Assert that the list contains no messages.
- `void assertMessageText(int index, String expected)` — Assert that the message at the given index has the expected text.
- `void assertMessageUserName(int index, String expected)` — Assert that the message at the given index has the expected user name.
- `void assertMessageTime(int index, String expected)` — Assert that the message at the given index has the expected time.

### MultiSelectComboBoxElement  `<vaadin-multi-select-combo-box>`

PlaywrightElement for <vaadin-multi-select-combo-box>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasInputFieldElement, HasThemeElement, HasPlaceholderElement, HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement, HasClearButtonElement, HasAllowedCharPatternElement, HasReadOnlyElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-multi-select-combo-box"`, `String FIELD_ITEM_TAG_NAME = "vaadin-multi-select-combo-box-item"`, `String FIELD_CHIP_TAG_NAME = "vaadin-multi-select-combo-box-chip"`

**Constructors:**

- `MultiSelectComboBoxElement(Locator locator)` — Create a new MultiSelectComboBoxElement.

**Static factory methods:**

- `MultiSelectComboBoxElement getByLabel(Page page, String label)` — Get the MultiSelectComboBoxElement by its label.
- `MultiSelectComboBoxElement getByLabel(Locator locator, String label)` — Get the MultiSelectComboBoxElement by its label within a given scope.

**Methods:**

- `String getValue()` — Get the selected values as a comma-separated string from the selectedItems property.
- `void assertValue(String expected)` — Assert that the displayed value equals the expected string.
- `void selectItem(String item)` — Select an item by its visible label.
- `void deselectItem(String item)` — Deselect an item by its visible label.
- `void selectItems(String... items)` — Select multiple items in sequence.
- `void deselectItems(String... items)` — Deselect multiple items in sequence.
- `void filterAndSelectItem(String filter, String item)` — Type filter text into the input, then click the matching item.
- `void setFilter(String filter)` — Type into the input to trigger filtering.
- `String getFilter()` — Get the current filter value from the DOM property.
- `void open()` — Open the combo box overlay.
- `void close()` — Close the combo box overlay.
- `boolean isOpened()` — Whether the overlay is currently open.
- `void assertOpened()` — Assert that the combo box overlay is open.
- `void assertClosed()` — Assert that the combo box overlay is closed.
- `Locator getToggleButtonLocator()` — Locator for the toggle button part.
- `void clickToggleButton()` — Click the dropdown toggle button.
- `int getOverlayItemCount()` — Count visible overlay items.
- `void assertItemCount(int expected)` — Assert that the overlay contains exactly the expected number of items.
- `Locator getChipLocators()` — Get the locator for all non-overflow chips.
- `Locator getOverflowChipLocator()` — Get the locator for the overflow chip.
- `List<String> getSelectedItems()` — Get the labels of all currently selected items by reading the selectedItems property from the web component.
- `int getSelectedItemCount()` — Get the count of currently selected items from the selectedItems property.
- `void assertSelectedItems(String... expected)` — Assert that the selected item labels match the expected values.
- `void assertSelectedCount(int expected)` — Assert that the number of selected items matches.
- `T getOverlayItemComponent(String itemText, Class<T> type)` — Get a typed component element from an overlay item matching the given text.
- `T getOverlayItemComponent(int index, Class<T> type)` — Get a typed component element from an overlay item at the given visible index.

### NotificationElement

PlaywrightElement for notification cards <vaadin-notification-card>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-notification-card"`

**Constructors:**

- `NotificationElement(Page page)` — Create a NotificationElement for the open notification on the page.
- `NotificationElement(Locator locator)` — Create a NotificationElement from an existing locator.

**Static factory methods:**

- `NotificationElement getByText(Page page, String text)` — Get an open notification by (a substring of) its text.

**Methods:**

- `boolean isOpen()` — Whether the notification is open (visible).
- `void assertOpen()` — Assert that the notification is open.
- `void assertClosed()` — Assert that the notification is closed.
- `Locator getContentLocator()` — Locator for the notification content.
- `void assertContent(String content)` — Assert that the notification contains the given text.

### NumberFieldElement  `<vaadin-number-field>`

PlaywrightElement for <vaadin-number-field>.

**Extends:** AbstractNumberFieldElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-number-field"`

**Constructors:**

- `NumberFieldElement(Locator locator)` — Creates a new NumberFieldElement.

**Static factory methods:**

- `NumberFieldElement getByLabel(Page page, String label)` — Get the NumberFieldElement by its label.
- `NumberFieldElement getByLabel(Locator locator, String label)` — Get the NumberFieldElement by its label within a given scope.

**Methods:**

- `Double getStep()` — Get the current step value.
- `void setStep(double step)` — Set the step value.
- `void assertStep(Double step)` — Assert that the step attribute matches the expected value.
- `Double getMin()` — Get the current min value.
- `void setMin(double min)` — Set the min value.
- `void assertMin(Double min)` — Assert that the min attribute matches the expected value.
- `Double getMax()` — Get the current max value.
- `void setMax(double max)` — Set the max value.
- `void assertMax(Double max)` — Assert that the max attribute matches the expected value.

### PasswordFieldElement  `<vaadin-password-field>`

PlaywrightElement for <vaadin-password-field>.

**Extends:** TextFieldElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-password-field"`

**Constructors:**

- `PasswordFieldElement(Locator locator)` — Create a new PasswordFieldElement.

**Static factory methods:**

- `PasswordFieldElement getByLabel(Page page, String label)` — Get the PasswordFieldElement by its accessible name, searching the entire page.

### PopoverElement

PlaywrightElement for <vaadin-popover>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement, HasAriaLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-popover"`

**Constructors:**

- `PopoverElement(Page page)` — Create a PopoverElement by resolving the dialog with ARIA role.
- `PopoverElement(Locator locator)` — Create a PopoverElement from an existing locator.

**Static factory methods:**

- `PopoverElement getByLabel(Page page, String label)` — Get a popover by its accessible label.

**Methods:**

- `boolean isOpen()` — Whether the popover is open (visible).
- `void assertOpen()` — Assert that the popover is open.
- `void assertClosed()` — Assert that the popover is closed (hidden).
- `Locator getContentLocator()` — Locator for the popover content.

### ProgressBarElement  `<vaadin-progress-bar>`

PlaywrightElement for <vaadin-progress-bar>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-progress-bar"`, `String INDETERMINATE_ATTRIBUTE = "indeterminate"`

**Constructors:**

- `ProgressBarElement(Locator locator)` — Create a ProgressBarElement from an existing locator.

**Methods:**

- `double getValue()` — Current numeric value parsed from aria-valuenow.
- `void setValue(double min)` — Set the progress bar value.
- `void assertValue(Double expected)` — Assert that the numeric value matches.
- `Double getMin()` — Get the min value.
- `void setMin(double min)` — Set the min value.
- `void assertMin(double min)` — Assert that min matches the expected value.
- `Double getMax()` — Get the max value.
- `void setMax(double max)` — Set the max value.
- `void assertMax(double max)` — Assert that max matches the expected value.
- `boolean isIndeterminate()` — Whether the bar is indeterminate.
- `void assertIndeterminate()` — Assert indeterminate state.
- `void assertNotIndeterminate()` — Assert not indeterminate.
- `void setIndeterminate(boolean indeterminate)` — Set the indeterminate state.

### RadioButtonGroupElement  `<vaadin-radio-group>`

PlaywrightElement for <vaadin-radio-group>.

**Extends:** VaadinElement  
**Implements:** HasLabelElement, HasEnabledElement, HasHelperElement, HasValidationPropertiesElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-radio-group"`

**Constructors:**

- `RadioButtonGroupElement(Locator locator)` — Create a new RadioButtonGroupElement.

**Static factory methods:**

- `RadioButtonGroupElement getByLabel(Page page, String label)` — Get the group by its accessible label.

**Methods:**

- `void selectByLabel(String label)` — Select a radio by its label text.
- `void selectByValue(String value)` — Select a radio by its value.
- `RadioButtonElement getRadioButtonByLabel(String label)` — Get a specific radio by its label within the group.
- `void setValue(String value)` — Set the selected value by label.
- `void assertValue(String value)` — Assert the selected value by label.

### SelectElement  `<vaadin-select>`

PlaywrightElement for <vaadin-select>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasInputFieldElement, HasPrefixElement, HasThemeElement, HasPlaceholderElement, HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-select"`, `String FIELD_ITEM_TAG_NAME = "vaadin-select-item"`, `String FIELD_OVERLAY_TAG_NAME = "vaadin-select-list-box"`

**Constructors:**

- `SelectElement(Locator locator)` — Create a new SelectElement.

**Static factory methods:**

- `SelectElement getByLabel(Page page, String label)` — Get the SelectElement by its accessible name, searching the entire page.
- `SelectElement getByLabel(Locator locator, String label)` — Get the SelectElement by its accessible name within a given scope.

**Methods:**

- `void selectItem(String item)` — Select an item by its visible label.
- `String getValue()` — Get the selected value label for single-select.
- `void assertValue(String expected)` — Assert the selected value label equals the expected string.

### SideNavigationElement  `<vaadin-side-nav>`

PlaywrightElement for <vaadin-side-nav>.

**Extends:** VaadinElement  
**Implements:** HasLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-side-nav"`

**Constructors:**

- `SideNavigationElement(Locator locator)`

**Static factory methods:**

- `SideNavigationElement getByLabel(Page page, String label)` — Get the SideNavigationElement by its label.

**Methods:**

- `boolean isCollapsed()` — Checks if the side nav is collapsed.
- `void assertCollapsed()` — Asserts that the side nav is collapsed.
- `void assertExpanded()` — Asserts that the side nav is expanded.
- `void assertCollapsible()` — Asserts that the side nav is collapsible.
- `void assertNotCollapsible()` — Asserts that the side nav is not collapsible.
- `SideNavigationItemElement getItem(String label)` — Gets a SideNavigationItemElement by its label text.
- `void clickItem(String label)` — Clicks an item by its label.
- `void toggle()` — Toggles the expansion state of the item.

### SideNavigationItemElement  `<vaadin-side-nav-item>`

PlaywrightElement for <vaadin-side-nav-item>.

**Extends:** VaadinElement  
**Implements:** HasDisabledAttributeElement, HasPrefixElement, HasSuffixElement, HasLabelElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-side-nav-item"`

**Constructors:**

- `SideNavigationItemElement(Locator locator)`

**Methods:**

- `boolean isExpanded()` — Checks if the item is expanded.
- `void assertExpanded()` — Asserts that the item is expanded.
- `void assertCollapsed()` — Asserts that the item is collapsed.
- `void assertCurrent()` — Asserts that the item is current.
- `void assertNotCurrent()` — Asserts that the item is not current.
- `void toggle()` — Toggles the expansion state of the item.
- `void navigate()`

### SliderElement  `<vaadin-slider>`

PlaywrightElement for <vaadin-slider> (the Flow IntegerSlider and DecimalSlider components).

**Extends:** VaadinElement  
**Implements:** HasLabelElement, HasHelperElement, HasEnabledElement, HasReadOnlyElement, FocusableElement, HasThemeElement, HasStyleElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-slider"`

**Constructors:**

- `SliderElement(Locator locator)` — Creates a new SliderElement.

**Static factory methods:**

- `SliderElement getByLabel(Page page, String label)` — Get the SliderElement by its label.
- `SliderElement getByLabel(Locator locator, String label)` — Get the SliderElement by its label within a given scope.

**Methods:**

- `Locator getInputLocator()` — Locator for the native range input rendered in the slider's light DOM.
- `Locator getFocusLocator()` — Focus lives on the native range input, not on the component root.
- `Locator getEnabledLocator()` — The disabled state is carried by the native range input; the component root only mirrors it with an attribute.
- `double getValue()` — Get the current value.
- `void setValue(double value)` — Set the value.
- `void assertValue(double value)` — Assert that the value matches the expected one.
- `Double getMin()` — Get the min constraint.
- `void assertMin(double min)` — Assert that the min constraint matches the expected value.
- `Double getMax()` — Get the max constraint.
- `void assertMax(double max)` — Assert that the max constraint matches the expected value.
- `Double getStep()` — Get the step constraint.
- `void assertStep(double step)` — Assert that the step constraint matches the expected value.
- `void increment()` — Increase the value by one step, as pressing the right arrow key does.
- `void increment(int steps)` — Increase the value by the given number of steps.
- `void decrement()` — Decrease the value by one step, as pressing the left arrow key does.
- `void decrement(int steps)` — Decrease the value by the given number of steps.
- `void moveToMin()` — Move the value to min, as pressing the Home key does.
- `void moveToMax()` — Move the value to max, as pressing the End key does.

### SplitLayoutElement  `<vaadin-split-layout>`

PlaywrightElement for vaadin-split-layout.

**Extends:** VaadinElement  
**Implements:** HasStyleElement, HasThemeElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-split-layout"`

**Constructors:**

- `SplitLayoutElement(Locator locator)` — Create a new SplitLayoutElement.

**Static factory methods:**

- `SplitLayoutElement get(Page page)` — Get the first split layout on the page.

**Methods:**

- `Locator getPrimaryLocator()` — Locator for the primary content (slot="primary").
- `Locator getSecondaryLocator()` — Locator for the secondary content (slot="secondary").
- `Locator getHandleLocator()` — Locator for the splitter handle (shadow part="handle").
- `void assertVertical()` — Assert that the layout orientation is vertical.
- `void assertHorizontal()` — Assert that the layout orientation is horizontal.
- `void dragSplitterBy(double deltaX, double deltaY)` — Drag the splitter by a delta offset in pixels.

### SwitchElement  `<vaadin-switch>`

PlaywrightElement for <vaadin-switch>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement, HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-switch"`

**Constructors:**

- `SwitchElement(Locator locator)` — Create a new SwitchElement.

**Static factory methods:**

- `SwitchElement getByLabel(Page page, String label)` — Get a SwitchElement by its accessible label.

### TabElement  `<vaadin-tab>`

PlaywrightElement for tabs <vaadin-tab>.

**Extends:** VaadinElement  

**Constants:** `String FIELD_PARENT_TAG_NAME = "vaadin-tabs"`, `String FIELD_TAG_NAME = "vaadin-tab"`

**Constructors:**

- `TabElement(Locator locator)` — Create a TabElement from an existing locator.

**Static factory methods:**

- `TabElement getTabByText(Locator locator, String summary)` — Get a tab by visible text within a scope.
- `TabElement getSelectedTab(Locator locator)` — Get the currently selected tab within a scope.

**Methods:**

- `boolean isSelected()` — Whether the tab is currently selected.
- `void select()` — Select the tab by clicking it.
- `String getLabel()` — Get the tab label text.
- `void assertSelected()` — Assert that the tab is selected.
- `void assertNotSelected()` — Assert that the tab is not selected.

### TabSheetElement  `<vaadin-tabsheet>`

PlaywrightElement for <vaadin-tabsheet>.

**Extends:** VaadinElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-tabsheet"`

**Constructors:**

- `TabSheetElement(Locator locator)` — Create a TabSheetElement from an existing locator.

**Static factory methods:**

- `TabSheetElement get(Page page)` — Get the first tabsheet instance on the page.

**Methods:**

- `TabsElement getTabsElement()` — Get the embedded tab strip.
- `void assertTabsCount(int count)` — Assert the count of tabs.
- `TabElement getTab(String label)` — Get a tab by its label.
- `TabElement getSelectedTab()` — Get the currently selected tab.
- `void selectTab(String label)` — Select a tab by label text.
- `void selectTab(int index)` — Select a tab by its zero-based index.
- `void assertSelectedTab(String label)` — Assert that the tab with the given label is selected.
- `Locator getContentLocator()` — Locator for the currently visible content panel.

### TabsElement  `<vaadin-tabs>`

PlaywrightElement for <vaadin-tabs>.

**Extends:** VaadinElement  
**Implements:** HasThemeElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-tabs"`, `String ORIENTATION_HORIZONTAL = "horizontal"`, `String ORIENTATION_VERTICAL = "vertical"`

**Constructors:**

- `TabsElement(Locator locator)` — Create a new TabsElement.

**Static factory methods:**

- `TabsElement get(Page page)` — Get the first TabsElement on the page.
- `TabsElement get(Locator parent)` — Get the first TabsElement within a parent locator.
- `TabsElement getById(Page page, String id)` — Get a TabsElement by its id attribute.

**Methods:**

- `Locator getTabs()` — Locator matching every tab of this tab strip, in DOM order.
- `int getTabCount()` — Get the number of tabs currently in the tab strip.
- `TabElement getTab(String label)` — Get a tab by its label.
- `TabElement getTab(int index)` — Get a tab by its zero-based index.
- `TabElement getSelectedTab()` — Get the currently selected tab.
- `int getSelectedIndex()` — Get the zero-based index of the selected tab.
- `String getOrientation()` — Get the current orientation.
- `void selectTab(String label)` — Select a tab by its label.
- `void selectTab(int index)` — Select a tab by its zero-based index.
- `void assertTabCount(int count)` — Assert the number of tabs in the tab strip.
- `void assertSelectedTab(String label)` — Assert that the tab with the given label is the selected one.
- `void assertSelectedTab(int index)` — Assert that the tab at the given zero-based index is the selected one.
- `void assertOrientation(String orientation)` — Assert the orientation of the tab strip.

### TextAreaElement  `<vaadin-text-area>`

PlaywrightElement for <vaadin-text-area>.

**Extends:** TextFieldElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-text-area"`

**Constructors:**

- `TextAreaElement(Locator locator)` — Create a new TextAreaElement.

**Static factory methods:**

- `TextAreaElement getByLabel(Page page, String label)` — Get the TextAreaElement by its accessible name, searching the entire page.
- `TextAreaElement getByLabel(Locator locator, String label)` — Get the TextAreaElement by its accessible name, scoped to the given locator.

**Methods:**

- `Locator getInputLocator()` — Locator for the native textarea slotted into the component.

### TextFieldElement  `<vaadin-text-field>`

PlaywrightElement for <vaadin-text-field>

**Extends:** VaadinElement  
**Implements:** HasValidationPropertiesElement, HasInputFieldElement, HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasReadOnlyElement, HasTooltipElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-text-field"`, `String MAXLENGTH_ATTRIBUTE = "maxlength"`, `String PATTERN_ATTRIBUTE = "pattern"`, `String MIN_LENGTH_ATTRIBUTE = "minLength"`

**Constructors:**

- `TextFieldElement(Locator locator)` — Creates a new TextFieldElement

**Static factory methods:**

- `TextFieldElement getByLabel(Page page, String label)` — Get the TextFieldElement by its accessible name, searching the entire page.
- `TextFieldElement getByLabel(Locator locator, String label)` — Get the TextFieldElement by its accessible name, scoped to the given locator.

**Methods:**

- `Integer getMinLength()` — Get the current minimum length of the text field.
- `void setMinLength(int min)` — Set the minimum length for the text field.
- `void assertMinLength(Integer min)` — Assert that the minimum length of the text field is as expected.
- `Integer getMaxLength()` — Get the current maximum length of the text field.
- `void setMaxLength(int max)` — Set the maximum length for the text field.
- `void assertMaxLength(Integer max)` — Assert that the maximum length of the text field is as expected.
- `String getPattern()` — Get the current pattern of the text field.
- `void setPattern(String pattern)` — Set the pattern for the text field.
- `void assertPattern(String pattern)` — Assert that the pattern of the text field is as expected.

### TimePickerElement  `<vaadin-time-picker>`

PlaywrightElement for <vaadin-time-picker>.

**Extends:** VaadinElement  
**Implements:** HasInputFieldElement, HasValidationPropertiesElement, HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-time-picker"`, `DateTimeFormatter LOCAL_TIME = DateTimeFormatter.ofPattern("HH:mm")`

**Constructors:**

- `TimePickerElement(Locator locator)` — Create a new TimePickerElement.

**Static factory methods:**

- `TimePickerElement getByLabel(Page page, String label)` — Get the TimePickerElement by its label.
- `TimePickerElement getByLabel(Locator locator, String label)` — Get the TimePickerElement by its label within a given scope.

**Methods:**

- `void setValue(LocalTime time)` — Set the value using a LocalTime formatted as HH:mm.
- `LocalTime getValueAsLocalTime()` — Get the current value as a LocalTime.
- `void assertValue(LocalTime value)` — Assert that the value equals the provided time.

### TreeGridElement

PlaywrightElement for Vaadin Tree Grid.

**Extends:** GridElement  

**Constants:** `String FIELD_TAG_NAME = GridElement.FIELD_TAG_NAME`

**Constructors:**

- `TreeGridElement(Locator locator)` — Create a new TreeGridElement.

**Static factory methods:**

- `TreeGridElement get(Page page)` — Get the first TreeGridElement on the page.
- `TreeGridElement get(Locator parent)` — Get the first TreeGridElement within a parent locator.
- `TreeGridElement getById(Page page, String id)` — Get a TreeGridElement by its id attribute.

**Methods:**

- `Optional<TreeRowElement> findTreeRow(int rowIndex)` — Find the tree row at the given index, returning a TreeRowElement that exposes tree-specific state and actions.
- `boolean isRowExpanded(int rowIndex)` — Whether the row at the given index is expanded.
- `boolean isRowCollapsed(int rowIndex)` — Whether the row at the given index is collapsed (has children but is not expanded).
- `boolean isRowLeaf(int rowIndex)` — Whether the row at the given index is a leaf node (has no children).
- `int getRowLevel(int rowIndex)` — Get the hierarchy level of the row at the given index (0-based; root items are level 0).
- `int getExpandedRowCount()` — Get the number of currently visible expanded rows.
- `void expandRow(int rowIndex)` — Expand the row at the given index.
- `void collapseRow(int rowIndex)` — Collapse the row at the given index.
- `void toggleRow(int rowIndex)` — Toggle the expand/collapse state of the row at the given index.

#### TreeGridElement.TreeRowElement

Represents a row in a TreeGrid, extending GridElement.RowElement with tree-specific state queries and expand/collapse actions.

- `Locator getTreeToggleLocator()` — Get the locator for the vaadin-grid-tree-toggle element in this row.
- `boolean isExpanded()` — Whether this row is expanded.
- `boolean isLeaf()` — Whether this row is a leaf node (has no children).
- `boolean isCollapsed()` — Whether this row is collapsed (has children but is not expanded).
- `int getLevel()` — Get the hierarchy level of this row (0-based; root items are level 0).
- `void expand()` — Expand this row.
- `void collapse()` — Collapse this row.
- `void toggle()` — Toggle the expand/collapse state of this row.

### UploadElement  `<vaadin-upload>`

PlaywrightElement for vaadin-upload.

**Extends:** VaadinElement  
**Implements:** HasEnabledElement, HasValidationPropertiesElement, HasThemeElement, FocusableElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-upload"`, `String FILE_ITEM_TAG_NAME = "vaadin-upload-file"`

**Constructors:**

- `UploadElement(Locator locator)` — Create a new UploadElement.

**Static factory methods:**

- `UploadElement getByButtonText(Page page, String buttonText)` — Get the UploadElement by the accessible text of its upload button.

**Methods:**

- `Locator getFileInputLocator()` — Locator for the native input[type=file] element.
- `Locator getUploadButtonLocator()` — Locator for the primary upload button.
- `Locator getFileItemLocator(String fileName)` — Locator for a specific file row.
- `Locator getFileStatusLocator(String fileName)` — Locator for the status cell of a given file row.
- `void uploadFiles(Path... files)` — Upload one or more files by feeding the hidden input.
- `void removeFile(String fileName)` — Remove a file from the list using the remove button.
- `void assertHasFile(String fileName)` — Assert that a file is listed in the upload file list.
- `void assertNoFile(String fileName)` — Assert that a file is not present in the upload file list.
- `void assertFileComplete(String fileName)` — Assert that a file row is marked complete.
- `void assertMaxFilesReached()`

### VirtualListElement  `<vaadin-virtual-list>`

PlaywrightElement for <vaadin-virtual-list>.

**Extends:** VaadinElement  
**Implements:** FocusableElement, HasStyleElement  

**Constants:** `String FIELD_TAG_NAME = "vaadin-virtual-list"`

**Constructors:**

- `VirtualListElement(Locator locator)` — Create a new VirtualListElement.

**Static factory methods:**

- `VirtualListElement get(Page page)` — Get the first VirtualListElement on the page.

**Methods:**

- `int getRowCount()` — Get the total number of items in the list.
- `int getFirstVisibleRowIndex()` — Get the index of the first row that is at least partially visible.
- `int getLastVisibleRowIndex()` — Get the index of the last row that is at least partially visible.
- `int getVisibleRowCount()` — Get the number of currently visible rows in a single browser round-trip.
- `boolean isRowInView(int rowIndex)` — Whether the given row index is currently visible in the viewport.
- `Locator getRenderedItems()` — Get a locator matching all currently rendered child elements.
- `Locator getItemByIndex(int index)` — Get the rendered DOM element at the given virtual index.
- `Locator getItemByText(String text)` — Get a rendered item containing the given text.
- `T getItemComponent(int index, Class<T> type)` — Get a typed component element from the rendered item at the given virtual index.
- `T getItemComponentByText(String text, Class<T> type)` — Get a typed component element from the rendered item whose text matches.
- `T getComponent(Class<T> type)` — Get the first typed component element found anywhere in the currently rendered items.
- `void scrollToRow(int rowIndex)` — Scroll the list so that the given row index becomes visible.
- `void scrollToStart()` — Scroll to the very beginning of the list.
- `void scrollToEnd()` — Scroll to the very end of the list.
- `void assertRowCount(int expected)` — Assert the total number of items matches the expected count.
- `void assertRowInView(int rowIndex)` — Assert that the given row index is currently visible.
- `void assertRowNotInView(int rowIndex)` — Assert that the given row index is NOT currently visible.
- `void assertFirstVisibleRow(int expected)` — Assert that the first visible row index equals the expected value.
- `void assertLastVisibleRow(int expected)` — Assert that the last visible row index equals the expected value.
- `void assertItemRendered(String text)` — Assert that an item containing the given text is currently rendered.
- `void assertEmpty()` — Assert the list has zero items.

## Shared mixins

Behaviours composed into elements via `implements`. An element that lists a mixin below exposes all of that mixin's methods.

### FocusableElement

Mixin for components that can receive keyboard focus.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getFocusLocator()` — The locator to focus/blur.
- `void focus()` — Focus the component.
- `void blur()` — Blur the component.
- `String getTabIndex()` — Current tab index as string (from tabIndex attribute).
- `void assertIsFocused()` — Assert that the component has focus.
- `void assertIsNotFocused()` — Assert that the component does not have focus.

### HasAllowedCharPatternElement

Mixin for components supporting allowedCharPattern to constrain input.

**Extends:** HasLocatorElement  

**Methods:**

- `String getAllowedCharPattern()` — Get the current allowedCharPattern.
- `void setAllowedCharPattern(String pattern)` — Set the allowedCharPattern.
- `void assertAllowedCharPattern(String pattern)` — Assert that the allowedCharPattern matches the expected value.

### HasAriaLabelElement

Mixin for components exposing an ARIA label.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getAriaLabelLocator()` — Locator where the aria-label is applied.
- `String getAriaLabel()` — Get the current aria-label value.
- `void assertAriaLabel(String ariaLabel)` — Assert that the aria-label matches the expected text, or is absent when null.

### HasCheckedElement

Mixin for components backed by a checkbox-like native input, i.e. one whose state is a boolean checked flag rather than a textual value (vaadin-checkbox, vaadin-radio-button, vaadin-switch).

**Extends:** HasEnabledElement, HasAriaLabelElement, FocusableElement  

**Methods:**

- `Locator getInputLocator()` — Locator for the native input element inside the component.
- `boolean isChecked()` — Whether the component is currently checked.
- `void assertChecked()` — Assert that the component is checked.
- `void assertNotChecked()` — Assert that the component is not checked.
- `void assertChecked(boolean checked)` — Assert the component's checked state.
- `void check()` — Check the component.
- `void uncheck()` — Uncheck the component.
- `void setChecked(boolean checked)` — Check or uncheck the component.

### HasClearButtonElement

Mixin for components with a clear button part.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getClearButtonLocator()` — Locator for the clear button (part~=clear-button).
- `void clickClearButton()` — Click the clear button.
- `boolean isClearButtonVisible()` — Whether the clear button is visible.
- `void assertClearButtonVisible()` — Assert that the clear button is visible.
- `void assertClearButtonNotVisible()` — Assert that the clear button is not visible.

### HasDisabledAttributeElement

Mixin for components whose enablement has to be read from the disabled attribute of the host element instead of Playwright's enablement check.

**Extends:** HasEnabledElement  

*Marker interface — composes the mixins listed above; no own methods.*

### HasEnabledElement

Mixin for components that expose enabled/disabled state.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getEnabledLocator()` — Locator used to check enablement.
- `boolean isEnabled()` — Whether the component is enabled.
- `boolean isEnabled(boolean enabled)` — Whether the component's enabled state matches the expected value.
- `void assertEnabled()` — Assert that the component is enabled.
- `void assertEnabled(boolean enabled)` — Assert that the component is enabled (true) or disabled (false).
- `void assertDisabled()` — Assert that the component is disabled.

### HasHelperElement

Mixin for components that provide helper text via the helper slot.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getHelperLocator()` — Locator for the helper slot content.
- `String getHelperText()` — Text content of the helper slot.
- `void assertHelperHasText(String helperText)` — Assert that the helper slot has the expected text.

### HasInputFieldElement

Convenience mixin grouping common capabilities of Vaadin input fields (label, value handling, helper and styling).

**Extends:** HasHelperElement, HasValueElement, HasStyleElement, HasLabelElement  

*Marker interface — composes the mixins listed above; no own methods.*

### HasLabelElement

Mixin for components that render a visible label.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getLabelLocator()` — Locator for the visible label element.
- `String getLabel()` — Get the label text.
- `void assertLabel(String label)` — Assert that the label text matches, or is hidden when null.

### HasLocatorElement

Base contract for objects that expose a Playwright Locator.

**Methods:**

- `Locator getLocator()` — The root locator for the component.
- `void waitForVaadinIdle()` — Block until Vaadin Flow has no active client-server exchange in flight.

### HasPlaceholderElement

Mixin for components that support the placeholder attribute.

**Extends:** HasLocatorElement  

**Methods:**

- `void setPlaceholder(String placeholder)` — Set the placeholder attribute.
- `String getPlaceholder()` — Get the current placeholder text.
- `void assertPlaceholder(String placeholder)` — Assert that the placeholder matches the expected text.

### HasPrefixElement

Utilities to interact with components implementing Vaadin's HasPrefix(slot="prefix").

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getPrefixLocator()` — Locator for the prefix slot content.
- `String getPrefixText()` — Text content of the prefix slot.
- `void assertPrefixHasText(String text)` — Assert that the prefix slot has the expected text, or is hidden when null.

### HasReadOnlyElement

Mixin for components that expose a read-only state through the reflected readonly attribute.

**Extends:** HasLocatorElement  

**Constants:** `String READONLY_ATTRIBUTE = "readonly"`

**Methods:**

- `Locator getReadOnlyLocator()` — Locator used to check the read-only state.
- `boolean isReadOnly()` — Whether the component is read-only.
- `boolean isReadOnly(boolean readOnly)` — Whether the component's read-only state matches the expected value.
- `void assertReadOnly()` — Assert that the component is read-only.
- `void assertReadOnly(boolean readOnly)` — Assert that the component is read-only (true) or not read-only (false).
- `void assertNotReadOnly()` — Assert that the component is not read-only.

### HasStyleElement

Mixin for components exposing styling via CSS classes.

**Extends:** HasLocatorElement  

**Methods:**

- `String getCssClass()` — Get the raw class attribute value.
- `void assertCssClass(String... classnames)` — Assert the component has exactly the provided class names, or no classes when null.

### HasSuffixElement

Utilities to interact with components implementing Vaadin's HasSuffix (slot="suffix").

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getSuffixLocator()` — Locator for the suffix slot content.
- `String getSuffixText()` — Text content of the suffix slot.
- `void assertSuffixHasText(String text)` — Assert that the suffix slot has the expected text, or is hidden when null.

### HasThemeElement

Mixin for components that support the theme attribute.

**Extends:** HasLocatorElement  

**Methods:**

- `String getTheme()` — Get the current theme attribute value.
- `void assertTheme(String theme)` — Assert that the theme attribute matches, or is absent when null.
- `void assertHasThemeVariant(String variant)` — Assert that the theme attribute contains the given variant, ignoring any other variants that are also applied.
- `void assertHasNoThemeVariant(String variant)` — Assert that the theme attribute does not contain the given variant.

### HasTooltipElement

Utilities to interact with components implementing Vaadin's HasTooltip the first child with role tooltip

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getTooltipLocator()` — Locator for the tooltip content (role=tooltip).
- `String getTooltipText()` — Tooltip text content.
- `void assertTooltipHasText(String text)`

### HasValidationPropertiesElement

Mixin for components exposing validation state and error messages.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getErrorMessageLocator()` — Locator for the error message slot.
- `void assertValid()` — Assert that the component is valid (not invalid).
- `void assertInvalid()` — Assert that the component is invalid.
- `void assertErrorMessage(String errorMessage)` — Assert that the error message equals the expected text.

### HasValueElement

Mixin for components that expose a textual value through an input slot.

**Extends:** HasLocatorElement  

**Methods:**

- `Locator getInputLocator()` — Locator for the native input element inside the component.
- `String getValue()` — Get the current string value.
- `void setValue(String value)` — Set the field value by filling the input and dispatching a change event.
- `void clear()` — Clear the input value.
- `void assertValue(String value)` — Assert that the input value matches the expected string.

## Base class

Every element extends `VaadinElement`; these methods are available on all of them.

### VaadinElement

Base class for typed Playwright wrappers around Vaadin components.

*abstract* **Implements:** HasLocatorElement  

**Constructors:**

- `VaadinElement(Locator locator)` — Create a new VaadinElement wrapper.

**Methods:**

- `void click()` — Click the component root.
- `String getText()` — Get the textual content of the component root.
- `void setProperty(String name, Object value)` — Set a DOM property on the underlying element (e.g. value, disabled).
- `Object getProperty(String name)` — Get a DOM property from the underlying element.
- `boolean isVisible()` — Whether the component is visible.
- `void assertVisible()` — Assert that the component is visible.
- `void assertHidden()` — Assert that the component is hidden.
- `boolean isVisible(boolean visible)` — Whether the component's visibility matches the expected state.
- `void assertVisible(boolean visible)` — Assert the component's visibility state.
- `boolean isHidden()` — Whether the component is hidden.

