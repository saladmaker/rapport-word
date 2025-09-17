package mf.dgb.rpp.model;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;

/**
 *
 * @author khaled
 */
public record ProgrammeRepartition(String name, Long ae, Long cp) {
    static final List<ColumnExtractor<ProgrammeRepartition, ?>> EXTRACTORS = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeRepartition::name),
                ColumnExtractor.ofSummable(ProgrammeRepartition::ae),
                ColumnExtractor.ofSummable(ProgrammeRepartition::cp));

    // checking invariant (name, ae, cp)
    public ProgrammeRepartition {

        Objects.requireNonNull(name, "name can not be null!");
        Objects.requireNonNull(ae, "ae can't be null!");
        Objects.requireNonNull(cp, "cp can't be null!");

        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException("name should not be blank name: " + name);
        }
        if (ae < 0 || cp < 0) {
            throw new IllegalArgumentException("ae and cp should not be negative! ae: "
                    + ae + " cp: " + cp);
        }

    }
}
