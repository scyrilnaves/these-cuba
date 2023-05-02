#!/bin/bash
#cd ./rancher-v2.4.10/

# Remember for BootNode:

#Volume: /etc/testnet/bootnode is not deleted automatically

#REf: https://stackoverflow.com/questions/61747724/how-to-remove-mounted-volumes-pv-pvc-wont-deleteeditpatch

#Deleting manually in the pod seems to work rm -rf /etc/testnet/bootnode

#echo "Get the Shell for the Test Machine"
#./rancher kubectl exec -it -n dltsim-net $(./rancher kubectl -n dltsim-net get pods | awk '/ubuntu-/{printf $1}') -- bash

NBNODES=$1
echo "No of Nodes"
echo $NBNODES
#######################################################################################
#$noofnode $nwtype $blsize $nwlaten $nodebehaviour $totaltxs $noofvalidator $isvalidator
#$roundchangetimeout $heartbeat $totalquorums $epochthreshold $subepochthreshold $privacyid $startprivacyboolean $startnormalboolean
#$fullblockfullfillment $partialblockfullfillment $blacklistthreshold
#Reinitialise Variables
NWTYPE=$2
BLSIZE=$3
NLATENCY=$4
NBEHAVIOUR=$5
TOTXS=$6
TOVALIS=$7
ISVALI=$8
RCHANGE=$9
HBEAT=${10}
TQUORUMS=${11}
EPOCHTHRESHOLD=${12}
SUBEPOCHTHRESHOLD=${13}
PRIVACYID=${14}
STARTPRIVACY=${15}
STARTNORMAL=${16}
FULLBLOCKFULFILLMENT=${17}
PARTIALBLOCKFULFILLMENT=${18}
BLACKLISTTHRESHOLD=${19}
NOOFBAD=${20}
BADBEHAVIOUR=${21}
#Divide tx for each node
NOTXS=$((TOTXS / NBNODES))

########################################################################################
validatorvalue=$((TOVALIS - NOOFBAD - 1))
validatoriter=$((TOVALIS - NOOFBAD))
value=$((NBNODES - 1))
iter=$value

echo "Clone the Git Repo in the Test Machine"
./rancher kubectl exec -it -n dltsim-net $(./rancher kubectl -n dltsim-net get pods | awk '/ubuntu-/{printf $1}') -- bash -c "git clone https://cyrilnavessamuel:nebzRuvGqLhHmgafxg6y@bitbucket.org/cyrilnavessamuel/capbftdltsimulatordeploy-democratic.git"
#######################################################################################

rm -rf nohup.out

echo "Launch the Test"
#Iteration value for validators
for ((i = 0; i <= $validatorvalue; ++i)); do
    nohup ./rancher kubectl exec -it -n dltsim-net $(./rancher kubectl -n dltsim-net get pods | awk '/ubuntu-/{printf $1}') -- bash -c "bash /capbftdltsimulatordeploy-democratic/deploy/podScriptTest.sh "$i" "$NBNODES" "$NWTYPE" "$BLSIZE" "$NLATENCY" "$NBEHAVIOUR" true "$NOTXS" "$TOVALIS" true "$RCHANGE" "$HBEAT" "$TQUORUMS" "$EPOCHTHRESHOLD" "$SUBEPOCHTHRESHOLD" "$PRIVACYID" "$STARTPRIVACY" "$STARTNORMAL" "$FULLBLOCKFULFILLMENT" "$PARTIALBLOCKFULFILLMENT" "$BLACKLISTTHRESHOLD"" &
done
#Iteration value for bad/faulty validators
for ((j = $validatoriter; j <= $iter; ++j)); do
    nohup ./rancher kubectl exec -it -n dltsim-net $(./rancher kubectl -n dltsim-net get pods | awk '/ubuntu-/{printf $1}') -- bash -c "bash /capbftdltsimulatordeploy-democratic/deploy/podScriptTest.sh "$j" "$NBNODES" "$NWTYPE" "$BLSIZE" "$NLATENCY" "$BADBEHAVIOUR" true "$NOTXS" "$TOVALIS" true "$RCHANGE" "$HBEAT" "$TQUORUMS" "$EPOCHTHRESHOLD" "$SUBEPOCHTHRESHOLD" "$PRIVACYID" "$STARTPRIVACY" "$STARTNORMAL" "$FULLBLOCKFULFILLMENT" "$PARTIALBLOCKFULFILLMENT" "$BLACKLISTTHRESHOLD"" &
done
