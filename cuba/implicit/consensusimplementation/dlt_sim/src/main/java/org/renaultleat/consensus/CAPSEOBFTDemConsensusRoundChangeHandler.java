package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.api.Simulator_result;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

/**
 * This Handler is used in case of Multi threaded call from P2PHandler or single
 * threaded we call PBFTMessageHandler
 * 
 */
public class CAPSEOBFTDemConsensusRoundChangeHandler extends AbstractScheduledService {

    public volatile AtomicInteger blockhainHeightCounter = new AtomicInteger(1);

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    public Synchronizer synchronizer;

    // To increment Round Counter
    public Blockchain blockChain;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    public Wallet wallet;

    public Timer timer;

    public BlockPool blockPool;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void broadcastRoundChange(int subEpoch)
            throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", this.currentuser);
        jsonObject.put("nodeindex", String.valueOf(this.wallet.getNodeproperty()));
        jsonObject.put("messenger", this.wallet.getPublicKey());
        jsonObject.put("type", "ROUNDCHANGE");
        jsonObject.put("QuromId", String.valueOf(QuorumStorage.getQuorumId()));
        jsonObject.put("QuorumIndex", String.valueOf(QuorumStorage.getQuorumIndex()));
        jsonObject.put("subEpoch", String.valueOf(subEpoch));
        this.nodeCommunicator.sendMessage(jsonObject.toString());
    }

    public void scheduleTimer() {
        try {
            this.startAsync();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void rescheduleTimer() {
        try {
            this.shutDown();
            this.startUp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void runOneIteration() throws Exception {
        // Run only if the counter has not progressed
        // if (this.blockhainHeightCounter.get() != 1) {
        if (this.blockhainHeightCounter.get() == this.blockChain.getBlockChainIndexCounter()) {
            task();
        } else {
            this.blockhainHeightCounter.set(this.blockChain.blockChainIndexCounter.get());
        }
        // }
    }

    public void task() {
        // Only if validator
        if (NodeProperty.isValidator() && QuorumStorage.quorumInitialised
                && QuorumStorage.getQuorumId() != CAPSEOBFTProperty.blacklistId) {
            // Send Round Change Message
            try {
                int subEpoch = this.blockChain.getFinalSubEpochCounter();
                broadcastRoundChange(subEpoch);
                this.capSEOBFTDemMessagePool.addRoundChangeMessage(subEpoch, this.wallet.getPublicKey());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Consensus Round Change Handler Exception" + e.toString());
                e.printStackTrace();
            }
            // rescheduleTimer();
        }

    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, NodeProperty.getRoundChange(), TimeUnit.MILLISECONDS);
    }

    public CAPSEOBFTDemConsensusRoundChangeHandler(
            NodeCommunicator nodeCommunicator, String currentuser, Synchronizer synchronizer, Blockchain blockChain,
            CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool, Wallet wallet, BlockPool blockPool) {
        this.nodeCommunicator = nodeCommunicator;
        this.currentuser = currentuser;
        this.synchronizer = synchronizer;
        this.blockChain = blockChain;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.wallet = wallet;
        this.blockPool = blockPool;
    }

}