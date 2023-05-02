package org.renaultleat.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

// Class for Storing all Messages in Queue
public class QueueResource {
    // Queue for all transaction messages
    // TRANSACTION
    volatile BlockingQueue<JSONObject> transactionBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    volatile BlockingQueue<JSONObject> transactionBlockingQueueSec = new LinkedBlockingQueue<JSONObject>();

    volatile BlockingQueue<JSONObject> transactionBlockingQueueTer = new LinkedBlockingQueue<JSONObject>();

    // Queue for all consensus messages
    // PREPARE, COMMIT, ROUNDNCHANGE
    volatile BlockingQueue<JSONObject> messageBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    // Queue for all pipeline messages: To receive incoming blocks
    // BLOCK : For Full Block Broadcast
    volatile BlockingQueue<JSONObject> fullBlockBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    // Queue for all quorum messages & quorm members
    // QUORUM
    volatile BlockingQueue<JSONObject> quorumBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    // Queue for all finalise messages for broadcast and diffusion
    // FINALISE : For Partial Block Broadcast
    volatile BlockingQueue<JSONObject> finaliseBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    // Queue for all Heart Beat Messages
    // HEART BEAT
    volatile BlockingQueue<JSONObject> heartBeatBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    // Queue for all Privacy Messages
    // PRIVACY
    volatile BlockingQueue<JSONObject> privacyBlockingQueue = new LinkedBlockingQueue<JSONObject>();

    public synchronized BlockingQueue<JSONObject> getTransactionBlockingQueueSec() {
        return this.transactionBlockingQueueSec;
    }

    public synchronized BlockingQueue<JSONObject> getTransactionBlockingQueueTer() {
        return this.transactionBlockingQueueTer;
    }

    public synchronized BlockingQueue<JSONObject> getPrivacyBlockingQueue() {
        return this.privacyBlockingQueue;
    }

    public synchronized void setPrivacyBlockingQueue(BlockingQueue<JSONObject> privacyBlockingQueue) {
        this.privacyBlockingQueue = privacyBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getTransactionBlockingQueue() {
        return this.transactionBlockingQueue;
    }

    public synchronized void setTransactionBlockingQueue(BlockingQueue<JSONObject> transactionBlockingQueue) {
        this.transactionBlockingQueue = transactionBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getMessageBlockingQueue() {
        return this.messageBlockingQueue;
    }

    public synchronized void setMessageBlockingQueue(BlockingQueue<JSONObject> messageBlockingQueue) {
        this.messageBlockingQueue = messageBlockingQueue;
    }

    public synchronized void setfullBlockBlockingQueue(BlockingQueue<JSONObject> fullBlockBlockingQueue) {
        this.fullBlockBlockingQueue = fullBlockBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getFullBlockBlockingQueue() {
        return this.fullBlockBlockingQueue;
    }

    public synchronized void setQuorumBlockingQueue(BlockingQueue<JSONObject> quorumBlockingQueue) {
        this.quorumBlockingQueue = quorumBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getQuorumBlockingQueue() {
        return this.quorumBlockingQueue;
    }

    public synchronized void setFinaliseBlockingQueue(BlockingQueue<JSONObject> finaliseBlockingQueue) {
        this.finaliseBlockingQueue = finaliseBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getFinaliseBlockingQueue() {
        return this.finaliseBlockingQueue;
    }

    public synchronized void setHeartBeatBlockingQueue(BlockingQueue<JSONObject> heartBeatBlockingQueue) {
        this.heartBeatBlockingQueue = heartBeatBlockingQueue;
    }

    public synchronized BlockingQueue<JSONObject> getHeartBeatBlockingQueue() {
        return this.heartBeatBlockingQueue;
    }

}