package org.ssd.auction;

import lombok.NonNull;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.Wallet;
import org.ssd.utils.Menu;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Map;
import java.util.Scanner;

public class AuctionUI implements Runnable {
    private static final Scanner sc = new Scanner(System.in);
    @Override
    public void run() {
        Wallet wallet = DHT.getWallet();

        System.out.println("Choose a seller ID: ");
        byte[] id = sc.nextLine().getBytes(StandardCharsets.UTF_8);
        wallet.setId(id);

        Menu menu = new Menu("Auction System", new String[]{
                "Create Auction",
                "Join auction",
                "Show PK" // TODO: remover isto
        });

        menu.setHandler(1, this::newAuction);
        menu.setHandler(2, this::joinAuctions);
        menu.setHandler(3, () -> {
            System.out.println(Hex.toHexString(wallet.getPublicKey().getEncoded()));
        });

        menu.run();
    }

    private void newAuction() {
        System.out.println("Item name: ");
        String itemName = sc.nextLine();

        System.out.println("Min value for bid: ");
        double minAmount = sc.nextDouble();

        System.out.println("Duration of the auction: ");
        long timeout = sc.nextLong();

        Wallet wallet = DHT.getWallet();
        PublicKey pk = DHT.getWallet().getPublicKey();
        Item auctionedItem = new Item(itemName, pk, minAmount);
        Auction newAuction = new Auction(auctionedItem, timeout, pk);

        DHT.getAuctionsService().publishAuction(newAuction);

        System.out.println("Successfully created a new auction!");
        System.out.println("Press ENTER to return");
        sc.nextLine();
    }

    private void joinAuctions() {
        printAuctions(DHT.getAuctionsService().getAuctionMap());

        System.out.println("Choose an auction to bid");
        String line = sc.nextLine();
        byte[] id = Hex.decode(line); // assumes the auction exists
                                      // TODO: handle input of an auction that does not exist

        RunningAuction auction = DHT.getAuctionsService().getAuctionById(id);
        Bid highestBid = auction.getHighestBid();

        System.out.println("Highest bid: " + highestBid.getAmount());
        System.out.println("New bid: "); // todo: handle this

        System.out.println("Press enter to return");
        sc.nextLine();
    }

    private void printAuctions(@NonNull Map<byte[], RunningAuction> auctionMap) {
        // TODO
    }
}
