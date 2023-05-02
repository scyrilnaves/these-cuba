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
				<h3>Effective utilitarian Score</h3>
				<div id="myGridEffective" class="ag-theme-alpine" width="300" style="height: 500px"></div>

				<h3>Utilitarian Classification</h3>
				<div id="myGridUtilitarianClassification" class="ag-theme-alpine" width="300" style="height: 500px">
				</div>

				<h3>Quorum Suspension</h3>
				<div id="myGridQuorumSuspension" class="ag-theme-alpine" width="300" style="height: 500px"></div>

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
					//START PROCESSED EFFECTIVE SCORE PART
					var columnEffectiveScoreDefs = [];
					var rowEffectiveScoreData = [];
					async function geteffectivescoreData() {
						let utilitarianeffectivescoreresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianEffectiveScoreGrid", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let utilitarianeffectivescoredata = await utilitarianeffectivescoreresponse.json();
						columnEffectiveScoreDefs = utilitarianeffectivescoredata.gridHeaderArray;
						rowEffectiveScoreData = utilitarianeffectivescoredata.scoreJSONValues;
						return rowEffectiveScoreData;
					}
					geteffectivescoreData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnEffectiveScoreDefs,
							rowData: rowEffectiveScoreData,
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
						var gridDiv = document.querySelector('#myGridEffective');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END EFFECTIVE SCORE PART
					//**

					//****
					//START PROCESSED CLASSIFICATION PART
					var columnClassificationDefs = [];
					var rowClassificationData = [];
					async function getclassificationData() {
						let utilitarianclassificationresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getUtilitarianClassificationGrid", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let utilitarianclassificationdata = await utilitarianclassificationresponse.json();
						columnClassificationDefs = utilitarianclassificationdata.gridHeaderArray;
						rowClassificationData = utilitarianclassificationdata.scoreJSONValues;
						return rowClassificationData;
					}
					getclassificationData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnClassificationDefs,
							rowData: rowClassificationData,
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
						var gridDiv = document.querySelector('#myGridUtilitarianClassification');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END CLASSIFICATION PART
					//**

					//****
					//START PROCESSED QUROUM SUSPENSION PART
					var columnQuorumSuspensionDefs = [];
					var rowQuorumSuspensionData = [];
					async function getquorumsuspensionData() {
						let utilitarianquorumsuspensionresponse = await fetch("http://localhost:" + port + "/distributed_ledger_simulator_framework_consensus_testbench/rest/simulatorcontroller/getQuorumSuspensionGrid", { method: "GET", mode: 'cors', headers: { 'Content-Type': 'application/json' } });
						let utilitarianquorumsupensiondata = await utilitarianquorumsuspensionresponse.json();
						columnQuorumSuspensionDefs = utilitarianquorumsupensiondata.gridHeaderArray;
						rowQuorumSuspensionData = utilitarianquorumsupensiondata.scoreJSONValues;
						return rowQuorumSuspensionData;
					}
					getquorumsuspensionData().then(data => {
						// specify the data
						var gridOptions = {
							columnDefs: columnQuorumSuspensionDefs,
							rowData: rowQuorumSuspensionData,
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
						var gridDiv = document.querySelector('#myGridQuorumSuspension');
						new agGrid.Grid(gridDiv, gridOptions);
					});
					//END QUORUM SUSPENSION PART
                    //**

					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					/// END GRAPH PART ///

				</script>
			</body>

			</html>