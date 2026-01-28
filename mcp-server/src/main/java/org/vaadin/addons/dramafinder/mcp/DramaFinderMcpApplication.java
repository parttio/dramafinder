package org.vaadin.addons.dramafinder.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Drama Finder MCP Server.
 * <p>
 * This server provides MCP (Model Context Protocol) tools for AI assistants
 * to interact with and test Vaadin applications using the Drama Finder library.
 */
@SpringBootApplication
public class DramaFinderMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(DramaFinderMcpApplication.class, args);
    }
}
