package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.api.Simulator_result;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.PartialBlock;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

/**
 * This Handler is used in case of Multi threaded call from P2PHandler or single
 * threaded we call PBFTMessageHandler
 * 
 * Class to append Block reeived by Non Validators from Validators
 * 
 */

public class UtilitarianCalculator {

    public Blockchain blockchain;

    public HeartBeatStorage heartBeatStorage;
    public QuorumStorage quorumStorage;

    public UtilitarianScoreStorage utilitarianScoreStorage;
    public Wallet wallet;

    // Get Respective Score
    // Apply Inter Block Coefficient
    // Apply Fairness Coefficient
    // Only Apply sisyphus only during quorum
    public UtilitarianBlockScore getUtilitarianBlockScore(Block currentBlock, Blockchain blockChain,
            HeartBeatStorage heartBeatStorage) {

        // Block Proposal
        Map<Integer, Double> peerUtilitarianProposalScoreWinMap = new HashMap<Integer, Double>();

        // Block Proposal
        Map<Integer, Double> peerUtilitarianProposalScoreLossMap = new HashMap<Integer, Double>();

        // Commit Message
        Map<Integer, Double> peerUtilitarianCommitScoreMap = new HashMap<Integer, Double>();

        // Missed Proposal
        Map<Integer, Double> peerUtilitarianMissedCommitScoreMap = new HashMap<Integer, Double>();

        // Heart Beat Message
        Map<Integer, Double> peerHeartBeatScoreMap = new HashMap<Integer, Double>();

        // Heart Beat Missed Message
        Map<Integer, Double> peerMissedHeartBeatScoreMap = new HashMap<Integer, Double>();

        // Malicious Activity
        Map<Integer, Double> peerMaliciousScoreMap = new HashMap<Integer, Double>();

        List<Integer> totalProposalValidators = new ArrayList<>();

        List<Integer> totalCommitValidators = new ArrayList<>();

        List<Integer> totalMissedProposalValidators = new ArrayList<>();

        List<Integer> totalMissedMessageValidators = new ArrayList<>();

        List<Integer> totalHeartBeatMessengers = new ArrayList<>();

        List<Integer> totalHeartBeatMissers = new ArrayList<>();
        // System.out.println("inside score calculator" +
        // currentBlock.getBlocknumber());
        // Get Previous Block details to get the time interval
        int previousblockno = currentBlock.getBlocknumber() - 1;
        double interblocktimedifference = 0;
        if (blockChain.getChain().containsKey(previousblockno)) {
            Block previousblock = blockChain.getChain().get(previousblockno);
            interblocktimedifference = currentBlock.getBlocktime().getTime() - previousblock.getBlocktime().getTime();
        } else {
            interblocktimedifference = CAPSEOBFTProperty.defaultTimeStampDifference;
        }

        // System.out.println("currentblocktime" +
        // currentBlock.getBlocktime().getTime());
        // System.out.println("previousblocktime" + previousBlockTimeStamp.getTime());
        // System.out.println(
        // "interblocktimedifference" + (interblocktimedifference /
        // CAPSEOBFTProperty.conversioncoefficient));

        // In to secs
        double differenceconverted = (interblocktimedifference / CAPSEOBFTProperty.conversioncoefficient);
        double timecoefficient = 0;
        if (differenceconverted >= 1) {
            timecoefficient = CAPSEOBFTProperty.defaultcoefficient;
        } else {
            timecoefficient = 1 - differenceconverted;
        }
        // System.out.println("differenceconverted" + differenceconverted);
        // Convert to double

        // System.out.println("timecoefficient" + timecoefficient);
        double netblockproposalcore = CAPSEOBFTProperty.blockproposalcore * timecoefficient;
        double netblockproposalmisscore = CAPSEOBFTProperty.blockproposalmisscore * timecoefficient;
        double netblockcommitcore = CAPSEOBFTProperty.blockcommitcore * timecoefficient;
        double netblockcommitmisscore = CAPSEOBFTProperty.blockcommitmisscore * timecoefficient;
        double netmaliciousscore = CAPSEOBFTProperty.maliciousscore * timecoefficient;
        double netheartbeatscore = CAPSEOBFTProperty.heartbeatscore * timecoefficient;
        double netmissedheartbeatscore = CAPSEOBFTProperty.missedheartbeatscore * timecoefficient;

        // QuroumID, Details
        Map<Integer, QuorumOriginal> originalNodeMap = quorumStorage.getQuorumMessageMap()
                .get(currentBlock.getSubEpoch()).getOriginalNodeMap();

        for (Map.Entry<Integer, PartialBlock> entry : currentBlock.getPartialBlockMap().entrySet()) {

            PartialBlock partialBlock = entry.getValue();
            // System.out.println(
            // "Qrum currnt subepoch" + currentBlock.getSubEpoch());
            // System.out.println(
            // "Qrum Storage" +
            // quorumStorage.getQuorumMessageMap().get(currentBlock.getSubEpoch()).toString());
            // Get Particular Quorum Members
            List<Integer> quorumMembersIndex = new ArrayList(
                    quorumStorage.getQuorumMessageMap().get(currentBlock.getSubEpoch())
                            .getQuorumIdInverseMapping().get(partialBlock.getQuorumId()));
            // Convert Quorum Id and Index to original Wallet Node Property
            QuorumOriginal quorumOriginal = originalNodeMap.get(partialBlock.getQuorumId());
            // Node Index within Quorum, Original Node Id
            Map<Integer, Integer> quorumOriginalMapping = quorumOriginal.getQuorumOriginalMappping();

            List<Integer> quorumMembers = new ArrayList<>(quorumMembersIndex);

            List<Integer> quorumMembersDuplicate = new ArrayList<>(quorumMembers);
            // Total Heart Beat Senders
            List<Integer> heartBeatWinners = populateHeartBeatWin(quorumMembersDuplicate, currentBlock.getBlocktime());
            totalHeartBeatMessengers.addAll(heartBeatWinners);
            quorumMembersDuplicate.removeAll(heartBeatWinners);
            // Total Heart Beat Losers
            List<Integer> heartBeatLosers = quorumMembersDuplicate;

            totalHeartBeatMissers.addAll(heartBeatLosers);

            // Total Commit Validators
            List<Integer> commitValidators = (partialBlock.getCommitMessageValidatorIndexes());

            // Remove the committer who don send heart beat for IMPLICIT VOTING
            commitValidators.removeAll(totalHeartBeatMissers);

            totalCommitValidators.addAll(commitValidators);
            // Total proposers
            totalProposalValidators.add(partialBlock.getOriginalProposerNodeIndex());
            // Total missed proposers
            totalMissedProposalValidators.add(partialBlock.getOriginalRivalProposerIndex());

            // Total missed commit messengers
            quorumMembers.removeAll(commitValidators);

            totalMissedMessageValidators.addAll(quorumMembers);

        }
        // System.out.println("bloko" + currentBlock.getBlocknumber());
        // System.out.println("Proposers" + totalProposalValidators);
        // System.out.println("Committers" + totalCommitValidators);
        // System.out.println("Missed Poposers" + totalProposalValidators);
        // System.out.println("Missed Comiter" + totalMissedMessageValidators);
        // System.out.println("Heartbeates" + totalHeartBeatMessengers);
        // System.out.println("MissedHeartbeates" + totalHeartBeatMissers);

        // Heart Beat Calculation: HeartBeat Storage

        // Block win
        updateScoreMapwithFairness(peerUtilitarianProposalScoreWinMap, totalProposalValidators, netblockproposalcore);
        // Block lose
        updateScoreMapwithFairness(peerUtilitarianProposalScoreLossMap, totalMissedProposalValidators,
                netblockproposalmisscore);
        // Commit Win
        updateScoreMapwithFairness(peerUtilitarianCommitScoreMap, totalCommitValidators, netblockcommitcore);
        // Commit Lose
        updateScoreMapwithFairness(peerUtilitarianMissedCommitScoreMap, totalMissedMessageValidators,
                netblockcommitmisscore);
        // HeartBeat Win
        updateScoreMapwithFairness(peerHeartBeatScoreMap, totalHeartBeatMessengers,
                netheartbeatscore);
        // HeartBeat Loss
        updateScoreMapwithFairness(peerMissedHeartBeatScoreMap, totalHeartBeatMissers,
                netmissedheartbeatscore);

        if (currentBlock.isValidity() == false) {
            // Malicious Act for full Block
            List<Integer> fullBlockProposers = new ArrayList<>();
            fullBlockProposers.add(currentBlock.getOriginalproposernodeindex());

            updateScoreMapwithFairness(peerMaliciousScoreMap, fullBlockProposers,
                    netmaliciousscore);
        }

        List<Integer> partialBlockMaliciousProposers = new ArrayList<>();
        for (Map.Entry<Integer, PartialBlock> entry : currentBlock.getPartialBlockMap().entrySet()) {
            // Malicious Act for partial Block
            PartialBlock partialBlock = entry.getValue();
            if (partialBlock.isValidity() == false) {
                partialBlockMaliciousProposers.add(partialBlock.getOriginalProposerNodeIndex());
                partialBlockMaliciousProposers.addAll(partialBlock.getCommitMessageValidatorIndexes());
            }
        }
        // Malicious Act for Partial Block
        updateScoreMapwithFairness(peerMaliciousScoreMap, partialBlockMaliciousProposers,
                netmaliciousscore);

        UtilitarianBlockScore utilitarianBlockScore = new UtilitarianBlockScore(Wallet.getNodeproperty(),
                currentBlock.getBlocknumber(), currentBlock.getSubEpoch(),
                peerUtilitarianProposalScoreWinMap, peerUtilitarianProposalScoreLossMap, peerUtilitarianCommitScoreMap,
                peerUtilitarianMissedCommitScoreMap, peerHeartBeatScoreMap, peerMissedHeartBeatScoreMap,
                peerMaliciousScoreMap, timecoefficient);
        return utilitarianBlockScore;

    }

