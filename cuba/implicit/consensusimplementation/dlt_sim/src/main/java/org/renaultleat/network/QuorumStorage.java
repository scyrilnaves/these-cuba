package org.renaultleat.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.renaultleat.consensus.QuorumMessage;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;

// Class for storing current Quorum State and the past states in the different SubEpochs
// Current State should be updated every time the quorum message is received for a sub epoch
public class QuorumStorage {

    public static boolean quorumInitialised = false;

    // Self Quorum Id for current SubEpoch
    // For participating in consensus
    public static int quorumid = 0;

    // Self Quorum Index for current SubEpoch
    // For to identify if a proposer withing the quorum
    public static int quorumindex = 0;

    // Total Members in the whole of Network for current SubEpoch
    // Sub Set of set of Validators as we can remove certain based on Utilitarian
    public static int totaleffectivemembers = 0;

    // Total members inside your quorum for current SubEpoch
    public static int totalquorummembers = 0;

    // Total members inside your quorum for current SubEpoch
    public static int currentmembersinsideurquorum = 0;

    // Ideal Altruists for current SubEpoch
    List<Integer> idealAltruistList = new ArrayList<Integer>();

    // Altruists for current SubEpoch
    List<Integer> altruistList = new ArrayList<Integer>();
    // Fair Altruists for current SubEpoch
    List<Integer> fairAltruistList = new ArrayList<Integer>();
    // Weak Altruits for current SubEpoch
    List<Integer> weakAltruistList = new ArrayList<Integer>();

    // Classify the Utilitarian
    // 1 --> Ideal Utilitarian
    // 2 --> Utilitarian
    // 3 --> Fair Altruists
    // 4 --> Weak Altruists
    // Node Index , Utilitarian Type
    // To Update during the Quorum change for current SubEpoch
    public volatile Map<Integer, Integer> utilitarianClassification = new ConcurrentHashMap<Integer, Integer>();

    // SubEpoch, Quorum State at the particular subEpoch
    public volatile NavigableMap<Integer, QuorumMessage> quorumMessageMap = new TreeMap<Integer, QuorumMessage>();

    // Effective Utilitarian Score:
    // Calculate at the end of the quorum or At the End of the Sub Epoch
    // SubEpoch, Index, Effective Score
    public volatile Map<Integer, Map<Integer, Double>> effectiveUtilitarianScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Intra Quorum Approvals for current Sub Epoch
    int intraQuorumApprovals = 0;

    // Inter Quorum Approvals for current Sub Epoch
    int interQuorumApprovals = 0;

    public Map<Integer, Map<Integer, Double>> getEffectiveUtilitarianScore() {
        return this.effectiveUtilitarianScore;

    }

