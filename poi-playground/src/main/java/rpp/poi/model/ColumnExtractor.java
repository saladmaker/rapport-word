package rpp.poi.model;

import java.util.function.Function;

public sealed interface ColumnExtractor<T, R> extends Function<T, R> permits ColumnExtractor.SummableColumnExtractor, ColumnExtractor.UnsummableColumnExtractor {

    public non-sealed interface SummableColumnExtractor<T, Long> extends ColumnExtractor<T, Long> { 
    }
    
    public non-sealed interface UnsummableColumnExtractor<T, String> extends ColumnExtractor<T, String> {
    }
    
    static <S> SummableColumnExtractor<S,Long> ofSummable(Function<S,Long> summable){
        return summable::apply;
    }

    static <S> UnsummableColumnExtractor<S, String> ofUnsummable(Function<S, String> unsummable) {
        return unsummable::apply;
    }

}
