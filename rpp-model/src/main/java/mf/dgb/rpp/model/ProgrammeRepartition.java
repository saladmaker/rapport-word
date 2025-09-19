package mf.dgb.rpp.model;

import java.util.Objects;


public record ProgrammeRepartition(String name, Long ae, Long cp) {
        public ProgrammeRepartition{
            Objects.requireNonNull(name, "name can not be null!");
            Objects.requireNonNull(ae, "ae can't be null!");
            Objects.requireNonNull(cp, "cp can't be null!");
            if((name.isBlank())) throw new IllegalArgumentException("name should not be blank name: " + name);
            if(ae < 0 || cp < 0) throw new IllegalArgumentException("ae and cp should not be null! ae: "
             + ae + " cp: " + cp);
        }
}