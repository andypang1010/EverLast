package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.utils.Json;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.redpacts.frostpurge.game.assets.AssetDirectory;

public class SaveFileManager {
    private JsonValue saveFile;
    private static final String SAVE_FILE_NAME = "save_data.json";

    public SaveFileManager(JsonValue json) {
        saveFile = json;
    }

    public void saveGame(String level, boolean unlocked, boolean completed, float score, float starScore) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                levelValue.get("unlocked").set(String.valueOf(new JsonValue(unlocked)));
                levelValue.get("completed").set(String.valueOf(new JsonValue(completed)));
                float currScore = levelValue.getFloat("score");
                if (score<currScore || currScore == 0){
                    levelValue.get("score").set(String.valueOf(new JsonValue(score)));
                }
                float currStarScore = levelValue.getFloat("starScore");
                if (starScore<currStarScore || currStarScore == 0){
                    levelValue.get("starScore").set(String.valueOf(new JsonValue(score)));
                }
                break;
            }
        }
        try (FileWriter fileWriter = new FileWriter(SAVE_FILE_NAME)) {
            fileWriter.write(saveFile.toJson(JsonWriter.OutputType.json));
        } catch (IOException e) {
            System.out.println("Error creating JSON file: " + e.getMessage());
        }
    }

    public void clearGame() {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals("level1")) {
                levelValue.get("unlocked").set(String.valueOf(new JsonValue(true)));
                levelValue.get("completed").set(String.valueOf(new JsonValue(false)));
            } else {
                levelValue.get("unlocked").set(String.valueOf(new JsonValue(false)));
                levelValue.get("completed").set(String.valueOf(new JsonValue(false)));
            }
            levelValue.get("score").set(String.valueOf(new JsonValue(0)));
            levelValue.get("starScore").set(String.valueOf(new JsonValue(0)));
            // Optionally update score if needed
            // levelValue.get("score").setInt(score);
        }
        try (FileWriter fileWriter = new FileWriter(SAVE_FILE_NAME)) {
            fileWriter.write(saveFile.toJson(JsonWriter.OutputType.json));
        } catch (IOException e) {
            System.out.println("Error creating JSON file: " + e.getMessage());
        }
    }
    public void unlockAll() {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            levelValue.get("unlocked").set(String.valueOf(new JsonValue(true)));
            levelValue.get("completed").set(String.valueOf(new JsonValue(true)));

            levelValue.get("score").set(String.valueOf(new JsonValue(0)));
            levelValue.get("starScore").set(String.valueOf(new JsonValue(0)));
        }
        try (FileWriter fileWriter = new FileWriter(SAVE_FILE_NAME)) {
            fileWriter.write(saveFile.toJson(JsonWriter.OutputType.json));
        } catch (IOException e) {
            System.out.println("Error creating JSON file: " + e.getMessage());
        }
    }

    public boolean getUnlockStatus(String level) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                return (levelValue.getString("unlocked").equals("true"));
            }
        }
        return false;
    }
    public boolean getCompleteStatus(String level) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                return (levelValue.getString("completed").equals("true"));
            }
        }
        return false;
    }
    public float getScore(String level) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                return (levelValue.getFloat("score"));
            }
        }
        return 0;
    }
    public float getStarScore(String level) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                return (levelValue.getFloat("starScore"));
            }
        }
        return 0;
    }

    public void update(JsonValue json){
        saveFile = json;
    }
}





