///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21
//DEPS com.github.javaparser:javaparser-core:3.26.4

// Generates skills/vaadin-playwright-test/api-reference.md — a dense, complete
// signature index of every DramaFinder element wrapper — straight from source,
// so it can never drift from the released API.
//
// Run locally:   jbang tools/generate-api-reference.java
// In CI:         see .github/workflows/api-reference.yml
//
// It lives in the SKILL folder on purpose: the skill is the self-contained
// artifact that travels to consumer projects, so the reference must be inside
// it (a copy in docs/ would not ship with the skill). llms.txt links to this
// same path via a raw GitHub URL.

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.javadoc.Javadoc;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

// Package-private on purpose: JBang compiles this under its hyphenated file name,
// and javac only allows a *public* top-level class to differ from the file name.
class GenerateApiReference {

    static final Path REPO = Paths.get(System.getProperty("repo.dir", "."));
    static final Path ELEMENT_DIR = REPO.resolve("src/main/java/org/vaadin/addons/dramafinder/element");
    static final Path OUT = REPO.resolve("skills/vaadin-playwright-test/api-reference.md");

    public static void main(String[] args) throws IOException {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(LanguageLevel.JAVA_21);

        // Parse every top-level type, keyed by simple name.
        Map<String, TypeDeclaration<?>> elements = new TreeMap<>(); // element wrappers (alpha)
        Map<String, TypeDeclaration<?>> mixins = new TreeMap<>();    // element/shared/*
        TypeDeclaration<?> base = null;                              // VaadinElement

        List<Path> files;
        try (Stream<Path> s = Files.walk(ELEMENT_DIR)) {
            files = s.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
        }
        for (Path f : files) {
            // Skip the internal utils package — not part of the public test API.
            if (f.getParent().getFileName().toString().equals("utils")) continue;
            CompilationUnit cu = StaticJavaParser.parse(f);
            for (TypeDeclaration<?> t : cu.getTypes()) {
                if (!t.isPublic()) continue;
                String name = t.getNameAsString();
                if (t instanceof AnnotationDeclaration) continue;      // @PlaywrightElement marker
                boolean shared = f.getParent().getFileName().toString().equals("shared");
                if (shared) { mixins.put(name, t); }
                else if (name.equals("VaadinElement")) { base = t; }
                else { elements.put(name, t); }
            }
        }

        StringBuilder md = new StringBuilder();
        String version = readVersion();

        md.append("# DramaFinder API Reference\n\n");
        md.append("> **Auto-generated from source — do not edit by hand.** ");
        md.append("Regenerate with `jbang tools/generate-api-reference.java`.\n");
        md.append("> DramaFinder ").append(version).append(" — ")
          .append(elements.size()).append(" element wrappers.\n\n");
        md.append("Complete public API of every DramaFinder element wrapper. Each element lists ");
        md.append("the shared mixin interfaces it implements; those interfaces' methods are ");
        md.append("documented once under **Shared mixins** at the end (not repeated per element). ");
        md.append("Method one-liners come from Javadoc.\n\n");
        md.append("**Do not download or unzip the DramaFinder jar to discover its API — it is all here.**\n\n");

        // Table of contents for the elements.
        md.append("## Elements\n\n");
        md.append(elements.keySet().stream()
                .map(n -> "[" + n + "](#" + anchor(n) + ")")
                .collect(Collectors.joining(" · ")));
        md.append("\n\n");

        for (var e : elements.entrySet()) {
            renderType(md, e.getValue(), 3, true);
        }

        md.append("## Shared mixins\n\n");
        md.append("Behaviours composed into elements via `implements`. An element that lists a ");
        md.append("mixin below exposes all of that mixin's methods.\n\n");
        for (var e : mixins.entrySet()) {
            renderType(md, e.getValue(), 3, false);
        }

        if (base != null) {
            md.append("## Base class\n\n");
            md.append("Every element extends `VaadinElement`; these methods are available on all of them.\n\n");
            renderType(md, base, 3, false);
        }

        Files.createDirectories(OUT.getParent());
        Files.writeString(OUT, md.toString());
        System.out.println("Wrote " + OUT + " (" + elements.size() + " elements, "
                + mixins.size() + " mixins)");
    }