    public List<Integer> populateHeartBeatWin(List<Integer> quorumMembers, Timestamp blockTime) {
        // System.out.println("New Membes" + quorumMembers.toString());
        List<Integer> heartBeatSenders = new ArrayList<>();
        for (Integer member : quorumMembers) {
            Timestamp lastHeartBeatTimeStamp = this.heartBeatStorage.getLatestPeerHeartBeat(member);
            // System.out.println("lastHeartBeatTimeStamp" +
            // lastHeartBeatTimeStamp.getTime());
            // System.out.println("blockTimeStamp" + blockTime.getTime());
            // Compare
            // Check if the heart beat is received recently at the time of calulation
            double timedifference = (lastHeartBeatTimeStamp.getTime() - blockTime.getTime())
                    / CAPSEOBFTProperty.conversioncoefficient;
            // If Heartbeat is equal to block timestamp OR
            // If Heartbeat is after the block timestamp
            // Add the member
            if (Math.abs(timedifference) < CAPSEOBFTProperty.timethreshold) {
                heartBeatSenders.add(member);
            }
        }
        return heartBeatSenders;
    }

    public void updateScoreMap(Map<Integer, Double> scoreMap, List<Integer> scorers, double scorefactor) {
        for (Integer index : scorers) {
            if (scoreMap.containsKey(index)) {
                double earlierscore = scoreMap.get(index);
                double newscore = earlierscore + scorefactor;
                scoreMap.put(index, newscore);
            } else {
                scoreMap.put(index, scorefactor);
            }
        }
    }

