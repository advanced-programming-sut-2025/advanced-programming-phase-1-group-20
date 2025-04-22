package org.example.models.enums.Types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.example.models.entities.Plant;

public class LoadPlant {

    public Plant getPlant(String plantName) {
        try (Reader reader = new FileReader("plant.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            List<Plant> plants = gson.fromJson(reader, new TypeToken<List<Plant>>() {}.getType());

            for (Plant plant : plants) {
                if (plant.getName().equals(plantName)) {
                    return plant;
                }
            }

            return null;
        }catch (IOException e) {
            throw new RuntimeException("Error reading the file: ", e);
        }
    }
}