package org.renaultleat.chain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.renaultleat.consensus.CAPSEOBFTDemMessagePool;
import org.renaultleat.consensus.QuorumMessage;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.CAPSEOHashPointProperty;
import org.renaultleat.properties.NodeProperty;

public class Blockchain {

    public volatile AtomicInteger roundCounter = new AtomicInteger(1);

    public volatile AtomicInteger partialblockProposeCounter = new AtomicInteger(1);

    public volatile AtomicInteger partialblockSubEpochCounter = new AtomicInteger(1);

    // Intermediate Counter
    // public volatile AtomicInteger epochCounter = new AtomicInteger(1);

    // Intermediate Counter
    // public volatile AtomicInteger quorumsubepochCounter = new AtomicInteger(1);

    // To check at the moment of the
    // For Quorum Proposal it is better to look at the chain after finalisation of
    // chain as there would be problem if quorum changes
    // Update after the BLOCK PHASE
    public volatile AtomicInteger finalEpochCounter = new AtomicInteger(1);

    // For Quorum Proposal it is better to look at the chain after finalisation of
    // chain as there would be problem if quorum changes
    // Update after the BLOCK PHASE
    public volatile AtomicInteger finalSubEpochCounter = new AtomicInteger(1);

    // Considering already genesis block is stored at index
    public volatile AtomicInteger blockChainIndexCounter = new AtomicInteger(1);

    QuorumStorage quorumStorage;

    public volatile CopyOnWriteArrayList<Integer> blockIds = new CopyOnWriteArrayList<Integer>();

    Validator validator;

    NonValidator nonValidator;

    CopyOnWriteArrayList<String> validators;

    CopyOnWriteArrayList<String> nonValidators;

    // We create two storage for the chain because:
    // 1) {{{{{{{{{{{{{{{{{{{{{{{CHAIN:}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
    // For Getting the Quorum Proposer, Utilitarian Proposer, Block Proposer etc...
    // For STABILITY and the exact State Machine Only Final Update on the Chain:
    // During BLOCK PHASE & ROUND CHANGE PHASE

    volatile NavigableMap<Integer, Block> chain;

    // 2) {{{{{{{{{{{{{{{{{{{EPHEMERAL CHAIN:}}}}}}}}}}}}}}}}}}}
    // To perform consensus, partial blocks and other transitory business.
    // During Other Consensus Phase
    // Also Update Ephmeral during BLOCK PHASE

    volatile NavigableMap<Integer, Block> ephemeralchain;

    public CopyOnWriteArrayList<String> getValidators() {
        return this.validators;
    }

    public void setNonValidators(CopyOnWriteArrayList<String> nonValidators) {
        this.nonValidators = nonValidators;
    }

    public CopyOnWriteArrayList<String> getNonValidators() {
        return this.nonValidators;
    }

    public void setValidators(CopyOnWriteArrayList<String> validators) {
        this.validators = validators;
    }

    public void setEphemeralChain(NavigableMap<Integer, Block> ephemeralchain) {
        this.ephemeralchain = ephemeralchain;
    }

    public SortedMap<Integer, Block> getEphemeralChain() {
        return this.ephemeralchain;
    }

    public void setChain(NavigableMap<Integer, Block> chain) {
        this.chain = chain;
    }

    public SortedMap<Integer, Block> getChain() {
        return this.chain;
    }

    // Update Round After each block finalisation
    public void setRoundCounter(int newRoundCounter) {
        this.roundCounter.set(newRoundCounter);
    }

    // Update Round After each block finalisation
    public void incrementRoundCounter() {
        this.roundCounter.incrementAndGet();
    }

    // Update Round After each block finalisation
    public int getRoundCounter() {
        return this.roundCounter.get();
    }

    // Get the final Blockchain Index Counter
    public int getBlockChainIndexCounter() {
        return this.blockChainIndexCounter.get();
    }

    // Update Epoch After certain block level
    /*
     * public void setEpochCounter(int newEpochCounter) {
     * this.epochCounter.set(newEpochCounter);
     * }
     */

    // Update Epoch After certain block level
    /*
     * public void incrementEpochCounter() {
     * this.epochCounter.incrementAndGet();
     * }
     */

    // Get Epoch After certain block level

    // FINAL///

    // Update Epoch After certain block level
    public void setFinalEpochCounter(int newFinalEpochCounter) {
        this.finalEpochCounter.set(newFinalEpochCounter);
    }

    // Update Epoch After certain block level
    public void incrementFinalEpochCounter() {
        this.finalEpochCounter.incrementAndGet();
    }

    // Get Epoch After certain block level
    public int getFinalEpochCounter() {
        return this.finalEpochCounter.get();
    }

