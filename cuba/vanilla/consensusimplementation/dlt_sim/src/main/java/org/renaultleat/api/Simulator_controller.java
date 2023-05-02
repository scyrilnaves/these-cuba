package org.renaultleat.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.renaultleat.chain.BlockPool;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.consensus.UtilitarianCalculator;
import org.renaultleat.consensus.CAPSEOBFTDemConsensusMessageHandler;
import org.renaultleat.consensus.CAPSEOBFTDemConsensusRoundChangeHandler;
import org.renaultleat.consensus.CAPSEOBFTDemFinaliseHandler;
import org.renaultleat.consensus.CAPSEOBFTDemFullBlockFullfiller;
import org.renaultleat.consensus.CAPSEOBFTDemFullBlockHandler;
import org.renaultleat.consensus.CAPSEOBFTDemFullBlockManager;
import org.renaultleat.consensus.CAPSEOBFTDemHeartBeatHandler;
import org.renaultleat.consensus.CAPSEOBFTDemHeartBeatManager;
import org.renaultleat.consensus.CAPSEOBFTDemMessagePool;
import org.renaultleat.consensus.CAPSEOBFTDemPartialBlockFullfiller;
import org.renaultleat.consensus.CAPSEOBFTDemPrivacyHandler;
import org.renaultleat.consensus.CAPSEOBFTDemQuorumManager;
import org.renaultleat.consensus.CAPSEOBFTDemQuorumMessageHandler;
import org.renaultleat.consensus.CAPSEOBFTDemTransactionMessageHandler;
import org.renaultleat.consensus.CAPSEOBFTDemTransactionMessageHandlerSec;
import org.renaultleat.consensus.CAPSEOBFTDemTransactionMessageHandlerTer;
import org.renaultleat.consensus.CAPSEOBFTPartialBlockManager;
import org.renaultleat.consensus.QuorumMessage;
import org.renaultleat.consensus.Synchronizer;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.NodeCommunicator;
import org.renaultleat.network.P2PServer;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.network.QueueResource;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.network.TransactionBroadcaster;
import org.renaultleat.network.TransactionBroadcasterSec;
import org.renaultleat.network.TransactionBroadcasterTer;
import org.renaultleat.node.NonValidator;
import org.renaultleat.node.Validator;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.NodeProperty;

import java.util.List;
import java.util.Timer;

import javax.crypto.AEADBadTagException;
import javax.inject.Singleton;
import javax.json.Json;

@Path("/simulatorcontroller")
@Singleton
public class Simulator_controller {

    NonValidator nonValidator;

    Validator validator;

    Blockchain blockchain;

    TransactionPool transactionPool;

    CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

    BlockPool blockPool;

    Wallet wallet = new Wallet();

    QueueResource queueResource;

    P2PServer p2pserver;

    Simulator_service simulator_service = new Simulator_service(wallet);

    Synchronizer synchronizer;

    QuorumStorage quorumStorage;

    HeartBeatStorage heartBeatStorage;

    PrivacyStorage privacyStorage;

    UtilitarianScoreStorage utilitarianScoreStorage;

    UtilitarianCalculator utilitarianCalculator;

    CAPSEOBFTDemTransactionMessageHandler capSEOBFTDemTransactionMessageHandler;

    CAPSEOBFTDemTransactionMessageHandlerSec capSEOBFTDemTransactionMessageHandlerSec;

    CAPSEOBFTDemTransactionMessageHandlerTer capSEOBFTDemTransactionMessageHandlerTer;

    CAPSEOBFTPartialBlockManager capSEOBFTPartialBlockManager;

    CAPSEOBFTDemConsensusMessageHandler capSEOBFTDemConsensusMessageHandler;

    CAPSEOBFTDemConsensusRoundChangeHandler capSEOBFTDemConsensusRoundChangeHandler;

    CAPSEOBFTDemFullBlockFullfiller capSEOBFTDemFullBlockFullfiller;

    CAPSEOBFTDemPartialBlockFullfiller capSEOBFTDemPartialBlockFullfiller;

    CAPSEOBFTDemFullBlockManager capSEOBFTDemFullBlockManager;

    CAPSEOBFTDemFinaliseHandler capSEOBFTDemFinaliseHandler;

