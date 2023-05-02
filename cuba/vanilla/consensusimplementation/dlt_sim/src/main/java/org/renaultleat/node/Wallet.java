package org.renaultleat.node;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.renaultleat.crypto.CryptoGroupUtil;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.PrivacyStorage;

public class Wallet {

    // Actual Value to be used for finding the index
    public static int nodeproperty;

    // Just a Reference : "Node"+ NodeProperty
    public String nodeid;

    KeyPair keypair;

    public static String publicKey;

    public static void setnodeproperty(String input) {
        // Set only once
        if (nodeproperty == 0) {
            nodeproperty = Integer.valueOf(input);
        }

    }

    public static int getNodeproperty() {
        return nodeproperty;

    }

    public String getNodeId() {
        return this.nodeid;

    }

    public KeyPair getKeyPair() {
        return this.keypair;

    }

    public static String getPublicKey() {
        return publicKey;

    }

    public String signData(String message) {
        String signature = "";
        try {
            signature = CryptoUtil.getSignature(Integer.valueOf(getNodeproperty()), CryptoUtil.getHash(message));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return signature;
    }

    // Encrypted Privacy Data
    public String getPrivacyData(String message) {
        String privacysignature = "";
        try {
            privacysignature = CryptoGroupUtil.getEncryptedData(PrivacyStorage.getPrivacyGroupId(), message);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return privacysignature;
    }

    // Decrypted Privacy Data
    public String decryptPrivacyData(String message) {
        String plainData = "";
        try {
            plainData = CryptoGroupUtil.getDecryptedData(PrivacyStorage.getPrivacyGroupId(), message);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plainData;
    }

    public Transaction createTransaction(String message) {
        return new Transaction(message, this);

    }

    public Wallet() {
    }

    public void initalise(int index) {
        try {
            this.keypair = CryptoUtil.getKeyPair(index);
            publicKey = CryptoUtil.getPublicKeyString(index);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nodeproperty = index;
        this.nodeid = "node" + String.valueOf(index);
    }

}
