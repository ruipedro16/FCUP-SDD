package org.ssd.auction;

import com.google.protobuf.ByteString;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.DHT;
import org.ssd.ledger.Wallet;
import org.ssd.utils.Menu;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AuctionUI implements Runnable {
    private static final Scanner sc = new Scanner(System.in);

    @Override
    public void run() {
        Wallet wallet = DHT.getWallet();

        System.out.println("Choose your seller ID: ");
        byte[] id = sc.nextLine().getBytes(StandardCharsets.UTF_8);
        wallet.setId(id);

        Menu menu = new Menu("Auction System", new String[]{
                "Create auction",
                "Bid on auction",
                "Close my auctions",
                "Show all active auctions",
                "Show funds",
                "Show PK",
                "Show blockchain",
                "Show Transaction Pool",
                "Show DHT"
        });

        menu.setHandler(1, this::newAuction);
        menu.setHandler(2, this::joinAuctions);
        menu.setHandler(3, this::closeAuction);
        menu.setHandler(4, this::printRunningAuctions);
        menu.setHandler(5, this::printFunds);
        menu.setHandler(6, () -> {
            System.out.println(Hex.toHexString(wallet.getPublicKey().getEncoded()));
        });
        menu.setHandler(7, this::printBlocks);

        menu.setHandler(8, () -> {
            DHT.getBlockchain()
                    .getTransactionPool()
                    .getPendingTransactions()
                    .forEach(System.out::println);
        });


        menu.setHandler(9, () -> {
            DHT.getNode().getDht()
                    .forEach((key, value) -> System.out.println("Key: " + Hex.toHexString(key) + ", Value: " + Hex.toHexString(value.getValue())));
        });

        boolean m = true;
        while (m) {
            m = menu.run();
        }

    }

    private void newAuction() {
        System.out.println("Item name: ");
        String itemName = sc.nextLine();

        System.out.println("Min value for bid: ");
        double minAmount = sc.nextDouble();

        PublicKey pk = DHT.getWallet().getPublicKey();
        Item auctionedItem = new Item(itemName, pk, minAmount);
        Auction newAuction = new Auction(auctionedItem, pk);

        DHT.getAuctionsService().publishAuction(newAuction);

        System.out.println("Successfully created a new auction!");
        System.out.println("Press ENTER to return");//TODO, fix on intellij it is ignored
        sc.nextLine();
    }

    private void joinAuctions() {
        printRunningAuctions();
        System.out.println("Type \"cancel\" to cancel");

        System.out.println("Choose an auction to bid (type the ID): ");
        String line = sc.nextLine();
        if (line.equals("cancel")) {
            return;
        }
        byte[] id = Hex.decodeStrict(line);

        if (!DHT.getAuctionsService().containsAuction(id)) {
            System.out.println("Invalid id! Try again...");
            return;
        }
        ActiveAuction auction = DHT.getAuctionsService().getAuctionById(id);
        System.out.println("New bid: ");
        double b = sc.nextDouble();
        if (b < auction.getAuction().getAuctionedItem().getMinimumAmount()) {
            System.out.println("Value below minimal amount. Try again...\n");
            System.out.println("Press enter to return");
            sc.nextLine();
            return;
        }
        Bid myBid = new Bid(id, b, DHT.getWallet().getPublicKey());
        auction.placeBid(myBid);
        DHT.getAuctionsService().publishBid(myBid);

        System.out.println("Press enter to return");
        sc.nextLine();
    }

    /**
     * Closes an auction by specifying the item name (we must own that auction)
     */
    private void closeAuction() {
        boolean success = false;
        printRunningAuctions();
        System.out.println("Type \"cancel\" to cancel");

        System.out.println("Item name: ");
        String itemName = sc.nextLine();
        if (itemName.equals("cancel")) {
            return;
        }
        success = DHT.getAuctionsService().endAuction(itemName);
        if (success) {
            System.out.println("Auction closed\n");
        } else {
            System.out.println("Auction couldn't be closed or you don't own this auction\n");
        }
        System.out.println("Press enter to return");
        sc.nextLine();
    }

    private void printRunningAuctions() {
        Map<ByteString, ActiveAuction> activeAuctions = DHT.getAuctionsService().getAuctionMap();
        final StringBuilder sb = new StringBuilder();
        sb.append("Currently ongoing auctions:\n");
        sb.append("=========================\n");
        if (activeAuctions.isEmpty()) {
            sb.append("No active auctions\n");
        } else {
            activeAuctions.forEach((id, activeAuction) -> {
                sb.append("Auction : ").append(Hex.toHexString(id.toByteArray())).append("\n");
                sb.append(" - Item : ").append(activeAuction.getAuction().getAuctionedItem().getItemName());
                sb.append(" - Minimum : ").append(activeAuction.getAuction().getAuctionedItem().getMinimumAmount());
                Bid hb;
                try {
                    hb = activeAuction.getHighestBid();
                    sb.append(" - Highest Bid : ").append(hb.toString());
                } catch (NoSuchElementException e) {
                    sb.append(" - Highest Bid : ").append("No bids made\n");
                }
                sb.append("=========================\n");
            });

        }
        System.out.print(sb);
    }

    private void printFunds() {
        System.out.println("Current funds : " + DHT.getWallet().getBalance());
    }

    private void printBlocks() {
        System.out.println(DHT.getBlockchain().toString());
    }

}
