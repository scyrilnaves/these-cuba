package org.renaultleat.consensus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map.Entry;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.PartialBlock;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;

public class CAPSEOBFTDemMessagePool {

    public UtilitarianCalculator utilitarianCalculator;

    public QuorumStorage quorumStorage;
    // We verify the message before for each validator so no need to store the
    // entire message
    // Store only the ACK of the message instead of the entire message as it will be
    // discarded
    // PartialBlockhash, Message Sender
    public volatile Map<String, CopyOnWriteArrayList<String>> proposeMessagePool;
    // PartialBlockhash, Message Sender
    public volatile Map<String, CopyOnWriteArrayList<String>> commitMessagePool;

    // PartialBlockhash, Message Sender Index
    public volatile Map<String, CopyOnWriteArrayList<Integer>> commitMessagePoolIndexes;

    // Height No, MessageSender
    public volatile Map<Integer, CopyOnWriteArrayList<String>> blockMessagePool;
    // SubEpoch, MessageSender
    public volatile Map<Integer, CopyOnWriteArrayList<String>> quorumMessagePool;

    // RoundNo, MessageSender
    public volatile Map<Integer, CopyOnWriteArrayList<String>> finaliseMessagePool;

    // RoundNo, MessageSender
    public volatile Map<Integer, CopyOnWriteArrayList<String>> heartBeatMessagePool;

    // BlockHeight ,Message Sender
    public volatile Map<Integer, CopyOnWriteArrayList<String>> roundChangeMessagePool;

    // txhash, Requestor
    public volatile Map<String, CopyOnWriteArrayList<String>> privacyRequestMessagePool;

    // txhash, Replier
    public volatile Map<String, CopyOnWriteArrayList<String>> privacyResponseMessagePool;

    public Map<String, CopyOnWriteArrayList<String>> getPrivacyRequestMessagePool() {
        return this.privacyRequestMessagePool;
    }

    public void setPrivacyRequestMessagePool(Map<String, CopyOnWriteArrayList<String>> privacyRequestMessagePool) {
        this.privacyRequestMessagePool = privacyRequestMessagePool;
    }

    public Map<String, CopyOnWriteArrayList<String>> getPrivacyResponseMessagePool() {
        return this.privacyResponseMessagePool;
    }

    public void setPrivacyResponseMessagePool(Map<String, CopyOnWriteArrayList<String>> privacyResponseMessagePool) {
        this.privacyResponseMessagePool = privacyResponseMessagePool;
    }

    public Map<String, CopyOnWriteArrayList<String>> getProposeMessagePool() {
        return this.proposeMessagePool;
    }

    public void setProposeMessagePool(Map<String, CopyOnWriteArrayList<String>> proposeMessagePool) {
        this.proposeMessagePool = proposeMessagePool;
    }

    public Map<Integer, CopyOnWriteArrayList<String>> getFinaliseMessagePool() {
        return this.finaliseMessagePool;
    }

    public void setFinaliseMessagePool(Map<Integer, CopyOnWriteArrayList<String>> finaliseMessagePool) {
        this.finaliseMessagePool = finaliseMessagePool;
    }

    public Map<Integer, CopyOnWriteArrayList<String>> getHeartBeatMessagePool() {
        return this.heartBeatMessagePool;
    }

    public Map<String, CopyOnWriteArrayList<String>> getCommmitMessagePool() {
        return this.commitMessagePool;
    }

    public void setCommmitMessagePool(Map<String, CopyOnWriteArrayList<String>> commmitMessagePool) {
        this.commitMessagePool = commmitMessagePool;
    }

    public Map<String, CopyOnWriteArrayList<Integer>> getCommmitMessagePoolIndexes() {
        return this.commitMessagePoolIndexes;
    }

    public void setCommmitMessagePoolIndexes(Map<String, CopyOnWriteArrayList<Integer>> commmitMessagePoolIndexes) {
        this.commitMessagePoolIndexes = commmitMessagePoolIndexes;
    }

