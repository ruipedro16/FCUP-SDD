package org.ssd.constants;

import java.util.concurrent.TimeUnit;

public class KademliaConstants {
    /**
     * Number of leading zeros in the ID of the node
     * Ensures resistance against Sybil attacks
     */
    public static final int PREFIX_LENGTH = 3;

    /**
     * B : size in bits of the key
     * K : maximum number of contacts stored in a Kbucket
     * alpha : degree of parallelism in network calls
     */
    public static final int B = 160, K = 20, ALPHA = 3;

    /**
     * Kademlia time constants in ms
     */
    private static final int tExpire = 86400, tRefresh = 3600, tReplicate = 3600, tRepublish = 86400;

    /**
     * tExpire : the time after which a key/value pair expires (TTL)
     * tRefresh : the time after which an otherwise unaccessed bucket must be refreshed
     * tReplicate : the interval between Kademlia replication events (publish entire db)
     * tRepublish : the time after which the original publisher must republish a
     * key/value
     */
    public static final long T_EXPIRE = TimeUnit.SECONDS.toMillis(tExpire),
                            T_REFRESH = TimeUnit.SECONDS.toMillis(tRefresh),
                            T_REPLICATE = TimeUnit.SECONDS.toMillis(tReplicate),
                            T_REPUBLISH = TimeUnit.SECONDS.toMillis(tRepublish);

}
