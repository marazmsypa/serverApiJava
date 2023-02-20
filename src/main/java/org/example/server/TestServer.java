package org.example.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.controllers.MainController;
import org.example.server.controller.Controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;

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

    private static class TestHandler implements HttpHandler {
        private final Controller controller;

        private final Map<String, Method> routes;

        private TestHandler(Controller controller) {
            this.controller = controller;
            this.routes = ControllerRoutingParser.parse(controller);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            for (String url : routes.keySet()) {
                if (url.equals(exchange.getRequestURI().toString())) {
                    Method method = routes.get(url);

                    try {
                        Object result = method.invoke(controller, exchange);
                        System.out.println(result);

                        DataOutputStream out = new DataOutputStream(exchange.getResponseBody());
                        out.writeUTF("<h1>Hello, Client!</h1>");
                        out.flush();
                        out.close();
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
