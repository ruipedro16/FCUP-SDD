package org.ssd.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Triple <F, S, T>{
    private long seen;
    private final F first;
    private final S second;
    private final T third;
}
