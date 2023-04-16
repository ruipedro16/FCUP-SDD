package org.ssd.p2p.remote;

import lombok.NonNull;
import org.ssd.p2p.routing.NodeContact;
import org.ssd.p2p.storage.StoredData;

import java.util.List;

/**
 * The {@code KademliaAction} interface defines the base action for remote gRPC processes in a Kademlia network.
 * Any class implementing this interface must define the {@code trigger()} method which will be invoked to execute
 * the associated action.
 */
public interface KadAction {
    /**
     * Executes the action associated with this {@code KademliaAction} instance.
     */
    void trigger();

    /**
     * Called when the action has completed successfully.
     *
     * @param nodeContact the {@link NodeContact} associated with the successful response
     */
    default void onSuccess(@NonNull NodeContact nodeContact) {

    }

    default void onSuccess(@NonNull NodeContact nodeContact, @NonNull StoredData storedData) {
    }

    default void onSuccess(@NonNull NodeContact nodeContact, @NonNull List<NodeContact> nodeContacts) {

    }

    /**
     * Called when the action has failed to complete for the given {@link NodeContact}.
     *
     * @param nodeContact the {@link NodeContact} associated with the failed response
     */
    default void onFailure(@NonNull NodeContact nodeContact) {
    }

}
