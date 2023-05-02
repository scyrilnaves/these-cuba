package org.renaultleat.consensus;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO Class for any Type of Message
 * 
 * @param type
 * @param blockHash
 * @param messagesignature
 * @param messageSender
 * @param contents
 */
public class UtilitarianBlockScore {

    // Calculate based on members in the Quorum
    int nodeIndex;

    int blockno;

    int subEpoch;

    @Override
    public String toString() {
        return "{" +
                " nodeIndex='" + getNodeIndex() + "'" +
                ", blockno='" + getBlockNo() + "'" +
                ", subEpoch='" + getSubEpoch() + "'" +
                ", interblockTimecoefficient='" + getInterblockTimecoefficient() + "'" +
                ", peerUtilitarianProposalScoreMap='" + getpeerUtilitarianScoreMap() + "'" +
                ", peerUtilitarianCommitScoreMap='" + getPeerUtilitarianCommitScoreMap() + "'" +
                ", peerUtilitarianMissedProposalScoreMap='" + getPeerUtilitarianMissedProposalScoreMap() + "'" +
                ", peerUtilitarianMissedCommitScoreMap='" + getPeerUtilitarianMissedCommitScoreMap() + "'" +
                ", peerUtilitarianHeartBeatScoreMap='" + getPeerUtilitarianHeartBeatScoreMap() + "'" +
                ", peerUtilitarianMissedHeartBeatScoreMap='" + getPeerUtilitarianMissedHeartBeatScoreMap() + "'" +
                ", peerUtilitarianMaliciousScoreMap='" + getPeerUtilitarianMaliciousBeatScoreMap() + "'" +
                "}";
    }

    double interblockTimecoefficient = 0;
    // Utilitarian Score
    // Node Index, Score
    Map<Integer, Double> peerUtilitarianProposalScoreMap = new HashMap<Integer, Double>();

    // Missed Commit Message
    Map<Integer, Double> peerUtilitarianCommitScoreMap = new HashMap<Integer, Double>();

    // Missed Proposal
    Map<Integer, Double> peerUtilitarianMissedProposalScoreMap = new HashMap<Integer, Double>();

    // Missed Commit Message
    Map<Integer, Double> peerUtilitarianMissedCommitScoreMap = new HashMap<Integer, Double>();

    // HeartBeat Message
    Map<Integer, Double> peerUtilitarianHeartBeatScoreMap = new HashMap<Integer, Double>();

    // Missed Heart Beat Message
    Map<Integer, Double> peerUtilitarianMissedHeartBeatScoreMap = new HashMap<Integer, Double>();

    // Malicious Message
    Map<Integer, Double> peerUtilitarianMaliciousScoreMap = new HashMap<Integer, Double>();

    public double getInterblockTimecoefficient() {
        return this.interblockTimecoefficient;
    }

    public void setInterblockTimecoefficient(double interblockTimecoefficient) {
        this.interblockTimecoefficient = interblockTimecoefficient;
    }

    public int getNodeIndex() {
        return this.nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public int getSubEpoch() {
        return this.subEpoch;
    }

    public void setSubEpoch(int inSubEpoch) {
        this.subEpoch = inSubEpoch;
    }

    public int getBlockNo() {
        return this.blockno;
    }

    public void setBlockNo(int inBlockno) {
        this.blockno = inBlockno;
    }

    public void setpeerUtilitarianProposalScoreMap(Map<Integer, Double> inPeerUtilitarianProposalScoreMap) {
        this.peerUtilitarianProposalScoreMap = inPeerUtilitarianProposalScoreMap;
    }

    public Map<Integer, Double> getpeerUtilitarianScoreMap() {
        return this.peerUtilitarianProposalScoreMap;
    }

    public void setpeerUtilitarianCommitScoreMap(Map<Integer, Double> inPeerUtilitarianCommitScoreMap) {
        this.peerUtilitarianCommitScoreMap = inPeerUtilitarianCommitScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianCommitScoreMap() {
        return this.peerUtilitarianCommitScoreMap;
    }

    public void setPeerUtilitarianMissedProposalScoreMap(Map<Integer, Double> inPeerUtilitarianMissedProposalScoreMap) {
        this.peerUtilitarianMissedProposalScoreMap = inPeerUtilitarianMissedProposalScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianMissedProposalScoreMap() {
        return this.peerUtilitarianMissedProposalScoreMap;
    }

    public void setPeerUtilitarianMissedCommitScoreMap(Map<Integer, Double> inPeerUtilitarianMissedCommitScoreMap) {
        this.peerUtilitarianMissedCommitScoreMap = inPeerUtilitarianMissedCommitScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianMissedCommitScoreMap() {
        return this.peerUtilitarianMissedCommitScoreMap;
    }

    public void setPeerUtilitarianHeartBeatScoreMap(Map<Integer, Double> inPeerHeartBeatScoreMap) {
        this.peerUtilitarianHeartBeatScoreMap = inPeerHeartBeatScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianHeartBeatScoreMap() {
        return this.peerUtilitarianHeartBeatScoreMap;
    }

    public void setPeerUtilitarianMissedHeartBeatScoreMap(Map<Integer, Double> inPeerMissedHeartBeatScoreMap) {
        this.peerUtilitarianMissedHeartBeatScoreMap = inPeerMissedHeartBeatScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianMissedHeartBeatScoreMap() {
        return this.peerUtilitarianMissedHeartBeatScoreMap;
    }

    public void setPeerMaliciousBeatScoreMap(Map<Integer, Double> inMaliciousScoreMap) {
        this.peerUtilitarianMaliciousScoreMap = inMaliciousScoreMap;
    }

    public Map<Integer, Double> getPeerUtilitarianMaliciousBeatScoreMap() {
        return this.peerUtilitarianMaliciousScoreMap;
    }

    public UtilitarianBlockScore(int nodeIndex, int blockno, int subEpoch,
            Map<Integer, Double> peerUtilitarianProposalScoreMap,
            Map<Integer, Double> peerUtilitarianMissedProposalScoreMap,
            Map<Integer, Double> peerUtilitarianCommitScoreMap,
            Map<Integer, Double> peerUtilitarianMissedCommitScoreMap,
            Map<Integer, Double> peerUtilitarianHeartBeatScoreMap,
            Map<Integer, Double> peerUtilitarianMissedHeartBeatScoreMap,
            Map<Integer, Double> peerUtilitarianMaliciousScoreMap, double interblockTimecoefficient) {
        this.blockno = blockno;
        this.nodeIndex = nodeIndex;
        this.subEpoch = subEpoch;
        this.peerUtilitarianProposalScoreMap = peerUtilitarianProposalScoreMap;
        this.peerUtilitarianMissedProposalScoreMap = peerUtilitarianMissedProposalScoreMap;
        this.peerUtilitarianCommitScoreMap = peerUtilitarianCommitScoreMap;
        this.peerUtilitarianMissedCommitScoreMap = peerUtilitarianMissedCommitScoreMap;
        this.peerUtilitarianHeartBeatScoreMap = peerUtilitarianHeartBeatScoreMap;
        this.peerUtilitarianMissedHeartBeatScoreMap = peerUtilitarianMissedHeartBeatScoreMap;
        this.peerUtilitarianMaliciousScoreMap = peerUtilitarianMaliciousScoreMap;
        this.interblockTimecoefficient = interblockTimecoefficient;
    }

}
