package org.ssd.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Triple <F, S, T>{
    private long seen;
    private final F first;
    private final S second;
    private final T third;
}
