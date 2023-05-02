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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
the 
import org.renaultleat.consensus.Message;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.NodeProperty;

public class PartialBlock {

    Timestamp blocktime;
    String previoushash;
    String hash;
    String proposer;
    int QuorumId;

    // Index within Quorum
    int proposernodeindex;

    // Index within Quorum
    int rivalproposerindex;

    // Original Index
    int originalProposernodeindex;

    // Original Index
    int originalRivalProposernodeindex;

    String signature;

    String proposalType;

    int subepoch;

    boolean validity = true;

    public Timestamp getBlocktime() {
        return this.blocktime;
    }

    public void setBlocktime(Timestamp blocktime) {
        this.blocktime = blocktime;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getProposernodeindex() {
        return this.proposernodeindex;
    }

    public void setProposernodeindex(int proposernodeindex) {
        this.proposernodeindex = proposernodeindex;
    }

    public int getRivalproposerindex() {
        return this.rivalproposerindex;
    }

    public void setRivalproposerindex(int rivalproposerindex) {
        this.rivalproposerindex = rivalproposerindex;
    }

    public int getOriginalProposernodeindex() {
        return this.originalProposernodeindex;
    }

    public void setOriginalProposernodeindex(int originalProposernodeindex) {
        this.originalProposernodeindex = originalProposernodeindex;
    }

    public int getOriginalRivalProposernodeindex() {
        return this.originalRivalProposernodeindex;
    }

    public void setOriginalRivalProposernodeindex(int originalRivalProposernodeindex) {
        this.originalRivalProposernodeindex = originalRivalProposernodeindex;
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

    public int getMajorblocknumber() {
        return this.majorblocknumber;
    }

    public void setMajorblocknumber(int majorblocknumber) {
        this.majorblocknumber = majorblocknumber;
    }

    public int getMinorblocknumber() {
        return this.minorblocknumber;
    }

    public void setMinorblocknumber(int minorblocknumber) {
        this.minorblocknumber = minorblocknumber;
    }

    public boolean getFinalised() {
        return this.finalised;
    }

    public CopyOnWriteArrayList<Integer> getCommitMessageValidatorsIndexes() {
        return this.commitMessageValidatorsIndexes;
    }

    public void setCommitMessageValidatorsIndexes(CopyOnWriteArrayList<Integer> commitMessageValidatorsIndexes) {
        this.commitMessageValidatorsIndexes = commitMessageValidatorsIndexes;
    }

    // Block Number to which it belongs
    // --------Round Value as it is pipeline -------
    int majorblocknumber;
    // Minor Block Number: It is the Quorum Index of the Quorum to which it belongs
    // Always static non need to increment
    int minorblocknumber;
    // Look at the Final Chain always
    int blockHeight;

    // Partial Block Proposer claulated at main chhain index
    int proposeCalculatedatindex;

    public boolean finalised = false;

    CopyOnWriteArrayList<Transaction> transactions = new CopyOnWriteArrayList<Transaction>();

    CopyOnWriteArrayList<String> commitMessageValidators = new CopyOnWriteArrayList<String>();

    CopyOnWriteArrayList<Integer> commitMessageValidatorsIndexes = new CopyOnWriteArrayList<Integer>();

    public boolean isFinalised() {
        return this.finalised;
    }

    public void setFinalised(boolean finalised) {
        this.finalised = finalised;
    }

    public String getProposalType() {
        return this.proposalType;
    }

    public void setProposalType(String inProposalType) {
        this.proposalType = inProposalType;
    }

    public int getQuorumId() {
        return this.QuorumId;
    }

    public void setQuorumId(int inQuorumId) {
        this.QuorumId = inQuorumId;
    }

    public int getSubEpoch() {
        return this.subepoch;
    }

    public void setSubEpoch(int subepoch) {
        this.subepoch = subepoch;
    }

    public Timestamp getPartialBlocktime() {
        return this.blocktime;
    }

    public void setPartialBlocktime(Timestamp blocktime) {
        this.blocktime = blocktime;
    }

    public String getPrevioushash() {
        return this.previoushash;
    }

    public void setPrevioushash(String previoushash) {
        this.previoushash = previoushash;
    }

    public void setPartialBlockHash(String hash) {
        this.hash = hash;
    }

    public CopyOnWriteArrayList<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(CopyOnWriteArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public CopyOnWriteArrayList<String> getCommitMessageValidators() {
        return this.commitMessageValidators;
    }

    public void setCommitMessageValidators(CopyOnWriteArrayList<String> commitMessageValidators) {
        this.commitMessageValidators = commitMessageValidators;
    }

    public CopyOnWriteArrayList<Integer> getCommitMessageValidatorIndexes() {
        return this.commitMessageValidatorsIndexes;
    }

    public void setCommitMessageValidatorIndexes(CopyOnWriteArrayList<Integer> commitMessageValidatorsIndexes) {
        this.commitMessageValidatorsIndexes = commitMessageValidatorsIndexes;
    }

    public String getProposer() {
        return this.proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public int getRivalProposerIndex() {
        return this.rivalproposerindex;
    }

    public void setRivalProposerIndex(int rivalproposerindex) {
        this.rivalproposerindex = rivalproposerindex;
    }

    public int getProposerNodeIndex() {
        return this.proposernodeindex;
    }

    public void setProposerNodeIndex(int proposernodeindex) {
        this.proposernodeindex = proposernodeindex;
    }

    public int getOriginalRivalProposerIndex() {
        return this.originalRivalProposernodeindex;
    }

    public void setOriginalRivalProposerIndex(int originalRivalproposerindex) {
        this.originalRivalProposernodeindex = originalRivalproposerindex;
    }

    public int getOriginalProposerNodeIndex() {
        return this.originalProposernodeindex;
    }

    @Override
    public String toString() {
        return "{" +
                " blocktime='" + getBlockTimestamp() + "'" +
                ", previoushash='" + getPrevioushash() + "'" +
                ", hash='" + getPartialBlockHash() + "'" +
                ", proposer='" + getProposer() + "'" +
                ", QuorumId='" + getQuorumId() + "'" +
                ", proposernodeindex='" + getProposerNodeIndex() + "'" +
                ", rivalproposerindex='" + getRivalProposerIndex() + "'" +
                ", originalProposernodeindex='" + getOriginalProposerNodeIndex() + "'" +
                ", originalRivalProposernodeindex='" + getOriginalRivalProposerIndex() + "'" +
                ", signature='" + getSignature() + "'" +
                ", subepoch='" + getSubEpoch() + "'" +
                ", majorblocknumber='" + getMajorBlocknumber() + "'" +
                ", minorblocknumber='" + getMinorBlocknumber() + "'" +
                ", blockHeight='" + getBlockHeight() + "'" +
                ", proposeCalculatedatindex='" + getProposeCalculatedatindex() + "'" +
                ", finalised='" + isFinalised() + "'" +
                ", transactions='" + getTransactions() + "'" +
                ", transactionsSIZE='" + getTransactions().size() + "'" +
                ", commitMessageValidators='" + getCommitMessageValidators() +
                "}";
    }

    public void setOriginalProposerNodeIndex(int originalProposernodeindex) {
        this.originalProposernodeindex = originalProposernodeindex;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getMajorBlocknumber() {
        return this.majorblocknumber;
    }

    public void setMajorBlocknumber(int majorBlockNumber) {
        this.majorblocknumber = majorBlockNumber;
    }

    public int getMinorBlocknumber() {
        return this.minorblocknumber;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public int getBlockHeight() {
        return this.blockHeight;
    }

    public void setProposeCalculatedatindex(int proposeCalculatedatindex) {
        this.proposeCalculatedatindex = proposeCalculatedatindex;
    }

    public int getProposeCalculatedatindex() {
        return this.proposeCalculatedatindex;
    }

    public void setMinorBlocknumber(int minorBlockNumber) {
        this.minorblocknumber = minorBlockNumber;
    }

    public String getPartialBlockDetails() {
        return "blocktime:" + this.blocktime.toString() + ";" + "previousblockhash:" + this.previoushash + ";"
                + "hash:" + this.hash + ";" + ";" + "transactionsize:" + String.valueOf(transactions.size()) + ";"
                + "proposer" + this.proposer + ";" + "signature:" + this.signature + "blocknumber:"
                + String.valueOf(this.majorblocknumber);
    }

    public String getPartialBlockHash() {
        return this.hash;
    }

    public String getBlockSignature() {
        return this.signature;
    }

    public Timestamp getBlockTimestamp() {
        return this.blocktime;
    }

    public CopyOnWriteArrayList<Transaction> getBlockData() {
        return this.transactions;
    }

    public PartialBlock() {

    }

    public PartialBlock(Timestamp timestamp, String previoushash, String currentblockhash,
            CopyOnWriteArrayList<Transaction> data,
            String partialblockproposer, String signature, int majorblocknum, int minorblocknum, int proposernodeindex,
            int blockHeight, int rivalproposerindex, int proposeCalculatedatindex, int subEpoch,
            int originalProposernodeindex,
            int originalRivalProposernodeindex, int quorumId, String proposalType) {
        this.blocktime = timestamp;
        this.previoushash = previoushash;
        this.hash = currentblockhash;
        this.transactions = data;
        this.proposer = partialblockproposer;
        this.signature = signature;
        this.majorblocknumber = majorblocknum;
        this.minorblocknumber = minorblocknum;
        this.proposernodeindex = proposernodeindex;
        this.blockHeight = blockHeight;
        this.rivalproposerindex = rivalproposerindex;
        this.proposeCalculatedatindex = proposeCalculatedatindex;
        this.subepoch = subEpoch;
        this.originalProposernodeindex = originalProposernodeindex;
        this.originalRivalProposernodeindex = originalRivalProposernodeindex;
        this.QuorumId = quorumId;
        this.proposalType = proposalType;
    }

    public PartialBlock(Timestamp timestamp, String previoushash, String currentblockhash,
            CopyOnWriteArrayList<Transaction> data,
            String partialblockproposer, String signature, int majorblocknum, int minorblocknum, int proposernodeindex,
            int blockHeight, int rivalproposerindex, int proposeCalculatedatindex, int subEpoch,
            int originalProposernodeindex,
            int originalRivalProposernodeindex, int quorumId, String proposalType, boolean validity) {
        this.blocktime = timestamp;
        this.previoushash = previoushash;
        this.hash = currentblockhash;
        this.transactions = data;
        this.proposer = partialblockproposer;
        this.signature = signature;
        this.majorblocknumber = majorblocknum;
        this.minorblocknumber = minorblocknum;
        this.proposernodeindex = proposernodeindex;
        this.blockHeight = blockHeight;
        this.rivalproposerindex = rivalproposerindex;
        this.proposeCalculatedatindex = proposeCalculatedatindex;
        this.subepoch = subEpoch;
        this.originalProposernodeindex = originalProposernodeindex;
        this.originalRivalProposernodeindex = originalRivalProposernodeindex;
        this.QuorumId = quorumId;
        this.proposalType = proposalType;
        this.validity = validity;
    }

    // Create a Partial Block based on the last Block Actual Hash
    // if finalised = last Block Actual Hash
    // or else
    // Partial Block Hash State
    // Block --- []
    public static PartialBlock createPartialBlock(String lastHash, CopyOnWriteArrayList<Transaction> data,
            Wallet wallet,
            int majorblocknum, int minorblocknum, int blockProposernodeIndex, int blockHeight,
            int inRivalProposernodeindex,
            int proposeCalculatedatindex, int subEpoch, int originalProposerIndex, int originalRivalProposerIndex,
            int quorumId, String inProposalType) {

        Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
        String proposer = wallet.getPublicKey();
        int rivalproposernodeindex = inRivalProposernodeindex;
        int proposerNodeIndex = wallet.getNodeproperty();
        String partialBlockHash = generatePartialBlockHash(lastHash, data, proposer, majorblocknum, minorblocknum);

        // String proposer =
        // Base64.getEncoder().encodeToString(proposerkey.getEncoded());
        String signature = null;
        boolean validity = false;
        if (NodeProperty.getnodeBehavior() == 3) {
            String maliciousblockHash = CryptoUtil.getHash("MALICIOUS");
            signature = signPartialBlockHash(maliciousblockHash, wallet);
            validity = false;

        } else {
            signature = signPartialBlockHash(partialBlockHash, wallet);
            validity = true;
        }
        return new PartialBlock(timeStampcurrent, lastHash, partialBlockHash, data, proposer, signature, majorblocknum,
                minorblocknum, blockProposernodeIndex, blockHeight, inRivalProposernodeindex, proposeCalculatedatindex,
                subEpoch,
                originalProposerIndex, originalRivalProposerIndex, quorumId, inProposalType, validity);
    }

    public static String generatePartialBlockHash(String lastHash,
            List<Transaction> transactions, String proposer, int majorblocknum, int minorblocknum) {
        // Last State Hash + First Transaction Id + Last Transaction Id + Proposer
        if (transactions.size() > 0) {
            return CryptoUtil.getHash(lastHash +
                    transactions.get(0).getId()
                    + transactions.get(transactions.size() - 1).getId() + proposer + majorblocknum + minorblocknum);
        }
        return CryptoUtil.getHash(lastHash +
                "firsttransaction"
                + "lasttransaction" + proposer + majorblocknum + minorblocknum);

    }

    public static String generatePartialBlockHash(PartialBlock partialBlock) {
        // Last State Hash + First Transaction Id + Last Transaction Id
        if (partialBlock.getBlockData().size() > 0) {
            return CryptoUtil.getHash(partialBlock.getPrevioushash() +
                    partialBlock.getBlockData().get(0).getId()
                    + partialBlock.getBlockData().get(partialBlock.getBlockData().size() - 1).getId()
                    + partialBlock.getProposer() + partialBlock.getMajorBlocknumber()
                    + partialBlock.getMinorBlocknumber());
        } else {
            return CryptoUtil.getHash(partialBlock.getPrevioushash() +
                    "firsttransaction"
                    + "lasttransaction" + partialBlock.getProposer() + partialBlock.getMajorBlocknumber()
                    + partialBlock.getMinorBlocknumber());
        }
    }

    public static String signPartialBlockHash(String partialBlockHash, Wallet wallet) {
        return wallet.signData(partialBlockHash);

    }

    public static boolean verifyPartialBlock(PartialBlock partialBlock) {
        try {
            return CryptoUtil.verify(partialBlock.getProposer(), partialBlock.getBlockSignature(),
                    PartialBlock.generatePartialBlockHash(partialBlock.getPrevioushash(), partialBlock.getBlockData(),
                            partialBlock.getProposer(), partialBlock.getMajorBlocknumber(),
                            partialBlock.getMinorBlocknumber()));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException
                | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verifyProposer(PartialBlock partialBlock, String proposer) {
        return partialBlock.getProposer().equals(proposer);
    }

}
