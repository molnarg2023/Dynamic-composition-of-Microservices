package functions.f1;

import com.google.gson.Gson;
import java.nio.file.*;
import java.util.List;
import java.util.function.Function;

public class CameraFunc implements Function<String, String> {
    private static final Gson gson = new Gson();

    @Override
    public String apply(String inputFilePath) {
        System.out.println(Thread.currentThread().getName() + "(F1) Camera init...");
        try {
            Thread.sleep(500);
            String outputFilePath = "f1_camera_output.json";
            List<String> cars = List.of("car1", "car2", "car3");
            Files.writeString(Path.of(outputFilePath), gson.toJson(cars));
            System.out.println(Thread.currentThread().getName() + "(F1) Camera captured " + cars.size() + " cars.");
            return outputFilePath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        CameraFunc camera = new CameraFunc();
        String resultPath = camera.apply("dummy_input.txt");
    }
}
