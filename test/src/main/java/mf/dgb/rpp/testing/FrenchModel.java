package mf.dgb.rpp.testing;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import mf.dgb.rpp.model.AuSujetDuPortefeuille;
import mf.dgb.rpp.model.CartographieProgrammesPortefeuille;
import mf.dgb.rpp.model.CentreResponsabiliteTitre;
import mf.dgb.rpp.model.FichePortefeuille;
import mf.dgb.rpp.model.GenerationContext;
import mf.dgb.rpp.model.Mission;
import mf.dgb.rpp.model.LaMission;
import mf.dgb.rpp.model.LeMinistere;
import mf.dgb.rpp.model.ProgrammeStructure;
import mf.dgb.rpp.model.ProgrammeCentreResponsibilite;
import mf.dgb.rpp.model.ProgrammeTitre;
import mf.dgb.rpp.model.ProgrammeEvolutionDepense;
import mf.dgb.rpp.model.LanguageDirection;
import mf.dgb.rpp.model.PortefeuilleCentreResponsabiliteTitre;
import mf.dgb.rpp.model.ProgrammeRepartition;

public class FrenchModel implements DocumentGenerator {

        private static final String INTRO = """
                        Le ministère s’engage, pour l’année prochaine, à renforcer la qualité des services publics au
                        bénéfice des citoyens. Il prévoit la modernisation des infrastructures numériques afin de faciliter
                        l’accès aux démarches administratives. Une attention particulière sera accordée à la transparence et
                        à la lutte contre la corruption. Le développement durable restera au cœur des politiques publiques,
                        avec des programmes ambitieux pour la transition écologique. Enfin, des partenariats stratégiques
                        seront établis pour stimuler l’innovation et l’investissement dans les secteurs clés.
                        """;

