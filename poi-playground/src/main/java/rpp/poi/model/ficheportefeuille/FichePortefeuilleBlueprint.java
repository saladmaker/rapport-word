package rpp.poi.model.ficheportefeuille;

import java.time.Year;
import java.util.List;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint {

    String $_1_GEST_RESP_TITLE = "Gestionnaire responsable :";

    String $_2_REPARTITION_VERSION_B = """
            Répartition des crédits de paiements
            et les autorisations d'engagement par programme
            (en milliers de dinars) (version B)
            """.replaceAll("\\R", " ");
    
    String $_3_DEMARCHE = "Demarche adoptée pour le budget programme 2026";

    String $_4_REPARTITION_VERSION_A ="""
            Répartition des crédits de paiements
            et des autorisations d'engagement par programme
            (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_5_PROGRAMME_CTRES = """
            Répartition des crédits des programmes par
            type de centre de responsabilitées (en dinars)
            """.replaceAll("\\R", " ");

    String $_6_PROGRAMME_TTR = """
            Répartition des crédits des programmes et 
            titre-année %1 (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_7_CTRES_TTR = """
            Répartition du portefeuille par titre et type
            de centre de responsabilité année %1 (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_8_EVL_DEPS_PROGR = """
            Évolutions des dépense par programme (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_9_EVL_PST_CTES = """
            Évolutions des postes ouverts par type de service
            """.replaceAll("\\R", " ");

    @Option.Required
    Year targetYear();

    List<Programme_AE_CP> table_1();

    List<String> demarches();

    List<Programme_AE_CP> table_2();

    List<Programme_CTRES> table_3();
}