    CAPSEOBFTDemFullBlockHandler capSEOBFTDemFullBlockHandler;

    CAPSEOBFTDemHeartBeatHandler capSEOBFTDemHeartBeatHandler;

    CAPSEOBFTDemHeartBeatManager capSEOBFTDemHeartBeatManager;

    CAPSEOBFTDemPrivacyHandler capSEOBFTDemPrivacyHandler;

    CAPSEOBFTDemQuorumMessageHandler capSEOBFTDemQuorumMessageHandler;

    CAPSEOBFTDemQuorumManager capSEOBFTDemQuorumManager;

    Simulator_result simulator_result;

    Simulator_collator simulator_collator;
    Gson gson = new Gson();

    @Path("/getVersion")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion() {
        String version = "Simulator Test Bench:" + " " + simulator_service.getVersion();
        String json = gson.toJson(version);
        return json;
    }

    @Path("/setPeers/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storePeers(@PathParam("value") String value) {
        simulator_service.storeIPS(value);
        return gson.toJson("Peers Value Set");
    }

    @Path("/getPeers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeers() {
        String peers = simulator_service.getIPS();
        return gson.toJson(String.valueOf(peers));
    }

    @Path("/getIP")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIP() {
        String ip = simulator_service.getIP();
        return gson.toJson(String.valueOf(ip));
    }

    @Path("/setHeartBeat/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setHeartBeat(@PathParam("value") String value) {
        simulator_service.setHeartBeat(value);
        return gson.toJson("Heart Beat Frequency Out Set");
    }

    @Path("/setInitialQuorum")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setInitialQuorum() {
        simulator_service.setInitialQuorum();
        return gson.toJson("Initital Quorum Attempted");
    }

    @Path("/getHeartBeat")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getHeartBeat() {
        long heartBeatFrequency = simulator_service.getHeartBeat();
        return gson.toJson(String.valueOf(heartBeatFrequency));
    }

    @Path("/getInterBlockTimeCoefficient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInterBlockTimecoefficient() {
        String interBlockTimecoefficient = simulator_service.getInterBlockTimeCoefficient();
        return gson.toJson(String.valueOf(interBlockTimecoefficient));
    }

    @Path("/setEpochThreshold/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setEpochThreshold(@PathParam("value") String value) {
        simulator_service.setEpochThreshold(value);
        return gson.toJson("Epoch Threshold Set");
    }

    @Path("/getEpochThreshold")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getEpochThreshold(@PathParam("value") String value) {
        int epoch = simulator_service.getEpochThreshold();
        return gson.toJson(String.valueOf(epoch));
    }

    @Path("/setSubEpochThreshold/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setSubEpochThreshold(@PathParam("value") String value) {
        simulator_service.setSubEpochThreshold(value);
        return gson.toJson(" Sub Epoch Threshold Set");
    }

    @Path("/getSubEpochThreshold")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSubEpochThreshold(@PathParam("value") String value) {
        int subepoch = simulator_service.getSubEpochThreshold();
        return gson.toJson(String.valueOf(subepoch));
    }

    @Path("/setTotalQuorums/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setTotalQuorums(@PathParam("value") String value) {
        simulator_service.setTotalQuorums(value);
        return gson.toJson(" Total Quorums Set");
    }

    @Path("/getTotalQuorums")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTotalQuorums(@PathParam("value") String value) {
        int totalQuorums = simulator_service.getTotalQuorums();
        return gson.toJson(String.valueOf(totalQuorums));
    }

    // Set Privacy Id and is Privacy
    @Path("/setPrivacyId/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setPrivacyId(@PathParam("value") String value) {
        simulator_service.setPrivacyId(value);
        return gson.toJson("Privacy Id Set");
    }

    @Path("/getPrivacyId")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPrivacyId(@PathParam("value") String value) {
        int privacyId = simulator_service.getPrivacyId();
        return gson.toJson(String.valueOf(privacyId));
    }

    @Path("/setRoundChange/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setRoundchangeTimeOut(@PathParam("value") String value) {
        simulator_service.setRoundChange(value);
        return gson.toJson("Round Change Time Out Set");
    }

