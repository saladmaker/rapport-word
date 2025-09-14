package mf.dgb.rpp.testing;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

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

        String fqcn = "mf.dgb.rpp.testing." + generatorName;
        Class<?> clazz = Class.forName(fqcn);

        if (!DocumentGenerator.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(generatorName + " does not implement DocumentGenerator");
        }

        DocumentGenerator generator = (DocumentGenerator) clazz.getDeclaredConstructor().newInstance();

        Path baseDir = Paths.get("").toAbsolutePath();
        Path themeDoc = baseDir.resolve("themes").resolve(themeName).resolve("theme.docx");
        Path outFile = OutputManager.getOutputFile(themeName, generatorName);

        // Open theme.docx as XWPFDocument
        try (InputStream in = Files.newInputStream(themeDoc);
                XWPFDocument document = new XWPFDocument(in);
                OutputStream out = Files.newOutputStream(outFile)) {

            // Let the generator modify the document
            generator.generate(document);

            // Save the modified document to outFile
            document.write(out);

        }
        OutputManager.openInWord(outFile);
    }
}
