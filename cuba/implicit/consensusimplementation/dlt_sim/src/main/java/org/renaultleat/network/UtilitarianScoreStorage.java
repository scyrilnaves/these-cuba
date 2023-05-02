package org.renaultleat.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.metamodel.ListAttribute;

import org.json.JSONObject;
import org.renaultleat.consensus.UtilitarianBlockScore;

// Class for Storing all Messages in Queue
public class UtilitarianScoreStorage {

    // To maintain the total ledger state of Utilitarian
    // To include epoch as well
    // Index of Node, Utilitarian Score
    // SubEpoch, Utilitarian Block Proposal Score
    // blockproposalwinscore ||
    // To Update during the BLOCK Receive

    // SubEpoch, Block No, InterBlockTimecoefficient

    public volatile Map<Integer, Map<Integer, Double>> interBlockTimeCoefficient = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Node Index, Sub Epoch, Score
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianBlockProposalScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Node Index, SubEpoch, Utilitarian Commit Score
    // CommitScore ||
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianCommitScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Node Index, SubEpoch, Missed Block Proposal Score
    // Index of Node, Utilitarian Failure in case of proposal
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianMissedBlockProposalScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Node Index, SubEpoch,Missed Commit Score
    // Index of Node, Utilitarian Failure in case of message communication
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianMissedCommitScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // MaliciousnScore of Peers
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianMaliciousScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Last Hearbeat Sent by a Peers
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianHeartBeatScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    // Missed Hearbeat Sent by a Peers
    public volatile Map<Integer, Map<Integer, Double>> PeerUtilitarianMissedHeartBeatScore = new ConcurrentHashMap<Integer, Map<Integer, Double>>();

    public String getInterBlockTimeCoefficient() {
        return this.interBlockTimeCoefficient.toString();
    }