    // Update Score with Fairness Coefficient
    public void updateScoreMapwithFairness(Map<Integer, Double> scoreMap, List<Integer> scorers, double scorefactor) {
        for (Integer index : scorers) {
            double fairnesscoefficient = quorumStorage.getFairnessCoefficient(index);
            double fairscore = scorefactor + fairnesscoefficient;
            if (scoreMap.containsKey(index)) {
                double earlierscore = scoreMap.get(index);
                double newscore = earlierscore + fairscore;
                scoreMap.put(index, newscore);
            } else {

                scoreMap.put(index, fairscore);
            }
        }
    }

    // currentSubEpoch: Epoch End at whihc calculation of Quorum is to be formed
    // Type of Quorum Message can be Normal or Round Change // 1 or 0
    public QuorumMessage formQuorumMessageForNextSubEpoch(int proposedSubEpoch, int type, int blockIndex)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, URISyntaxException {
        // System.out.println("FORMING QUORUM MESSAGE:" + proposedSubEpoch);
        int currentSubEpoch = proposedSubEpoch - 1;
        // Form Effective Sore for Latest Sub Epoch by looking at the Utilitarian
        // Storage
        // Get the All the quorum memebers from the quorum storage (quorumIndexMapping)
        int firstindexsubEpoch = 1;
        // We choose first index as there is no black list at this sub epoch
        Set<Integer> quorumMembers = quorumStorage.getQuorumMessageMap().get(firstindexsubEpoch).getQuorumIdMappping()
                .keySet();
        // System.out.println("FORMING QUORUM MESSAGE1" + quorumMembers.toString());
        // Form Effective Utilitarian Score for the current latest subepoch
        // Node Index, Score
        Map<Integer, Double> currentEffectiveUtilitarianScore = new HashMap<Integer, Double>();
        for (Integer index : quorumMembers) {
            // Then get the Positive score and the negative score
            double positivescore = 0;
            double negativescore = 0;
            double effectivescore = 0;
            // Block Proposal + Commit + Heart Beat
            positivescore += utilitarianScoreStorage.getUtilitarianBlockProposalScore(index, currentSubEpoch)
                    + utilitarianScoreStorage.getUtilitarianCommitScore(index, currentSubEpoch)
                    + utilitarianScoreStorage.getPeerHeartBeatScore(index, currentSubEpoch);
            // System.out.println("Index:" + index + "blockproposal:"
            // + utilitarianScoreStorage.getUtilitarianBlockProposalScore(index,
            // currentSubEpoch));
            // System.out.println("Index:" + index + "utilitariancommit:"
            // + utilitarianScoreStorage.getUtilitarianCommitScore(index, currentSubEpoch));
            // System.out.println("Index:" + index + "peerhearbeat:"
            // + utilitarianScoreStorage.getPeerHeartBeatScore(index, currentSubEpoch));
            // Block Miss + Commit Miss + Heart Beat Miss + Malicious Score
            negativescore += utilitarianScoreStorage.getUtilitarianMissedBlockProposalScore(index, currentSubEpoch)
                    + utilitarianScoreStorage.getUtilitarianMissedCommitScore(index, currentSubEpoch)
                    + utilitarianScoreStorage.getPeerMaliciousScore(index, currentSubEpoch)
                    + utilitarianScoreStorage.getPeerMissedHeartBeatScore(index, currentSubEpoch);
            // System.out.println("Index:" + index + "missedblock:"
            // + utilitarianScoreStorage.getUtilitarianMissedBlockProposalScore(index,
            // currentSubEpoch));
            // System.out.println("Index:" + index + "missedcommit:"
            // + utilitarianScoreStorage.getUtilitarianMissedBlockProposalScore(index,
            // currentSubEpoch));
            // System.out.println("Index:" + index + "peermalicious:"
            // + utilitarianScoreStorage.getPeerMaliciousScore(index, currentSubEpoch));
            // System.out.println("Index:" + index + "peermissedheartbeat:"
            // + utilitarianScoreStorage.getPeerMissedHeartBeatScore(index,
            // currentSubEpoch));
            effectivescore = positivescore - negativescore;
            if (effectivescore < 0) {
                // Cannot make it negative as , so we set a default value
                effectivescore = CAPSEOBFTProperty.defaultcoefficient;
            }
            currentEffectiveUtilitarianScore.put(index, effectivescore);
        }
        // System.out.println("FORMING QUORUM MESSAGE2" +
        // currentEffectiveUtilitarianScore.toString());
        // Aggregate all the effective score for the index depending upon the subepoch.
        // Form the forgetting coefficient since the latest subepoch
        // Previous SubEpoch, Score Coefficient
        Map<Integer, Double> forgettingCoefficient = new HashMap<Integer, Double>();
        // SubEpoch start from 1
        for (int i = (currentSubEpoch - 1); i > 0; i--) {
            double forgettingcoefficient = 1 - ((currentSubEpoch - i) / currentSubEpoch);
            forgettingCoefficient.put(i, forgettingcoefficient);
        }
        // System.out.println("Forgetting Coefficient" +
        // forgettingCoefficient.toString());
        // SubEpoch, Index, Score
        Map<Integer, Map<Integer, Double>> historicalEffectiveUtilitarianScore = quorumStorage
                .getEffectiveUtilitarianScore();
        // System.out.println("historicalEffectiveUtilitarianScore" +
        // historicalEffectiveUtilitarianScore.toString());
        // Form Score for Sorting Elements including forgetting element at each subepoch
        Map<Integer, Double> finalSortingScoreMap = new HashMap<Integer, Double>();
        // Remove Elements if did not sent hart beat recently
        List<Integer> activeNodes = heartBeatStorage.getActivePeers();
        // System.out.println("Active Nodes" + activeNodes.toString());

        List<Integer> activeNodesDuplicate = heartBeatStorage.getActivePeers();
        List<Integer> allMembers = new ArrayList<>(quorumMembers);

        allMembers.removeAll(activeNodesDuplicate);
        Set<Integer> InactiveNodes = new HashSet<>(allMembers);

        for (Integer index : quorumMembers) {
            // We consider the Node only if we received a heart beat withing 100 secs
            if (activeNodes.contains(index)) {
                double finalscore = 0;
                // We check this as for shorter subepoch threshold like 2 we can have zer
                // historaical forgetting coefficient
                // System.out.println("Forgetting Coefficient Value" + forgettingCoefficient);
                if (forgettingCoefficient.size() > 0) {
                    // System.out.println("inside");
                    for (Integer previousSubEpoch : forgettingCoefficient.keySet()) {
                        // Previous SubEpoch * Forgetting Coefficent
                        // System.out.println("previousSubEpoch" + previousSubEpoch);
                        if (historicalEffectiveUtilitarianScore.containsKey(previousSubEpoch)) {
                            if (historicalEffectiveUtilitarianScore.get(previousSubEpoch).containsKey(index)) {
                                finalscore += (historicalEffectiveUtilitarianScore.get(previousSubEpoch).get(index)
                                        * forgettingCoefficient.get(previousSubEpoch));
                            }
                        }

                    }
                    finalscore += currentEffectiveUtilitarianScore.get(index);
                } else {
                    // System.out.println("outside");
                    finalscore += currentEffectiveUtilitarianScore.get(index);
                }
                // System.out.println("finalSortingScoreMap" + finalSortingScoreMap.toString());
                finalSortingScoreMap.put(index, finalscore);
            }
        }
        // System.out.println("FORMING QUORUM MESSAGE3" +
        // finalSortingScoreMap.toString());
        // Sort the Elments of finalSortingScoreMap based upon their effective score
        List<Entry<Integer, Double>> list = new ArrayList<>(finalSortingScoreMap.entrySet());
        // Sort by Natural Order Ascending to Descending
        list.sort(Entry.comparingByValue());
        // Reverse list from Descending to ascending
        Collections.reverse(list);
        Iterator<Entry<Integer, Double>> iteratorList = list.iterator();
        // Populate Sorted Map
        // Index Rank, Node Index
        Map<Integer, Integer> finalSortedMap = new HashMap<Integer, Integer>();
        int index = 1;
        while (iteratorList.hasNext()) {
            Entry<Integer, Double> element = iteratorList.next();
            finalSortedMap.put(index, element.getKey());
            index++;
        }
        // Classify the Scorers based on the sorted effective
        double doubleindex = Double.valueOf(index);
        double indexlimit = Math.ceil((doubleindex - 1) / CAPSEOBFTProperty.totalclassification);
        // System.out.println("indexlimit" + indexlimit);
        // i=0 -> Ideal Altruists i=1 -> altruistList i=2 -> fairAltruist i=3 ->
        // weakAltruist
        List<Integer> idealAltruistList = new ArrayList<>();
        List<Integer> altruistList = new ArrayList<>();
        List<Integer> fairAltruistList = new ArrayList<>();
        List<Integer> weakAltruistList = new ArrayList<>();
        List<Integer> currentblackListedMembers = new ArrayList<>();
        // Get Last 5 % mmembers who are the last rank
        double lastRankElementSize = Math
                .ceil(Double.valueOf(quorumMembers.size()) / CAPSEOBFTProperty.totalweakmemberpercent);
        // System.out.println("lastRankElementSize" + lastRankElementSize);
        // System.out.println("finalSortedMap" + finalSortedMap.toString());

        Map<Integer, List<Integer>> previousBlackListStorage = new ConcurrentHashMap<>();
        // Add the node Indexes to this list for identiying the remaining
        List<Integer> addedScorers = new ArrayList<Integer>();
        int startindex = 1;
        int endindex = (int) indexlimit;

        for (int i = 0; i <= 3; i++) {
            for (int j = startindex; j <= endindex; j++) {
                if (finalSortedMap.containsKey(j)) {

                    int nodeIndex = finalSortedMap.get(j);
                    if (i == 0) {
                        idealAltruistList.add(nodeIndex);
                        // System.out.println("j" + j);
                        // System.out.println("IdealAltruistList" + nodeIndex);
                    } else if (i == 1) {
                        altruistList.add(nodeIndex);
                        // System.out.println("j" + j);
                        // System.out.println("AltruistList" + nodeIndex);
                    } else if (i == 2) {
                        fairAltruistList.add(nodeIndex);
                        // System.out.println("j" + j);
                        // System.out.println("fairAltruistList" + nodeIndex);
                    } else if (i == 3) {
                        weakAltruistList.add(nodeIndex);
                        // System.out.println("j" + j);
                        // System.out.println("weakAltruistList" + nodeIndex);
                    }
                    addedScorers.add(nodeIndex);
                }
            }
            // Populate last 5% elements to the blacklist
            if (i == 3 && quorumMembers.size() >= CAPSEOBFTProperty.minquorumforblacklist) {
                if (currentSubEpoch > CAPSEOBFTProperty.blackListThreshold) {
                    for (double k = endindex - lastRankElementSize; k <= endindex; k++) {
                        if (finalSortedMap.containsKey((int) k)) {
                            int nodeIndex = finalSortedMap.get((int) k);
                            currentblackListedMembers.add(nodeIndex);
                        }

                    }
                }
            }
            startindex = startindex + (int) indexlimit;
            endindex = endindex + (int) indexlimit;

        }
        // Get the left over elements and add to the weak Altruits
        List<Integer> leftOverLastIndexes = new ArrayList<>(quorumMembers);
        leftOverLastIndexes.removeAll(addedScorers);
        weakAltruistList.addAll(leftOverLastIndexes);
        // System.out.println("currentblackListedMembers" + currentblackListedMembers);
        // System.out.println("currentSubEpoch" + currentSubEpoch);
        // System.out.println("qrumstorqge" +
        // quorumStorage.getQuorumMessageMap().toString());
        ////////////////////////// CLASSIFICATION
        ////////////////////////// COMPLETE/////////////////////////////////////////////////////////////////////
        // After classification check for black list elements for the last
        // blackListThreshold subepoch and remove only if it is still in the black list.
        // Eg for currentsubepoch=4, add to whitelist the black list of recent
        ////////////////////////// subepochs: 3,4
        // Remove if the elements is still in the black list of 2,1 and remove the
        ////////////////////////// elements

        // Make a copy
        List<Integer> effectiveblackListedMembers = new ArrayList<>(currentblackListedMembers);
        List<Integer> whiteListedMembers = new ArrayList<>();
        int totaleffectivemembers = 0;
        if (currentSubEpoch > CAPSEOBFTProperty.blackListThreshold) {
            for (int i = currentSubEpoch - 1; i <= currentSubEpoch; i++) {
                if (quorumStorage.getQuorumMessageMap().containsKey(i)) {
                    // System.out.println("quorumStorage.getQuorumMessageMap().get(i).getBlackListedMembers()"
                    // + quorumStorage.getQuorumMessageMap().get(i).getBlackListedMembers());
                    if (quorumStorage.getQuorumMessageMap().get(i).getBlackListedMembers().containsKey(i)) {
                        whiteListedMembers
                                .addAll(quorumStorage.getQuorumMessageMap().get(i).getBlackListedMembers().get(i));
                    }

                }

            }
            for (int j = currentSubEpoch - CAPSEOBFTProperty.blackListThreshold; j <= (currentSubEpoch
                    - 2); j++) {
                if (quorumStorage.getQuorumMessageMap().containsKey(j)
                        && quorumStorage.getQuorumMessageMap().get(j).getBlackListedMembers().containsKey(j)) {
                    effectiveblackListedMembers
                            .retainAll(quorumStorage.getQuorumMessageMap().get(j).getBlackListedMembers().get(j));
                }
            }
            // Converting to set to get Unique Elements
            Set<Integer> weakAltruistSet = new HashSet<>(weakAltruistList);
            weakAltruistSet.removeAll(effectiveblackListedMembers);
            // But we add white listed members by default
            weakAltruistSet.addAll(whiteListedMembers);

            weakAltruistList = new ArrayList<>(weakAltruistSet);
            // System.out.println("weakAltruistList" + weakAltruistList);
            // Get the last blacklisted lists of previous sub epoch
            if (quorumStorage.getQuorumMessageMap().containsKey(currentSubEpoch)) {
                if (quorumStorage.getQuorumMessageMap().get(currentSubEpoch).getBlackListedMembers().size() > 0) {
                    if (quorumStorage.getQuorumMessageMap().get(currentSubEpoch).getBlackListedMembers()
                            .containsKey(currentSubEpoch)) {
                        previousBlackListStorage = quorumStorage.getQuorumMessageMap().get(currentSubEpoch)
                                .getBlackListedMembers();
                    }
                }
            }
            // Update the black list with the current identified elements.
            previousBlackListStorage.put(proposedSubEpoch, currentblackListedMembers);

        }
        // We add the blacklist of pevious subepoch as to give a chance
        /*
         * int previoussubepoch = currentSubEpoch - 1;
         * if (quorumStorage.getQuorumMessageMap().containsKey(previoussubepoch)) {
         * weakAltruistList.addAll(quorumStorage.getQuorumMessageMap().get(
         * previoussubepoch).getBlackListedMembers());
         * }
         */
        Set<Integer> consolidatedEffectiveMembersSet = new HashSet<>();

        consolidatedEffectiveMembersSet.addAll(idealAltruistList);
        consolidatedEffectiveMembersSet.addAll(altruistList);
        consolidatedEffectiveMembersSet.addAll(fairAltruistList);
        consolidatedEffectiveMembersSet.addAll(weakAltruistList);

        totaleffectivemembers = consolidatedEffectiveMembersSet.size();

        List<Integer> consolidatedEffectiveMembers = new ArrayList<>(consolidatedEffectiveMembersSet);

        List<Integer> allQuorumMembersDuplicate = new ArrayList<>(quorumMembers);
        List<Integer> consolidatedEffectiveMembersDuplicate = new ArrayList<>(consolidatedEffectiveMembers);
        allQuorumMembersDuplicate.removeAll(consolidatedEffectiveMembersDuplicate);
        Set<Integer> currentSuspendedMembers = new HashSet<>(allQuorumMembersDuplicate);

        int epoch = this.blockchain.getFinalEpochCounter();
        Map<Integer, Integer> utilitarianClassification = formUtilitarianClassification(idealAltruistList, altruistList,
                fairAltruistList, weakAltruistList);
        Map<Integer, Integer> finalSortedMapWithoutBlackList = removeBlackList(finalSortedMap,
                consolidatedEffectiveMembers);

        Map<Integer, Integer> walletMapping = populateWalletMapping(finalSortedMapWithoutBlackList);
        QuorumMessage quorumMessage = null;
        if (totaleffectivemembers < CAPSEOBFTProperty.totalquorums * CAPSEOBFTProperty.minmembersinsidequoum) {

            quorumMessage = new QuorumMessage("QUORUM", epoch, proposedSubEpoch, Wallet.getNodeproperty(),
                    null, CryptoUtil.getPublicKeyString(Wallet.getNodeproperty()),
                    blockIndex, null, null,
                    null, null,
                    totaleffectivemembers, quorumMembers.size(), previousBlackListStorage, utilitarianClassification,
                    currentEffectiveUtilitarianScore, idealAltruistList, altruistList, fairAltruistList,
                    weakAltruistList,
                    finalSortedMapWithoutBlackList, proposedSubEpoch, type, walletMapping,
                    "SIMULATOR MEMBERS IS BELOW THE MINIMUM", InactiveNodes, currentSuspendedMembers);

        } else {
            quorumMessage = new QuorumMessage("QUORUM", epoch, proposedSubEpoch, Wallet.getNodeproperty(),
                    null, CryptoUtil.getPublicKeyString(Wallet.getNodeproperty()),
                    blockIndex, null, null,
                    null, null,
                    totaleffectivemembers, quorumMembers.size(), previousBlackListStorage, utilitarianClassification,
                    currentEffectiveUtilitarianScore, idealAltruistList, altruistList, fairAltruistList,
                    weakAltruistList,
                    finalSortedMapWithoutBlackList, proposedSubEpoch, type, walletMapping,
                    "NORMAL QUORUM FORMATION", InactiveNodes, currentSuspendedMembers);
        }
        // Update with the Actual Quorum Segregation

        updateQuorumMessage(quorumMessage);
        // System.out.println("END QURM" + quorumMessage.toString());
        return quorumMessage;

    }

