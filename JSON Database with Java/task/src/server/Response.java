package server;

import com.google.gson.JsonElement;

public class Response {
    private String response;
    private JsonElement value;
    private String reason;

    private Response(String response, JsonElement value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public static Response ok() {
        return new Response("OK", null, null);
    }

    public static Response okWithValue(JsonElement value) {
        return new Response("OK", value, null);
    }

    public static Response error(String reason) {
        return new Response("ERROR", null, reason);
    }
}