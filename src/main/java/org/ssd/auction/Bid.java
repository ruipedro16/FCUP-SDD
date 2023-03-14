package org.ssd.auction;

import lombok.Data;

import java.security.PublicKey;

@Data
public class Bid {
    private final byte[] bidID;
    private final PublicKey userPK;
}
