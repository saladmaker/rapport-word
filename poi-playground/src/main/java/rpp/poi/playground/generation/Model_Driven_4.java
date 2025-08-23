package rpp.poi.playground.generation;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import rpp.poi.playground.DocumentGenerator;

import rpp.poi.model.AuSujetDuPortefeuille;
import rpp.poi.model.LaMission;
import rpp.poi.model.LeMinistere;
import rpp.poi.model.Mission;

public class Model_Driven_4 implements DocumentGenerator {

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
                        List.of()))
                .addMission(new Mission(
                        "Accessibilité accrue : Améliorer l’accès des citoyens aux services publics, y compris en zones rurales.",
                        List.of()))
                .addMission((new Mission(
                        "Transparence et gouvernance : Renforcement des mécanismes de contrôle et lutte active contre la corruption.",
                        List.of())))
                .addMission(new Mission(
                        "Transition écologique : Mise en œuvre de projets favorisant les énergies renouvelables et la réduction des émissions.",
                        List.of()))
                .addMission(new Mission(
                        "Amélioration des infrastructures : Réhabilitation des établissements publics pour répondre aux besoins actuels.",
                        List.of()))
                .addMission((new Mission(
                        "Innovation et transformation : Encourager la recherche et le développement pour moderniser les secteurs stratégiques.",
                        List.of())))
                .build();
        var imageBytes = Model_Driven_4.class.getClassLoader().getResourceAsStream("ORGANIGRAME.jpg").readAllBytes();
        var leMinistere = LeMinistere.builder().image(imageBytes).build();
        AuSujetDuPortefeuille auSujetDuPortefeuille = AuSujetDuPortefeuille.builder()
                .laMission(laMission)
                .leMinistere(leMinistere)
                .build();
        auSujetDuPortefeuille.write(document);
    }

}
