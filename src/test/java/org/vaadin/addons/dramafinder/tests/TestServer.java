package org.vaadin.addons.dramafinder.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.aura.Aura;

@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
@SpringBootApplication
public class TestServer implements AppShellConfigurator {

    public static void main(String... args) throws Exception {
        SpringApplication.run(TestServer.class, args);
    }

}