    // Update Epoch After certain block level
    public void setFinalSubEpochCounter(int newFinalSubEpochCounter) {
        this.finalSubEpochCounter.set(newFinalSubEpochCounter);
    }

    // Update Epoch After certain block level
    public void incrementFinalSubEpochCounter() {
        this.finalSubEpochCounter.incrementAndGet();
    }

    // Get Epoch After certain block level
    public int getPartialBlockSubEpochCounter() {
        return this.partialblockSubEpochCounter.get();
    }

    // Get Epoch After certain block level
    public int getFinalSubEpochCounter() {
        return this.finalSubEpochCounter.get();
    }

    // Update Epoch After certain block level
    public void setPartialblockProposeCounter(int newPartialblockProposeCounter) {
        this.partialblockProposeCounter.set(newPartialblockProposeCounter);
    }

    // Update Epoch After certain block level
    public void incrementPartialblockProposeCounter() {
        this.partialblockProposeCounter.incrementAndGet();
    }

    // Get Epoch After certain block level
    public int getPartialblockProposeCounter() {
        return this.partialblockProposeCounter.get();
    }

    public Blockchain() {

    }

    public synchronized Block createBlocknandAddtoEphemeral(Wallet wallet, int round) {
        ConcurrentMap<Integer, PartialBlock> data = new ConcurrentHashMap<Integer, PartialBlock>();
        ConcurrentMap<Integer, HashingState> inTemporalHashingState = new ConcurrentHashMap<Integer, HashingState>();
        Block block = null;
        // Empty Block a certain index
        if (this.ephemeralchain.containsKey(round - 1)) {
            block = Block.createBlock(this.ephemeralchain.get(round - 1), data, wallet,
                    round, false, inTemporalHashingState, this.getFinalEpochCounter(), this.getFinalSubEpochCounter());
        } else {
            block = Block.createBlock(null, data, wallet,
                    round, false, inTemporalHashingState, this.getFinalEpochCounter(), this.getFinalSubEpochCounter());
        }

        // Increment Round Counter Auto
        // this.incrementRoundCounter();
        this.addBlockToEphmeralChain(block);
        return block;
    }

    public synchronized void addBlockToEphmeralChain(Block block) {
        this.ephemeralchain.put(block.getBlocknumber(), block);

        int blockspassedforcurrentepoch = this.finalEpochCounter.get() * CAPSEOBFTProperty.epochthreshold;
        int blockspassedforcurrentsubepoch = this.finalSubEpochCounter.get() * CAPSEOBFTProperty.subepochthreshold;
        // if (this.ephemeralchain.size() >= blockspassedforcurrentepoch) {
        // this.finalEpochCounter.incrementAndGet();
        // }
        // if (this.ephemeralchain.size() >= blockspassedforcurrentsubepoch) {
        // //System.out.println("addBlockToEphmeralChain");
        // this.finalSubEpochCounter.incrementAndGet();
        // }

    }

    public synchronized PartialBlock createPartialBlock(Block block, Wallet wallet,
            CopyOnWriteArrayList<Transaction> tempTransactionList, int round, int blockHeight, int blockproposer,
            int rivalblockproposer,
            int proposeCalculatedatindex, int subepoch, String inProposalType) {

        // Get the last Hash of the block to attach
        ConcurrentMap<Integer, HashingState> blockTemporalHashingState = block.getTemporalHashingState();
        String lastHash = null;
        if (blockTemporalHashingState.size() == 0) {
            lastHash = "genesispartial";
        } else {
            lastHash = blockTemporalHashingState.get(blockTemporalHashingState.size() - 1).getHash();
        }

        int quorumId = quorumStorage.getQuorumId();

        /*
         * for (Entry<Integer, Block> EphemeralEntry : this.ephemeralchain.entrySet()) {
         * //System.out.println("ephemheralkey" + EphemeralEntry.getKey());
         * //System.out.println("ephemheral" + EphemeralEntry.getValue());
         * Block bvlock = EphemeralEntry.getValue();
         * for (Entry<Integer, PartialBlock> partialBlockEntry :
         * bvlock.partialBlockMap.entrySet()) {
         * //System.out.println("partialblockkey" + partialBlockEntry.getKey());
         * //System.out.println("partialblock" + partialBlockEntry.getValue());
         * }
         * }
         * 
         * //System.out.println("finalchain" + this.chain.size());
         * 
         * //System.out.println("finalsubepoch" + this.finalSubEpochCounter.get());
         */

        // System.out.println("Quorum Storage" + quorumStorage.toString());

        // Get the oringal Node Index of the rival Proposer
        int originalIndexofRivalProposer = quorumStorage.getPartialBlockProposerOriginalIndex(subepoch, quorumId,
                rivalblockproposer);

        // Add the
        PartialBlock partialBlock = PartialBlock.createPartialBlock(
                lastHash, tempTransactionList, wallet,
                round, quorumId, blockproposer, blockHeight, rivalblockproposer,
                proposeCalculatedatindex, subepoch, wallet.nodeproperty, originalIndexofRivalProposer, quorumId,
                inProposalType);
        // Increment Round Counter Auto
        // this.incrementRoundCounter();
        // this.addBlock(block);
        return partialBlock;
    }

