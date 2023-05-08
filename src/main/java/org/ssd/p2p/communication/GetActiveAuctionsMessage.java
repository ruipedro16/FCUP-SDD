package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetActiveAuctionsMessage extends MessageContent {

    //private final RunningAuction[] auctionsList;

    /**
     * @return Get Running Auction class type
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.GET_AUCTION_MESSAGE;
    }
}