    @Path("/getRoundChange")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRoundChange() {
        long roundChangeTimeOut = simulator_service.getRoundChange();
        return gson.toJson(String.valueOf(roundChangeTimeOut));
    }

    @Path("/setFullBlockFullfillment/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setFullBlockFullfillment(@PathParam("value") String value) {
        simulator_service.setFullBlockFullfillment(value);
        return gson.toJson("Full Block Time Out Set");
    }

    @Path("/getFullBlockFullfillment")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFullBlockFullfillment() {
        long fullBlockFullfillmentTimeOut = simulator_service.getFullBlockFullfillment();
        return gson.toJson(String.valueOf(fullBlockFullfillmentTimeOut));
    }

    @Path("/setPartialBlockFullfillment/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String setPartialBlockFullfillment(@PathParam("value") String value) {
        simulator_service.setPartialBlockFullfillment(value);
        return gson.toJson("Partial Block Time Out Set");
    }

    @Path("/getPartialBlockFullfillment")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPartialBlockFullfillment() {
        long partialBlockFullfillmentTimeOut = simulator_service.getPartialBlockFullfillment();
        return gson.toJson(String.valueOf(partialBlockFullfillmentTimeOut));
    }

    @Path("/setNodeLatency/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeNodeLatency(@PathParam("value") String value) {
        simulator_service.storeNodeLatency(value);
        return gson.toJson("Node Latency Set");
    }

    @Path("/getNodeLatency")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodeLatency() {
        long latency = simulator_service.getNodeLatency();
        return gson.toJson(String.valueOf(latency));
    }

    @Path("/setAsValidator/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeAsValidator(@PathParam("value") String value) {
        simulator_service.storeAsValidator(value);
        return gson.toJson("Set As Validator:" + value);
    }

    @Path("/getAsValidator")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAsValidator() {
        boolean isValidator = simulator_service.getAsValidator();
        return gson.toJson(String.valueOf(isValidator));
    }

    @Path("/setNodeBehavior/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeNodeBehavior(@PathParam("value") String value) {
        simulator_service.storeNodeBehavior(value);
        return gson.toJson("Node Behavior Set");
    }

    @Path("/getNodeBehavior")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodeBehavior() {
        String behavior = simulator_service.getNodeBehavior();
        return gson.toJson(behavior);
    }

    @Path("/setNodeNetwork/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeNodeNetwork(@PathParam("value") String value) {
        simulator_service.storeNodeNetwork(value);
        return gson.toJson("Node Network Set");
    }

    @Path("/getNodeNetwork")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodeNetwork() {
        String network = simulator_service.getNodeNetwork();
        return gson.toJson(network);
    }

    @Path("/setBlockSize/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeBlockSize(@PathParam("value") String value) {
        simulator_service.storeBlockSize(value);
        return gson.toJson("Block Size Set");
    }

    @Path("/getBlockSize")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlockSize() {
        int blocksize = simulator_service.getBlockSize();
        return gson.toJson(String.valueOf(blocksize));
    }

    @Path("/setTotalNodes/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeTotalNodes(@PathParam("value") String value) {
        simulator_service.storeTotalNodes(value);
        return gson.toJson("Total Nodes Set");
    }

    @Path("/getTotalNodes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTotalNodes() {
        int totalnodes = simulator_service.getTotalNodes();
        return gson.toJson(String.valueOf(totalnodes));
    }

    @Path("/setBlackListThreshold/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeBlackListThreshold(@PathParam("value") String value) {
        simulator_service.storeBlackListThreshold(value);
        return gson.toJson("Black List Threshold Set");
    }

    @Path("/getBlackListThreshold")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlackListThreshold() {
        int blackListThreshold = simulator_service.getBlackListThreshold();
        return gson.toJson(String.valueOf(blackListThreshold));
    }

    @Path("/setTotalValidators/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeTotalValidators(@PathParam("value") String value) {
        simulator_service.storeTotalValidators(value);
        return gson.toJson("Total Validators Set");
    }

    @Path("/setTotalEffectiveMembers/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeTotalEffectiveMembers(@PathParam("value") String value) {
        simulator_service.setTotalEffectiveMembers(value);
        return gson.toJson("Total Effective Members Set only Initially");
    }

