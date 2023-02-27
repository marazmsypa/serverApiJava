package org.example.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.server.model.RequestData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBodyParser {
    private final static ObjectMapper mapper = new ObjectMapper();

    public static RequestData parseRequest(HttpExchange exchange, String url) {
        RequestData requestData = new RequestData();

        requestData.setHttpMethod(exchange.getRequestMethod());

        try {
            parseRequestBody(requestData, exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseGetParams(requestData, exchange);

        parsePath(requestData, exchange, url);

        return requestData;
    }

    public static void parseRequestBody(RequestData requestData, HttpExchange exchange)
            throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream in = exchange.getRequestBody();

        int c;
        while ((c = in.read()) != -1) {
            buf.append((char) c);
        }

        String body = new String(buf.toString().getBytes(StandardCharsets.ISO_8859_1));

        requestData.setPostValues(mapper.readValue(body, new TypeReference<>() {
        }));
    }

    public static void parseGetParams(RequestData requestData, HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.length() == 0) return;

        Map<String, Object> newMap = new HashMap<>();
        for (String part : query.split("&")) {
            if (part.length() == 0) continue;

            String[] param = part.split("=", 2);
            if (param.length == 1) newMap.put(param[0], null);
            else newMap.put(param[0], param[1]);
        }
        requestData.setGetParams(newMap);
    }

    public static void parsePath(RequestData requestData, HttpExchange exchange, String checkUrl) {
        List<String> pathChunks = filterArray(exchange.getRequestURI().getPath().split("/"));
        List<String> checkUrlChunks = filterArray(checkUrl.split("/"));
        Map<String, String> newMap = new HashMap<>();

        for (int i = 0; i < pathChunks.size(); i++) {
            if (isPath(checkUrlChunks.get(i))) {
                String Chunk = checkUrlChunks.get(i);
                newMap.put(Chunk.substring(1, Chunk.length() - 1), pathChunks.get(i));
            }
        }
        requestData.setPathParams(newMap);
    }

    private static boolean isPath(String chunk) {
        return chunk.charAt(0) == '{' && chunk.charAt(chunk.length() - 1) == '}';
    }

    private static List<String> filterArray(String[] arr) {
        return Arrays.stream(arr).filter(s -> s.length() > 0).toList();
    }
}
