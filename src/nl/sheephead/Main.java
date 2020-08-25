package nl.sheephead;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
	// write your code here


        // Some testing
        System.out.println("Hi!");
        System.out.println(Test(add(1, 3)));

        IntStream.range(0, 5).parallel().forEach(i -> {
            System.out.println(isSlimeChunk(6376913898547617582L, 3, i+2));
        });

        // Code
        String response = getApiResponse().body();
        // Variables from GET Request
        JSONObject job = (JSONObject) convertJSON(response);
        Long seedMax = (Long) job.get("seedMax");
        Long seedMin = (Long) job.get("seedMin");
        Long set = (Long) job.get("set");
        JSONArray x = (JSONArray) job.get("x");
        JSONArray z = (JSONArray) job.get("z");
    }

    // Checks the chunk
    private static boolean isSlimeChunk(long seed, int x, int z){
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
                return null;
            }
            if(response.statusCode() == 200){
                return response;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get JSON
    private static JSONObject convertJSON(String e){

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
