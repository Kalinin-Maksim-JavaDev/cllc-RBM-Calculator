package ru.vtb.cllc.remote_banking_calculating.model.calculating;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class DynamicCompilation {

    public static void main(String[] args) throws Exception {
        // create the source
        Path tmpdir = Files.createTempDirectory("generated");
        Path tmpFile = tmpdir.resolve("Hello.java");

        FileWriter writer = new FileWriter(tmpFile.toFile());
        writer.write(
                "public class Hello {\n" +
                        "\n" +
                        "        java.util.function.LongBinaryOperator op = (a, b) -> a - b;\n" +
                        "\n" +
                        "        public long doit(long a, long b) {\n" +
                        "            return op.applyAsLong(a, b);\n" +
                        "        }\n" +
                        "    }"
        );
        writer.close();

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
                        fileManager.getJavaFileObjectsFromFiles(Arrays.asList(tmpFile.toFile())))
                .call();
        fileManager.close();


        System.out.println(tmpFile);
        try {

            Class thisClass = new URLClassLoader(new URL[]{tmpdir.toUri().toURL()}).loadClass("Hello");
            Object iClass = thisClass.getDeclaredConstructor().newInstance();

            Class params[] = {long.class, long.class};
            Method thisMethod = thisClass.getDeclaredMethod("doit", params);
            Object result = thisMethod.invoke(iClass, 1, 1);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}