package org.renaultleat.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import org.renaultleat.chain.Block;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.ProposerMessage;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.consensus.Message;
import org.renaultleat.consensus.QuorumMessage;
import org.renaultleat.consensus.CAPSEOBFTDemMessagePool;
import org.renaultleat.crypto.CryptoUtil;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Transaction;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.NodeDevProperty;
import org.renaultleat.properties.NodeProperty;
import org.json.JSONObject;

// Server for Deployment in Cloud
public class P2PServer {
    public NodeCommunicator nodeCommunicator;
    public NodeCommunicatorSec nodeCommunicatorSec;
    public NodeCommunicatorTer nodeCommunicatorTer;
    public Blockchain blockchain;
    public TransactionPool transactionPool;
    public BlockPool blockPool;
    public Wallet wallet;
    public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;
    public Validator validator;
    public NonValidator nonValidator;
    public String currentuser;
    public QueueResource queueResource;

    public void connect() throws IOException, InterruptedException {
        // Get index for current Node
        int index = Integer.valueOf(Wallet.getNodeproperty());
        String currentuser = NodeProperty.user + String.valueOf(index);
        this.currentuser = currentuser;
        String currentport = NodeProperty.port;
        this.nodeCommunicator = new NodeCommunicator(currentport, this.blockchain);
        this.nodeCommunicatorSec = new NodeCommunicatorSec(currentport, this.blockchain);
        this.nodeCommunicatorTer = new NodeCommunicatorTer(currentport, this.blockchain);

        nodeCommunicator.start();
        nodeCommunicatorSec.start();
        nodeCommunicatorTer.start();
        // //System.out.println("send c to initiate coomunication with peers");
    }

    public void connectToPeers()
            throws FileNotFoundException, IOException {
        // Form peers List
        List<String> peerlist = getPeerList();

        new P2PServer(this.blockchain, this.transactionPool, this.wallet, this.capSEOBFTDemMessagePool, this.validator,
                this.nonValidator,
                this.blockPool, this.nodeCommunicator, this.queueResource, this.nodeCommunicatorSec,
                this.nodeCommunicatorTer)
                .listenToPeers(this.currentuser, this.nodeCommunicator, peerlist, this.nodeCommunicatorSec,
                        this.nodeCommunicatorTer);
    }

