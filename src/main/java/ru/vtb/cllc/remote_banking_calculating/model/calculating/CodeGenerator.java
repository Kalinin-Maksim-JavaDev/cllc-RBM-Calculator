package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
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

    @SneakyThrows
    public CodeGenerator() {
        this.tmpdir = Files.createTempDirectory("generated");
        ;
    }

    Class createFunction(String name, String exp) throws CodeGenerateException {

        log.debug("try generate class-function: '%s' - name; '%s' - lambda", name, exp);

        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(exp, "expression must not be null");

        exp = StringUtils.trimAllWhitespace(exp);

        checkExpression(exp);

        var javaClassName = "indicator-".concat(name);

        var classBody = new StringBuilder()
                .append(String.format("public class %s{\n", capitalize(name.toLowerCase())))
                .append("\n")
                .append("   public long apply(long a, long b) {\n")
                .append("       return op.applyAsLong(a, b);\n")
                .append("   }\n")
                .append("}")
                .toString();

        var tmpFile = writeDown(javaClassName, classBody);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(tmpdir.toFile()));
        // Compile the file
        compiler.getTask(null,
                        fileManager,
                        null,
                        null,
                        null,
                        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(tmpFile)))
                .call();
        fileManager.close();

        var clazz = new URLClassLoader(new URL[]{tmpdir.toUri().toURL()}).loadClass("Hello");

        return clazz;
    }

    private void checkExpression(String exp) {
        Matcher m = LAMBDA_PATTERN.matcher(exp);
        var validInput = m.matches();
        if (!validInput)
            throw new CodeGenerateException("bad expression: \"" + exp + "\"");
    }

    @SneakyThrows
    private File writeDown(String name, String text) {
        var tmpFile = tmpdir.resolve(name.concat(".java")).toFile();
        var writer = new FileWriter(tmpFile);
        writer.write(text);
        writer.close();
        return tmpFile;
    }

    @AllArgsConstructor
    private static class Lambda {
        final private String[] args;
        final private String body;

        static Lambda of(String exp) {
            var parts = exp.split("->");

            var argst = parts[0];
            var body = parts[1];
            String[] args = argst.split(",");

            return new Lambda(args, body);
        }
    }

    public static void main(String[] args) {
        var codeGenerator = new CodeGenerator();
        try {
            codeGenerator.createFunction("sum", " a_2, b333 -> a_2+  b333");
        } catch (CodeGenerateException e) {
            e.printStackTrace();
        }
    }
}
