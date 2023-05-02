package org.renaultleat.properties;

import java.util.concurrent.TimeUnit;

import org.renaultleat.node.Wallet;

public class NodeProperty {

    public static final int totalkeys = 100;

    // Differentiate between participants and validators
    // Need to set for diffrentiations
    public static int validators = 0;

    ////////////////////////// Deployment///////////////////////////////////////////////////////////////////////////////////////
    public static final String user = "node";
    // Socket Port
    public static final String port = "7080";
    // IP's Fed from Controller
    public static String ips = "";
    // Retrieved from P2P Server
    public static String peers = "";
    public static int peercount = 0;
    ////////////////////////// Deployment///////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////// Dev//////////////////////////////////////////////////////////////////////////////////////////////
    public static final String users = "node1,node2,node3,node4,node5,node6,node7,node8";
    public static final String ip = "localhost";
    public static final String ports = "7080,7081,7082,7083,7084,7085,7086,7087";
    public static final String httpports = "8080,9080,8088,8089";
    ////////////////////////// Dev//////////////////////////////////////////////////////////////////////////////////////////////

    public static final String deployed = "01 FEB2022";
    // Set value for the testing
    public static int totalnodes = 0;
    public static int blocksize = 0;

    public static int threshold = 10;
    // mesh or ringlattice or wattsstrogatz
    public static String nodeNetwork = "";

    // Node Behavior
    // 0 --> good (default)
    // 1 --> malicious
    public static int nodeBehavior = 0;
    // Node Latency in millisecs
    public static long latency = 0;

    // Set if Validator or Not
    public static boolean isValidator = false;

    // Frequenct to be set for RoundChange Timeout in millisecs
    // 10000 = 10secs
    public static long roundChange = 0;

    // Frequenct to be set for Full Block Timeout in millisecs
    // 10000 = 10secs
    public static long fullBlockFullfillment = 0;

    // Frequenct to be set for Partial Block Timeout in millisecs
    // 10000 = 10secs
    public static long partialBlockFullfillment = 0;

    // Frequency to be set for Heart Beat
    public static long heartBeatBroadcast = 0;

    // Static Portion

    public static void setPeers(String input) {
        // Set only once
        if (peers == "") {
            peers = input;
        }

    }

    public static String getPeers() {
        return peers;

    }

    public static void setIsValidator(boolean input) {
        // Set only once
        isValidator = input;

    }

    public static boolean isValidator() {
        return isValidator;

    }

    public static void setValidators(int input) {
        // Set only once
        if (validators == 0) {
            validators = input;
        }

    }

    public static int getValidators() {
        return validators;

    }

    public static void setPeerCount(int input) {
        // Set only once
        if (peercount == 0) {
            peercount = input;
        }

    }

    public static int getPeerCount() {
        return peercount;

    }

    public static void setIPS(String input) {
        // Set only once
        if (ips == "") {
            ips = input;
        }

    }

    public static String getIPS() {
        return ips;

    }

    public static int getCurrentPort() {
        int index = Wallet.getNodeproperty();
        String[] ports = httpports.split(",");
        return Integer.valueOf(ports[index]);

    }

    public static void setRoundChange(long input) {
        // Set only once
        if (roundChange == 0) {
            roundChange = input;
        }

    }

    public static long getRoundChange() {
        return roundChange;

    }

    public static void setFullBlockFullfillment(long input) {
        // Set only once
        if (fullBlockFullfillment == 0) {
            fullBlockFullfillment = input;
        }

    }

    public static long getFullBlockFullfillment() {
        return fullBlockFullfillment;

    }

    public static void setPartialBlockFullfillment(long input) {
        // Set only once
        if (partialBlockFullfillment == 0) {
            partialBlockFullfillment = input;
        }

    }

    public static long getPartialBlockFullfillment() {
        return partialBlockFullfillment;

    }

    public static void setHeartBeatBroadcast(long input) {
        // Set only once
        heartBeatBroadcast = input;
    }

    public static long getHeartBeatBroadcast() {
        return heartBeatBroadcast;

    }

    public static void setnodeLatency(long input) {
        // Set only once
        if (latency == 0) {
            latency = input;
        }

    }

    public static long getnodeLatency() {
        return latency;

    }

    public static String getIP() {
        try {
            return NodeProperty.getIPS().split(",")[Integer.valueOf(Wallet.getNodeproperty())];
        } catch (Exception e) {
            return "Test Mode";
        }

    }

    public static void setnodeBehavior(int input) {
        // Set only once
        if (nodeBehavior == 0) {
            nodeBehavior = input;
        }

    }

    public static int getnodeBehavior() {
        return nodeBehavior;

    }

    public static void setnodeNetwork(String input) {
        // Set only once
        if (nodeNetwork == null || nodeNetwork == "") {
            nodeNetwork = input;
        }

    }

    public static String getnodeNetwork() {
        return nodeNetwork;

    }

    public static void settotalNodes(int input) {
        // Set only once
        if (totalnodes == 0) {
            totalnodes = input;
        }

    }

    public static int gettotalNodes() {
        return totalnodes;

    }

    public static void setBlockSize(int input) {
        // Set only once
        if (blocksize == 0) {
            blocksize = input;
        }

    }

    public static int getBlockSize() {
        return blocksize;

    }

}
