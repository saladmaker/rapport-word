package rpp.poi.playground.generation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import rpp.poi.model.AuSujetDuPortefeuille;
import rpp.poi.model.CartographieProgrammesPortefeuille;
import rpp.poi.model.FichePortefeuille;
import rpp.poi.model.GenerationContext;
import rpp.poi.model.LaMission;
import rpp.poi.model.LanguageDirection;
import rpp.poi.model.LeMinistere;
import rpp.poi.model.Mission;
import rpp.poi.model.ProgrammeRepartition;
import rpp.poi.model.ProgrammeStructure;
import rpp.poi.playground.DocumentGenerator;

public class ArabicModel implements DocumentGenerator {

    @Override
    public void generate(XWPFDocument document) throws Exception {
        var laMission = LaMission.builder()
                .intro("""
                        تلتزم الوزارة، للسنة القادمة، بتعزيز جودة الخدمات العمومية لفائدة المواطنين.
                        كما تخطط لتحديث البنية التحتية الرقمية من أجل تسهيل الوصول إلى الإجراءات الإدارية.
                        سيُولى اهتمام خاص للشفافية ومكافحة الفساد.
                        وسيظلّ التنمية المستدامة في صميم السياسات العمومية، مع برامج طموحة من أجل الانتقال البيئي.
                        وأخيراً، سيتم إنشاء شراكات استراتيجية لتحفيز الابتكار والاستثمار في القطاعات الرئيسية.
                        """.replaceAll("\\R", " "))
                .addMission(new Mission(
                        "التحول الرقمي: نشر منصات رقمية لتبسيط الإجراءات الإدارية.",
                        List.of("إنشاء شباك موحد عبر الإنترنت",
                                "أتمتة الإجراءات الداخلية")))
                .addMission(new Mission(
                        "زيادة سهولة الوصول: تحسين وصول المواطنين إلى الخدمات العمومية، بما في ذلك في المناطق الريفية.",
                        List.of("نشر الأكشاك التفاعلية",
                                "تعزيز الخدمات المتنقلة")))
                .addMission(new Mission(
                        "الشفافية والحوكمة: تعزيز آليات الرقابة ومكافحة الفساد بشكل فعال.",
                        List.of("النشر السنوي للتقارير المالية",
                                "إنشاء منصة للتبليغ المجهول")))
                .build();

        var imageBytes = FrenchModel.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg").readAllBytes();
        var leMinistere = LeMinistere.builder().image(imageBytes).build();

        CartographieProgrammesPortefeuille cProgrammesPortefeuille = CartographieProgrammesPortefeuille.builder()
                .addProgrammeStructure(new ProgrammeStructure(
                        "البرنامج 001 - تحديث الإدارة",
                        Set.of("الأمانة العامة", "المفتشية العامة"),
                        Set.of("المديرية الجهوية الجزائر", "المديرية الجهوية وهران"),
                        Set.of("الوكالة الوطنية للرقمنة", "المعهد العالي للإدارة العمومية"),
                        Set.of("مديرية ولاية الجزائر", "مديرية ولاية وهران")))
                .addProgrammeStructure(new ProgrammeStructure(
                        "البرنامج 002 - التنمية المستدامة",
                        Set.of("المديرية العامة للبيئة"),
                        Set.of("المديرية الجهوية عنابة", "المديرية الجهوية تلمسان"),
                        Set.of("المكتب الوطني للغابات"),
                        Set.of("المحافظة على المناطق الرطبة")))
                .build();

        FichePortefeuille fichePortefeuille = FichePortefeuille.builder()
                .targetYear(Year.of(2026))
                .addRepartitionProgrammeVersionBs(List.of(new ProgrammeRepartition("التكوين المهني", 20_143_691_000L, 19_506_191_000L),
                                new ProgrammeRepartition("التعليم المهني", 622_000_000L, 540_000_000L),
                                new ProgrammeRepartition("الإدارة العامة", 97_250_926_000L, 98_536_426_000L)))
                .build();

        AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                .laMission(laMission)
                .leMinistere(leMinistere)
                .cartographie(cProgrammesPortefeuille)
                .fichePortefeuille(fichePortefeuille)
                .build();
                auSujetDuPortefeuille.write(document, new GenerationContext(document, loadPropertiesAsMap("arab.properties"), LanguageDirection.RTL));


    }
    public static Map<String, String> loadPropertiesAsMap(String resourceName) {
        Properties props = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName); InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            props.load(r);
            Map<String, String> map = new HashMap<>();
            for (String name : props.stringPropertyNames()) {
                map.put(name, props.getProperty(name));
            }

            return map;
        } catch (Exception e) {
            throw new IllegalStateException("can't load resource");
        }
    }

}
