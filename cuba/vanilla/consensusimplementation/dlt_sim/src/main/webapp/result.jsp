<!-- ADDED JAVA CLASS IMPORTS-->
<%@page import="org.renaultleat.properties.NodeProperty" %>
	<%@page import="org.renaultleat.network.QuorumStorage" %>
		<%@page import="org.renaultleat.node.Wallet" %>
			<!DOCTYPE html>
			<html lang="en">

			<head>
				<meta charset="utf-8">
				<title>DLT Simulator</title>
				<script src='chart/Chart.js' charset="utf-8"></script>
				<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
				<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
				<link rel="stylesheet"
					href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
				<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
				<style>
					label {
						font-family: sans-serif;
					}

					svg {
						background: #eeeeee;
					}
				</style>
			</head>

			<body>
				<div style="margin:5px;">
					<div class="navbar navbar-inverse navbar-static-top">
						<!-- <div class="container">
						<a class="navbar-brand" href="/">DLT Simulator</a>
					</div> -->
						<div id="logo">
							<h1 style="color:white;">DLT Simulator</h1>
							<img src="images/leat.jpeg" alt="LEAT" width="300"
								height="150">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;
							<img src="images/renault.png" alt="Renault" width="300" height="150">
						</div>
						<div class="jumbotron" style="padding:40px;">
							<h3 style="color:darkblue;">DLT Property</h3>
							<b>TotalNodes:</b> <input type="text" id="TotalNodes" value="" readonly>&nbsp&nbsp;
							<b>TotalPeers:</b> <input type="text" id="TotalPeers" value="" readonly>&nbsp&nbsp;
							<b>NodeIndex:</b><input type="text" id="NodeIndex" value="" readonly>
							<b>BlockSize:</b><input type="text" id="BlockSize" value="" readonly><br>
							<b>NetworkLatency:</b><input type="text" id="NetworkLatency" value="" readonly><br>
							<b>NodeBehaviour:</b><input type="text" id="NodeBehaviour" value="" readonly><br>
							<b>NetworkType:</b><input type="text" id="NetworkType" value="" readonly><br>
							<b>QuorumId:</b><textarea id="qid" name="qid" rows="4" cols="50"></textarea><br>&nbsp&nbsp;
							<b>QuorumIndex:</b><textarea id="qindex" name="qindex" rows="4"
								cols="50"></textarea><br>&nbsp&nbsp;
							<b>Node IP:</b> <input type="text" id="nodeip" value="" readonly>&nbsp&nbsp;
							<b>Peer IP:</b><textarea id="peerip" name="peerip" rows="4" cols="50"></textarea><br>
							<h3 style="color:darkblue;">DLT Network State</h3>
							<b>Blocks:</b> <input type="text" id="TotalBlocks" value="" readonly>&nbsp&nbsp;
							<b>Transactions Validated:</b> <input type="text" id="txvalidated" value=""
								readonly>&nbsp&nbsp;
							<b>Total Validators:</b> <input type="text" id="totalValid" value="" readonly>&nbsp&nbsp;
							<b>Are you Validator:</b> <input type="text" id="isValid" value="" readonly>&nbsp&nbsp;
							<b>Round Change Timeout:</b> <input type="text" id="roundChange" value=""
								readonly>&nbsp&nbsp;
						</div>
					</div>
				</div>
				<br>
				<div id="txgraph">
					<h3>Transaction Rate</h1>
						<canvas id="dltTX" style="width:100%;max-width:700px"></canvas>
				</div>
				<div id="bftgraph">
					<h3>Consensus Message Rate</h1>
						<canvas id="dltBFT" style="width:100%;max-width:700px"></canvas>
				</div>
				<script type="text/javascript">
					//Added all the necessary Node Property
					var totalNodes = '<%=NodeProperty.totalnodes %>';
					var totalPeers = '<%=NodeProperty.getPeerCount() %>';
					var nodeproperty = '<%=Wallet.nodeproperty %>';
					var blocksize = '<%=NodeProperty.blocksize %>';
					var latency = '<%=NodeProperty.latency %>';
					var behaviour = '<%=NodeProperty.nodeBehavior %>';
					var networkType = '<%=NodeProperty.nodeNetwork %>';
					var port = 8080;
					var qid = '<%=QuorumStorage.getQuorumId() %>';
					var qindex = '<%=QuorumStorage.getQuorumIndex() %>';
					var ip = '<%=NodeProperty.getIP() %>';
					var totalValid = '<%=NodeProperty.getValidators() %>';
					var isValid = '<%=NodeProperty.isValidator() %>';
					var rchangeTime = '<%=NodeProperty.getRoundChange() %>';
					var ipurl = "dltsim-dash.unice.cust.tasfrance.com";
					var midnodeindex = totalNodes - 3;
					var lastnodeindex = totalNodes - 1;
					if (nodeproperty == "1") {
						ipurl = "dltsim-dash.unice.cust.tasfrance.com"
					} else if (midnodeindex == nodeproperty) {
						ipurl = "dltsim-dash-mid.unice.cust.tasfrance.com"
					} else if (lastnodeindex == nodeproperty) {
						ipurl = "dltsim-dash-last.unice.cust.tasfrance.com"
					}
					var peers = '<%=NodeProperty.getPeers() %>';

					document.getElementById("qid").value = qid;
					document.getElementById("qindex").value = qindex;
					document.getElementById("TotalNodes").value = totalNodes;
					document.getElementById("TotalPeers").value = totalPeers;
					document.getElementById("NodeIndex").value = nodeproperty;
					document.getElementById("BlockSize").value = blocksize;
					document.getElementById("NetworkLatency").value = latency;
					if (behaviour == "0") {
						document.getElementById("NodeBehaviour").value = "GOOD NODE :)";
					} else {
						document.getElementById("NodeBehaviour").value = "BAD NODE !! :(";
					}
					document.getElementById("NetworkType").value = networkType;
					document.getElementById("nodeip").value = ip;
					document.getElementById("peerip").value = peers;
					document.getElementById("totalValid").value = totalValid;
					document.getElementById("isValid").value = isValid;
					document.getElementById("roundChange").value = rchangeTime;

					// Get the network State as API CALLS
					var request = new XMLHttpRequest()

					// BlockData
					request.open("GET", "http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getBlockNo");
					request.send();
					request.onload = () => {
						const blockdata = JSON.parse(request.response);
						document.getElementById("TotalBlocks").value = blockdata;
					}
					// Transaction Validated
					// Get the network State as API CALLS
					var txvalidatedrequest = new XMLHttpRequest()
					txvalidatedrequest.open("GET", "http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getTransactionsValidatedNo");
					txvalidatedrequest.send();
					txvalidatedrequest.onload = () => {
						const txvalidateddata = JSON.parse(txvalidatedrequest.response);
						document.getElementById("txvalidated").value = txvalidateddata;
					}
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					// End API Calls
					//PROCESSED TX PART
					var processedtpstime = [];
					var processedtpsdata = [];

					//FINALISED TX PART
					var finalisedtpstime = [];
					var finalisedtpsdata = [];

					//Partialised Tx Part
					var partialtpstime = [];
					var partialtpsdata = [];

					async function gettxData() {
						let txprocessedresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getProcessedTPS", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let txprocesseddata = await txprocessedresponse.json();
						for (let i = 0; i < txprocesseddata.length; i++) {
							processedtpstime[i] = txprocesseddata[i].map.time;
							processedtpsdata[i] = Number(txprocesseddata[i].map.tps)
						}

						let partialtxprocessedresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getPartialTPS", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let partialtxprocesseddata = await partialtxprocessedresponse.json();
						for (let i = 0; i < partialtxprocesseddata.length; i++) {
							partialtpstime[i] = partialtxprocesseddata[i].map.time;
							partialtpsdata[i] = Number(partialtxprocesseddata[i].map.tps)
						}

						let txfinalisedresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getFinalisedTPS", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let txfinaliseddata = await txfinalisedresponse.json();
						for (let i = 0; i < txfinaliseddata.length; i++) {
							finalisedtpstime[i] = txfinaliseddata[i].map.time;
							finalisedtpsdata[i] = Number(txfinaliseddata[i].map.tps)
						}
						return txfinaliseddata;
					}
					gettxData().then(data =>
						new Chart("dltTX", {
							//type: "bar",
							type: "line",
							data: {
								labels: processedtpstime,
								datasets: [{
									label: 'TPS Processed Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(255, 99, 132)",
									borderColor: "rgb(255, 99, 132)",
									data: processedtpsdata
								}, {
									label: 'Partial TPS Processed Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(54, 57, 122)",
									borderColor: "rgb(54, 57, 122)",
									data: partialtpsdata
								}, {
									label: 'TPS Finalised Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(255, 159, 64)",
									borderColor: "rgb(255, 159, 64)",
									data: finalisedtpsdata
								}]
							},
							options: {
								responsive: true,
								interaction: {
									mode: 'index',
									intersect: false,
								},
								stacked: false,
								plugins: {
									title: {
										display: true,
										text: 'Processed, Partial and Finalised TPS'
									}
								},
								legend: { display: true }/* ,
							scales: {
								yAxes: [{ ticks: { min: 0, max: 50000 } }],
							} */
							}
						})

					);
					//END PROCESSED TX PART
					//////////////////////////////////////////////////////////////////////////////////////////////
					//CONSENSUS MESSAGE PART
					var consensusmsgtime = [];
					var proposempsdata = [];
					var commitmpsdata = [];
					var finalisempsdata = [];
					var quorummpsdata = [];
					var heartbeatmmpsdata = [];
					var roundchangempsdata = [];

					async function getconsensusData() {
						let mxproposeresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getProposeRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxproposedata = await mxproposeresponse.json();
						for (let i = 0; i < mxproposedata.length; i++) {
							consensusmsgtime[i] = mxproposedata[i].map.time;
							proposempsdata[i] = Number(mxproposedata[i].map.mps)
						}
						let mxcommitresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getCommitRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxcommitdata = await mxcommitresponse.json();
						for (let i = 0; i < mxcommitdata.length; i++) {
							commitmpsdata[i] = Number(mxcommitdata[i].map.mps)
						}

						let mxfinaliseresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getFinaliseRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxfinalisedata = await mxfinaliseresponse.json();
						for (let i = 0; i < mxfinalisedata.length; i++) {
							finalisempsdata[i] = Number(mxfinalisedata[i].map.mps)
						}

						let mxheartbeatresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getHeartBeatRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxhearbeatdata = await mxheartbeatresponse.json();
						for (let i = 0; i < mxhearbeatdata.length; i++) {
							heartbeatmmpsdata[i] = Number(mxhearbeatdata[i].map.mps)
						}

						let mxquorumresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getQuorumRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxquorumdata = await mxquorumresponse.json();
						for (let i = 0; i < mxquorumdata.length; i++) {
							quorummpsdata[i] = Number(mxquorumdata[i].map.mps)
						}

						let mxroundchangeresponse = await fetch("http://" + ipurl + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getRoundChangeRate", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let mxroundchangedata = await mxroundchangeresponse.json();
						for (let i = 0; i < mxroundchangedata.length; i++) {
							roundchangempsdata[i] = Number(mxroundchangedata[i].map.mps)
						}
						return roundchangempsdata;
					}
					getconsensusData().then(data =>
						new Chart("dltBFT", {
							//type: "bar",
							type: "line",
							data: {
								labels: consensusmsgtime,
								datasets: [{
									label: 'Propose Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(255, 99, 132)",
									borderColor: "rgb(255, 99, 132)",
									data: proposempsdata
								}, {
									label: 'Commit Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(255, 159, 64)",
									borderColor: "rgb(255, 159, 64)",
									data: commitmpsdata
								}, {
									label: 'Round Change Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(64, 67, 255)",
									borderColor: "rgb(64, 67, 255)",
									data: roundchangempsdata
								}, {
									label: 'Finalise Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(163, 34, 69)",
									borderColor: "rgb(163, 34, 69)",
									data: finalisempsdata
								}, {
									label: 'Quorum Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(8, 87, 30)",
									borderColor: "rgb(8, 87, 30)",
									data: quorummpsdata
								}, {
									label: 'HeartBeat Message Rate',
									fill: false,
									lineTension: 0,
									borderWidth: 1,
									backgroundColor: "rgb(128, 22, 181)",
									borderColor: "rgb(128, 22, 181)",
									data: heartbeatmmpsdata
								}]
							},
							options: {
								responsive: true,
								interaction: {
									mode: 'index',
									intersect: false,
								},
								stacked: false,
								plugins: {
									title: {
										display: true,
										text: 'Prepare,Commit,Finalise,Quorum, HeartBeat,RoundChange MPS'
									}
								},
								legend: { display: true }/* ,
							scales: {
								yAxes: [{ ticks: { min: 0, max: 50000 } }],
							} */
							}
						})

					);

				</script>
			</body>

			</html>