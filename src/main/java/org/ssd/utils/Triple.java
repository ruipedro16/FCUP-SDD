package org.ssd.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

/**
 * A generic triple class representing a tuple of three values.
 *
 * @param <F> the type of the first value
 * @param <S> the type of the second value
 * @param <T> the type of the third value
 */
@Data
@AllArgsConstructor
public class Triple<F, S, T> {
    private final F first;
    private final S second;
    private final T third;

    /**
     * Applies the given function to the three values and returns the result.
     *
     * @param function the function to apply
     * @param <R>      the type of the result
     * @return the result of applying the function
     */
    public <R> Triple<R, S, T> mapFirst(Function<F, R> function) {
        return new Triple<>(function.apply(this.first), this.second, this.third);
    }

    /**
     * Returns a new triple with the first value replaced by the result of applying the given function to it.
     *
     * @param function the function to apply
     * @param <R>      the type of the new first value
     * @return a new triple with the new first value
     */
    public <R> Triple<F, R, T> mapSecond(Function<S, R> function) {
        return new Triple<>(this.first, function.apply(this.second), this.third);
    }

    /**
     * Returns a new triple with the second value replaced by the result of applying the given function to it.
     *
     * @param function the function to apply
     * @param <R>      the type of the new second value
     * @return a new triple with the new second value
     */
    public <R> Triple<F, S, R> mapThird(Function<T, R> function) {
        return new Triple<>(this.first, this.second, function.apply(this.third));
    }

    /**
     * Creates a new triple with the given values.
     *
     * @param first  the first value
     * @param second the second value
     * @param third  the third value
     * @param <F>    the type of the first value
     * @param <S>    the type of the second value
     * @param <T>    the type of the third value
     * @return a new triple with the given values
     */
    public static <F, S, T> Triple<F, S, T> of(F first, S second, T third) {
        return new Triple<>(first, second, third);
    }


    /**
     * Returns a string representation of the triple in the format (first, second, third).
     *
     * @return a string representation of the triple
     */
    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ", " + this.third + ")";
    }

}
