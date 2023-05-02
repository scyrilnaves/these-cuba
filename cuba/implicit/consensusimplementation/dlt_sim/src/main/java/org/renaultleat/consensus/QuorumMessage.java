package org.renaultleat.consensus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POJO Class for Quorum Message
 * 
 * @param type
 * @param blockHash
 * @param messagesignature
 * @param messageSender
 * @param contents
 */
// State at particular sub epoch
public class QuorumMessage {

    // 1
    String type;
    // 2
    int epoch;
    // 3
    int subepoch;
    // 4
    int blockindex;
    // 5
    String messageSignature;
    // 6
    String messageSender;
    // 7
    // Proposer of Quorum
    int nodeIndex;
    // 8
    // NodeIndex, Quorum Id: Quorum Comittee Index,
    Map<Integer, Integer> quorumIdMapping = new HashMap<Integer, Integer>();
    // 9
    // Quorum Id: Quorum Comittee Index, List of Node Indexes belonging to the
    // particular Quorum Id
    Map<Integer, List<Integer>> quorumIdInverseMapping = new HashMap<Integer, List<Integer>>();
    // 10
    // NodeIndex,Quorum Index: Index of a Node within a Particular Quorum
    Map<Integer, Integer> quorumIndexMapping = new HashMap<Integer, Integer>();

    // For Identifying the original Node Index
    // QuorumId, Map<Node Index within Quorum, Original Node Id>
    Map<Integer, QuorumOriginal> originalNodeMap = new HashMap<>();

    // 11
    // Quorum Id: Quorum Comittee Index, Total Members inside a particular Quorum
    Map<Integer, Integer> quorumIdMemberCount = new HashMap<Integer, Integer>();
    // 12
    // Ideal Altruists
    List<Integer> idealAltruistList = new ArrayList<Integer>();
    // 13
    // Altruists
    List<Integer> altruistList = new ArrayList<Integer>();
    // 14
    // Fair Altruists
    List<Integer> fairAltruistList = new ArrayList<Integer>();
    // 15
    // Weak Altruits
    List<Integer> weakAltruistList = new ArrayList<Integer>();
    // 16
    // Total members including those in the blacklist
    public int totalquorummembers = 0;
    // 18
    // Total Members in the whole of Netwotk
    // Sub Set of set of Validators as we can remove certain based on Utilitarian
    // (Excluding Blacklist)
    public int totaleffectivemembers = 0;

    // 19
    // Continuous Weak Altruists for 5 sub epoch then we can add to the blacklist
    // and add them back after three subepochs by removing from the blacklist again
    // and considering as part of the quorum (Only Blacklist)
    // Temporary Suspension
    // SubEpoch, Node Index at which the node Index was BlackListed
    // To be updated each time
    public Map<Integer, List<Integer>> blackListedMembers = new HashMap<>();

    // Classify the Utilitarian
    // 1 --> Ideal Utilitarian
    // 2 --> Utilitarian
    // 3 --> Fair Altruists
    // 4 --> Weak Altruists
    // Node Index , Utilitarian Type
    // To Update during the Quorum change
    // Sort and add the members to the quorum
    // 20
    public volatile Map<Integer, Integer> utilitarianClassification = new ConcurrentHashMap<Integer, Integer>();

    // Effective Utilitarian Score:
    // Index, SubEpoch Score
    // 21
    public volatile Map<Integer, Double> effectiveUtilitarianScore = new ConcurrentHashMap<Integer, Double>();

    // 22
    // Sorted Map based on Effective Score ithour the blacklist elements
    Map<Integer, Integer> sortedFinalMap = new HashMap<Integer, Integer>();

    // 23
    // Temp Index to get Proposer Index, Original Wallet Node Property
    Map<Integer, Integer> walletMapping = new HashMap<Integer, Integer>();

    // 24
    String messageComment;

    // 25
    Set<Integer> inActiveMembers = new HashSet<>();

    // 26
    Set<Integer> currentSuspendedMembers = new HashSet<>();