    public Map<Integer, CopyOnWriteArrayList<String>> getRoundChangeMessagePool() {
        return this.roundChangeMessagePool;
    }

    public void setRoundChangePool(Map<Integer, CopyOnWriteArrayList<String>> roundChangeMessagePool) {
        this.roundChangeMessagePool = roundChangeMessagePool;
    }

    public Map<Integer, CopyOnWriteArrayList<String>> getBlockMessagePool() {
        return this.blockMessagePool;
    }

    public Map<Integer, CopyOnWriteArrayList<String>> getQuorumMessagePool() {
        return this.quorumMessagePool;
    }

    public void setBlockMessagePool(Map<Integer, CopyOnWriteArrayList<String>> blockMessagePool) {
        this.blockMessagePool = blockMessagePool;
    }

    public synchronized Message message(String type, Block block, Wallet wallet, Blockchain blockchain) {
        int epoch = blockchain.getFinalEpochCounter();
        int subepoch = blockchain.getFinalSubEpochCounter();
        int index = Wallet.getNodeproperty();
        int round = blockchain.getRoundCounter();
        Message message = new Message(type, block.getBlockHash(),
                wallet.signData(block.getBlockHash() + CryptoUtil.getHash(type)),
                wallet.getPublicKey(), type, round, epoch, subepoch, index);
        if (message.type.equals("PROPOSE")) {
            if (this.proposeMessagePool.get(message.blockHash) != null) {
                this.proposeMessagePool.get(message.blockHash).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> preparePoolMessages = new CopyOnWriteArrayList<String>();
                preparePoolMessages.add(message.getMessageSender());
                this.proposeMessagePool.put(message.blockHash, preparePoolMessages);
            }

        } else if (message.type.equals("COMMIT")) {
            if (this.commitMessagePool.get(message.blockHash) != null) {
                this.commitMessagePool.get(message.blockHash).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> commitPoolMessages = new CopyOnWriteArrayList<String>();
                commitPoolMessages.add(message.getMessageSender());
                this.commitMessagePool.put(message.blockHash, commitPoolMessages);
            }
        } else if (message.type.equals("ROUNDCHANGE")) {
            if (this.roundChangeMessagePool.get(message.getRound()) != null) {
                this.roundChangeMessagePool.get(message.getRound()).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> roundChangePoolMessages = new CopyOnWriteArrayList<String>();
                roundChangePoolMessages.add(message.getMessageSender());
                this.roundChangeMessagePool.put(message.getRound(), roundChangePoolMessages);
            }
        }
        return message;
    }

