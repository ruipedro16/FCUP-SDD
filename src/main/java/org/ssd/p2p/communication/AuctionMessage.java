package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.ssd.auction.ActiveAuction;

@AllArgsConstructor
@Getter
public class AuctionMessage extends MessageContent {

    @NonNull
    private final ActiveAuction auction;

    /**
     * @return Auction message class type identidier
     */
    @Override
    public MessageClass messageClass() {
        return MessageClass.AUCTION_MESSAGE;
    }
}