        @Override
        public void generate(XWPFDocument document) throws Exception {
                var laMission = LaMission.builder()
                                .intro(INTRO.replaceAll("\\R", " "))
                                .addMission(t -> t.mission(
                                                "Modernisation numérique : Déploiement de plateformes digitales pour simplifier "
                                                                + "les démarches administratives.")
                                                .addSubMission("Mise en place d’un guichet unique en ligne")
                                                .addSubMission("Automatisation des procédures internes"))

                                .addMission(t -> t.mission(
                                                "Accessibilité accrue : Améliorer l’accès des citoyens aux services publics, y "
                                                                + "compris en zones rurales.")
                                                .addSubMission("Déploiement de bornes interactives")
                                                .addSubMission("Renforcement des services mobiles"))

                                .addMission(t -> t.mission(
                                                "Transparence et gouvernance : Renforcement des mécanismes de contrôle et lutte "
                                                                + "active contre la corruption.")
                                                .addSubMission("Publication annuelle des rapports financiers")
                                                .addSubMission("Création d’une plateforme de signalement anonyme"))
                                .build();

                var imageBytes = FrenchModel.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg")
                                .readAllBytes();
                var leMinistere = LeMinistere.builder().image(imageBytes).build();

                CartographieProgrammesPortefeuille cProgrammesPortefeuille = CartographieProgrammesPortefeuille
                                .builder()
                                .addProgrammeStructure(
                                                ProgrammeStructure.builder()
                                                                .name("Programme 001 - Modernisation de l'administration")
                                                                .addServiceCentre("Secrétariat Général")
                                                                .addServiceCentre("Inspection Générale")
                                                                .addServiceDeconcentree("Direction Régionale Alger")
                                                                .addServiceDeconcentree("Direction Régionale Oran")
                                                                .addOrganismeSousTutelle(
                                                                                "Agence Nationale du Numérique")
                                                                .addOrganismeSousTutelle(
                                                                                "Institut Supérieur d’Administration Publique")
                                                                .addOrgamismeTerri("Direction Wilaya Alger")
                                                                .addOrgamismeTerri("Direction Wilaya Oran")
                                                                .build())
                                .addProgrammeStructure(
                                                ProgrammeStructure.builder()
                                                                .name("Programme 002 - Développement durable")
                                                                .addServiceCentre(
                                                                                "Direction Générale de l’Environnement")
                                                                .addServiceDeconcentree("Direction Régionale Annaba")
                                                                .addServiceDeconcentree("Direction Régionale Tlemcen")
                                                                .addOrganismeSousTutelle("Office National des Forêts")
                                                                .addOrgamismeTerri("Conservatoire des Zones Humides")
                                                                .build())
                                .build();

                FichePortefeuille fichePortefeuille = FichePortefeuille.builder()
                                .targetYear(Year.of(2026))
                                // repartition programme version B
                                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                                                "Formation professionnelle",
                                                20_143_691_000L, 19_506_191_000L))
                                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                                                "Enseignement professionnel",
                                                622_000_000L, 540_000_000L))
                                .addRepartitionProgrammeVersionB(new ProgrammeRepartition(
                                                "Administration générale",
                                                97_250_926_000L, 98_536_426_000L))
                                // repartition programme
                                .addRepartitionProgramme(new ProgrammeRepartition(
                                                "Formation professionnelle",
                                                20_143_691_000L, 19_506_191_000L))
                                .addRepartitionProgramme(new ProgrammeRepartition(
                                                "Enseignement professionnel",
                                                622_000_000L, 540_000_000L))
                                .addRepartitionProgramme(new ProgrammeRepartition(
                                                "Administration générale",
                                                97_250_926_000L, 98_536_426_000L))
                                // repartition programme centre de responsabilite
                                .addRepartitionProgrammeCentreResp(
                                                ProgrammeCentreResponsibilite.builder()
                                                                .name("Enseignement professionnel")
                                                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                                                                642342L))
                                                                .build())
                                .addRepartitionProgrammeCentreResp(
                                                ProgrammeCentreResponsibilite.builder()
                                                                .name("Enseignement professionnel")
                                                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                                                                642342L))
                                                                .build())
                                .addRepartitionProgrammeCentreResp(
                                                ProgrammeCentreResponsibilite.builder()
                                                                .name("Administration générale")
                                                                .repartition(List.of(10033343L, 72334233L, 3343424L,
                                                                                642342L))
                                                                .build())
                                // repartition programme titre
                                .addRepartitionProgrammeTitre(
                                                ProgrammeTitre.builder()
                                                                .name("Formation professionnel")
                                                                .isMF(false)
                                                                .titre1(2434234L)
                                                                .titre2(43453453L)
                                                                .titre3(423424332L)
                                                                .titre4(234242432L)
                                                                .build())
                                .addRepartitionProgrammeTitre(
                                                ProgrammeTitre.builder()
                                                                .name("Formation professionnel")
                                                                .isMF(false)
                                                                .titre1(2434234L)
                                                                .titre2(43453453L)
                                                                .titre3(423424332L)
                                                                .titre4(234242432L)
                                                                .build())
                                .addRepartitionProgrammeTitre(
                                                ProgrammeTitre.builder()
                                                                .name("Formation professionnel")
                                                                .isMF(false)
                                                                .titre1(2434234L)
                                                                .titre2(43453453L)
                                                                .titre3(423424332L)
                                                                .titre4(234242432L)
                                                                .build())

                                // repartition titre centre de responsabilite
                                .repartitionTitreCentreResp(PortefeuilleCentreResponsabiliteTitre.builder()
                                        .service(CentreResponsabiliteTitre
                                                .servicesCentraux(List.of(121234L, 34234324L, 324234L, 432424L)))
                                        .service(CentreResponsabiliteTitre
                                                .organismesSousTutelle(List.of(23234L, 32342L,234243L,3242342L)))
                                        .service(CentreResponsabiliteTitre
                                                .serviceDeconcentrees(List.of(123242L,234234L,234243L,234243L)))
                                        .service(CentreResponsabiliteTitre
                                                .organismesTerritotiaux(List.of(123242L,234234L,234243L,234243L)))
                                        .build()
                                )
                                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                                                .name("Formation professionnelle")
                                                .evolution(List.of(13232332323L, 234234234L, 324234234L, 324234234L,
                                                                4234234234L))
                                        .build()
                                )
                                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                                                .name("Enseignement professionnel")
                                                .evolution(List.of(34234234L, 324234234L, 3242342342L, 23423423423L,
                                                                343443234243L))
                                        .build()
                                )
                                .addProgrammesEvolutionDepense(ProgrammeEvolutionDepense.builder()
                                                .name("Administration general")
                                                .evolution(List.of(32313123123L, 3123123132L, 3423424243L,
                                                                234234243234L, 3423424234243L))
                                        .build()
                                )
                                .build();

                AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                                .laMission(laMission)
                                .leMinistere(leMinistere)
                                .cartographie(cProgrammesPortefeuille)
                                .fichePortefeuille(fichePortefeuille)
                                .build();

                auSujetDuPortefeuille.write(document, GenerationContext.of(document, LanguageDirection.LTR));
        }

}
