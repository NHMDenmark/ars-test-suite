package dk.northtech.dassco_test_suite.conditions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Conditions {

    public static HttpResponse<String> response;
    public static HttpClient httpClient = HttpClient.newHttpClient();
    public static HttpRequest request;
    public static String token;

    // ENV Variables:
    private static String fileProxyUrl;
    private static String assetServiceUrl;
    private static String keycloakHostname;
    private static String clientId;
    private static String clientSecret;
    private static final Logger logger = LoggerFactory.getLogger(Conditions.class);

    @Value("${test-asset}")
    private static String mainAsset;

    public static void init(Environment env){
        fileProxyUrl = env.getProperty("fileproxy.url");
        assetServiceUrl = env.getProperty("assetservice.url");
        keycloakHostname = env.getProperty("keycloak.hostname");
        clientId = env.getProperty("client.id");
        clientSecret = env.getProperty("client.secret");
    }

    // True if already exists.
    static boolean institutionAlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else if (response.statusCode() == 200) {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean institution2AlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-2"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else if (response.statusCode() == 200) {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean institution3AlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-3"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else if (response.statusCode() == 200) {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    // True if no workstation exists.
    static boolean noWorkstationExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/workstations"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0){
                return false;
            } else {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean noWorkstationExistsInstitution2(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-2/workstations"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0){
                return false;
            } else {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean noWorkstationExistsInstitution3(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-3/workstations"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0){
                return false;
            } else {
                return true;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    // True if institution has two workstations.
    static boolean institutionHasTwoWorkstations(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/workstations"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() == 2){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    // True if pipeline already exists.
    static boolean pipelineAlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/pipelines"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-pipeline")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean pipelineAlreadyExistsInstitution2(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-2/pipelines"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-pipeline-2")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean pipelineAlreadyExistsInstitution3(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-3/pipelines"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-pipeline-3")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    // True if collection already exists.
    static boolean collectionAlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/collections"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-collection")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean collectionAlreadyExistsInstitution2(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-2/collections"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-collection-2")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean collectionAlreadyExistsInstitution3(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution-3/collections"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray workstations = new JSONArray(response.body());
            if (workstations.length() > 0 && workstations.getJSONObject(0).getString("name").equals("test-suite-collection-3")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetAlreadyExists(){

        getToken();

        // Use Token to get Workstations for the test-suite-institution:
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + mainAsset))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetCreatedAndUploadedAlreadyExists(){

        getToken();

        // Use Token to get Workstations for the test-suite-institution:
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-created-and-uploaded"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetFilesToBeDeletedAlreadyExists(){

        getToken();

        // Use Token to get Workstations for the test-suite-institution:
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-files-to-be-deleted"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetFileToDeleteFromListAlreadyExists(){

        getToken();

        // Use Token to get Workstations for the test-suite-institution:
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-file-to-delete-from-list"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetFileToMessWithAllocationAlreadyExists(){

        getToken();

        // Use Token to get Workstations for the test-suite-institution:
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-to-mess-with-allocation"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean createdAssetAlreadyExists(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-created"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return true;
            } else if(response.statusCode() == 204){
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenUpdated(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-updated"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else {
                JSONObject jsonResponse = new JSONObject(response.body());
                String funding = jsonResponse.getString("funding");
                if (funding != "null"){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenDeleted(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-deleted"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                JSONObject jsonObject = new JSONObject(response.body());
                JSONArray events = jsonObject.getJSONArray("events");
                if (events.length() > 0){
                    JSONObject event = events.getJSONObject(0);
                    String status = event.getString("event");
                    if (status.equals("DELETE_ASSET_METADATA")){
                        return true;
                    } else {
                        return false;
                    }
                }

            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenUnlocked(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-unlocked"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 204){
                JSONObject jsonObject = new JSONObject(response.body());
                boolean locked = jsonObject.getBoolean("asset_locked");
                if (locked) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenReceived(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-received"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                String received = jsonObject.getString("internal_status");
                if (received.equals("ASSET_RECEIVED")){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenCompleted(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-completed"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                String received = jsonObject.getString("internal_status");
                if (received.equals("COMPLETED")){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyBeenAudited(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-audited"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                boolean audited = jsonObject.getBoolean("audited");
                if (audited){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static boolean assetHasAlreadyHadStatusChanged(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-status"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return false;
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                String status = jsonObject.getString("internal_status");
                if (status.equals("ERDA_FAILED")){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    static void getToken(){

        // Get Token:
        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put("client_id", clientId);
        requestBodyParams.put("client_secret", clientSecret);
        requestBodyParams.put("grant_type", "client_credentials");
        requestBodyParams.put("scope", "openid");
        String requestBody = requestBodyParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");

        request = HttpRequest.newBuilder()
                .uri(URI.create(keycloakHostname + "/realms/dassco/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Save the Token
                JSONObject jsonResponse = new JSONObject(response.body());
                token = jsonResponse.getString("access_token");
            } else {
                logger.error("Failed to obtain access token. HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
