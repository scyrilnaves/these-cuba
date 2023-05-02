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
 * Class to append Block reeived by Non Validators from Validators
 * 
 */

public class CAPSEOBFTDemFinaliseHandler extends Thread {

    public BlockingQueue<JSONObject> finaliseBlockingQueue;

    public Blockchain blockchain;

    public Wallet wallet;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public TransactionPool transactionPool;

    public BlockPool blockPool;

    public Validator validator;

    public NonValidator nonValidator;

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    private int minapprovals;

    public Synchronizer synchronizer;

    public Simulator_result simulator_result;

    public UtilitarianCalculator utilitarianCalculator;

    public HeartBeatStorage heartBeatStorage;

    public UtilitarianScoreStorage utilitarianScoreStorage;

    public CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void rebroadCastFinalise(JSONObject jsonObject)
            throws IOException {
        if (NodeProperty.isValidator()) {
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    public void broadCastBlock(Block block, UtilitarianBlockScore utilitarianBlockScore)
            throws IOException {
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
        // System.out.println("bloc" + block.toString());
        // System.out.println("altruis" + utilitarianBlockScore.toString());
        Gson utilitarianGson = new Gson();
        String utilitariandata = utilitarianGson.toJson(utilitarianBlockScore);
        jsonObject.put("utilitarianBlockScore", utilitariandata);
        Gson gson = new Gson();
        String data = gson.toJson(block);
        // System.out.println("CHECK" + block.toString());
        jsonObject.put("data", data);
        this.nodeCommunicator.sendMessage(jsonObject.toString());
    }

    // Quorum Id and Validator Check
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            {

                if (!this.finaliseBlockingQueue.isEmpty()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = this.finaliseBlockingQueue.take();
                        String messageType = jsonObject.getString("type");

                        if (messageType.equals("FINALISE")) {
                            Gson gson = new Gson();
                            Type partialBlockObject = new TypeToken<PartialBlock>() {
                            }.getType();
                            PartialBlock inPartialblock = gson.fromJson(jsonObject.getString("data"),
                                    partialBlockObject);
                            // Check if we are ready to validate a Partial Block
                            if (this.blockchain.isReadyforPartialBlockValidity(inPartialblock)) {
                                if (inPartialblock.getValidity()) {
                                    if (inPartialblock.isFinalised()
                                            && this.blockchain.isValidPartialBlock(inPartialblock)
                                            && !this.blockchain.finalisedPartialBlockFilled(inPartialblock)
                                            && !this.capSEOBFTDemMessagePool.existingFinaliseMessage(inPartialblock)) {

                                        this.capSEOBFTDemMessagePool.addFinaliseMessage(inPartialblock);
                                        // System.out.println("FINALISEhandler" + "major:"
                                        // + inPartialblock.getMajorBlocknumber() + "minor:"
                                        // + inPartialblock.getMinorBlocknumber());
                                        try {
                                            this.rebroadCastFinalise(jsonObject);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        // If the received partial block is the last and you're the FULL BLOCK proposer
                                        // Prepare and Broadcast the BLOCK
                                        this.blockchain.addFinalisedPartialBlock(this.wallet,
                                                inPartialblock);

                                    }
                                } else {
                                    this.utilitarianScoreStorage.incrementPeerMaliciousScore(
                                            inPartialblock.getProposerNodeIndex(),
                                            inPartialblock.getSubEpoch(),
                                            CAPSEOBFTProperty.maliciousscore);
                                    // We rebroadcast any way to propagate the malicious
                                    // activity to be know by all
                                    this.rebroadCastFinalise(jsonObject);
                                }
                            } else {
                                if (inPartialblock.getMajorBlocknumber() > this.blockchain.getChain().lastKey()) {
                                    try {
                                        this.rebroadCastFinalise(jsonObject);
                                        this.finaliseBlockingQueue.add(jsonObject);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("Finalise Handler Exception" + e.toString());
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    public CAPSEOBFTDemFinaliseHandler(BlockingQueue<JSONObject> finaliseBlockingQueue, Blockchain blockChain,
            TransactionPool transactionPool,
            BlockPool blockPool, Wallet wallet, Validator validator, NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Synchronizer synchronizer, Simulator_result simulator_result,
            UtilitarianCalculator utilitarianCalculator, HeartBeatStorage heartBeatStorage,
            UtilitarianScoreStorage utilitarianScoreStorage, CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager) {
        this.finaliseBlockingQueue = finaliseBlockingQueue;
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
        this.minapprovals = 2 * (totalValidators / 3) + 1;
        this.currentuser = currentuser;
        this.synchronizer = synchronizer;
        this.simulator_result = simulator_result;
        this.heartBeatStorage = heartBeatStorage;
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.utilitarianCalculator = utilitarianCalculator;
        this.capSEOBFTDemQuorumManager = capSEOBFTDemQuorumManager;
    }
}