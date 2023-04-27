package org.ssd.p2p.communication;

import org.ssd.auction.RunningAuction;

public record AuctionMessage(RunningAuction auction) implements Message {
}
