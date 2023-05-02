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
import org.renaultleat.chain.ProposerMessage;
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

public class CAPSEOBFTDemQuorumManager extends Thread {

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

    public UtilitarianCalculator utilitarianCalculator;

    public QuorumStorage quorumStorage;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();
    // We Change the Quorum for Every Sub Epoch
    public void broadCastQuorum(QuorumMessage data)
            throws IOException {
        if (NodeProperty.isValidator()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("message", "QuorumMessage");
            jsonObject.put("type", "QUORUM");
            jsonObject.put("epoch", String.valueOf(data.getEpoch()));
            jsonObject.put("subepoch", String.valueOf(data.getSubEpoch()));
            jsonObject.put("nodeIndex", String.valueOf(data.getNodeIndex()));
            jsonObject.put("blockheight", String.valueOf(data.getBlockIndex()));
            jsonObject.put("sender", data.getMessageSender());
            Gson gson = new Gson();
            String datajson = gson.toJson(data);
            jsonObject.put("data", datajson);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    public void sendQuorumMessage(int type, int blockIndex) {

        // Calculate Utilitarian State and Form the Quorum Accordingly
        // Maintain Epoch, Maintain Sub Epoch Utilitarian Quorum Formation
        QuorumMessage quorumMessage = capSEOBFTDemMessagePool.formQuorumMessage(blockchain, wallet,
                utilitarianCalculator, type, blockIndex);
        try {
            this.broadCastQuorum(quorumMessage);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            if (this.blockchain.isSubEpochFilled()) {
                try {
                    // System.out.println("New Quorum PROCESS");
                    int subEpoch = this.blockchain.getFinalSubEpochCounter();
                    int previousSubEpoch = subEpoch - 1;
                    ProposerMessage quorumProposerMessage = this.blockchain
                            .getQuorumProposer(subEpoch);
                    int proposerIndex = quorumProposerMessage.getNodeIndex();
                    // System.out.println("New Quorum Node Id" + proposerIndex);
                    if (quorumStorage.getQuorumMessageMap().get(previousSubEpoch)
                            .getWalletMapping().containsKey(proposerIndex)) {
                        if (quorumStorage.getQuorumMessageMap().get(previousSubEpoch)
                                .getWalletMapping()
                                .get(proposerIndex) == wallet.getNodeproperty()
                                && QuorumStorage
                                        .getQuorumId() != CAPSEOBFTProperty.blacklistId) {
                            // System.out.println(
                            // "QurumProposer"
                            // + quorumStorage.getQuorumMessageMap().get(previousSubEpoch)
                            // .getWalletMapping()
                            // .get(proposerIndex));
                            sendQuorumMessage(CAPSEOBFTProperty.normalQuorumMessage,
                                    quorumProposerMessage.getChainIndex());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Quorum Manager Exception" + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    public CAPSEOBFTDemQuorumManager(BlockingQueue<JSONObject> quorumMessageBlockingQueue, Blockchain blockChain,
            TransactionPool transactionPool,
            BlockPool blockPool, Wallet wallet, Validator validator, NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            Synchronizer synchronizer, Simulator_result simulator_result,
            UtilitarianCalculator utilitarianCalculator, QuorumStorage quorumStorage) {
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
        this.utilitarianCalculator = utilitarianCalculator;
        this.quorumStorage = quorumStorage;
    }
}