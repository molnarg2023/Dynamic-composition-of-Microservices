package wrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Wrapper {
    private static final Gson gson = new Gson();

    public static class ChainStep {
        public final String name;
        public final boolean isParallel;

        public ChainStep(String name , boolean isParallel) {
            this.name = name;
            this.isParallel = isParallel;
        }
    }

    public static void main(String[] args) throws Exception{
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        String configPath = "wrapper/chain-config.json";
        String configJson = Files.readString(Path.of(configPath));
        Type planType = new TypeToken<List<ChainStep>>(){}.getType();
        List<ChainStep> plan = gson.fromJson(configJson, planType);

        String currentPath = "";
        ConcurrentLinkedQueue<Path> garbagePaths = new ConcurrentLinkedQueue<>();
        garbagePaths.add(Path.of(currentPath));

        int nFunc = plan.size();

        try{
            for (int i = 0; i < nFunc; i++){
                ChainStep step = plan.get(i);

                if (!step.isParallel){
                    @SuppressWarnings("unchecked")
                    Function<String, String> functionInstance = (Function<String,String>) Class.forName(step.name).getDeclaredConstructor().newInstance();

                    currentPath = functionInstance.apply(currentPath);


                    if (currentPath != null && !currentPath.trim().isEmpty()){
                        garbagePaths.add(Path.of(currentPath));
                    }
                }
                else {
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<String> items = gson.fromJson(Files.readString(Path.of(currentPath)), listType);

                    List<CompletableFuture<String>> futures = new ArrayList<>();

                    for (String item : items){
                        String tempInput = "temp_" + UUID.randomUUID() + ".txt";
                        Files.writeString(Path.of(tempInput), item);
                        garbagePaths.add(Path.of(tempInput));

                        futures.add(CompletableFuture.supplyAsync(() ->{
                            try{
                                @SuppressWarnings("unchecked")
                                Function<String, String> functionInstance = (Function<String,String>) Class.forName(step.name).getDeclaredConstructor().newInstance();
                                return functionInstance.apply(tempInput); 

                            }
                            catch (Exception e){
                                throw new CompletionException(e);
                            }
                        }, executor));
                    }

                    List<String> tempOutputs = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

                    List<String> results = new ArrayList<>();
                    for (String tempOutput : tempOutputs){
                        garbagePaths.add(Path.of(tempOutput));
                        results.add(Files.readString(Path.of(tempOutput)));
                    }

                    String nextPath = "data_" + UUID.randomUUID() + ".json";
                    Files.writeString(Path.of(nextPath), gson.toJson(results));

                    currentPath = nextPath;
                    garbagePaths.add(Path.of(currentPath));
                }
            }
            garbagePaths.remove(Path.of(currentPath));
        }
        finally{
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            for(Path path : garbagePaths){
                try{
                    if (path != null && !path.toString().trim().isEmpty() && Files.isRegularFile(path)){
                        System.out.println(path + " deleted");
                        Files.deleteIfExists(path);
                    }
                }
                catch(Exception e){
                    System.err.println("Error..." + path);
                }
            }
        }
    }
}
