package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AssetServiceAssetMetadataTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceAssetMetadataTests.class);

    @Value("${test-asset}")
    private String mainAsset;

    @Test
    @Order(0)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#createdAssetAlreadyExists")
    public void create_asset_metadata() {
        logger.info("Creating Asset: test-suite-asset-created");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-created", false);
        then().response_is_200(when().getStatusCode())
                .and().asset_internal_status_is_metadata_received(when().getInternalStatus());
    }

    @Test
    public void fail_create_asset_metadata(){
        logger.info("Creating asset with missing information to trigger expected errors:");
        logger.info("Missing status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("", "", "", "", "", "", "", 0);
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("", "", "INCORRECT_STATUS_SENT", "", "", "", "", 0);
        then().response_is_400(when().getStatusCode());

        logger.info("0 mb allocated");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("", "", "WORKING_COPY", "", "", "", "", 0);
        then().response_is_400(when().getStatusCode());

        logger.info("No asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("", "", "WORKING_COPY", "", "", "", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No asset_pid");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("", "test-asset-for-failing-creation", "WORKING_COPY", "", "", "", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No institution");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("test-asset-for-failing-creation", "test-asset-for-failing-creation", "WORKING_COPY", "", "", "", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No collection");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("test-asset-for-failing-creation", "test-asset-for-failing-creation", "WORKING_COPY", "test-suite-institution", "", "", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("test-asset-for-failing-creation", "test-asset-for-failing-creation", "WORKING_COPY", "test-suite-institution", "test-suite-collection", "", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No workstation");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("test-asset-for-failing-creation", "test-asset-for-failing-creation", "WORKING_COPY", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("Creating asset in a workstation out of service");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata("test-asset-for-failing-creation", "test-asset-for-failing-creation", "WORKING_COPY", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation-out-of-service", 10);
        then().response_is_403(when().getStatusCode());

        logger.info("Creating an already existing asset");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_metadata(mainAsset, mainAsset, "WORKING_COPY", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation", 10);
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void get_asset_metadata(){
        logger.info("Getting asset metadata");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_an_asset(mainAsset);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_get_asset_metadata(){
        logger.info("Getting asset metadata with no asset_guid to trigger expected errors");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_fail_the_retrieval_of_an_asset("");
        then().response_is_405(when().getStatusCode());

        logger.info("Getting asset metadata with incorrect asset_guid to trigger expected errors");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_fail_the_retrieval_of_an_asset("test-this-is-incorrect");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(1)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenUpdated")
    public void update_asset_metadata() {
        logger.info("Creating asset: test-suite-asset-updated");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-updated", true);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_update_an_asset();
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void failed_to_update_asset_metadata() {
        logger.info("Trying to update asset with incorrect metadata to trigger expected errors:");
        logger.info("No asset guid");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("", "", "", "", "", "", "", true);
        then().response_is_405(when().getStatusCode());

        logger.info("Empty status");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "", "", "", "", "", "", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect status");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "", "", "", "", "VERY_INCORRECT_STATUS", "", true);
        then().response_is_400(when().getStatusCode());

        logger.info("No updateUser");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "", "", "", "", "WORKING_COPY", "", true);
        then().response_is_400(when().getStatusCode());

        logger.info("No institution");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "", "", "", "", "WORKING_COPY", "test-suite", true);
        then().response_is_400(when().getStatusCode());

        logger.info("No collection");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "test-suite-institution", "", "", "", "WORKING_COPY", "test-suite", true);
        then().response_is_400(when().getStatusCode());

        logger.info("No pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "test-suite-institution", "", "", "test-suite-collection", "WORKING_COPY", "test-suite", true);
        then().response_is_400(when().getStatusCode());

        logger.info("No workstation");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "test-suite-institution", "", "test-suite-pipeline", "test-suite-collection", "WORKING_COPY", "test-suite", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Trying to unlock an asset from the update");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_the_update_an_asset("test-suite-asset-updated", "test-suite-institution", "test-suite-workstation", "test-suite-pipeline", "test-suite-collection", "WORKING_COPY", "test-suite", false);
        then().response_is_403(when().getStatusCode());

    }

    @Test
    @Order(2)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenDeleted")
    public void delete_asset() {
        logger.info("Creating asset: test-suite-asset-deleted");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-deleted", false);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_asset();
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_to_delete_asset(){
        logger.info("Trying to delete asset with empty asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_fail_the_deletion_of_an_asset("");
        then().response_is_405(when().getStatusCode());

        logger.info("Trying to delete asset with incorrect asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_fail_the_deletion_of_an_asset("this-asset-does-not-exist");
        then().response_is_400(when().getStatusCode());
    }

    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenReceived")
    @Test
    @Order(3)
    public void receive_asset() {
        logger.info("Creating asset: test-suite-asset-received");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-received", false);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset("test-suite-asset-received");
        then().response_is_204(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_receive_an_asset();
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_receive_asset() {
        logger.info("Trying to receive assets with incorrect metadata to trigger expected errors");
        logger.info("Trying to receive asset without share");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_receive_an_asset(false, "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Trying to receive asset without shareName");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_receive_an_asset(true, "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Trying to receive asset without minimalAsset");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_receive_an_asset(true, "test-suite-share-name", false);
        then().response_is_400(when().getStatusCode());
    }


    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenCompleted")
    @Test
    @Order(4)
    public void complete_asset() {
        logger.info("Creating asset: test-suite-asset-completed");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-completed", false);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_complete_an_asset("test-suite-asset-completed");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_complete_asset() {
        logger.info("Trying to complete asset with incorrect metadata to trigger expected errors");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_complete_an_asset();
        then().response_is_400(when().getStatusCode());
    }

    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenAudited")
    @Test
    @Order(5)
    public void audit_asset() {
        logger.info("Creating asset: test-suite-asset-audited");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-audited", false);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_complete_an_asset("test-suite-asset-audited");
        then().response_is_204(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_audit_an_asset();
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_audit_asset() {
        logger.info("Trying to audit asset with incorrect metadata to trigger expected errors");
        logger.info("Trying to audit asset without a user");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_audit_an_asset(true);
        then().response_is_403(when().getStatusCode());


        logger.info("Trying to audit asset with a null user");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_audit_an_asset(false);
        then().response_is_400(when().getStatusCode());
    }

    @Test
        public void get_asset_event(){
        logger.info("Getting asset event");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_return_asset_events();
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void failed_get_asset_event(){
        logger.info("Trying to get asset events with no asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_fail_to_return_asset_events("");
        then().response_is_400(when().getStatusCode());
    }

    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyHadStatusChanged")
    @Test
    @Order(6)
    public void set_asset_status() {
        logger.info("Creating asset: test-suite-asset-status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-status", false);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_manually_edit_an_assets_status("test-suite-asset-status", "ERDA_FAILED", "Error");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_set_asset_status() {
        logger.info("Trying to set asset status with missing metadata to trigger expected errors");
        logger.info("Sending empty parameters");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_set_an_asset_status("", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Sending empty status, correct asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_set_an_asset_status("test-suite-asset-status", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Sending correct status, incorrect asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_set_an_asset_status("this-asset-does-not-exist", "ERDA_FAILED");
        then().response_is_400(when().getStatusCode());
    }


    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetHasAlreadyBeenUnlocked")
    @Test
    @Order(7)
    public void unlock_asset() {
        logger.info("Creating asset: test-suite-asset-unlocked");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-unlocked", true);
        then().response_is_200(when().getStatusCode());

        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset("test-suite-asset-unlocked");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void failed_unlock_asset(){
        logger.info("Trying to unlock asset with no asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_unlock_an_asset("");
        then().response_is_400(when().getStatusCode());

        logger.info("Trying to unlock asset with incorrect asset_guid");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_unlock_an_asset("this-asset-does-not-exist");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    @Order(8)
    public void bulk_update_asset_metadata(){
        logger.info("Creating asset: test-suite-bulk-update-1");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-bulk-update-1", false);
        then().response_is_200(when().getStatusCode());

        logger.info("Creating asset: test-suite-bulk-update-2");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-bulk-update-2", false);
        then().response_is_200(when().getStatusCode());

        List<String> assetGuids = new ArrayList<>();
        assetGuids.add("test-suite-bulk-update-1");
        assetGuids.add("test-suite-bulk-update-2");

        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_bulk_update_assets(assetGuids);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    void fail_bulk_update_asset_metadata(){
        logger.info("Trying to bulk update assets with incorrect metadata to trigger expected errors:");

        List<String> assetGuids = new ArrayList<>();

        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("bulk-update-errors-1", true);
        then().response_is_200(when().getStatusCode());

        assetGuids.add("bulk-update-errors-1");

        logger.info("No body:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, false, false, false, false, true);
        then().response_is_400(when().getStatusCode());

        logger.info("No update user:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, true, false, false, false, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Assets to update is empty:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(new ArrayList<>(), true, true, false, false, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more assets do not exist:");
        assetGuids.add("bulk-update-errors-2");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, true, true, false, false, true);
        then().response_is_400(when().getStatusCode());
        assetGuids.remove(1);

        logger.info("Asset cannot be its own parent:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, true, true, true, false, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset parent does not exist:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, true, true, false, true, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Trying to unlock locked asset:");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_bulk_update_assets(assetGuids, true, true, false,false, false);
        then().response_is_403(when().getStatusCode());


    }

    @Test
    @Order(Integer.MAX_VALUE - 10)
    public void close_share_and_delete_asset_unlocked() throws JSONException {
        // test-suite-asset-unlocked
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-unlocked");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-unlocked");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 9)
    public void close_share_and_delete_asset_status() throws JSONException {
        // test-suite-asset-status
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-status");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-status");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 8)
    public void close_share_and_delete_asset_audited() throws JSONException {
        // test-suite-asset-audited
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-audited");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-audited");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 7)
    public void close_share_and_delete_asset_completed() throws JSONException {
        // test-suite-asset-completed
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-completed");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-completed");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 6)
    public void close_share_and_delete_asset_received() throws JSONException {
        // test-suite-asset-received
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-received");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-received");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 5)
    public void close_share_and_delete_asset_deleted() throws JSONException {
        // test-suite-asset-deleted
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-deleted");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-deleted");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 4)
    public void close_share_and_delete_asset_updated() throws JSONException {
        // test-suite-asset-updated
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-updated");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-updated");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 3)
    public void close_share_and_delete_asset_created() throws JSONException {
        // test-suite-asset-created
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-created");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-created");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 2)
    public void close_share_and_delete_asset_bulk_update_1() throws JSONException {
        //test-suite-bulk-update-1
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-bulk-update-1");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-bulk-update-1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    public void close_share_and_delete_asset_bulk_update_2() throws JSONException {
        // test-suite-bulk-update-2
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-bulk-update-2");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-bulk-update-2");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void close_shares_and_delete_assets() throws JSONException {

        //bulk-update-errors-1
        when().a_DELETE_request_is_sent_to_delete_a_share("bulk-update-errors-1");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("bulk-update-errors-1");
        then().response_is_204(when().getStatusCode());
    }
}