    /**
     * Method to populate the wallet mapping
     * Key: Node Intermediary Index for the particular sub epoch
     * Value: Node Wallet Index
     * 
     * @param sortedMap
     * @return
     */
    public Map<Integer, Integer> populateWalletMapping(Map<Integer, Integer> sortedMap) {
        Map<Integer, Integer> newWalletMapping = new HashMap<>();
        int index = 0;
        for (Entry<Integer, Integer> sortedMapEntry : sortedMap.entrySet()) {
            newWalletMapping.put(index, sortedMapEntry.getValue());
            index++;
        }

        return newWalletMapping;

    }

    // Method to Update the Actual Quorum formation
    public void updateQuorumMessage(QuorumMessage quorumMessage) {
        // NodeIndex, Quorum Id: Quorum Comittee Index,
        Map<Integer, Integer> quorumIdMapping = new HashMap<Integer, Integer>();
        // Quorum Id: Quorum Comittee Index, List of Node Indexes belonging to the
        // particular Quorum Id
        Map<Integer, List<Integer>> quorumIdInverseMapping = new HashMap<Integer, List<Integer>>();
        // NodeIndex,Quorum Index: Index of a Node within a Particular Quorum
        Map<Integer, Integer> quorumIndexMapping = new HashMap<Integer, Integer>();
        // Quorum Id: Quorum Comittee Index, Total Members inside a particular Quorum
        Map<Integer, Integer> quorumIdMemberCount = new HashMap<Integer, Integer>();

        Map<Integer, QuorumOriginal> originalNodeMap = new HashMap<>();

        // Steps:
        // Get Total Effective Members
        // Get Total Quorum and fill them accroding to classification order
        int totalEffectiveMembers = quorumMessage.getTotalEffectiveMembers();
        int totalQuorumsNeeded = CAPSEOBFTProperty.getTotalQuorums();
        // Targeted Member Size Per Quorum
        int memberperQuorumsize = totalEffectiveMembers / totalQuorumsNeeded;

        // Rank, Node Index
        Map<Integer, Integer> sortedMapWithoutBlackList = quorumMessage.getSortedFinalMap();
        int quorumId = 0;
        int memberCounter = 0;
        for (int m = 1; m <= sortedMapWithoutBlackList.size(); m++) {
            if (quorumId < totalQuorumsNeeded) {
                if (sortedMapWithoutBlackList.containsKey(m)) {
                    int nodeIndex = sortedMapWithoutBlackList.get(m);
                    // Filling quorumIdMapping
                    quorumIdMapping.put(nodeIndex, quorumId);
                    // Filing quorumIdInverseMapping
                    if (quorumIdInverseMapping.containsKey(quorumId)) {
                        quorumIdInverseMapping.get(quorumId).add(nodeIndex);

                    } else {
                        List<Integer> quorumMembers = new ArrayList<Integer>();
                        quorumMembers.add(nodeIndex);
                        quorumIdInverseMapping.put(quorumId, quorumMembers);
                    }
                    memberCounter++;
                    // Increment the quorum Id if we achieve the limit/ target of each Quorum
                    if (memberCounter % memberperQuorumsize == 0) {
                        quorumId += 1;
                    }
                }
            } else {
                // Clearance Last Indexes
                int lastQuorumIndex = quorumId - 1;
                // Add the rest of elements to the last Quorum
                // System.out.println("listsortedMapWithoutBlackList" +
                // sortedMapWithoutBlackList.toString());
                // System.out.println("sortedMapWithoutBlackList" + m);
                if (sortedMapWithoutBlackList.containsKey(m)) {
                    int nodeIndex = sortedMapWithoutBlackList.get(m);
                    // Filling quorumIdMapping
                    quorumIdMapping.put(nodeIndex, lastQuorumIndex);
                    // Filing quorumIdInverseMapping
                    if (quorumIdInverseMapping.containsKey(lastQuorumIndex)) {
                        quorumIdInverseMapping.get(lastQuorumIndex).add(nodeIndex);

                    } else {
                        List<Integer> quorumMembers = new ArrayList<Integer>();
                        quorumMembers.add(nodeIndex);
                        quorumIdInverseMapping.put(lastQuorumIndex, quorumMembers);
                    }
                }

            }
        }

        // Populate quorumIndexMapping & quorumIdMemberCount
        populateQuorumIndexMappingandMemberCountandOriginalNode(quorumIndexMapping, quorumIdMemberCount,
                quorumIdInverseMapping,
                originalNodeMap);
        // Final Update
        quorumMessage.setQuorumIdInverseMapping(quorumIdInverseMapping);
        quorumMessage.setQuorumIdMappping(quorumIdMapping);
        quorumMessage.setQuorumIndexMappping(quorumIndexMapping);
        quorumMessage.setQuorumIdMemberCount(quorumIdMemberCount);
        quorumMessage.setOriginalNodeMap(originalNodeMap);
        String signature = wallet.signData(CryptoUtil.getHash(String
                .valueOf(quorumMessage.getNodeIndex() + quorumMessage.getSubEpoch())));
        quorumMessage.setMessagesignature(signature);
    }

