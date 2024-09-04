package dk.northtech.dassco_test_suite.states;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;

public class ThenOutcome extends Stage<ThenOutcome> {

    // Auth Token:
    @ProvidedScenarioState
    public String token;

    // Workstation Variables:
    @ProvidedScenarioState
    public String workstationStatus;
    @ProvidedScenarioState
    public String newWorkstationStatus;

    // HTTP:
    private final HttpClient httpClient = HttpClient.newHttpClient();
    public HttpResponse<String> response;
    public HttpRequest request;

    // ENV VARIABLES:
    @ProvidedScenarioState
    String assetServiceUrl;

    public ThenOutcome response_is_200(int statusCode){
        assertEquals("OK. Expected Code: 200", 200, statusCode);
        return self();
    }

    public ThenOutcome response_is_204(int statusCode){
        assertEquals("OK. Expected Code: 204", 204, statusCode);
        return self();
    }

    public ThenOutcome response_is_400(int statusCode){
        assertEquals("Expected Code: 400", 400, statusCode);
        return self();
    }

    public ThenOutcome response_is_403(int statusCode){
        assertEquals("FORBIDDEN. Expected Code: 403", 403, statusCode);
        return self();
    }

    public ThenOutcome response_is_404(int statusCode){
        assertEquals("Expected Code: 404", 404, statusCode);
        return self();
    }

    public ThenOutcome response_is_405(int statusCode){
        assertEquals("Expected Code: 405", 405, statusCode);
        return self();
    }

    public ThenOutcome response_is_500(int statusCode){
        assertEquals("Expected Code: 500", 500, statusCode);
        return self();
    }

    public ThenOutcome response_is_507(int statusCode){
        assertEquals("Expected Code: 507", 507, statusCode);
        return self();
    }

    public ThenOutcome workstation_status_changed(){
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
                        newWorkstationStatus = workstation.getString("status");
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (workstationStatus.equals("IN_SERVICE")){
            assertEquals("New Status is OUT_OF_SERVICE", "OUT_OF_SERVICE", newWorkstationStatus);
        } else if (workstationStatus.equals("OUT_OF_SERVICE")){
            assertEquals("New Status is IN_SERVICE", "IN_SERVICE", newWorkstationStatus);
        }

        return self();
    }

    public ThenOutcome workstation_is_set_as_IN_SERVICE_again(){
        HttpRequest.Builder newRequest = HttpRequest.newBuilder();
        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/workstations/test-suite-workstation"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString("{\"name\":\"test-suite-workstation\", \"status\":\"IN_SERVICE\", \"institution\":\"test-suite-institution\"}"))
                .header("Authorization", "Bearer " + token)
                .build();
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return self();
    }

    public ThenOutcome http_allocation_status_returns_success(String status){

        assertEquals("SUCCESS", status);

        return self();
    }

    public ThenOutcome http_allocation_status_returns_disk_full(String status){

        assertEquals("DISK_FULL", status);

        return self();
    }

    public ThenOutcome http_allocation_status_returns_bad_request(String status){

        assertEquals("BAD_REQUEST", status);

        return self();
    }

    public ThenOutcome response_has_populated_array(boolean populated){

        assertTrue(populated);

        return self();
    }

    public ThenOutcome response_has_empty_array(boolean populated){

        assertFalse(populated);

        return self();
    }

    public ThenOutcome response_array_has_one_element(int arrayLength){

        assertEquals(1, arrayLength);

        return self();
    }

    public ThenOutcome response_array_has_two_elements(int arrayLength){

        assertEquals(2, arrayLength);

        return self();
    }

    public ThenOutcome asset_internal_status_is_metadata_received(String internalStatus) {
        assertEquals(internalStatus, "METADATA_RECEIVED");
        return self();
    }

    public ThenOutcome asset_status_is_completed(String status){
        assertEquals("COMPLETED", status);
        return self();
    }
}
