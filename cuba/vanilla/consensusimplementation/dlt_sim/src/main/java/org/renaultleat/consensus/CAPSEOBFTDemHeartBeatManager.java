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
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.NodeProperty;

/**
 * This Handler is used in case of Multi threaded call from P2PHandler or single
 * threaded we call PBFTMessageHandler
 * Class to broadcast the Utilitarian status
 * 
 */
public class CAPSEOBFTDemHeartBeatManager extends AbstractScheduledService {

    public volatile AtomicInteger roundCounter = new AtomicInteger(1);

    public HeartBeatStorage heartBeatStorage;

    public NodeCommunicator nodeCommunicator;

    public String currentuser;

    public Synchronizer synchronizer;

    // To increment Round Counter
    public Blockchain blockChain;

    public Wallet wallet;

    public Timer timer;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void broadCastHeartBeat(Timestamp inTimeStamp, int roundcounter)
            throws IOException {
        if (NodeProperty.isValidator()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("nodeindex", String.valueOf(this.wallet.getNodeproperty()));
            jsonObject.put("messenger", this.wallet.getPublicKey());
            jsonObject.put("type", "HEARTBEAT");
            jsonObject.put("round", String.valueOf(roundcounter));
            jsonObject.put("QuromId", String.valueOf(QuorumStorage.getQuorumId()));
            jsonObject.put("QuorumIndex", String.valueOf(QuorumStorage.getQuorumIndex()));
            Gson gson = new Gson();
            String timestampData = gson.toJson(inTimeStamp);
            jsonObject.put("timestamp", timestampData);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
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
    // Run for every round increase
    protected void runOneIteration() throws Exception {
        // Run only if the counter has not progressed
        task();
        /*
         * if (this.roundCounter.get() == this.blockChain.roundCounter.get()) {
         * task();
         * } else {
         * this.roundCounter.set(this.blockChain.roundCounter.get());
         * }
         */

    }

    public void task() {
        // Only if validator
        if (NodeProperty.isValidator()) {
            // Send Round Change Message
            try {
                Timestamp timeStampcurrent = new Timestamp(System.currentTimeMillis());
                // Update my heart beat before sending
                heartBeatStorage.incrementPeerHeartBeat(Wallet.getNodeproperty(), timeStampcurrent);
                broadCastHeartBeat(timeStampcurrent, this.blockChain.roundCounter.get());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Heart Beat Manager Exception" + e.toString());
                e.printStackTrace();
            }
            // rescheduleTimer();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, NodeProperty.getHeartBeatBroadcast(), TimeUnit.MILLISECONDS);
    }

    public CAPSEOBFTDemHeartBeatManager(
            NodeCommunicator nodeCommunicator, String currentuser, Synchronizer synchronizer, Blockchain blockChain,
            Wallet wallet, HeartBeatStorage heartBeatStorage) {
        this.nodeCommunicator = nodeCommunicator;
        this.currentuser = currentuser;
        this.synchronizer = synchronizer;
        this.blockChain = blockChain;
        this.wallet = wallet;
        this.heartBeatStorage = heartBeatStorage;

    }

}