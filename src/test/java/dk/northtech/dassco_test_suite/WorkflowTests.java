package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

	private static final Logger logger = LoggerFactory.getLogger(WorkflowTests.class);

	@Value("${test-asset}")
	private String mainAsset;

	// First Workflow:
	// Create Asset - Upload File - Synchronize with ERDA - ASSET COMPLETE.
	@Test
	@Order(0)
	@DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetAlreadyExists")
	public void asset_complete() throws JSONException {
		logger.info("Workflow #1");
		logger.info("Creating asset");
		// Create Asset:
		given().dassco_asset_service_server_is_up();
		when().a_POST_request_is_sent_to_create_an_assets_metadata(mainAsset, false);
		then().response_is_200(when().getStatusCode())
				.and().http_allocation_status_returns_success(when().getHttpAllocationStatus())
				.and().asset_internal_status_is_metadata_received(when().getInternalStatus());

		logger.info("Uploading file");
		// Upload File:
		given().dassco_asset_service_server_is_up();
		when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", mainAsset, 1);
		then().response_is_200(when().getStatusCode());

		logger.info("Syncronizing with ERDA");
		// Synchronize with ERDA:
		given().dassco_asset_service_server_is_up();
		when().a_POST_request_is_sent_to_synchronize_with_erda(mainAsset);
		then().response_is_204(when().getStatusCode());

		logger.info("Checking sync");
		// Check Sync:
		given().dassco_asset_service_server_is_up();
		when().waiting_for_erda_to_synchronize(mainAsset);
		then().asset_status_is_completed(when().a_GET_request_is_sent_to_get_an_asset(mainAsset).getInternalStatus());
	}

	// Flows #2 to 4:
	// Creating assets that will be used for uploading and deleting files.
	@Test
	@Order(1)
	@DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetCreatedAndUploadedAlreadyExists")
	public void create_and_upload_asset() {

		logger.info("Workflow #2");
		logger.info("Creating asset");
		given().dassco_asset_service_server_is_up();
		when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-created-and-uploaded", false);
		then().response_is_200(when().getStatusCode())
				.and().http_allocation_status_returns_success(when().getHttpAllocationStatus())
				.and().asset_internal_status_is_metadata_received(when().getInternalStatus());

		logger.info("Uploading file");
		// Upload File:
		given().dassco_asset_service_server_is_up();
		when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", "test-suite-asset-created-and-uploaded", 1);
		then().response_is_200(when().getStatusCode());
	}

	@Test
	@Order(2)
	@DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetFilesToBeDeletedAlreadyExists")
	public void create_asset_for_deleting_files() {
		logger.info("Workflow #3");
		logger.info("Creating asset");
		given().dassco_asset_service_server_is_up();
		when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-files-to-be-deleted", false);
		then().response_is_200(when().getStatusCode())
				.and().http_allocation_status_returns_success(when().getHttpAllocationStatus())
				.and().asset_internal_status_is_metadata_received(when().getInternalStatus());
	}

	@Test
	@Order(3)
	@DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#assetFileToDeleteFromListAlreadyExists")
	public void create_asset_for_deleting_only_one_file() {
		logger.info("Workflow #4");
		logger.info("Creating asset");
		given().dassco_asset_service_server_is_up();
		when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-file-to-delete-from-list", false);
		then().response_is_200(when().getStatusCode())
				.and().http_allocation_status_returns_success(when().getHttpAllocationStatus())
				.and().asset_internal_status_is_metadata_received(when().getInternalStatus());
	}


}
