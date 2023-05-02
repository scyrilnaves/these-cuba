package org.renaultleat.network;

import com.google.gson.JsonArray;

import org.json.JSONObject;

public class MainGrid {

    // Only Header Values
    JsonArray gridHeaderArray;

    // Real Values
    JsonArray scoreJSONValues;
    // Real Values
    JsonArray altuismGridValues;

    public JsonArray getGridHeaderArray() {
        return this.gridHeaderArray;
    }

    public void setGridHeaderArray(JsonArray gridHeaderArray) {
        this.gridHeaderArray = gridHeaderArray;
    }

    public JsonArray getScoreJSONValues() {
        return this.scoreJSONValues;
    }

    public void setScoreJSONValues(JsonArray scoreJSONValues) {
        this.scoreJSONValues = scoreJSONValues;
    }

    public JsonArray getAltuismGridValues() {
        return this.altuismGridValues;
    }

    public void setAltuismGridValues(JsonArray altuismGridValues) {
        this.altuismGridValues = altuismGridValues;
    }

    public MainGrid() {
        this.gridHeaderArray = new JsonArray();
        this.scoreJSONValues = new JsonArray();
        this.altuismGridValues = new JsonArray();
    }

}
