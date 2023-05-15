package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.Bid;

@AllArgsConstructor
@Getter
public class BidMessage extends Message {

    @NonNull
    private final Bid bid;

    /**
     * @return Bid message class type identifier
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.BID_MESSAGE;
    }
}
