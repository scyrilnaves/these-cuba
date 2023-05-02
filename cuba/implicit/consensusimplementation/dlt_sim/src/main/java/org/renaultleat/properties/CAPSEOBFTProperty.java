package org.renaultleat.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.renaultleat.node.Wallet;

public class CAPSEOBFTProperty {

    public static final double timethreshold = 1;

    // Milliseconds to coefficient conversion
    public static final double conversioncoefficient = 100000;

    public static final double defaultcoefficient = 0.00001;

    // When a member of Quorum wins the block proposal
    public static final double blockproposalcore = 0.5;
    // When a member of Quorum loses the block proposal
    public static final double blockproposalmisscore = 0.3;
    // When a member of Quorum commit the block proposal
    public static final double blockcommitcore = 0.3;
    // When a member of Quorum does not commit the block proposal
    public static final double blockcommitmisscore = 0.3;
    // When a member of Quorum proposes a mailicious block or forges signature ..etc
    public static final double maliciousscore = 0.5;
    // When a member of Quorum gives a heart beat within the time interval of the
    // full block
    public static final double heartbeatscore = 0.1;
    // When a member of Quorum misses a heart beat within the time interval of the
    // full block
    public static final double missedheartbeatscore = 0.1;

    // FAIRNESS COEFFICIENT
    // No Coefficient for Ideal Altruists
    // Coefficient for Ideal Altruists
    public static final double fairnessIdealAltruistsCoefficient = 0;
    // Coefficient for Altruists
    public static final double fairnessAltruistsCoefficient = 0.05;
    // Coefficient for fair Altruists
    public static final double fairnessFairAltruistsCoefficient = 0.1;
    // Coefficient for weak Altruists
    public static final double fairnessWeakAltruistsCoefficient = 0.15;

    // TIME DEFINITION PROPERTY
    // Block Size as a measure for epoch and subepoch
    public static int epochthreshold = 4;

    public static int subepochthreshold = 2;

    public static int genesisindex = 1;

    // BLACK LIST PROPERTY
    // 999999 is in valid
    public static int blacklistId = 99999999;

    // Quorum Propoerty

    // Quorum Message Type
    public static int normalQuorumMessage = 1;
    public static int roundChangeQuorumMessage = 0;

    // Privacy Message Type
    public static String requestPrivacyMessage = "REQUEST";
    public static String responsePrivacyMesage = "RESPONSE";

    // Full Block Proposal Type
    public static String normalProposal = "NORMAL";
    public static String normalFulfillment = "FULFILL";

    // Partial Block Proposal Type
    public static String normalPartialProposal = "NORMAL";
    public static String normalPartialFulfillment = "FULFILL";

    // BlackList SubEpoch Threshold to compare against
    public static int blackListThreshold = 5;

    // Default Timestamp if blocks dont arrive in time
    public static int defaultTimeStampDifference = 50000;

    public static int minquorumforblacklist = 4;

    // Total Quorums in the network
    // Total possible quorum in the network
    public static int totalquorums = 0;

    public static int minmembersinsidequoum = 2;

    public static int totalclassification = 4;

    public static int totalweakmemberpercent = 5;

    // Setter for Total Quorums
    public static void setTotalquorums(int input) {
        totalquorums = input;
    }

    // Getter for Total Quorums
    public static int getTotalQuorums() {
        return totalquorums;
    }

    // Setter for Epoch Threshold
    public static void setEpochThreshold(int input) {
        epochthreshold = input;
    }

    // Getter for Epoch Threshold
    public static int getEpochThreshold() {
        return epochthreshold;
    }

    // Setter for Sub Epoch Threshold
    public static void setSubEpochThreshold(int input) {
        subepochthreshold = input;
    }

    // Getter for Sub Epoch Threshold
    public static int getSubEpochThreshold() {
        return subepochthreshold;
    }

    // Setter for Sub Epoch Threshold
    public static void setBlackListThreshold(int input) {
        blackListThreshold = input;
    }

    // Getter for Sub Epoch Threshold
    public static int getBlackListThreshold() {
        return blackListThreshold;
    }

}