    // Type: Normal or Round Change
    public synchronized QuorumMessage formQuorumMessage(Blockchain blockChain, Wallet wallet,
            UtilitarianCalculator utilitarianCalculator, int type, int inblockIndex) {

        int epoch = blockChain.getFinalEpochCounter();
        // Considering that the SubEpoch is already incremented after the addition of
        // the block
        int proposedSubEpoch = blockChain.getFinalSubEpochCounter();
        // System.out.println("formQMESSAGE" + proposedSubEpoch);
        int index = Wallet.getNodeproperty();
        int blockIndex = inblockIndex;
        QuorumMessage quorumMessage = null;
        // System.out.println("PRPOSED SUBEPOCH" + proposedSubEpoch);
        try {
            quorumMessage = utilitarianCalculator.formQuorumMessageForNextSubEpoch(proposedSubEpoch, type, blockIndex);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (this.quorumMessagePool.get(proposedSubEpoch) != null) {
            this.quorumMessagePool.get(proposedSubEpoch).add(quorumMessage.getMessageSender());
        } else {
            CopyOnWriteArrayList<String> quorumPoolMessages = new CopyOnWriteArrayList<String>();
            quorumPoolMessages.add(quorumMessage.getMessageSender());
            this.quorumMessagePool.put(quorumMessage.getSubEpoch(), quorumPoolMessages);
        }
        this.quorumStorage.updateQuorumMessage(quorumMessage, wallet);
        return quorumMessage;
    }

    // Type: Normal or Round Change
    public synchronized QuorumMessage formInitialQuorumMessage(Blockchain blockChain, Wallet wallet, int blockIndex)
            throws Exception {
        // System.out.println("Initital Qurum");
        int epoch = 1;
        int subEpoch = 1;

        Map<Integer, Integer> initialGenesisNodeList = populateGenesisNodeList();

        Map<Integer, Integer> walletMapping = populateWalletMapping(initialGenesisNodeList);

        QuorumMessage quorumMessage = new QuorumMessage("QUORUM", epoch, subEpoch, Wallet.getNodeproperty(),
                null, CryptoUtil.getPublicKeyString(Wallet.getNodeproperty()), blockIndex,
                null, null,
                null, null,
                QuorumStorage.getTotalEffectiveMembers(), QuorumStorage.getTotalEffectiveMembers(),
                new HashMap<Integer, List<Integer>>(),
                new ConcurrentHashMap<Integer, Integer>(),
                new ConcurrentHashMap<Integer, Double>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(),
                initialGenesisNodeList, subEpoch, CAPSEOBFTProperty.normalQuorumMessage, walletMapping,
                "NORMAL QUORUM FORMATION", new HashSet<>(), new HashSet<>());
        this.utilitarianCalculator.updateQuorumMessage(quorumMessage);
        this.quorumStorage.updateQuorumMessage(quorumMessage, wallet);

        if (this.quorumMessagePool.get(subEpoch) != null) {
            this.quorumMessagePool.get(subEpoch).add(quorumMessage.getMessageSender());
        } else {
            CopyOnWriteArrayList<String> quorumPoolMessages = new CopyOnWriteArrayList<String>();
            quorumPoolMessages.add(quorumMessage.getMessageSender());
            this.quorumMessagePool.put(quorumMessage.getSubEpoch(), quorumPoolMessages);
        }
        return quorumMessage;
    }

    public Map<Integer, Integer> populateWalletMapping(Map<Integer, Integer> sortedMap) {
        Map<Integer, Integer> newWalletMapping = new HashMap<>();
        int index = 0;
        for (Entry<Integer, Integer> sortedMapEntry : sortedMap.entrySet()) {
            newWalletMapping.put(index, sortedMapEntry.getValue());
            index++;
        }

        return newWalletMapping;

    }

    // We just start from 0 as the Node List start from 0 until the validator size
    // or Effective Members Size
    // Ranks are Random at the moment
    public Map<Integer, Integer> populateGenesisNodeList() {

        Map<Integer, Integer> nodeGenesisList = new HashMap<Integer, Integer>();
        for (int i = 0; i < QuorumStorage.totaleffectivemembers; i++) {
            nodeGenesisList.put(i + 1, i);
        }
        return nodeGenesisList;

    }

    public synchronized FinaliseMessage formFinaliseMessage(Blockchain blockChain, Wallet wallet,
            Map<Timestamp, Map<List<Integer>, String>> inTemporalHashingState, PartialBlock partialBlock) {
        int epoch = blockChain.getFinalEpochCounter();
        int subepoch = blockChain.getFinalSubEpochCounter();
        int index = Wallet.getNodeproperty();
        int round = blockChain.getRoundCounter();
        Map<Timestamp, Map<List<Integer>, String>> temporalHashingState = inTemporalHashingState;

        FinaliseMessage finaliseMessage = new FinaliseMessage("FINALISE", index,
                wallet.signData(CryptoUtil.getHash(String
                        .valueOf(index + round))),
                wallet.getPublicKey(), round, temporalHashingState, epoch, subepoch, partialBlock);

        if (this.finaliseMessagePool.get(round) != null) {
            this.finaliseMessagePool.get(round).add(finaliseMessage.getMessageSender());
        } else {
            CopyOnWriteArrayList<String> finalisePoolMessages = new CopyOnWriteArrayList<String>();
            finalisePoolMessages.add(finaliseMessage.getMessageSender());
            this.finaliseMessagePool.put(finaliseMessage.getRound(), finalisePoolMessages);
        }
        return finaliseMessage;
    }

    public synchronized BlockMessage formBlockMessage(Blockchain blockChain, Block block,
            Wallet wallet) {
        int epoch = blockChain.getFinalEpochCounter();
        int subepoch = blockChain.getFinalSubEpochCounter();
        int index = Wallet.getNodeproperty();
        int round = blockChain.getRoundCounter();
        BlockMessage blockMessage = new BlockMessage("BLOCK", index,
                wallet.signData(CryptoUtil.getHash(String
                        .valueOf(index + round))),
                wallet.getPublicKey(), round, epoch, subepoch, block);

        if (this.blockMessagePool.get(round) != null) {
            this.blockMessagePool.get(round).add(blockMessage.getMessageSender());
        } else {
            CopyOnWriteArrayList<String> fullBlockPoolMessages = new CopyOnWriteArrayList<String>();
            fullBlockPoolMessages.add(blockMessage.getMessageSender());
            this.blockMessagePool.put(blockMessage.getRound(), fullBlockPoolMessages);
        }
        return blockMessage;
    }

    public synchronized boolean addMessage(Message message) {
        if (message.type.equals("PROPOSE")) {
            if (this.proposeMessagePool.get(message.blockHash) != null) {
                this.proposeMessagePool.get(message.blockHash).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> proposePoolMessages = new CopyOnWriteArrayList<String>();
                proposePoolMessages.add(message.getMessageSender());
                this.proposeMessagePool.put(message.blockHash, proposePoolMessages);
            }
            return true;
        } else if (message.type.equals("COMMIT")) {
            if (this.commitMessagePool.get(message.blockHash) != null) {
                this.commitMessagePool.get(message.blockHash).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> commitPoolMessages = new CopyOnWriteArrayList<String>();
                commitPoolMessages.add(message.getMessageSender());
                this.commitMessagePool.put(message.blockHash, commitPoolMessages);
            }
            return true;
        } else if (message.type.equals("ROUNDCHANGE")) {
            if (this.roundChangeMessagePool.get(message.getRound()) != null) {
                this.roundChangeMessagePool.get(message.getRound()).add(message.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> roundChangePoolMessages = new CopyOnWriteArrayList<String>();
                roundChangePoolMessages.add(message.getMessageSender());
                this.roundChangeMessagePool.put(message.getRound(), roundChangePoolMessages);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean addProposeMessage(PartialBlock partialBlock) {
        if (this.proposeMessagePool.get(partialBlock.getPartialBlockHash()) != null) {
            this.proposeMessagePool.get(partialBlock.getPartialBlockHash()).add(partialBlock.getProposer());
        } else {
            CopyOnWriteArrayList<String> proposePoolMessages = new CopyOnWriteArrayList<String>();
            proposePoolMessages.add(partialBlock.getProposer());
            this.proposeMessagePool.put(partialBlock.getPartialBlockHash(), proposePoolMessages);
        }
        return true;
    }

    public synchronized boolean addCommitMessage(PartialBlock partialBlock, String messenger, int nodeIndex) {
        if (this.commitMessagePool.get(partialBlock.getPartialBlockHash()) != null) {
            this.commitMessagePool.get(partialBlock.getPartialBlockHash()).add(messenger);
        } else {
            CopyOnWriteArrayList<String> commitPoolMessages = new CopyOnWriteArrayList<String>();
            commitPoolMessages.add(messenger);
            this.commitMessagePool.put(partialBlock.getPartialBlockHash(), commitPoolMessages);
        }

        if (this.commitMessagePoolIndexes.get(partialBlock.getPartialBlockHash()) != null) {
            this.commitMessagePoolIndexes.get(partialBlock.getPartialBlockHash()).add(nodeIndex);
        } else {
            CopyOnWriteArrayList<Integer> commitPoolMessageIndexList = new CopyOnWriteArrayList<Integer>();
            commitPoolMessageIndexList.add(nodeIndex);
            this.commitMessagePoolIndexes.put(partialBlock.getPartialBlockHash(), commitPoolMessageIndexList);
        }
        return true;
    }

    public synchronized boolean addHeartBeatMessage(int round, String messenger) {
        if (this.heartBeatMessagePool.get(round) != null) {
            this.heartBeatMessagePool.get(round).add(messenger);
        } else {
            CopyOnWriteArrayList<String> heartBeatPoolMessages = new CopyOnWriteArrayList<String>();
            heartBeatPoolMessages.add(messenger);
            this.heartBeatMessagePool.put(round, heartBeatPoolMessages);
        }
        return true;

    }

    public synchronized boolean addRoundChangeMessage(int subEpoch, String messenger) {
        if (this.roundChangeMessagePool.get(subEpoch) != null) {
            this.roundChangeMessagePool.get(subEpoch).add(messenger);
        } else {
            CopyOnWriteArrayList<String> roundChangePoolMessages = new CopyOnWriteArrayList<String>();
            roundChangePoolMessages.add(messenger);
            this.roundChangeMessagePool.put(subEpoch, roundChangePoolMessages);
        }
        return true;
    }

    public synchronized boolean addFinaliseMessage(PartialBlock partialBlock) {

        if (this.finaliseMessagePool.get(partialBlock.getMajorBlocknumber()) != null) {
            this.finaliseMessagePool.get(partialBlock.getMajorBlocknumber()).add(partialBlock.getProposer());
        } else {
            CopyOnWriteArrayList<String> finalisePoolMessages = new CopyOnWriteArrayList<String>();
            finalisePoolMessages.add(partialBlock.getProposer());
            this.finaliseMessagePool.put(partialBlock.getMajorBlocknumber(), finalisePoolMessages);
        }
        return true;

    }

    public synchronized boolean addBlockMessage(Block block) {

        if (this.blockMessagePool.get(block.getBlocknumber()) != null) {
            this.blockMessagePool.get(block.getBlocknumber()).add(block.getProposer());
        } else {
            CopyOnWriteArrayList<String> blockPoolMessages = new CopyOnWriteArrayList<String>();
            blockPoolMessages.add(block.getProposer());
            this.blockMessagePool.put(block.getBlocknumber(), blockPoolMessages);
        }
        return true;

    }

    public synchronized boolean addQuorumMessage(QuorumMessage quorumMessage) {
        if (quorumMessage.type.equals("QUORUM")) {
            if (this.quorumMessagePool.get(quorumMessage.getProposedSubEpoch()) != null) {
                this.quorumMessagePool.get(quorumMessage.getProposedSubEpoch()).add(quorumMessage.getMessageSender());
            } else {
                CopyOnWriteArrayList<String> quorumPoolMessages = new CopyOnWriteArrayList<String>();
                quorumPoolMessages.add(quorumMessage.getMessageSender());
                this.quorumMessagePool.put(quorumMessage.getProposedSubEpoch(), quorumPoolMessages);
            }
            return true;
        }
        return false;
    }

    // For PROPOSE // CMMIT // ROUNDHCHANGE
    public synchronized boolean existingConsensusMessage(Message message) {
        if (message.type.equals("PROPOSE")) {
            return this.proposeMessagePool.containsKey(message.getBlockHash())
                    && this.proposeMessagePool.get(message.getBlockHash()).contains(message.getMessageSender());
        } else if (message.type.equals("COMMIT")) {
            return this.commitMessagePool.containsKey(message.getBlockHash())
                    && this.commitMessagePool.get(message.getBlockHash()).contains(message.getMessageSender());
        } else if (message.type.equals("ROUNDCHANGE")) {
            return this.roundChangeMessagePool.containsKey(message.getRound())
                    && this.roundChangeMessagePool.get(message.getRound()).contains(message.getMessageSender());
        } else {
            return false;
        }
    }

    public synchronized boolean existingProposeMessage(PartialBlock partialBlock) {

        return this.proposeMessagePool.containsKey(partialBlock.getPartialBlockHash())
                && this.proposeMessagePool.get(partialBlock.getPartialBlockHash()).contains(partialBlock.getProposer());
    }

    public synchronized boolean existingPrivacyMessage(String txHash, String messenger, String type) {
        if (type == "REQUEST") {
            return this.privacyRequestMessagePool.containsKey(txHash)
                    && this.privacyRequestMessagePool.get(txHash).contains(messenger);
        } else if (type == "RESPONSE") {
            return this.privacyResponseMessagePool.containsKey(txHash)
                    && this.privacyResponseMessagePool.get(txHash).contains(messenger);
        }
        return false;

    }

    public synchronized boolean addPrivacyMessage(String txHash, String messenger, String type) {
        if (type == "REQUEST") {
            if (this.privacyRequestMessagePool.get(txHash) != null) {
                this.privacyRequestMessagePool.get(txHash).add(messenger);
            } else {
                CopyOnWriteArrayList<String> privacyRequestMessages = new CopyOnWriteArrayList<String>();
                privacyRequestMessages.add(messenger);
                this.privacyRequestMessagePool.put(txHash, privacyRequestMessages);
            }
            return true;
        } else if (type == "RESPONSE") {
            if (this.privacyResponseMessagePool.get(txHash) != null) {
                this.privacyResponseMessagePool.get(txHash).add(messenger);
            } else {
                CopyOnWriteArrayList<String> privacyResponseMessages = new CopyOnWriteArrayList<String>();
                privacyResponseMessages.add(messenger);
                this.privacyResponseMessagePool.put(txHash, privacyResponseMessages);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean existingHeartBeatMessage(int round, String messenger) {

        return this.heartBeatMessagePool.containsKey(round)
                && this.heartBeatMessagePool.get(round).contains(messenger);
    }

    public synchronized boolean existingCommitMessage(PartialBlock partialBlock, String messenger) {

        return this.commitMessagePool.containsKey(partialBlock.getPartialBlockHash())
                && this.commitMessagePool.get(partialBlock.getPartialBlockHash()).contains(messenger);
    }

    public synchronized boolean existingRoundChangeMessage(int blockheight, String messenger) {
        return this.roundChangeMessagePool.containsKey(blockheight)
                && this.roundChangeMessagePool.get(blockheight)
                        .contains(messenger);
    }

    // Only one message is necessary for Full block Message
    public synchronized boolean existingBlockMessage(Block block) {
        return this.blockMessagePool.containsKey(block.getBlocknumber())
                && this.blockMessagePool.get(block.getBlocknumber())
                        .contains(block.getProposer());
    }

    public synchronized boolean existingFinaliseMessage(PartialBlock partialBlock) {
        return this.finaliseMessagePool.containsKey(partialBlock.getMajorBlocknumber())
                && this.finaliseMessagePool.get(partialBlock.getMajorBlocknumber())
                        .contains(partialBlock.getProposer());
    }

    // SubEpoch, MessageSender
    public synchronized boolean existingQuorumMessage(QuorumMessage quorumMessage) {
        return this.quorumMessagePool.containsKey(quorumMessage.getProposedSubEpoch())
                && this.quorumMessagePool.get(quorumMessage.getProposedSubEpoch())
                        .contains(quorumMessage.getMessageSender());
    }

    public synchronized void clearAllMessagePool() {
        this.proposeMessagePool = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();
        this.commitMessagePool = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();
        this.roundChangeMessagePool = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();
        this.blockMessagePool = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();
        this.finaliseMessagePool = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<String>>();

    }

    public synchronized int getSizeofPrepareMessagePoolForBlockHash(String blockhash) {
        if (this.proposeMessagePool.containsKey(blockhash)) {
            return this.proposeMessagePool.get(blockhash).size();
        }
        return 0;
    }

    public synchronized int getSizeofCommitMessagePoolForBlockHash(String blockhash) {
        if (this.commitMessagePool.containsKey(blockhash)) {
            return this.commitMessagePool.get(blockhash).size();
        }
        return 0;
    }

    public synchronized int getSizeofRoundChangeMessagePoolForBlockHash(int round) {
        if (this.roundChangeMessagePool.containsKey(round)) {
            return this.roundChangeMessagePool.get(round).size();
        }
        return 0;

    }

    public synchronized void clearAllMessagePoolForBlockHash(String blockhash) {
        this.proposeMessagePool.remove(blockhash);
        this.commitMessagePool.remove(blockhash);
    }

    public synchronized void clearProposeMessagePoolForBlockHash(String blockhash) {
        this.proposeMessagePool.remove(blockhash);

    }

    public synchronized void clearCommitMessagePoolForBlockHash(String blockhash) {
        this.commitMessagePool.remove(blockhash);

    }

    public synchronized void clearRoundChangeMessagePoolForBlockHash(Integer round) {
        this.roundChangeMessagePool.remove(round);

    }

    public synchronized boolean isValidMessage(Message message) {
        try {
            return CryptoUtil.verify(message.getMessageSender(), message.getMessagesignature(),
                    message.getBlockHash() + CryptoUtil.getHash(message.getType()));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean isValidQuorumMessage(QuorumMessage quorumMessage) {
        try {
            return CryptoUtil.verify(quorumMessage.getMessageSender(), quorumMessage.getMessagesignature(),
                    CryptoUtil.getHash(String.valueOf(quorumMessage.getNodeIndex() + quorumMessage.getSubEpoch())));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean isValidRoundChangeQuorumMessage(QuorumMessage quorumMessage) {
        try {
            return CryptoUtil.verify(quorumMessage.getMessageSender(), quorumMessage.getMessagesignature(),
                    CryptoUtil.getHash(String.valueOf(quorumMessage.getNodeIndex() + quorumMessage.getSubEpoch())));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean isValidBlockMessage(BlockMessage blockMessage) {
        try {
            return CryptoUtil.verify(blockMessage.getMessageSender(), blockMessage.getMessagesignature(),
                    CryptoUtil.getHash(
                            String.valueOf(blockMessage.getNodeIndex() + blockMessage.getRound())));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean isValidFinaliseMessage(FinaliseMessage finaliseMessage) {
        try {
            return CryptoUtil.verify(finaliseMessage.getMessageSender(), finaliseMessage.getMessagesignature(),
                    CryptoUtil.getHash(
                            String.valueOf(finaliseMessage.getNodeIndex() + finaliseMessage.getRound())));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public CAPSEOBFTDemMessagePool(UtilitarianCalculator utilitarianCalculator, QuorumStorage quorumStorage) {
        this.proposeMessagePool = new HashMap<String, CopyOnWriteArrayList<String>>();
        this.commitMessagePool = new HashMap<String, CopyOnWriteArrayList<String>>();
        this.commitMessagePoolIndexes = new HashMap<String, CopyOnWriteArrayList<Integer>>();
        this.blockMessagePool = new HashMap<Integer, CopyOnWriteArrayList<String>>();
        this.finaliseMessagePool = new HashMap<Integer, CopyOnWriteArrayList<String>>();
        this.roundChangeMessagePool = new HashMap<Integer, CopyOnWriteArrayList<String>>();
        this.quorumMessagePool = new HashMap<Integer, CopyOnWriteArrayList<String>>();
        this.heartBeatMessagePool = new HashMap<Integer, CopyOnWriteArrayList<String>>();
        this.privacyRequestMessagePool = new HashMap<String, CopyOnWriteArrayList<String>>();
        this.privacyResponseMessagePool = new HashMap<String, CopyOnWriteArrayList<String>>();
        this.utilitarianCalculator = utilitarianCalculator;
        this.quorumStorage = quorumStorage;
    }

}