    public Map<Integer, Map<Integer, Double>> getRawEffectiveUtilitarianScore() {

        // Transform Format from [SubEpoch, Node Index , Score] to [Node Index,
        // SubEpoch, Score]
        Map<Integer, Map<Integer, Double>> transformedResult = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Double>> entry : effectiveUtilitarianScore.entrySet()) {
            for (Map.Entry<Integer, Double> secEntry : entry.getValue().entrySet()) {
                if (!transformedResult.containsKey(secEntry.getKey())) {
                    Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
                    scoreMap.put(entry.getKey(), secEntry.getValue());
                    transformedResult.put(secEntry.getKey(), scoreMap);
                } else {
                    transformedResult.get(secEntry.getKey()).put(entry.getKey(), secEntry.getValue());
                }

            }
        }
        return transformedResult;

    }

    public Map<Integer, String> getRawQuorumSuspension(int blockchainCounter) {
        // SubEpoch, Suspension
        Map<Integer, String> rawQuorumSuspension = new HashMap<>();
        for (int subEpoch = 1; subEpoch < blockchainCounter; subEpoch++) {
            Set<Integer> suspendedMembers = this.quorumMessageMap.get(subEpoch)
                    .getCurrentSuspendedMembers();
            String supensionString = "";
            for (Integer suspendedMember : suspendedMembers) {
                supensionString += "Node" + suspendedMember + ";";
            }
            rawQuorumSuspension.put(subEpoch, supensionString);
        }

        return rawQuorumSuspension;
    }

    public Map<Integer, Map<Integer, String>> getRawUtilitarianClassification(int blockchainCounter) {
        // SubEpoch, Node Index, Classification
        Map<Integer, Map<Integer, String>> rawClassification = new HashMap<>();
        for (int subEpoch = 1; subEpoch < blockchainCounter; subEpoch++) {
            Map<Integer, String> classification = new HashMap<>();
            Map<Integer, Integer> integerClassification = this.quorumMessageMap.get(subEpoch)
                    .getUtilitarianClassification();
            for (Map.Entry<Integer, Integer> entry : integerClassification.entrySet()) {
                String classificationString = "";
                if (entry.getValue().equals(1)) {
                    classificationString = "Ideal Altruist";
                } else if (entry.getValue().equals(2)) {
                    classificationString = "Altruist";
                } else if (entry.getValue().equals(3)) {
                    classificationString = "Fair Altruist";
                } else if (entry.getValue().equals(4)) {
                    classificationString = "Weak Altruist";
                }
                classification.put(entry.getKey(), classificationString);
            }
            rawClassification.put(subEpoch, classification);
        }

        // Transform Format from [SubEpoch, Node Index , Classification] to [Node Index,
        // SubEpoch, Classification]
        Map<Integer, Map<Integer, String>> transformedResult = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, String>> entry : rawClassification.entrySet()) {
            for (Map.Entry<Integer, String> secEntry : entry.getValue().entrySet()) {
                if (!transformedResult.containsKey(secEntry.getKey())) {
                    Map<Integer, String> scoreMap = new HashMap<Integer, String>();
                    scoreMap.put(entry.getKey(), secEntry.getValue());
                    transformedResult.put(secEntry.getKey(), scoreMap);
                } else {
                    transformedResult.get(secEntry.getKey()).put(entry.getKey(), secEntry.getValue());
                }

            }
        }
        return transformedResult;
    }

    public void setEffectiveUtilitarianScore(Map<Integer, Map<Integer, Double>> effectiveUtilitarianScore) {
        this.effectiveUtilitarianScore = effectiveUtilitarianScore;

    }

    public double getFairnessCoefficient(int index) {
        if (utilitarianClassification.containsKey(index)) {
            int utilitarianType = utilitarianClassification.get(index);
            if (utilitarianType == 1) {
                // Ideal Altruists
                return CAPSEOBFTProperty.fairnessIdealAltruistsCoefficient;
            } else if (utilitarianType == 2) {
                // Altruists
                return CAPSEOBFTProperty.fairnessAltruistsCoefficient;
            } else if (utilitarianType == 3) {
                // Fair Altruists
                return CAPSEOBFTProperty.fairnessFairAltruistsCoefficient;
            } else if (utilitarianType == 4) {
                // Weak Altruists
                return CAPSEOBFTProperty.fairnessWeakAltruistsCoefficient;
            }
        }
        return 0;
    }

    public void updateQuorumMessage(QuorumMessage quorumMessage, Wallet wallet) {
        // System.out.println("qrummesaage" + quorumMessage.toString());
        // Permanent Storage for Historical Reasons at each Sub Epoch
        if (!quorumMessageMap.containsKey(quorumMessage.getSubEpoch())) {
            quorumMessageMap.put(quorumMessage.getSubEpoch(), quorumMessage);

            // Temporary Storage
            // System.out.println("QuorumId Intitial" + quorumid);
            updateQuorumId(
                    quorumMessage, wallet);
            updateQuorumIndex(
                    quorumMessage, wallet);
            // System.out.println("QuorumId After" + quorumid);
            totaleffectivemembers = quorumMessage.getTotalEffectiveMembers();
            totalquorummembers = quorumMessage.getTotalQuorumMembers();
            if (quorumid != CAPSEOBFTProperty.blacklistId) {
                currentmembersinsideurquorum = quorumMessage.getQuorumIdMemberCount().get(quorumid);
            } else {
                System.out.println("Blacklisted");
            }

            // Update Classfication Details
            idealAltruistList = quorumMessage.getIdealAltruistList();
            altruistList = quorumMessage.getAltruistList();
            fairAltruistList = quorumMessage.getFairAltruistList();
            weakAltruistList = quorumMessage.getWeakAltruistList();
            utilitarianClassification = quorumMessage.getUtilitarianClassification();

            // Update IntraQuorum Approvals Depending on the Quorum Members inside the
            // quorum
            float intraQuorumMembers = (float) this.currentmembersinsideurquorum;
            float intraQuorumApprovalsFloat = (intraQuorumMembers * (2.0f / 3.0f));
            int intraQuorumApprovals = (int) intraQuorumApprovalsFloat;
            this.intraQuorumApprovals = intraQuorumApprovals;
            // this.intraQuorumApprovals = 0;

            // Update InterQuorum Approvals Depending on the total effecive Quorum Members
            float interQuorumMembers = (float) this.totaleffectivemembers;
            float interQuorumApprovalsFloat = interQuorumMembers * (2.0f / 3.0f);
            int interQuorumApprovals = (int) (interQuorumApprovalsFloat);
            this.interQuorumApprovals = interQuorumApprovals;
            // this.interQuorumApprovals = 0;

            this.effectiveUtilitarianScore.put(quorumMessage.getSubEpoch(),
                    quorumMessage.getEffectiveUtilitarianScore());

            quorumInitialised = true;
        }
    }

    // Method to get the original Node Index based in Index within Quorum
    public int getPartialBlockProposerOriginalIndex(int subEpoch, int quorumId, int indexWithinQuorum) {
        // System.out.println("subEpoch" + subEpoch);
        // System.out.println("quorumId" + quorumId);
        // System.out.println("indexWithinQuorum" + indexWithinQuorum);
        return quorumMessageMap.get(subEpoch).getOriginalNodeMap().get(quorumId).getQuorumOriginalMappping()
                .get(indexWithinQuorum);
    }

    public NavigableMap<Integer, QuorumMessage> getQuorumMessageMap() {
        return quorumMessageMap;
    }

    // Set the Id of the Quorum
    public static void updateQuorumId(QuorumMessage quorumMessage, Wallet wallet) {
        // Set after Quorum Mesage
        if (quorumMessage.getQuorumIdMappping().containsKey(wallet.getNodeproperty())) {
            quorumid = quorumMessage.getQuorumIdMappping().get(wallet.getNodeproperty());
        } else {
            // Considering as BlackListed as id not presented in QuorumIdMappping
            quorumid = CAPSEOBFTProperty.blacklistId;
        }
    }

    // Set the Id of the Quorum
    public static void setQuorumId(int input) {
        // Set after Quorum Mesage
        quorumid = input;
    }

    public static int getQuorumId() {
        return quorumid;
    }

    // Set minimum approvals necessary inside the Quorum
    public void setIntraQuorumApprovals(int input) {
        // Set after Quorum Mesage
        intraQuorumApprovals = input;
    }

    public int getIntraQuorumApprovals() {
        return intraQuorumApprovals;
    }

    // Set minimum approvals necessary inside the Quorum
    public void setInterQuorumApprovals(int input) {
        // Set after Quorum Mesage
        interQuorumApprovals = input;
    }

    @Override
    public String toString() {
        return "{" +
                " All Sub Epoch='" + quorumMessageMap.keySet().toString() + "'" +
                "}";
    }

    public int getInterQuorumApprovals() {
        return interQuorumApprovals;
    }

    // Setter for Total Quorum Members
    public void setCurrentmembersinsideurquorum(int input) {
        currentmembersinsideurquorum = input;
    }

    // Getter for Total Quorum Members
    public int getCurrentmembersinsideurquorum() {
        return currentmembersinsideurquorum;
    }

    // Setter for Total Quorum Members
    public void setTotalQuorumMembers(int input) {
        totalquorummembers = input;
    }

    // Getter for Total Quorum Members
    public int getTotalQuorumMembers() {
        return totalquorummembers;
    }

    // Getter for Total Quorum Members
    public int getMembersofaQuorum(int subepoch, int quorumId) {
        // System.out.println("SUBSTORAGE" + subepoch);
        return quorumMessageMap.get(subepoch).getQuorumIdMemberCount().get(quorumId);
    }

    // Set the Index within a Quorum
    public static void setQuorumIndex(int input) {
        // Set after Quorum Index
        quorumindex = input;
    }

    // Set the Index within your Quorum
    public static void updateQuorumIndex(QuorumMessage quorumMessage, Wallet wallet) {
        // Set after Quorum Mesage
        if (quorumMessage.getQuorumIndexMappping().containsKey(wallet.getNodeproperty())) {
            quorumindex = quorumMessage.getQuorumIndexMappping().get(wallet.getNodeproperty());
        } else {
            // Considering as BlackListed as index not presented in QuorumIdMappping
            quorumindex = CAPSEOBFTProperty.blacklistId;
        }
    }

    public static int getQuorumIndex() {
        return quorumindex;
    }

    // Setter for Total Effective Members
    public static void setTotalEffectiveMembers(int input) {
        totaleffectivemembers = input;
    }

    // Getter for Total Effective Members
    public static int getTotalEffectiveMembers() {
        return totaleffectivemembers;
    }

    public void addNodeToClassification(int index, int classification) {
        if (utilitarianClassification.containsKey(index)) {
            utilitarianClassification.put(index, classification);
        } else {
            utilitarianClassification.put(index, classification);
        }
    }

}