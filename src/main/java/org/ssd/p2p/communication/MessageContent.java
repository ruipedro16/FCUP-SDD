package org.ssd.p2p.communication;

import java.io.Serializable;

public abstract class MessageContent implements Serializable {
    public abstract MessageClass messageClass();

}
