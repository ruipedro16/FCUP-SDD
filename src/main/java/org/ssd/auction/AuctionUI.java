package org.ssd.auction;

import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.Wallet;
import org.ssd.utils.Menu;

import java.util.Scanner;

public class AuctionUI implements Runnable {
    private static final Scanner sc = new Scanner(System.in);
    @Override
    public void run() {
        Wallet wallet = DHT.getWallet();
        Menu menu = new Menu("Auction System", new String[]{
                "Create Auction",
                "Join auction",
                "Show PK" // TODO: remover isto
        });

        menu.setHandler(1, this::newAuction);
        menu.setHandler(2, () -> joinAuctions(wallet));
        menu.setHandler(3, () -> {
            System.out.println(Hex.toHexString(wallet.getPublicKey().getEncoded()));
        });

        menu.run();
    }

    private void newAuction() {

    }

    private void joinAuctions(Wallet wallet) {
        System.out.print("Item name: ");
        String itemName = sc.nextLine();

        System.out.print("Value of the minimum bid: ");
        long minValue = sc.nextLong();

        System.out.print("Duration of the auction: ");
        long timeout = sc.nextLong();

        Auction auction = null; // TODO: update auction class
        // DHT.getAuctionsService().startAuction();

        System.out.println("Auction successfully added");
        System.out.println("Press enter");
    }
}