    public String getPeerUtilitarianBlockProposalScore() {
        return this.PeerUtilitarianBlockProposalScore.toString();
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianBlockProposalScore() {
        return this.PeerUtilitarianBlockProposalScore;
    }

    public Map<Integer, Map<Integer, Double>> getRawInterBlockTimeCoefficient() {
        Map<Integer, Map<Integer, Double>> result = this.interBlockTimeCoefficient;

        // SubEpoch, BlokNo, TimeCoefficient
        Map<Integer, Map<Integer, Double>> transformedResult = new HashMap<>();
        // Need to transform this Result
        for (Map.Entry<Integer, Map<Integer, Double>> entry : result.entrySet()) {
            Map<Integer, Double> transformation = new HashMap<>();
            int i = 1;
            for (Map.Entry<Integer, Double> secEntry : entry.getValue().entrySet()) {
                transformation.put(i, secEntry.getValue());
                i = i + 1;
            }
            transformedResult.put(entry.getKey(), transformation);
        }
        return transformedResult;

    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianMissedBlockProposalScore() {
        return this.PeerUtilitarianMissedBlockProposalScore;
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianMissedCommitScore() {
        return this.PeerUtilitarianMissedCommitScore;
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianMaliciousScore() {
        return this.PeerUtilitarianMaliciousScore;
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianHeartBeatScore() {
        return this.PeerUtilitarianHeartBeatScore;
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianMissedHeartBeatScore() {
        return this.PeerUtilitarianMissedHeartBeatScore;
    }

    public String getPeerUtilitarianCommitScore() {
        return this.PeerUtilitarianCommitScore.toString();
    }

    public Map<Integer, Map<Integer, Double>> getRawPeerUtilitarianCommitScore() {
        return this.PeerUtilitarianCommitScore;
    }

    public String getPeerUtilitarianMissedBlockProposalScore() {
        return this.PeerUtilitarianMissedBlockProposalScore.toString();
    }

    public String getPeerUtilitarianMissedCommitScore() {
        return this.PeerUtilitarianMissedCommitScore.toString();
    }

    public String getPeerUtilitarianMaliciousScore() {
        return this.PeerUtilitarianMaliciousScore.toString();
    }

    public String getPeerUtilitarianHeartBeatScore() {
        return this.PeerUtilitarianHeartBeatScore.toString();
    }

    public String getPeerUtilitarianMissedHeartBeatScore() {
        return this.PeerUtilitarianMissedHeartBeatScore.toString();
    }

    public void updateAtruismBlockStorage(UtilitarianBlockScore utilitarianBlockScore) {

        int index = utilitarianBlockScore.getNodeIndex();

        int subEpoch = utilitarianBlockScore.getSubEpoch();

        // Block Proposal Score
        Map<Integer, Double> peerUtilitarianProposalScoreMap = utilitarianBlockScore.getpeerUtilitarianScoreMap();

        // Missed Commit Message
        Map<Integer, Double> peerUtilitarianCommitScoreMap = utilitarianBlockScore.getPeerUtilitarianCommitScoreMap();

        // Missed Proposal
        Map<Integer, Double> peerUtilitarianMissedProposalScoreMap = utilitarianBlockScore
                .getPeerUtilitarianMissedProposalScoreMap();

        // Missed Commit Message
        Map<Integer, Double> peerUtilitarianMissedCommitScoreMap = utilitarianBlockScore
                .getPeerUtilitarianMissedCommitScoreMap();

        // HeartBeat Message
        Map<Integer, Double> peerUtilitarianHeartBeatScoreMap = utilitarianBlockScore
                .getPeerUtilitarianHeartBeatScoreMap();

        // Missed Heart Beat Message
        Map<Integer, Double> peerUtilitarianMissedHeartBeatScoreMap = utilitarianBlockScore
                .getPeerUtilitarianMissedHeartBeatScoreMap();

        // Malicious Message
        Map<Integer, Double> peerUtilitarianMaliciousScoreMap = utilitarianBlockScore
                .getPeerUtilitarianMaliciousBeatScoreMap();

        // Peer Utilitarian Proposal Score
        for (Map.Entry<Integer, Double> peerAlruismMapElements : peerUtilitarianProposalScoreMap.entrySet()) {
            incrementUtilitarianBlockProposalScore(peerAlruismMapElements.getKey(), subEpoch,
                    peerAlruismMapElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerAlruismCommitMapElements : peerUtilitarianCommitScoreMap.entrySet()) {
            incrementUtilitarianCommitScore(peerAlruismCommitMapElements.getKey(), subEpoch,
                    peerAlruismCommitMapElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerAlruismMissedProposalElements : peerUtilitarianMissedProposalScoreMap
                .entrySet()) {
            incrementUtilitarianMissedBlockProposalScore(peerAlruismMissedProposalElements.getKey(), subEpoch,
                    peerAlruismMissedProposalElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerAlruismMissedCommitElements : peerUtilitarianMissedCommitScoreMap
                .entrySet()) {
            incrementUtilitarianMissedCommitScore(peerAlruismMissedCommitElements.getKey(), subEpoch,
                    peerAlruismMissedCommitElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerAlruismHeartBeatElements : peerUtilitarianHeartBeatScoreMap.entrySet()) {
            incrementPeerHeartBeatScore(peerAlruismHeartBeatElements.getKey(), subEpoch,
                    peerAlruismHeartBeatElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerAlruismMissedHeartBeatElements : peerUtilitarianMissedHeartBeatScoreMap
                .entrySet()) {
            incrementPeerMissedHeartBeatScore(peerAlruismMissedHeartBeatElements.getKey(), subEpoch,
                    peerAlruismMissedHeartBeatElements.getValue());
        }

        for (Map.Entry<Integer, Double> peerMaliciousElements : peerUtilitarianMaliciousScoreMap
                .entrySet()) {
            incrementPeerMaliciousScore(peerMaliciousElements.getKey(), subEpoch,
                    peerMaliciousElements.getValue());
        }

        incrementblockTimeCoefficient(subEpoch, utilitarianBlockScore.getBlockNo(),
                utilitarianBlockScore.getInterblockTimecoefficient());

    }

    // For Setting Utilitarian Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementUtilitarianBlockProposalScore(int index, int subepoch, double score) {
        // check for Node index
        if (PeerUtilitarianBlockProposalScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianBlockProposalScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianBlockProposalScore.get(index).get(subepoch);
                PeerUtilitarianBlockProposalScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianBlockProposalScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianBlockProposalScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianBlockProposalScore.put(index, subEpochMap);
        }

    }

    public void incrementblockTimeCoefficient(int subepoch, int blockno, double coefficient) {
        // check for Node index
        // System.out.println("Increment called Blocktime" + blockno);
        if (this.interBlockTimeCoefficient.containsKey(subepoch)) {
            // check for Node index and epoch
            if (this.interBlockTimeCoefficient.get(subepoch).containsKey(blockno)) {
                // Then update score for subepoch
                double oldscore = this.interBlockTimeCoefficient.get(subepoch).get(blockno);
                this.interBlockTimeCoefficient.get(subepoch).put(blockno, oldscore + coefficient);
            } else {
                this.interBlockTimeCoefficient.get(subepoch).put(blockno, coefficient);
            }
        } else {
            Map<Integer, Double> blockMap = new HashMap<>();
            blockMap.put(blockno, coefficient);
            this.interBlockTimeCoefficient.put(subepoch, blockMap);
        }

    }

    // For Getting Utilitarian Score
    public double getUtilitarianBlockProposalScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianBlockProposalScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianBlockProposalScore.get(index).containsKey(subepoch)) {
                // check score for subepoch
                return PeerUtilitarianBlockProposalScore.get(index).get(subepoch);
            }
        }
        return 0;
    }

    // For Setting Utilitarian Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementUtilitarianCommitScore(int index, int subepoch, double score) {
        // check for Node index
        // PeerUtilitarianCommitScore
        if (PeerUtilitarianCommitScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianCommitScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianCommitScore.get(index).get(subepoch);
                PeerUtilitarianCommitScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianCommitScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianCommitScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianCommitScore.put(index, subEpochMap);
        }

    }

    // For Getting Utilitarian Score
    public double getUtilitarianCommitScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianCommitScore.containsKey(index)) {
            // check for Node index and epoch
            // check score for subepoch
            if (PeerUtilitarianCommitScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianCommitScore.get(index).get(subepoch);
            }

        }
        return 0;
    }

    // For Setting Utilitarian Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    // PeerUtilitarianMissedBlockProposalScore
    public void incrementUtilitarianMissedBlockProposalScore(int index, int subepoch, double score) {
        // PeerUtilitarianMissedBlockProposalScore
        // check for Node index
        if (PeerUtilitarianMissedBlockProposalScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianMissedBlockProposalScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianMissedBlockProposalScore.get(index).get(subepoch);
                PeerUtilitarianMissedBlockProposalScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianMissedBlockProposalScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianMissedBlockProposalScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianMissedBlockProposalScore.put(index, subEpochMap);
        }

    }

    // For Getting Utilitarian Score
    public double getUtilitarianMissedBlockProposalScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianMissedBlockProposalScore.containsKey(index)) {
            // check for Node index and epoch
            // check score for subepoch
            if (PeerUtilitarianMissedBlockProposalScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianMissedBlockProposalScore.get(index).get(subepoch);
            }

        }
        return 0;
    }

    // For Setting Utilitarian Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    // PeerUtilitarianMissedBlockProposalScore
    public void incrementUtilitarianMissedCommitScore(int index, int subepoch, double score) {
        // check for Node index
        // PeerUtilitarianMissedCommitScore
        if (PeerUtilitarianMissedCommitScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianMissedCommitScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianMissedCommitScore.get(index).get(subepoch);
                PeerUtilitarianMissedCommitScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianMissedCommitScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianMissedCommitScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianMissedCommitScore.put(index, subEpochMap);
        }
    }

    // For Getting Utilitarian Score
    public double getUtilitarianMissedCommitScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianMissedCommitScore.containsKey(index)) {
            // check for Node index and epoch
            // check score for subepoch
            if (PeerUtilitarianMissedCommitScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianMissedCommitScore.get(index).get(subepoch);
            }

        }
        return 0;
    }

