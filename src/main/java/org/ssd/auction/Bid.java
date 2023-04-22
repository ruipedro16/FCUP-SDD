package org.ssd.auction;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.NonNull;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.ssd.ledger.Wallet;
import org.ssd.utils.CryptoUtils;
import org.ssd.utils.Utils;

import java.io.Serializable;

@Data
public class Bid implements Serializable  {
    private final byte[] bidID;
    private final Wallet wallet; // wallet of the bidder
    private final Item item;
    private final double amount;

    public Bid(@NonNull Wallet wallet, @NonNull Item item, double amount) {
        this.wallet = wallet;
        this.item = item;
        this.amount = amount;
        this.bidID = generateID();
    }

    private byte[] generateID() {
        byte[] dataToHash = Arrays.concatenate(
                this.wallet.getPublicKey().getEncoded(),
                this.item.getItemID(),
                Utils.toByteArray(this.amount)
        );

        return CryptoUtils.hash(dataToHash);
    }

    @Override
    public String toString() {
        String block = """
               Bid:
                    Item id: %s
                    Amount: %f
                    Bidder: %s
               """;
        return String.format(block, Hex.toHexString(this.getItem().getItemID()), this.getAmount(), this.getWallet().toString());
    }

}
