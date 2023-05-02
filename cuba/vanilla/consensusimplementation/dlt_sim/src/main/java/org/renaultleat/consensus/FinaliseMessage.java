package org.renaultleat.consensus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.renaultleat.chain.PartialBlock;

/**
 * POJO Class for PartialBlock Message
 * 
 * @param type
 * @param blockHash
 * @param messagesignature
 * @param messageSender
 * @param contents
 */
public class FinaliseMessage {

    String type;
    int nodeIndex;
    // Temporal Hashing
    // Timestamp of State at which message formed
    // Actual State: List of Index of Partial Blocks added ,
    // Hash of Partially Ordered Blocks added
    PartialBlock partialBlock;
    Map<Timestamp, Map<List<Integer>, String>> temporalHashingState = new HashMap<Timestamp, Map<List<Integer>, String>>();
    int epoch;
    int subepoch;
    int round;
    String messageSignature;
    String messageSender;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNodeIndex() {
        return this.nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getEpoch() {
        return this.epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public int getSubEpoch() {
        return this.subepoch;
    }

    public void setPartialBlock(PartialBlock partialBlock) {
        this.partialBlock = partialBlock;
    }

    public PartialBlock getPartialBlock() {
        return this.partialBlock;
    }

    public void setSubEpoch(int subepoch) {
        this.subepoch = subepoch;
    }

    public String getMessagesignature() {
        return this.messageSignature;
    }

    public void setMessagesignature(String messageSignature) {
        this.messageSignature = messageSignature;
    }

    public String getMessageSender() {
        return this.messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public FinaliseMessage(String type, int nodeIndex, String messagesignature, String messageSender,
            int round, Map<Timestamp, Map<List<Integer>, String>> temporalHashingState, int epoch, int subepoch,
            PartialBlock partialBlock) {
        this.type = type;
        this.nodeIndex = nodeIndex;
        this.temporalHashingState = temporalHashingState;
        this.messageSignature = messagesignature;
        this.messageSender = messageSender;
        this.subepoch = subepoch;
        this.epoch = epoch;
        this.round = round;
        this.partialBlock = partialBlock;
    }

}
