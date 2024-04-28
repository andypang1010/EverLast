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

    public void saveGame(String level, boolean unlocked, boolean completed, int score) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                levelValue.get("unlocked").set(String.valueOf(new JsonValue(unlocked)));
                levelValue.get("completed").set(String.valueOf(new JsonValue(completed)));
                levelValue.get("score").set(String.valueOf(new JsonValue(score)));
                // Optionally update score if needed
                // levelValue.get("score").setInt(score);
                break;
            }
        }
        String fileName = "assets/save_data.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(saveFile.toJson(JsonWriter.OutputType.json));
        } catch (IOException e) {
            System.out.println("Error creating JSON file: " + e.getMessage());
        }
        System.out.println(saveFile);
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
            // Optionally update score if needed
            // levelValue.get("score").setInt(score);
        }
        String fileName = "assets/save_data.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
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
        }
        String fileName = "assets/save_data.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
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
    public int getScore(String level) {
        JsonValue levelsArray = saveFile.get("levels");
        for (JsonValue levelValue : levelsArray) {
            if (levelValue.getString("name").equals(level)) {
                return (levelValue.getInt("score"));
            }
        }
        return 0;
    }
    public void update(JsonValue json){
        saveFile = json;
    }
}





