package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
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
import org.renaultleat.chain.ProposerMessage;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

/**
 * This Handler is used in case of Multi threaded call from P2PHandler or single
 * threaded we call PBFTMessageHandler
 * 
 */
public class CAPSEOBFTDemFullBlockFullfiller extends AbstractScheduledService {

    public volatile AtomicInteger blockhainHeightCounter = new AtomicInteger(1);

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    // To increment Round Counter
    public Blockchain blockChain;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public UtilitarianScoreStorage utilitarianScoreStorage;

    public HeartBeatStorage heartBeatStorage;

    public QuorumStorage quorumStorage;

    public UtilitarianCalculator utilitarianCalculator;

    public Simulator_result simulator_result;

    public Wallet wallet;

    public Timer timer;

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
                int nextIndex = this.blockChain.getChain().lastKey() + 1;

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
                if (this.blockChain.getChain().containsKey(nextIndex - 1)
                        && !this.blockChain.getChain().containsKey(nextIndex)) {
                    // System.out.println("nextIndex0" + nextIndex);

                    if (nextIndex == 1 || ((this.blockChain.getChain().containsKey(nextIndex - 1))
                            && (this.blockChain.getChain().get(nextIndex - 1).getBlockHash() != null))) {

                        if (this.blockChain.getEphemeralChain().containsKey(
                                nextIndex)) {

                            if (this.blockChain.getEphemeralChain().get(nextIndex)
                                    .getPartialBlockMap() != null) {

                                if (this.blockChain.getEphemeralChain().get(nextIndex).getPartialBlockMap()
                                        .size() >= CAPSEOBFTProperty
                                                .getTotalQuorums()) {

                                    ProposerMessage fullBlockFulFillerMessage = this.blockChain
                                            .getFullBlockFulFiller(
                                                    nextIndex);
                                    int fullBlockFillerIndex = fullBlockFulFillerMessage.getNodeIndex();
                                    int subEpoch = this.blockChain.getFinalSubEpochCounter();
                                    if (printindex != nextIndex) {
                                        // System.out.println("FULLFILLERnextIndex1" + nextIndex);
                                        // System.out.println("FULLFILLERnextIndexSubEpoh" + subEpoch);
                                        // System.out.println(
                                        // "FULLFILLERNodeIndex1" + fullBlockFulFillerMessage.getNodeIndex());
                                        // System.out.println("FULLFILLERnextIndex4" +
                                        // quorumStorage.getQuorumMessageMap().get(subEpoch)
                                        // .getWalletMapping()
                                        // .get(fullBlockFillerIndex));
                                        printindex = nextIndex;
                                    }

                                    if (quorumStorage.getQuorumMessageMap().containsKey(subEpoch)) {
                                        QuorumMessage quorumMessageforSubEpoch = this.quorumStorage
                                                .getQuorumMessageMap()
                                                .get(subEpoch);
                                        if (quorumMessageforSubEpoch
                                                .getWalletMapping().containsKey(fullBlockFillerIndex)) {

                                            if (quorumMessageforSubEpoch
                                                    .getWalletMapping()
                                                    .get(fullBlockFillerIndex) == wallet.getNodeproperty()) {
                                                // System.out.println("FULLFILLERnextIndex2" + nextIndex);
                                                Block ephemeralBlock = this.blockChain.getEphemeralChain()
                                                        .get(nextIndex);
                                                // Update the main chain with block
                                                Block block = this.blockChain.finalisedFullBlock(wallet, nextIndex,
                                                        ephemeralBlock.getSubEpoch(),
                                                        fullBlockFulFillerMessage.getChainIndex(),
                                                        fullBlockFillerIndex, CAPSEOBFTProperty.normalFulfillment);
                                                // Update the ephemeral chain with block
                                                this.blockChain.updatefinalisedEphemeralBlock(block);
                                                // System.out.println(
                                                // "FULLFILLER full block broadcast"
                                                // + block.getBlocknumber());
                                                // Calculate Utilitarian Block Score
                                                UtilitarianBlockScore utilitarianBlockScore = this.utilitarianCalculator
                                                        .getUtilitarianBlockScore(block, blockChain,
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
        } catch (Exception e) {
            System.out.println("Full Block Filler Exception" + e.toString());
            e.printStackTrace();

        }

    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, NodeProperty.getFullBlockFullfillment(), TimeUnit.MILLISECONDS);
    }

    public CAPSEOBFTDemFullBlockFullfiller(
            Blockchain blockChain,
            Wallet wallet,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Simulator_result simulator_result,
            UtilitarianCalculator utilitarianCalculator, HeartBeatStorage heartBeatStorage,
            UtilitarianScoreStorage utilitarianScoreStorage, QuorumStorage quorumStorage) {
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