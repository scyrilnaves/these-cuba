package org.renaultleat.node;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

import org.renaultleat.crypto.CryptoGroupUtil;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.properties.NodeProperty;

public class Transaction {

    public String id;

    public String from;

    public String nodeid;

    public int groupPrivacyid;

    public int nodeindex;

    public String input;

    public Timestamp timestamp;

    public String hash;

    public String signature;

    public boolean isPrivate;

    public boolean isPrivateTransaction() {
        return this.isPrivate;
    }

    public void setPrivateTransaction(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getGroupPrivacyid() {
        return this.groupPrivacyid;
    }

    public void setGroupPrivacyid(int groupPrivacyId) {
        this.groupPrivacyid = groupPrivacyId;
    }

    public String getNodeid() {
        return this.nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public int getNodeindex() {
        return this.nodeindex;
    }

    public void setNodeindex(int nodeindex) {
        this.nodeindex = nodeindex;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Transaction(String id, String from, String nodeId, int nodeIndex, String input, Timestamp inputTimestamp,
            String hash, String signature) {
        this.id = id;
        this.nodeid = nodeId;
        this.nodeindex = nodeIndex;
        this.from = from;
        this.input = input;
        this.timestamp = inputTimestamp;
        this.hash = hash;
        this.signature = signature;
        this.isPrivate = false;

    }

    // Form a transaction without privacy feature
    public Transaction(String data, Wallet wallet) {
        this.id = CryptoUtil.getUniqueIdentifier();
        this.nodeid = wallet.getNodeId();
        this.groupPrivacyid = PrivacyStorage.getPrivacyGroupId();
        this.nodeindex = wallet.getNodeproperty();
        this.from = wallet.getPublicKey();
        this.input = data;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.hash = CryptoUtil.getHash(data);
        this.signature = wallet.signData(data);
        this.isPrivate = false;
    }

    // Form a transaction with privacy feature
    public Transaction(String data, Wallet wallet, boolean isPrivacy) {
        System.out.println("privzacy");
        // Input: Signed by Privacy Group Key: (Data)
        // Signature: Signed by Node Private Key: (Data)
        if (isPrivacy && PrivacyStorage.isPrivacy) {
            this.id = CryptoUtil.getUniqueIdentifier();
            this.nodeid = wallet.getNodeId();
            this.nodeindex = wallet.getNodeproperty();
            this.from = wallet.getPublicKey();
            this.groupPrivacyid = PrivacyStorage.getPrivacyGroupId();
            // Privacy Data
            this.input = wallet.getPrivacyData(data);
            // System.out.println("inoput" + wallet.getPrivacyData(data));
            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.hash = CryptoUtil.getHash(data);
            // Sign Data
            this.signature = wallet.signData(data);

            this.isPrivate = true;
        }
    }

    public static boolean verifyTransaction(Transaction transaction) {
        if (PrivacyStorage.isPrivacy) {
            try {
                return CryptoUtil.verify(transaction.getNodeindex(), transaction.getSignature(),
                        CryptoUtil.getHash(
                                CryptoGroupUtil.getDecryptedData(transaction.getGroupPrivacyid(),
                                        transaction.getInput())));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                return CryptoUtil.verify(transaction.nodeindex, transaction.signature,
                        CryptoUtil.getHash(transaction.input));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

}