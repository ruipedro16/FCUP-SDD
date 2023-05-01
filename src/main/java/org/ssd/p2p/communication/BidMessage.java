package org.ssd.p2p.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ssd.auction.Bid;

public record BidMessage(Bid bid) implements Message {
}
