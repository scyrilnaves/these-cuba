package org.renaultleat.consensus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.reflect.TypeToken;
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
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

/**
 * Handler to update the Utilitarian Score
 * //peerUtilitarianScore //peerUtilitarianMissedProposalScore
 * //peerUtilitarianMissedMessageScore //peerMaliciousScore;
 * This Handler is used in case of Multi threaded call from P2PHandler
 * 
 */

public class CAPSEOBFTDemHeartBeatHandler extends Thread {

    public BlockingQueue<JSONObject> heartBeatBlockingQueue;

    public NodeCommunicator nodeCommunicator;

    public HeartBeatStorage heartBeatStorage;

    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    // Be careful as this is thread and need to be concurrent and synchronized
    // Thread Safe and Read from Main Memory
    // public volatile Map<String, Boolean> consensusReached = new HashMap<String,
    // Boolean>();

    public void reBroadCastHeartBeat(JSONObject jsonObject)
            throws IOException {
        if (NodeProperty.isValidator()) {
            this.nodeCommunicator.sendMessage(jsonObject.toString());
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            {
                if (NodeProperty.isValidator()) {
                    if (!this.heartBeatBlockingQueue.isEmpty()) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = this.heartBeatBlockingQueue.take();
                            String messageType = jsonObject.getString("type");

                            if (messageType.equals("HEARTBEAT")) {
                                int round = Integer.valueOf(jsonObject.getString("round"));
                                String messenger = jsonObject.getString("messenger");

                                if (!capSEOBFTDemMessagePool.existingHeartBeatMessage(round, messenger)) {
                                    capSEOBFTDemMessagePool.addHeartBeatMessage(round, messenger);
                                    int index = Integer.valueOf(jsonObject.getString("nodeindex"));
                                    Gson gson = new Gson();
                                    Type timestampMessageObject = new TypeToken<Timestamp>() {
                                    }.getType();
                                    Timestamp inTimestamp = gson.fromJson(jsonObject.getString("timestamp"),
                                            timestampMessageObject);
                                    // Update Peer HeartBeat
                                    heartBeatStorage.incrementPeerHeartBeat(index, inTimestamp);
                                    reBroadCastHeartBeat(jsonObject);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Heart Beat Handler Exception" + e.toString());
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    public CAPSEOBFTDemHeartBeatHandler(
            HeartBeatStorage heartBeatStorage, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool,
            BlockingQueue<JSONObject> heartBeatBlockingQueue, NodeCommunicator nodeCommunicator) {
        this.heartBeatStorage = heartBeatStorage;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.heartBeatBlockingQueue = heartBeatBlockingQueue;
        this.nodeCommunicator = nodeCommunicator;
    }
}