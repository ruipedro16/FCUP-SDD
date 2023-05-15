package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.Bid;

@AllArgsConstructor
@Getter
public class PayRequestMessage extends Message {

    @NonNull
    private final Bid bid;

    /**
     * @return Message class identifier
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.REQ_PAYMENT_MESSAGE;
    }
}
