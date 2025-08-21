package rpp.poi.playground;


import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: Launcher <GeneratorClassName> <themeName> [--clean]");
            return;
        }

        String generatorName = args[0];
        String themeName = args[1];
        boolean clean = args.length > 2 && args[2].equals("--clean");

        if (clean) {
            OutputManager.cleanOutputs();
        }

        String fqcn = "rpp.poi.playground.generation." + generatorName;
        Class<?> clazz = Class.forName(fqcn);

        if (!DocumentGenerator.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(generatorName + " does not implement DocumentGenerator");
        }

        DocumentGenerator generator = (DocumentGenerator) clazz.getDeclaredConstructor().newInstance();

        Path baseDir = Paths.get("").toAbsolutePath().getParent();
        Path themeDoc = baseDir.resolve("themes").resolve(themeName).resolve("theme.docx");
        Path outFile = OutputManager.getOutputFile(themeName, generatorName);

        try (InputStream in = Files.newInputStream(themeDoc);
             OutputStream out = Files.newOutputStream(outFile)) {
            generator.generate(in, out, args);
        }

        OutputManager.openInWord(outFile);
    }
}
