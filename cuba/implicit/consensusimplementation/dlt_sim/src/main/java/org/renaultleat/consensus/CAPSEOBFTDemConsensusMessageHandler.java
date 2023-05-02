package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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

public class CAPSEOBFTDemConsensusMessageHandler extends Thread {

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

    public void rebroadCastMessage(JSONObject jsonObject)
            throws IOException {
        if (NodeProperty.isValidator()) {
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

    public void broadCastFinalise(PartialBlock partialBlock)
            throws IOException {
        if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("nodeindex", this.wallet.getNodeproperty());
            jsonObject.put("partialblockhash", partialBlock.getPartialBlockHash());
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("type", "FINALISE");
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

    @Override
    public void run() {
        // TODO Auto-generated method stub

        while (true) {
            {
                if (NodeProperty.isValidator && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
                    if (!this.messageBlockingQueue.isEmpty()) {
                        JSONObject jsonObject = null;

                        try {
                            jsonObject = this.messageBlockingQueue.take();
                            int quorumId = Integer.valueOf(jsonObject.getString("QuromId"));
                            String messageType = jsonObject.getString("type");
                            String messenger = jsonObject.getString("messenger");
                            int nodeIndex = Integer.valueOf(jsonObject.getString("nodeindex"));

                            if (!messageType.equals("ROUNDCHANGE")) {
                                Gson gson = new Gson();
                                Type partialBlockObject = new TypeToken<PartialBlock>() {
                                }.getType();
                                PartialBlock inPartialBlock = gson.fromJson(jsonObject.getString("data"),
                                        partialBlockObject);
                                int inSubepoch = inPartialBlock.getSubEpoch();
                                if (this.blockchain.isReadyforPartialBlockValidity(inPartialBlock)) {
                                    if (messageType.equals("PROPOSE")) {
                                        // Perform Consensus Only for the same quorum messages
                                        // System.out.println("ATPROPOSE" + "major:"
                                        // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                        // + inPartialBlock.getMinorBlocknumber() + "directqid:"
                                        // + this.quorumStorage.getQuorumMessageMap()
                                        // .get(inPartialBlock.getSubEpoch())
                                        // .getQuorumIdMappping().get(wallet.getNodeproperty())
                                        // + "inqid:" + quorumId + "QuoumId:"
                                        // + QuorumStorage.getQuorumId() + "subepoh:"
                                        // + this.blockchain.getFinalSubEpochCounter() + ";" + "partialsubepoch:"
                                        // + inSubepoch + ";");
                                        if (this.quorumStorage.getQuorumMessageMap()
                                                .containsKey(inPartialBlock.getSubEpoch())) {
                                            if (this.quorumStorage.getQuorumMessageMap()
                                                    .get(inPartialBlock.getSubEpoch()).getQuorumIdMappping()
                                                    .containsKey(wallet.getNodeproperty())) {
                                                if (quorumId == this.quorumStorage.getQuorumMessageMap()
                                                        .get(inPartialBlock.getSubEpoch())
                                                        .getQuorumIdMappping().get(wallet.getNodeproperty())) {

                                                    // System.out.println("PROPOSEDE" + inPartialBlock.toString());
                                                    // System.out.println(
                                                    // "PROPOSEDE1" +
                                                    // !this.blockchain.partialBlockFilled(inPartialBlock));
                                                    // System.out.println("PROPOSEDE2"
                                                    // +
                                                    // !this.capSEOBFTDemMessagePool.existingProposeMessage(inPartialBlock));
                                                    // System.out.println("PROPOSEDE3"
                                                    // + this.validator.isValidValidator(inPartialBlock.getProposer()));
                                                    // System.out.println("PROPOSEDE4"
                                                    // + this.blockchain.isValidPartialBlock(inPartialBlock));
                                                    // System.out.println("BEFOREPROPOSE" + "major:"
                                                    // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                                    // + inPartialBlock.getMinorBlocknumber());
                                                    if (!this.blockchain.partialBlockFilled(inPartialBlock)) {

                                                        // System.out.println("MINBEFOREPROPOSECHECK" + "major:"
                                                        // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                                        // + inPartialBlock.getMinorBlocknumber() + "ersult"
                                                        // + this.capSEOBFTDemMessagePool
                                                        // .existingProposeMessage(inPartialBlock));
                                                        // System.out.println("MINBEFOREPROPOSECHECK2" + "major:"
                                                        // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                                        // + inPartialBlock.getMinorBlocknumber() + "ersult"
                                                        // + this.validator
                                                        // .isValidValidator(inPartialBlock.getProposer()));
                                                        // System.out.println("MINBEFOREPROPOSECHECK3" + "major:"
                                                        // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                                        // + inPartialBlock.getMinorBlocknumber() + "ersult"
                                                        // + this.blockchain.isValidPartialBlock(inPartialBlock));
                                                        if (!this.capSEOBFTDemMessagePool
                                                                .existingProposeMessage(inPartialBlock)
                                                                && this.validator
                                                                        .isValidValidator(inPartialBlock.getProposer())
                                                                && this.blockchain
                                                                        .isValidPartialBlock(inPartialBlock)) {
                                                            this.capSEOBFTDemMessagePool
                                                                    .addProposeMessage(inPartialBlock);
                                                            // System.out.println("PROPOSE" + "major:"
                                                            // + inPartialBlock.getMajorBlocknumber() + "minor:"
                                                            // + inPartialBlock.getMinorBlocknumber());
                                                            try {
                                                                this.rebroadCastMessage(jsonObject);
                                                            } catch (Exception e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }
                                                            // Commit Vote for the partial block
                                                            try {
                                                                // Delay the commit message
                                                                if (NodeProperty.getnodeBehavior() == 1) {
                                                                    TimeUnit.MILLISECONDS
                                                                            .sleep(NodeProperty.latency + 30000);
                                                                }
                                                                if (NodeProperty.getnodeBehavior() == 2) {
                                                                    TimeUnit.MILLISECONDS
                                                                            .sleep(NodeProperty.latency + 60000);
                                                                }
                                                                this.broadCastCommit(inPartialBlock);
                                                            } catch (Exception e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }
                                                            // Add the commit vote to onself
                                                            this.capSEOBFTDemMessagePool.addCommitMessage(
                                                                    inPartialBlock,
                                                                    wallet.getPublicKey(), wallet.getNodeproperty());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (messageType.equals("COMMIT")) {

                                        String partialblockhash = jsonObject.getString("partialblockhash");
                                        // Perform Consensus Only for the same quorum messages
                                        if (this.quorumStorage.getQuorumMessageMap()
                                                .containsKey(inPartialBlock.getSubEpoch())) {
                                            QuorumMessage quorumMessageforSubEpoch = this.quorumStorage
                                                    .getQuorumMessageMap()
                                                    .get(inPartialBlock.getSubEpoch());
                                            if (quorumMessageforSubEpoch
                                                    .getQuorumIdMappping() != null) {
                                                if (quorumMessageforSubEpoch
                                                        .getQuorumIdMappping().containsKey(wallet.getNodeproperty())) {
                                                    if (quorumId == quorumMessageforSubEpoch
                                                            .getQuorumIdMappping().get(wallet.getNodeproperty())) {

                                                        {
                                                            if (!this.synchronizer.commitconsensusReached
                                                                    .containsKey(partialblockhash)) {
                                                                this.synchronizer.commitconsensusReached
                                                                        .put(partialblockhash, false);
                                                            }

                                                            // Check Valid Commit Message
                                                            if (!this.blockchain.partialBlockFilled(inPartialBlock)) {
                                                                if (!this.capSEOBFTDemMessagePool.existingCommitMessage(
                                                                        inPartialBlock,
                                                                        messenger)
                                                                        && this.validator.isValidValidator(
                                                                                inPartialBlock.getProposer())
                                                                        && this.blockchain
                                                                                .isValidPartialBlock(inPartialBlock)
                                                                        && !this.synchronizer.commitconsensusReached
                                                                                .get(inPartialBlock
                                                                                        .getPartialBlockHash())) {

                                                                    this.capSEOBFTDemMessagePool.addCommitMessage(
                                                                            inPartialBlock,
                                                                            messenger,
                                                                            nodeIndex);
                                                                    // System.out.println("COMMIT" + "major:"
                                                                    // + inPartialBlock.getMajorBlocknumber()
                                                                    // + "minor:"
                                                                    // + inPartialBlock.getMinorBlocknumber());
                                                                    try {
                                                                        // Re-BroadCast Commit Message
                                                                        this.rebroadCastMessage(jsonObject);
                                                                        this.broadCastCommit(inPartialBlock);
                                                                    } catch (Exception e) {
                                                                        // TODO Auto-generated catch block
                                                                        e.printStackTrace();
                                                                    }
                                                                    if (this.capSEOBFTDemMessagePool.commitMessagePool
                                                                            .get(inPartialBlock.getPartialBlockHash())
                                                                            .size() >= quorumStorage
                                                                                    .getIntraQuorumApprovals()) {
                                                                        // System.out.println("COMMITVALID1" + "major:"
                                                                        // + inPartialBlock.getMajorBlocknumber()
                                                                        // + "minor:"
                                                                        // + inPartialBlock.getMinorBlocknumber());
                                                                        this.synchronizer.commitconsensusReached
                                                                                .put(inPartialBlock
                                                                                        .getPartialBlockHash(),
                                                                                        true);
                                                                        if (inPartialBlock.getProposer()
                                                                                .equals(this.wallet.getPublicKey())) {
                                                                            // System.out.println("COMMITVALID2" +
                                                                            // "major:"
                                                                            // + inPartialBlock
                                                                            // .getMajorBlocknumber()
                                                                            // + "minor:"
                                                                            // + inPartialBlock
                                                                            // .getMinorBlocknumber());
                                                                            // Send Finalise only
                                                                            inPartialBlock.setFinalised(true);
                                                                            // Update the Commit Voters for the Partial
                                                                            // Block
                                                                            inPartialBlock.setCommitMessageValidators(
                                                                                    this.capSEOBFTDemMessagePool.commitMessagePool
                                                                                            .get(inPartialBlock
                                                                                                    .getPartialBlockHash()));
                                                                            // Update the Commit Voter Indexes
                                                                            inPartialBlock
                                                                                    .setCommitMessageValidatorIndexes(
                                                                                            this.capSEOBFTDemMessagePool.commitMessagePoolIndexes
                                                                                                    .get(inPartialBlock
                                                                                                            .getPartialBlockHash()));
                                                                            this.capSEOBFTDemMessagePool
                                                                                    .addFinaliseMessage(inPartialBlock);
                                                                            try {
                                                                                // BroadCast Finalise Message for the
                                                                                // Partial Block
                                                                                // System.out.println("FINALISE" +
                                                                                // "major:"
                                                                                // + inPartialBlock
                                                                                // .getMajorBlocknumber()
                                                                                // + "minor:"
                                                                                // + inPartialBlock
                                                                                // .getMinorBlocknumber());
                                                                                this.broadCastFinalise(inPartialBlock);
                                                                            } catch (Exception e) {
                                                                                // TODO Auto-generated catch block
                                                                                e.printStackTrace();
                                                                            }
                                                                            // Add the finalised Block to the ephemeral
                                                                            // chain
                                                                            // If TRUE: the partial blocks completely
                                                                            // fills
                                                                            // the required
                                                                            // block
                                                                            // FALSE: Not Completely Filled
                                                                            // Check if we can propose the BLOCK
                                                                            // message.
                                                                            String result = this.blockchain
                                                                                    .addFinalisedPartialBlock(
                                                                                            this.wallet,
                                                                                            inPartialBlock);

                                                                        }

                                                                    }
                                                                }

                                                            }
                                                            /*
                                                             * // Result Collation
                                                             * // Prepare Message
                                                             * this.simulator_result.prepareCounter
                                                             * .put(new Timestamp(System.currentTimeMillis()),
                                                             * this.ibftMessagePool
                                                             * .getSizeofPrepareMessagePoolForBlockHash(blockhash));
                                                             * // Commit Message
                                                             * this.simulator_result.commitCounter
                                                             * .put(new Timestamp(System.currentTimeMillis()),
                                                             * this.ibftMessagePool
                                                             * .getSizeofCommitMessagePoolForBlockHash(blockhash));
                                                             * // RoundChange Message
                                                             * this.simulator_result.roundChangeCounter
                                                             * .put(new Timestamp(System.currentTimeMillis()),
                                                             * this.ibftMessagePool
                                                             * .getSizeofRoundChangeMessagePoolForBlockHash(blockhash));
                                                             */

                                                        }
                                                    }
                                                }

                                            }
                                        } else {
                                            try {
                                                // Re-BroadCast Commit Message
                                                if (inPartialBlock.getMajorBlocknumber() > this.blockchain.getChain()
                                                        .lastKey()) {
                                                    this.messageBlockingQueue.put(jsonObject);
                                                    this.rebroadCastMessage(jsonObject);
                                                }

                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }
                            } else {
                                if (messageType.equals("ROUNDCHANGE")) {
                                    int roundChangeSubEpoch = Integer.valueOf(jsonObject.getString("subEpoch"));
                                    // No Quorum Check Needed as it network wide
                                    if (!this.synchronizer.roundchangeconsensusReached
                                            .containsKey(roundChangeSubEpoch)) {
                                        this.synchronizer.roundchangeconsensusReached.put(roundChangeSubEpoch,
                                                false);
                                    }
                                    if (!this.capSEOBFTDemMessagePool.existingRoundChangeMessage(
                                            roundChangeSubEpoch,
                                            messenger)
                                            && this.validator.isValidValidator(messenger)
                                            && !this.synchronizer.roundchangeconsensusReached
                                                    .get(roundChangeSubEpoch)) {
                                        this.capSEOBFTDemMessagePool.addRoundChangeMessage(roundChangeSubEpoch,
                                                messenger);

                                        try {
                                            this.rebroadCastMessage(jsonObject);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        // Clear the message Pool
                                        if (this.capSEOBFTDemMessagePool.roundChangeMessagePool
                                                .get(roundChangeSubEpoch)
                                                .size() >= quorumStorage.getInterQuorumApprovals()) {
                                            // Parallelizing opportunity to run with round as key and then other
                                            // data
                                            // No Need as we remove in pre-prepare stage as we might lose
                                            // incoming
                                            // transaction as well
                                            // Later to change as hashmap if need as (round,transactionpool)
                                            // TOCHECK!!!!
                                            // this.transactionPool.clearTransactionPool();
                                            // Thread Safe

                                            this.synchronizer.roundchangeconsensusReached.put(
                                                    roundChangeSubEpoch,
                                                    true);
                                            int proposedRoundChangeSubEpoch = roundChangeSubEpoch + 1;
                                            ProposerMessage quorumProposerMessage = this.blockchain
                                                    .getRoundChangeQuorumProposer(proposedRoundChangeSubEpoch);
                                            int proposerIndex = quorumProposerMessage
                                                    .getNodeIndex();
                                            // System.out.println("RounchangeSubEpoch:" + roundChangeSubEpoch);
                                            // System.out.println("proprindx:" + proposerIndex);
                                            if (quorumStorage.getQuorumMessageMap()
                                                    .containsKey(roundChangeSubEpoch)) {
                                                QuorumMessage quorumMessageforSubEpoch = this.quorumStorage
                                                        .getQuorumMessageMap()
                                                        .get(roundChangeSubEpoch);
                                                if (quorumMessageforSubEpoch
                                                        .getWalletMapping().containsKey(proposerIndex)) {
                                                    if (quorumMessageforSubEpoch
                                                            .getWalletMapping()
                                                            .get(proposerIndex) == wallet.getNodeproperty()) {
                                                        // Increment all the subepoch and epoch if neessary
                                                        this.blockchain
                                                                .incrementAllEpochCountersforRoundChange();
                                                        // Send the RoundChange Quorum
                                                        this.capSEOBFTDemQuorumManager
                                                                .sendQuorumMessage(
                                                                        CAPSEOBFTProperty.roundChangeQuorumMessage,
                                                                        proposedRoundChangeSubEpoch);

                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            System.out.println("Consensus Message Handler Exeption" + e.toString());
                            e.printStackTrace();
                        }

                    }

                }
            }
        }

    }

    public CAPSEOBFTDemConsensusMessageHandler(BlockingQueue<JSONObject> inMessageBlockingQueue, Blockchain blockChain,
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