package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.PartialBlock;
import org.renaultleat.chain.ProposerMessage;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.NodeCommunicatorSec;
import org.renaultleat.network.NodeCommunicatorTer;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

public class CAPSEOBFTDemTransactionMessageHandlerTer extends Thread {
    public BlockingQueue<JSONObject> transactionBlockingQueue;

    public Blockchain blockchain;

    public TransactionPool transactionPool;

    public BlockPool blockPool;

    public CAPSEOBFTDemMessagePool pbftMessagePool;

    public Wallet wallet;

    public Validator validator;

    public NonValidator nonValidator;

    public NodeCommunicatorTer nodeCommunicatorTer;

    public Synchronizer synchronizer;

    private int minapprovals;

    public String currentuser;

    public CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager;

    public QuorumStorage quorumStorage;
    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile boolean thresholdReached = false;

    // public volatile boolean consensusincourse = false;

    public void broadCastPrivacy(String txHash, String privacyMessageType, boolean response, int to, int groupprivacyid,
            Transaction txdata)
            throws IOException {
        if (NodeProperty.isValidator()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "PRIVACY");
            jsonObject.put("privacymessagetype", privacyMessageType);
            jsonObject.put("transactionhash", txHash);
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("nodeIndex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("from", this.wallet.getNodeproperty());
            jsonObject.put("groupprivacyid", String.valueOf(groupprivacyid));
            jsonObject.put("to", String.valueOf(to));
            if (privacyMessageType.equals(CAPSEOBFTProperty.requestPrivacyMessage)) {
                Gson gson = new Gson();
                String txdatajson = gson.toJson(txdata);
                jsonObject.put("txdata", txdatajson);
            } else if (privacyMessageType.equals(CAPSEOBFTProperty.responsePrivacyMesage)) {
                jsonObject.put("validateddata", response);
            }
            this.nodeCommunicatorTer.sendMessage(jsonObject.toString());
        }
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        while (true) {
            int currentround = this.blockchain.getRoundCounter();
            if (!this.transactionBlockingQueue.isEmpty()) {
                int finalSubEpoch = this.blockchain.getFinalSubEpochCounter();
                JSONObject jsonObject;
                try {
                    jsonObject = this.transactionBlockingQueue.take();
                    Gson gson = new Gson();
                    Type transactionObject = new TypeToken<Transaction>() {
                    }.getType();
                    Transaction inputTransaction = gson.fromJson(jsonObject.getString("data"), transactionObject);
                    // if (!this.transactionPool.transactionExists(inputTransaction)
                    // &&
                    // !this.transactionPool.confirmedTransactionIdexists(inputTransaction.getId())
                    // && this.transactionPool.verifyTransaction(inputTransaction)
                    // && this.validator.isValidValidator(inputTransaction.getFrom()))
                    // REMOVED ADDITIOANL TRANSZCION EXIST
                    if (!PrivacyStorage.isPrivacy() || (PrivacyStorage.isPrivacy()
                            && inputTransaction.getGroupPrivacyid() == PrivacyStorage.getPrivacyGroupId())) {
                        if (!this.transactionPool.getConfirmedTransactionIds().contains(inputTransaction.getId())
                                && this.transactionPool.verifyTransaction(inputTransaction)
                                && this.nonValidator.isValidNonValidator(inputTransaction.getFrom())) {
                            this.synchronizer.thresholdReached = this.transactionPool
                                    .addTransaction(inputTransaction);
                            this.transactionPool.getConfirmedTransactionIds().add(inputTransaction.getId());
                            try {
                                this.nodeCommunicatorTer.rebroadCastTransaction(jsonObject);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        broadCastPrivacy(inputTransaction.getHash(), CAPSEOBFTProperty.requestPrivacyMessage, false,
                                Wallet.getNodeproperty(), inputTransaction.getGroupPrivacyid(), inputTransaction);
                    }
                    if (this.transactionPool.getTransactionRoundStatus().containsKey(currentround)
                            && this.transactionPool.getTransactionRoundStatus().get(currentround)) {
                        this.blockchain.incrementRoundCounter();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("Transaction Message Handler Exception" + e.toString());
                    e.printStackTrace();
                }

            }
        }
    }

    public CAPSEOBFTDemTransactionMessageHandlerTer(BlockingQueue<JSONObject> intransactionBlockingQueue,
            Blockchain blockChain,
            TransactionPool transactionPool, BlockPool blockPool, Wallet wallet, Validator validator,
            NonValidator nonValidator,
            NodeCommunicatorTer nodeCommunicatorTer, String currentuser,
            CAPSEOBFTDemMessagePool pbftMessagePool, Synchronizer synchronizer,
            CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager, QuorumStorage quorumStorage) {
        this.transactionBlockingQueue = intransactionBlockingQueue;
        this.blockchain = blockChain;
        this.transactionPool = transactionPool;
        this.blockPool = blockPool;
        this.wallet = wallet;
        this.validator = validator;
        this.nonValidator = nonValidator;
        int total = Integer.valueOf(NodeProperty.totalnodes);
        int totalValidators = Integer.valueOf(NodeProperty.validators);
        this.nodeCommunicatorTer = nodeCommunicatorTer;
        this.pbftMessagePool = pbftMessagePool;
        // (2N/3)+1
        this.minapprovals = 2 * (totalValidators / 3) + 1;
        this.currentuser = currentuser;
        this.synchronizer = synchronizer;
        this.capSEOBFTDemQuorumManager = capSEOBFTDemQuorumManager;
        this.quorumStorage = quorumStorage;
    }

}
