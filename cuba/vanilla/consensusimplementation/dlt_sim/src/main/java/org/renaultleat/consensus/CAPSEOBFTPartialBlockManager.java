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
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

public class CAPSEOBFTPartialBlockManager extends Thread {

    public Blockchain blockchain;

    public TransactionPool transactionPool;

    public Wallet wallet;

    public Validator validator;

    public NonValidator nonValidator;

    public NodeCommunicator nodeCommunicator;

    private int minapprovals;

    public String currentuser;

    public QuorumStorage quorumStorage;
    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile boolean thresholdReached = false;

    // public volatile boolean consensusincourse = false;

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
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        while (true) {
            try {
                int finalSubEpoch = this.blockchain.getPartialBlockSubEpochCounter();
                int partialBlockProposeCounter = this.blockchain.getPartialblockProposeCounter();

                if (this.quorumStorage.getQuorumMessageMap().containsKey(finalSubEpoch)) {
                    // System.out.println("QUOUMStatus" +
                    // this.quorumStorage.getQuorumMessageMap().toString());
                    if (this.transactionPool.getTransactionRoundStatus().containsKey(partialBlockProposeCounter)
                            && this.transactionPool.getTransactionRoundStatus().get(partialBlockProposeCounter)) {
                        // System.out.println("Status" +
                        // this.transactionPool.getTransactionRoundStatus().toString());
                        // System.out.println("TRIED PATIAL" + partialBlockProposeCounter);
                        // System.out.println("QID PATIAL" + QuorumStorage.getQuorumId());
                        // Check if a validator
                        ProposerMessage partialBlockProposeMessage = this.blockchain.getPartialBlockProposer(
                                partialBlockProposeCounter,
                                QuorumStorage.getQuorumId());
                        int partialBlockProposer = partialBlockProposeMessage.getNodeIndex();
                        ProposerMessage rivalPartialBlockProposeMessage = this.blockchain
                                .getRivalPartialBlockProposer(partialBlockProposeCounter, QuorumStorage.getQuorumId());
                        int rivalPartialBlockProposer = rivalPartialBlockProposeMessage.getNodeIndex();
                        // System.out.println("partialBlockProposer:" + partialBlockProposer);
                        // System.out.println("rivalPartialBlockProposer:" + rivalPartialBlockProposer);
                        // System.out.println("QuorumStorage:" + QuorumStorage
                        // .getQuorumIndex());

                        if (QuorumStorage.quorumInitialised) {

                            if ((partialBlockProposer == QuorumStorage.getQuorumIndex()
                                    || rivalPartialBlockProposer == QuorumStorage
                                            .getQuorumIndex())
                                    && NodeProperty.isValidator()) {

                                int proposeCalculatedatindex = 0;
                                if (partialBlockProposer == QuorumStorage.getQuorumIndex()) {
                                    proposeCalculatedatindex = partialBlockProposeMessage.getChainIndex();
                                } else if (rivalPartialBlockProposer == QuorumStorage
                                        .getQuorumIndex()) {
                                    proposeCalculatedatindex = rivalPartialBlockProposeMessage.getChainIndex();
                                }
                                // this.synchronizer.consensusincourse = true;
                                // this.synchronizer.thresholdReached = false;
                                List<Transaction> transactions = new ArrayList<Transaction>();
                                if (this.transactionPool
                                        .getTransactions(partialBlockProposeCounter) != null
                                        && this.transactionPool
                                                .getTransactions(partialBlockProposeCounter).size() > 0) {
                                    transactions.add(this.transactionPool
                                            .getTransactions(partialBlockProposeCounter).get(0));
                                    // Can be null as well
                                }

                                CopyOnWriteArrayList<Transaction> tempTransactionList = new CopyOnWriteArrayList<Transaction>(
                                        transactions);
                                Block block = null;
                                // Existing block
                                if (this.blockchain.getEphemeralChain().containsKey(partialBlockProposeCounter)) {
                                    block = this.blockchain.getEphemeralChain().get(partialBlockProposeCounter);
                                } else {
                                    // Create a new Block and Add to Ephemeral chain
                                    block = this.blockchain.createBlocknandAddtoEphemeral(wallet,
                                            partialBlockProposeCounter);
                                }
                                if (block == null) {
                                    System.out.println("BLOCK IS NULL");
                                }
                                PartialBlock partialBlock = null;
                                if (partialBlockProposer == QuorumStorage.getQuorumIndex()) {
                                    // You are the partial proposer
                                    partialBlock = this.blockchain.createPartialBlock(block, this.wallet,
                                            tempTransactionList,
                                            partialBlockProposeCounter, partialBlockProposeCounter,
                                            partialBlockProposer, rivalPartialBlockProposer, proposeCalculatedatindex,
                                            finalSubEpoch, CAPSEOBFTProperty.normalPartialProposal);
                                } else {
                                    partialBlock = this.blockchain.createPartialBlock(block, this.wallet,
                                            tempTransactionList,
                                            partialBlockProposeCounter, partialBlockProposeCounter,
                                            rivalPartialBlockProposer, partialBlockProposer, proposeCalculatedatindex,
                                            finalSubEpoch, CAPSEOBFTProperty.normalPartialProposal);
                                }

                                // Add the partial block created
                                block.addPartialBlock(partialBlock, this.wallet);
                                this.blockchain.getEphemeralChain().put(block.getBlocknumber(), block);
                                this.blockchain.incrementPartialblockProposeCounter();

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
                            } else {
                                // We also increment beause we dont have the right to propose partial block
                                this.blockchain.incrementPartialblockProposeCounter();

                            }
                            int blockspassedforcurrentsubepoch = (this.blockchain.partialblockSubEpochCounter.get()
                                    * CAPSEOBFTProperty.subepochthreshold);

                            // System.out.println("PATIALSUBEPOH" + blockspassedforcurrentsubepoch);
                            // System.out.println("PATIALOUNTER" +
                            // this.blockchain.getPartialblockProposeCounter());

                            // System.out.println(
                            // "INITIALPATIALSUBEPOCHOUNTER" +
                            // this.blockchain.partialblockSubEpochCounter.get());
                            if (this.blockchain.getPartialblockProposeCounter() >= blockspassedforcurrentsubepoch) {
                                this.blockchain.partialblockSubEpochCounter.incrementAndGet();

                            }
                            // System.out.println(
                            // "PATIALSUBEPOCHOUNTER" + this.blockchain.partialblockSubEpochCounter.get());
                            // Heavy Operation
                            // this.transactionPool
                            // .clearTransactionPoolFromIncomingBlock(tempTransactionList);
                            // Setting back to default
                            // this.synchronizer.consensusincourse = false;
                            // this.synchronizer.thresholdReached = true;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("PartialBlockManager Exception" + e.toString());
                e.printStackTrace();
            }
        }
    }

    public CAPSEOBFTPartialBlockManager(
            Blockchain blockChain,
            TransactionPool transactionPool, Wallet wallet, Validator validator,
            NonValidator nonValidator,
            NodeCommunicator nodeCommunicator, String currentuser,
            QuorumStorage quorumStorage) {

        this.blockchain = blockChain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.validator = validator;
        this.nonValidator = nonValidator;
        int total = Integer.valueOf(NodeProperty.totalnodes);
        int totalValidators = Integer.valueOf(NodeProperty.validators);
        this.nodeCommunicator = nodeCommunicator;
        // (2N/3)+1
        this.minapprovals = 2 * (totalValidators / 3) + 1;
        this.currentuser = currentuser;
        this.quorumStorage = quorumStorage;
    }

}
