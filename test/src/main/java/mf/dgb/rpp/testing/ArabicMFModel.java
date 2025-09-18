package mf.dgb.rpp.testing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mf.dgb.rpp.model.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class ArabicMFModel implements DocumentGenerator {
    static final String INTRO = """
                    تلتزم الوزارة، للسنة القادمة، بتعزيز جودة الخدمات العمومية لفائدة المواطنين.
                    كما تخطط لتحديث البنية التحتية الرقمية من أجل تسهيل الوصول إلى الإجراءات الإدارية.
                    سيُولى اهتمام خاص للشفافية ومكافحة الفساد.
                    وسيظلّ التنمية المستدامة في صميم السياسات العمومية، مع برامج طموحة من أجل الLانتقال البيئي.
                    وأخيراً، سيتم إنشاء شراكات استراتيجية لتحفيز الابتكار والاستثمار في القطاعات الرئيسية.
            """.replaceAll("\\R", " ");


    @Override
    public void generate(XWPFDocument document) throws Exception {
        LaMission mission = LaMission.builder().intro(INTRO)
                .addMission(Mission.builder()
                        .mission("التحول الرقمي: نشر منصات رقمية لتبسيط الإجراءات الإدارية.")
                        .addSubMission("إنشاء شباك موحد عبر الإنترنت")
                        .addSubMission("أتمتة الإجراءات الداخلية")
                        .build())
                .addMission(Mission.builder()
                        .mission("زيادة سهولة الوصول: تحسين وصول المواطنين إلى الخدمات العمومية، بما في ذلك في المناطق الريفية.")
                        .addSubMission("نشر الأكشاك التفاعلية")
                        .addSubMission("تعزيز الخدمات المتنقلة")
                        .build())
                .addMission(Mission.builder()
                        .mission("الشفافية والحوكمة: تعزيز آليات الرقابة ومكافحة الفساد بشكل فعال.")
                        .addSubMission("النشر السنوي للتقارير المالية")
                        .addSubMission("إنشاء منصة للتبليغ المجهول")
                        .build())
                .build();

        byte[] imageBytes =  FrenchModel.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg").readAllBytes();
        LeMinistere leMinistere = LeMinistere.builder().image(imageBytes).build();

        CartographieProgrammesPortefeuille cartographie = CartographieProgrammesPortefeuille.builder()
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
                // repartition programme version B
                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                        "التكوين المهني",
                        20_143_691_000L, 19_506_191_000L))
                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                        "التعليم المهني",
                        622_000_000L, 540_000_000L))
                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                        "الادارة العامة",
                        97_250_926_000L, 98_536_426_000L))
                // repartition programme
                .addRepartitionProgramme(new ProgrammeRepartition(
                        "التكوين المهني",
                        20_143_691_000L, 19_506_191_000L))
                .addRepartitionProgramme(new ProgrammeRepartition(
                        "التعليم المهني",
                        622_000_000L, 540_000_000L))
                .addRepartitionProgramme(new ProgrammeRepartition(
                        "الادارة العامة",
                        97_250_926_000L, 98_536_426_000L))
                // repartition programme centre de responsabilite
                .addRepartitionProgrammeCentreResp(
                        ProgrammeCentreResponsibilite.builder()
                                .name("التكوين المهني")
                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                        642342L))
                                .build())
                .addRepartitionProgrammeCentreResp(
                        ProgrammeCentreResponsibilite.builder()
                                .name("التعليم المهني")
                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                        642342L))
                                .build())
                .addRepartitionProgrammeCentreResp(
                        ProgrammeCentreResponsibilite.builder()
                                .name("الادارة العامة")
                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                        642342L))
                                .build())
                // repartition programme titre
                .addRepartitionProgrammeTitre(
                        ProgrammeTitre.builder()
                                .name("التكوين المهني")
                                .isMF(true)
                                .titre1(2434234L)
                                .titre2(43453453L)
                                .titre3(423424332L)
                                .titre4(234242432L)
                                .titre5(324234L)
                                .titre6(34234234L)
                                .titre7(234234243L)
                                .build())
                .addRepartitionProgrammeTitre(
                        ProgrammeTitre.builder()
                                .name("التعليم المهني")
                                .isMF(false)
                                .titre1(2434234L)
                                .titre2(43453453L)
                                .titre3(423424332L)
                                .titre4(234242432L)
                                .build())
                .addRepartitionProgrammeTitre(
                        ProgrammeTitre.builder()
                                .name("الادارة العامة")
                                .isMF(false)
                                .titre1(2434234L)
                                .titre2(43453453L)
                                .titre3(423424332L)
                                .titre4(234242432L)
                                .build())

                // repartition titre centre de responsabilite
                .repartitionCentreRespTitre(PortefeuilleCentreResponsabiliteTitre.builder()
                        .isMF(true)
                        .service(CentreResponsabiliteTitre
                                .servicesCentraux(List.of(121234L, 34234324L, 324234L, 432424L, 3434234L, 34234234L, 423423L)))
                        .service(CentreResponsabiliteTitre
                                .organismesSousTutelle(List.of(23234L, 32342L,234243L,3242342L)))
                        .service(CentreResponsabiliteTitre
                                .serviceDeconcentrees(List.of(123242L,234234L,234243L,234243L)))
                        .service(CentreResponsabiliteTitre
                                .organismesTerritotiaux(List.of(123242L,234234L,234243L,234243L)))
                        .build()
                )
                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                        .name("التكوين المهني")
                        .evolution(List.of(13232332323L, 234234234L, 324234234L, 324234234L,
                                4234234234L))
                        .build()
                )
                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                        .name("التعليم المهني")
                        .evolution(List.of(34234234L, 324234234L, 3242342342L, 23423423423L,
                                343443234243L))
                        .build()
                )
                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                        .name("الادارة العامة")
                        .evolution(List.of(32313123123L, 3123123132L, 3423424243L,
                                234234243234L, 3423424234243L))
                        .build()
                )
                .addPostesEvolution(CentreRespEvoluPostes.servicesCentraux(List.of(22323L, 32424L,4324243L,324243L,2342432L)))
                .addPostesEvolution(CentreRespEvoluPostes.organismesSousTutelle(List.of(22323L, 32424L,4324243L,324243L,2342432L)))
                .addPostesEvolution(CentreRespEvoluPostes.organesTerritoriaux(List.of(22323L, 32424L,434L,45L,5L)))
                .build();

        AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                .laMission(mission)
                .leMinistere(leMinistere)
                .cartographie(cartographie)
                .fichePortefeuille(fichePortefeuille)
                .build();
        var mappers = Map.of(
                        "portefeuille", "الصحة",
                        "gestionnaire", "الصحة",
                        "n-2", "2024",
                        "n-1", "2025",
                        "n", "2026",
                        "n+1", "2027",
                        "n+2", "2028");
        auSujetDuPortefeuille.write(document, GenerationContext.of(document, LanguageDirection.RTL, mappers));

    }

}
