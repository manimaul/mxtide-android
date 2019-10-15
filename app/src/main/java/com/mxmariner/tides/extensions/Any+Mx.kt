package com.mxmariner.tides.extensions

/**
 * Evaluate 2 nullable values and perform actions for all the combinations of nullability.
 *
 * @param a the first value
 * @param b the second value
 * @param both action to perform when both values are not null
 * @param first action to perform when only the first value is not null
 * @param second action to perform when only the second value is not null
 * @param none action to perform when all values are null
 */
fun <T, E, R> evaluateNullables(a: T?,
                                b: E?,
                                both: ((Pair<T, E>) -> R)? = null,
                                first: ((T) -> R)? = null,
                                second: ((E) -> R)? = null,
                                none: (() -> R)? = null): R? {
    return if (a != null && b != null) {
        both?.invoke(a to b)
    } else if (a != null) {
        first?.invoke(a)
    } else if (b != null) {
        second?.invoke(b)
    } else {
        none?.invoke()
    }
}
