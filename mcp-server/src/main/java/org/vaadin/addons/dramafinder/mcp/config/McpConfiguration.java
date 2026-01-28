package org.vaadin.addons.dramafinder.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.vaadin.addons.dramafinder.mcp.tools.VaadinMcpTools;

/**
 * MCP Server configuration for Drama Finder.
 * <p>
 * Uses Spring AI MCP to automatically expose tools annotated with @Tool.
 */
@Configuration
@EnableConfigurationProperties(PlaywrightProperties.class)
public class McpConfiguration {

    @Bean
    public ToolCallbackProvider vaadinToolsProvider(VaadinMcpTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
