package rpp.poi.model;

import java.util.List;
import java.util.Objects;

public record ProgrammeAnnee(String name, List<Long> annees) {
    public ProgrammeAnnee{
        Objects.requireNonNull(name, "name can not be null");
        Objects.requireNonNull(annees, "annee can not be null");
        if(!(annees.size() == 5)) throw new IllegalArgumentException("annee size should be 5 " + annees);
        boolean negativeOrNullValues = annees.stream().anyMatch(i-> (null == i) || (i < 0));
        if(negativeOrNullValues) throw new IllegalArgumentException("annee should not contain null or negative" + annees);
    }
}