    /**
     * Get the latest block hash + Round as IBFT is dependent on Rounds and get the
     * unicode value at position 2
     * Then get the modulo value with the total nodes
     * 
     * @return
     */
    // IBFT Changed to the validators and not total nodes
    public ProposerMessage getProposer() {
        int totalvalidators = Integer.valueOf(NodeProperty.validators);

        int inChainIndex = this.chain.lastKey();
        Block blockValue = this.chain.get(inChainIndex);
        int inNodeIndex = CryptoUtil.getHash(blockValue.getBlockHash())
                .codePointAt(2) % totalvalidators;
        // return validators.get(initialIndex);

        /*
         * if (!validators.get(index).equals(this.chain.get(chain.size() -
         * 1).getBlockProposer())) {
         * return validators.get(index);
         * } else {
         * int newindex = this.chain.get(chain.size() - 1).getBlockHash().codePointAt(3)
         * % totalnodes;
         * return validators.get(newindex);
         * }
         */

        return new ProposerMessage(inNodeIndex, inChainIndex);
    }

    // Supposedly it should be at genesis block
    public ProposerMessage getInitialQuorumProposer() {
        int totalEffectiveMembers = Integer.valueOf(quorumStorage.getTotalEffectiveMembers());
        int inSubEpoch = CAPSEOBFTProperty.genesisindex;
        String effectiveHash = String.valueOf(inSubEpoch);
        // if (this.chain.containsKey(inChainIndex)) {
        int inNodeIndex = CryptoUtil.getHash(effectiveHash)
                .codePointAt(CAPSEOHashPointProperty.quorumproposercodePoint) % totalEffectiveMembers;
        // System.out.println(inNodeIndex + "Genesis Proposer");
        return new ProposerMessage(inNodeIndex, inSubEpoch);
        // }
        // High Value
        // return new ProposerMessage(99999, 99999);

    }

    // Round Change Proposer
    // Should be a definite value of Blockchain Height
    // Blockchain Height to be decided during the Round Change
    // Agreement should be on the blockHeight
    public ProposerMessage getRoundChangeQuorumProposer(int proposedSubEpoch) {
        int previousSubEpoch = proposedSubEpoch - 1;
        int totalEffectiveMembers = quorumStorage.getTotalEffectiveMembers();
        String effectiveHash = String.valueOf(proposedSubEpoch);
        int inNodeIndex = CryptoUtil.getHash(effectiveHash)
                .codePointAt(CAPSEOHashPointProperty.roundchangequorumproposercodePoint) % totalEffectiveMembers;
        return new ProposerMessage(inNodeIndex, proposedSubEpoch);

        /*
         * if (!validators.get(index).equals(this.chain.get(chain.size() -
         * 1).getBlockProposer())) {
         * return validators.get(index);
         * } else {
         * int newindex = this.chain.get(chain.size() - 1).getBlockHash().codePointAt(3)
         * % totalnodes;
         * return validators.get(newindex);
         * }
         */
    }

    // Get the Proposer for a Quorum at a partiular sub epoch
    public boolean isValidQuorumProposer(QuorumMessage quorumMessage) {
        int proposedSubEpoch = quorumMessage.getProposedSubEpoch();
        int previousSubEpoch = proposedSubEpoch - 1;
        if (proposedSubEpoch == 1) {
            return true;
        }
        int totalEffectiveMembers = Integer
                .valueOf(quorumStorage.getQuorumMessageMap().get(previousSubEpoch).getTotalEffectiveMembers());

        // int indexBlockchain = quorumMessage.getBlockIndex();
        // if (this.chain.containsKey(indexBlockchain)) {
        int index = CryptoUtil.getHash(String.valueOf(proposedSubEpoch))
                .codePointAt(CAPSEOHashPointProperty.quorumproposercodePoint) % totalEffectiveMembers;
        int originalIndex = quorumStorage.getQuorumMessageMap().get(previousSubEpoch).getWalletMapping().get(index);
        if (originalIndex == quorumMessage.getNodeIndex()) {
            return true;
        }
        // }
        return false;
    }

