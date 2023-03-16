package org.ssd.auction;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Data
public class Table {
    /**
     * Struct:
     * table {
     * | highestBuy   | lowestSell   |
     * | nodeId-price | nodeId-price |
     * | nodeId-price | nodeId-price |
     * ... until N_MAX_SIZE / 2 for each ...
     * }
     * to get highestBuy or lowestSell simply sort and can return with entry
     * <p>
     * ALTERNATIVE -> Object in Table ->  {type:Provider/Consumer, nodeId: string, price: double}[M_SIZE]
     * following PeerMart-like structure but might change depending on underlying stuff
     **/
    private static final int MAX_M_SIZE = 20;

    private String resource;//todo
    //current price of the
    private double currentServicePrice;

    //consumers buying table todo with Bid instead of String-Double
    private final Map<String, Float> buyRecords = new HashMap<>();
    //providers selling table todo with Bid instead of String-Double
    private final Map<String, Float> sellRecords = new HashMap<>();

    /**
     * Responsible for this node's price table
     */
    public Table() {
        this.currentServicePrice = 0.0;
    }

    //add a buy price

    //add a sell price

    public Map.Entry<String, Float> getHighestBuy() {
        return Collections.max(this.buyRecords.entrySet(), Map.Entry.comparingByValue());
    }

    public Map.Entry<String, Float> getLowestBuy() {
        return Collections.min(this.buyRecords.entrySet(), Map.Entry.comparingByValue());
    }

    public Map.Entry<String, Float> getHighestSell() {
        return Collections.max(this.sellRecords.entrySet(), Map.Entry.comparingByValue());
    }

    public Map.Entry<String, Float> getLowestSell() {
        return Collections.min(this.sellRecords.entrySet(), Map.Entry.comparingByValue());
    }

}
