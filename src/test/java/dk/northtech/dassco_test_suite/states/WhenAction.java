package dk.northtech.dassco_test_suite.states;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        // Minimum information for updating is: institution name, workstation, pipeline, collection, status and update user.
        // Then the Update field. We are testing if "funding" changes value (original = null, updated = "50000 kroner")
        String body = "{\"institution\":\"test-suite-institution\", \"workstation\":\"test-suite-workstation\", \"pipeline\":\"test-suite-pipeline\", \"collection\":\"test-suite-collection\", \"status\":\"WORKING_COPY\", \"updateUser\":\"test-suite\", \"funding\":\"50000 kroner\", \"asset_locked\": true }";

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

        String body = "{\"institution\":\"" + institution + "\", \"workstation\":\"" + workstation + "\", \"pipeline\":\"" + pipeline + "\", \"collection\":\"" + collection + "\", \"status\":\"" + status + "\", \"updateUser\":\"" + updateUser + "\", \"asset_locked\": \"" + asset_locked + "\"}";

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

    // TODO: This endpoint used to work, but now it's not.
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

    public WhenAction a_POST_request_is_sent_to_open_a_share() throws JSONException {

        String body = "{ \"assets\": [ { \"asset_guid\": \"" + mainAsset + "\", \"institution\": \"test-suite-institution\", \"collection\": \"test-suite-collection\" } ], \"users\": [ \"service-account-test-suite-service-user\" ], \"allocation_mb\": 10 }";

        getToken();

        request = HttpRequest.newBuilder()
                .uri(URI.create(fileProxyUrl + "/shares/assets/"+ mainAsset +"/createShare"))
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

    public WhenAction waiting_for_erda_to_synchronize(String assetGuid) throws JSONException {

        getToken();

        Duration timeout = Duration.ofMinutes(2);
        Instant startTime = Instant.now();

        while(true){
            // Check:

            a_GET_request_is_sent_to_get_list_of_asset_files(assetGuid);

            if (getResponseArray()){
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

    // Helper functions:
    public HttpRequest postRequestBuilder(String entityType, String i_role, String c_role, String i_name, String c_name, String p_name, String w_name){

        HttpRequest.Builder newRequest = HttpRequest.newBuilder();

        if (entityType.equals("institution")){
            if (i_role.isEmpty()){
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + i_name +"\", \"roleRestriction\": []}"));
            } else {
                newRequest.uri(URI.create(assetServiceUrl + "/v1/institutions"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + i_name  +"\", \"roleRestriction\": [{\"name\": \"" + i_role + "\"}]}"));
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
            logger.info(response.body());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
