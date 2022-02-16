package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import javax.tools.JavaCompiler;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.capitalize;

@Log4j2
public class CodeGenerator {

    private static final Pattern LAMBDA_PATTERN = Pattern.compile("((,)*([a-zA-Z1-9_])*)*->");
    private final Path tmpdir;
    private final JavaCompiler compiler;

    @SneakyThrows
    public CodeGenerator() {
        tmpdir = Files.createTempDirectory("generated");
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    Class createFunction(String name, String exp, Class<?> type) {

        log.debug("try generate class-function: '%s' - name; '%s' - lambda", name, exp);

        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(exp, "expression must not be null");

        Lambda lambda = Lambda.of(exp);

        var javaClassName = "indicator-".concat(name);

        var classBody = new StringBuilder()
                .append(String.format("public class %s{\n", capitalize(name.toLowerCase())))
                .append("\n")
                .append("   public long apply(long a, long b) {\n")
                .append("       return op.applyAsLong(a, b);\n")
                .append("   }\n")
                .append("}")
                .toString();

        Class<?> clazz = null;
        try {
            clazz = new URLClassLoader(new URL[]{compile(writeDown(javaClassName, classBody))}).loadClass("Hello");
        } catch (ClassNotFoundException e) {
            throw new CodeGenerateException(e.getMessage());
        }

        return clazz;
    }

    private File writeDown(String name, String text) {
        var tmpFile = tmpdir.resolve(name.concat(".java")).toFile();

        try (var writer = new FileWriter(tmpFile);) {
            writer.write(text);
            return tmpFile;
        } catch (IOException e) {
            throw new CodeGenerateException(e.getMessage());
        }
    }

    private URL compile(File tmpFile) {
        try (var fileManager = compiler.getStandardFileManager(null, null, null);) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                    Arrays.asList(tmpdir.toFile()));
            compiler.getTask(null,
                            fileManager,
                            null,
                            null,
                            null,
                            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(tmpFile)))
                    .call();
            return tmpdir.toUri().toURL();
        } catch (IOException e) {
            throw new CodeGenerateException(e.getMessage());
        }
    }

    @AllArgsConstructor
    private static class Lambda {
        final private String[] args;
        final private String body;

        static Lambda of(String exp) {

            exp = StringUtils.trimAllWhitespace(exp);

            checkExpression(exp);

            var parts = exp.split("->");

            var argst = parts[0];
            var body = parts[1];
            String[] args = argst.split(",");

            return new Lambda(args, body);
        }

        private static void checkExpression(String exp) {
            Matcher m = LAMBDA_PATTERN.matcher(exp);
            var validInput = m.matches();
            if (!validInput)
                throw new CodeGenerateException("bad expression: \"" + exp + "\"");
        }
    }

    public static void main(String[] args) {
        var codeGenerator = new CodeGenerator();
        try {
            codeGenerator.createFunction("sum", " a_2, b333 -> a_2+  b333", Long.class);
        } catch (CodeGenerateException e) {
            e.printStackTrace();
        }
    }
}
