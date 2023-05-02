package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.api.Simulator_result;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.PartialBlock;
import org.renaultleat.chain.ProposerMessage;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
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
 */

public class CAPSEOBFTDemFullBlockManager extends Thread {

    public BlockingQueue<JSONObject> messageBlockingQueue;

    public Blockchain blockchain;

    public Wallet wallet;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public TransactionPool transactionPool;

    public BlockPool blockPool;

    public Validator validator;

    public NonValidator nonValidator;

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    public Synchronizer synchronizer;

    public Simulator_result simulator_result;

    public UtilitarianCalculator utilitarianCalculator;

    public HeartBeatStorage heartBeatStorage;

    public UtilitarianScoreStorage utilitarianScoreStorage;

    public CAPSEOBFTDemConsensusRoundChangeHandler capSEOBFTDemConsensusRoundChangeHandler;

    public QuorumStorage quorumStorage;

    public CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void broadCastBlock(Block block, UtilitarianBlockScore utilitarianBlockScore)
            throws IOException {
        if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("nodeindex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("blockhash", block.getBlockHash());
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("type", "BLOCK");
            jsonObject.put("QuromId", String.valueOf(QuorumStorage.getQuorumId()));
            jsonObject.put("QuorumIndex", String.valueOf(QuorumStorage.getQuorumIndex()));
            // Pipeline Round is equal to Major Block Number
            jsonObject.put("round", String.valueOf(block.getRoundNo()));
            jsonObject.put("blockheight", String.valueOf(block.getBlocknumber()));
            Gson utilitarianGson = new Gson();
            String utilitariandata = utilitarianGson.toJson(utilitarianBlockScore);
            jsonObject.put("utilitarianBlockScore", utilitariandata);
            Gson gson = new Gson();
            String data = gson.toJson(block);
            jsonObject.put("data", data);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        // int printindex = 0;
        // int jinprintindex = 0;
        // int finprintindex = 0;
        // int primprintindex = 0;
        // int secprintindex = 0;

        // int abcprintindex = 0;
        // int defprintindex = 0;
        // int ghiprintindex = 0;
        while (true) {

            try {
                if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
                    int nextIndex = this.blockchain.getChain().lastKey() + 1;
                    // if (finprintindex != nextIndex) {
                    // // System.out.println("nextIndexD" + nextIndex);
                    // finprintindex = nextIndex;

                    // }

                    /*
                     * System.out.println("nextIndex" + nextIndex);
                     * System.out.println(
                     * "nextIndexcheck" + this.blockchain.getEphemeralChain().toString());
                     * System.out.println(
                     * "nextIndexcheck1" + this.blockchain.getChain().containsKey(nextIndex - 1));
                     * System.out.println(
                     * "nextIndexcheck2" + this.blockchain.getChain().get(nextIndex -
                     * 1).getBlockHash());
                     * System.out.println(
                     * "nextIndexcheck3" + this.blockchain.getChain().get(nextIndex - 1)
                     * .getTemporalHashingState());
                     */
                    if (this.blockchain.getChain().containsKey(nextIndex - 1)
                            && !this.blockchain.getChain().containsKey(nextIndex)) {
                        // System.out.println("nextIndex0" + nextIndex);
                        // if (jinprintindex != nextIndex) {
                        // // System.out.println("nextIndexE" + nextIndex);
                        // jinprintindex = nextIndex;

                        // }
                        if (nextIndex == 1 || ((this.blockchain.getChain().containsKey(nextIndex - 1))
                                && (this.blockchain.getChain().get(nextIndex - 1).getBlockHash() != null))) {
                            // if (primprintindex != nextIndex) {
                            // // System.out.println("nextIndexF" + nextIndex);
                            // primprintindex = nextIndex;

                            // }
                            if (this.blockchain.getEphemeralChain().containsKey(
                                    nextIndex)) {
                                // if (abcprintindex != nextIndex) {
                                // // System.out.println("nextIndexabc" + nextIndex);
                                // abcprintindex = nextIndex;

                                // }
                                if (this.blockchain.getEphemeralChain().get(
                                        nextIndex) != null) {
                                    if (this.blockchain.getEphemeralChain().get(nextIndex)
                                            .getPartialBlockMap() != null) {
                                        // if (defprintindex != nextIndex) {
                                        // // System.out.println(
                                        // // "nextIndexdef" + this.blockchain.getEphemeralChain().get(nextIndex)
                                        // // .getPartialBlockMap());
                                        // defprintindex = nextIndex;

                                        // }
                                        if (this.blockchain.getEphemeralChain().get(nextIndex).getPartialBlockMap()
                                                .size() == CAPSEOBFTProperty
                                                        .getTotalQuorums()) {
                                            // if (secprintindex != nextIndex) {
                                            // System.out.println("nextIndexG" + nextIndex);
                                            // secprintindex = nextIndex;

                                            // }
                                            ProposerMessage fullBlockProposerMessage = this.blockchain
                                                    .getFullBlockProposer(
                                                            nextIndex);
                                            int fullBlockProposerIndex = fullBlockProposerMessage.getNodeIndex();
                                            int subEpoch = this.blockchain.getFinalSubEpochCounter();
                                            // if (printindex != nextIndex) {
                                            // System.out.println("nextIndex1" + nextIndex);
                                            // System.out.println("nextIndexSubEpoh" + subEpoch);
                                            // System.out.println("NodeIndex1" +
                                            // fullBlockProposerMessage.getNodeIndex());
                                            // printindex = nextIndex;
                                            // }

                                            if (quorumStorage.getQuorumMessageMap().containsKey(subEpoch)) {
                                                QuorumMessage quorumMessageforSubEpoch = quorumStorage
                                                        .getQuorumMessageMap()
                                                        .get(subEpoch);

                                                if (quorumMessageforSubEpoch
                                                        .getWalletMapping().containsKey(fullBlockProposerIndex)) {

                                                    if (quorumMessageforSubEpoch
                                                            .getWalletMapping()
                                                            .get(fullBlockProposerIndex) == wallet.getNodeproperty()) {
                                                        // System.out.println("nextIndex2" + nextIndex);
                                                        Block ephemeralBlock = this.blockchain.getEphemeralChain()
                                                                .get(nextIndex);
                                                        Block block = null;
                                                        // Update the main chain with block
                                                        if (NodeProperty.getnodeBehavior() == 2) {
                                                            // Malicious Behaviour
                                                            block = this.blockchain.finalisedMaliciousFullBlock(wallet,
                                                                    nextIndex,
                                                                    ephemeralBlock.getSubEpoch(),
                                                                    fullBlockProposerMessage.getChainIndex(),
                                                                    fullBlockProposerIndex,
                                                                    CAPSEOBFTProperty.normalProposal);
                                                        } else {
                                                            // Good Behaviour
                                                            block = this.blockchain.finalisedFullBlock(wallet,
                                                                    nextIndex,
                                                                    ephemeralBlock.getSubEpoch(),
                                                                    fullBlockProposerMessage.getChainIndex(),
                                                                    fullBlockProposerIndex,
                                                                    CAPSEOBFTProperty.normalProposal);

                                                        }

                                                        // Update the ephemeral chain with block
                                                        this.blockchain.updatefinalisedEphemeralBlock(block);
                                                        // System.out.println(
                                                        // "full block broadcast"
                                                        // + block.getBlocknumber());
                                                        // Calculate Utilitarian Block Score
                                                        UtilitarianBlockScore utilitarianBlockScore = this.utilitarianCalculator
                                                                .getUtilitarianBlockScore(block, blockchain,
                                                                        heartBeatStorage);

                                                        // Broadcast BLOCK message
                                                        try {
                                                            this.broadCastBlock(block, utilitarianBlockScore);
                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }
                                                        // Update local storage
                                                        this.capSEOBFTDemMessagePool.addBlockMessage(block);
                                                        // Update Local Utilitarian Storage
                                                        this.utilitarianScoreStorage
                                                                .updateAtruismBlockStorage(utilitarianBlockScore);
                                                        // System.out.println("started full block");
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Full Block Manager Exeption" + e.toString());
                e.printStackTrace();
            }
        }

    }

    public CAPSEOBFTDemFullBlockManager(BlockingQueue<JSONObject> inMessageBlockingQueue, Blockchain blockChain,
            TransactionPool transactionPool,
            BlockPool blockPool, Wallet wallet, Validator validator, NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Synchronizer synchronizer, Simulator_result simulator_result,
            CAPSEOBFTDemConsensusRoundChangeHandler capSEOBFTDemConsensusRoundChangeHandler,
            UtilitarianCalculator utilitarianCalculator, HeartBeatStorage heartBeatStorage,
            UtilitarianScoreStorage utilitarianScoreStorage, QuorumStorage quorumStorage,
            CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager) {
        this.messageBlockingQueue = inMessageBlockingQueue;
        this.transactionPool = transactionPool;
        this.blockchain = blockChain;
        this.blockPool = blockPool;
        this.wallet = wallet;
        this.validator = validator;
        this.nonValidator = nonValidator;
        int total = Integer.valueOf(NodeProperty.totalnodes);
        int totalValidators = Integer.valueOf(NodeProperty.validators);
        this.nodeCommunicator = nodeCommunicator;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        // (2N/3)+1
        // this.minapprovals = 2 * (totalValidators / 3) + 1;
        this.currentuser = currentuser;
        this.synchronizer = synchronizer;
        this.simulator_result = simulator_result;
        this.capSEOBFTDemConsensusRoundChangeHandler = capSEOBFTDemConsensusRoundChangeHandler;
        this.utilitarianCalculator = utilitarianCalculator;
        this.heartBeatStorage = heartBeatStorage;
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.quorumStorage = quorumStorage;
        this.capSEOBFTDemQuorumManager = capSEOBFTDemQuorumManager;
    }
}