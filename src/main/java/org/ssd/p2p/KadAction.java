package org.ssd.p2p;

/**
 * Base action interface for remote grpc processes
 */
public interface KadAction {

    /**
     * Generic trigger for whatever action should be taken
     */
    public void trigger();

}
