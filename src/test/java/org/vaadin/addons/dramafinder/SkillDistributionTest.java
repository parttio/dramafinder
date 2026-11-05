package org.vaadin.addons.dramafinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Keeps the skill distribution manifests in step with the {@code skills/}
 * folder: every skill must be listed in the Claude Code marketplace and in the
 * Tessl tile, have its own plugin manifest, and the tile must carry the version
 * of the main skill. A skill missing from a manifest is invisible to everyone
 * installing through it, and nothing else would notice.
 */
class SkillDistributionTest {

    private static final Pattern VERSION = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"");

    @Test
    void everySkillIsDistributed() throws IOException {
        String marketplace = Files.readString(Path.of(".claude-plugin/marketplace.json"));
        String tile = Files.readString(Path.of("tile.json"));
        List<String> problems = new ArrayList<>();
        for (String skill : skills()) {
            if (!marketplace.contains("\"source\": \"./skills/" + skill + "\"")) {
                problems.add(skill + " is not in .claude-plugin/marketplace.json");
            }
            if (!tile.contains("\"path\": \"skills/" + skill + "/SKILL.md\"")) {
                problems.add(skill + " is not in tile.json");
            }
            if (!Files.exists(pluginManifest(skill))) {
                problems.add(skill + " has no .claude-plugin/plugin.json");
            }
        }
        assertEquals(List.of(), problems);
    }

    @Test
    void tileVersionFollowsTheTestSkill() throws IOException {
        String tileVersion = version(Path.of("tile.json"));
        String pluginVersion = version(pluginManifest("vaadin-playwright-test"));
        assertEquals(pluginVersion, tileVersion,
                "tile.json must carry the vaadin-playwright-test plugin version");
    }

    private static List<String> skills() throws IOException {
        try (Stream<Path> dirs = Files.list(Path.of("skills"))) {
            List<String> skills = dirs.filter(d -> Files.exists(d.resolve("SKILL.md")))
                    .map(d -> d.getFileName().toString())
                    .sorted()
                    .toList();
            assertTrue(!skills.isEmpty(), "no skills found under skills/");
            return skills;
        }
    }

    private static Path pluginManifest(String skill) {
        return Path.of("skills", skill, ".claude-plugin", "plugin.json");
    }

    private static String version(Path manifest) throws IOException {
        Matcher m = VERSION.matcher(Files.readString(manifest));
        assertTrue(m.find(), "no version in " + manifest);
        return m.group(1);
    }
}
