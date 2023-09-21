package net.phoenix.api;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class RequestHandler {

    public static void get(String url, String player, String uuid, Consumer<? super String> callback) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("X-Minecraft-Username", player)
                .header("X-Minecraft-UUID", uuid)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body)
                .thenAcceptAsync(callback);
    }

    public static void post(String url, String json, String player, String uuid, Consumer<? super String> callback) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("X-Minecraft-Username", player)
                .header("X-Minecraft-UUID", uuid)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body)
                .thenAcceptAsync(callback);
    }

    public static boolean isUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
