package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Markdown Demo")
@Route(value = "markdown", layout = MainLayout.class)
public class MarkdownView extends Main {

    public MarkdownView() {
        createBasicExample();
        createLineBreaksExample();
        createEmptyExample();
    }

    private void createBasicExample() {
        Markdown markdown = new Markdown("""
                # Release notes

                DramaFinder is a **Playwright** helper library for Vaadin.
                See the [documentation](https://vaadin.com/docs) and the
                [add-on page](https://vaadin.com/directory) for details.

                ## Getting started

                ```java
                ButtonElement.getByText(page, "Save").click();
                ```

                ### Notes

                Use `assertContainsText` for partial matches.
                """);
        addExample("Basic Example", markdown);
    }

    private void createLineBreaksExample() {
        Markdown markdown = new Markdown("""
                First line
                Second line
                """);
        markdown.setLineBreaks(true);
        addExample("Line Breaks Example", markdown);
    }

    private void createEmptyExample() {
        addExample("Empty Example", new Markdown());
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        for (Component component : components) {
            add(component);
        }
    }
}
