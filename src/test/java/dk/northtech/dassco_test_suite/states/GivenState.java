package dk.northtech.dassco_test_suite.states;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GivenState extends Stage<GivenState> {

    // Workstation Variable:
    @ProvidedScenarioState
    public String workstationStatus;

    // HTTP:
    public HttpResponse<String> response;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    public HttpRequest request;
    public String token;
    private static final Logger logger = LoggerFactory.getLogger(GivenState.class);

    // ENV VARIABLES:
    @ProvidedScenarioState
    private String fileProxyUrl;
    @ProvidedScenarioState
    private String assetServiceUrl;
    @ProvidedScenarioState
    private String keycloakHostname;
    @ProvidedScenarioState
    private String assetServiceHealth;
    @ProvidedScenarioState
    private String clientId;
    @ProvidedScenarioState
    private String clientSecret;
    @ProvidedScenarioState
    private String readRole1ClientId;
    @ProvidedScenarioState
    private String readRole1ClientSecret;
    @ProvidedScenarioState
    private String writeRole1ClientId;
    @ProvidedScenarioState
    private String writeRole1ClientSecret;
    @ProvidedScenarioState
    private String mainAsset;

    public GivenState setup(String fileProxyUrl, String assetServiceUrl, String assetServiceHealth, String keycloakHostname,
                            String clientId, String clientSecret, String readRole1ClientId, String readRole1ClientSecret,
                            String writeRole1ClientId, String writeRole1ClientSecret, String mainAsset){
        this.fileProxyUrl = fileProxyUrl;
        this.assetServiceUrl = assetServiceUrl;
        this.keycloakHostname = keycloakHostname;
        this.assetServiceHealth = assetServiceHealth;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.readRole1ClientId = readRole1ClientId;
        this.readRole1ClientSecret = readRole1ClientSecret;
        this.writeRole1ClientId = writeRole1ClientId;
        this.writeRole1ClientSecret = writeRole1ClientSecret;
        this.mainAsset = mainAsset;
        return self();
    }


    public GivenState dassco_asset_service_server_is_up(){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceHealth + "/actuator/health"))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            String status = jsonResponse.getString("status");

            if (status.equals("UP")){
                return self();
            } else {
                throw new IllegalStateException("Server is not running or it's unhealthy");
            }
        } catch (Exception e){
            throw new IllegalStateException("Error occurred while checking Server health: " + e.getMessage());
        }
    }

    public GivenState dassco_file_proxy_server_is_up(){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/actuator/health"))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            String status = jsonResponse.getString("status");

            if (status.equals("UP")){
                return self();
            } else {
                throw new IllegalStateException("Server is not running or it's unhealthy");
            }
        } catch (Exception e){
            throw new IllegalStateException("Error occurred while checking Server health: " + e.getMessage());
        }
    }

    // Checks the status of the first created workstation
    public GivenState workstation_has_status(){

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
                for (int i = 0; i < workstations.length(); i++){
                    JSONObject workstation = workstations.getJSONObject(i);
                    if (workstation.getString("name").equals("test-suite-workstation")){
                        workstationStatus = workstation.getString("status");
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return self();
    }

    // Get auth token:
    public void getToken(){

        // Parameters for getting the Token.
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
