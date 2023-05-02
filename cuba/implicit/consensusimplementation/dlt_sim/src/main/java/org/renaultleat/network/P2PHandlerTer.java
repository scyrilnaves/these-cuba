package org.renaultleat.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.consensus.CAPSEOBFTDemMessagePool;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.NodeProperty;
import org.json.JSONObject;
import org.json.JSONTokener;

public class P2PHandlerTer extends Thread {

    private BufferedReader bufferedReader;

    public Blockchain blockchain;

    public TransactionPool transactionPool;

    public BlockPool blockPool;

    public Wallet wallet;

    public CAPSEOBFTDemMessagePool pbftMessagePool;

    public Validator validator;

    public NonValidator nonValidator;

    public P2PServer p2pServer;

    public NodeCommunicator nodeCommunicator;

    public NodeCommunicatorTer nodeCommunicatorTer;

    public QueueResource queueResource;

    public String currentuser;

    public P2PHandlerTer(Socket socket, Blockchain blockChain, TransactionPool transactionPool, Wallet wallet,
            CAPSEOBFTDemMessagePool pbftMessagePool, Validator validator, NonValidator nonValidator,
            P2PServer p2pServer,
            NodeCommunicator nodeCommunicator, String currrentuser, BlockPool blockPool, QueueResource queueResource,
            NodeCommunicatorTer nodeCommunicatorTer)
            throws IOException {
        this.blockchain = blockChain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.pbftMessagePool = pbftMessagePool;
        this.blockPool = blockPool;
        this.validator = validator;
        this.nonValidator = nonValidator;
        this.p2pServer = p2pServer;
        this.nodeCommunicator = nodeCommunicator;
        this.currentuser = currrentuser;
        this.queueResource = queueResource;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.nodeCommunicatorTer = nodeCommunicatorTer;

    }

    public void run() {
        boolean flag = true;
        boolean multithread = true;

        // int i = 0;
        while (flag) {
            try {
                StringBuilder sb = new StringBuilder();
                String line;
                String tempData;
                String indata;
                String finaldata;
                do {
                    line = bufferedReader.readLine();
                    sb.append(line);
                } while (sb.toString().startsWith("YYY") && !line.endsWith("ZZZ"));
                tempData = sb.toString();
                if (tempData.startsWith("YYY") && tempData.endsWith("ZZZ")) {
                    indata = tempData.replaceFirst("YYY", "");
                    finaldata = indata.replaceFirst("ZZZ", "");
                    // System.out.println("data"+indata);
                    if (finaldata.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(finaldata);
                        // Receiver Logic
                        /*
                         * if (jsonObject.getString("username").equals("node1")) {
                         * //System.out.println("Hey Buddy");
                         * }
                         */
                        // //System.out.println(jsonObject);
                        String messagevalidity = jsonObject.getString("username");
                        String messageType = jsonObject.getString("type");
                        if (multithread) {
                            if (!messagevalidity.isEmpty() && !messagevalidity.isBlank()) {
                                TimeUnit.MILLISECONDS.sleep(NodeProperty.latency);
                                if (NodeProperty.getnodeBehavior() == 1) {
                                    TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 30000);
                                }
                                if (NodeProperty.getnodeBehavior() == 2) {
                                    TimeUnit.MILLISECONDS.sleep(NodeProperty.latency + 60000);
                                }
                                // For Simple Transactions
                                // TRANSACTION
                                if (messageType.equals("TRANSACTION")) {
                                    this.queueResource.getTransactionBlockingQueueTer().put(jsonObject);
                                    // For Quorum Messages to change them
                                    // QUORUM
                                } else if (messageType.equals("QUORUM")) {
                                    this.queueResource.getQuorumBlockingQueue().put(jsonObject);
                                    // For Incoming Full Block Messages: Partially Ordered Block
                                    // BLOCK
                                } else if (messageType.equals("BLOCK")) {
                                    this.queueResource.getFullBlockBlockingQueue().put(jsonObject);
                                    // For Broadcasting Partial Block Messages to inside Quorum and external to
                                    // Quorum
                                    // FINALISE
                                } else if (messageType.equals("FINALISE")) {
                                    this.queueResource.getFinaliseBlockingQueue().put(jsonObject);
                                    // For Privacy Messages
                                    // PRIVACY
                                } else if (messageType.equals("PRIVACY")) {
                                    this.queueResource.getPrivacyBlockingQueue().put(jsonObject);
                                    // For HeartBeat Message
                                    // HEARTBEAT
                                } else if (messageType.equals("HEARTBEAT")) {
                                    this.queueResource.getHeartBeatBlockingQueue().put(jsonObject);
                                } else {
                                    // For consensus Messages to handle inside a particular quorum
                                    // PROPOSE // COMMIT // ROUND CHANGE
                                    this.queueResource.getMessageBlockingQueue().put(jsonObject);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                flag = false;
                interrupt();
            }
        }
    }

}