    // For Setting Utilitarian Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementPeerMaliciousScore(int index, int subepoch, double score) {
        // check for Node index
        // PeerUtilitarianMaliciousScore
        if (PeerUtilitarianMaliciousScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianMaliciousScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianMaliciousScore.get(index).get(subepoch);
                PeerUtilitarianMaliciousScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianMaliciousScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianMaliciousScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianMaliciousScore.put(index, subEpochMap);
        }

    }

    // For Getting Malicious Score
    public double getPeerMaliciousScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianMaliciousScore.containsKey(index)) {
            // check for Node index and epoch
            // check score for subepoch
            if (PeerUtilitarianMaliciousScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianMaliciousScore.get(index).get(subepoch);
            }
        }
        return 0;
    }

    // For Setting HeartBeat Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementPeerHeartBeatScore(int index, int subepoch, double score) {
        // check for Node index
        // PeerUtilitarianHeartBeatScore
        if (PeerUtilitarianHeartBeatScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianHeartBeatScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianHeartBeatScore.get(index).get(subepoch);
                PeerUtilitarianHeartBeatScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianHeartBeatScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianHeartBeatScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianHeartBeatScore.put(index, subEpochMap);
        }

    }

    // For Getting HeartBeat Score
    public double getPeerHeartBeatScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianHeartBeatScore.containsKey(index)) {
            // check for Node index and epoch
            // check score for subepoch
            if (PeerUtilitarianHeartBeatScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianHeartBeatScore.get(index).get(subepoch);
            }
        }
        return 0;
    }

    // For Setting HeartBeat Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementPeerMissedHeartBeatScore(int index, int subepoch, double score) {
        // check for Node index
        // PeerUtilitarianMissedHeartBeatScore
        if (PeerUtilitarianMissedHeartBeatScore.containsKey(index)) {
            // check for Node index and epoch
            if (PeerUtilitarianMissedHeartBeatScore.get(index).containsKey(subepoch)) {
                // Then update score for subepoch
                double oldscore = PeerUtilitarianMissedHeartBeatScore.get(index).get(subepoch);
                PeerUtilitarianMissedHeartBeatScore.get(index).put(subepoch, oldscore + score);
            } else {
                Map<Integer, Double> subEpochMap = PeerUtilitarianMissedHeartBeatScore.get(index);
                subEpochMap.put(subepoch, score);
                PeerUtilitarianMissedHeartBeatScore.put(index, subEpochMap);
            }
        } else {
            Map<Integer, Double> subEpochMap = new HashMap<>();
            subEpochMap.put(subepoch, score);
            PeerUtilitarianMissedHeartBeatScore.put(index, subEpochMap);
        }
    }

    // For Getting HeartBeat Score
    public double getPeerMissedHeartBeatScore(int index, int subepoch) {
        // check for Node index
        if (PeerUtilitarianMissedHeartBeatScore.containsKey(index)) {
            // check score for subepoch
            if (PeerUtilitarianMissedHeartBeatScore.get(index).containsKey(subepoch)) {
                return PeerUtilitarianMissedHeartBeatScore.get(index).get(subepoch);
            }
        }
        return 0;
    }

}