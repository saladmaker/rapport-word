package mf.dgb.rpp.model;

import java.util.function.Function;

public sealed interface ColumnExtractor<T, R> extends Function<T, R>
        permits ColumnExtractor.SummableColumnExtractor,
        ColumnExtractor.UnsummableColumnExtractor,
        ColumnExtractor.ConstableColumnExtractor,
        ColumnExtractor.AvergableColumnExtractor{

    @SuppressWarnings("hiding")
    public non-sealed interface SummableColumnExtractor<T> extends ColumnExtractor<T, Long> {
    }

    @SuppressWarnings("hiding")
    public non-sealed interface UnsummableColumnExtractor<T> extends ColumnExtractor<T, String> {
    }

    public non-sealed interface ConstableColumnExtractor<T, E extends Enum<E>> extends ColumnExtractor<T, E> {

    }
    public non-sealed interface AvergableColumnExtractor<T> extends ColumnExtractor<T, Double>{

    }

    static <S> SummableColumnExtractor<S> ofSummable(Function<S, Long> summable) {
        return summable::apply;
    }

    static <S> AvergableColumnExtractor<S> ofAveragable(Function<S, Double> avergable){
        return avergable::apply;
    }
    static <S> UnsummableColumnExtractor<S> ofUnsummable(Function<S, String> unsummable) {
        return unsummable::apply;
    }

    static <S, E extends Enum<E>> ConstableColumnExtractor<S, E> ofConstable(Function<S, E> constable) {
        return constable::apply;
    }
}
