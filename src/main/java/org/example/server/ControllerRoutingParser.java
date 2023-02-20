package org.example.server;

import org.example.server.controller.Controller;
import org.example.server.controller.Get;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerRoutingParser {
    public static Map<String, Method> parse(Controller controller) {
        Class<?> controllerClass = controller.getClass();

        Map<String, Method> routes = new HashMap<>();

        for (Method method : controllerClass.getDeclaredMethods()) {
            Get annotation = method.getAnnotation(Get.class);

            if (annotation != null) {
                method.setAccessible(true);

                routes.put(annotation.url(), method);
            }
        }

        return routes;
    }
}
