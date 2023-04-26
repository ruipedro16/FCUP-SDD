package org.ssd.p2p.communication;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetRunningAuctionMessage implements Message  {
    private final MessageType messageType = MessageType.GET_RuNNING_AUCTIONS;
}
