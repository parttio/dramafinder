package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.NotificationElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

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
    public void testAssertContent() {
        ButtonElement openNotificationButton = ButtonElement.getByText(page, "Show notification");
        openNotificationButton.click();

        NotificationElement notification = new NotificationElement(page);
        notification.assertOpen();
        notification.assertContent("This is a notification.");
    }

    @Test
    public void testGetByTextWithMultipleOpen() {
        ButtonElement openNotificationsButton = ButtonElement.getByText(page, "Show multiple notifications");
        openNotificationsButton.click();

        NotificationElement first = NotificationElement.getByText(page, "First notification");
        first.assertOpen();
        first.assertContent("First notification");

        NotificationElement second = NotificationElement.getByText(page, "Second notification");
        second.assertOpen();
        second.assertContent("Second notification");
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

    @Test
    public void testFindWithText() {
        ButtonElement.getByText(page, "Show multiple notifications").click();

        NotificationElement second = find(page, NotificationElement.class)
                .withText("Second notification").single();
        second.assertOpen();
        second.assertContent("Second notification");
        assertThat(find(page, NotificationElement.class).getLocator()).hasCount(2);
    }

    @Test
    public void testFindWithTextIsExact() {
        ButtonElement.getByText(page, "Show same-text notifications").click();

        assertThat(find(page, NotificationElement.class).withText("Upload finished").getLocator())
                .hasCount(2);
        assertThat(find(page, NotificationElement.class).withTextContaining("upload FINISHED").getLocator())
                .hasCount(3);
        assertThat(find(page, NotificationElement.class)
                .withText("Upload finished with warnings").getLocator()).hasCount(1);
    }

    @Test
    public void testFindCombinesTextAndTheme() {
        ButtonElement.getByText(page, "Show same-text notifications").click();

        NotificationElement error = find(page, NotificationElement.class)
                .withText("Upload finished")
                .withAttribute("theme", "error")
                .single();
        error.assertOpen();
        error.assertTheme("error");
    }

    @Test
    public void testFindComponentNotification() {
        ButtonElement.getByText(page, "Show component notification").click();

        NotificationElement notification = find(page, NotificationElement.class)
                .withTextContaining("This is a notification.")
                .withClassName("custom-notification")
                .single();
        notification.assertOpen();
        // Component texts join without a separator: the span, then the button.
        assertThat(find(page, NotificationElement.class)
                .withText("This is a notification.Close").getLocator()).hasCount(1);
        ButtonElement.getByText(notification.getContentLocator(), "Close").click();
        notification.assertClosed();
        assertThat(find(page, NotificationElement.class).getLocator()).hasCount(0);
        assertFalse(find(page, NotificationElement.class).exists());
    }

    @Test
    public void testFindIgnoresClosedNotifications() {
        ButtonElement.getByText(page, "Show notification").click();

        NotificationElement notification = find(page, NotificationElement.class)
                .withText("This is a notification.").single();
        notification.assertOpen();
        notification.assertClosed();
        assertThat(find(page, NotificationElement.class).getLocator()).hasCount(0);
    }
}
