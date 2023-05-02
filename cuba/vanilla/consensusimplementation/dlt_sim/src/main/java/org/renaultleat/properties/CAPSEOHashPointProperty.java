package org.renaultleat.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.renaultleat.node.Wallet;

public class CAPSEOHashPointProperty {

    // CodePoint to segregate the transaction for each quorum
    public static final int transactioncodePoint = 5;

    // CodePoint to get the partial block proposer for each quorum
    public static final int partialblockproposercodePoint = 2;

    // CodePoint to get the rival partial block proposer for each quorum
    public static final int rivalpartialblockproposercodePoint = 6;

    // CodePoint to get the partial block Fulfiller in cas of issue with Proposer at
    // the whole network level
    public static final int partialblockfulfillercodePoint = 8;

    // CodePoint to get the full block proposer at the whole network level
    public static final int fullblockproposercodePoint = 6;

    // CodePoint to get the full block Fulfiller in cas of issue with Proposer at
    // the whole network level
    public static final int fullblockfulfillercodePoint = 5;

    // CodePoint to get the quorum proposer for the entire network at the end of
    // subepoch
    public static final int quorumproposercodePoint = 4;

    // CodePoint to get the round change quorum proposer for the entire network if
    // round change is detected
    public static final int roundchangequorumproposercodePoint = 7;

}
