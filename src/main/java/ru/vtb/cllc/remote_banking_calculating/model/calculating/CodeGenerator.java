package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import ru.vtb.cllc.remote_banking_calculating.model.Record;
import ru.vtb.cllc.remote_banking_calculating.model.enity.IndicatorFormula;
import ru.vtb.cllc.remote_banking_calculating.model.indicator.Indicator;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Log4j2
public class CodeGenerator {

    private static final Pattern LAMBDA_PATTERN = Pattern.compile("((,)*([a-zA-Z1-9_])*)*->(.)*");
    private static final String INDICATOR_INTERFACE_NAME = Indicator.class.getName();
    private static final String INDICATOR_FORMULA_NAME = IndicatorFormula.class.getName();
    private final Path tmpdir;
    private final JavaCompiler compiler;
    private final List<File> files;

    @SneakyThrows
    public CodeGenerator() {
        tmpdir = Files.createTempDirectory("generated");
        files = new ArrayList<>();
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    void add(String name, String exp, String type, String sourceName) {

        log.debug("try generate indicator: '{}' - name; '{}' - operator", name, exp);

        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(exp, "expression must not be null");

        Operator operator = Operator.of(exp, type);

        var javaClassName = name.concat(".java");

        var bodyBuilder = new StringBuilder()
                .append(format("public class %s extends %s implements %s{\n", name, INDICATOR_FORMULA_NAME, INDICATOR_INTERFACE_NAME))
                .append("\n")
                .append(format("    public %s apply(%s src) {\n", operator.getType(), sourceName));
        for (var arg : operator.getArgs())
            bodyBuilder.append(format("        final %s %s = src.%s;\n", arg.getType(), arg.getName(), arg.getName()));
        bodyBuilder
                .append(format("        return %s;\n", operator.body))
                .append("    }\n")
                .append("}")
                .toString();

        var classBody = bodyBuilder.toString();

        files.add(writeDown(javaClassName, classBody));
    }

    URLClassLoader compile() {
        compile(files);
        try {
            return new URLClassLoader(new URL[]{tmpdir.toUri().toURL()});
        } catch (Exception e) {
            throw new CodeGenerateException(e.getMessage());
        }
    }

    private File writeDown(String fileName, String text) {
        var tmpFile = tmpdir.resolve(fileName).toFile();

        try (var writer = new FileWriter(tmpFile);) {
            writer.write(text);
            return tmpFile;
        } catch (IOException e) {
            throw new CodeGenerateException(e.getMessage());
        }
    }

    private void compile(List<File> files) {
        try (var fileManager = compiler.getStandardFileManager(null, null, null);) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                    Arrays.asList(tmpdir.toFile()));
            compiler.getTask(null,
                            fileManager,
                            null,
                            null,
                            null,
                            fileManager.getJavaFileObjectsFromFiles(files))
                    .call();
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

    @SneakyThrows
    public static void main(String[] args) {
        var codeGenerator = new CodeGenerator();
        codeGenerator.add("AHT", " t_ring , t_inb , t_hold ,  t_acw, n_inb -> (t_ring + t_inb + t_hold + t_acw) / n_inb", "long", Record.class.getName());
        var clazz = codeGenerator.compile().loadClass("AHT");
        Indicator indicator = (Indicator) clazz.getDeclaredConstructor().newInstance();
        var record = new Record();
        record.t_ring = 10;
        record.n_inb = 2;
        System.out.println(indicator.apply(record));
    }
}