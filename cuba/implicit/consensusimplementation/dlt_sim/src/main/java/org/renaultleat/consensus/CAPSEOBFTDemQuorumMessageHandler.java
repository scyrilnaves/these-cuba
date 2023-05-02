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
import org.renaultleat.chain.TransactionPool;
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

public class CAPSEOBFTDemQuorumMessageHandler extends Thread {

    public BlockingQueue<JSONObject> quorumMessageBlockingQueue;

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

    public QuorumStorage quorumStorage;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void rebroadCastQuorum(JSONObject jsonObject)
            throws IOException {
        if (NodeProperty.isValidator()) {
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            {

                if (!this.quorumMessageBlockingQueue.isEmpty()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = this.quorumMessageBlockingQueue.take();
                        String messageType = jsonObject.getString("type");
                        int subEpoch = Integer.valueOf(jsonObject.getString("subepoch"));
                        //System.out.println("Quoum for Sub Epoch" + subEpoch);
                        // if (quorumStorage.getQuorumMessageMap().size() > 0) {
                        //     System.out
                        //             .println("My status for Sub Epoch" + quorumStorage.getQuorumMessageMap().lastKey());
                        // }

                        if ((subEpoch == 1 && this.blockchain.getChain().containsKey(
                                ((subEpoch - 1) * CAPSEOBFTProperty.getSubEpochThreshold())))
                                || (this.blockchain.getChain().containsKey(
                                        (((subEpoch - 1) * CAPSEOBFTProperty.getSubEpochThreshold()) - 1)))) {
                            if (messageType.equals("QUORUM")) {
                                Gson gson = new Gson();
                                Type quorumMessageObject = new TypeToken<QuorumMessage>() {
                                }.getType();
                                QuorumMessage quorumMessage = gson.fromJson(jsonObject.getString("data"),
                                        quorumMessageObject);
                                if (!this.capSEOBFTDemMessagePool.existingQuorumMessage(quorumMessage)
                                        && this.capSEOBFTDemMessagePool.isValidQuorumMessage(quorumMessage)
                                        && this.validator.isValidValidator(quorumMessage.getMessageSender())) {
                                    if ((quorumMessage
                                            .getQuorumMessageType() == CAPSEOBFTProperty.normalQuorumMessage
                                            && this.blockchain.isValidQuorumProposer(quorumMessage))
                                            || (quorumMessage
                                                    .getQuorumMessageType() == CAPSEOBFTProperty.roundChangeQuorumMessage
                                                    && this.blockchain
                                                            .isValidRoundChangeQuorumProposer(quorumMessage))) {
                                        this.capSEOBFTDemMessagePool.addQuorumMessage(quorumMessage);
                                        // Incrment epoch counter for Round Chnage
                                        if (quorumMessage
                                                .getQuorumMessageType() == CAPSEOBFTProperty.roundChangeQuorumMessage) {
                                            this.blockchain.incrementAllEpochCountersforRoundChange();
                                        }

                                        try {
                                            this.rebroadCastQuorum(jsonObject);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        // Check if Epoch, Sub Epoch Equalisation for updating the quorum
                                        // As it is necessary or it will spoil the blocks formed.
                                        // Not Needed as we maintain signle epoch or subepoch counter
                                        int epoch = this.blockchain.getFinalEpochCounter();
                                        int finalepoch = this.blockchain.getFinalEpochCounter();
                                        int subepoch = this.blockchain.getFinalSubEpochCounter();
                                        int finalsubepoch = this.blockchain.getFinalSubEpochCounter();
                                        if (subepoch == finalsubepoch) {
                                            // Update Quorum Storage of Quorum Id and Index within
                                            quorumStorage.updateQuorumMessage(quorumMessage, wallet);

                                        }
                                    }

                                }
                            }
                        } else {
                            if (subEpoch > quorumStorage.getQuorumMessageMap().lastKey()) {
                                this.quorumMessageBlockingQueue.put(jsonObject);
                                this.rebroadCastQuorum(jsonObject);
                            }

                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        System.out.println("Quorum Message Handler Exception" + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public CAPSEOBFTDemQuorumMessageHandler(BlockingQueue<JSONObject> quorumMessageBlockingQueue, Blockchain blockChain,
            TransactionPool transactionPool,
            BlockPool blockPool, Wallet wallet, Validator validator, NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Synchronizer synchronizer, Simulator_result simulator_result, QuorumStorage quorumStorage) {
        this.quorumMessageBlockingQueue = quorumMessageBlockingQueue;
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

        this.quorumStorage = quorumStorage;
    }
}