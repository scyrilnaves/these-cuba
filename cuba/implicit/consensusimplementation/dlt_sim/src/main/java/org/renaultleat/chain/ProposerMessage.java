package org.renaultleat.chain;

public class ProposerMessage {

    int nodeIndex;

    int chainKey;

    public int getNodeIndex() {
        return this.nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public int getChainIndex() {
        return this.chainKey;
    }

    public void setChainIndex(int chainIndex) {
        this.chainKey = chainIndex;
    }

    ProposerMessage(int inNodeIndex, int inChainKey) {
        this.nodeIndex = inNodeIndex;
        this.chainKey = inChainKey;
    }

}
