package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileProxyShareTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(FileProxyShareTests.class);

    @Value("${test-asset}")
    private String mainAsset;

    @Test
    @Order(0)
    public void open_share() throws JSONException, InterruptedException {
        logger.info("Opening share");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_open_a_share();
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
    }

    @Test
    @Order(1)
    public void change_allocation() throws JSONException, InterruptedException {
        logger.info("Changing allocation");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_change_allocation_of_a_share();
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
    }

    @Test
    @Order(2)
    public void delete_share() throws JSONException {
        logger.info("Closing share");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_share(mainAsset);
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
    }

    @Test
    public void fail_to_change_allocation() throws JSONException {
        logger.info("Trying to change allocation to an asset_guid that does not exist");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_change_allocation_of_a_share(2, "test-suite-asset-to-mess-with-allocations-failed");
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_bad_request(when().getShareHttpAllocationStatus());
    }

    @Test
    public void fail_to_open_share(){
        logger.info("Trying to open share with faulty metadata to trigger expected errors:");
        logger.info("Different asset_guid in param and body:");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_open_a_share(mainAsset, mainAsset + "-2", 10);
        then().response_is_400(when().getStatusCode());

        logger.info("No allocation");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_open_a_share(mainAsset, mainAsset, 0);
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_delete_share(){
        logger.info("Trying to delete a share from an non existent asset:");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_fail_delete_a_share("fake-test-suite-asset");
        then().response_is_404(when().getStatusCode());
    }

    @Test
    public void fail_to_synchronize_to_erda(){
        logger.info("Trying to synchronize with ERDA, sending incorrect data to trigger expected errors:");
        logger.info("Wrong pipeline, asset and workstation");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_synchronize_with_erda("fake-asset", "fake-workstation", "fake-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("Wrong pipeline and workstation");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_synchronize_with_erda(mainAsset, "fake-workstation", "fake-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("Wrong pipeline");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_fail_to_synchronize_with_erda(mainAsset, "test-suite-workstation", "fake-pipeline");
        then().response_is_400(when().getStatusCode());
    }
}
