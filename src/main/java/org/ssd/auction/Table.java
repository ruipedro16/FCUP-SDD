package org.ssd.auction;

import java.util.HashMap;
import java.util.Map;

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
     **/
    private static final int MAX_M_SIZE = 20;

    private String resource;//todo
    //current price of the
    private double currentServicePrice;

    //consumers buying table todo with Bid instead of String-Double
    private final Map<String, Double> highestBuyTable = new HashMap<>();
    //providers selling table todo with Bid instead of String-Double
    private final Map<String, Double> lowestSellTable = new HashMap<>();

    /**
     * Responsible for this node's price table
     */
    public Table() {

    }

    //add a buy price

    //add a sell price

}
