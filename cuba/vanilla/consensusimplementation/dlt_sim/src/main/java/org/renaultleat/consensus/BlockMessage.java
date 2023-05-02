package org.renaultleat.consensus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.renaultleat.chain.Block;
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
public class BlockMessage {

    String type;
    int nodeIndex;
    // Temporal Hashing
    // Timestamp of State at which message formed
    // Actual State: List of Index of Partial Blocks added ,
    // Hash of Partially Ordered Blocks added
    Block block;
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

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
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

    public BlockMessage(String type, int nodeIndex, String messagesignature, String messageSender,
            int round, int epoch, int subepoch,
            Block block) {
        this.type = type;
        this.nodeIndex = nodeIndex;
        this.messageSignature = messagesignature;
        this.messageSender = messageSender;
        this.subepoch = subepoch;
        this.epoch = epoch;
        this.round = round;
        this.block = block;
    }

}
