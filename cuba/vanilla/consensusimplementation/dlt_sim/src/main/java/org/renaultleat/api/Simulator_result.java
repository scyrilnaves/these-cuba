package org.renaultleat.api;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// DataStructure for storing collatting results
public class Simulator_result {

    // Time Series, Timestamp, Confirmed Tx Count
    public volatile SortedMap<Timestamp, Integer> inputTpsStorage = new TreeMap<Timestamp, Integer>();

    // Time Series, TimeStamp
    public volatile Map<Timestamp, Integer> partialTpsStorage = new LinkedHashMap<Timestamp, Integer>();

    // Time Series, Timestamp, Confirmed Block Count (extrapolate with block size to
    // get tx finalised)
    public volatile Map<Timestamp, Integer> finalisedTpsStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for Propose in consensus Message for the partial block
    public volatile Map<Timestamp, Integer> proposeConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for Commit in consensus Message Handler for the partial
    // block
    public volatile Map<Timestamp, Integer> commitConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for Finalise in consensus Message Handler for the block
    public volatile Map<Timestamp, Integer> finaliseConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for Quorum in consensus Message Handler after updated
    // block
    public volatile Map<Timestamp, Integer> quorumConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for Heart Beat in consensus Message Handler after updated
    // block
    public volatile Map<Timestamp, Integer> heartBeatConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    // Call the counter for RoundChange in consensus Message Handler after updated
    // block
    public volatile Map<Timestamp, Integer> roundChangeConsensusCounterStorage = new LinkedHashMap<Timestamp, Integer>();

    public Map<Timestamp, Integer> getInputTPSStorage() {
        return this.inputTpsStorage;
    }

    public Map<Timestamp, Integer> getPartialTPSStorage() {
        return this.partialTpsStorage;
    }

    public Map<Timestamp, Integer> getFinalisedTPSStorage() {
        return this.finalisedTpsStorage;
    }

    public Map<Timestamp, Integer> getProposeConsensusCounterStorage() {
        return this.proposeConsensusCounterStorage;
    }

    public Map<Timestamp, Integer> getCommitConsensusCounterStorage() {
        return this.commitConsensusCounterStorage;
    }

    public Map<Timestamp, Integer> getFinaliseConsensusCounterStorage() {
        return this.finaliseConsensusCounterStorage;
    }

    public Map<Timestamp, Integer> getQuorumConsensusCounterStorage() {
        return this.quorumConsensusCounterStorage;
    }

    public Map<Timestamp, Integer> getHeartBeatConsensusCounterStorage() {
        return this.heartBeatConsensusCounterStorage;
    }

    public Map<Timestamp, Integer> getRoundChangeConsensusCounterStorage() {
        return this.roundChangeConsensusCounterStorage;
    }

}
