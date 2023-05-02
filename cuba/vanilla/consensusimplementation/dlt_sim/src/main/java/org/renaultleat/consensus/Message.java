package org.renaultleat.consensus;

/**
 * POJO Class for any Type of Message
 * 
 * @param type
 * @param blockHash
 * @param messagesignature
 * @param messageSender
 * @param contents
 */
public class Message {
    // Type can be: PREPARE, COMMIT, ROUNDCHANGE
    String type;
    int index;
    String blockHash;
    String messagesignature;
    String messageSender;
    String contents;
    int round;
    int epoch;
    int subepoch;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setSubEpoch(int subepoch) {
        this.subepoch = subepoch;
    }

    public int getRound() {
        return this.round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getBlockHash() {
        return this.blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getMessagesignature() {
        return this.messagesignature;
    }

    public void setMessagesignature(String messagesignature) {
        this.messagesignature = messagesignature;
    }

    public String getMessageSender() {
        return this.messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getContents() {
        return this.contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Message(String type, String blockHash, String messagesignature, String messageSender, String contents,
            int round, int epoch, int subepoch, int index) {
        this.type = type;
        this.blockHash = blockHash;
        this.messagesignature = messagesignature;
        this.messageSender = messageSender;
        this.contents = contents;
        this.round = round;
        this.epoch = epoch;
        this.subepoch = subepoch;
        this.index = index;
    }

    public Message(String type, String messagesignature, String messageSender, String contents,
            int round, int epoch, int subepoch) {
        this.type = type;
        this.messagesignature = messagesignature;
        this.messageSender = messageSender;
        this.contents = contents;
        this.round = round;
        this.epoch = epoch;
        this.subepoch = subepoch;
    }

}
