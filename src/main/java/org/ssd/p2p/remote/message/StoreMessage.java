package org.ssd.p2p.remote.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreMessage {

    private byte[] dataOwnerId;
    private byte[] key;
    private byte[] value;

}
