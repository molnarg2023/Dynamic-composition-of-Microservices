package functions.f2;

import java.nio.file.*;
import java.util.function.Function;
import java.util.concurrent.ThreadLocalRandom;

public class LicensePlateFunc implements Function<String, String> {
    @Override
    public String apply(String inputFilePath) {
        
       try{
            String carID = Files.readString(Path.of(inputFilePath)).trim();
            System.out.println(Thread.currentThread().getName() + "(F2) License plate recognition: " + carID);
            Thread.sleep(ThreadLocalRandom.current().nextInt(500) + 500);
            String plate = carID.replace("car", "ABC") + 23;
            System.out.println(Thread.currentThread().getName() + "(F2) " + carID + "License plate recognized: " + plate);
            String outputFilePath = "f2OutPut" + carID + ".txt";
            Files.writeString(Path.of(outputFilePath), plate);
            return outputFilePath;
       } catch (Exception e) {
            throw new RuntimeException(e);
       }
    }
}