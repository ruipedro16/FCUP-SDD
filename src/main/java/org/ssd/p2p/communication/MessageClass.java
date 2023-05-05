package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageClass {
    AUCTION_MESSAGE(AuctionMessage.class),
    BID_MESSAGE(BidMessage.class),
    BLOCK_MESSAGE(BlockMessage.class),
    GET_AUCTION_MESSAGE(GetRunningAuctionMessage.class),
    REQ_PAYMENT_MESSAGE(PayRequestMessage.class),
    TRANSACTION_MESSAGE(TransactionMessage.class);
    private final Class<? extends MessageContent> type;
}
