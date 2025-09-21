package mf.dgb.rpp.model;

import java.util.function.Function;


public sealed interface ColumnExtractor<T, R> extends Function<T, R>
        permits ColumnExtractor.SummableColumnExtractor,
        ColumnExtractor.UnsummableColumnExtractor,
        ColumnExtractor.ConstableColumnExtractor {

    @SuppressWarnings("hiding")
    public non-sealed interface SummableColumnExtractor<T, Long> extends ColumnExtractor<T, Long> {
    }

    @SuppressWarnings("hiding")
    public non-sealed interface UnsummableColumnExtractor<T, String> extends ColumnExtractor<T, String> {
    }

    public non-sealed interface ConstableColumnExtractor<T, E extends Enum<E>> extends ColumnExtractor<T, E> {

    }

    static <S> SummableColumnExtractor<S, Long> ofSummable(Function<S, Long> summable) {
        return summable::apply;
    }

    static <S> UnsummableColumnExtractor<S, String> ofUnsummable(Function<S, String> unsummable) {
        return unsummable::apply;
    }

    static <S, E extends Enum<E>> ConstableColumnExtractor<S, E> ofConstable(Function<S, E> constable) {
        return constable::apply;
    }
}
