package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.RunningAuction;

@AllArgsConstructor
@Data
public class AuctionMessage implements Message {
    private final MessageType messageType = MessageType.BROADCAST_AUCTION;
    private final RunningAuction auction;
}
