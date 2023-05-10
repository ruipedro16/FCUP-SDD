# ssd_2023

- [ ] No PoS, tem este Hashmap `private final Map<PublicKey, Double> validators;`. É preciso adicionar codigo para que ao inicializar cada nodo da rede, a pk desse nodo seja adicionada a este hashmap. Tb e preciso calcular o staked amount de cada nodo (provavelmente no fim de cada transacao (?))

- [ ] A validacao das transacoess e feita no metodo `Block.addTransactions` ou no `Transaction.validateTransaction`? (Talvez juntar estes dois metodos)


```java
public void addTransactions(@NonNull List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            if (transaction == null) {
                System.out.println("Transaction failed to process. Ignored.");
                return;
            }

            if (header.getPreviousHash() != null) { // the hash of the genesis block is `null`
                if ((!transaction.verifySignature())) {
                    System.out.println("Transaction Signature failed to verify. Ignored.");
                    return;
                }
            }

            transactions.add(transaction);
            System.out.println("Transaction Successfully added to the block");
        });
    }
```

```java
public boolean validateTransaction() {
        // Check if the transaction signature is valid & Check if the inputs and outputs amounts match
        if (!verifySignature() || getInputsAmount() != getOutputsAmount()) {
            return false;
        }


        // Check if each input's unspent output exists and has the correct amount
        for (TransactionInput input : getTxInputs()) {
            TransactionOutput unspentOutput = DHT.getBlockchain().getUTXOs().get(input.getTxOutputID());
            if (unspentOutput == null || unspentOutput.getAmount() != input.getUnspentTxOutput().getAmount()) {
                return false;
            }

            // Remove the input's unspent output from the UTXOs
            DHT.getBlockchain().getUTXOs().remove(input.getTxOutputID());
        }

        // If all checks passed, the transaction is valid
        return true;
    }
```