    @Path("/getTotalValidators")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTotalValidators() {
        int totalvalidators = simulator_service.getTotalValidators();
        return gson.toJson(String.valueOf(totalvalidators));
    }

    @Path("/setnodeProperty/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String storeNodeProperty(@PathParam("value") String value) {
        simulator_service.storeNodeProperty(value);

        this.nonValidator = new NonValidator();

        // Initialisation based on node id
        this.validator = new Validator();

        this.quorumStorage = new QuorumStorage();

        this.blockchain = new Blockchain(validator, nonValidator, this.quorumStorage);

        this.transactionPool = new TransactionPool();

        this.blockPool = new BlockPool();

        this.queueResource = new QueueResource();

        this.heartBeatStorage = new HeartBeatStorage();

        this.privacyStorage = new PrivacyStorage();

        this.utilitarianScoreStorage = new UtilitarianScoreStorage();

        this.utilitarianCalculator = new UtilitarianCalculator(this.blockchain, this.heartBeatStorage,
                this.quorumStorage,
                this.utilitarianScoreStorage, this.wallet);
        this.capSEOBFTDemMessagePool = new CAPSEOBFTDemMessagePool(this.utilitarianCalculator, this.quorumStorage);

        this.p2pserver = new P2PServer(this.blockchain, this.transactionPool, this.wallet, this.capSEOBFTDemMessagePool,
                this.validator, this.nonValidator, this.blockPool,
                this.queueResource);

        this.simulator_result = new Simulator_result();
        this.simulator_service = new Simulator_service(this.p2pserver, this.wallet, this.simulator_result,
                this.utilitarianScoreStorage, this.heartBeatStorage, this.blockchain, this.quorumStorage);

        this.synchronizer = new Synchronizer();

        return gson.toJson("Node Property Set");
    }

    @Path("/getnodeProperty")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getNodeProperty() {
        int instance = simulator_service.getNodeProperty();
        return String.valueOf(instance);
    }

    @Path("/initiateConnection")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String initiateConnection() {
        simulator_service.initiateConnection();
        return gson.toJson("Connection initiated");
    }

    @Path("/listentoPeers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listentoPeers() {
        simulator_service.listentoPeers();
        return gson.toJson("Listening to Peers");
    }

    @Path("/initialiseQuorum")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String initialiseQuorum() {
        // Thread
        this.capSEOBFTDemQuorumMessageHandler = new CAPSEOBFTDemQuorumMessageHandler(
                this.queueResource.getQuorumBlockingQueue(), this.blockchain, this.transactionPool, blockPool, wallet,
                validator, nonValidator, this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser,
                capSEOBFTDemMessagePool, synchronizer, simulator_result, quorumStorage);
        this.capSEOBFTDemQuorumMessageHandler.start();

        return gson.toJson("Initialised");
    }

