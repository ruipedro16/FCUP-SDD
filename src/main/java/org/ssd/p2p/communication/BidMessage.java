package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.Bid;

@AllArgsConstructor
@Data
public class BidMessage implements Message  {
    private final MessageType messageType = MessageType.BROADCAST_BID;
    private final Bid bid;
}
