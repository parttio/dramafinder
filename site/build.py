#!/usr/bin/env python3
"""Stage the repository's Markdown documentation for MkDocs.

The documentation lives where agents and the jar need it (README, skills/,
docs/, META-INF/dramafinder/), so it is not moved for the site. This script
copies the published files into target/docs-site/src at their repository
paths, so the relative links between them keep working, rewrites links to
anything not published (sources, TODO.md, ...) into GitHub URLs, and writes
target/docs-site/mkdocs.yml with a navigation built from the file list.

Usage: python3 site/build.py && mkdocs build -f target/docs-site/mkdocs.yml
"""
import os
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "target" / "docs-site"
SRC = OUT / "src"
REPO = "parttio/dramafinder"
BLOB = f"https://github.com/{REPO}/blob/master/"
TREE = f"https://github.com/{REPO}/tree/master/"


def files(pattern):
    return sorted(p.relative_to(ROOT).as_posix() for p in ROOT.glob(pattern))


def title(path):
    """The first level-1 heading, or the file name."""
    in_front_matter = False
    for i, line in enumerate((ROOT / path).read_text(encoding="utf-8").splitlines()):
        if i == 0 and line.strip() == "---":
            in_front_matter = True
            continue
        if in_front_matter:
            if line.strip() == "---":
                in_front_matter = False
            continue
        if line.startswith("# "):
            return line[2:].strip()
    return Path(path).stem


def entries(paths, label=title):
    return [{label(p): p} for p in paths]


META = "src/main/resources/META-INF/dramafinder/"
NAV = [
    {"Home": "README.md"},
    {"Getting started": [
        {"Start here": META + "START-HERE.md"},
        {"Migrating to 2.0": META + "MIGRATION-2.0.md"},
        {"Glossary": "GLOSSARY.md"},
    ]},
    {"Reference": [
        {"API reference": "skills/vaadin-playwright-test/api-reference.md"},
        {"Agent helpers API reference": "skills/vaadin-playwright-screenshot/agent-api-reference.md"},
        {"Component to element mapping": "skills/vaadin-playwright-test/element-mapping.md"},
        {"Javadoc": "https://parttio.github.io/dramafinder/javadoc/"},
    ]},
    {"Component specifications": entries(
        files("docs/specifications/*.md"), lambda p: Path(p).stem)},
    {"Agent skills": [
        {"vaadin-playwright-test": "skills/vaadin-playwright-test/SKILL.md"},
        {"Project setup": "skills/vaadin-playwright-test/setup.md"},
        {"Testing guidelines": "skills/vaadin-playwright-test/TESTING.md"},
        {"vaadin-playwright-screenshot": "skills/vaadin-playwright-screenshot/SKILL.md"},
    ]},
    {"Architecture decisions": entries(files("docs/adr/*.md"))},
    {"Release notes": entries(
        list(reversed(files("docs/release-notes/*.md"))), lambda p: Path(p).stem)},
    {"Contributing": [
        {"Repository guidelines": "AGENTS.md"},
        *entries(files("docs/agents/*.md")),
    ]},
]


def nav_paths(nav):
    for item in nav:
        for value in item.values():
            if isinstance(value, list):
                yield from nav_paths(value)
            elif value.endswith(".md"):
                yield value


PUBLISHED = set(nav_paths(NAV))
LINK = re.compile(r"(\]\()([^)\s]+)(\))")


def rewrite(path, text):
    """Point links at unpublished repository files to GitHub."""
    base = Path(path).parent

    def fix(match):
        target = match.group(2)
        if re.match(r"^[a-z][a-z0-9+.-]*:|^#|^/", target):
            return match.group(0)
        file_part, _, anchor = target.partition("#")
        resolved = Path(os.path.normpath(base / file_part)).as_posix()
        if resolved in PUBLISHED or resolved.startswith(".."):
            return match.group(0)
        if not (ROOT / resolved).exists():
            return match.group(0)
        url = (TREE if (ROOT / resolved).is_dir() else BLOB) + resolved
        return match.group(1) + url + ("#" + anchor if anchor else "") + match.group(3)

    return LINK.sub(fix, text)


HEADING = re.compile(r"^(#{1,6}) +([A-Za-z][\w.]*)(.*)$", re.MULTILINE)


def anchor_headings(text):
    """Give a heading the short anchor a link in the page uses.

    The generated references link to "#gridelement" for the heading
    "GridElement `<vaadin-grid>`", whose slug includes the tag. Such headings
    get an explicit id equal to the linked anchor.
    """
    linked = set(re.findall(r"\]\(#([\w-]+)\)", text))

    def fix(match):
        hashes, word, rest = match.groups()
        anchor = word.lower()
        if not rest.strip() or anchor not in linked or "{" in rest:
            return match.group(0)
        return f"{hashes} {word}{rest} {{ #{anchor} }}"

    return HEADING.sub(fix, text)


def yaml_nav(nav, indent=2):
    lines = []
    for item in nav:
        for key, value in item.items():
            key = '"' + key.replace('"', '\\"') + '"'
            if isinstance(value, list):
                lines.append(" " * indent + f"- {key}:")
                lines.extend(yaml_nav(value, indent + 4))
            else:
                lines.append(" " * indent + f"- {key}: {value}")
    return lines


def main():
    if OUT.exists():
        shutil.rmtree(OUT)
    for path in sorted(PUBLISHED):
        dest = SRC / path
        dest.parent.mkdir(parents=True, exist_ok=True)
        text = (ROOT / path).read_text(encoding="utf-8")
        dest.write_text(anchor_headings(rewrite(path, text)),
                        encoding="utf-8")
    config = (ROOT / "site" / "mkdocs.yml").read_text(encoding="utf-8")
    config += "\nnav:\n" + "\n".join(yaml_nav(NAV)) + "\n"
    (OUT / "mkdocs.yml").write_text(config, encoding="utf-8")
    print(f"Staged {len(PUBLISHED)} pages in {SRC.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
