package org.example.server.model;

import java.lang.reflect.Method;

public record Route(String url, Method function, String HTTPtype) {

}
