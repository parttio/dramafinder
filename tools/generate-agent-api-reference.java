///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21
//DEPS com.github.javaparser:javaparser-core:3.26.4

// Generates skills/vaadin-playwright-screenshot/agent-api-reference.md — the
// public API of the DramaFinder "agent" helpers used by the
// vaadin-playwright-screenshot skill (VisualVerificationTest, AgentReporting,
// AgentReport, AgentReportProvider, ComponentSnapshot) — straight from source,
// so it can never drift from the released API.
//
// Run locally:   jbang tools/generate-agent-api-reference.java
// In CI:         see .github/workflows/agent-api-reference.yml
//
// Like the element api-reference generator, it lives in / writes into the SKILL
// folder on purpose: the skill is the self-contained artifact that travels to
// consumer projects, so the reference must ship inside it — that is what lets an
// agent look the API up WITHOUT unzipping the DramaFinder sources jar.

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.javadoc.Javadoc;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

// Package-private on purpose: JBang compiles this under its hyphenated file name,
// and javac only allows a *public* top-level class to differ from the file name.
class GenerateAgentApiReference {

    static final Path REPO = Paths.get(System.getProperty("repo.dir", "."));
    static final Path AGENT_DIR = REPO.resolve("src/main/java/org/vaadin/addons/dramafinder/agent");
    static final Path OUT = REPO.resolve("skills/vaadin-playwright-screenshot/agent-api-reference.md");

    // VisualVerificationTest is the entry point (what a temp test extends), so it
    // leads; the rest follow alphabetically.
    static final List<String> ORDER = List.of("VisualVerificationTest");

    public static void main(String[] args) throws IOException {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(LanguageLevel.JAVA_21);

        Map<String, TypeDeclaration<?>> types = new TreeMap<>();
        List<Path> files;
        try (Stream<Path> s = Files.walk(AGENT_DIR)) {
            files = s.filter(p -> p.toString().endsWith(".java")).sorted().collect(Collectors.toList());
        }
        for (Path f : files) {
            CompilationUnit cu = StaticJavaParser.parse(f);
            for (TypeDeclaration<?> t : cu.getTypes()) {
                if (t.isPublic()) types.put(t.getNameAsString(), t);
            }
        }

        // Entry point(s) first, then the remainder alphabetically.
        List<String> names = new ArrayList<>(ORDER);
        types.keySet().stream().filter(n -> !ORDER.contains(n)).forEach(names::add);

        StringBuilder md = new StringBuilder();
        String version = readVersion();

        md.append("# DramaFinder Agent Helpers API Reference\n\n");
        md.append("> **Auto-generated from source — do not edit by hand.** ");
        md.append("Regenerate with `jbang tools/generate-agent-api-reference.java`.\n");
        md.append("> DramaFinder ").append(version).append(" — ")
          .append(types.size()).append(" agent helper types.\n\n");
        md.append("Public API of the `org.vaadin.addons.dramafinder.agent` helpers used by the ");
        md.append("**vaadin-playwright-screenshot** skill. Method one-liners come from Javadoc.\n\n");
        md.append("**Do not download or unzip the DramaFinder jar to discover this API — it is all here.**\n\n");
        md.append("A temp verification test extends `VisualVerificationTest`; the other types are ");
        md.append("its supporting cast (the failure-report writer, the JUnit extension, and the ");
        md.append("semantic component snapshot).\n\n");

        md.append("## Types\n\n");
        md.append(names.stream()
                .map(n -> "[" + n + "](#" + anchor(n) + ")")
                .collect(Collectors.joining(" · ")));
        md.append("\n\n");

        for (String n : names) {
            renderType(md, types.get(n));
        }

        Files.createDirectories(OUT.getParent());
        Files.writeString(OUT, md.toString());
        System.out.println("Wrote " + OUT + " (" + types.size() + " types)");
    }

