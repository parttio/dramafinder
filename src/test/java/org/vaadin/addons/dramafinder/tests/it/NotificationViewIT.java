package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.NotificationElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NotificationViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "notification";
    }

    @Test
    public void testNotification() {
        ButtonElement openNotificationButton = ButtonElement.getByText(page, "Show notification");
        openNotificationButton.click();

        NotificationElement notification = new NotificationElement(page);
        notification.assertOpen();

        assertThat(notification.getContentLocator()).hasText("This is a notification.");
        notification.assertContent("This is a notification.");
        notification.assertClosed();
    }

    @Test
    public void testNotificationComponent() {
        ButtonElement openNotificationButton = ButtonElement.getByText(page, "Show component notification");
        openNotificationButton.click();

        NotificationElement notification = new NotificationElement(page);
        notification.assertOpen();

        ButtonElement closeNotificationButton = ButtonElement.getByText(notification.getContentLocator(), "Close");
        closeNotificationButton.click();
        notification.assertClosed();
    }


    @Test
    public void testHasClass() {
        ButtonElement openNotificationButton = ButtonElement.getByText(page, "Show component notification");
        openNotificationButton.click();

        NotificationElement notification = new NotificationElement(page);
        notification.assertCssClass("custom-notification");
    }

    @Test
    public void testTheme() {
        ButtonElement openNotificationButton = ButtonElement.getByText(page, "Show component notification");
        openNotificationButton.click();

        NotificationElement notification = new NotificationElement(page);
        notification.assertTheme("error");
    }
}
