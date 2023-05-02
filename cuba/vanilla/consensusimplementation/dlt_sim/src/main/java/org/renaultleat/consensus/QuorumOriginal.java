package org.renaultleat.consensus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POJO Class for Quorum Message
 * 
 * @param type
 * @param blockHash
 * @param messagesignature
 * @param messageSender
 * @param contents
 */
// State at particular sub epoch
public class QuorumOriginal {

    // Map<Node Index within Quorum, Original Node Id>
    Map<Integer, Integer> quorumOriginalMapping = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getQuorumOriginalMappping() {
        return this.quorumOriginalMapping;
    }

    public void setQuorumOriginalMappping(Map<Integer, Integer> quorumOriginalMapping) {
        this.quorumOriginalMapping = quorumOriginalMapping;
    }

    public void addOriginalMapping(int quorumIndex, int nodeId) {
        quorumOriginalMapping.put(quorumIndex, nodeId);
    }

}
