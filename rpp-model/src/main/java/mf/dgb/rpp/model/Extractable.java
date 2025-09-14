package mf.dgb.rpp.model;

import java.util.List;
import io.helidon.builder.api.Prototype;
/**
 *
 * @author khaled
 * @param <T>
 */
public interface Extractable<T extends Prototype.Api & Extractable<T>> {
    List<ColumnExtractor<T, ?>> extractors();
}
