package org.renaultleat.consensus;

import java.util.HashMap;
import java.util.Map;

public class Synchronizer {

    public volatile Map<String, Boolean> commitconsensusReached = new HashMap<String, Boolean>();

    public volatile Map<Integer, Boolean> roundchangeconsensusReached = new HashMap<Integer, Boolean>();

    public volatile boolean thresholdReached = false;

    public volatile boolean consensusincourse = false;

}