    @Path("/initialise")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String initialise() {

        this.capSEOBFTDemQuorumManager = new CAPSEOBFTDemQuorumManager(this.queueResource.getQuorumBlockingQueue(),
                this.blockchain, this.transactionPool, blockPool, wallet, validator, nonValidator,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, capSEOBFTDemMessagePool, synchronizer,
                simulator_result, utilitarianCalculator, this.quorumStorage);
        this.capSEOBFTDemQuorumManager.start();

        // Depends on Node Communicator Object for communication
        // Thread
        this.capSEOBFTDemFinaliseHandler = new CAPSEOBFTDemFinaliseHandler(
                this.queueResource.getFinaliseBlockingQueue(), this.blockchain, this.transactionPool, blockPool, wallet,
                validator, nonValidator, this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser,
                capSEOBFTDemMessagePool, synchronizer, simulator_result, utilitarianCalculator, heartBeatStorage,
                utilitarianScoreStorage, this.capSEOBFTDemQuorumManager);
        this.capSEOBFTDemFinaliseHandler.start();
        // Timer Thread
        this.capSEOBFTDemHeartBeatManager = new CAPSEOBFTDemHeartBeatManager(this.p2pserver.getNodeCommunicator(),
                this.p2pserver.currentuser, synchronizer, this.blockchain, this.wallet, this.heartBeatStorage);

        // Thread
        this.capSEOBFTDemFullBlockHandler = new CAPSEOBFTDemFullBlockHandler(
                this.queueResource.getFullBlockBlockingQueue(), this.blockchain, this.transactionPool, blockPool,
                wallet,
                validator, nonValidator, this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser,
                capSEOBFTDemMessagePool, synchronizer, simulator_result, utilitarianScoreStorage, utilitarianCalculator,
                capSEOBFTDemQuorumManager);
        this.capSEOBFTDemFullBlockHandler.start();
        // Thread
        this.capSEOBFTDemHeartBeatHandler = new CAPSEOBFTDemHeartBeatHandler(heartBeatStorage, capSEOBFTDemMessagePool,
                this.queueResource.getHeartBeatBlockingQueue(), this.p2pserver.getNodeCommunicator());
        this.capSEOBFTDemHeartBeatHandler.start();
        // Thread
        this.capSEOBFTDemPrivacyHandler = new CAPSEOBFTDemPrivacyHandler(capSEOBFTDemMessagePool, wallet,
                this.transactionPool, this.queueResource.getPrivacyBlockingQueue());
        this.capSEOBFTDemPrivacyHandler.start();

        // Thread
        this.capSEOBFTDemTransactionMessageHandler = new CAPSEOBFTDemTransactionMessageHandler(
                this.queueResource.getTransactionBlockingQueue(), this.blockchain, this.transactionPool, this.blockPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.synchronizer, this.capSEOBFTDemQuorumManager, this.quorumStorage);
        this.capSEOBFTDemTransactionMessageHandler.start();

        // Thread
        this.capSEOBFTDemTransactionMessageHandlerSec = new CAPSEOBFTDemTransactionMessageHandlerSec(
                this.queueResource.getTransactionBlockingQueueSec(), this.blockchain, this.transactionPool,
                this.blockPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicatorSec(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.synchronizer, this.capSEOBFTDemQuorumManager, this.quorumStorage);
        this.capSEOBFTDemTransactionMessageHandlerSec.start();

        // Thread
        this.capSEOBFTDemTransactionMessageHandlerTer = new CAPSEOBFTDemTransactionMessageHandlerTer(
                this.queueResource.getTransactionBlockingQueueTer(), this.blockchain, this.transactionPool,
                this.blockPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicatorTer(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.synchronizer, this.capSEOBFTDemQuorumManager, this.quorumStorage);
        this.capSEOBFTDemTransactionMessageHandlerTer.start();

        this.capSEOBFTPartialBlockManager = new CAPSEOBFTPartialBlockManager(
                this.blockchain, this.transactionPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, this.quorumStorage);
        this.capSEOBFTPartialBlockManager.start();

        // Timer thread
        this.capSEOBFTDemConsensusRoundChangeHandler = new CAPSEOBFTDemConsensusRoundChangeHandler(
                this.p2pserver.getNodeCommunicator(),
                this.p2pserver.currentuser, this.synchronizer, this.blockchain, this.capSEOBFTDemMessagePool,
                this.wallet,
                this.blockPool);

        // Timer Thread for Fulfiller
        this.capSEOBFTDemFullBlockFullfiller = new CAPSEOBFTDemFullBlockFullfiller(this.blockchain, this.wallet,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.simulator_result, this.utilitarianCalculator, this.heartBeatStorage, this.utilitarianScoreStorage,
                this.quorumStorage);
        this.capSEOBFTDemPartialBlockFullfiller = new CAPSEOBFTDemPartialBlockFullfiller(this.transactionPool,
                this.blockchain, wallet,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, capSEOBFTDemMessagePool,
                simulator_result,
                utilitarianCalculator, heartBeatStorage, utilitarianScoreStorage, quorumStorage);

        // Thread
        this.capSEOBFTDemConsensusMessageHandler = new CAPSEOBFTDemConsensusMessageHandler(
                this.queueResource.getMessageBlockingQueue(), this.blockchain, this.transactionPool, this.blockPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.synchronizer,
                this.simulator_result, capSEOBFTDemConsensusRoundChangeHandler, utilitarianCalculator, heartBeatStorage,
                utilitarianScoreStorage, quorumStorage, capSEOBFTDemQuorumManager);
        this.capSEOBFTDemConsensusMessageHandler.start();

        this.capSEOBFTDemFullBlockManager = new CAPSEOBFTDemFullBlockManager(
                this.queueResource.getMessageBlockingQueue(), this.blockchain, this.transactionPool, this.blockPool,
                this.wallet,
                this.validator, this.nonValidator,
                this.p2pserver.getNodeCommunicator(), this.p2pserver.currentuser, this.capSEOBFTDemMessagePool,
                this.synchronizer,
                this.simulator_result, capSEOBFTDemConsensusRoundChangeHandler, utilitarianCalculator, heartBeatStorage,
                utilitarianScoreStorage, quorumStorage, capSEOBFTDemQuorumManager);
        this.capSEOBFTDemFullBlockManager.start();

        // Start Round Change Handler
        this.capSEOBFTDemConsensusRoundChangeHandler.scheduleTimer();
        this.capSEOBFTDemFullBlockFullfiller.scheduleTimer();
        this.capSEOBFTDemPartialBlockFullfiller.scheduleTimer();
        this.capSEOBFTDemHeartBeatManager.scheduleTimer();

        return gson.toJson("Initialised");
    }

    @Path("/startsimulation1/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation1(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        return gson.toJson("Simulation Started 1");
    }

    @Path("/startsimulation2/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation2(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        TransactionBroadcaster transactionBroadcasterSec = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSec.start();

        return gson.toJson("Simulation Started 2");
    }

    @Path("/startsimulation3/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation3(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        TransactionBroadcaster transactionBroadcasterSec = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSec.start();

        TransactionBroadcasterSec transactionBroadcasterTer = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterTer.start();

        return gson.toJson("Simulation Started 3");
    }

    @Path("/startsimulation4/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation4(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        TransactionBroadcaster transactionBroadcasterSec = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSec.start();

        TransactionBroadcasterSec transactionBroadcasterTer = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterTer.start();

        TransactionBroadcasterSec transactionBroadcasterfour = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterfour.start();

        return gson.toJson("Simulation Started 4");
    }

    @Path("/startsimulation5/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation5(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        TransactionBroadcaster transactionBroadcasterSec = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSec.start();

        TransactionBroadcasterSec transactionBroadcasterTer = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterTer.start();

        TransactionBroadcasterSec transactionBroadcasterfour = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterfour.start();

        TransactionBroadcasterTer transactionBroadcasterFive = new TransactionBroadcasterTer(
                this.p2pserver.nodeCommunicatorTer,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterFive.start();

        return gson.toJson("Simulation Started 5");
    }

    @Path("/startsimulation6/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulation6(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        // simulator_service.startSimulationSingle(notxs);
        TransactionBroadcaster transactionBroadcaster = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcaster.start();

        TransactionBroadcaster transactionBroadcasterSec = new TransactionBroadcaster(
                this.p2pserver.nodeCommunicator,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSec.start();

        TransactionBroadcasterSec transactionBroadcasterTer = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterTer.start();

        TransactionBroadcasterSec transactionBroadcasterfour = new TransactionBroadcasterSec(
                this.p2pserver.nodeCommunicatorSec,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterfour.start();

        TransactionBroadcasterTer transactionBroadcasterFive = new TransactionBroadcasterTer(
                this.p2pserver.nodeCommunicatorTer,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterFive.start();

        TransactionBroadcasterTer transactionBroadcasterSix = new TransactionBroadcasterTer(
                this.p2pserver.nodeCommunicatorTer,
                wallet, Integer.valueOf(notxs));
        transactionBroadcasterSix.start();

        return gson.toJson("Simulation Started 6");
    }

    @Path("/sendMessageToPeer/{message}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String sendMessageToPeer(@PathParam("message") String message) {
        simulator_service.sendMessageToPeer(message);
        return gson.toJson("Message Sent/Broadcasted");
    }
    /*
     * @Path("/startsimulation/{notxs}/{nothreads}")
     * 
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_JSON)
     * public String startSimulation(@PathParam("notxs") String
     * notxs, @PathParam("nothreads") String nothreads) {
     * simulator_service.startSimulation(notxs, nothreads);
     * return "Simulation Started";
     * }
     */

    // Better way of simulation with nodes interconnected
    @Path("/startResultCollator")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startResultCollator() {
        // Start the result collator thread
        this.simulator_collator = new Simulator_collator(this.simulator_result, this.transactionPool, this.blockchain,
                this.capSEOBFTDemMessagePool);
        this.simulator_collator.start();
        return gson.toJson("Collator Started");
    }

    @Path("/getQuorumStorage/{subepoch}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getQuorumStorage(@PathParam("subepoch") String subepoch) {
        // Get the quorum message from the storage
        QuorumMessage quorumMessage = null;
        if (this.quorumStorage.getQuorumMessageMap().containsKey(Integer.valueOf(subepoch))) {
            quorumMessage = this.quorumStorage.getQuorumMessageMap().get(Integer.valueOf(subepoch));
        }
        String quorumMessageString = quorumMessage.toString();
        return gson.toJson("QuorumStorage" + quorumMessageString);
    }

    @Path("/getTransactionStorage/{round}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionStorage(@PathParam("round") String roundno) {
        // Get the quorum message from the storage

        String size = simulator_service.getTransactionsinStorage(roundno);
        return gson.toJson("TransactionStorage" + size);
    }

    @Path("/stopResultCollator")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String stopResultCollator() {
        // Stop the result collator thread
        this.simulator_collator.stopThread();
        return gson.toJson("Collator Stopped");
    }

    // Better way of simulation with nodes interconnected
    @Path("/startsimulationsingle/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulationSingle(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        simulator_service.startSimulationSingle(notxs);
        return gson.toJson("Simulation Started");
    }

    // Better way of simulation with nodes interconnected
    @Path("/startsimulationsingleprivacy/{notxs}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String startSimulationSinglePrivacy(@PathParam("notxs") String notxs) {
        // Start Actual Simulation
        simulator_service.startSimulationSinglePrivacy(notxs);
        return gson.toJson("Simulation Privacy Started");
    }

    @Path("/getAllScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllScore() {
        return gson.toJson(simulator_service.getAllScore());
    }

    @Path("/getUtilitarianEffectiveScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianEffectiveScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianEffectiveScoreGrid());
    }

    @Path("/getUtilitarianCommitScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianCommitScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianCommitScoreGrid());
    }

    @Path("/getUtilitarianBlockProposalScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianBlockProposalScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianBlockProposalScoreGrid());
    }

    @Path("/getUtilitarianMissedCommitScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedCommitScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianMissedCommitScoreGrid());
    }

    @Path("/getUtilitarianMaliciousScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMaliciousScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianMaliciousScoreGrid());
    }

    @Path("/getUtilitarianHeartBeatScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianHeartBeatScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianHeartBeatScoreGrid());
    }

    @Path("/getUtilitarianMissedHeartBeatScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedHeartBeatScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianMissedHeartBeatScoreGrid());
    }

    @Path("/getUtilitarianMissedBlockProposalScoreGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedBlockProposalScoreGrid() {
        return gson.toJson(simulator_service.getUtilitarianMissedBlockProposalScoreGrid());
    }

    ////////////// Non Cumulative

    @Path("/getUtilitarianEffectiveScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianEffectiveScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianEffectiveScoreGridNonCumul());
    }

    @Path("/getUtilitarianCommitScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianCommitScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianCommitScoreGridNonCumul());
    }

    @Path("/getUtilitarianBlockProposalScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianBlockProposalScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianBlockProposalScoreGridNonCumul());
    }

    @Path("/getUtilitarianMissedCommitScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedCommitScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianMissedCommitScoreGridNonCumul());
    }

    @Path("/getUtilitarianMaliciousScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMaliciousScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianMaliciousScoreGridNonCumul());
    }

    @Path("/getUtilitarianHeartBeatScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianHeartBeatScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianHeartBeatScoreGridNonCumul());
    }

    @Path("/getUtilitarianMissedHeartBeatScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedHeartBeatScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianMissedHeartBeatScoreGridNonCumul());
    }

    @Path("/getUtilitarianMissedBlockProposalScoreGridNonCumul")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianMissedBlockProposalScoreGridNonCumul() {
        return gson.toJson(simulator_service.getUtilitarianMissedBlockProposalScoreGridNonCumul());
    }
    /////////////////////////////

    @Path("/getUtilitarianClassificationGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianClassificationGrid() {
        return gson.toJson(simulator_service.getUtilitarianClassificationGrid());
    }

    @Path("/getQuorumSuspensionGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getQuorumSuspensionGrid() {
        return gson.toJson(simulator_service.getQuorumSuspensionGrid());
    }

    @Path("/getUtilitarianInterBlockTimeCoefficientGrid")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUtilitarianInterBlockTimeCoefficientGrid() {
        return gson.toJson(simulator_service.getUtilitarianInterBlockTimeCoefficientGrid());
    }

    @Path("/getHeartBeatStorage")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getHeartBeatStorage() {
        return gson.toJson(simulator_service.getHeartBeatStorage());
    }

    @Path("/getPeerUtilitarianBlockProposalScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianBlockProposalScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianBlockProposalScore());
    }

    @Path("/getPeerUtilitarianCommitScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianCommitScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianCommitScore());
    }

    @Path("/getPeerUtilitarianMissedBlockProposalScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianMissedBlockProposalScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianMissedBlockProposalScore());
    }

    @Path("/getPeerUtilitarianMissedCommitScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianMissedCommitScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianMissedCommitScore());
    }

    @Path("/getPeerUtilitarianMaliciousScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianMaliciousScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianMaliciousScore());
    }

    @Path("/getPeerUtilitarianHeartBeatScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianHeartBeatScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianHeartBeatScore());
    }

    @Path("/getPeerUtilitarianMissedHeartBeatScore")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPeerUtilitarianMissedHeartBeatScore() {
        return gson.toJson(simulator_service.getPeerUtilitarianMissedHeartBeatScore());
    }

    @Path("/getBlocks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlocks() {
        return gson.toJson(simulator_service.getBlocks());
    }

    @Path("/getEphemeralBlocks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getEphemeralBlocks() {
        return gson.toJson(simulator_service.getEphemeralBlocks());
    }

    @Path("/getBlocksSize")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlocksSize() {
        return gson.toJson(simulator_service.getBlocksSize());
    }

    @Path("/getBlocksinPool")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlocksinPool() {
        return gson.toJson(simulator_service.getBlocksinBlockPool());
    }

    @Path("/getTransactionsValidatedNo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionsValidatedNo() {
        return gson.toJson(simulator_service.getTotalTransactionsValidatedNo());
    }

    @Path("/getTransactionsinPoolNo/{roundno}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTransactionsinPoolNo(@PathParam("roundno") String roundno) {
        return gson.toJson(simulator_service.getTransactionsinPoolNo(roundno));
    }

    @Path("/getBlockNo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getBlockNo() {
        return gson.toJson(String.valueOf(simulator_service.getNoofBlocks()));
    }

    @Path("/getProcessedTPS")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProcessedTPS() {
        return gson.toJson(simulator_service.getInputTPSJSON());
    }

    @Path("/getPartialTPS")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPartialPS() {
        return gson.toJson(simulator_service.getPartialTPSJSON());
    }

    @Path("/getFinalisedTPS")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFinalisedTPS() {
        return gson.toJson(simulator_service.getFinalisedTPSJSON());
    }

    @Path("/getProposeRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPrepareRate() {
        return gson.toJson(simulator_service.getProposeMPSJSON());
    }

    @Path("/getCommitRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCommitRate() {
        return gson.toJson(simulator_service.getCommitMPSJSON());
    }

    @Path("/getFinaliseRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFinaliseRate() {
        return gson.toJson(simulator_service.getFinaliseMPSJSON());
    }

    @Path("/getQuorumRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getQuorumRate() {
        return gson.toJson(simulator_service.getQuorumMPSJSON());
    }

    @Path("/getHeartBeatRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getHeartBeatRate() {
        return gson.toJson(simulator_service.getHeatBeatMPSJSON());
    }

    @Path("/getRoundChangeRate")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRoundChangeRate() {
        return gson.toJson(simulator_service.getRoundChangeMPSJSON());
    }
}
