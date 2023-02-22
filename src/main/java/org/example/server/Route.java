package org.example.server;

import java.lang.reflect.Method;

public record Route(String url, Method function, String HTTPtype) {

}
