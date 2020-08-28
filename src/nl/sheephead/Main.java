package nl.sheephead;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Main {

    public static void main(String[] args) {
	// write your code here

        IntStream.range(0, 5).parallel().forEach(i -> {
            System.out.println(isSlimeChunk(6376913898547617582L, 3, i+2));
        });

        List<Integer> ints = new ArrayList<Integer>();
        ints.add(12);
        System.out.println(ints);
        ints.add(9);
        System.out.println(ints);
        ints.add(12);
        System.out.println(ints);

        // Some testing
        System.out.println("Hi!");
        System.out.println(Test(add(1, 3)));

        boolean x = true;
        List<Long> workingsSeeds = new ArrayList<Long>();

        while(x == true){
            List<Long> temp = run();
            System.out.println(temp);
            if (temp == null){
                System.out.println("All seeds have been processed");
                System.out.println("All working seeds: " + workingsSeeds);
                System.out.println("awaiting new batch");
                break;
            }
            if(temp.size() > 0){
                System.out.println(temp.get(0));
                if(temp.get(0) == 0124L){
                    System.out.println("B");
                } else {
                    for(int i = 0; i < temp.size(); i++){
                        long tempp = temp.get(i);
                        workingsSeeds.add(tempp);
                    }
                }


            }
        }
    }
    private static List run(){
        List<Long> workingSeeds = new ArrayList<Long>();
        // Code
        boolean b = false;
        String response = new String();
        while(!b){
            try {
                HttpResponse<String> a = getApiResponse();
                if(a.statusCode() == 123){
                    return null;
                }
                if (a == null){
                    List<Long> connectionError = new ArrayList<Long>();
                    connectionError.add(0124L);
                    return connectionError;
                }
                response = a.body();
                b = true;
            } catch(NullPointerException e){
                b = false;
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        // Variables from GET Request
        if(response == null){
            System.out.println("test");
            return null;
        }
        JSONObject job = new JSONObject();
        try{
            job = convertJSON(response);
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long seedMax = null;
        Long seedMin = null;
        Long set = null;
        JSONArray z = null;
        JSONArray x = null;
        if (job != null) {
            seedMax = (Long) job.get("seedMax");
            seedMin = (Long) job.get("seedMin");
            set = (Long) job.get("set");
            x = (JSONArray) job.get("x");
            z = (JSONArray) job.get("z");
        } else {
            System.out.println("EMPTY ERROR");
        }

        System.out.println("seedMax: " + seedMax + ", seedMin: " + seedMin + ", Set: " + set + ", X and Z: "  + x + z);


        JSONArray finalX = x;
        JSONArray finalZ = z;
        final long[] speed = {0l};
        long startTime = System.nanoTime();
        LongStream.range(seedMin, seedMax).parallel().forEach(i -> {
            speed[0]++;
            boolean c = parseSeed(i, finalX, finalZ);
            if (c){
                System.out.println("Speed: " + speed[0]/((System.nanoTime()-startTime)/1000000000) + " seeds a second");
                System.out.println(i + " <- this seed checks all the boxes");
                workingSeeds.add(i);
            }
        });
        return workingSeeds;
    }

    //Parses Seed
    private static boolean parseSeed(long seed, JSONArray x, JSONArray z){
        int temp = 0;
        int count = 0;
        for(int i = 0; i < x.size(); i++){
            Long xI = (Long) x.get(i);
            Long zI = (Long) z.get(i);
            boolean c = isSlimeChunk(seed, xI, zI);
            temp++;

            if (c){
                count++;
                if(count != temp && count != x.size()){
                    i = x.size() -1;
                }
            }
        }

        if (count == x.size()){
            return true;
        }
        return false;
    }

    // Checks the chunk
    private static boolean isSlimeChunk(long seed, long x, long z){
        Random rnd = new Random(
                seed +
                        (int) (x*x*0x4c1906) +
                        (int) (x*0x5ac0db) +
                        (int) (z*z)*0x4307a7L +
                        (int) (z * 0x5f24f) ^ 0x3ad8025fL
        );
        boolean isSlime = rnd.nextInt(10) == 0;
        return isSlime;
    }

    // Get Response from Web API
    private static HttpResponse<String> getApiResponse(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = (HttpRequest) HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:5000/state"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 123){
                //System.out.println("reacjed");
                return response;
            }
            if(response.statusCode() == 200){
                return response;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ConnectException e){
            System.out.println("Connection Error, retrying in 5 seconds");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get JSON
    private static JSONObject convertJSON(String e){
        try {
            Object obj = new JSONParser().parse(e);
            JSONObject job = (JSONObject) obj;
            return job;
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        return null;
    }

    // From Tutorial
    private static Integer add(int i, int i1) {
        return i + i1;
    }

    // Test
    public static Integer Test(Integer e){
        return e;
    }

}