    /** Render one type: heading, javadoc, hierarchy, constants, constructors, methods. */
    static void renderType(StringBuilder md, TypeDeclaration<?> t) {
        String name = t.getNameAsString();
        md.append("## ").append(name).append("\n\n");

        firstSentence(t).ifPresent(s -> md.append(s).append("\n\n"));

        List<String> ext = extendedNames(t);
        List<String> impl = implementedNames(t);
        boolean isIface = t instanceof ClassOrInterfaceDeclaration ci && ci.isInterface();
        // One hierarchy line: kind + extends/implements, only what applies.
        List<String> bits = new ArrayList<>();
        if (isIface) bits.add("*interface*");
        else if (t instanceof ClassOrInterfaceDeclaration cid && cid.isAbstract()) bits.add("*abstract class*");
        if (!ext.isEmpty()) bits.add("**extends** " + String.join(", ", ext));
        if (!impl.isEmpty()) bits.add("**implements** " + String.join(", ", impl));
        if (!bits.isEmpty()) md.append(String.join(" · ", bits)).append("\n\n");

        // Public constants.
        List<String> constants = t.getFields().stream()
                .filter(f -> f.isPublic() && f.isStatic() && f.isFinal())
                .flatMap(f -> f.getVariables().stream()
                        .map(v -> "`" + f.getElementType().asString() + " " + v.getNameAsString() + "`"))
                .collect(Collectors.toList());
        if (!constants.isEmpty()) {
            md.append("**Constants:** ").append(String.join(", ", constants)).append("\n\n");
        }

        // Public constructors.
        List<ConstructorDeclaration> ctors = t.getConstructors().stream()
                .filter(c -> c.getModifiers().contains(Modifier.publicModifier()))
                .collect(Collectors.toList());
        if (!ctors.isEmpty()) {
            md.append("**Constructors:**\n\n");
            for (ConstructorDeclaration c : ctors) {
                md.append("- `").append(ctorSig(c)).append("`");
                firstSentence(c).ifPresent(s -> md.append(" — ").append(s));
                md.append("\n");
            }
            md.append("\n");
        }

        // Public AND protected methods: an abstract base class exposes its
        // interaction API (open/shot/baseUrl on VisualVerificationTest) as
        // protected members that subclasses call. Interface members are
        // implicitly public. Drop @Override noise (documented on the supertype).
        List<MethodDeclaration> methods = t.getMethods().stream()
                .filter(m -> m.isPublic() || m.isProtected() || (isIface && !m.isPrivate() && !m.isStatic()))
                .filter(m -> !isOverride(m))
                .sorted(Comparator.comparing(MethodDeclaration::getNameAsString))
                .collect(Collectors.toList());
        List<MethodDeclaration> statics = methods.stream().filter(MethodDeclaration::isStatic).toList();
        List<MethodDeclaration> instance = methods.stream().filter(m -> !m.isStatic()).toList();

        if (!statics.isEmpty()) {
            md.append("**Static methods:**\n\n");
            statics.forEach(m -> appendMethod(md, m));
            md.append("\n");
        }
        if (!instance.isEmpty()) {
            md.append("**Methods:**\n\n");
            instance.forEach(m -> appendMethod(md, m));
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

    // ---- hierarchy helpers -------------------------------------------------

    static List<String> extendedNames(TypeDeclaration<?> t) {
        if (t instanceof ClassOrInterfaceDeclaration cid) {
            return cid.getExtendedTypes().stream().map(x -> x.getNameAsString()).collect(Collectors.toList());
        }
        return List.of();
    }

    static List<String> implementedNames(TypeDeclaration<?> t) {
        if (t instanceof ClassOrInterfaceDeclaration cid && !cid.isInterface()) {
            return cid.getImplementedTypes().stream().map(x -> x.getNameAsString()).collect(Collectors.toList());
        }
        return List.of();
    }

    static boolean isOverride(MethodDeclaration m) {
        return m.getAnnotationByName("Override").isPresent();
    }

    // ---- javadoc -----------------------------------------------------------

    static Optional<String> firstSentence(NodeWithJavadoc<?> n) {
        Optional<Javadoc> jd = n.getJavadoc();
        if (jd.isEmpty()) return Optional.empty();
        String text = jd.get().getDescription().toText();
        if (text == null || text.isBlank()) return Optional.empty();
        text = text.replaceAll("\\s+", " ").trim();
        text = text.replaceAll("\\{@\\w+\\s+([^}]+)}", "$1");
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
            if (m.find()) return m.group(1);
        } catch (IOException ignored) {}
        return "";
    }
}
