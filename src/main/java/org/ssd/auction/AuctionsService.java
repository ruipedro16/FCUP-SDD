package org.ssd.auction;

import org.ssd.p2p.Node;

import java.util.ArrayList;

public class AuctionsService {

    private Node node;
    private ArrayList<Auction> thisNodeAuctions;

    public AuctionsService(Node node) {
        this.node = node;
        this.thisNodeAuctions = new ArrayList<>();
    }

}
