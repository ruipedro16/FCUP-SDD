package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.Bid;

@AllArgsConstructor
@Data
public class RequestPaymentMessage implements Message {
    private final MessageType messageType = MessageType.REQUEST_PAYMENT;
    private final Bid bid;
}
