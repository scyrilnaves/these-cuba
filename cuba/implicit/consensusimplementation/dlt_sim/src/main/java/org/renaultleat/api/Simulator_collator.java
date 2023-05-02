package org.renaultleat.api;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;

import org.renaultleat.chain.Blockchain;
import org.renaultleat.chain.TransactionPool;
import org.renaultleat.consensus.CAPSEOBFTDemMessagePool;
import org.renaultleat.properties.NodeProperty;

public class Simulator_collator extends Thread {

        public Simulator_result simulator_result;

        public TransactionPool transactionPool;

        public CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool;

        public Blockchain blockchain;

        // Get input tx/s from confirmed transaction ids

        // Get processed tx/s from
        boolean canRun = true;

        @Override
        public void run() {
                while (canRun) {
                        // Input TPS Fill
                        this.simulator_result.getInputTPSStorage().put(new Timestamp(System.currentTimeMillis()),
                                        this.transactionPool.getTransactionRoundStatus().size()
                                                        * NodeProperty.blocksize);
                        // Patial TPS Fill
                        this.simulator_result.getPartialTPSStorage().put(new Timestamp(System.currentTimeMillis()),
                                        this.blockchain.getEphemeralChain().size());
                        // Finalised Block Fill to later extrapolate with block size to get the
                        // finalized tps
                        this.simulator_result.getFinalisedTPSStorage().put(new Timestamp(System.currentTimeMillis()),
                                        this.blockchain.getChain().size());

                        this.simulator_result.getProposeConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getProposeMessagePool().size());

                        this.simulator_result.getCommitConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getCommmitMessagePool().size());

                        this.simulator_result.getFinaliseConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getFinaliseMessagePool().size());

                        this.simulator_result.getQuorumConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getQuorumMessagePool().size());

                        this.simulator_result.getHeartBeatConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getHeartBeatMessagePool().size());

                        this.simulator_result.getRoundChangeConsensusCounterStorage().put(
                                        new Timestamp(System.currentTimeMillis()),
                                        this.capSEOBFTDemMessagePool.getRoundChangeMessagePool().size());

                        try {
                                Thread.sleep(20000);
                        } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
        }

        public void stopThread() {
                this.canRun = false;
        }

        // Constructor with result datastructure
        public Simulator_collator(Simulator_result simulator_result, TransactionPool transactionPool,
                        Blockchain blockchain, CAPSEOBFTDemMessagePool capSEOBFTDemMessagePool) {
                this.simulator_result = simulator_result;
                this.transactionPool = transactionPool;
                this.blockchain = blockchain;
                this.capSEOBFTDemMessagePool = capSEOBFTDemMessagePool;
        }

}
