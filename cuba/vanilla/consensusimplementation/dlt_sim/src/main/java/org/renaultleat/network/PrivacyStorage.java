package org.renaultleat.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.renaultleat.consensus.QuorumMessage;
import org.renaultleat.node.Wallet;

// Class for Storing all Privacy 
// Set only once
public class PrivacyStorage {

    // Enable Privacy Feature or Not
    public static boolean isPrivacy = false;

    // Privacy Group Id
    public static int privacyGroupId;

    public static List<Integer> privacyGroups = new ArrayList<>();

    // NodeIndex,Privacy Group Id:
    Map<Integer, Integer> nodetoprivacyGroupIndexMapping = new HashMap<Integer, Integer>();

    // Privacy Group Id, List of NodeIndexes
    Map<Integer, List<Integer>> privacyGrouptoNodeIndexMapping = new HashMap<Integer, List<Integer>>();

    public Map<Integer, Integer> getNodetoprivacyGroupIndexMapping() {
        return this.nodetoprivacyGroupIndexMapping;
    }

    public void setNodetoprivacyGroupIndexMapping(Map<Integer, Integer> nodetoprivacyGroupIndexMapping) {
        this.nodetoprivacyGroupIndexMapping = nodetoprivacyGroupIndexMapping;
    }

    public Map<Integer, List<Integer>> getPrivacyGrouptoNodeIndexMapping() {
        return this.privacyGrouptoNodeIndexMapping;
    }

    public void setPrivacyGrouptoNodeIndexMapping(Map<Integer, List<Integer>> privacyGrouptoNodeIndexMapping) {
        this.privacyGrouptoNodeIndexMapping = privacyGrouptoNodeIndexMapping;
    }

    public static void setIsPrivacy(boolean input) {
        // Set only once
        isPrivacy = input;

    }

    public static boolean isPrivacy() {
        return isPrivacy;

    }

    public static void setPrivacyGroupId(String input) {
        privacyGroupId = Integer.valueOf(input);
    }

    public static int getPrivacyGroupId() {
        return privacyGroupId;

    }

    public static void setPrivacyGroups(List<Integer> inPrivacyGroups) {
        privacyGroups = inPrivacyGroups;
    }

    public static List<Integer> getPrivacyGroups() {
        return privacyGroups;

    }

}