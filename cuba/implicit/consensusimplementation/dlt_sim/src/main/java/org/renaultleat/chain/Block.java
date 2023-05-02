package org.renaultleat.chain;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.renaultleat.consensus.UtilitarianBlockScore;
import org.renaultleat.consensus.Message;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;

// Contains 0..N Partial Block
public class Block {
    // After Finalise
    Timestamp blocktime;
    // After Finalise
    String previousblockhash;
    // After Finalise
    String hash;
    // After Finalise Message Sender
    String proposer;
    String signature;

    String proposalType;

    int originalproposernodeindex;

    int proposernodeindex;

    // After Finalise Message is performed
    boolean finalised;
    // After Finalise
    // Temporal Hashing State to be shared during finalise message
    ConcurrentMap<Integer, HashingState> temporalHashingState = new ConcurrentHashMap<Integer, HashingState>();

    // Before Finalise
    int blocknumber;
    // Before Finalise
    // Round Change
    int roundno;

    int epoch;

    int subepoch;

    boolean validity = true;

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getOriginalproposernodeindex() {
        return this.originalproposernodeindex;
    }

    public void setOriginalproposernodeindex(int originalproposernodeindex) {
        this.originalproposernodeindex = originalproposernodeindex;
    }

    public int getProposernodeindex() {
        return this.proposernodeindex;
    }

    public void setProposernodeindex(int proposernodeindex) {
        this.proposernodeindex = proposernodeindex;
    }

    public boolean getFinalised() {
        return this.finalised;
    }

    public int getRoundno() {
        return this.roundno;
    }

    public void setRoundno(int roundno) {
        this.roundno = roundno;
    }

    public int getSubepoch() {
        return this.subepoch;
    }

    public void setSubepoch(int subepoch) {
        this.subepoch = subepoch;
    }

    public boolean isValidity() {
        return this.validity;
    }