    public void populateQuorumIndexMappingandMemberCountandOriginalNode(Map<Integer, Integer> quorumIndexMapping,
            Map<Integer, Integer> quorumIdMemberCount,
            Map<Integer, List<Integer>> quorumIdInverseMapping, Map<Integer, QuorumOriginal> originalNodeMap) {
        for (Integer quorumId : quorumIdInverseMapping.keySet()) {
            List<Integer> quorumMembers = quorumIdInverseMapping.get(quorumId);
            for (int i = 0; i < quorumMembers.size(); i++) {
                // NodeIndex, Position of Node within Quorum
                quorumIndexMapping.put(quorumMembers.get(i), i);
                // Populate Original Node Map
                if (originalNodeMap.containsKey(quorumId)) {
                    QuorumOriginal quorumOriginal = originalNodeMap.get(quorumId);
                    quorumOriginal.addOriginalMapping(i, quorumMembers.get(i));
                } else {
                    QuorumOriginal quorumOriginal = new QuorumOriginal();
                    quorumOriginal.addOriginalMapping(i, quorumMembers.get(i));
                    originalNodeMap.put(quorumId, quorumOriginal);
                }
            }
            // Quorum Id, Total Members inside Quorum
            quorumIdMemberCount.put(quorumId, quorumMembers.size());
        }
    }

