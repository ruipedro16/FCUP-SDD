package org.ssd.p2p;

import org.ssd.constants.BlockchainConstants;
import org.ssd.ledger.Transaction;
import org.ssd.ledger.blockchain.Blockchain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MiningWorker implements Runnable {
    // todo: Change to CopyOnWriteArrayList (??) => Needs to be thread safe
    private List<Transaction> transactions;
    private Node node;
    private Blockchain blockchain;

    @Override
    public void run() {
        for (int i = 0; i < BlockchainConstants.MAX_N_TRANSACTIONS; i++)  {
            // todo: mine block and broadcast it
        }
    }
}
