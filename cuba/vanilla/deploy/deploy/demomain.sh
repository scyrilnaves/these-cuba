#!/bin/bash
cd "$(dirname "$0")"

echo "This is Demo Run"
echo "Hope you have set the configuration"
echo "Results are colated in Results Folder"
echo "check below links for state"
echo "http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/result.jsp"
# N-3 Node
echo "http://dltsim-dash-mid.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/result.jsp"
# N-1 Node
echo "http://dltsim-dash-last.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/result.jsp"

echo "http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getVersion"
sleep 10
echo "OK Let's Start"
###########################################
##CONFIGURATION
###########################################
consensus=democratic
#Minimum to be atleast 2000
totaltxs=1500000
noofnodes=(20)
noofvalidators=(20)
#mesh --normal
#ringLattice
#wattsStrogatz
networktype=(wattsStrogatz)
latency=(0)
isvalidator=true
#millisecs
roundchangetimeout=900000
blocksize=(2000)
nodebehaviour=0
#new
heartbeat=5000
totalquorums=5
# Fast Epoch SubEpoch = 25 blocs | Epoch = 50 blocs
# Mid Epoch SubEpoch = 50 blocs | Epoch= 100 blocs
epochthreshold=100000
# MORE IMPORTANT!!
subepochthreshold=50000
privacyid=1
startprivacyboolean=false
startnormalboolean=true
fullblockfullfillment=5000
partialblockfullfillment=5000
blacklistthreshold=5
###########################################
for noofnode in "${noofnodes[@]}"; do
  for noofvalidator in "${noofvalidators[@]}"; do
    for nwtype in "${networktype[@]}"; do
      for nwlaten in "${latency[@]}"; do
        for blsize in "${blocksize[@]}"; do

          echo "Main Cloud Deployment Started"
          ./mainredeploy.sh $noofnode

          ##START OF TEST
          starttime=$(date +%s)

          echo "Main Test Execution Started"
          ./mainexecutetest.sh $noofnode $nwtype $blsize $nwlaten $nodebehaviour $totaltxs $noofvalidator $isvalidator $roundchangetimeout $heartbeat $totalquorums $epochthreshold $subepochthreshold $privacyid $startprivacyboolean $startnormalboolean $fullblockfullfillment $partialblockfullfillment $blacklistthreshold

          # A: NoNode //  B: NwType // C: BlockSize // D: NWLatency // E: NodeBehaviour // D: TotalTxs // E: Consensus // F: TotalValidators // G: Is Validator // H: RoundChange // I: HeartBeat // J: TotalQuorums // K: EpochThreshold // L: SubEpochThreshold // M: PrivacyId // N: StartPrivacy // O: Start Normal // P: Full Block Fulfill // Q: BlackList

          testtype="1P:"$noofnode";2P:"$nwtype";3P:"$blsize";4P:"$nwlaten";5P:"$nodebehaviour";6P:"$totaltxs";7P:"$consensus";8P:"$noofvalidator";9P:"$isvalidator";10P:"$roundchangetimeout";11P:"$heartbeat";12P:"$totalquorums";13P:"$epochthreshold";14P:"$subepochthreshold";15P:"$privacyid";16P:"$startprivacyboolean";17P:"$startnormalboolean";18P:"$fullblockfullfillment";19P:"$partialblockfullfillment";20P:"$blacklistthreshold
          echo "Wait till we initialise"
          sleep 180
          # write test type to a d
          echo $testtype >>results/testtype.txt
          #Do CURL to get values and stop test
          txvalidated=$(curl -s http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTransactionsValidatedNo | tr -d '"')
          txthreshold=$((totaltxs - 1000))
          sleep 60
          while (("$txvalidated" < "$txthreshold")); do
            #used tr to remove double quotes
            actualtime=$(time date)
            echo "Current time : $actualtime"
            txprocessed=$(curl --connect-timeout 60 -s http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getProcessedTPS | tr -d '"')
            txvalidated=$(curl --connect-timeout 60 -s http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTransactionsValidatedNo | tr -d '"')
            #echo "Txprocessed : $txprocessed"
            echo "Txvalidated : $txvalidated"
            echo "waiting"
            sleep 60
          done
          echo "exited"
          sleep 30
          endtime=$(date +%s)
          delta=$((endtime - starttime))
          ############################################################################################################################################################
          echo "stopping collators"
          #used to stop result collator of all
          #TODO: UNCOMMENT
          #----------------
          curl -s http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
          curl -s http://dltsim-dash-mid.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
          curl -s http://dltsim-dash-last.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator

          ############################################################################################################################################################
          # collect the results from 3 resources in local
          mkdir results
          mkdir results/$testtype
          #iterate from 3 different points for comparison
          endpoints=(first mid last)
          for endpoint in "${endpoints[@]}"; do
            #make a subdirectory for each endpoint
            mkdir results/$testtype/$endpoint
            urlcontext=''
            if [ "$endpoint" == "first" ]; then
              urlcontext=dltsim-dash.unice.cust.tasfrance.com
            elif [ "$endpoint" == "mid" ]; then
              urlcontext=dltsim-dash-mid.unice.cust.tasfrance.com
            else
              urlcontext=dltsim-dash-last.unice.cust.tasfrance.com
            fi
            echo $urlcontext
            #Transactions validated
            curl -s -o results/$testtype/$endpoint/txvalidated.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTransactionsValidatedNo | tr -d '"'
            #TODO: UNCOMMENT
            #------------------
            #Result Part

            #Processed TPS
            curl -s -o results/$testtype/$endpoint/processedtps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getProcessedTPS
            #Partial TPS
            curl -s -o results/$testtype/$endpoint/partialtps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getPartialTPS
            #Finalised TPS
            curl -s -o results/$testtype/$endpoint/finalisedtps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getFinalisedTPS
            #Prepare Message Rate
            curl -s -o results/$testtype/$endpoint/proposemps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getProposeRate
            #Commit Message Rate
            curl -s -o results/$testtype/$endpoint/commitmps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getCommitRate
            #Finalise Message Rate
            curl -s -o results/$testtype/$endpoint/finalisemps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getFinaliseRate
            #Heart Beat Message Rate
            curl -s -o results/$testtype/$endpoint/heartbeatmps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getHeartBeatRate
            #Quorum Message Rate
            curl -s -o results/$testtype/$endpoint/quorummps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getQuorumRate
            #Round Change Message Rate
            curl -s -o results/$testtype/$endpoint/roundchangemps.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getRoundChangeRate

            #------------------
            #Utilitarian Part:

            #Commit Score
            curl -s -o results/$testtype/$endpoint/commitscore.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianCommitScoreGrid
            # Heart Beat Score
            curl -s -o results/$testtype/$endpoint/heartbeatscore.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianHeartBeatScoreGrid
            # Block Time Coefficient
            curl -s -o results/$testtype/$endpoint/interblocktime.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianInterBlockTimeCoefficientGrid
            #Block Proposal
            curl -s -o results/$testtype/$endpoint/blockproposal.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianBlockProposalScoreGrid

            #------------------
            #Effective Utilitarian Part:
            #Effective Score
            curl -s -o results/$testtype/$endpoint/effectivescore.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianEffectiveScoreGrid
            #Classification data
            curl -s -o results/$testtype/$endpoint/classification.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianClassificationGrid
            #Suspension data
            curl -s -o results/$testtype/$endpoint/suspension.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getQuorumSuspensionGrid

            #------------------
            #Non Utilitarian Part:
            #Missed Commit
            curl -s -o results/$testtype/$endpoint/missedcommit.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedCommitScoreGrid
            #Malicious Score
            curl -s -o results/$testtype/$endpoint/maliciousscore.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMaliciousScoreGrid
            #Missed Heart Beat
            curl -s -o results/$testtype/$endpoint/missedheartbeat.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedHeartBeatScoreGrid
            #Missed Block Proposal
            curl -s -o results/$testtype/$endpoint/missedblockproposal.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedBlockProposalScoreGrid

            #------------------
            #NON CUMUL
            #Utilitarian Part:

            #Commit Score
            curl -s -o results/$testtype/$endpoint/commitscorenoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianCommitScoreGridNonCumul
            # Heart Beat Score
            curl -s -o results/$testtype/$endpoint/heartbeatscorenoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianHeartBeatScoreGridNonCumul
            # Block Time Coefficient
            curl -s -o results/$testtype/$endpoint/interblocktime.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianInterBlockTimeCoefficientGridNonCumul
            #Block Proposal
            curl -s -o results/$testtype/$endpoint/blockproposalnoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianBlockProposalScoreGridNonCumul

            #------------------
            #Effective Utilitarian Part:
            #Effective Score
            curl -s -o results/$testtype/$endpoint/effectivescorenoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianEffectiveScoreGridNonCumul
            #Classification data
            curl -s -o results/$testtype/$endpoint/classification.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianClassificationGrid
            #Suspension data
            curl -s -o results/$testtype/$endpoint/suspension.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getQuorumSuspensionGrid

            #------------------
            #Non Utilitarian Part:
            #Missed Commit
            curl -s -o results/$testtype/$endpoint/missedcommitnoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedCommitScoreGridNonCumul
            #Malicious Score
            curl -s -o results/$testtype/$endpoint/maliciousscorenoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMaliciousScoreGridNonCumul
            #Missed Heart Beat
            curl -s -o results/$testtype/$endpoint/missedheartbeatnoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedHeartBeatScoreGridNonCumul
            #Missed Block Proposal
            curl -s -o results/$testtype/$endpoint/missedblockproposalnoncumul.json http://$urlcontext/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedBlockProposalScoreGridNonCumul

            echo "TimeDifference:"$delta >results/$testtype/$endpoint/duration.txt
            echo "finished"
          done
        done
      done
    done
  done
done