    // Get the Proposer for a Quorum at a partiular sub epoch
    // Make index at a particular sub epoch
    // Since it is difficult to get the particular blok index hash
    public ProposerMessage getQuorumProposer(int proposedSubEpoch) {
        int previousSubEpoch = proposedSubEpoch - 1;
        int totalEffectiveMembers = Integer
                .valueOf(quorumStorage.getQuorumMessageMap().get(previousSubEpoch).getTotalEffectiveMembers());
        int inChainIndex = this.finalSubEpochCounter.get() * CAPSEOBFTProperty.subepochthreshold;
        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        String effectiveHash = String.valueOf(proposedSubEpoch);
        // if (this.chain.containsKey(inChainIndex)) {
        // System.out.println("QuorumProposerinChainIndex" + inChainIndex);
        int inNodeIndex = CryptoUtil.getHash(effectiveHash)
                .codePointAt(CAPSEOHashPointProperty.quorumproposercodePoint) % totalEffectiveMembers;
        // System.out.println("BefoeQuorumProposerinChainIndex" + inNodeIndex);
        return new ProposerMessage(inNodeIndex, proposedSubEpoch);
        // }
        // High Value
        // return new ProposerMessage(99999, 99999);

    }

    // Get the Proposer for a Quorum at a partiular sub epoch
    public boolean isValidRoundChangeQuorumProposer(QuorumMessage quorumMessage) {
        int proposedSubEpoch = quorumMessage.getProposedSubEpoch();
        int previousSubEpoch = proposedSubEpoch - 1;
        int totalEffectiveMembers = Integer
                .valueOf(quorumStorage.getQuorumMessageMap().get(previousSubEpoch).getTotalEffectiveMembers());
        String effectiveHash = String.valueOf(quorumMessage.getProposedSubEpoch());
        // if (this.chain.containsKey(effectiveindex)) {
        int index = CryptoUtil.getHash(effectiveHash)
                .codePointAt(CAPSEOHashPointProperty.roundchangequorumproposercodePoint) % totalEffectiveMembers;
        if (index == quorumMessage.getNodeIndex()) {
            return true;
        }
        // }
        return false;
    }

