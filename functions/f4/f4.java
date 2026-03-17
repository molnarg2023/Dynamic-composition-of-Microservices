package functions.f4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class f4 implements Function<String, String>{
    private static final Gson gson = new Gson();

    @Override
    public String apply(String inputFilePath) {
        try {
            String jsonInput = Files.readString(Path.of(inputFilePath));

            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> plates = gson.fromJson(jsonInput, listType);

            System.out.println("f4 " + plates);
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
