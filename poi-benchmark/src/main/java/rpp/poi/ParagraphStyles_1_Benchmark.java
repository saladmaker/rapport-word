package rpp.poi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import rpp.poi.playground.generation.ParagraphStyles_1;

import org.apache.poi.xwpf.usermodel.XWPFDocument;


@BenchmarkMode({ Mode.Throughput, Mode.AverageTime })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(1)
public class ParagraphStyles_1_Benchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        ParagraphStyles_1 generator;
        byte[] fatTemplate;
        byte[] slimTemplate;
        byte[] extraSlimTemplate;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            generator = new ParagraphStyles_1();
            fatTemplate = loadResource("fat.docx");
            slimTemplate = loadResource("slim.docx");
            extraSlimTemplate = loadResource("extrac-slim.docx");
        }

        private byte[] loadResource(String name) throws Exception {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
                if (in == null) {
                    throw new RuntimeException("Resource " + name + " not found!");
                }
                return in.readAllBytes();
            }
        }
    }

    @Benchmark
    public void testGenerateFat(BenchmarkState state) throws Exception {
        try (InputStream in = new ByteArrayInputStream(state.fatTemplate);
             XWPFDocument document = new XWPFDocument(in)) {

            state.generator.generate(document);
        }
    }

    @Benchmark
    public void testGenerateSlim(BenchmarkState state) throws Exception {
        try (InputStream in = new ByteArrayInputStream(state.slimTemplate);
             XWPFDocument document = new XWPFDocument(in)) {

            state.generator.generate(document);
        }
    }

    @Benchmark
    public void testGenerateExtraSlim(BenchmarkState state) throws Exception {
        try (InputStream in = new ByteArrayInputStream(state.extraSlimTemplate);
             XWPFDocument document = new XWPFDocument(in)) {

            state.generator.generate(document);
        }
    }
}