    public ProposerMessage getFullBlockProposer(int majorIndex) {
        int totaleffectivemmembers = Integer.valueOf(quorumStorage.getTotalEffectiveMembers());
        // System.out.println("SIZE" + this.chain.size());
        // System.out.println("CHAINVALUE" + this.chain.toString());
        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        String effectivehash = String.valueOf(majorIndex);

        int inNodeIndex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.fullblockproposercodePoint) % totaleffectivemmembers;
        return new ProposerMessage(inNodeIndex, majorIndex);

    }

    public ProposerMessage getFullBlockFulFiller(int majorIndex) {
        int totaleffectivemmembers = Integer.valueOf(quorumStorage.getTotalEffectiveMembers());
        // System.out.println("SIZE" + this.chain.size());
        // System.out.println("CHAINVALUE" + this.chain.toString());
        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        String effectivehash = String.valueOf(majorIndex);
        // System.out.println("EffectiveHash" + CryptoUtil.getHash(effectivehash));
        int inNodeIndex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.fullblockfulfillercodePoint) % totaleffectivemmembers;
        return new ProposerMessage(inNodeIndex, majorIndex);

    }

    // Simplify for having a better proposal way
    // Form Hash of Partial Block: Major Block No + Minor Block No
    public ProposerMessage getPartialBlockFulfiller(int majorIndex, int minorIndex) {
        int currentmembersinsideurquorum = Integer.valueOf(quorumStorage.getCurrentmembersinsideurquorum());
        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        // if (this.chain.containsKey(inChainIndex)) {
        String effectivehash = String.valueOf(majorIndex + minorIndex);
        int inNodeIndex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.partialblockfulfillercodePoint) % currentmembersinsideurquorum;
        return new ProposerMessage(inNodeIndex, majorIndex);
        // }
        // return new ProposerMessage(99999, 99999);

    }

    // Simplify for having a better proposal way
    // Form Hash of Partial Block: Major Block No + Minor Block No
    public ProposerMessage getPartialBlockProposer(int majorIndex, int minorIndex) {
        int currentmembersinsideurquorum = Integer.valueOf(quorumStorage.getCurrentmembersinsideurquorum());
        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        // if (this.chain.containsKey(inChainIndex)) {
        String effectivehash = String.valueOf(majorIndex + minorIndex);
        int inNodeIndex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.partialblockproposercodePoint) % currentmembersinsideurquorum;
        return new ProposerMessage(inNodeIndex, majorIndex);
        // }
        // return new ProposerMessage(99999, 99999);

    }

    public ProposerMessage getRivalPartialBlockProposer(int majorIndex, int minorIndex) {
        int currentmembersinsideurquorum = Integer.valueOf(quorumStorage.getCurrentmembersinsideurquorum());

        // int inChainIndex = this.chain.lastKey();
        // Block blockValue = this.chain.get(inChainIndex);
        // if (this.chain.containsKey(inChainIndex)) {
        String effectivehash = String.valueOf(majorIndex + minorIndex);
        int inNodeIndex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.rivalpartialblockproposercodePoint)
                % currentmembersinsideurquorum;
        return new ProposerMessage(inNodeIndex, majorIndex);
        // }
        // return new ProposerMessage(99999, 99999);

    }

    // Can be either Proper Block Proposer or Rival Block Proposer
    public boolean isValidPartialBlockProposer(PartialBlock partialBlock) {
        // System.out.println("validSubepoch" + partialBlock.getSubEpoch());
        // System.out.println("validSubepochQuoum" + quorumStorage.toString());
        int quorummembers = Integer
                .valueOf(quorumStorage.getMembersofaQuorum(partialBlock.getSubEpoch(), partialBlock.getQuorumId()));
        // int mainchaineffectiveindex = partialBlock.getProposeCalculatedatindex();
        String effectivehash = String.valueOf(partialBlock.getMajorBlocknumber() + partialBlock.getMinorBlocknumber());
        // if (this.chain.containsKey(mainchaineffectiveindex)) {
        int index = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.partialblockproposercodePoint) % quorummembers;
        int rivalindex = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.rivalpartialblockproposercodePoint) % quorummembers;

        if (partialBlock.getProposerNodeIndex() == index || partialBlock.getProposerNodeIndex() == rivalindex) {
            return true;
            // }
        }
        return false;
    }

    // Can be either Proper Block Proposer or Rival Block Proposer
    public boolean isValidPartialBlockFulFiller(PartialBlock partialBlock) {
        // System.out.println("validSubepoch" + partialBlock.getSubEpoch());
        // System.out.println("validSubepochQuoum" + quorumStorage.toString());
        int quorummembers = Integer
                .valueOf(quorumStorage.getMembersofaQuorum(partialBlock.getSubEpoch(), partialBlock.getQuorumId()));
        // int mainchaineffectiveindex = partialBlock.getProposeCalculatedatindex();
        String effectivehash = String.valueOf(partialBlock.getMajorBlocknumber() + partialBlock.getMinorBlocknumber());
        // if (this.chain.containsKey(mainchaineffectiveindex)) {
        int index = CryptoUtil.getHash(effectivehash)
                .codePointAt(CAPSEOHashPointProperty.partialblockfulfillercodePoint) % quorummembers;

        if (partialBlock.getProposerNodeIndex() == index) {
            return true;
            // }
        }
        return false;
    }

    // Can be either Proper Block Proposer or Rival Block Proposer
    public boolean isValidBlockProposer(Block block) {
        int totaleffectivemmembers = Integer
                .valueOf(this.quorumStorage.getQuorumMessageMap().get(block.getSubEpoch()).getTotalEffectiveMembers());
        // int mainchaineffectiveindex = block.getProposeCalculatedatindex();
        // if (this.chain.containsKey(mainchaineffectiveindex)) {
        String hash = String.valueOf(block.getBlocknumber());
        // block.getTemporalHashingState().get(CAPSEOBFTProperty.getTotalQuorums() -
        // 1).getHash();
        int index = CryptoUtil.getHash(hash)
                .codePointAt(CAPSEOHashPointProperty.fullblockproposercodePoint) % totaleffectivemmembers;
        if (block.getProposerNodeIndex() == index) {

            return true;
            // }

        }

        return false;
    }

    // Can be either Proper Block Proposer or Rival Block Proposer
    public boolean isValidBlockFulfiller(Block block) {
        int totaleffectivemmembers = Integer
                .valueOf(this.quorumStorage.getQuorumMessageMap().get(block.getSubEpoch()).getTotalEffectiveMembers());
        // int mainchaineffectiveindex = block.getProposeCalculatedatindex();
        // if (this.chain.containsKey(mainchaineffectiveindex)) {
        String hash = String.valueOf(block.getBlocknumber());
        // block.getTemporalHashingState().get(CAPSEOBFTProperty.getTotalQuorums() -
        // 1).getHash();
        int index = CryptoUtil.getHash(hash)
                .codePointAt(CAPSEOHashPointProperty.fullblockfulfillercodePoint) % totaleffectivemmembers;
        if (block.getProposerNodeIndex() == index) {

            return true;
            // }

        }

        return false;
    }

    /**
     * Get the next block expected as round no
     */

    public boolean isValidBlock(Block inblock) {
        // System.out.println("1" + inblock.getBlockHash());
        // System.out.println("2" + Block.generateBlockHash(inblock));
        // System.out.println("3" + Block.verifyBlock(inblock));
        // System.out.println("4" + isValidBlockProposer(inblock));
        // System.out.println("5" + inblock.getBlocknumber());
        // Block Hash verification of current

        if (Block.verifyBlock(inblock)
                && (isValidBlockProposer(inblock) || isValidBlockFulfiller(inblock))) {
            return true;
        }
        return true;

    }

    public boolean isReadyforPartialBlockValidity(PartialBlock inPartialblock) {
        // Hash Verification
        // Verify the Partial Block Proposer
        if (quorumStorage.quorumMessageMap.containsKey(inPartialblock.getSubEpoch())) {
            return true;
        }
        return false;

    }

    public boolean isReadyforBlockValidity(Block inblock) {
        // Hash Verification
        // Verify the Partial Block Proposer
        if (quorumStorage.quorumMessageMap.containsKey(inblock.getSubEpoch())) {
            return true;
        }
        return false;

    }

    public boolean isValidPartialBlock(PartialBlock inPartialblock) {
        // Hash Verification
        // Verify the Partial Block Proposer
        if (inPartialblock.getPartialBlockHash().equals(PartialBlock.generatePartialBlockHash(inPartialblock))
                && (isValidPartialBlockProposer(inPartialblock) || isValidPartialBlockFulFiller(inPartialblock))) {
            // System.out.println("PASS");
            return true;
        }
        // System.out.println("FAILED");
        return true;

    }

    // Used in Prepare and Commit Phase to check if the partial block proposed
    // position (major and minor) is already filled
    public boolean partialBlockFilled(PartialBlock inPartialblock) {

        int majorindex = inPartialblock.getMajorBlocknumber();
        int minorindex = inPartialblock.getMinorBlocknumber();
        // Finalised Chain
        if (this.ephemeralchain.containsKey(majorindex)
                && this.ephemeralchain.get(majorindex).getPartialBlockMap().containsKey(minorindex)) {
            PartialBlock partialBlock = this.ephemeralchain.get(majorindex).getPartialBlockMap().get(minorindex);
            return partialBlock.isFinalised();
        }
        // Ephemeral Chain
        // Not Needed as it can be a rival proposer
        /*
         * if (this.ephemeralchain.containsKey(majorindex)
         * && this.ephemeralchain.get(majorindex).getPartialBlockMap().containsKey(
         * minorindex)) {
         * return true;
         * }
         */
        return false;

    }

    public boolean finalisedPartialBlockFilled(PartialBlock inPartialblock) {

        int majorindex = inPartialblock.getMajorBlocknumber();
        int minorindex = inPartialblock.getMinorBlocknumber();
        // Finalised Chain
        if (this.ephemeralchain.containsKey(majorindex)
                && this.ephemeralchain.get(majorindex).getPartialBlockMap().containsKey(minorindex)) {
            PartialBlock partialBlock = this.ephemeralchain.get(majorindex).getPartialBlockMap().get(minorindex);
            return partialBlock.isFinalised();
        }
        // Ephemeral Chain
        // Not Needed as it can be a rival proposer
        /*
         * if (this.ephemeralchain.containsKey(majorindex)
         * && this.ephemeralchain.get(majorindex).getPartialBlockMap().containsKey(
         * minorindex)) {
         * return true;
         * }
         */
        return false;

    }

    // Used in Prepare and Commit Phase to check if the partial block proposed
    // position (major and minor) is already filled
    public boolean blockFilled(Block block) {

        int height = block.getBlocknumber();
        // Finalised Chain
        if (block.getProposalType() == CAPSEOBFTProperty.normalProposal) {
            return (this.chain.containsKey(height) && this.chain.get(height).isFinalised());
            // Aloow the block always if it is fullfillment
            // Allow only if the chain has not progressed since meaning next index
        } else if (block.getProposalType() == CAPSEOBFTProperty.normalFulfillment
                && !this.chain.containsKey(block.getBlocknumber() + 1)) {
            return false;
        }
        return false;

    }

    public Block finalisedFullBlock(Wallet wallet, int majorindex, int subEpoch, int proposeCalculatedatindex,
            int proposerIndex, String proposalType) {

        Block block = null;
        // Existing block
        if (this.ephemeralchain.containsKey(majorindex)) {
            block = this.ephemeralchain.get(majorindex);

            Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
            // System.out.println("finalisedmajorindex" + majorindex);
            String previousblockHash = "initialprevioushash";
            if (this.chain.containsKey(majorindex - 1) && this.chain.get(majorindex - 1).getBlockHash() != null) {
                previousblockHash = this.chain.get(majorindex - 1).getBlockHash();
            } else if (this.chain.containsKey(majorindex - 1)
                    && this.chain.get(majorindex - 1).getTemporalHashingState() != null) {
                ConcurrentMap<Integer, HashingState> lasttemporalHashing = this.chain.get(majorindex - 1)
                        .getTemporalHashingState();
                previousblockHash = lasttemporalHashing.get(lasttemporalHashing.size() - 1).getHash();
            }
            block.setBlocktime(timeStampcurrent);
            block.setPreviousblockhash(previousblockHash);
            block.setValidity(true);

            // block.setBlockHash(block.getTemporalHashingState().get(block.getTemporalHashingState().size()
            // - 1).getHash());
            block.setProposer(wallet.getPublicKey());
            block.setSubEpoch(subEpoch);
            block.setOriginalProposerNodeIndex(wallet.getNodeproperty());
            block.setProposerNodeIndex(proposerIndex);
            block.setFinalised(true);
            block.setProposeCalculatedatindex(proposeCalculatedatindex);
            block.setBlockHash(
                    Block.generateBlockHash(previousblockHash, block.getPartialBlockMap(), wallet.getPublicKey()));
            block.setSignature(block.getSignedBlockHash(wallet));
            block.setProposalType(proposalType);
            // Append the block to the main chain
            addBlock(block);
            return block;
        }
        return null;
    }

    public Block finalisedMaliciousFullBlock(Wallet wallet, int majorindex, int subEpoch, int proposeCalculatedatindex,
            int proposerIndex, String proposalType) {

        Block block = null;
        // Existing block
        if (this.ephemeralchain.containsKey(majorindex)) {
            block = this.ephemeralchain.get(majorindex);

            Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
            // System.out.println("finalisedmajorindex" + majorindex);
            String previousblockHash = "initialprevioushash";
            if (this.chain.containsKey(majorindex - 1) && this.chain.get(majorindex - 1).getBlockHash() != null) {
                previousblockHash = this.chain.get(majorindex - 1).getBlockHash();
            } else if (this.chain.containsKey(majorindex - 1)
                    && this.chain.get(majorindex - 1).getTemporalHashingState() != null) {
                ConcurrentMap<Integer, HashingState> lasttemporalHashing = this.chain.get(majorindex - 1)
                        .getTemporalHashingState();
                previousblockHash = lasttemporalHashing.get(lasttemporalHashing.size() - 1).getHash();
            }
            block.setBlocktime(timeStampcurrent);
            block.setPreviousblockhash(previousblockHash);
            block.setValidity(false);

            // block.setBlockHash(block.getTemporalHashingState().get(block.getTemporalHashingState().size()
            // - 1).getHash());
            block.setProposer(wallet.getPublicKey());
            block.setSubEpoch(subEpoch);
            block.setOriginalProposerNodeIndex(wallet.getNodeproperty());
            block.setProposerNodeIndex(proposerIndex);
            block.setFinalised(true);
            block.setProposeCalculatedatindex(proposeCalculatedatindex);
            block.setBlockHash(
                    Block.generateBlockHash(previousblockHash, block.getPartialBlockMap(), wallet.getPublicKey()));
            // Malicious Signature
            block.setSignature(block.getSignedMaliciousBlockHash(wallet));
            block.setProposalType(proposalType);
            // Append the block to the main chain
            addBlock(block);
            return block;
        }
        return null;
    }

    // Just Replace the Block in the ephemeral chain
    public void updatefinalisedEphemeralBlock(Block block) {
        // Existing block
        // if (this.ephemeralchain.containsKey(block.getBlocknumber())) {
        // block =
        this.ephemeralchain.put(block.getBlocknumber(), block);
        // }
    }

    // Return true when incrementing the subepoch is filled and new epoch is started
    // as it requires to initate the new Quorum
    public synchronized boolean addBlock(Block block) {
        this.chain.put(block.getBlocknumber(), block);
        // this.roundCounter.incrementAndGet();
        this.blockChainIndexCounter.incrementAndGet();
        // 1 for Genesis Block
        int blockspassedforcurrentepoch = (this.finalEpochCounter.get() * CAPSEOBFTProperty.epochthreshold) + 1;
        int blockspassedforcurrentsubepoch = (this.finalSubEpochCounter.get() * CAPSEOBFTProperty.subepochthreshold)
                + 1;
        if (this.chain.size() >= blockspassedforcurrentepoch) {
            // this.finalEpochCounter.incrementAndGet();
            return true;
        }
        if (this.chain.size() >= blockspassedforcurrentsubepoch) {
            // System.out.println("addBlock");
            // this.finalSubEpochCounter.incrementAndGet();
            return true;
        }
        return false;

    }

    public synchronized boolean isSubEpochFilled() {
        int blockspassedforcurrentepoch = (this.finalEpochCounter.get() * CAPSEOBFTProperty.epochthreshold);
        int blockspassedforcurrentsubepoch = (this.finalSubEpochCounter.get() * CAPSEOBFTProperty.subepochthreshold);
        if (this.chain.size() >= blockspassedforcurrentsubepoch) {
            // System.out.println("isSubEpochFilled");
            this.finalSubEpochCounter.incrementAndGet();
            return true;
        }

        if (this.chain.size() >= blockspassedforcurrentepoch) {
            this.finalEpochCounter.incrementAndGet();
            return true;
        }

        return false;
    }

    // Used during Round change as we pass to the new SubEpoch with the left over
    // blocks
    public synchronized void incrementAllEpochCountersforRoundChange() {
        // System.out.println("called round incremecremt");
        int blockspassedforcurrentepoch = this.finalEpochCounter.get() * CAPSEOBFTProperty.epochthreshold;
        if (this.chain.size() >= blockspassedforcurrentepoch) {
            this.finalEpochCounter.incrementAndGet();
        }

        this.finalSubEpochCounter.incrementAndGet();
        // Ephemeral Chain
        // int blockspassedforcurrentephemeralepoch = this.epochCounter.get() *
        // CAPSEOBFTProperty.epochthreshold;

        // if (this.ephemeralchain.size() >= blockspassedforcurrentephemeralepoch) {
        // this.epochCounter.incrementAndGet();
        // }
        // this.subepochCounter.incrementAndGet();

    }

    // Used in Finalised Phase to add the finalised partial block proposed
    // position at (major and minor) index
    // Boolean to signify all the partial blocks are received at the particular
    // major index
    public String addFinalisedPartialBlock(Wallet wallet, PartialBlock inPartialblock) {
        // System.out.println("adding finalised" + inPartialblock.toString());
        int majorindex = inPartialblock.getMajorBlocknumber();
        int minorindex = inPartialblock.getMinorBlocknumber();

        Block block = null;
        // Existing block
        if (this.ephemeralchain.containsKey(majorindex)) {
            block = this.ephemeralchain.get(majorindex);
        } else {
            // Create a new Block
            // Apends the block to ephemeral chain
            // increments round counter
            block = this.createBlocknandAddtoEphemeral(wallet, majorindex);
        }
        if (block == null) {
            System.out.println("BLOCK IS NULL at addFinalisedPartialBlock");
        }
        // Add the partial block created
        // Updates the Temporal Hash State
        block.addPartialBlock(inPartialblock, wallet);
        this.ephemeralchain.put(block.getBlocknumber(), block);
        // System.out.println("after add finalised" + inPartialblock.toString());
        // Total Partial Blocks should be equal to the number of Quorums
        // System.out.println(
        // "CHECK1FULLBLOCKINFINALISEhandler" +
        // this.ephemeralchain.get(majorindex).getPartialBlockMap().size()
        // + "num:" + block.getBlocknumber());
        // System.out.println("Total quorums" + CAPSEOBFTProperty.getTotalQuorums());
        // System.out.println("Partal Map size" + CAPSEOBFTProperty.getTotalQuorums());
        if (this.ephemeralchain.get(majorindex).getPartialBlockMap().size() == CAPSEOBFTProperty
                .getTotalQuorums()) {
            // System.out.println("tempoal" +
            // this.ephemeralchain.get(majorindex).getPartialBlockMap().toString());
            return this.ephemeralchain.get(majorindex).getTemporalHashingState()
                    .get(CAPSEOBFTProperty.getTotalQuorums() - 1).getHash();
        } else {
            return null;
        }
    }

    // Append Commit and Prepare Messages to a Block
    public void addUpdatedBlock(String hash, BlockPool blockPool, CAPSEOBFTDemMessagePool pbftMessagePool) {

        Block block = blockPool.getBlockforHash(hash);
        if (block != null) {
            if (!this.blockIds.contains(block.getBlocknumber())) {
                if (this.chain.get(this.chain.size() - 1).getBlocknumber() + 1 >= block.getBlocknumber()) {
                    // block.getPrepareMessages().addAll(pbftMessagePool.getPreparePool().get(hash));
                    // block.getCommitMessages().addAll(pbftMessagePool.getCommmitPool().get(hash));
                    // this.addBlock(block);
                    this.blockIds.add(block.getBlocknumber());

                }
            }
        }
    }

    public Blockchain(Validator validator, NonValidator nonValidator, QuorumStorage quorumStorage) {
        this.validator = validator;
        this.nonValidator = nonValidator;
        this.chain = new TreeMap<Integer, Block>();
        this.chain.put(0, Block.generateGenesis());
        this.ephemeralchain = new TreeMap<Integer, Block>();
        this.ephemeralchain.put(0, Block.generateGenesis());
        this.validators = new CopyOnWriteArrayList<String>();
        this.nonValidators = new CopyOnWriteArrayList<String>();
        this.quorumStorage = quorumStorage;
        try {
            this.validators.addAll(validator.generateAddresses());
            this.nonValidators.addAll(nonValidator.generateAddresses());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
