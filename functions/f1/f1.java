package functions.f1;

import com.google.gson.Gson;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;

public class f1 implements Function<String, String> {
    private static final Gson gson = new Gson();

    @Override
    public String apply(String inputPath) {
        try {
            System.out.println("F1 indul, generál 3 elemet.");
            List<String> data = List.of("Item_A", "Item_B", "Item_C");

            f1Helper.help();
            
            String outPath = "f1_" + UUID.randomUUID() + ".json";
            Files.writeString(Path.of(outPath), gson.toJson(data));
            return outPath;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
