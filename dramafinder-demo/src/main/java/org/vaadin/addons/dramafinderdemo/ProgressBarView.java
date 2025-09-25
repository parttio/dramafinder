package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("ProgressBar Demo")
@Route(value = "progress-bar", layout = MainLayout.class)
public class ProgressBarView extends Main {

    public ProgressBarView() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setId("progress-bar");
        progressBar.setValue(0.5);

        ProgressBar indeterminate = new ProgressBar();
        indeterminate.setId("indeterminate-progress-bar");
        indeterminate.setIndeterminate(true);

        ProgressBar minMax = new ProgressBar(10.5, 100, 50);
        minMax.setId("min-max-progress-bar");

        add(progressBar, indeterminate, minMax);
    }
}
