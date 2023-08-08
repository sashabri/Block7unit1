package ru.netology;

import java.net.http.HttpRequest;
import java.util.Map;

public class Request {
    private HttpMethod method;
    private Map<String,String> headers;
    private String body;

    public Request(HttpMethod method, Map<String, String> headers, String body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
