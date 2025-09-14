package mf.dgb.rpp.aggregator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import static com.example.jooq.generated.tables.DsiExerciceBudgetaire.DSI_EXERCICE_BUDGETAIRE;
import static com.example.jooq.generated.tables.DsiPortefeuille.DSI_PORTEFEUILLE;
import static com.example.jooq.generated.tables.DsiProgramme.DSI_PROGRAMME;

import com.example.jooq.generated.tables.records.DsiExerciceBudgetaireRecord;
import com.example.jooq.generated.tables.records.DsiPortefeuilleRecord;
import com.example.jooq.generated.tables.records.DsiProgrammeRecord;

public class Portefeuilles {

    private final DSLContext ctx;

    public Portefeuilles() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/abms", "root", "toor");
            this.ctx = DSL.using(conn, SQLDialect.MYSQL);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public DsiExerciceBudgetaireRecord findMostRecentExercice() {

        DsiExerciceBudgetaireRecord record = ctx
                .selectFrom(DSI_EXERCICE_BUDGETAIRE)
                .orderBy(DSI_EXERCICE_BUDGETAIRE.ANNEE.desc())
                .limit(1)
                .fetchOne();

        if (record == null) {
            throw new IllegalStateException("No records found in dsi_exercice_budgetaire");
        }
        System.out.println(record);
        return record;
    }

    public List<DsiPortefeuilleRecord> portefeuilleByExecrcice(DsiExerciceBudgetaireRecord exercice) {
        return ctx.select(DSI_PORTEFEUILLE.fields())
                .from(DSI_PORTEFEUILLE)
                .join(DSI_EXERCICE_BUDGETAIRE)
                .on(DSI_PORTEFEUILLE.EXERCICE_BUDGETAIRE.eq(DSI_EXERCICE_BUDGETAIRE.ID))
                .where(DSI_EXERCICE_BUDGETAIRE.ID.eq(exercice.getId()))
                .fetchInto(DsiPortefeuilleRecord.class);
    }

    public List<DsiProgrammeRecord> programmeByProtefeuille(DsiPortefeuilleRecord portefeuille) {
        return ctx.select(DSI_PROGRAMME.fields())
                .from(DSI_PROGRAMME)
                .join(DSI_PORTEFEUILLE)
                .on(DSI_PROGRAMME.PORTEFEUILLE.eq(DSI_PORTEFEUILLE.ID))
                .where(DSI_PORTEFEUILLE.ID.eq(portefeuille.getId()))
                .fetchInto(DsiProgrammeRecord.class);
    }

}
