package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetActiveAuctionsMessage extends Message {

    /**
     * @return Get Active Auction class type
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.GET_AUCTION_MESSAGE;
    }
}
