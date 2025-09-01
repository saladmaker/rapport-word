package rpp.poi.model;

import java.util.function.Function;

public interface ColumnExtractor<T, R> extends Function<T, R> {

    public interface SummableColumnExtractor<T, R extends Long> extends ColumnExtractor<T, R> { 
    }
    
    public interface UnsummableColumnExtractor<T, R extends String> extends ColumnExtractor<T, R> {
    }
    
    static <S> SummableColumnExtractor<S,Long> ofSummable(Function<S,Long> summable){
        return summable::apply;
    }
    
    static <S> UnsummableColumnExtractor<S, String> ofUnsummable(Function<S, String> unsummable) {
        return unsummable::apply;
    }

}
