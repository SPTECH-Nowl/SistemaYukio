package sistemaCaptura;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class BotSlack {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    public static void sendMessage(JSONObject content) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

if (response.statusCode()!=200){
    System.out.println(String.format("Status: %s", response.statusCode()));
    System.out.println(String.format("Response: %s", response.body()));
}

    }
}
