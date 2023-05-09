package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Message {
    @Getter
    private MessageContent content;
}
