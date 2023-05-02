package org.renaultleat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Timestamp;
import java.time.temporal.ValueRange;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

public class NodeCommunicator extends Thread {

    private ServerSocket serverSocket;
    private Blockchain blockchain;
    private Set<NodeCommunicatorThread> nodeCommunicatorThreads = new HashSet<NodeCommunicatorThread>();
    private ValueRange firstrange;
    private ValueRange secondrange;
    private ValueRange thirdrange;
    private ValueRange fourthrange;

    public NodeCommunicator(String port, Blockchain blockchain) throws NumberFormatException, IOException {
        serverSocket = new ServerSocket(Integer.valueOf(port), 500);
        this.blockchain = blockchain;
        this.firstrange = java.time.temporal.ValueRange.of(CAPSEOBFTProperty.firstrangeupperindex,
                CAPSEOBFTProperty.firstrangelowerindex);
        this.secondrange = java.time.temporal.ValueRange.of(CAPSEOBFTProperty.secondrangeupperindex,
                CAPSEOBFTProperty.secondrangelowerindex);
        this.thirdrange = java.time.temporal.ValueRange.of(
                CAPSEOBFTProperty.thirdrangeupperindex,
                CAPSEOBFTProperty.thirdrangelowerindex);
        this.fourthrange = java.time.temporal.ValueRange.of(
                CAPSEOBFTProperty.fourthrangeupperindex,
                CAPSEOBFTProperty.fourthrangelowerindex);
    }

    public void run() {
        try {
            while (true) {
                NodeCommunicatorThread nodeCommunicatorThread = new NodeCommunicatorThread(serverSocket.accept(), this);
                nodeCommunicatorThreads.add(nodeCommunicatorThread);
                nodeCommunicatorThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadCastTransaction(String currentuser, String message, Transaction inTransaction)
            throws IOException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", currentuser);
        jsonObject.put("message", message);
        jsonObject.put("type", "TRANSACTION");
        Gson gson = new Gson();
        String datajson = gson.toJson(inTransaction);
        jsonObject.put("data", datajson);
        sendMessage(jsonObject.toString());
    }

    public void rebroadCastTransaction(JSONObject jsonObject)
            throws IOException {
        sendMessage(jsonObject.toString());
    }

    public void broadCastNewTransaction(String currentuser, Wallet wallet, int no_of_transactions)
            throws IOException {
        for (int i = 0; i < no_of_transactions; i++) {
            Transaction inputTransaction = new Transaction("Simulation Test", wallet);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", currentuser);
            jsonObject.put("message", "Simulation Test");
            jsonObject.put("type", "TRANSACTION");
            Gson gson = new Gson();
            String datajson = gson.toJson(inputTransaction);
            jsonObject.put("data", datajson);
            sendMessage(jsonObject.toString());
        }
    }

    public void sendMessage(String message) {
        try {
            TimeUnit.MILLISECONDS.sleep(NodeProperty.latency);
            if (NodeProperty.getnodeBehavior() == 1) {
                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 100);
            }
            if (NodeProperty.getnodeBehavior() == 2 || NodeProperty.getnodeBehavior() == 3) {
                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 200);
            }
            if (NodeProperty.getnodeBehavior() == 4
                    && this.blockchain.getChain().size() < CAPSEOBFTProperty.upperindex) {
                // System.out
                // .println("44444Node" + ":::" + this.blockchain
                // .getChain().lastKey().intValue());
                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 200);
            }
            if (NodeProperty.getnodeBehavior() == 5
                    && this.blockchain.getChain().size() < CAPSEOBFTProperty.secondupperindex) {
                // System.out
                // .println("44444Node" + ":::" + this.blockchain
                // .getChain().lastKey().intValue());
                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 200);
            }
            if (NodeProperty.getnodeBehavior() == 6
                    && (firstrange.isValidIntValue(
                            this.blockchain.getChain().size())
                            || secondrange.isValidIntValue(this.blockchain
                                    .getChain().size())
                            || thirdrange.isValidIntValue(this.blockchain
                                    .getChain().size())
                            || fourthrange.isValidIntValue(this.blockchain
                                    .getChain().size()))) {
                // System.out
                // .println("5555Node" + ":::" + this.blockchain
                // .getChain().lastKey().intValue());
                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 200);
            }
            nodeCommunicatorThreads.forEach(t -> t.getPrintWriter().println("YYY" + message + "ZZZ"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<NodeCommunicatorThread> getnodeCommunicatorThread() {
        return nodeCommunicatorThreads;
    }
}
