<!-- ADDED JAVA CLASS IMPORTS-->
<%@page import="org.renaultleat.properties.NodeProperty" %>
	<%@page import="org.renaultleat.network.QuorumStorage" %>
		<%@page import="org.renaultleat.node.Wallet" %>
			<!DOCTYPE html>
			<html lang="en">

			<head>
				<meta charset="utf-8">
				<title>DLT Simulator</title>
				<script src="chart/ag-grid-enterprise.min.js"></script>
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
				<div id="logo">
					<h1>DLT Simulator</h1>
					<img src="images/leat.jpeg" alt="LEAT" width="300" height="150">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp;
					<img src="images/renault.png" alt="Renault" width="300" height="150">
				</div>
				<div id="details">
					<h3>DLT Property</h3>
					<b>TotalNodes:</b> <input type="text" id="TotalNodes" value="" readonly>&nbsp&nbsp;
					<b>NodeIndex:</b><input type="text" id="NodeIndex" value="" readonly>&nbsp&nbsp;
				</div>
				<br>
				<h3>Missed Commit utilitarian Score</h3>
				<div id="myGridMissedCommit" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Malicious utilitarian Score</h3>
				<div id="myGridMalicious" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Missed Heart Beat utilitarian Score</h3>
				<div id="myGridMissedHeartBeat" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Missed Partial Block Proposal Score</h3>
				<div id="myGridMissedBlockProposal" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<script type="text/javascript">
					//Added all the necessary Node Property
					var totalNodes = '<%=NodeProperty.totalnodes %>';
					var nodeproperty = '<%=Wallet.nodeproperty %>';
					var port = '<%=NodeProperty.getCurrentPort() %>';
					document.getElementById("TotalNodes").value = totalNodes;
					document.getElementById("NodeIndex").value = nodeproperty;
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					// End API Calls

					//// GRAPH PART ////
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

					//****
					//START PROCESSED MISSED COMMIT PART
					var columnMissedCommitDefs = [];
					var rowMissedCommitData = [];
					async function getmissedcommitscoreData() {
						let utilitarianmissedcommitresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedCommitScoreGrid");
						let utilitarianmissedcommitdata = await utilitarianmissedcommitresponse.json();
						columnMissedCommitDefs = utilitarianmissedcommitdata.gridHeaderArray;
						rowMissedCommitData = utilitarianmissedcommitdata.scoreJSONValues;
						return rowMissedCommitData;
					}
					getmissedcommitscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnMissedCommitDefs,
							rowData: rowMissedCommitData,
							defaultColDef: {
								editable: true,
								sortable: true,
								flex: 1,
								minWidth: 100,
								filter: true,
								resizable: true,
							},
							enableRangeSelection: true,
							enableCharts: true,
							popupParent: document.body,
							chartThemes: ['ag-pastel', 'ag-material-dark', 'ag-vivid-dark', 'ag-solar']
						};
						var gridDiv = document.querySelector('#myGridMissedCommit');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END MISSED COMMIT PART
					//***

					//***
					//START PROCESSED MALICIOUS PART
					var columnMaliciousDefs = [];
					var rowMaliciousData = [];
					async function getmaliciousscoreData() {
						let utilitarianmaliciousresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMaliciousScoreGrid");
						let utilitarianmaliciousdata = await utilitarianmaliciousresponse.json();
						columnMaliciousDefs = utilitarianmaliciousdata.gridHeaderArray;
						rowMaliciousData = utilitarianmaliciousdata.scoreJSONValues;
						return rowMaliciousData;
					}
					getmaliciousscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnMaliciousDefs,
							rowData: rowMaliciousData,
							defaultColDef: {
								editable: true,
								sortable: true,
								flex: 1,
								minWidth: 100,
								filter: true,
								resizable: true,
							},
							enableRangeSelection: true,
							enableCharts: true,
							popupParent: document.body,
							chartThemes: ['ag-pastel', 'ag-material-dark', 'ag-vivid-dark', 'ag-solar']
						};
						var gridDiv = document.querySelector('#myGridMalicious');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END MALICIOUS PART
					//***

					//START PROCESSED MISSED HEART BEAT PART
					var columnMissedHeartBeatDefs = [];
					var rowMissedHeartBeatData = [];
					async function getmissedheartbeatscoreData() {
						let utilitarianmissedheartbeatresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedHeartBeatScoreGrid");
						let utilitarianmissedheartbeatdata = await utilitarianmissedheartbeatresponse.json();
						columnMissedHeartBeatDefs = utilitarianmissedheartbeatdata.gridHeaderArray;
						rowMissedHeartBeatData = utilitarianmissedheartbeatdata.scoreJSONValues;
						return rowMissedHeartBeatData;
					}
					getmissedheartbeatscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnMissedHeartBeatDefs,
							rowData: rowMissedHeartBeatData,
							defaultColDef: {
								editable: true,
								sortable: true,
								flex: 1,
								minWidth: 100,
								filter: true,
								resizable: true,
							},
							enableRangeSelection: true,
							enableCharts: true,
							popupParent: document.body,
							chartThemes: ['ag-pastel', 'ag-material-dark', 'ag-vivid-dark', 'ag-solar']
						};
						var gridDiv = document.querySelector('#myGridMissedHeartBeat');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END MISSED HEART BEAT PART
					//***

					//START PROCESSED MISSED BLOCK PROPOSAL PART
					var columnMissedBlockProposalDefs = [];
					var rowMissedBlockProposalData = [];
					async function getmissedblockproposalscoreData() {
						let utilitarianmissedblockproposalresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianMissedBlockProposalScoreGrid");
						let utilitarianmissedblockproposaldata = await utilitarianmissedblockproposalresponse.json();
						columnMissedBlockProposalDefs = utilitarianmissedblockproposaldata.gridHeaderArray;
						rowMissedBlockProposalData = utilitarianmissedblockproposaldata.scoreJSONValues;
						return rowMissedBlockProposalData;
					}
					getmissedblockproposalscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnMissedBlockProposalDefs,
							rowData: rowMissedBlockProposalData,
							defaultColDef: {
								editable: true,
								sortable: true,
								flex: 1,
								minWidth: 100,
								filter: true,
								resizable: true,
							},
							enableRangeSelection: true,
							enableCharts: true,
							popupParent: document.body,
							chartThemes: ['ag-pastel', 'ag-material-dark', 'ag-vivid-dark', 'ag-solar']
						};
						var gridDiv = document.querySelector('#myGridMissedBlockProposal');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END MISSED HEART BEAT PART
                    //***

					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					/// END GRAPH PART ///

				</script>
			</body>

			</html>