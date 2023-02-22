package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.server.annotations.Controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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
//                StringBuilder buf = new StringBuilder();
//                InputStream in = exchange.getRequestBody();

//                int c;
//                while ((c = in.read()) != -1) {
//                    buf.append((char) c);
//                }

                String httpMethod = exchange.getRequestMethod();

                for (Route currentRoute : routes) {
                    if (currentRoute.url().equals(exchange.getRequestURI().toString()) &&
                            currentRoute.HTTPtype().equals(httpMethod)) {
                        Method method = currentRoute.function();

                        try {
                            Object result = method.invoke(controller, exchange);
                            byte[] response;

                            if (result instanceof byte[]) {
                                response = (byte[]) result;
                            } else {
                                response = mapper
                                        .writeValueAsString(result)
                                        .getBytes(StandardCharsets.UTF_8);
                            }

                            OutputStream os = exchange.getResponseBody();

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
    }
}
