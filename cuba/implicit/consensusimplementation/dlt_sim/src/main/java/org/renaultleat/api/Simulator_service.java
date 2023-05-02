package org.renaultleat.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.renaultleat.chain.Block;
import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.PartialBlock;
import org.renaultleat.network.UtilitarianScoreStorage;
import org.renaultleat.network.HeartBeatStorage;
import org.renaultleat.network.MainGrid;
import org.renaultleat.network.P2PServer;
import org.renaultleat.network.PrivacyStorage;
import org.renaultleat.network.QuorumStorage;
import org.renaultleat.node.Wallet;
import org.renaultleat.properties.CAPSEOBFTProperty;
import org.renaultleat.properties.NodeProperty;

public class Simulator_service {

    P2PServer p2pServer;

    Wallet wallet;

    Simulator_result simulator_result;

    UtilitarianScoreStorage utilitarianScoreStorage;

    HeartBeatStorage heartBeatStorage;

    Blockchain blockchain;

    QuorumStorage quorumStorage;

    public Simulator_service(P2PServer p2pServer, Wallet wallet, Simulator_result simulator_result,
            UtilitarianScoreStorage utilitarianScoreStorage, HeartBeatStorage heartBeatStorage, Blockchain blockchain,
            QuorumStorage quorumStorage) {
        this.p2pServer = p2pServer;
        this.wallet = wallet;
        this.simulator_result = simulator_result;
        this.utilitarianScoreStorage = utilitarianScoreStorage;
        this.heartBeatStorage = heartBeatStorage;
        this.quorumStorage = quorumStorage;
        this.blockchain = blockchain;
    }

    public Simulator_service(Wallet wallet) {
        this.wallet = wallet;
    }

    public void storeNodeProperty(String value) {
        Wallet.setnodeproperty(value);
        wallet.initalise(Integer.parseInt(value));
    }

    public void storeBlackListThreshold(String value) {
        CAPSEOBFTProperty.setBlackListThreshold(Integer.valueOf(value));

    }

    public int getBlackListThreshold() {
        return CAPSEOBFTProperty.getBlackListThreshold();

    }

    public void storeNodeLatency(String value) {
        NodeProperty.setnodeLatency(Long.valueOf(value));

    }

    public long getNodeLatency() {
        return NodeProperty.getnodeLatency();

    }

    public void setRoundChange(String value) {
        NodeProperty.setRoundChange(Long.valueOf(value));

    }

    public long getRoundChange() {
        return NodeProperty.getRoundChange();

    }

    public void setFullBlockFullfillment(String value) {
        NodeProperty.setFullBlockFullfillment(Long.valueOf(value));

    }

    public long getFullBlockFullfillment() {
        return NodeProperty.getFullBlockFullfillment();

    }

    public void setPartialBlockFullfillment(String value) {
        NodeProperty.setPartialBlockFullfillment(Long.valueOf(value));

    }

    public long getPartialBlockFullfillment() {
        return NodeProperty.getPartialBlockFullfillment();

    }

    public void setHeartBeat(String value) {
        NodeProperty.setHeartBeatBroadcast(Long.valueOf(value));

    }

    public long getHeartBeat() {
        return NodeProperty.getHeartBeatBroadcast();

    }

    public void setEpochThreshold(String value) {
        CAPSEOBFTProperty.setEpochThreshold(Integer.valueOf(value));
    }

    public int getEpochThreshold() {
        return CAPSEOBFTProperty.epochthreshold;
    }

    public void setSubEpochThreshold(String value) {
        CAPSEOBFTProperty.setSubEpochThreshold(Integer.valueOf(value));
    }

    public int getSubEpochThreshold() {
        return CAPSEOBFTProperty.subepochthreshold;
    }

    public void setTotalQuorums(String value) {
        CAPSEOBFTProperty.setTotalquorums(Integer.valueOf(value));
    }

    public int getTotalQuorums() {
        return CAPSEOBFTProperty.getTotalQuorums();
    }