    @Override
    public String toString() {
        return "{" +
                " type='" + getType() + "'" +
                ", epoch='" + getEpoch() + "'" +
                ", subepoch='" + getSubEpoch() + "'" +
                ", blockheight='" + getBlockIndex() + "'" +
                ", messageSignature='" + getMessagesignature() + "'" +
                ", messageSender='" + getMessageSender() + "'" +
                ", nodeIndex='" + getNodeIndex() + "'" +
                ", quorumIdMapping='" + getQuorumIdMappping() + "'" +
                ", quorumIdInverseMapping='" + getQuorumIdInverseMapping() + "'" +
                ", quorumIndexMapping='" + getQuorumIndexMappping() + "'" +
                ", quorumIdMemberCount='" + getQuorumIdMemberCount() + "'" +
                ", idealAltruistList='" + getIdealAltruistList() + "'" +
                ", altruistList='" + getAltruistList() + "'" +
                ", fairAltruistList='" + getFairAltruistList() + "'" +
                ", weakAltruistList='" + getWeakAltruistList() + "'" +
                ", totalquorummembers='" + getTotalQuorumMembers() + "'" +
                ", totaleffectivemembers='" + getTotalEffectiveMembers() + "'" +
                ", blackListedMembers='" + getBlackListedMembers() + "'" +
                ", sortedFinalMap='" + getSortedFinalMap() + "'" +
                ", proposedSubEpoch='" + getProposedSubEpoch() + "'" +
                ", quorumMessageType='" + getQuorumMessageType() + "'" +
                ", originalNodeMapping='" + getOriginalNodeMap() + "'" +
                ", MessageComment='" + getMessageComment() + "'" +
                ", EffectiveUtilitarianScore='" + getEffectiveUtilitarianScore() + "'" +
                ", InactiveMembers='" + getInactiveMembers() + "'" +
                ", Suspended Members='" + getCurrentSuspendedMembers() + "'" +
                ", Wallet Mapping='" + getWalletMapping() + "'" +
                "}";
    }

    // Sub Epoch for which Quorum is to be proposed
    int proposedSubEpoch;

    // 24
    // Type of Quorum Message : NORMAL OR ROUND CHANGE
    // 1 or 0
    int quorumMessageType;

    public Set<Integer> getInactiveMembers() {
        return this.inActiveMembers;
    }

    public void setInactiveMembers(Set<Integer> curentInactiveMembers) {
        this.inActiveMembers = curentInactiveMembers;
    }

    public Set<Integer> getCurrentSuspendedMembers() {
        return this.currentSuspendedMembers;
    }

    public void setCurrentSuspendedMembers(Set<Integer> currentSuspendedMembers) {
        this.currentSuspendedMembers = currentSuspendedMembers;
    }

    public Map<Integer, Double> getEffectiveUtilitarianScore() {
        return this.effectiveUtilitarianScore;
    }

    public void setEffectiveUtilitarianScore(Map<Integer, Double> effectiveUtilitarianScore) {
        this.effectiveUtilitarianScore = effectiveUtilitarianScore;
    }

    public Map<Integer, Integer> getWalletMapping() {
        return this.walletMapping;
    }

    public void setWalletMapping(Map<Integer, Integer> inWalletMapping) {
        this.walletMapping = inWalletMapping;
    }

    public Map<Integer, QuorumOriginal> getOriginalNodeMap() {
        return this.originalNodeMap;
    }

    public void setOriginalNodeMap(Map<Integer, QuorumOriginal> inOriginalNodeMap) {
        this.originalNodeMap = inOriginalNodeMap;
    }

    public int getQuorumMessageType() {
        return this.quorumMessageType;
    }

    public void setQuorumMessageType(int input) {
        this.quorumMessageType = input;
    }

    public String getMessageComment() {
        return this.messageComment;
    }

    public void setMessageComment(String input) {
        this.messageComment = input;
    }

    public List<Integer> getIdealAltruistList() {
        return this.idealAltruistList;
    }

    public void setIdealAltruistList(List<Integer> idealAltruistList) {
        this.idealAltruistList = idealAltruistList;
    }

    public int getProposedSubEpoch() {
        return this.proposedSubEpoch;
    }

    public void setProposedSubEpoch(int proposedSubEpoch) {
        this.proposedSubEpoch = proposedSubEpoch;
    }

    public List<Integer> getAltruistList() {
        return this.altruistList;
    }

    public void setAltruistList(List<Integer> altruistList) {
        this.altruistList = altruistList;
    }

    public List<Integer> getFairAltruistList() {
        return this.fairAltruistList;
    }

    public void setFairAltruistList(List<Integer> fairAltruistList) {
        this.fairAltruistList = fairAltruistList;
    }

    public List<Integer> getWeakAltruistList() {
        return this.weakAltruistList;
    }

    public void setWeakAltruistList(List<Integer> weakAltruistList) {
        this.weakAltruistList = weakAltruistList;
    }

    public Map<Integer, Integer> getUtilitarianClassification() {
        return this.utilitarianClassification;
    }

    public void setUtilitarianClassification(Map<Integer, Integer> utilitarianClassification) {
        this.utilitarianClassification = utilitarianClassification;
    }

    public Map<Integer, Integer> getQuorumIndexMappping() {
        return this.quorumIndexMapping;
    }

