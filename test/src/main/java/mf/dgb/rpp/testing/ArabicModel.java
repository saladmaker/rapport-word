package mf.dgb.rpp.testing;


import java.time.Year;
import java.util.List;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import mf.dgb.rpp.model.AuSujetDuPortefeuille;
import mf.dgb.rpp.model.CartographieProgrammesPortefeuille;
import mf.dgb.rpp.model.FichePortefeuille;
import mf.dgb.rpp.model.GenerationContext;
import mf.dgb.rpp.model.LaMission;
import mf.dgb.rpp.model.LanguageDirection;
import mf.dgb.rpp.model.LeMinistere;
import mf.dgb.rpp.model.Mission;
import mf.dgb.rpp.model.ProgrammeCentreResponsibilite;
import mf.dgb.rpp.model.ProgrammeRepartition;
import mf.dgb.rpp.model.ProgrammeStructure;

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
                .addMission(Mission.builder()
                        .mission("التحول الرقمي: نشر منصات رقمية لتبسيط الإجراءات الإدارية.")
                        .addSubMission("إنشاء شباك موحد عبر الإنترنت")
                        .addSubMission("أتمتة الإجراءات الداخلية")
                        .build()
                )
                .addMission(Mission.builder()
                        .mission("زيادة سهولة الوصول: تحسين وصول المواطنين إلى الخدمات العمومية، بما في ذلك في المناطق الريفية.")
                        .addSubMission("نشر الأكشاك التفاعلية")
                        .addSubMission("تعزيز الخدمات المتنقلة")
                        .build()
                )
                .addMission(Mission.builder()
                        .mission("الشفافية والحوكمة: تعزيز آليات الرقابة ومكافحة الفساد بشكل فعال.")
                        .addSubMission("النشر السنوي للتقارير المالية")
                        .addSubMission("إنشاء منصة للتبليغ المجهول")
                        .build())

                .build();

        var imageBytes = FrenchModel.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg").readAllBytes();
        var leMinistere = LeMinistere.builder().image(imageBytes).build();

        CartographieProgrammesPortefeuille cProgrammesPortefeuille = CartographieProgrammesPortefeuille.builder()
                .addProgrammeStructure(ProgrammeStructure.builder()
                        .name("البرنامج 001 - تحديث الإدارة")
                        .addServiceCentres(Set.of("الأمانة العامة", "المفتشية العامة"))
                        .addServiceDeconcentrees(Set.of("المديرية الجهوية الجزائر", "المديرية الجهوية وهران"))
                        .addOrganismeSousTutelles(Set.of("الوكالة الوطنية للرقمنة", "المعهد العالي للإدارة العمومية"))
                        .addOrgamismeTerris(Set.of("مديرية ولاية الجزائر", "مديرية ولاية وهران"))
                        .build())
                .addProgrammeStructure(ProgrammeStructure.builder()
                        .name("البرنامج 002 - التنمية المستدامة")
                        .addServiceCentre("المديرية العامة للبيئة")
                        .addServiceDeconcentrees(Set.of("المديرية الجهوية عنابة", "المديرية الجهوية تلمسان"))
                        .addOrganismeSousTutelle("المكتب الوطني للغابات")
                        .addOrgamismeTerri("المحافظة على المناطق الرطبة")
                        .build())
                .build();

        FichePortefeuille fichePortefeuille = FichePortefeuille.builder()
                .targetYear(Year.of(2026))
                .addRepartitionProgrammeVersionBs(List.of(new ProgrammeRepartition("التكوين المهني", 20_143_691_000L, 19_506_191_000L),
                        new ProgrammeRepartition("التعليم المهني", 622_000_000L, 540_000_000L),
                        new ProgrammeRepartition("الإدارة العامة", 97_250_926_000L, 98_536_426_000L)))
                .addRepartitionProgrammes(List.of(new ProgrammeRepartition("التكوين المهني", 20_143_691_000L, 19_506_191_000L),
                        new ProgrammeRepartition("التعليم المهني", 622_000_000L, 540_000_000L),
                        new ProgrammeRepartition("الإدارة العامة", 97_250_926_000L, 98_536_426_000L)))
                .addRepartitionProgrammeCentreResp(ProgrammeCentreResponsibilite.builder()
                        .name("التعليم المهني")
                        .repartition(List.of(23231L, 324234L, 234324234L, 324242L))
                        .build())
                .addRepartitionProgrammeCentreResp(ProgrammeCentreResponsibilite.builder()
                        .name("الإدارة العامة")
                        .repartition(List.of(23231L, 324234L, 234324234L, 324242L))
                        .build())
                .build();

        AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                .laMission(laMission)
                .leMinistere(leMinistere)
                .cartographie(cProgrammesPortefeuille)
                .fichePortefeuille(fichePortefeuille)
                .build();
        auSujetDuPortefeuille.write(document, GenerationContext.of(document, LanguageDirection.RTL));


    }


}
