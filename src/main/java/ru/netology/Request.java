package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Request {
    private HttpMethod method;
    private Map<String,String> headers;
    private String body;
    private String url;
    private List<NameValuePair> queryParams;
    private String path;

    public Request(HttpMethod method, Map<String, String> headers, String body, String url) {
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.url = url;
        parseUrl();
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

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        for (NameValuePair nameValuePair : queryParams) {
            if(nameValuePair.getName().equals(name)) {
                return nameValuePair.getValue();
            }
        }
        return null;
    }

    public String getPath() {
        return path;
    }


    private void parseUrl() {
        String query = "";

        String[] arr1 = url.split("/?");
        path = arr1[0];
        if(arr1.length > 1 && arr1[1] != null) {
            String[] arr = arr1[1].split("/#");
            query = arr[0];
        }
       queryParams = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
    }

}
