#!/bin/sh
cd "$(dirname "$0")"
NBINDEX=$1

#Frist Argument: Port 8080 ... 8081
#Second Argument: Instance No: 0,...1 etc

#  mesh or   or wattsStrogatz
#nodelatency--> in millisecs = 30 -40
#nodebehavior 0 -- HONEST ||| 1 --DISHONEST
# Affects Latency and Conensus Prepare phase

#################################################################################################################################################################################################################################################################################################################################################
#                       $1         $2        $3           $4         $5           $6          $7         $8  $9              $10           $11                  $12         $13          $14             $15           $16          $17             $18                    $19                  $20                  $21
# ./podScriptTest.sh nodeindex totalnodes  nodenetwork blocksize  nodelatency nodebehavior fireboolean notxs totalvalidators isvalidator  roundchangetimeout heartbeat totalquorums epochthreshold subepochthreshold privacyid startprivacyboolean startnormalboolean fullblockfullfillment partialblockfullfillment blacklistthreshold
#################################################################################################################################################################################################################################################################################################################################################
#                    $1 $2  $3  $4  $5  $6  $7    $8   $9 $10  $11    $12  $13 $14 $15 $16 $17 $18   $19    $20  $21
# ./podScriptTest.sh 0  4 mesh 100  0  0  true   5000  4  true 60000 30000  4  20  10  1 false true 20000 20000  5
##################################################################################################################################################################################################################################################################################################################################################
# Get the IP and IP of the Peers from IP List
filename='iplist.txt'
peeriparray=''
myip=
myport=8080
n=0
for ip in $(cat iplist.txt); do
  if [ $n == $NBINDEX ]; then
    myip=$ip
    peeriparray+=${ip}
    peeriparray+=','
    #echo $ip;
  else
    peeriparray+=${ip}
    peeriparray+=','
  fi
  echo "N Value:"$n
  n=$((n + 1))
done
# Formed Peer IP to Connect for Peer $NBINDEX
echo "Formed Peer IP to Connect for Peer:" $NBINDEX
echo "My IP:" $myip
echo "Peers are:"$peeriparray
#######################################################################################################################

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeNetwork/$3

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setPeers/$peeriparray

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setBlockSize/$4

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalNodes/$2

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalValidators/$9

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalEffectiveMembers/$9

#new : Set Total Nodes, Total Validators before set node property

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setnodeProperty/$NBINDEX

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeLatency/$5

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeBehavior/$6

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setAsValidator/${10}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setRoundChange/${11}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setHeartBeat/${12}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalQuorums/${13}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setEpochThreshold/${14}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setSubEpochThreshold/${15}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setFullBlockFullfillment/${19}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setPartialBlockFullfillment/${20}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setBlackListThreshold/${21}

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initiateConnection

sleep 60

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/listentoPeers

# Allow to connect and establish connection with peers sufficiently
sleep 60

#curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initialisePool/$4

#sleep 600

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initialiseQuorum

sleep 90

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setInitialQuorum

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initialise

#Start Result Collator

curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startResultCollator

#PRIVACY PART
if ${17}; then

  curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setPrivacyId/${16}

  echo "privacy started firing"

  sleep 30

  curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingleprivacy/$8

  sleep 500
  curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
fi
#NORMAL PART
if ${18}; then

  echo "started firing"

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingle/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation1/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation2/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation3/$8

  curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation4/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation5/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation6/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation7/$8

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation8/$8

  #sleep 500

  #curl -s http://$myip:$myport/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
fi