    public Map<Integer, Integer> formUtilitarianClassification(List<Integer> idealAltruistList,
            List<Integer> altruistList,
            List<Integer> fairAltruistList,
            List<Integer> weakAltruistList) {
        Map<Integer, Integer> utilitarianClassification = new HashMap<Integer, Integer>();
        // Classify the Utilitarian
        // 1 --> Ideal Utilitarian
        // 2 --> Utilitarian
        // 3 --> Fair Altruists
        // 4 --> Weak Altruists
        // Node Index , Utilitarian Type
        for (int i = 1; i < 5; i++) {
            if (i == 1) {
                for (Integer idealAltruist : idealAltruistList) {
                    utilitarianClassification.put(idealAltruist, 1);
                }
            } else if (i == 2) {
                for (Integer altruist : altruistList) {
                    utilitarianClassification.put(altruist, 2);
                }

            } else if (i == 3) {
                for (Integer fairAltruist : fairAltruistList) {
                    utilitarianClassification.put(fairAltruist, 3);
                }
            } else if (i == 4) {
                for (Integer weakAltruist : weakAltruistList) {
                    utilitarianClassification.put(weakAltruist, 4);
                }
            }
        }

        return utilitarianClassification;

    }

    /**
     * Method to remove the black list elements from the Sorted Map
     * 
     * @param finalSortedMap
     * @param blackListedMembers
     * @return
     */
    public Map<Integer, Integer> removeBlackList(Map<Integer, Integer> finalSortedMap,
            List<Integer> consolidatedAltruistList) {
        Map<Integer, Integer> finalisedMap = new ConcurrentHashMap<>();
        for (Entry<Integer, Integer> finalSortedMapEntry : finalSortedMap.entrySet()) {
            if (consolidatedAltruistList.contains(finalSortedMapEntry.getValue())) {
                finalisedMap.put(finalSortedMapEntry.getKey(), finalSortedMapEntry.getValue());
            }
        }
        return finalisedMap;
    }

    public UtilitarianCalculator(Blockchain blockchain, HeartBeatStorage heartBeatStorage, QuorumStorage quorumStorage,
            UtilitarianScoreStorage utilitarianScoreStorage, Wallet wallet) {
        this.blockchain = blockchain;
        this.heartBeatStorage = heartBeatStorage;
        this.quorumStorage = quorumStorage;
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.wallet = wallet;
    }
}