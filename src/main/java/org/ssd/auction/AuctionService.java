package org.ssd.auction;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class AuctionService {
    private static final Scanner sc = new Scanner(System.in);

    private final Map<byte[], RunningAuction> auctionMap;

    private Thread thread;

    public AuctionService() {
        this.auctionMap = new HashMap<>();
        this.thread = null;
    }
}
