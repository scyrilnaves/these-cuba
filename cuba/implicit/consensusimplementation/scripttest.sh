#!/bin/sh

#Frist Argument: Port 8080 ... 8081
#Second Argument: Instance No: 0,...1 etc

# mvn clean package -DskipTests -Dhttps.protocols=TLSv1.2

#  mesh or ringLattice or wattsStrogatz

# IF DEV!!!
# MAKE SURE TO CHANGE P2P SERVER AND RESULT.JSP
#                 $1     $2           $3       $4         $5            $6          $7           $8            $9               $10            $11         $12         $13             $14             $15           $16             $17          $18               $19                    $20                     $21
# ./scripttest.sh port nodenetwork blocksize totalnodes nodeproperty nodelatency nodebehavior isvalidator totalvalidators roundchangetimeout heartbeat totalquorums epochthreshold subepochthreshold privacyid startprivacyboolean totaltxs startnormalboolean fullblockfullfillment partialblockfullfillment blacklistthreshold
#nodelatency--> in millisecs = 30 -40
#nodebehavior 0 -- HONEST ||| 1 --DISHONEST
# Affects Latency and Conensus Prepare phase
########################################################################################################################
#
#                  $1   $2  $3 $4$5$6 $7 $8  $9 $10   $11 $12 $13 $14 $15 $16  $17   $18   $19 $20 $21
# ./scripttest.sh 8080 mesh 100 4 0 0 0 true 2 600000 30000 2  500 100  1 false 5000 true 5000 5000 5

# ./scripttest.sh 9080 mesh 100 4 1 0 0 true 2 600000 30000 2  500 100  1 false 5000 true 5000 5000 5

# ./scripttest.sh 8088 mesh 100 4 2 0 0 true 2 600000 30000 2  500 100  1 false 5000 true 5000 5000 5

# ./scripttest.sh 8090 mesh 100 4 3 0 0 true 2 600000 30000 2  500 100  1 false 5000 true 5000 5000 5
########################################################################################################################
#                  $1   $2           $3 $4$5$6 $7 $8  $9 $10  $11  $12
# ./scripttest.sh 8080 wattsStrogatz 100 4 0 0 0 true 4 30000 true 5000

# ./scripttest.sh 9080 wattsStrogatz 100 4 1 0 0 true 4 30000 true 5000

# ./scripttest.sh 8088 wattsStrogatz 100 4 2 0 0 true 4 30000 true 5000

# ./scripttest.sh 8089 wattsStrogatz 100 4 3 0 0 true 4 30000 true 5000
########################################################################################################################
# ./scripttest.sh 8080 ringLattice 100 4 0 0 0 true 4 30000 true 5000

# ./scripttest.sh 9080 ringLattice 100 4 1 0 0 true 4 30000 true 5000

# ./scripttest.sh 8088 ringLattice 100 4 2 0 0 true 4 30000 true 5000

# ./scripttest.sh 8089 ringLattice 100 4 3 0 0 false 4 30000 true 5000
##########################################################################################################################
#http://localhost:8080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getVersion

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getVersion

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeNetwork/$2

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setBlockSize/$3

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalNodes/$4

#New

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalValidators/$9

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalEffectiveMembers/$9

#new : Set Total Nodes, Total Validators before set node property

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setnodeProperty/$5

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeLatency/$6

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setNodeBehavior/$7

#New

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setAsValidator/$8

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setRoundChange/$10

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setHeartBeat/$11

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setTotalQuorums/$12

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setEpochThreshold/$13

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setSubEpochThreshold/$14

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setFullBlockFullfillment/$19

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setPartialBlockFullfillment/$20

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setBlackListThreshold/$21

#New

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getnodeProperty

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getNodeBehavior

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTotalValidators

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getFullBlockFullfillment

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getAsValidator

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getBlockSize

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getNodeLatency

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initiateConnection

sleep 20

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/listentoPeers

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/initialise

# Allow to connect and establish connection with peers sufficiently
sleep 20

#Start Result Collator

curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startResultCollator

if $16; then

    #curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/sendMessageToPeer/cyrilmessage

    #curl http://localhost:8080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getBlockNo

    #curl http://localhost:9080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getBlockNo

    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setPrivacyId/$15

    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setInitialQuorum

    echo "started firing"

    sleep 30

    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingleprivacy/$17

    #curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation/100/1

    #curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulation/100000/4

    #curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingle/90

    #curl http://localhost:8080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingle/100000
    sleep 500
    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
fi
if $18; then

    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/setInitialQuorum

    sleep 30

    echo "started firing"

    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/startsimulationsingle/$17

    sleep 500
    curl http://localhost:$1/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/stopResultCollator
fi
#To check Block No
#curl http://localhost:8080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getBlockNo

#curl http://localhost:8080/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTransactionsValidatedNo
