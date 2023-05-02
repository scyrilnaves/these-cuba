package org.renaultleat.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.renaultleat.properties.CAPSEOBFTProperty;

// Class for Storing Heart Beats from a Peer to check liveness
public class HeartBeatStorage {

    // Last Hearbeat Sent by a Peers
    // Node Index, Last TimeStamp
    public volatile Map<Integer, Timestamp> peerUtilitarianHeartBeat = new ConcurrentHashMap<Integer, Timestamp>();

    public Map<Integer, Timestamp> getPeerUtilitarianHeartBeat() {
        return this.peerUtilitarianHeartBeat;
    }

    public void setEffectiveUtilitarianScore(Map<Integer, Timestamp> peerUtilitarianHeartBeat) {
        this.peerUtilitarianHeartBeat = peerUtilitarianHeartBeat;
    }

    // For Setting HeartBeat Score
    // Node Index, Epoch, SubEpoch, Utilitarian Score
    public void incrementPeerHeartBeat(int index, Timestamp inTimeStamp) {
        // check for Node index
        if (peerUtilitarianHeartBeat.containsKey(index)) {

            peerUtilitarianHeartBeat.put(index, inTimeStamp);

        } else {
            peerUtilitarianHeartBeat.put(index, inTimeStamp);
        }
        // check for Node index and epoch

    }

    // For Getting HeartBeat
    public Timestamp getLatestPeerHeartBeat(int index) {
        // check for Node index
        if (peerUtilitarianHeartBeat.containsKey(index)) {
            // check for Node index and epoch
            return peerUtilitarianHeartBeat.get(index);
        }
        return null;
    }

    // For Getting HeartBeat
    public List<Integer> getActivePeers() {
        List<Integer> activePeers = new ArrayList<Integer>();
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        long currentTime = currentTimeStamp.getTime();
        for (Entry<Integer, Timestamp> peerUtilitarianHeartBeatMap : peerUtilitarianHeartBeat.entrySet()) {
            // System.out.println("hearcurrentTime" + currentTime);
            // System.out.println("hear2currentTime" +
            // peerUtilitarianHeartBeatMap.getValue().getTime());
            // System.out.println("difference" + (currentTime -
            // peerUtilitarianHeartBeatMap.getValue().getTime()));
            // System.out.println("absdifference" + Math.abs(currentTime -
            // peerUtilitarianHeartBeatMap.getValue().getTime()));
            // System.out.println("abs" + Math.abs(currentTime -
            // peerUtilitarianHeartBeatMap.getValue().getTime())
            /// CAPSEOBFTProperty.conversioncoefficient);
            if (Math.abs(currentTime - peerUtilitarianHeartBeatMap.getValue().getTime())
                    / CAPSEOBFTProperty.conversioncoefficient <= CAPSEOBFTProperty.timethreshold) {

                activePeers.add(peerUtilitarianHeartBeatMap.getKey());
            }
        }
        return activePeers;
    }

}