    /** Render one type: heading, tag, javadoc, hierarchy, constants, constructors, methods, nested types. */
    static void renderType(StringBuilder md, TypeDeclaration<?> t, int level, boolean isElement) {
        String name = t.getNameAsString();
        md.append("#".repeat(level)).append(" ").append(name);
        String tag = tagOf(t);
        if (tag != null) md.append("  `<").append(tag).append(">`");
        md.append("\n\n");

        firstSentence(t).ifPresent(s -> md.append(s).append("\n\n"));

        // Hierarchy hints.
        List<String> ext = extendedNames(t);
        List<String> impl = implementedNames(t);
        if (t instanceof ClassOrInterfaceDeclaration cid && cid.isAbstract()) {
            md.append("*abstract* ");
        }
        if (!ext.isEmpty()) md.append("**Extends:** ").append(String.join(", ", ext)).append("  \n");
        if (!impl.isEmpty()) md.append("**Implements:** ").append(String.join(", ", impl)).append("  \n");
        if (!ext.isEmpty() || !impl.isEmpty()) md.append("\n");

        // Public constants.
        List<String> constants = t.getFields().stream()
                .filter(f -> f.isPublic() && f.isStatic() && f.isFinal())
                .flatMap(f -> f.getVariables().stream()
                        .map(v -> "`" + f.getElementType().asString() + " " + v.getNameAsString()
                                + (v.getInitializer().map(i -> " = " + i).orElse("")) + "`"))
                .collect(Collectors.toList());
        if (!constants.isEmpty()) {
            md.append("**Constants:** ").append(String.join(", ", constants)).append("\n\n");
        }

        // Public constructors.
        List<ConstructorDeclaration> ctors = t.getConstructors().stream()
                .filter(NodeWithModifiersPublic()).collect(Collectors.toList());
        if (!ctors.isEmpty()) {
            md.append("**Constructors:**\n\n");
            for (ConstructorDeclaration c : ctors) {
                md.append("- `").append(ctorSig(c)).append("`");
                firstSentence(c).ifPresent(s -> md.append(" — ").append(s));
                md.append("\n");
            }
            md.append("\n");
        }

        // Public methods, split into static factories and instance methods.
        boolean isIface = t instanceof ClassOrInterfaceDeclaration c2 && c2.isInterface();
        List<MethodDeclaration> methods = t.getMethods().stream()
                .filter(m -> m.isPublic() || (isIface && !m.isStatic() && !m.isPrivate()))
                .filter(m -> !isOverride(m)) // mixin impls / internal locators — covered by the mixin section
                .collect(Collectors.toList());
        List<MethodDeclaration> statics = methods.stream().filter(MethodDeclaration::isStatic).collect(Collectors.toList());
        List<MethodDeclaration> instance = methods.stream().filter(m -> !m.isStatic()).collect(Collectors.toList());

        if (!statics.isEmpty()) {
            md.append("**Static factory methods:**\n\n");
            statics.forEach(m -> appendMethod(md, m));
            md.append("\n");
        }
        if (!instance.isEmpty()) {
            md.append(isElement ? "**Methods:**\n\n" : "**Methods:**\n\n");
            instance.forEach(m -> appendMethod(md, m));
            md.append("\n");
        }
        if (statics.isEmpty() && instance.isEmpty() && constants.isEmpty() && ctors.isEmpty()) {
            md.append("*Marker interface — composes the mixins listed above; no own methods.*\n\n");
        }

        // Public nested types (e.g. GridElement.RowElement / CellElement).
        List<TypeDeclaration<?>> nested = t.getMembers().stream()
                .filter(m -> m instanceof TypeDeclaration<?>)
                .map(m -> (TypeDeclaration<?>) m)
                .filter(td -> td.isPublic())
                .collect(Collectors.toList());
        for (TypeDeclaration<?> n : nested) {
            // Render nested with a qualified heading so the anchor is unique.
            String saved = n.getNameAsString();
            md.append("#".repeat(level + 1)).append(" ").append(name).append(".").append(saved).append("\n\n");
            firstSentence(n).ifPresent(s -> md.append(s).append("\n\n"));
            n.getMethods().stream()
                    .filter(m -> m.isPublic() && !isOverride(m))
                    .forEach(m -> appendMethod(md, m));
            md.append("\n");
        }
    }

    static void appendMethod(StringBuilder md, MethodDeclaration m) {
        md.append("- `").append(methodSig(m)).append("`");
        firstSentence(m).ifPresent(s -> md.append(" — ").append(s));
        md.append("\n");
    }

    // ---- signature helpers -------------------------------------------------

    static String methodSig(MethodDeclaration m) {
        return m.getType().asString() + " " + m.getNameAsString() + "(" + params(m.getParameters()) + ")";
    }