    public void setPrivacyId(String value) {
        PrivacyStorage.setIsPrivacy(true);
        PrivacyStorage.setPrivacyGroupId(value);
    }

    public int getPrivacyId() {
        return PrivacyStorage.getPrivacyGroupId();
    }

    public void storeAsValidator(String value) {
        NodeProperty.setIsValidator(Boolean.valueOf(value));

    }

    public boolean getAsValidator() {
        return NodeProperty.isValidator();

    }

    public void storeNodeBehavior(String value) {
        NodeProperty.setnodeBehavior(Integer.valueOf(value));

    }

    public String getNodeBehavior() {
        if (NodeProperty.getnodeBehavior() == 0) {
            return "Honest [:)";
        } else if (NodeProperty.getnodeBehavior() == 1) {
            return "Failed Node [:(";
        } else {
            return "DisHonest Node [:|(";
        }

    }

    public void setTotalEffectiveMembers(String input) {
        QuorumStorage.setTotalEffectiveMembers(Integer.valueOf(input));

    }

    public void setInitialQuorum() {
        // System.out.println("Intitial Quoum Cqlled");
        p2pServer.setGenesisQuorum();

    }

    public void storeNodeNetwork(String value) {
        NodeProperty.setnodeNetwork(value);

    }

    public String getNodeNetwork() {
        return NodeProperty.getnodeNetwork();

    }

    public void storeBlockSize(String value) {
        NodeProperty.setBlockSize(Integer.valueOf(value));

    }

    public void storeIPS(String value) {
        NodeProperty.setIPS(value);

    }

    public String getIPS() {
        return NodeProperty.getIPS();

    }

    public String getIP() {
        return NodeProperty.getIP();

    }

    public int getBlockSize() {
        return NodeProperty.getBlockSize();

    }

    public void storeTotalNodes(String value) {
        NodeProperty.settotalNodes(Integer.valueOf(value));

    }

    public int getTotalNodes() {
        return NodeProperty.gettotalNodes();

    }

    public void storeTotalValidators(String value) {
        NodeProperty.setValidators(Integer.valueOf(value));

    }

    public int getTotalValidators() {
        return NodeProperty.getValidators();

    }

    public String getVersion() {
        String version = NodeProperty.deployed;
        return version;

    }

    public int getNodeProperty() {
        int index = Integer.valueOf(Wallet.getNodeproperty());
        return index;

    }

    public String getAllScore() {
        String finalScore = "";
        finalScore += "UtilitarianBlockProposalScore:" + getPeerUtilitarianBlockProposalScore() + ";";
        finalScore += "AltruisCommitScore:" + getPeerUtilitarianCommitScore() + ";";
        finalScore += "UtilitarianMissedBlockProposalScore:" + getPeerUtilitarianMissedBlockProposalScore() + ";";
        finalScore += "UtilitarianMissedCommitScore:" + getPeerUtilitarianMissedCommitScore() + ";";
        finalScore += "UtilitarianMaliciousScore:" + getPeerUtilitarianMaliciousScore() + ";";
        finalScore += "UtilitarianHeartBeatScore:" + getPeerUtilitarianHeartBeatScore() + ";";
        finalScore += "UtilitarianMissedHeartBeatScore:" + getPeerUtilitarianMissedHeartBeatScore() + ";";
        finalScore += "InterBlok:" + this.utilitarianScoreStorage
                .getInterBlockTimeCoefficient().toString() + ";";
        return finalScore;
    }
    /// GRID HELPERS ///////////////////////////////////////////////////////////

    public MainGrid getUtilitarianEffectiveScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianEffectiveStorage = this.quorumStorage
                .getRawEffectiveUtilitarianScore();
        populateGridObject(peerUtilitarianEffectiveStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianCommitScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianCommitStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianCommitScore();
        populateGridObject(peerUtilitarianCommitStorage, mainGrid.getGridHeaderArray(), mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianBlockProposalScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianBlockProposalStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianBlockProposalScore();
        populateGridObject(peerUtilitarianBlockProposalStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedCommitScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedCommitStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedCommitScore();
        populateGridObject(peerUtilitarianMissedCommitStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedBlockProposalScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedBlockProposalStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedBlockProposalScore();
        populateGridObject(peerUtilitarianMissedBlockProposalStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMaliciousScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMaliciousStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMaliciousScore();
        populateGridObject(peerUtilitarianMaliciousStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianHeartBeatScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianHeartBeatStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianHeartBeatScore();
        populateGridObject(peerUtilitarianHeartBeatStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedHeartBeatScoreGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedHeartBeatStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedHeartBeatScore();
        populateGridObject(peerUtilitarianMissedHeartBeatStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    /// Non CUMULATIVE GRID HELPERS
    /// ///////////////////////////////////////////////////////////

    public MainGrid getUtilitarianEffectiveScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianEffectiveStorage = this.quorumStorage
                .getRawEffectiveUtilitarianScore();
        populateGridNonCumulativeObject(peerUtilitarianEffectiveStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianCommitScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianCommitStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianCommitScore();
        populateGridNonCumulativeObject(peerUtilitarianCommitStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianBlockProposalScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianBlockProposalStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianBlockProposalScore();
        populateGridNonCumulativeObject(peerUtilitarianBlockProposalStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedCommitScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedCommitStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedCommitScore();
        populateGridNonCumulativeObject(peerUtilitarianMissedCommitStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedBlockProposalScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedBlockProposalStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedBlockProposalScore();
        populateGridNonCumulativeObject(peerUtilitarianMissedBlockProposalStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMaliciousScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMaliciousStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMaliciousScore();
        populateGridNonCumulativeObject(peerUtilitarianMaliciousStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianHeartBeatScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianHeartBeatStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianHeartBeatScore();
        populateGridNonCumulativeObject(peerUtilitarianHeartBeatStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianMissedHeartBeatScoreGridNonCumul() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianMissedHeartBeatStorage = this.utilitarianScoreStorage
                .getRawPeerUtilitarianMissedHeartBeatScore();
        populateGridNonCumulativeObject(peerUtilitarianMissedHeartBeatStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }
    /////////////////////////////////////////////////////////////////

    public MainGrid getUtilitarianClassificationGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, String>> peerUtilitarianClassificationStorage = this.quorumStorage
                .getRawUtilitarianClassification(this.blockchain.getFinalSubEpochCounter());
        populateGridClassificationObject(peerUtilitarianClassificationStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getQuorumSuspensionGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, String> peerQuorumSuspensionStorage = this.quorumStorage
                .getRawQuorumSuspension(this.blockchain.getFinalSubEpochCounter());
        populateGridSuspensionObject(peerQuorumSuspensionStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public MainGrid getUtilitarianInterBlockTimeCoefficientGrid() {
        MainGrid mainGrid = new MainGrid();
        Map<Integer, Map<Integer, Double>> peerUtilitarianInterBlockTimeStorage = this.utilitarianScoreStorage
                .getRawInterBlockTimeCoefficient();
        populateGridTimeObject(peerUtilitarianInterBlockTimeStorage, mainGrid.getGridHeaderArray(),
                mainGrid.getScoreJSONValues());
        return mainGrid;
    }

    public void populateGridTimeObject(Map<Integer, Map<Integer, Double>> storageMap,
            JsonArray gridHeader, JsonArray timeValues) {

        /// Default ///
        JsonObject jSONObject = new JsonObject();
        jSONObject.addProperty("headerName", "SubEpoch");
        jSONObject.addProperty("field", "subepoch");
        gridHeader.add(jSONObject);
        /// Finished Default ///

        int finalSubEpochCounter = this.blockchain.getFinalSubEpochCounter();

        /// Start Column Name Population ///

        for (int i = 1; i <= CAPSEOBFTProperty.getSubEpochThreshold(); i++) {
            JsonObject jSONObject1 = new JsonObject();
            jSONObject1.addProperty("headerName", "Block" + i);
            jSONObject1.addProperty("field", "block" + i);
            gridHeader.add(jSONObject1);
        }

        /// Finished Column Name population ///

        // Node Index, SubEpoch, Actual Value
        for (Map.Entry<Integer, Map<Integer, Double>> entry : storageMap.entrySet()) {
            JsonObject jSONObject2 = new JsonObject();
            jSONObject2.addProperty("subepoch", "subepoch" + entry.getKey());
            double cumulativescore = 0;
            Map<Integer, Double> timeValue = entry.getValue();
            for (int blocko = 1; blocko <= CAPSEOBFTProperty.getSubEpochThreshold(); blocko++) {

                if (timeValue.containsKey(blocko)) {
                    cumulativescore = timeValue.get(blocko);
                    jSONObject2.addProperty("block" + blocko, cumulativescore);
                } else {
                    jSONObject2.addProperty("block" + blocko, cumulativescore);
                }
            }
            timeValues.add(jSONObject2);
        }
    }

    public void populateGridObject(Map<Integer, Map<Integer, Double>> storageMap,
            JsonArray gridHeader, JsonArray scoreValues) {

        /// Default ///
        JsonObject jSONObject = new JsonObject();
        jSONObject.addProperty("headerName", "Node");
        jSONObject.addProperty("field", "node");
        gridHeader.add(jSONObject);
        /// Finished Default ///

        int finalSubEpochCounter = this.blockchain.getFinalSubEpochCounter();

        /// Start Column Name Population ///
        for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
            JsonObject jSONObject1 = new JsonObject();
            jSONObject1.addProperty("headerName", "SubEpoch" + subEpoch);
            jSONObject1.addProperty("field", "subepoch" + subEpoch);
            gridHeader.add(jSONObject1);

        }

        /// Finished Column Name population ///

        // Node Index, SubEpoch, Actual Value
        for (Map.Entry<Integer, Map<Integer, Double>> entry : storageMap.entrySet()) {
            JsonObject jSONObject2 = new JsonObject();
            jSONObject2.addProperty("node", "node" + entry.getKey());

            double cumulativescore = 0;
            Map<Integer, Double> scoreValue = entry.getValue();
            for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
                if (scoreValue.containsKey(subEpoch)) {
                    cumulativescore += scoreValue.get(subEpoch);
                    jSONObject2.addProperty("subepoch" + subEpoch, cumulativescore);
                } else {
                    jSONObject2.addProperty("subepoch" + subEpoch, cumulativescore);
                }
            }
            scoreValues.add(jSONObject2);
        }
    }

    public void populateGridNonCumulativeObject(Map<Integer, Map<Integer, Double>> storageMap,
            JsonArray gridHeader, JsonArray scoreValues) {

        /// Default ///
        JsonObject jSONObject = new JsonObject();
        jSONObject.addProperty("headerName", "Node");
        jSONObject.addProperty("field", "node");
        gridHeader.add(jSONObject);
        /// Finished Default ///

        int finalSubEpochCounter = this.blockchain.getFinalSubEpochCounter();

        /// Start Column Name Population ///
        for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
            JsonObject jSONObject1 = new JsonObject();
            jSONObject1.addProperty("headerName", "SubEpoch" + subEpoch);
            jSONObject1.addProperty("field", "subepoch" + subEpoch);
            gridHeader.add(jSONObject1);

        }

        /// Finished Column Name population ///

        // Node Index, SubEpoch, Actual Value
        for (Map.Entry<Integer, Map<Integer, Double>> entry : storageMap.entrySet()) {
            JsonObject jSONObject2 = new JsonObject();
            jSONObject2.addProperty("node", "node" + entry.getKey());

            double cumulativescore = 0;
            Map<Integer, Double> scoreValue = entry.getValue();
            for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
                if (scoreValue.containsKey(subEpoch)) {
                    cumulativescore = scoreValue.get(subEpoch);
                    jSONObject2.addProperty("subepoch" + subEpoch, cumulativescore);
                } else {
                    jSONObject2.addProperty("subepoch" + subEpoch, cumulativescore);
                }
            }
            scoreValues.add(jSONObject2);
        }
    }

    public void populateGridClassificationObject(Map<Integer, Map<Integer, String>> storageMap,
            JsonArray gridHeader, JsonArray classificationValues) {

        /// Default ///
        JsonObject jSONObject = new JsonObject();
        jSONObject.addProperty("headerName", "Node");
        jSONObject.addProperty("field", "node");
        gridHeader.add(jSONObject);
        /// Finished Default ///

        int finalSubEpochCounter = this.blockchain.getFinalSubEpochCounter();

        /// Start Column Name Population ///
        for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
            JsonObject jSONObject1 = new JsonObject();
            jSONObject1.addProperty("headerName", "SubEpoch" + subEpoch);
            jSONObject1.addProperty("field", "subepoch" + subEpoch);
            gridHeader.add(jSONObject1);

        }

        /// Finished Column Name population ///

        // Node Index, SubEpoch, Actual Value
        for (Map.Entry<Integer, Map<Integer, String>> entry : storageMap.entrySet()) {
            JsonObject jSONObject2 = new JsonObject();
            jSONObject2.addProperty("node", "node" + entry.getKey());

            String classification = "None";
            Map<Integer, String> scoreValue = entry.getValue();
            for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
                if (scoreValue.containsKey(subEpoch)) {
                    classification = scoreValue.get(subEpoch);
                    jSONObject2.addProperty("subepoch" + subEpoch, classification);
                } else {
                    jSONObject2.addProperty("subepoch" + subEpoch, classification);
                }
            }
            classificationValues.add(jSONObject2);
        }
    }

    public void populateGridSuspensionObject(Map<Integer, String> storageMap,
            JsonArray gridHeader, JsonArray suspendedValues) {

        /// Default ///
        JsonObject jSONObject = new JsonObject();
        jSONObject.addProperty("headerName", "Suspended Node");
        jSONObject.addProperty("field", "node");
        gridHeader.add(jSONObject);
        /// Finished Default ///

        int finalSubEpochCounter = this.blockchain.getFinalSubEpochCounter();

        /// Start Column Name Population ///
        for (int subEpoch = 1; subEpoch < finalSubEpochCounter; subEpoch++) {
            JsonObject jSONObject1 = new JsonObject();
            jSONObject1.addProperty("headerName", "SubEpoch" + subEpoch);
            jSONObject1.addProperty("field", "subepoch" + subEpoch);
            gridHeader.add(jSONObject1);

        }

        /// Finished Column Name population ///

        // Node Index, SubEpoch, Actual Value
        for (Map.Entry<Integer, String> entry : storageMap.entrySet()) {
            JsonObject jSONObject2 = new JsonObject();
            jSONObject2.addProperty("node", "suspended node");
            jSONObject2.addProperty("subepoch" + entry.getKey(), entry.getValue());
            suspendedValues.add(jSONObject2);
        }
    }

    ///// END GRID HELPERS
    ///// //////////////////////////////////////////////////////////////////////////////////////////

    public String getInterBlockTimeCoefficient() {
        return this.utilitarianScoreStorage.getInterBlockTimeCoefficient().toString();
    }

    public String getHeartBeatStorage() {
        return this.heartBeatStorage.getPeerUtilitarianHeartBeat().toString();
    }

    public String getPeerUtilitarianBlockProposalScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianBlockProposalScore();
    }

    public String getPeerUtilitarianCommitScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianCommitScore();
    }

    public String getPeerUtilitarianMissedBlockProposalScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianMissedBlockProposalScore();
    }

    public String getPeerUtilitarianMissedCommitScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianMissedCommitScore();
    }

    public String getPeerUtilitarianMaliciousScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianMaliciousScore();
    }

    public String getPeerUtilitarianHeartBeatScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianHeartBeatScore();
    }

    public String getPeerUtilitarianMissedHeartBeatScore() {
        return this.utilitarianScoreStorage.getPeerUtilitarianMissedHeartBeatScore();
    }

    public String getBlocks() {
        String value = "";
        Map<Integer, Block> chain = p2pServer.blockchain.getChain();
        for (Entry<Integer, Block> blockEntry : chain.entrySet()) {
            value += "blockno: " + blockEntry.getValue().getBlocknumber() + "; " + "blockhash: "
                    + blockEntry.getValue().getBlockHash() + "; ";
            for (Entry<Integer, PartialBlock> partialBlockEntry : blockEntry.getValue().partialBlockMap
                    .entrySet()) {
                value += "partialblockid:" + partialBlockEntry.getValue().getMinorBlocknumber() + "partialblocksize: "
                        + partialBlockEntry.getValue().getBlockData().size() + "; ";
            }
        }
        return value;

    }

    public String getEphemeralBlocks() {
        String value = "";
        Map<Integer, Block> chain = p2pServer.blockchain.getEphemeralChain();
        for (Entry<Integer, Block> blockEntry : chain.entrySet()) {
            value += "blockno: " + blockEntry.getValue().getBlocknumber() + "; " + "temporalhashSize: "
                    + blockEntry.getValue().getTemporalHashingState().size() + "; ";
            for (Entry<Integer, PartialBlock> partialBlockEntry : blockEntry.getValue().partialBlockMap
                    .entrySet()) {
                value += "partialblockid:" + partialBlockEntry.getValue().getMinorBlocknumber() + "partialblocksize: "
                        + partialBlockEntry.getValue().getBlockData().size() + "; ";
            }
        }
        return value;

    }

    public String getBlocksSize() {
        Map<Integer, Block> chain = p2pServer.blockchain.getChain();

        return String.valueOf(chain.size());

    }

    // We dont have block pool as we manage with ephemeral chain and final chain
    public String getBlocksinBlockPool() {
        String value = "";
        if (p2pServer != null) {
            List<Block> chain = p2pServer.blockPool.getBlocks();
            for (Block block : chain) {
                value += "blockno: " + block.getBlocknumber() + "; " + "proposer: " + block.getProposer();
            }
        }
        return value;

    }

    public String getTotalTransactionsValidatedNo() {
        Integer total = 0;
        if (p2pServer != null) {
            Map<Integer, Block> chain = p2pServer.blockchain.getChain();
            for (Entry<Integer, Block> blockEntry : chain.entrySet()) {
                // for (Entry<Integer, PartialBlock> partialBlockEntry :
                // blockEntry.getValue().partialBlockMap
                // .entrySet()) {
                total += NodeProperty.getBlockSize();
                // }
            }
            return String.valueOf(total);
        }
        return "0";
    }

    public String getTransactionsinPoolNo(String roundno) {
        int roundnumber = Integer.valueOf(roundno);
        if (p2pServer != null) {
            int size = p2pServer.transactionPool.transactionStorage.get(roundnumber).size();
            String value = String.valueOf(size);
            return value;
        }
        return "0";

    }

    public String getTransactionsinStorage(String roundno) {
        int roundnumber = Integer.valueOf(roundno);
        if (p2pServer != null) {
            int size = p2pServer.transactionPool.transactionStorage.get(roundnumber).size();
            String value = String.valueOf(size);
            return value;
        }
        return "0";

    }

    public int getNoofBlocks() {
        if (p2pServer != null) {
            int noofblocks = p2pServer.blockchain.getChain().size();
            return noofblocks;
        }
        return 0;

    }

    public void initiateConnection() {
        if (p2pServer != null) {
            try {
                p2pServer.connect();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void listentoPeers() {
        if (p2pServer != null) {
            try {
                p2pServer.connectToPeers();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void sendMessageToPeer(String message) {
        if (p2pServer != null) {
            try {
                p2pServer.communicate(message);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void startSimulation(String notxs, String nothreads) {
        if (p2pServer != null) {
            int no_of_txs = Integer.valueOf(notxs);
            int no_of_threads = Integer.valueOf(nothreads);
            try {
                p2pServer.startSimulationBroadcast(no_of_txs, no_of_threads);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void startSimulationSingle(String notxs) {
        if (p2pServer != null) {
            int no_of_txs = Integer.valueOf(notxs);
            try {
                p2pServer.startSimulationBroadcastSingle(no_of_txs);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void startSimulationSinglePrivacy(String notxs) {
        if (p2pServer != null) {
            int no_of_txs = Integer.valueOf(notxs);
            try {
                p2pServer.startSimulationBroadcastPrivacySingle(no_of_txs);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Map<Timestamp, Integer> getInputTPS() {
        Map<Timestamp, Integer> inputTps = new LinkedHashMap<Timestamp, Integer>();
        Map<Timestamp, Integer> inputTpsStorage = this.simulator_result.getInputTPSStorage();
        List<Timestamp> keys = new ArrayList<>(inputTpsStorage.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            // Millisecond
            long milliseconddifference = keys.get(i + 1).getTime() - keys.get(i).getTime();
            int second = (int) milliseconddifference / 1000;
            int txdifference = inputTpsStorage.get(keys.get(i + 1)) - inputTpsStorage.get(keys.get(i));
            int tps = txdifference / second;
            inputTps.put(keys.get(i + 1), tps);

        }
        return inputTps;
    }

    public Map<Timestamp, Integer> getPartialTPS() {
        Map<Timestamp, Integer> partialTps = new LinkedHashMap<Timestamp, Integer>();
        Map<Timestamp, Integer> partialTpsStorage = this.simulator_result.getPartialTPSStorage();
        List<Timestamp> keys = new ArrayList<>(partialTpsStorage.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            // Millisecond
            long milliseconddifference = keys.get(i + 1).getTime() - keys.get(i).getTime();
            int second = (int) milliseconddifference / 1000;
            int txdifference = partialTpsStorage.get(keys.get(i + 1)) * NodeProperty.blocksize
                    - partialTpsStorage.get(keys.get(i)) * NodeProperty.blocksize;
            int tps = txdifference / second;
            partialTps.put(keys.get(i + 1), tps);

        }
        return partialTps;
    }

    public Map<Timestamp, Integer> getFinalisedTPS() {

        Map<Timestamp, Integer> finalisedTps = new LinkedHashMap<Timestamp, Integer>();
        Map<Timestamp, Integer> finalisedTpsStorage = this.simulator_result.getFinalisedTPSStorage();
        List<Timestamp> keys = new ArrayList<>(finalisedTpsStorage.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            // Millisecond
            long milliseconddifference = keys.get(i + 1).getTime() - keys.get(i).getTime();
            int second = (int) milliseconddifference / 1000;
            int txdifference = finalisedTpsStorage.get(keys.get(i + 1)) * NodeProperty.blocksize
                    - finalisedTpsStorage.get(keys.get(i)) * NodeProperty.blocksize;
            int tps = txdifference / second;
            finalisedTps.put(keys.get(i + 1), tps);

        }
        return finalisedTps;

    }

    public Map<Timestamp, Integer> getMPS(Map<Timestamp, Integer> inMPSStorage) {

        Map<Timestamp, Integer> Mps = new LinkedHashMap<Timestamp, Integer>();
        Map<Timestamp, Integer> MpsStorage = inMPSStorage;
        List<Timestamp> keys = new ArrayList<>(MpsStorage.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            // Millisecond
            int mpsdifference = MpsStorage.get(keys.get(i + 1))
                    - MpsStorage.get(keys.get(i));

            Mps.put(keys.get(i + 1), mpsdifference);

        }
        return Mps;

    }

    public Map<Timestamp, Integer> getMPSGrowth(Map<Timestamp, Integer> inMPSStorage) {

        Map<Timestamp, Integer> Mps = new LinkedHashMap<Timestamp, Integer>();
        Map<Timestamp, Integer> MpsStorage = inMPSStorage;
        List<Timestamp> keys = new ArrayList<>(MpsStorage.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            // Millisecond
            Mps.put(keys.get(i), MpsStorage.get(keys.get(i)));

        }
        return Mps;

    }

    public Map<Timestamp, Integer> getProposeMPS() {

        Map<Timestamp, Integer> proposeMps = getMPS(this.simulator_result.getProposeConsensusCounterStorage());
        return proposeMps;

    }

    public Map<Timestamp, Integer> getCommitMPS() {

        Map<Timestamp, Integer> commitMps = getMPS(this.simulator_result.getCommitConsensusCounterStorage());
        return commitMps;

    }

    public Map<Timestamp, Integer> getFinaliseMPS() {

        Map<Timestamp, Integer> finaliseMps = getMPS(this.simulator_result.getFinaliseConsensusCounterStorage());
        return finaliseMps;

    }

    public Map<Timestamp, Integer> getQuorumMPS() {

        Map<Timestamp, Integer> quorumMps = getMPS(this.simulator_result.getQuorumConsensusCounterStorage());
        return quorumMps;

    }

    public Map<Timestamp, Integer> getHeartBeatMPS() {

        Map<Timestamp, Integer> heartBeatMps = getMPS(this.simulator_result.getHeartBeatConsensusCounterStorage());
        return heartBeatMps;

    }

    public Map<Timestamp, Integer> getRoundChangeMPS() {

        Map<Timestamp, Integer> roundChangeMps = getMPS(this.simulator_result.getRoundChangeConsensusCounterStorage());
        return roundChangeMps;

    }

    public List<JSONObject> getFinalisedTPSJSON() {

        return getJSONData(getFinalisedTPS());

    }

    public List<JSONObject> getPartialTPSJSON() {

        return getJSONData(getPartialTPS());

    }

    public List<JSONObject> getInputTPSJSON() {

        return getJSONData(getInputTPS());

    }

    public List<JSONObject> getProposeMPSJSON() {

        return getJSONMPSData(getProposeMPS());

    }

    public List<JSONObject> getCommitMPSJSON() {

        return getJSONMPSData(getCommitMPS());

    }

    public List<JSONObject> getFinaliseMPSJSON() {

        return getJSONMPSData(getFinaliseMPS());

    }

    public List<JSONObject> getQuorumMPSJSON() {

        return getJSONMPSData(getQuorumMPS());

    }

    public List<JSONObject> getHeatBeatMPSJSON() {

        return getJSONMPSData(getHeartBeatMPS());

    }

    public List<JSONObject> getRoundChangeMPSJSON() {

        return getJSONMPSData(getRoundChangeMPS());

    }

    public List<JSONObject> getJSONMPSData(Map<Timestamp, Integer> objData) {
        List<JSONObject> jsonResult = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        for (Map.Entry<Timestamp, Integer> entryObj : objData.entrySet()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("time", timeFormatter.format(entryObj.getKey().toLocalDateTime()));
            jsonObj.put("mps", entryObj.getValue());
            jsonResult.add(jsonObj);
        }
        return jsonResult;
    }

    public List<JSONObject> getJSONData(Map<Timestamp, Integer> objData) {
        List<JSONObject> jsonResult = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
        for (Map.Entry<Timestamp, Integer> entryObj : objData.entrySet()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("time", timeFormatter.format(entryObj.getKey().toLocalDateTime()));
            jsonObj.put("tps", entryObj.getValue());
            jsonResult.add(jsonObj);
        }
        return jsonResult;
    }

}
