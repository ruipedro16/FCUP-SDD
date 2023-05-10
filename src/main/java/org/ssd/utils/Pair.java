package org.ssd.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.ActiveAuction;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class Pair<F, S> {
    private F first;
    private S second;

    public <R> Pair<R, S> mapFirst(Function<F, R> function) {
        return new Pair<>(function.apply(this.first), this.second);
    }

    public <R> Pair<F, R> mapSecond(Function<S, R> function) {
        return new Pair<>(this.first, function.apply(this.second));
    }

    public <R> R apply(BiFunction<F, S, R> function) {
        return function.apply(this.first, this.second);
    }

    public static <F, S> Pair<F, S> of(final F first, S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> Pair<F, S> of(final Map.Entry<F, S> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }
}
