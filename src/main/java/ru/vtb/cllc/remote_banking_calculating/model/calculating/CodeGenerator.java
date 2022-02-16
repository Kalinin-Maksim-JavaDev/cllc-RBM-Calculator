package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.AllArgsConstructor;
import lombok.Data;
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

import static java.lang.String.format;
import static org.springframework.util.StringUtils.capitalize;

@Log4j2
public class CodeGenerator {

    private static final Pattern LAMBDA_PATTERN = Pattern.compile("((,)*([a-zA-Z1-9_])*)*->(.)*");
    private final Path tmpdir;
    private final JavaCompiler compiler;

    @SneakyThrows
    public CodeGenerator() {
        tmpdir = Files.createTempDirectory("generated");
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    Class createFunction(String name, String exp, String type) {

        log.debug("try generate class-function: '%s' - name; '%s' - operator", name, exp);

        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(exp, "expression must not be null");

        Operator operator = Operator.of(exp, type.toLowerCase());

        var javaClassName = "indicator-".concat(name);

        var bodyBuilder = new StringBuilder()
                .append(format("public class %s{\n", capitalize(name.toLowerCase())))
                .append("\n")
                .append(format("   public %s apply(Record record) {\n", operator.getType()));
        for (var arg : operator.getArgs())
            bodyBuilder.append(format("final %s %s = record.%s;\n", arg.getType(), arg.getName(), arg.getName()));
        bodyBuilder.append(format("       return %s;\n", operator.body))
                .append("   }\n")
                .append("}")
                .toString();

        var classBody = bodyBuilder.toString();

        try {
            return new URLClassLoader(new URL[]{compile(writeDown(javaClassName, classBody))}).loadClass("Hello");
        } catch (ClassNotFoundException e) {
            throw new CodeGenerateException(e.getMessage());
        }
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

    @Data
    @AllArgsConstructor
    private static class Operator {
        private final String type;
        private final Argument[] args;
        private final String body;

        static Operator of(String exp, String type) {

            exp = StringUtils.trimAllWhitespace(exp);

            checkExpression(exp);

            var parts = exp.split("->");

            var body = parts[1];
            var names = parts[0].split(",");
            Argument[] args = new Argument[names.length];
            for (int i = 0; i < names.length; i++)
                args[i] = new Argument(type, names[i]);

            return new Operator(type, args, body);
        }

        public String getSignature() {
            var result = new StringBuilder();
            for (var p : args)
                result.append(type + p);
            return result.toString();
        }

        private static void checkExpression(String exp) {
            Matcher m = LAMBDA_PATTERN.matcher(exp);
            var validInput = m.matches();
            if (!validInput)
                throw new CodeGenerateException("bad expression: \"" + exp + "\"");
        }
    }

    @Data
    @AllArgsConstructor
    private static class Argument {
        String type;
        String name;
    }

    public static void main(String[] args) {
        var codeGenerator = new CodeGenerator();
        try {
            codeGenerator.createFunction("sum", " a_2, b333 -> a_2+  b333", "long");
        } catch (CodeGenerateException e) {
            e.printStackTrace();
        }
    }
}
