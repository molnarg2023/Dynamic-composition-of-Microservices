package functions.f4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;

public class f4 implements Function<String, String> {
    private static final Gson gson = new Gson();

    @Override
    public String apply(String inputPath) {
        try {
            String input = Files.readString(Path.of(inputPath));
            List<String> results = gson.fromJson(input, new TypeToken<List<String>>(){}.getType());
            
            System.out.println("f4 eredmények (" + results.size() + " db):");
            System.out.println(" -> " + results);
            
            return "";
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
