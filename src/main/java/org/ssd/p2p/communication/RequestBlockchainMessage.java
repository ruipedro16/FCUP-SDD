package org.ssd.p2p.communication;

public class RequestBlockchainMessage extends Message {
    /**
     * @return
     */
    @Override
    public MessageClass messageClass() { return MessageClass.REQ_BLOCKCHAIN; }
}