    public void setQuorumIndexMappping(Map<Integer, Integer> quorumIndexMapping) {
        this.quorumIndexMapping = quorumIndexMapping;
    }

    public Map<Integer, List<Integer>> getQuorumIdInverseMapping() {
        return this.quorumIdInverseMapping;
    }

    public void setQuorumIdInverseMapping(Map<Integer, List<Integer>> quorumIdInverseMapping) {
        this.quorumIdInverseMapping = quorumIdInverseMapping;
    }

    public Map<Integer, Integer> getSortedFinalMap() {
        return this.sortedFinalMap;
    }

    public void setSortedFinalMap(Map<Integer, Integer> sortedFinalMap) {
        this.sortedFinalMap = sortedFinalMap;
    }

    public Map<Integer, Integer> getQuorumIdMappping() {
        return this.quorumIdMapping;
    }

    public void setQuorumIdMappping(Map<Integer, Integer> quorumIdMapping) {
        this.quorumIdMapping = quorumIdMapping;
    }

    public Map<Integer, Integer> getQuorumIdMemberCount() {
        return this.quorumIdMemberCount;
    }

    public void setQuorumIdMemberCount(Map<Integer, Integer> quorumIdMemberCount) {
        this.quorumIdMemberCount = quorumIdMemberCount;
    }

    public int getNodeIndex() {
        return this.nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBlockIndex() {
        return this.blockindex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockindex = blockIndex;
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

    // Setter for Total Effective Members
    public void setTotalEffectiveMembers(int input) {
        totaleffectivemembers = input;
    }

    // Getter for Total Effective Members
    public int getTotalEffectiveMembers() {
        return totaleffectivemembers;
    }

    // Setter for Total Quorum Members
    public void setTotalQuorumMembers(int input) {
        totalquorummembers = input;
    }

    // Getter for Total Quorum Members
    public int getTotalQuorumMembers() {
        return totalquorummembers;
    }

    // Setter for Total Quorum Members
    public void setBlackListedMembers(Map<Integer, List<Integer>> blackListedMembers) {
        this.blackListedMembers = blackListedMembers;
    }

    // Getter for Total Quorum Members
    public Map<Integer, List<Integer>> getBlackListedMembers() {
        return this.blackListedMembers;
    }

    public QuorumMessage(String type, int epoch,
            int subepoch, int nodeIndex, String messagesignature, String messageSender,
            int blockIndex, Map<Integer, Integer> quorumIdMapping, Map<Integer, List<Integer>> quorumIdInverseMapping,
            Map<Integer, Integer> quorumIndexMapping, Map<Integer, Integer> quorumIdMemberCount,
            int totaleffectivemembers,
            int totalquorummembers,
            Map<Integer, List<Integer>> blackListedMembers, Map<Integer, Integer> utilitarianClassification,
            Map<Integer, Double> effectiveUtilitarianScore, List<Integer> idealAltruistList,
            List<Integer> altruistList,
            List<Integer> fairAltruistList,
            List<Integer> weakAltruistList, Map<Integer, Integer> sortedFinalMap, int proposedSubEpoch,
            int quorumMessageType, Map<Integer, Integer> inWalletMapping, String messageComment,
            Set<Integer> inActiveMembers, Set<Integer> currentSuspendedMembers) {
        this.type = type;
        this.nodeIndex = nodeIndex;
        this.quorumIdMapping = quorumIdMapping;
        this.quorumIndexMapping = quorumIndexMapping;
        this.quorumIdInverseMapping = quorumIdInverseMapping;
        this.messageSignature = messagesignature;
        this.messageSender = messageSender;
        this.subepoch = subepoch;
        this.epoch = epoch;
        this.proposedSubEpoch = proposedSubEpoch;
        this.blockindex = blockIndex;
        this.totaleffectivemembers = totaleffectivemembers;
        this.totalquorummembers = totalquorummembers;
        this.blackListedMembers = blackListedMembers;
        this.utilitarianClassification = utilitarianClassification;
        this.effectiveUtilitarianScore = effectiveUtilitarianScore;
        this.idealAltruistList = idealAltruistList;
        this.altruistList = altruistList;
        this.fairAltruistList = fairAltruistList;
        this.weakAltruistList = weakAltruistList;
        this.sortedFinalMap = sortedFinalMap;
        this.quorumIdMemberCount = quorumIdMemberCount;
        this.quorumMessageType = quorumMessageType;
        this.walletMapping = inWalletMapping;
        this.messageComment = messageComment;
        this.inActiveMembers = inActiveMembers;
        this.currentSuspendedMembers = currentSuspendedMembers;
    }

}
