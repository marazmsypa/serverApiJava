package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.server.annotations.Controller;
import org.example.server.model.RequestData;
import org.example.server.model.Route;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class TestServer {
    private HttpServer network;

    public TestServer(int port) throws IOException {
        this.network = HttpServer.create();

        this.network.bind(new InetSocketAddress(port), 0);
    }

    public void start() {
        network.start();
    }

    public void registerController(String uri, Controller controller) {
        HttpContext context = network.createContext(uri, new TestHandler(controller));
    }

    private boolean isPrimitive(Object obj) {
        return obj instanceof Integer ||
                obj instanceof String ||
                obj instanceof Long ||
                obj instanceof Short ||
                obj instanceof Character ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof Boolean ||
                obj instanceof Byte;
    }

    private static class TestHandler implements HttpHandler {
        private final Controller controller;

        private final List<Route> routes;

        private final ObjectMapper mapper = new ObjectMapper();

        private TestHandler(Controller controller) {
            this.controller = controller;
            this.routes = ControllerRoutingParser.parse(controller);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String httpMethod = exchange.getRequestMethod();

                for (Route currentRoute : routes) {
                    if (checkUrlEquals(exchange.getRequestURI(), currentRoute.url()) &&
                            currentRoute.HTTPtype().equals(httpMethod)) {
                        RequestData requestData = RequestBodyParser.parseRequest(exchange, currentRoute.url());

                        Method method = currentRoute.function();

                        try {
                            Object result = switch (method.getParameterCount()) {
                                case 0 -> method.invoke(controller);
                                case 1 -> method.invoke(controller, requestData);
                                case 2 -> method.invoke(controller, requestData, exchange);
                                default -> null;
                            };

                            byte[] response;

                            if (result instanceof byte[]) {
                                response = (byte[]) result;
                            } else {
                                response = mapper
                                        .writeValueAsString(result)
                                        .getBytes(StandardCharsets.UTF_8);
                            }

                            OutputStream os = exchange.getResponseBody();

                            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                            exchange.sendResponseHeaders(200, response.length);

                            os.write(response);

                            os.flush();
                            os.close();
                            exchange.close();
                            return;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
        }

        public boolean checkUrlEquals(URI reqUrl, String checkUrl) {
            List<String> pathChunks = filterArray(reqUrl.getPath().split("/"));
            List<String> checkUrlChunks = filterArray(checkUrl.split("/"));

            if (pathChunks.size() != checkUrlChunks.size()) {
                return false;
            }

            for (int i = 0; i < pathChunks.size(); i++) {
                if (!(isPath(checkUrlChunks.get(i)) ||
                        pathChunks.get(i).equals(checkUrlChunks.get(i)))) {
                    return false;
                }
            }

            return true;
        }

        private boolean isPath(String chunk) {
            return chunk.charAt(0) == '{' && chunk.charAt(chunk.length() - 1) == '}';
        }

        private List<String> filterArray(String[] arr) {
            return Arrays.stream(arr).filter(s -> s.length() > 0).toList();
        }
    }
}
