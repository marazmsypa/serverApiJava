package org.example.server;

import org.example.server.annotations.Controller;
import org.example.server.annotations.Get;
import org.example.server.annotations.Post;
import org.example.server.model.Route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ControllerRoutingParser {
    public static List<Route> parse(Controller controller) {
        Class<?> controllerClass = controller.getClass();

        List<Route> routes = new ArrayList<>();

        for (Method method : controllerClass.getDeclaredMethods()) {
            Get getAnnotation = method.getAnnotation(Get.class);
            Post postAnnotation = method.getAnnotation(Post.class);
            if (getAnnotation != null) {
                method.setAccessible(true);
                routes.add( new Route(getAnnotation.url(), method, "GET"));
            }
            if (postAnnotation != null) {
                method.setAccessible(true);
                routes.add( new Route(postAnnotation.url(), method, "POST"));
            }
        }

        return routes;
    }
}
