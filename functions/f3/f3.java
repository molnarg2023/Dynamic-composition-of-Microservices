package functions.f3;

import java.nio.file.*;
import java.util.UUID;
import java.util.function.Function;

public class f3 implements Function<String, String> {
    @Override
    public String apply(String inputPath) {
        try {
            String input = Files.readString(Path.of(inputPath));
            System.out.println("    F3 feldolgozás " + input + " " + Thread.currentThread().getName());
            
            Thread.sleep(1500);
            
            String output = input + "_F3";
            String outPath = "f3_" + UUID.randomUUID() + ".txt";
            Files.writeString(Path.of(outPath), output);
            return outPath;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