    // Use when deployed in Cloud
    public List<String> getPeerList() {

        int index = Integer.valueOf(Wallet.getNodeproperty());
        int total = NodeProperty.totalnodes;
        int totalvalidators = NodeProperty.getValidators();
        int totalpeers = NodeProperty.totalnodes - 1;
        String networkType = NodeProperty.getnodeNetwork();
        String ips[] = NodeProperty.ips.split(",");

        String basefile = "topology/" + networkType + "/" + String.valueOf(total) + "/" + String.valueOf(index)
                + ".csv";
        File file;

        List<String> peerlist = new ArrayList<String>();
        List<Integer> addedIndex = new ArrayList<Integer>();
        // Add the validators (If they feature in the configuration) and Non Validators
        // in total
        if (networkType.equals("mesh")) {
            for (int i = 0; i < total; i++) {
                if (i != index) {
                    peerlist.add(ips[i] + ":" + NodeProperty.port);
                    addedIndex.add(i);
                }

            }
        } else {
            try {
                file = CryptoUtil.getFileFromResource(basefile);
                BufferedReader br = new BufferedReader(new FileReader(file));
                {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        // //System.out.println("valueport" + values[0] + ":" + values[1] + ":" +
                        // values[2] + ":");
                        peerlist.add(ips[Integer.parseInt(values[1])] + ":" + NodeProperty.port);
                        addedIndex.add(Integer.parseInt(values[1]));
                    }
                    br.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

        // Now add the validators to communicate if you are a validator
        // Connect to all validators
        if (NodeProperty.isValidator()) {
            for (int i = 0; i < totalvalidators; i++) {
                // Check if not already added
                if (i != index && !addedIndex.contains(i)) {
                    peerlist.add(ips[i] + ":" + NodeProperty.port);
                    addedIndex.add(i);
                }

            }
        }

        return peerlist;
    }

    public void listenToPeers(String user, NodeCommunicator nodeCommunicator, List<String> peers,
            NodeCommunicatorSec nodeCommunicatorSec, NodeCommunicatorTer nodeCommunicatorTer)
            throws IOException {
        String peerslist = "";
        for (int i = 0; i < peers.size(); i++) {
            String[] address = peers.get(i).split(":");
            Socket socket = null;
            Socket secsocket = null;
            Socket tersocket = null;
            try {
                peerslist += address[0] + ";";
                socket = new Socket(address[0], Integer.valueOf(address[1]));
                secsocket = new Socket(address[0], Integer.valueOf(address[1]) + 10);
                tersocket = new Socket(address[0], Integer.valueOf(address[1]) + 20);

                new P2PHandler(socket, this.blockchain, this.transactionPool, this.wallet, this.capSEOBFTDemMessagePool,
                        this.validator, this.nonValidator,
                        this, this.nodeCommunicator, this.currentuser, this.blockPool, this.queueResource).start();

                new P2PHandlerSec(secsocket, this.blockchain, this.transactionPool, this.wallet,
                        this.capSEOBFTDemMessagePool,
                        this.validator, this.nonValidator,
                        this, this.nodeCommunicator, this.currentuser, this.blockPool, this.queueResource,
                        this.nodeCommunicatorSec).start();

                new P2PHandlerTer(tersocket, this.blockchain, this.transactionPool, this.wallet,
                        this.capSEOBFTDemMessagePool,
                        this.validator, this.nonValidator,
                        this, this.nodeCommunicator, this.currentuser, this.blockPool, this.queueResource,
                        this.nodeCommunicatorTer).start();
            } catch (Exception e) {
                // System.out.println("Connection exception" + e);
                if (socket != null) {
                    socket.close();

                } else {
                    // System.out.println("invalid input");
                }
            }
        }
        NodeProperty.setPeers(peerslist);
        NodeProperty.setPeerCount(peers.size());
    }

    public void communicate(String message)
            throws IOException {
        // Sender Logic
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", this.wallet.getNodeId());
        jsonObject.put("message", message);
        nodeCommunicator.sendMessage(jsonObject.toString());
    }

    public void startSimulationBroadcast(int no_of_transactions, int no_of_threads)
            throws IOException {

        int numberOfThreads = no_of_threads;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                try {
                    this.nodeCommunicator.broadCastNewTransaction(this.currentuser, this.wallet, no_of_transactions);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startSimulationBroadcastSingle(int no_of_transactions)
            throws IOException {
        // Sender Logic
        for (int i = 0; i < no_of_transactions; i++) {
            Transaction inputTransaction = new Transaction("Simulation Test" + i + wallet.getNodeproperty(),
                    this.wallet);
            this.nodeCommunicator.broadCastTransaction(this.currentuser,
                    "Simulation Test", inputTransaction);
        }
    }

    public void startSimulationBroadcastPrivacySingle(int no_of_transactions)
            throws IOException {
        // Sender Logic
        for (int i = 0; i < no_of_transactions; i++) {
            Transaction inputTransaction = new Transaction("Simulation Test" + i + wallet.getNodeproperty(),
                    this.wallet, true);
            this.nodeCommunicator.broadCastTransaction(this.currentuser,
                    "Simulation Test", inputTransaction);
        }
    }

    public void broadCastQuorum(QuorumMessage data)
            throws IOException {
        if (NodeProperty.isValidator()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", this.currentuser);
            jsonObject.put("message", "QuorumMessage");
            jsonObject.put("type", "QUORUM");
            jsonObject.put("epoch", String.valueOf(data.getEpoch()));
            jsonObject.put("subepoch", String.valueOf(data.getSubEpoch()));
            jsonObject.put("nodeIndex", String.valueOf(data.getNodeIndex()));
            jsonObject.put("blockheight", String.valueOf(data.getBlockIndex()));
            jsonObject.put("sender", data.getMessageSender());
            Gson gson = new Gson();
            String datajson = gson.toJson(data);
            jsonObject.put("data", datajson);
            this.nodeCommunicator.sendMessage(jsonObject.toString());
            // System.out.println("broadcasted");
        }
    }

    public void setGenesisQuorum() {
        ProposerMessage genesisQuorumPoposer = this.blockchain.getInitialQuorumProposer();
        if (genesisQuorumPoposer.getNodeIndex() == Wallet.getNodeproperty()) {
            QuorumMessage quorumMessage = null;
            try {
                quorumMessage = capSEOBFTDemMessagePool.formInitialQuorumMessage(blockchain, wallet,
                        genesisQuorumPoposer.getChainIndex());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                this.broadCastQuorum(quorumMessage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public NodeCommunicator getNodeCommunicator() {
        return this.nodeCommunicator;
    }

    public NodeCommunicatorSec getNodeCommunicatorSec() {
        return this.nodeCommunicatorSec;
    }

    public NodeCommunicatorTer getNodeCommunicatorTer() {
        return this.nodeCommunicatorTer;
    }

    public P2PServer(Blockchain blockChain, TransactionPool transactionPool, Wallet wallet,
            CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool, Validator validator, NonValidator nonValidator,
            BlockPool blockPool,
            QueueResource queueResource) {
        this.blockchain = blockChain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.validator = validator;
        this.nonValidator = nonValidator;
        this.blockPool = blockPool;
        this.queueResource = queueResource;
    }

    public P2PServer(Blockchain blockChain, TransactionPool transactionPool, Wallet wallet,
            CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool, Validator validator, NonValidator nonValidator,
            BlockPool blockPool,
            NodeCommunicator nodeCommunicator, QueueResource queueResource, NodeCommunicatorSec nodeCommunicatorSec,
            NodeCommunicatorTer nodeCommunicatorTer) {
        this.blockchain = blockChain;
        this.transactionPool = transactionPool;
        this.wallet = wallet;
        this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        this.validator = validator;
        this.nonValidator = nonValidator;
        this.blockPool = blockPool;
        this.nodeCommunicator = nodeCommunicator;
        this.queueResource = queueResource;
        this.nodeCommunicatorSec = nodeCommunicatorSec;
        this.nodeCommunicatorTer = nodeCommunicatorTer;
    }

}
