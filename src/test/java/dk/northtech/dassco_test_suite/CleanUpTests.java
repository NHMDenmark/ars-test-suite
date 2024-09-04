package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.json.JSONException;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(Integer.MAX_VALUE)
public class CleanUpTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    @Value("${test-asset}")
    private String mainAsset;

    @Test
    void cleanUpAssetParent() throws JSONException {
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata(mainAsset);
        then().response_is_204(when().getStatusCode());
    }

    @Test
    void cleanUpCreatedAndUploaded() throws JSONException {
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-created-and-uploaded");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-created-and-uploaded");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    void cleanUpFilesToBeDeleted() throws JSONException {
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-files-to-be-deleted");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-files-to-be-deleted");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    void cleanUpFilesToDeleteFromList() throws JSONException {
        when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-file-to-delete-from-list");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-file-to-delete-from-list");
        then().response_is_204(when().getStatusCode());
    }
}
