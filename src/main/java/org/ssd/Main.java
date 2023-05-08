package org.ssd;

import org.ssd.ledger.Consensus;

public class Main {
    public static void main(String[] args) {
        /*
         * args[0]: PORT
         * args[1]: PoW / PoS
         * args[2]: bootstrap [Optional: default is false]
         */
        int port = 0;
        Consensus consensus = null;
        boolean isBootstrap = false;

        // Check that the correct number of command line arguments were provided
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: <port number> <consensus> [bootstrap]");
            System.exit(1);
        }

        // Parse the port number from the first command line argument
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            System.exit(1);
        }

        // Parse the algorithm from the second command line argument
        if (args[1].equalsIgnoreCase("PoW")) {
            consensus = Consensus.PoW;
        } else if (args[1].equalsIgnoreCase("PoS")) {
            consensus = Consensus.PoS;
        } else {
            System.err.println("Invalid consensus: " + args[1]);
            System.exit(1);
        }

        // Check if the optional third command line argument was provided and set the isBootstrap flag accordingly
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("bootstrap")) {
                isBootstrap = true;
            } else if (!args[2].equalsIgnoreCase("regular")) {
                System.err.println("Invalid bootstrap type: " + args[2]);
                System.exit(1);
            }
        }

        DHT.start(port, consensus, isBootstrap);
    }
}
