#!/bin/sh
# SessionStart hook installed with the vaadin-playwright-test plugin.
#
# Skill matching is a heuristic over whatever is in context: if a task (e.g.
# a GitHub issue body) never mentions Vaadin, Playwright, or tests, nothing
# signals the model to load the skill. This hook makes the signal come from
# the environment instead — when the project declares a DramaFinder
# dependency, it injects a standing reminder into the session context.
if grep -qs '<artifactId>dramafinder</artifactId>' pom.xml */pom.xml 2>/dev/null; then
  cat <<'EOF'
This project depends on the DramaFinder Playwright testing library. For any
integration/IT test work (writing, editing, or running tests for Vaadin
views), load the vaadin-playwright-test skill before writing the test.
EOF
fi
exit 0
