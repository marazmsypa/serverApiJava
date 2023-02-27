package org.example.server.model;

import java.util.Map;

public class RequestData {
    private Map<String, Object> postValues;

    private Map<String, Object> getParams;

    private Map<String, String> pathParams;

    private String httpMethod;

    public RequestData() {
    }

    public RequestData(Map<String, Object> postValues, Map<String, Object> getParams, Map<String, String> pathParams, String httpMethod) {
        this.postValues = postValues;
        this.getParams = getParams;
        this.pathParams = pathParams;
        this.httpMethod = httpMethod;
    }

    public Map<String, Object> getPostValues() {
        return postValues;
    }

    public void setPostValues(Map<String, Object> postValues) {
        this.postValues = postValues;
    }

    public Map<String, Object> getGetParams() {
        return getParams;
    }

    public void setGetParams(Map<String, Object> getParams) {
        this.getParams = getParams;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}
