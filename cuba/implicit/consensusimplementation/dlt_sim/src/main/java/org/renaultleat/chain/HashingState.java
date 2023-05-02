package org.renaultleat.chain;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.renaultleat.consensus.Message;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;

// Contains representation of the state
public class HashingState {

    Timestamp hashtime;

    String hash;
    // Index of the node who performs the hash
    // Normally the proposer of the partial block
    int hasher;

    // Partial block Added at the particular state index
    List<Integer> partialBlockState = new ArrayList<Integer>();

    public List<Integer> getPartialBlockState() {
        return this.partialBlockState;
    }

    public void setPartialBlockState(List<Integer> inPartialBlockState) {
        this.partialBlockState = inPartialBlockState;
    }

    public Timestamp getBlockTime() {
        return this.hashtime;
    }

    public void setBlockTime(Timestamp inHashtime) {
        this.hashtime = inHashtime;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String inHash) {
        this.hash = inHash;
    }

    public int getHasher() {
        return this.hasher;
    }

    public void setHasher(int inHasher) {
        this.hasher = inHasher;
    }

    public HashingState(Timestamp inHashTime, String inHash, int inHasher, List<Integer> partialBlockState) {
        this.hashtime = inHashTime;
        this.hash = inHash;
        this.hasher = inHasher;
        this.partialBlockState = partialBlockState;
    }
}
