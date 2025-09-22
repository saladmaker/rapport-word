package mf.dgb.rpp.model;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.util.List;

@Prototype.Blueprint
@Prototype.CustomMethods(RppModelBlueprint.CustomMethods.class)
interface RppModelBlueprint {
    AuSujetDuPortefeuille auSujetDuPortefeuille();

    @Option.Access("")
    @Option.Singular
    List<FicheProgrammeGeneration> programmes();

    final class CustomMethods{
        @Prototype.BuilderMethod
        static void programme(RppModel.BuilderBase<?,?> builderBase, FicheProgramme programme){
            int size = builderBase.programmes().size();
            int counter = size + 1;
            FicheProgrammeGeneration programmeGeneration = FicheProgrammeGeneration.builder()
                    .from(programme)
                    .counter(counter).build();
            builderBase.addProgramme(programmeGeneration);
        }
    }
}