    static String ctorSig(ConstructorDeclaration c) {
        return c.getNameAsString() + "(" + params(c.getParameters()) + ")";
    }

    static String params(List<Parameter> ps) {
        return ps.stream()
                .map(p -> p.getType().asString() + (p.isVarArgs() ? "..." : "") + " " + p.getNameAsString())
                .collect(Collectors.joining(", "));
    }

    // ---- annotation / hierarchy helpers ------------------------------------

    /** Resolve the @PlaywrightElement tag, following constant references within the same type. */
    static String tagOf(TypeDeclaration<?> t) {
        var ann = t.getAnnotationByName("PlaywrightElement");
        if (ann.isEmpty()) return null;
        AnnotationExpr a = ann.get();
        Expression value = null;
        if (a instanceof SingleMemberAnnotationExpr s) value = s.getMemberValue();
        else if (a instanceof NormalAnnotationExpr n) {
            value = n.getPairs().stream().filter(p -> p.getNameAsString().equals("value"))
                    .map(MemberValuePair::getValue).findFirst().orElse(null);
        }
        return resolveString(t, value);
    }

    /** Best-effort constant folding: string literal, or a `FIELD` / `Type.FIELD` reference to a local constant. */
    static String resolveString(TypeDeclaration<?> t, Expression value) {
        if (value == null) return null;
        if (value.isStringLiteralExpr()) return value.asStringLiteralExpr().getValue();
        String constName = null;
        if (value.isNameExpr()) constName = value.asNameExpr().getNameAsString();
        else if (value.isFieldAccessExpr()) constName = value.asFieldAccessExpr().getNameAsString();
        if (constName != null) {
            for (FieldDeclaration f : t.getFields()) {
                for (VariableDeclarator v : f.getVariables()) {
                    if (v.getNameAsString().equals(constName) && v.getInitializer().isPresent()
                            && v.getInitializer().get().isStringLiteralExpr()) {
                        return v.getInitializer().get().asStringLiteralExpr().getValue();
                    }
                }
            }
        }
        return null; // unresolved constant from another class — omit rather than print noise
    }

    static List<String> extendedNames(TypeDeclaration<?> t) {
        if (t instanceof ClassOrInterfaceDeclaration cid) {
            return cid.getExtendedTypes().stream().map(x -> x.getNameAsString()).collect(Collectors.toList());
        }
        return List.of();
    }

    static List<String> implementedNames(TypeDeclaration<?> t) {
        if (t instanceof ClassOrInterfaceDeclaration cid) {
            var list = cid.isInterface() ? cid.getExtendedTypes() : cid.getImplementedTypes();
            if (cid.isInterface()) return List.of(); // handled via extends for interfaces
            return list.stream().map(x -> x.getNameAsString()).collect(Collectors.toList());
        }
        return List.of();
    }

    static boolean isOverride(MethodDeclaration m) {
        return m.getAnnotationByName("Override").isPresent();
    }

    static java.util.function.Predicate<ConstructorDeclaration> NodeWithModifiersPublic() {
        return c -> c.getModifiers().contains(Modifier.publicModifier());
    }

    // ---- javadoc -----------------------------------------------------------

    static Optional<String> firstSentence(NodeWithJavadoc<?> n) {
        Optional<Javadoc> jd = n.getJavadoc();
        if (jd.isEmpty()) return Optional.empty();
        String text = jd.get().getDescription().toText();
        if (text == null || text.isBlank()) return Optional.empty();
        // Collapse whitespace, stop at the first blank line (before <p> details).
        text = text.replaceAll("\\s+", " ").trim();
        // Strip leftover inline-tag braces that toText may keep.
        text = text.replaceAll("\\{@\\w+\\s+([^}]+)}", "$1");
        // Truncate at the first real sentence end, skipping "e.g." / "i.e." abbreviations.
        int from = 0;
        while (true) {
            int dot = text.indexOf(". ", from);
            if (dot < 0) break;
            String before = text.substring(0, dot);
            if (before.endsWith("e.g") || before.endsWith("i.e")) { from = dot + 2; continue; }
            text = text.substring(0, dot + 1);
            break;
        }
        return Optional.of(text.trim());
    }

    static String anchor(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
    }

    static String readVersion() {
        try {
            String pom = Files.readString(REPO.resolve("pom.xml"));
            var m = java.util.regex.Pattern.compile("<version>([^<]+)</version>").matcher(pom);
            if (m.find()) return m.group(1); // first <version> is the project version
        } catch (IOException ignored) {}
        return "";
    }
}
