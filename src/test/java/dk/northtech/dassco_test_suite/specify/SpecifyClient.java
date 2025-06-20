package dk.northtech.dassco_test_suite.specify;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecifyClient {
    private final HttpClient httpClient;
    private final CookieManager cookieManager;
    private final Gson gson;
    private final String baseUrl;
    private String csrfToken;
    private final String collectionId;

    public SpecifyClient(SpecifyCredentials credentials) {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.gson = new Gson();

        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        this.baseUrl = credentials.getSpecifyUrl();
        this.collectionId = credentials.getSpecifyCollectionId();
        this.csrfToken = this.login(credentials);
    }

    private HttpRequest.Builder baseRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(this.baseUrl + path))
                .header("Content-Type", "application/json")
                .header("Referer", this.baseUrl);
        System.out.println(this.baseUrl + path);
        if (this.csrfToken != null) {
            builder.header("X-CSRFToken", this.csrfToken);
        }
        return builder;
    }

    private String sendRequest(HttpRequest req) {
        try {
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if(res.statusCode() >= 200 && res.statusCode() < 300) {
                return res.body();
            } else {
                throw new RuntimeException("API error (HTTP " + res.statusCode() + "): " + res.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String login(SpecifyCredentials credentials) {

        String specifyId = credentials.getSpecifyId();
        String specifySecret = credentials.getSpecifySecret();
        String collectionId = credentials.getSpecifyCollectionId();

        String requestBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"collection\":%s}",
                specifyId,
                specifySecret,
                collectionId
        );

        HttpRequest req = baseRequestBuilder("/context/login/")
                .header("X-CSRFToken", this.retrieveCsrfToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        sendRequest(req);
        return findCsrfToken();
    }

    private String findCsrfToken() {
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        for(HttpCookie cookie : cookies) {
            if ("csrftoken".equals(cookie.getName())) {
                this.csrfToken = cookie.getValue();
                return csrfToken;
            }
        }
        return null;
    }

    private String retrieveCsrfToken() {
        HttpRequest req = baseRequestBuilder("/context/login/")
                .GET()
                .build();

        sendRequest(req);
        return findCsrfToken();
    }

    public void logout() {
        String requestBody = String.format(
                "{\"username\": null,\"password\": null,\"collection\":%s}",
                collectionId
        );

        HttpRequest req = baseRequestBuilder("/context/login/")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        sendRequest(req);
    }

    public GetBuilder get(String objectName, String objectId) {
        return new GetBuilder(objectName, objectId);
    }

    public DeleteAttachmentBuilder delete(String assetGuid){
        return new DeleteAttachmentBuilder(assetGuid);
    }


    public class GetBuilder {
        private final String objectName;
        private String objectId;
        private int limit = 100;
        private int offset = 0;
        private String sort = "";
        private final Map<String, String> filters = new LinkedHashMap<>();

        public GetBuilder(String objectName, String objectId) {
            this.objectName = objectName;
            this.objectId = objectId;
        }

        public GetBuilder objectId(String objectId){
            this.objectId = objectId;
            return this;
        }

        public GetBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public GetBuilder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public GetBuilder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public GetBuilder filter(String key, String value) {
            this.filters.put(key, value);
            return this;
        }

        public List<Map<String, Object>> execute() {

            StringBuilder qs = new StringBuilder()
                    .append("limit=").append(limit)
                    .append("&offset=").append(offset);

            if (sort != null && !sort.isBlank()) {
                qs.append("&orderby=")
                        .append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
            }
            for (var e : filters.entrySet()) {
                qs.append('&')
                        .append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            }

            String urlExt = "/api/specify/" + objectName;
            String body;
            if (objectId != null){
                urlExt = "/api/specify/" + objectName + "/?attachment=" + objectId;
                System.out.println(urlExt);
                HttpRequest req = baseRequestBuilder(urlExt)
                    .GET()
                    .build();
                body = sendRequest(req);               
            }
            else{
                HttpRequest req = baseRequestBuilder(urlExt + "/?" + qs)
                    .GET()
                    .build();
                body = sendRequest(req);
            }
            
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
            JsonArray objects = jsonObject.getAsJsonArray("objects");
            var listType = new TypeToken<Map<String,Object>>(){}.getType();
            List<Map<String,Object>> result = new ArrayList<>();
            for (var el : objects) {
                result.add(gson.fromJson(el, listType));
            }
            return result;
        }
    

        public String executeGetBody() {

            StringBuilder qs = new StringBuilder()
                    .append("limit=").append(limit)
                    .append("&offset=").append(offset);

            if (sort != null && !sort.isBlank()) {
                qs.append("&orderby=")
                        .append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
            }
            for (var e : filters.entrySet()) {
                qs.append('&')
                        .append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            }

            String urlExt = "/api/specify/" + objectName;
            String body;
            if (objectId != null){
                urlExt = "/api/specify/" + objectName + "/?attachment=" + objectId;
                System.out.println(urlExt);
                HttpRequest req = baseRequestBuilder(urlExt)
                    .GET()
                    .build();
                body = sendRequest(req);               
            }
            else{
                HttpRequest req = baseRequestBuilder(urlExt + "/?" + qs)
                    .GET()
                    .build();
                body = sendRequest(req);
            }
                
            return body;
        }
    }

    public class DeleteAttachmentBuilder{
        private final String attachment_id;

        public DeleteAttachmentBuilder(String attachment_id){
            this.attachment_id = attachment_id;
        }

        public Boolean execute(){

            HttpRequest req = baseRequestBuilder("/api/specify/attachmentmetadata/" + attachment_id + "/")
                    .DELETE()
                    .build();

            try {
                HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                if(res.statusCode() >= 200 && res.statusCode() < 300) {
                    return true;
                } else {
                    throw new RuntimeException("API error (HTTP " + res.statusCode() + "): " + res.body());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
