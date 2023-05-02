package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ejb.TransactionAttribute;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.api.Simulator_result;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

/**
 * Handler to update the Utilitarian Score
 * //peerUtilitarianScore //peerUtilitarianMissedProposalScore
 * //peerUtilitarianMissedMessageScore //peerMaliciousScore;
 * This Handler is used in case of Multi threaded call from P2PHandler
 * 
 */

public class CAPSEOBFTDemPrivacyHandler extends Thread {

    public BlockingQueue<JSONObject> privacyBlockingQueue;

    public NodeCommunicator nodeCommunicator;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public Wallet wallet;

    public TransactionPool transactionPool;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void broadCastPrivacy(String txHash, String privacyMessageType, boolean response, int to, int groupprivacyid,
            Transaction txdata, int txQuorumId)
            throws IOException {
        if (NodeProperty.isValidator()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "PRIVACY");
            jsonObject.put("privacymessagetype", privacyMessageType);
            jsonObject.put("transactionhash", txHash);
            jsonObject.put("sender", this.wallet.getPublicKey());
            jsonObject.put("nodeIndex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("from", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("groupprivacyid", String.valueOf(groupprivacyid));
            jsonObject.put("to", String.valueOf(to));
            jsonObject.put("txQuorumId", txQuorumId);
            if (privacyMessageType.equals(CAPSEOBFTProperty.requestPrivacyMessage)) {
                Gson gson = new Gson();
                String txdatajson = gson.toJson(txdata);
                jsonObject.put("txdata", txdatajson);
            } else if (privacyMessageType.equals(CAPSEOBFTProperty.responsePrivacyMesage)) {
                jsonObject.put("validateddata", response);
            }
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    public void rebroadCastMessage(JSONObject jsonObject)
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
                // HANDLE REQUEST
                if (!this.privacyBlockingQueue.isEmpty()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = this.privacyBlockingQueue.take();
                        String messageType = jsonObject.getString("type");
                        String privacymessagetype = jsonObject.getString("privacymessagetype");
                        String txHash = jsonObject.getString("transactionhash");
                        String sender = jsonObject.getString("sender");
                        int groupPrivacyId = Integer.valueOf(jsonObject.getString("groupprivacyid"));
                        Gson gson = new Gson();
                        Type transactionObject = new TypeToken<Transaction>() {
                        }.getType();
                        Transaction transaction = gson.fromJson(jsonObject.getString("txdata"),
                                transactionObject);
                        if (messageType.equals("PRIVACY")) {
                            if (!capSEOBFTDemMessagePool.existingPrivacyMessage(txHash, sender, privacymessagetype)) {
                                capSEOBFTDemMessagePool.addPrivacyMessage(txHash, sender, privacymessagetype);
                                if (privacymessagetype.equals(CAPSEOBFTProperty.requestPrivacyMessage)) {

                                    // Check if you belong to the same privacy Id as that of the transaction to
                                    // respond to its validity
                                    if (groupPrivacyId == PrivacyStorage.privacyGroupId) {
                                        int from = Integer.valueOf(jsonObject.getString("from"));
                                        int txQuorumId = Integer.valueOf(jsonObject.getString("txQuorumId"));

                                        boolean result = this.transactionPool.verifyTransaction(transaction);
                                        broadCastPrivacy(transaction.getHash(), privacymessagetype, result, from,
                                                groupPrivacyId, transaction, txQuorumId);
                                    }
                                    // Response Message
                                } else if (privacymessagetype.equals(CAPSEOBFTProperty.responsePrivacyMesage)) {
                                    int to = Integer.valueOf(jsonObject.getString("to"));
                                    // If the Response is targeted for you
                                    // Add to the transaction Pool
                                    if (to == Wallet.getNodeproperty()) {
                                        this.transactionPool
                                                .addTransaction(transaction);
                                        this.transactionPool.confirmedTransactionIds.add(transaction.getId());

                                    }
                                }

                            }
                            rebroadCastMessage(jsonObject);
                        }
                    } catch (Exception e) {
                        System.out.println("Privacy Handler Exception" + e.toString());
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public CAPSEOBFTDemPrivacyHandler(
            CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool, Wallet wallet, TransactionPool inTransactionPool,
            BlockingQueue<JSONObject> privacyBlockingQueue) {
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.wallet = wallet;
        this.transactionPool = inTransactionPool;
        this.privacyBlockingQueue = privacyBlockingQueue;

    }
}