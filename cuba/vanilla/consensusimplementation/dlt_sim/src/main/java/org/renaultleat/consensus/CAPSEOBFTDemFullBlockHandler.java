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

public class CAPSEOBFTDemFullBlockHandler extends Thread {

    public BlockingQueue<JSONObject> fullBlockBlockingQueue;

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

    public UtilitarianScoreStorage utilitarianScoreStorage;

    public UtilitarianCalculator utilitarianCalculator;

    public CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void rebroadCastBlock(JSONObject jsonObject)
            throws IOException {
        if (NodeProperty.isValidator()) {
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    // Quorum Id and Validator Check
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            {
                if (!this.fullBlockBlockingQueue.isEmpty()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = this.fullBlockBlockingQueue.take();
                        String messageType = jsonObject.getString("type");
                        if (messageType.equals("BLOCK")) {

                            Gson gson = new Gson();
                            Type blockObject = new TypeToken<Block>() {
                            }.getType();
                            Block inBlock = gson.fromJson(jsonObject.getString("data"),
                                    blockObject);
                            // System.out.println("Full Block HEIGHT" + inBlock.getBlocknumber());
                            // System.out.println("Full Block inital1" +
                            // this.blockchain.isValidBlock(inBlock));
                            // System.out.println("Full Block inital2" + inBlock.isFinalised());
                            // System.out.println("Full Block inital3" +
                            // !this.blockchain.blockFilled(inBlock));
                            // System.out.println(
                            // "Full Block inital4" +
                            // !this.capSEOBFTDemMessagePool.existingBlockMessage(inBlock));
                            // System.out.println("Full Block Initiate" + inBlock.getBlocknumber());
                            if (this.blockchain.isReadyforBlockValidity(inBlock)) {
                                if (this.blockchain.isValidBlock(inBlock) || inBlock.getValidity()) {
                                    if (inBlock.isFinalised()
                                            && !this.blockchain.blockFilled(inBlock)
                                            && !this.capSEOBFTDemMessagePool.existingBlockMessage(inBlock)) {
                                        // System.out.println("Full Block Handler" + inBlock.getBlocknumber());
                                        this.capSEOBFTDemMessagePool.addBlockMessage(inBlock);
                                        // Update the main chain with block
                                        boolean newsubepoch = this.blockchain.addBlock(inBlock);
                                        // Calculate Utilitarian Block Score
                                        Gson utilitarianGson = new Gson();
                                        Type utilitarianObject = new TypeToken<UtilitarianBlockScore>() {
                                        }.getType();

                                        UtilitarianBlockScore utilitarianBlockScore = utilitarianGson.fromJson(
                                                jsonObject.getString("utilitarianBlockScore"),
                                                utilitarianObject);
                                        utilitarianScoreStorage.updateAtruismBlockStorage(utilitarianBlockScore);
                                        // Update the ephemeral chain with block
                                        this.blockchain.updatefinalisedEphemeralBlock(inBlock);

                                        try {
                                            this.rebroadCastBlock(jsonObject);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        // Update Local Utilitarian Storage
                                        this.utilitarianScoreStorage
                                                .updateAtruismBlockStorage(utilitarianBlockScore);

                                    }
                                } else {
                                    // System.out.println("Invalid" + inBlock.getBlocknumber());
                                    // Malicious Actor
                                    this.utilitarianScoreStorage.incrementPeerMaliciousScore(
                                            inBlock.getProposerNodeIndex(),
                                            inBlock.getSubEpoch(), CAPSEOBFTProperty.maliciousscore);
                                    // We rebroadcast any way to propagate the malicious activity to be know by all
                                    this.rebroadCastBlock(jsonObject);
                                }
                            } else {
                                if (inBlock.getBlocknumber() > this.blockchain.getChain().lastKey()) {
                                    // System.out.println("lastkey" + this.blockchain.getChain().lastKey());
                                    this.rebroadCastBlock(jsonObject);
                                    // this.fullBlockBlockingQueue.add(jsonObject);
                                }

                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("Full Block Handler" + e.toString());
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    public CAPSEOBFTDemFullBlockHandler(BlockingQueue<JSONObject> fullBlockBlockingQueue, Blockchain blockChain,
            TransactionPool transactionPool,
            BlockPool blockPool, Wallet wallet, Validator validator, NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Synchronizer synchronizer, Simulator_result simulator_result,
            UtilitarianScoreStorage utilitarianScoreStorage, UtilitarianCalculator utilitarianCalculator,
            CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager) {
        this.fullBlockBlockingQueue = fullBlockBlockingQueue;
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
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.utilitarianCalculator = utilitarianCalculator;
        this.capSEOBFTDemQuorumManager = capSEOBFTDemQuorumManager;
    }
}