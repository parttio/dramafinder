package org.vaadin.addons.dramafinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DramaFinder must not drag Spring or Vaadin into a consumer's classpath:
 * every {@code org.springframework*} or {@code com.vaadin} dependency is
 * {@code provided}, {@code test} or {@code optional}, so Maven never passes it
 * on. Spring (and Spring Security) is needed only by the {@code spring}
 * package, Flow only by {@code RouteNavigation}, and the projects that use them
 * already have both.
 */
class DependencyScopeTest {

    @Test
    void springAndVaadinAreNeverTransitive() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("pom.xml"));
        Element project = pom.getDocumentElement();
        List<String> leaking = new ArrayList<>();
        int checked = 0;
        for (Element dependency : children(child(project, "dependencies"), "dependency")) {
            String groupId = text(dependency, "groupId");
            if (!groupId.startsWith("org.springframework") && !groupId.startsWith("com.vaadin")) {
                continue;
            }
            checked++;
            String scope = text(dependency, "scope");
            boolean optional = "true".equals(text(dependency, "optional"));
            if (!optional && !scope.equals("provided") && !scope.equals("test")) {
                leaking.add(groupId + ":" + text(dependency, "artifactId")
                        + " (scope " + (scope.isEmpty() ? "compile" : scope) + ")");
            }
        }
        assertTrue(checked > 0, "no Spring or Vaadin dependency found: is this the right pom?");
        assertTrue(leaking.isEmpty(), "Reaches consumers' classpath: " + leaking
                + " — make it provided, test or optional");
    }

    private static Element child(Element parent, String name) {
        return children(parent, name).get(0);
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> out = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element e && e.getTagName().equals(name)) {
                out.add(e);
            }
        }
        return out;
    }

    private static String text(Element parent, String name) {
        List<Element> found = children(parent, name);
        return found.isEmpty() ? "" : found.get(0).getTextContent().trim();
    }
}
