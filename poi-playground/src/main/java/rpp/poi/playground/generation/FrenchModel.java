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
import rpp.poi.model.ProgrammeAnnee;
import rpp.poi.model.ProgrammeRepartition;
import rpp.poi.model.ProgrammeStructure;
import rpp.poi.model.RepartitionProgrammeCentreResp;
import rpp.poi.model.RepartitionProgrammeTitre;
import rpp.poi.model.RepartitionTitreCentreResp;
import rpp.poi.playground.DocumentGenerator;

public class FrenchModel implements DocumentGenerator {

    @Override
    public void generate(XWPFDocument document) throws Exception {
        var laMission = LaMission.builder()
                .intro("""
                        Le ministère s’engage, pour l’année prochaine, à renforcer la qualité des services publics au
                        bénéfice des citoyens. Il prévoit la modernisation des infrastructures numériques afin de faciliter
                        l’accès aux démarches administratives. Une attention particulière sera accordée à la transparence et
                        à la lutte contre la corruption. Le développement durable restera au cœur des politiques publiques, avec des programmes ambitieux pour la transition écologique. Enfin, des partenariats stratégiques seront établis pour stimuler l’innovation et l’investissement dans les secteurs clés.
                        """
                        .replaceAll("\\R", " "))
                .addMission(new Mission(
                        "Modernisation numérique : Déploiement de plateformes digitales pour simplifier les démarches administratives.",
                        List.of("Mise en place d’un guichet unique en ligne",
                                "Automatisation des procédures internes",
                                "dfsd sdfs sdfsdf")))
                .addMission(new Mission(
                        "Accessibilité accrue : Améliorer l’accès des citoyens aux services publics, y compris en zones rurales.",
                        List.of("Déploiement de bornes interactives",
                                "Renforcement des services mobiles")))
                .addMission(new Mission(
                        "Transparence et gouvernance : Renforcement des mécanismes de contrôle et lutte active contre la corruption.",
                        List.of("Publication annuelle des rapports financiers",
                                "Création d’une plateforme de signalement anonyme")))
                .build();

        var imageBytes = FrenchModel.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg").readAllBytes();
        var leMinistere = LeMinistere.builder().image(imageBytes).build();

        CartographieProgrammesPortefeuille cProgrammesPortefeuille = CartographieProgrammesPortefeuille.builder()
                .addProgrammeStructure(new ProgrammeStructure(
                        "Programme 001 - Modernisation de l'administration",
                        Set.of("Secrétariat Général", "Inspection Générale"),
                        Set.of("Direction Régionale Alger", "Direction Régionale Oran"),
                        Set.of("Agence Nationale du Numérique", "Institut Supérieur d’Administration Publique"),
                        Set.of("Direction Wilaya Alger", "Direction Wilaya Oran")))
                .addProgrammeStructure(new ProgrammeStructure(
                        "Programme 002 - Développement durable",
                        Set.of("Direction Générale de l’Environnement"),
                        Set.of("Direction Régionale Annaba", "Direction Régionale Tlemcen"),
                        Set.of("Office National des Forêts"),
                        Set.of("Conservatoire des Zones Humides")))
                .build();
        FichePortefeuille fichePortefeuille = FichePortefeuille.builder()
                .targetYear(Year.of(2026))
                .addRepartitionProgrammeVersionBs(List.of(new ProgrammeRepartition("Formation professionnelle", 20_143_691_000L, 19_506_191_000L),
                                new ProgrammeRepartition("Enseignement professionnel", 622_000_000L, 540_000_000L),
                                new ProgrammeRepartition("Administration générale", 97_250_926_000L, 98_536_426_000L)))
                .addRepartitionProgrammes(List.of(new ProgrammeRepartition("Formation professionnelle", 20_143_691_000L, 19_506_191_000L),
                                new ProgrammeRepartition("Enseignement professionnel", 622_000_000L, 540_000_000L),
                                new ProgrammeRepartition("Administration générale", 97_250_926_000L, 98_536_426_000L)))
                .addRepartitionProgrammeCentreResps(
                        List.of(new RepartitionProgrammeCentreResp("Formation professionnelle", List.of(10033423L, 12334233L, 2343424L, 342342L)),
                        new RepartitionProgrammeCentreResp("Enseignement professionnel", List.of(10033343L, 72334233L, 3343424L, 642342L)),
                        new RepartitionProgrammeCentreResp("Administration générale", List.of(133343L, 34233L, 43424L, 642342L)))
                )
                .addRepartitionProgrammeTitres(List.of(
                        new RepartitionProgrammeTitre("Formation professionnelle", List.of(34242342L, 434243L, 32423424L, 342342L), false),
                        new RepartitionProgrammeTitre("Enseignement professionnel", List.of(32242342L, 23423424L, 3243423423L, 23425234L), false),
                        new RepartitionProgrammeTitre("Administration générale", List.of(32342342L, 234243523L, 3242353432L, 324234234L), false))
                ).addRepartitionTitreCentreResps(List.of(
                        new RepartitionTitreCentreResp("Services Centraux", List.of(34242342L, 434243L, 32423424L, 342342L)),
                        new RepartitionTitreCentreResp("Services déconcentrés", List.of(32242342L, 23423424L, 3243423423L, 23425234L)),
                        new RepartitionTitreCentreResp("Organismes sous tutelle", List.of(32342342L, 234243523L, 3242353432L, 324234234L)),
                        new RepartitionTitreCentreResp("Organes territoriaux", List.of(32342342L, 234243523L, 3242353432L, 324234234L)))
                )
                .addProgrammesAnnees(List.of(
                        new ProgrammeAnnee("Formation professionnelle", List.of(13232332323L,234234234L,324234234L,324234234L, 4234234234L)),
                        new ProgrammeAnnee("Enseignement professionnel", List.of(34234234L,324234234L,3242342342L,23423423423L, 343443234243L)),
                        new ProgrammeAnnee("Administration general", List.of(32313123123L, 3123123132L, 3423424243L, 234234243234L, 3423424234243L)))
                )
                .build();
        AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                .laMission(laMission)
                .leMinistere(leMinistere)
                .cartographie(cProgrammesPortefeuille)
                .fichePortefeuille(fichePortefeuille)
                .build();
        auSujetDuPortefeuille.write(document, new GenerationContext(document,  loadPropertiesAsMap("french.properties"), LanguageDirection.LTR));
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
