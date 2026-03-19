package functions.f2;

import java.nio.file.*;
import java.util.UUID;
import java.util.function.Function;

public class f2 implements Function<String, String> {
    @Override
    public String apply(String inputPath) {
        try {
            String input = Files.readString(Path.of(inputPath));
            System.out.println("  F2 feldolgozás: " + input + " " + Thread.currentThread().getName());
            
            Thread.sleep(1000);
            
            String output = input + "_F2";
            String outPath = "f2_" + UUID.randomUUID() + ".txt";
            Files.writeString(Path.of(outPath), output);
            return outPath;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
