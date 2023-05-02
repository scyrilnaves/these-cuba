#!/bin/bash
testtype=3
############################################################################################################################################################
echo "stopping collators"
#used to stop result collator of all
#TODO: UNCOMMENT
#----------------
#curl -s http://dltsim-dash.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
#curl -s http://dltsim-dash-mid.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
#curl -s http://dltsim-dash-last.unice.cust.tasfrance.com/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator

############################################################################################################################################################
# collect the results from 3 resources in local
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
  echo "finished"
done
