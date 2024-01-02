package Main.Java;
import Main.Java.Security.StringUtil;
import Main.Java.Wallet.Transaction;
import Main.Java.Wallet.TransactionInput;
import Main.Java.Wallet.TransactionOutput;
import Main.Java.Wallet.Wallet;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author pmpedrolima@gmail.com
 */
public class NoobChain {

    private static final Logger LOGGER = Logger.getLogger(NoobChain.class.getName());

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        LOGGER.log(Level.INFO, "Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        LOGGER.log(Level.INFO, "\nWalletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, "\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        LOGGER.log(Level.INFO, "\nWalletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, "WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        LOGGER.log(Level.INFO, "\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        LOGGER.log(Level.INFO, "\nWalletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, "WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        LOGGER.log(Level.INFO, "\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
        LOGGER.log(Level.INFO, "\nWalletA's balance is: " + walletA.getBalance());
        LOGGER.log(Level.INFO, "WalletB's balance is: " + walletB.getBalance());

        Block block = new Block("previousHash");
        String blockJson = StringUtil.getJson(block);
        System.out.println("Block JSON Representation: " + blockJson);



        isChainValid();
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                LOGGER.log(Level.SEVERE, "#Current Hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                LOGGER.log(Level.SEVERE, "#Previous Hashes not equal");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                LOGGER.log(Level.SEVERE, "#This block hasn't been mined");
                return false;
            }

            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    LOGGER.log(Level.SEVERE, "#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    LOGGER.log(Level.SEVERE, "#Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        LOGGER.log(Level.SEVERE, "#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        LOGGER.log(Level.SEVERE, "#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    LOGGER.log(Level.SEVERE, "#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (((Transaction) currentTransaction).outputs.get(1).reciepient != currentTransaction.sender) {
                    LOGGER.log(Level.SEVERE, "#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }
        }
        LOGGER.log(Level.INFO, "Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}