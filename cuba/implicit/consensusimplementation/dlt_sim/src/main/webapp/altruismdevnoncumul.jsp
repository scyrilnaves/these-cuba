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
				<h3>Commit utilitarian Score</h3>
				<div id="myGridCommit" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Block Proposal Score</h3>
				<div id="myGridBlockProposal" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Heart Beat Score</h3>
				<div id="myGridHeartBeat" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Inter Block Time Coefficient</h3>
				<div id="myGridInterBlock" class="ag-theme-alpine" width="300" style="height: 500px"></div>

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
					//START PROCESSED COMMIT PART
					var columnCommitDefs = [];
					var rowCommitData = [];
					async function getcommitscoreData() {
						let utilitariancommitresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianCommitScoreGridNonCumul");
						let utilitariancommitdata = await utilitariancommitresponse.json();
						columnCommitDefs = utilitariancommitdata.gridHeaderArray;
						rowCommitData = utilitariancommitdata.scoreJSONValues;
						return rowCommitData;
					}
					getcommitscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnCommitDefs,
							rowData: rowCommitData,
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
						var gridDiv = document.querySelector('#myGridCommit');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END COMMIT PART
					//***

					//START PROCESSED HEART BEAT PART
					var columnHeartBeatDefs = [];
					var rowHeartBeatData = [];
					async function getheartbeatscoreData() {
						let utilitarianheartbeatresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianHeartBeatScoreGridNonCumul");
						let utilitarianheartbeatdata = await utilitarianheartbeatresponse.json();
						columnHeartBeatDefs = utilitarianheartbeatdata.gridHeaderArray;
						rowHeartBeatData = utilitarianheartbeatdata.scoreJSONValues;
						return rowHeartBeatData;
					}
					getheartbeatscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnHeartBeatDefs,
							rowData: rowHeartBeatData,
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
						var gridDiv = document.querySelector('#myGridHeartBeat');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END HEART BEAT PART
                    //***

					//****
					//START PROCESSED INTER BLOCK PART
					var columnInterBlockDefs = [];
					var rowInterBlockData = [];
					async function getinterblockscoreData() {
						let utilitarianinterblockresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianInterBlockTimeCoefficientGrid");
						let utilitarianinterblockdata = await utilitarianinterblockresponse.json();
						columnInterBlockDefs = utilitarianinterblockdata.gridHeaderArray;
						rowInterBlockData = utilitarianinterblockdata.scoreJSONValues;
						return rowInterBlockData;
					}
					getinterblockscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnInterBlockDefs,
							rowData: rowInterBlockData,
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
						var gridDiv = document.querySelector('#myGridInterBlock');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END INTER BLOCK PART
					//***

					//START PROCESSED BLOCK PROPOSAL PART
					var columnBlockProposalDefs = [];
					var rowBlockProposalData = [];
					async function getblockproposalscoreData() {
						let utilitarianblockproposalresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianBlockProposalScoreGrid");
						let utilitarianblockproposaldata = await utilitarianblockproposalresponse.json();
						columnBlockProposalDefs = utilitarianblockproposaldata.gridHeaderArray;
						rowBlockProposalData = utilitarianblockproposaldata.scoreJSONValues;
						return rowBlockProposalData;
					}
					getblockproposalscoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnBlockProposalDefs,
							rowData: rowBlockProposalData,
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
						var gridDiv = document.querySelector('#myGridBlockProposal');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END BLOCK PROPOSAL PART
                    //***

					

					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					/// END GRAPH PART ///

				</script>
			</body>

			</html>