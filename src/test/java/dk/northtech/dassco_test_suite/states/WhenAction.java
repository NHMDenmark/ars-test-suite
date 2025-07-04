package dk.northtech.dassco_test_suite.states;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import dk.northtech.dassco_test_suite.metadata_model.LegalityModel;
import dk.northtech.dassco_test_suite.metadata_model.Metadata;
import dk.northtech.dassco_test_suite.metadata_model.UpdateMetadata;
import dk.northtech.dassco_test_suite.specify.SpecifyCredentials;
import dk.northtech.dassco_test_suite.specify.SpecifyClient;
import jakarta.annotation.Nullable;


public class WhenAction extends Stage<WhenAction> {
    // Auth Token:
    @ProvidedScenarioState
    public String token;

    // Workstation Variables:
    @ProvidedScenarioState
    public String workstationStatus;
    @ProvidedScenarioState
    public String newWorkstationStatus;

    @ProvidedScenarioState
    public String mainAsset;

    // HTTP:
    private final HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest request;
    HttpResponse<String> response;
    String zipCsvGuid;
    private static final Logger logger = LoggerFactory.getLogger(WhenAction.class);

    // ENV VARIABLES:
    @ProvidedScenarioState
    String fileProxyUrl;
    @ProvidedScenarioState
    String assetServiceUrl;
    @ProvidedScenarioState
    String keycloakHostname;
    @ProvidedScenarioState
    String clientId;
    @ProvidedScenarioState
    String clientSecret;
    @ProvidedScenarioState
    private String readRole1ClientId;
    @ProvidedScenarioState
    private String readRole1ClientSecret;
    @ProvidedScenarioState
    private String writeRole1ClientId;
    @ProvidedScenarioState
    private String writeRole1ClientSecret;
    @ProvidedScenarioState
    private String specifyCollectionId;

    // Created state
    @ProvidedScenarioState
    private boolean compareResult;

    // Specify credentials and client
    private final SpecifyCredentials specifyCredentials = new SpecifyCredentials();
    private final SpecifyClient specifyClient = new SpecifyClient(specifyCredentials);

    // Objectmapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // DASSCO-ASSET-SERVICE ENDPOINTS:
    public WhenAction a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection(String entityType, String i_role, String c_role, String i_name, String c_name, String p_name, String w_name){

        getToken();

        request = postRequestBuilder(entityType, i_role, c_role, i_name, c_name, p_name, w_name);
        
        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_a_collection(String institution_name, String collection_name, boolean body_present){

        getToken();

        String body = "";

        if (body_present) {
            body = "{ \"name\": \"" + collection_name + "\", \"institution\": \"" + institution_name + "\" }";
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution_name + "/collections"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_an_institution(String institution_name, boolean body_present){

        getToken();

        String body = "";

        if (body_present) {
            body = "{ \"name\": \"" + institution_name + "\" }";
            if (institution_name == null){
                body = "{ \"name\" : null }";
            }
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline(String institution_name, boolean body_present, String pipeline_name){

        getToken();

        String body = "";

        if (body_present) {
            body = "{ \"name\": \"" + pipeline_name + "\" }";
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution_name + "/pipelines"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_out_of_service_workstation(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/workstations"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"test-suite-workstation-out-of-service\", \"status\":\"OUT_OF_SERVICE\", \"institution_name\": \"test-suite-institution\"}"))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_a_workstation(String institution_name, boolean body_present, String status, String workstation_name){

        getToken();

        String body = "";

        if (body_present) {
            body = "{ \"name\": \"" + workstation_name + "\", \"status\": \"" + status + "\" }";
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution_name + "/workstations"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections(String entityType, String institution){

        request = getRequestBuilder(entityType, institution);

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_an_institution(String institutionName){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/" + institutionName))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_update_a_workstation(boolean invalidStatus){

        if (workstationStatus != null){
            if (workstationStatus.equals("IN_SERVICE")) {
                newWorkstationStatus = "OUT_OF_SERVICE";
            } else {
                newWorkstationStatus = "IN_SERVICE";
            }
        }

        if(invalidStatus){
            newWorkstationStatus = "INVALID_STATUS";
        }

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/test-suite-institution/workstations/test-suite-workstation"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString("{\"name\":\"test-suite-workstation\", \"status\":\"" + newWorkstationStatus + "\", \"institution_name\": \"test-suite-institution\"}"))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_fail_to_update_workstation(String institution_name, String workstation_name, String status, boolean body_present){

        getToken();

        String body = "";

        if (body_present) {
            body = "{ \"status\": \"" + status + "\" }";
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution_name + "/workstations/" + workstation_name))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_assets_metadata(String asset_guid, boolean locked){
        // Create new asset with the required minimal information: PID, GUID, STATUS, null PARENT_GUID, a WORKSTATION that is IN_SERVICE, Allocation > 0, Digitiser, Institution and Collection.

        getToken();

        String body = "{\"asset_pid\":\"test-suite-asset-pid\", \"asset_guid\":\"" + asset_guid + "\", \"status\":\"WORKING_COPY\", \"institution\":\"test-suite-institution\", \"collection\":\"test-suite-collection\", \"pipeline\":\"test-suite-pipeline\", \"workstation\": \"test-suite-workstation\", \"digitiser\":\"test-suite\", \"asset_locked\": " + locked + " }";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/?allocation_mb=10"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(Metadata model){
        // Create a new asset based on model data

        getToken();

        ObjectMapper mapper = new ObjectMapper();
        String body;
        try {
            body = mapper.writeValueAsString(model);
        } 
        catch (JsonProcessingException e) {
            e.printStackTrace();
            body = "{}";
        }

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/?allocation_mb=10"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions(String asset_guid, boolean locked, String institution, String collection, String pipeline, String workstation){
        // Create new asset with the required minimal information: PID, GUID, STATUS, null PARENT_GUID, a WORKSTATION that is IN_SERVICE, Allocation > 0, Digitiser, Institution and Collection.

        getToken();

        String body = "{\"asset_pid\":\"test-suite-asset-pid\", \"asset_guid\":\"" + asset_guid + "\", \"status\":\"WORKING_COPY\", \"institution\":\"" + institution + "\", \"collection\":\"" + collection + "\", \"pipeline\":\"" + pipeline + "\", \"workstation\": \"" + workstation + "\", \"digitiser\":\"test-suite\", \"asset_locked\": " + locked + " }";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/?allocation_mb=10"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata(String asset_pid, String asset_guid, String status, String institution, String collection, String pipeline, String workstation, int allocation){

        getToken();

        String body = "{\"asset_pid\":\"" + asset_pid + "\", \"asset_guid\":\"" + asset_guid + "\", \"status\":\"" + status + "\", \"institution\":\"" + institution + "\", \"collection\":\"" + collection + "\", \"pipeline\":\"" + pipeline + "\", \"workstation\":\"" + workstation + "\", \"digitiser\":\"test-suite\" }";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/?allocation_mb=" + allocation))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_an_asset(String assetGuid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + assetGuid))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_fail_the_retrieval_of_an_asset(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_update_an_asset(){

        // Minimum information for updating is: institution, workstation, pipeline, collection, status and update user.
        // Then the Update field. We are testing if "funding" changes value (original = null, updated = ["50000 kroner"])
        String body = "{\"asset_guid\":\"test-suite-asset-updated\", \"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"status\":\"WORKING_COPY\", \"updateUser\":\"test-suite\", \"funding\":[\"50000 kroner\"], \"asset_locked\": true }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-updated"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_fail_the_update_an_asset(String asset_guid, String institution, String workstation, String pipeline, String collection, String status, String updateUser, boolean asset_locked){

        String body = "{\"asset_guid\":\"" + asset_guid + "\", \"institution\":\"" + institution + "\", \"workstation\":\"" + workstation + "\", \"pipeline\":\"" + pipeline + "\", \"collection\":\"" + collection + "\", \"status\":\"" + status + "\", \"updateUser\":\"" + updateUser + "\", \"asset_locked\":" + asset_locked  + "}";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_asset(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-deleted"))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_assets_metadata(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/deleteMetadata"))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        
        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_fail_the_deletion_of_an_asset(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_receive_an_asset(){
        // ShareName and a MinimalAsset {asset_guid}
        String body = "{\"shareName\": \"test-suite-share-name\", \"minimalAsset\": { \"asset_guid\": \"test-suite-asset-received\" } }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-received/assetreceived"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_receive_an_asset(boolean share, String shareName, boolean minimalAsset){

        String body = "";
        getToken();

        HttpRequest.Builder newRequest = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-received/assetreceived"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token);

        if(share){
            body = "{ \"shareName\": \"" + shareName + "\" }";
            if (minimalAsset) {
                body = "{ \"shareName\": \"" + shareName + "\", { \"minimalAsset\": { \"asset_guid\": \"\" } }";
            }
        } else {
            body = "{ \"minimalAsset\": { \"asset_guid\": \"test-suite-asset-received\" } }";
        }

        newRequest.POST(HttpRequest.BodyPublishers.ofString(body));

        request = newRequest.build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_audit_an_asset(){

        String body = "{\"user\": \"test-suite-auditer\" }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-audited/audit"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_audit_an_asset(boolean digitiser){

        String body = "";

        if (digitiser){
            body = "{\"user\": \"test-suite\" }";
        } else {
            body = "{\"user\" : null }";
        }

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-audited/audit"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_complete_an_asset(String asset_guid){

        String body = "{\"shareName\": \"test-suite-share-name\", \"minimalAsset\": { \"asset_guid\": \"" + asset_guid + "\" } }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/complete"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_complete_an_asset(){

        String body = "{\"shareName\": \"test-suite-share-name\", \"minimalAsset\": { \"asset_guid\": null } }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-completed/complete"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_return_asset_events(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + mainAsset + "/events"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        System.out.println(request);
        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_fail_to_return_asset_events(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/events"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();

    }

    public WhenAction a_PUT_request_is_sent_to_manually_edit_an_assets_status(String asset_guid, String newStatus, String errorMessage){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/setstatus/?newStatus=" + newStatus + "&errorMessage=" + errorMessage))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_fail_to_set_an_asset_status(String asset_guid, String newStatus){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/setstatus/?newStatus=" + newStatus + "&errorMessage="))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_unlock_an_asset(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/unlock"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_fail_to_unlock_an_asset(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + asset_guid + "/unlock"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_bulk_update_assets(List<String> assetGuids){
        getToken();

        StringBuilder queryString = new StringBuilder();

        for (int i = 0; i < assetGuids.size(); i++){
            queryString.append("assets=").append(assetGuids.get(i));
            if (i < assetGuids.size() - 1){
                queryString.append("&");
            }
        }

        String body = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"status\":\"WORKING_COPY\", \"updateUser\":\"test-suite\", \"funding\":\"50000 kroner\" }";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_fail_bulk_update_assets(List<String> assetGuids, boolean body, boolean updateUser, boolean ownParent, boolean parentDoesntExist, boolean locked){

        getToken();

        StringBuilder queryString = new StringBuilder();

        for (int i = 0; i < assetGuids.size(); i++){
            queryString.append("assets=").append(assetGuids.get(i));
            if (i < assetGuids.size() - 1){
                queryString.append("&");
            }
        }

        if (body){
            if (updateUser){
                if (!locked){
                    String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"updateUser\": \"test-suite\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"asset_locked\": " + locked + " }";

                    request = HttpRequest.newBuilder()
                            .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                            .build();
                } else {
                    if (assetGuids.isEmpty()){
                        String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"updateUser\": \"test-suite\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"asset_locked\": " + locked + " }";

                        request = HttpRequest.newBuilder()
                                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                                .build();
                    }

                    if (assetGuids.size() == 2){
                        String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"updateUser\": \"test-suite\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"asset_locked\": " + locked + " }";

                        request = HttpRequest.newBuilder()
                                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                                .build();
                    }

                    if (ownParent){
                        String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"updateUser\": \"test-suite\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"parent_guid\": \"" + assetGuids.get(0) + "\", \"asset_locked\": " + locked + "}";
                        request = HttpRequest.newBuilder()
                                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                                .build();
                    }

                    if (parentDoesntExist){
                        String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"updateUser\": \"test-suite\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"parent_guid\": \"parent-non-existent\", \"asset_locked\": " + locked + "}";

                        request = HttpRequest.newBuilder()
                                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                                .build();
                    }
                }

            } else {
                String bodyObject = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"status\":\"WORKING_COPY\", \"funding\":\"50000 kroner\", \"asset_locked\": " + locked + " }";

                request = HttpRequest.newBuilder()
                        .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(bodyObject))
                        .build();
            }
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/bulkUpdate?" + queryString))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_assets(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assets"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_an_assets_status(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assets/status/" + asset_guid))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        try {
            makeApiCall(request);
        } catch (Exception e){
            e.printStackTrace();
        }

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_asset_group(String group_name, List<String> assets, boolean hasAccess){

        getToken();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "";

        if (!hasAccess){
            body = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [] }";
        } else {
            body = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [ \"service-account-test-suite-read-role-1\" ] }";
        }


        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/createassetgroup"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_asset_group_read_role_1(String group_name, List<String> assets, boolean hasAccess){

        getReadRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "";

        if (hasAccess){
            body = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [ \"test-user\" ] }";
        } else {
            body = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [] }";
        }


        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/createassetgroup"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_an_asset_group_write_role_1(String group_name, List<String> assets){

        getWriteRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [ \"test-suite\" ] }";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/createassetgroup"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(String group_name, List<String> assets, List<String> hasAccess, boolean body){

        getToken();

        if (body){
            StringBuilder stringAssets = new StringBuilder();
            for (int i = 0; i <= assets.size() - 1; i++){
                stringAssets.append("\"").append(assets.get(i)).append("\"");
                if (i < assets.size() - 1){
                    stringAssets.append(",");
                }
            }

            StringBuilder stringAccess = new StringBuilder();
            for (int i = 0; i <= hasAccess.size() - 1; i++){
                stringAccess.append("\"").append(hasAccess.get(i)).append("\"");
                if (i < assets.size() - 1){
                    stringAssets.append(",");
                }
            }

            String bodyString = "{\"group_name\": \"" + group_name + "\", \"assets\": [" + stringAssets + "], \"hasAccess\": [" + stringAccess + "]}";

            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/createassetgroup"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/createassetgroup"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_grant_access_to_asset_group_service_user(String groupName, List<String> users, boolean body){

        getToken();

        StringBuilder stringUsers = new StringBuilder();
        for (int i = 0; i <= users.size() - 1; i++){
            stringUsers.append("\"").append(users.get(i)).append("\"");
            if (i < users.size() - 1){
                stringUsers.append(",");
            }
        }
        if (body){
            String bodyString = "[" + stringUsers + "]";

            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/grantAccess/" + groupName))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/grantAccess/" + groupName))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_grant_access_to_asset_group_read_role_1(String groupName, List<String> users){

        getReadRole1Token();

        StringBuilder stringUsers = new StringBuilder();
        for (int i = 0; i <= users.size() - 1; i++){
            stringUsers.append("\"").append(users.get(i)).append("\"");
            if (i < users.size() - 1){
                stringUsers.append(",");
            }
        }

        String bodyString = "[" + stringUsers + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/grantAccess/" + groupName))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_grant_access_to_asset_group_write_role_1(String groupName, List<String> users){

        getWriteRole1Token();

        StringBuilder stringUsers = new StringBuilder();
        for (int i = 0; i <= users.size() - 1; i++){
            stringUsers.append("\"").append(users.get(i)).append("\"");
            if (i < users.size() - 1){
                stringUsers.append(",");
            }
        }

        String bodyString = "[" + stringUsers + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/grantAccess/" + groupName))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_revoke_access_to_asset_group_service_user(String groupName, List<String> users, boolean body){

        getToken();

        StringBuilder stringUsers = new StringBuilder();
        for (int i = 0; i <= users.size() - 1; i++){
            stringUsers.append("\"").append(users.get(i)).append("\"");
            if (i < users.size() - 1){
                stringUsers.append(",");
            }
        }
        if (body){
            String bodyString = "[" + stringUsers + "]";

            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/revokeAccess/" + groupName))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/revokeAccess/" + groupName))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_revoke_access_to_asset_group_write_role_1(String groupName, List<String> users){

        getWriteRole1Token();

        StringBuilder stringUsers = new StringBuilder();
        for (int i = 0; i <= users.size() - 1; i++){
            stringUsers.append("\"").append(users.get(i)).append("\"");
            if (i < users.size() - 1){
                stringUsers.append(",");
            }
        }

        String bodyString = "[" + stringUsers + "]";

        request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/revokeAccess/" + groupName))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();


        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_read_an_asset_group(String groupName){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/getgroup/" + groupName))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_asset_group(String groupName){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/deletegroup/" + groupName))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1(String groupName){

        getWriteRole1Token();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/deletegroup/" + groupName))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1(String groupName){

        getReadRole1Token();

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/deletegroup/" + groupName))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user(String groupName, List<String> assets, boolean body){

        getToken();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }
        if (body){
            String bodyString = "[" + stringAssets + "]";

            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/addAssets"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/addAssets"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
        }


        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_add_assets_to_asset_group_read_role_1(String groupName, List<String> assets){

        getReadRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "[" + stringAssets + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/addAssets"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_add_assets_to_asset_group_write_role_1(String groupName, List<String> assets){

        getWriteRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "[" + stringAssets + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/addAssets"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user(String groupName, List<String> assets, boolean body){

        getToken();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        if (body){
            String bodyString = "[" + stringAssets + "]";

            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/removeAssets"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyString))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/removeAssets"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
        }



        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_remove_assets_from_asset_group_read_role_1(String groupName, List<String> assets){

        getReadRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "[" + stringAssets + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/removeAssets"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_remove_assets_from_asset_group_write_role_1(String groupName, List<String> assets){

        getWriteRole1Token();

        StringBuilder stringAssets = new StringBuilder();
        for (int i = 0; i <= assets.size() - 1; i++){
            stringAssets.append("\"").append(assets.get(i)).append("\"");
            if (i < assets.size() - 1){
                stringAssets.append(",");
            }
        }

        String body = "[" + stringAssets + "]";

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetgroups/updategroup/" + groupName + "/removeAssets"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    // DASSCO-FILE-PROXY ENDPOINTS:
    public WhenAction a_GET_request_is_sent_to_get_list_of_asset_files_metadata(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assets/" + asset_guid + "/files"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_list_of_asset_files(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/" + asset_guid))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/" + asset_guid))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(String asset_guid, String institution, String collection){

        getToken();
        
        collection = collection.replaceAll(" ", "%20");
        request = HttpRequest.newBuilder()
            .uri(URI.create(fileProxyUrl + "/assetfiles/" + institution + "/" + collection + "/" + asset_guid))
            .header("Authorization", "Bearer " + token)
            .DELETE()
            .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_a_single_file_from_the_asset(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/test-suite-asset-created-and-uploaded/cat.png"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_fail_to_get_a_single_file_from_the_asset(String institution, String collection, String asset, String file){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/" + institution +  "/" + collection + "/" + asset + "/" + file))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_upload_a_file(String fileName, String crc, String asset_guid, int allocation){

        getToken();

        String pathToFile = "src/main/resources/static/" + fileName;
        Path file = Paths.get(pathToFile);

        try {
            HttpRequest.BodyPublisher bodyPublishers = HttpRequest.BodyPublishers.ofFile(file);

            request = HttpRequest.newBuilder()
                    .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/" + asset_guid + "/" + fileName + "?crc=" + crc + "&file_size_mb=" + allocation))
                    .header("Authorization", "Bearer " + token)
                    .PUT(bodyPublishers)
                    .build();

            makeApiCall(request);

        } catch (Exception e){
            e.printStackTrace();
        }
        return self();
    }

    public WhenAction a_PUT_request_is_sent_to_upload_a_file_to_NHMD_Vascular_Plants(String fileName, String crc, String asset_guid, int allocation){

        getToken();

        String pathToFile = "src/main/resources/static/" + fileName;
        Path file = Paths.get(pathToFile);

        try {
            HttpRequest.BodyPublisher bodyPublishers = HttpRequest.BodyPublishers.ofFile(file);

            request = HttpRequest.newBuilder()
                    .uri(URI.create(fileProxyUrl + "/assetfiles/NHMD/NHMD%20Vascular%20Plants/" + asset_guid + "/" + fileName + "?crc=" + crc + "&file_size_mb=" + allocation))
                    .header("Authorization", "Bearer " + token)
                    .PUT(bodyPublishers)
                    .build();

            makeApiCall(request);

        } catch (Exception e){
            e.printStackTrace();
        }
        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_a_single_file_from_the_asset(){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/test-suite-asset-file-to-delete-from-list/cat2.png"))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_fail_to_delete_a_single_file_from_the_asset(String file, String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/test-suite-institution/test-suite-collection/" + asset_guid + "/" + file))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_open_a_share(String asset_guid) throws JSONException {

        String body = "{ \"assets\": [ { \"asset_guid\": \"" + asset_guid + "\", \"institution\": \"test-suite-institution\", \"collection\": \"test-suite-collection\" } ], \"users\": [ \"service-account-test-suite-service-user\" ], \"allocation_mb\": 10 }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/"+ asset_guid +"/createShare"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_open_a_share(String asset_guid, String institution, String collection) throws JSONException {

        String body = "{ \"assets\": [ { \"asset_guid\": \"" + asset_guid + "\", \"institution\": \"" + institution + "\", \"collection\": \"" + collection + "\" } ], \"users\": [ \"service-account-test-suite-service-user\" ], \"allocation_mb\": 10 }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/"+ asset_guid +"/createShare"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_open_a_share(String asset_guid, String second_asset_guid, int allocation){
        String body = "{ \"assets\": [ { \"asset_guid\": \"" + asset_guid + "\", \"institution\": \"test-suite-institution\", \"collection\": \"test-suite-collection\" } ], \"users\": [ \"service-account-test-suite-service-user\" ], \"allocation_mb\": " + allocation + " }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + second_asset_guid + "/createShare"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();

    }

    public WhenAction a_POST_request_is_sent_to_change_allocation_of_a_share(){

        String body = "{ \"asset_guid\": \""+ mainAsset +"\", \"new_allocation_mb\": 10 }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + mainAsset + "/changeAllocation"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_change_allocation_of_a_share(int allocation, String asset_guid){

        String body = "{ \"asset_guid\": \"" + asset_guid + "\", \"new_allocation_mb\": " + allocation + " }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + asset_guid + "/changeAllocation"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_a_share(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + asset_guid + "/deleteShare"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_fail_delete_a_share(String asset_guid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + asset_guid + "/deleteShare"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_synchronize_with_erda(String assetGuid){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/" + assetGuid + "/synchronize?workstation=test-suite-workstation&pipeline=test-suite-pipeline"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_fail_to_synchronize_with_erda(String asset_guid, String workstation, String pipeline){
        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/"+ asset_guid + "/synchronize?workstation=" + workstation + "&pipeline=" + pipeline))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        makeApiCall(request);

        return self();

    }

    public WhenAction a_POST_request_is_sent_to_create_a_csv_file(List<String> assets) throws JsonProcessingException {

        getToken();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(assets);

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/createCsvFile"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        makeApiCall(request);

        zipCsvGuid = response.body();

        return self();
    }

    public WhenAction a_POST_request_is_sent_to_create_a_zip_file(List<String> assets) throws JsonProcessingException {
        getToken();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(assets);

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/createZipFile/" + zipCsvGuid))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_GET_request_is_sent_to_get_a_temp_file(String fileName){

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/getTempFile/" + zipCsvGuid + "/" + fileName))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_local_files(String institution, String collection, String assetGuid, String file){
        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/deleteLocalFiles/" + institution + "/" + collection + "/" + assetGuid + "/" + file))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_temp_folder(){
        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/assetfiles/deleteTempFolder/" + zipCsvGuid))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        makeApiCall(request);

        return self();
    }

    public WhenAction waiting_for_erda_to_synchronize(String assetGuid) throws JSONException, JsonProcessingException {

        getToken();

        Duration timeout = Duration.ofMinutes(2);
        Instant startTime = Instant.now();
        ObjectMapper OM = new ObjectMapper();

        while(true){
            // Check:
            a_GET_request_is_sent_to_get_an_assets_status(assetGuid);

            String responseBody = response.body();

            JsonNode rootNode = OM.readTree(responseBody);
            String status = rootNode.get("status").asText();

            if (!status.matches("ERDA_SYNCHRONISED")){
                logger.info("Erda hasn't synchronized yet. Trying again...");
            } else {
                logger.info("Erda has synchronized.");
                //a_GET_request_is_sent_to_get_an_assets_status("test-suite-asset-parent");
                break;
            }

            Instant currentTime = Instant.now();
            if(Duration.between(startTime, currentTime).compareTo(timeout) >= 0){
                logger.error("Timeout. Not attempting to synchronize anymore.");
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return self();
    }

    public WhenAction waiting_to_sync_with_specify(String assetGuid) throws JSONException, JsonProcessingException {

        getToken();

        Duration timeout = Duration.ofMinutes(3);
        Instant startTime = Instant.now();
        ObjectMapper OM = new ObjectMapper();

        while(true){

            // wait first to allow status change to happen, when updating data for an already specify synced asset
            Instant currentTime = Instant.now();
            if(Duration.between(startTime, currentTime).compareTo(timeout) >= 0){
                logger.error("Timeout. Not attempting to synchronize anymore.");
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check:
            a_GET_request_is_sent_to_get_an_assets_status(assetGuid);

            String responseBody = response.body();

            JsonNode rootNode = OM.readTree(responseBody);
            String status = rootNode.get("status").asText();

            if (!status.matches("SPECIFY_SYNCHRONISED")){
                logger.info("Specify hasn't synchronized yet. Trying again...");
            } else {
                logger.info("Specify has synchronized.");
        
                break;
            }

        }
        return self();
    }

    public WhenAction specimen_is_in_specify(String collection_object_id) throws JSONException, JsonProcessingException{
        String response = null;
        try {
            response = specifyClient.get("collectionobject", null).filter("id", collection_object_id).executeGetBody();
            
        } catch (Exception e){
            e.printStackTrace();
        }
        this.compareResult = specify_has_field_value_in_response(response, "meta.total_count", "1");
        return self();
    }

    public WhenAction get_and_compare_specify_data_with_model_data(String collection_object_id, UpdateMetadata updateMetadata) throws JSONException, JsonProcessingException{
        String response = null;
        try {
            response = specifyClient.get("collectionobjectattachment", null).filter("collectionobject_id", collection_object_id).limit(10).executeGetBody();
            
        } catch (Exception e){
            e.printStackTrace();
        }
        // logger.info(response);
        String fileType = updateMetadata.getFile_formats().get(0).toLowerCase();

        String specifyFilename = updateMetadata.getAsset_guid() + "." + fileType;
        String remarks = updateMetadata.getSpecify_attachment_remarks();
        String title = updateMetadata.getSpecify_attachment_title();
        String isPublic = Boolean.toString(updateMetadata.isMake_public());
        String dateAssetTaken = updateMetadata.getDate_asset_taken();

        LegalityModel legality = updateMetadata.getLegality();
        String copyright = legality.getCopyright();
        String license = legality.getLicense();
        String credit = legality.getCredit();


        Map<String, String> valueList = new HashMap<String, String>();

        valueList.put("title", title);
        valueList.put("remarks", remarks);
        valueList.put("origfilename", specifyFilename);
        valueList.put("mimetype", ("image/" + fileType));
        valueList.put("copyrightholder", copyright);
        valueList.put("credit", credit);
        valueList.put("license", license);
        valueList.put("ispublic", isPublic);
        // valueList.put("filecreateddate", token); // maybe only the date, not timestamp // add this later

        for (Map.Entry<String, String> entry : valueList.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entry.getValue();
            logger.info(key + " : " + expectedValue);
            Boolean currentResult = specify_has_field_value_in_response(response, ("objects[0].attachment." + key), expectedValue);
            if (!currentResult) {
                logger.info("Failed comparison for: " + key + " :: " + expectedValue);
                this.compareResult = false;
                return self();
            }
        }
        this.compareResult = true;
        return self();
    }

    public WhenAction get_and_compare_specify_updated_data_with_model_data(String collection_object_id, UpdateMetadata updateMetadata) throws JSONException, JsonProcessingException{
        String response = null;
        try {
            response = specifyClient.get("collectionobjectattachment", null).filter("collectionobject_id", collection_object_id).limit(10).executeGetBody();
            
        } catch (Exception e){
            e.printStackTrace();
        }

        String remarks = updateMetadata.getSpecify_attachment_remarks();

        LegalityModel legality = updateMetadata.getLegality();
        String credit = legality.getCredit();

        Map<String, String> valueList = new HashMap<String, String>();

        valueList.put("remarks", remarks);
        valueList.put("credit", credit);

        for (Map.Entry<String, String> entry : valueList.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entry.getValue();

            if (!key.equals("mime_type")){
            
                Boolean currentResult = specify_has_field_value_in_response(response, "objects[0].attachment." + key, expectedValue);
                if (!currentResult) {
                    this.compareResult = false;
                    return self();
                }
            }    
        }
        this.compareResult = true;
        return self();
    }

    public WhenAction a_DELETE_request_is_sent_to_delete_an_attachment_from_a_speciment(String collection_object_id) throws JSONException, JsonProcessingException{
        List<Map<String, Object>> response = new ArrayList<>();
        int attachmentId = -1;
        try {
            response = this.specifyClient.get("collectionobjectattachment", null).filter("collectionobject_id", collection_object_id).limit(2).execute();
            
        } catch (Exception e){
            e.printStackTrace();
        }

        if (response.size() != 1){
            logger.info("Found either multiple attachments or 0 attachments for specimen with collectionobject_id = " + collection_object_id + ". There should be 1 attachment.");
            this.compareResult = false;
            return self();
        }
        
        try{
            Object attachmentObj = response.get(0).get("attachment");
            if (attachmentObj instanceof Map) {
                Map<String, Object> attachmentMap = (Map<String, Object>) attachmentObj;
                attachmentId = (Integer)attachmentMap.get("id");
                
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (attachmentId == -1){
            logger.info("Failed to get the attachment id from specify");
            this.compareResult = false;
            return self();
        }

        String strAttachmentId = Integer.toString(attachmentId);

        try {            
            this.specifyClient.delete(strAttachmentId).execute();        

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.compareResult = true;
        return self();
    }


    // Helper functions: 
    public HttpRequest postRequestBuilder(String entityType, String i_role, String c_role, String i_name, String c_name, String p_name, String w_name){

        HttpRequest.Builder newRequest = HttpRequest.newBuilder();

        if (entityType.equals("institution")){
            if (i_role.isEmpty()){
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + i_name +"\", \"roleRestrictions\": []}"));
            } else {
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + i_name  +"\", \"roleRestrictions\": [{\"name\": \"" + i_role + "\"}]}"));
            }
        } else if (entityType.equals("workstation")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + i_name + "/workstations"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + w_name + "\", \"status\":\"IN_SERVICE\", \"institution_name\": \"" + i_name + "\"}"));
        } else if (entityType.equals("pipeline")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + i_name + "/pipelines"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + p_name + "\", \"institution\": \"" + i_name + "\"}"));
        } else if (entityType.equals("collection")) {
            
            if (c_role.isEmpty()){
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + i_name + "/collections"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + c_name + "\", \"institution\": \"" + i_name + "\", \"roleRestrictions\": []}"));
                        System.out.print("{\"name\":\"" + c_name + "\", \"institution\": \"" + i_name + "\", \"roleRestrictions\": []}");
            } else {
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + i_name + "/collections"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + c_name + "\", \"institution\": \"" + i_name + "\", \"roleRestrictions\": [{ \"name\": \"" + c_role + "\"}]}"));
            }
        }

        getToken();
        newRequest.header("Authorization", "Bearer " + token);

        return newRequest.build();
    }

    public HttpRequest getRequestBuilder(String entityType, String institution){

        HttpRequest.Builder newRequest = HttpRequest.newBuilder();

        if (entityType.equals("institution")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions"))
                    .GET();
        } else if (entityType.equals("workstation")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution + "/workstations"))
                    .GET();
        } else if(entityType.equals("pipeline")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution + "/pipelines"))
                    .GET();
        } else if(entityType.equals("collection")){
            newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions/" + institution + "/collections"))
                    .GET();
        }

        getToken();
        newRequest.header("Authorization", "Bearer " + token);
        return newRequest.build();
    }

    public int getStatusCode() {
        return response.statusCode();
    }

    public boolean getResponseArray() throws JSONException {
        JSONArray jsonArray = new JSONArray(response.body());
        if (jsonArray.length() > 0){
            return true;
        }
        return false;
    }

    public int getResponseArrayLength() throws JSONException {
        JSONArray jsonArray = new JSONArray(response.body());
        return jsonArray.length();
    }

    public String getHttpAllocationStatus(){
        String httpInfoStatus = "";

        try{
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject httpInfo = jsonResponse.getJSONObject("httpInfo");
            httpInfoStatus = httpInfo.getString("http_allocation_status");
        } catch(Exception e){
            e.printStackTrace();
        }

        return httpInfoStatus;
    }

    public String getShareHttpAllocationStatus() throws JSONException {

        String httpInfoStatus = "";
        JSONObject jsonResponse = new JSONObject(response.body());
        httpInfoStatus = jsonResponse.getString("http_allocation_status");

        try {

        } catch (Exception e){
            e.printStackTrace();
        }

        return httpInfoStatus;

    }

    public String getInternalStatus(){
        String internalStatus = "";

        try {
            JSONObject jsonResponse = new JSONObject(response.body());
            internalStatus = jsonResponse.getString("internal_status");
        } catch(Exception e){
            e.printStackTrace();
        }

        return internalStatus;
    }

    public String getStatus(){

        String status = "";

        try {
            JSONObject jsonResponse = new JSONObject(response.body());
            status = jsonResponse.getString("status");
        } catch(Exception e){
            e.printStackTrace();
        }

        return status;
    }

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

    public void getReadRole1Token(){

        // Parameters for getting the Token.
        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put("client_id", readRole1ClientId);
        requestBodyParams.put("client_secret", readRole1ClientSecret);
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

    public void getWriteRole1Token(){

        // Parameters for getting the Token.
        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put("client_id", writeRole1ClientId);
        requestBodyParams.put("client_secret", writeRole1ClientSecret);
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

    public void makeApiCall(HttpRequest request){
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logger.info(response.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public WhenAction compare_model_data_to_asset_in_ars(String model){

        try {
            
            JsonNode model_data = convert_json_to_node(model); 
            
            String assetGuid = model_data.get("asset_guid").textValue();
            
            String ars_asset = get_asset_metadata(assetGuid);
            
            JsonNode asset_data = convert_json_to_node(ars_asset);
            
            this.compareResult =  model_and_asset_data_match(model_data, asset_data);
            
        } catch (Exception e){
            e.printStackTrace();
        }

        return self();
    }
    
    public WhenAction compare_update_data_to_asset_in_ars(String model){

        try {
            
            JsonNode model_data = convert_json_to_node(model); 
            
            String assetGuid = model_data.get("asset_guid").textValue();
            
            String ars_asset = get_asset_metadata(assetGuid);
            
            JsonNode asset_data = convert_json_to_node(ars_asset);
            
            this.compareResult =  model_and_asset_data_match(model_data, asset_data);
            
        } catch (Exception e){
            e.printStackTrace();
        }

        return self();
    }

    public WhenAction update_asset_from_model(String model, String assetGuid){

        getToken();        

        request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/" + assetGuid))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(model))
                .build();

        makeApiCall(request);

        return self();
    }

    public String get_asset_metadata(String assetGuid){

        getToken();
        
        try {
            String uriString = assetServiceUrl + "/v1/assetmetadata/" + assetGuid;
            
            request = HttpRequest.newBuilder()
                    .uri(URI.create(uriString))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
        } catch(Exception e) {
            logger.error("Error while creating URI: " + e.getMessage(), e);
            throw e;
        }
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // logger.info(response.body());
            return response.body();
        } catch (Exception e){
            e.printStackTrace();
        }
        return response.body();
    }   

    public JsonNode convert_json_to_node(String json) throws IOException{
        JsonNode node = objectMapper.readTree(json);
        return node;
    }

    /**
     * Compares the shared keys between two JSON nodes.
     *
     * @param model The first JSON is the model.
     * @param asset The second JSON is the asset gotten from ARS.
     * @return true if for every key that exists in both nodes the values are identical, false otherwise.
     */
    public Boolean model_and_asset_data_match(JsonNode model, JsonNode asset) {
        
        if (model.isObject() && asset.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = model.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();

                // Only compare if the key exists in both nodes - and not automatic updates comes from ARS
                if (asset.has(key) && 
                    !key.equals("date_metadata_updated") && 
                    !key.equals("updateUser") && 
                    !key.equals("metadata_created_by") && 
                    !key.equals("metadata_updated_by")) {

                    JsonNode value1 = entry.getValue();
                    JsonNode value2 = asset.get(key);

                    // Handle keys starting with "date_"
                    if (key.startsWith("date_") && !value1.asText().equals("null") && !value2.asText().equals("null")) {
                        try {
                            Instant instant1 = Instant.parse(value1.asText());
                            Instant instant2 = Instant.parse(value2.asText());
                            if (!instant1.equals(instant2)) {
                                logger.error("Difference in Instant for key: " + key + " Model value: " + instant1 + " ARS value: " + instant2);
                                return false;
                            }
                            continue; // skip recursive call
                        } catch (DateTimeParseException e) {
                            logger.error("Invalid Instant format for key: " + key + " Value: " + value1.asText(), e);
                            return false;
                        }
                    }
                    
                    // handle arrays containing same objects but in different order
                    if (model.isArray() && asset.isArray()) {
                        ArrayNode array1 = (ArrayNode) model;
                        ArrayNode array2 = (ArrayNode) asset;

                        if (array1.size() != array2.size()) return false;

                        List<JsonNode> list1 = new ArrayList<>();
                        List<JsonNode> list2 = new ArrayList<>();
                        array1.forEach(list1::add);
                        array2.forEach(list2::add);

                        // Match elements from list1 with any in list2 (remove matched to avoid duplicates)
                        for (JsonNode node1 : list1) {
                            boolean matchFound = false;
                            Iterator<JsonNode> it = list2.iterator();
                            while (it.hasNext()) {
                                JsonNode node2 = it.next();
                                if (model_and_asset_data_match(node1, node2)) {
                                    it.remove(); // remove match
                                    matchFound = true;
                                    break;
                                }
                            }
                            if (!matchFound) {
                                logger.error("No matching element found for array item: " + node1);
                                return false;
                            }
                        }                        

                    // Recurse for other types
                    if (!model_and_asset_data_match(value1, value2)) {
                        logger.error("Difference found for key: " + key + " Model value: " + value1 + " ARS value: " + value2);
                        return false;
                    }
                }
            }
        }
            return true;
            
        }
        // For other types (string, number, boolean, null), compare them directly
        else {
            return model.equals(asset);
            }        
    }

    public Boolean specify_has_field_value_in_response(String response, String keyPath, String expectedValue) {
        Gson gson = new Gson();
        try {
            JsonElement root = gson.fromJson(response, JsonElement.class);
            JsonElement current = root;

            String[] parts = keyPath.split("\\.");

            for (String part : parts) {
                // Handle array indexes like objects[0]
                if (part.contains("[") && part.contains("]")) {
                    String fieldName = part.substring(0, part.indexOf("["));
                    int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
                    if (!current.getAsJsonObject().has(fieldName)) return false;
                    JsonArray array = current.getAsJsonObject().getAsJsonArray(fieldName);
                    if (array.size() <= index) return false;
                    current = array.get(index);
                } else {
                    if (!current.getAsJsonObject().has(part)) return false;
                    current = current.getAsJsonObject().get(part);
                }
            }

            // At this point, current should be the final value
            if (current.isJsonNull()) return expectedValue == null;

            String actualValue = current.getAsString();
            
            if (expectedValue.equals(actualValue)) {
                
                return true;
            } else {
                logger.info(actualValue + " : " + expectedValue + " failed to match for " + keyPath);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Failed to check nested key path: " + e.getMessage());
            return false;
        }
    }
}