    public boolean getValidity() {
        return this.validity;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    int proposeCalculatedatindex;

    // Partial Block Containers
    // Partially to be filled during
    public volatile ConcurrentMap<Integer, PartialBlock> partialBlockMap = new ConcurrentHashMap<Integer, PartialBlock>();

    public Timestamp getBlocktime() {
        return this.blocktime;
    }

    public void setBlocktime(Timestamp blocktime) {
        this.blocktime = blocktime;
    }

    public String getProposalType() {
        return this.proposalType;
    }

    public void setProposalType(String inProposalType) {
        this.proposalType = inProposalType;
    }

    public int getEpoch() {
        return this.epoch;
    }

    public void setEpoch(int inEpoch) {
        this.epoch = inEpoch;
    }

    public void setProposeCalculatedatindex(int proposeCalculatedatindex) {
        this.proposeCalculatedatindex = proposeCalculatedatindex;
    }

    public int getProposeCalculatedatindex() {
        return this.proposeCalculatedatindex;
    }

    public int getSubEpoch() {
        return this.subepoch;
    }

    public void setSubEpoch(int inSubEpoch) {
        this.subepoch = inSubEpoch;
    }

    public boolean isFinalised() {
        return this.finalised;
    }

    public void setFinalised(boolean inFinalised) {
        this.finalised = inFinalised;
    }

    public ConcurrentMap<Integer, HashingState> getTemporalHashingState() {
        return this.temporalHashingState;
    }

    public void setTemporalHashingState(
            ConcurrentMap<Integer, HashingState> inTemporalHashingState) {
        this.temporalHashingState = inTemporalHashingState;
    }

    public int getRoundNo() {
        return this.roundno;
    }

    public void setRoundNo(int roundno) {
        this.roundno = roundno;
    }

    public int getOriginalProposerNodeIndex() {
        return this.originalproposernodeindex;
    }

    public void setOriginalProposerNodeIndex(int inProposerNodeIndex) {
        this.originalproposernodeindex = inProposerNodeIndex;
    }

    public int getProposerNodeIndex() {
        return this.proposernodeindex;
    }

    public void setProposerNodeIndex(int inProposerNodeIndex) {
        this.proposernodeindex = inProposerNodeIndex;
    }

    public String getPreviousblockhash() {
        return this.previousblockhash;
    }

    public void setPreviousblockhash(String previousblockhash) {
        this.previousblockhash = previousblockhash;
    }

    public ConcurrentMap<Integer, PartialBlock> getPartialBlockMap() {
        if (this.partialBlockMap != null) {
            return partialBlockMap;
        } else {
            return new ConcurrentHashMap<Integer, PartialBlock>();
        }

    }

    public void setPartialBlockMap(ConcurrentMap<Integer, PartialBlock> inPartialBlockMap) {
        this.partialBlockMap = inPartialBlockMap;
    }

    public String getProposer() {
        return this.proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getBlocknumber() {
        return this.blocknumber;
    }

    public void setBlocknumber(int blocknumber) {
        this.blocknumber = blocknumber;
    }

    public String getBlockDetails() {
        return "blocktime:" + this.blocktime.toString() + ";" + "previousblockhash:" + this.previousblockhash + ";"
                + "hash:" + this.hash + ";" + ";" + "partialblocklistize:"
                + ";"
                + "proposer" + this.proposer + ";" + "signature:" + this.signature + "blocknumber:"
                + String.valueOf(this.blocknumber);
    }

    public void setBlockHash(String inHash) {
        this.hash = inHash;
    }

    public String getBlockHash() {
        return this.hash;
    }

    public Block() {

    }

    public Block(Timestamp timestamp, String previousblockhash, String currentblockhash,
            ConcurrentMap<Integer, PartialBlock> partialBlockData,
            String blockproposer, String signature, int blocknum, int round, boolean finalised,
            ConcurrentMap<Integer, HashingState> temporalHashingState, int epoch, int subepoch,
            int proposeCalculatedatindex, String proposalType) {
        this.blocktime = timestamp;
        this.previousblockhash = previousblockhash;
        this.hash = currentblockhash;
        this.partialBlockMap = partialBlockData;
        this.proposer = blockproposer;
        this.signature = signature;
        this.blocknumber = blocknum;
        this.roundno = round;
        this.finalised = finalised;
        this.temporalHashingState = temporalHashingState;
        this.epoch = epoch;
        this.subepoch = subepoch;
        this.proposeCalculatedatindex = proposeCalculatedatindex;
        this.proposalType = proposalType;
    }

    public static Block generateGenesis() {
        // should be uniform
        return new Block(new Timestamp(System.currentTimeMillis()), "genesishash", "genesisblockhash",
                new ConcurrentHashMap<Integer, PartialBlock>(),
                "genesisproposer",
                "genesissignature",
                0, 0, true, new ConcurrentHashMap<Integer, HashingState>(), 0, 0, 0, CAPSEOBFTProperty.normalProposal);
    }

    public static Block createBlock(Block lastBlock, ConcurrentMap<Integer, PartialBlock> data, Wallet wallet,
            int roundno, boolean finalised,
            ConcurrentMap<Integer, HashingState> inTemporalHashingState, int epoch, int subepoch) {
        String lastHash = null;
        if (lastBlock != null) {

            if (lastBlock.isFinalised() == true) {
                lastHash = lastBlock.getBlockHash();
            } else {
                // Get the last temporal Hashing State
                ConcurrentMap<Integer, HashingState> lastBlockTemporalHashingState = lastBlock
                        .getTemporalHashingState();
                // Get the Last Index as corresponds to size
                if (lastBlockTemporalHashingState == null || lastBlockTemporalHashingState.size() == 0) {
                    lastHash = "genesislasthash";
                } else {
                    lastHash = lastBlockTemporalHashingState.get(lastBlockTemporalHashingState.size() - 1).getHash();
                }

            }
        } else {
            lastHash = "temporaryhash";
        }
        // Null Elements will be filled after FINALISATION

        Timestamp timeStampcurrent = null;
        // new Timestamp(System.currentTimeMillis());
        String proposer = null;
        // wallet.getPublicKey();
        String blockhash = null;
        // generateBlockHash(lastHash, data, proposer);

        // String proposer =
        // Base64.getEncoder().encodeToString(proposerkey.getEncoded());
        // Also Null
        String signature = null;
        int proposeCalculatedatindex = 0;
        // signBlockHash(blockhash, wallet);
        return new Block(timeStampcurrent, lastHash, blockhash, data, proposer, signature,
                roundno, roundno, finalised, inTemporalHashingState, epoch, subepoch,
                proposeCalculatedatindex, CAPSEOBFTProperty.normalProposal);
    }

    public void finaliseBlock(Block lastBlock, ConcurrentMap<Integer, PartialBlock> data, Wallet wallet,
            int roundno, boolean finalised,
            ConcurrentMap<Timestamp, Map<List<Integer>, String>> inTemporalHashingState, int proposeCalculatedatindex) {

        String lastblockHash = lastBlock.getBlockHash();
        Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
        String proposer = wallet.getPublicKey();
        String blockhash = generateBlockHash(lastblockHash, data, proposer);

        // String proposer =
        // Base64.getEncoder().encodeToString(proposerkey.getEncoded());
        String signature = signBlockHash(blockhash, wallet);

    }

    // Add Partial Block to state at a particular index
    public void addPartialBlock(PartialBlock partialBlock, Wallet wallet) {
        // Get the partial block Hash
        String partialblockHash = partialBlock.getPartialBlockHash();
        ConcurrentMap<Integer, HashingState> blocktemporalHashingState = this.temporalHashingState;
        List<Integer> partialBlockState = null;
        // If Exists a hashing state : Get the Partial block State
        // Else Create a new Partial Block State
        if (blocktemporalHashingState.size() > 0) {
            HashingState LastHashingState = blocktemporalHashingState.get(this.temporalHashingState.size() - 1);
            partialBlockState = new ArrayList<Integer>(LastHashingState.getPartialBlockState());
        } else {
            partialBlockState = new ArrayList<Integer>();
        }
        // Update Partial Block Map
        // Update Temporal Hashing State
        if (partialBlock.isFinalised() == true) {
            this.partialBlockMap.put(partialBlock.getMinorBlocknumber(), partialBlock);

            Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
            // Partial block Added at the particular state index
            partialBlockState.add(partialBlock.getMinorBlocknumber());
            String currentStateHash = generateEffectiveHash(partialblockHash, this.partialBlockMap);
            HashingState hashingState = new HashingState(timeStampcurrent, currentStateHash,
                    Wallet.getNodeproperty(),
                    partialBlockState);
            // Append the Hashing State
            blocktemporalHashingState.put(blocktemporalHashingState.size(), hashingState);
        }
    }

    public static String generateEffectiveHash(String currentPartialHash,
            ConcurrentMap<Integer, PartialBlock> partialBlockMap) {
        return CryptoUtil.getHash(currentPartialHash +
                generateCumulativePartialBlockHash(partialBlockMap));

    }

    public static String generateBlockHash(String lastblockHash,
            ConcurrentMap<Integer, PartialBlock> partialBlockMap, String proposer) {
        return CryptoUtil.getHash(lastblockHash +
                generateCumulativePartialBlockHash(partialBlockMap) + proposer);

    }

    public static String generateBlockHash(Block inblock) {
        return CryptoUtil.getHash(inblock.getPreviousblockhash() +
                generateCumulativePartialBlockHash(inblock.getPartialBlockMap()) + inblock.getProposer());

    }

    public String getSignedBlockHash(Wallet wallet) {
        return wallet.signData(CryptoUtil.getHash(this.getPreviousblockhash() +
                generateCumulativePartialBlockHash(this.getPartialBlockMap()) + this.getProposer()));

    }

    public String getSignedMaliciousBlockHash(Wallet wallet) {
        return wallet.signData(CryptoUtil.getHash("MALICIOUS" +
                generateCumulativePartialBlockHash(this.getPartialBlockMap()) + this.getProposer()));

    }

    public static String generateCumulativePartialBlockHash(ConcurrentMap<Integer, PartialBlock> data) {
        String cumulativeHash = "";
        for (int i = 0; i < CAPSEOBFTProperty.getTotalQuorums(); i++) {
            if (data.containsKey(i)) {
                cumulativeHash += data.get(i).getPartialBlockHash();
            }
        }
        return CryptoUtil.getHash(cumulativeHash);
    }

    public static String signBlockHash(String blockHash, Wallet wallet) {
        return wallet.signData(blockHash);

    }

    public static boolean verifyBlock(Block block) {
        try {
            return CryptoUtil.verify(block.getProposer(), block.getSignature(), block.getBlockHash());
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verifyProposer(Block block, String proposer) {
        return block.getProposer().equals(proposer);
    }

    @Override
    public String toString() {

        for (Entry<Integer, PartialBlock> partialBlockEntry : this.partialBlockMap.entrySet()) {
            // System.out.println("partialblockkey" + partialBlockEntry.getKey());
            // System.out.println("partialblock" + partialBlockEntry.getValue().toString());
        }

        return "{" +
                " blocktime='" + getBlocktime() + "'" +
                ", previousblockhash='" + getPreviousblockhash() + "'" +
                ", hash='" + getBlockHash() + "'" +
                ", proposer='" + getProposer() + "'" +
                ", signature='" + getSignature() + "'" +
                ", proposernodeindex='" + getProposerNodeIndex() + "'" +
                ", finalised='" + isFinalised() + "'" +
                ", temporalHashingState='" + getTemporalHashingState() + "'" +
                ", blocknumber='" + getBlocknumber() + "'" +
                ", roundno='" + getRoundNo() + "'" +
                ", epoch='" + getEpoch() + "'" +
                ", subepoch='" + getSubEpoch() + "'" +
                ", proposeCalculatedatindex='" + getProposeCalculatedatindex() + "'" +
                "}";
    }

}
