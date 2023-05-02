package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.AbstractScheduledService;
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
public class CAPSEOBFTDemPartialBlockFullfiller extends AbstractScheduledService {

    public volatile AtomicInteger blockhainHeightCounter = new AtomicInteger(1);

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    // To increment Round Counter
    public Blockchain blockChain;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public TransactionPool transactionPool;

    public UtilitarianScoreStorage utilitarianScoreStorage;

    public HeartBeatStorage heartBeatStorage;

    public QuorumStorage quorumStorage;

    public UtilitarianCalculator utilitarianCalculator;

    public Simulator_result simulator_result;

    public Wallet wallet;

    public Timer timer;

    public void broadCastPropose(PartialBlock partialBlock)
            throws IOException {
        if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("nodeindex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("partialblockhash", partialBlock.getPartialBlockHash());
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("type", "PROPOSE");
            jsonObject.put("QuromId", String.valueOf(QuorumStorage.getQuorumId()));
            jsonObject.put("QuorumIndex", String.valueOf(QuorumStorage.getQuorumIndex()));
            // Pipeline Round is equal to Major Block Number
            jsonObject.put("round", String.valueOf(partialBlock.getMajorBlocknumber()));
            jsonObject.put("blockheight", String.valueOf(partialBlock.getBlockHeight()));
            Gson gson = new Gson();
            String data = gson.toJson(partialBlock);
            jsonObject.put("data", data);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    public void broadCastCommit(PartialBlock partialBlock)
            throws IOException {
        if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("nodeindex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("partialblockhash", partialBlock.getPartialBlockHash());
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("type", "COMMIT");
            jsonObject.put("QuromId", String.valueOf(QuorumStorage.getQuorumId()));
            jsonObject.put("QuorumIndex", String.valueOf(QuorumStorage.getQuorumIndex()));
            // Pipeline Round is equal to Major Block Number
            jsonObject.put("round", String.valueOf(partialBlock.getMajorBlocknumber()));
            jsonObject.put("blockheight", String.valueOf(partialBlock.getBlockHeight()));
            Gson gson = new Gson();
            String data = gson.toJson(partialBlock);
            jsonObject.put("data", data);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

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

    public void scheduleTimer() {
        try {
            this.startAsync();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void rescheduleTimer() {
        try {
            this.shutDown();
            this.startUp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void runOneIteration() throws Exception {
        // Run only if the counter has not progressed
        // if (this.blockhainHeightCounter.get() != 1) {
        if (this.blockhainHeightCounter.get() == this.blockChain.getChain().lastKey()) {
            task();
        } else {
            this.blockhainHeightCounter.set(this.blockChain.getChain().lastKey());
        }
        // }
    }

    public void task() {
        // TODO Auto-generated method stub
        int printindex = 0;

        try {
            if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
                int partialBlockFulfillCounter = this.blockChain.getChain().lastKey() + 1;
                // Get the available partial block from the epheremeal chain
                // If the quorum Id matches the requirement minor index then prpoose the partial
                // block fulfillment
                int finalSubEpoch = this.blockChain.getFinalSubEpochCounter();

                Block ephemeralBlock;
                int quorumId = QuorumStorage.getQuorumId();
                if (this.blockChain.getEphemeralChain().containsKey(partialBlockFulfillCounter)) {
                    ephemeralBlock = this.blockChain.getEphemeralChain().get(partialBlockFulfillCounter);
                    // check if the quorum Id is filled for the epheral block
                    // If yes then fulfill or leave it
                    if (!ephemeralBlock.getPartialBlockMap().containsKey(quorumId)) {
                        if (this.quorumStorage.getQuorumMessageMap().containsKey(finalSubEpoch)) {
                            // System.out.println("QUOUMStatus" +
                            // this.quorumStorage.getQuorumMessageMap().toString());
                            if (this.transactionPool.getTransactionRoundStatus().containsKey(partialBlockFulfillCounter)
                                    && this.transactionPool.getTransactionRoundStatus()
                                            .get(partialBlockFulfillCounter)) {
                                // System.out.println(
                                // "Status" + this.transactionPool.getTransactionRoundStatus().toString());
                                // System.out.println("Patial block Fulfiller" + "Major:" +
                                // partialBlockFulfillCounter
                                // + "Minor:" + quorumId);
                                // System.out.println("QID PATIAL" + QuorumStorage.getQuorumId());
                                // Check if a validator
                                ProposerMessage partialBlockFulfillMessage = this.blockChain.getPartialBlockFulfiller(
                                        partialBlockFulfillCounter,
                                        QuorumStorage.getQuorumId());
                                int partialBlockProposer = partialBlockFulfillMessage.getNodeIndex();

                                ProposerMessage partialBlockProposeMessage = this.blockChain.getPartialBlockProposer(
                                        partialBlockFulfillCounter,
                                        QuorumStorage.getQuorumId());
                                int rivalPartialBlockProposer = partialBlockProposeMessage.getNodeIndex();

                                // System.out.println("partialBlockProposer:" + partialBlockProposer);
                                // System.out.println("rivalPartialBlockProposer:" + rivalPartialBlockProposer);
                                // System.out.println("QuorumStorage:" + QuorumStorage
                                // .getQuorumIndex());

                                if (QuorumStorage.quorumInitialised) {

                                    if ((partialBlockProposer == QuorumStorage.getQuorumIndex())
                                            && NodeProperty.isValidator()) {

                                        int proposeCalculatedatindex = 0;
                                        if (partialBlockProposer == QuorumStorage.getQuorumIndex()) {
                                            proposeCalculatedatindex = partialBlockFulfillMessage.getChainIndex();
                                        }
                                        // this.synchronizer.consensusincourse = true;
                                        // this.synchronizer.thresholdReached = false;
                                        List<Transaction> transactions = this.transactionPool
                                                .getTransactions(partialBlockFulfillCounter);
                                        // Can be null as well
                                        if (transactions == null) {
                                            transactions = new ArrayList<Transaction>();
                                        }

                                        CopyOnWriteArrayList<Transaction> tempTransactionList = new CopyOnWriteArrayList<Transaction>(
                                                transactions);
                                        Block block = null;
                                        // Existing block
                                        if (this.blockChain.getEphemeralChain()
                                                .containsKey(partialBlockFulfillCounter)) {
                                            block = this.blockChain.getEphemeralChain().get(partialBlockFulfillCounter);
                                        } else {
                                            // Create a new Block and Add to Ephemeral chain
                                            block = this.blockChain.createBlocknandAddtoEphemeral(wallet,
                                                    partialBlockFulfillCounter);
                                        }
                                        if (block == null) {
                                            System.out.println("BLOCK IS NULL");
                                        }
                                        PartialBlock partialBlock = null;
                                        if (partialBlockProposer == QuorumStorage.getQuorumIndex()) {
                                            // You are the partial proposer
                                            partialBlock = this.blockChain.createPartialBlock(block, this.wallet,
                                                    tempTransactionList,
                                                    partialBlockFulfillCounter, partialBlockFulfillCounter,
                                                    partialBlockProposer, rivalPartialBlockProposer,
                                                    proposeCalculatedatindex,
                                                    finalSubEpoch, CAPSEOBFTProperty.normalPartialFulfillment);
                                        }

                                        // Add the partial block created
                                        block.addPartialBlock(partialBlock, this.wallet);
                                        this.blockChain.getEphemeralChain().put(block.getBlocknumber(), block);

                                        // this.blockPool.addBlock(block);
                                        // Clear transaction Pool as we formed a block with those transactions as this
                                        // for Proposer Node
                                        // System.out.println("ROPOSE PATIAL" + partialBlock.toString());
                                        try {
                                            this.broadCastPropose(partialBlock);
                                            this.broadCastCommit(partialBlock);

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("PartialBlockFulfillerException" + e.toString());
            e.printStackTrace();

        }

    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, NodeProperty.getPartialBlockFullfillment(), TimeUnit.MILLISECONDS);
    }

    public CAPSEOBFTDemPartialBlockFullfiller(TransactionPool transactionPool,
            Blockchain blockChain,
            Wallet wallet,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Simulator_result simulator_result,
            UtilitarianCalculator utilitarianCalculator, HeartBeatStorage heartBeatStorage,
            UtilitarianScoreStorage utilitarianScoreStorage, QuorumStorage quorumStorage) {
        this.transactionPool = transactionPool;
        this.nodeCommunicator = nodeCommunicator;
        this.currentuser = currentuser;
        this.blockChain = blockChain;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.wallet = wallet;
        this.utilitarianCalculator = utilitarianCalculator;
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.heartBeatStorage = heartBeatStorage;
        this.quorumStorage = quorumStorage;
        this.simulator_result = simulator_result;
    }

}