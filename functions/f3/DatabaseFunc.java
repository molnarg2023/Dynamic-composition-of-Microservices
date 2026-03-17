package functions.f3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;


public class DatabaseFunc implements Function<String,String>{
    private static final Gson gson = new Gson();

    @Override
    public String apply(String inputFilePath) {
        System.out.println(Thread.currentThread().getName() + "(F3) Database init...");
        try {
            String jsonInput = Files.readString(Path.of(inputFilePath));
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> plates = gson.fromJson(jsonInput, listType);
            System.out.println(Thread.currentThread().getName() + "(F3) Database updated: " + plates);
            Thread.sleep(500);

            String outputFilePath = "f3.json";
            Files.writeString(Path.of(outputFilePath), gson.toJson(plates));
            System.out.println("Database sent");
            return outputFilePath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
