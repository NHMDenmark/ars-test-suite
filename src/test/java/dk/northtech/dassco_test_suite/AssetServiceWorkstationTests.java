package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AssetServiceWorkstationTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceWorkstationTests.class);

    // GET WORKSTATION LIST:
    @Test
    public void list_workstations(){
        logger.info("Listing workstations");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("workstation", "test-suite-institution");
        then().response_is_200(when().getStatusCode());
    }

    // UPDATE WORKSTATION:
    // TODO: When bug is fixed in DASSCO, remove the comment-out:
//    @Test
//    public void update_workstation(){
//        System.err.println("Updating workstation status");
//        given().dassco_asset_service_server_is_up()
//                .and().workstation_has_status();
//        when().a_PUT_request_is_sent_to_update_a_workstation(false);
//        then().workstation_status_changed()
//                .and().workstation_is_set_as_IN_SERVICE_again()
//                .and().response_is_204(when().getStatusCode());
//    }

    @Test
    public void fail_to_list_workstations(){
        logger.info("Trying to list workstations with faulty metadata to trigger expected errors:");
        logger.info("No institution name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("workstation", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("workstation", "incorrect-institution");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_create_workstation(){
        logger.info("Trying to create workstations with faulty metadata to trigger expected errors:");
        logger.info("No body, no institution name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("", false, "", "");
        then().response_is_400(when().getStatusCode());

        logger.info("No body, correct institution name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("test-suite-institution", false, "", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution name, correct workstation name, no status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("incorrect-institution", true, "", "valid-workstation-name");
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution name, correct workstation name, incorrect status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("test-suite-institution", true, "TOTALLY_INVALID_STATUS", "valid-workstation-name");
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution name, null workstation name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("test-suite-institution", true, "IN_SERVICE", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution name, correct workstation name, no status");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_workstation("test-suite-institution", true, "", "valid-workstation-name");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_update_workstation(){
        logger.info("Trying to update workstations with faulty metadata to trigger expected errors");
        logger.info("No body, no institution");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_update_workstation("", "", "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("No body, correct institution");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_update_workstation("test-suite-institution", "incorrect-workstation", "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_update_workstation("incorrect-institution", "test-suite-workstation", "IN_SERVICE", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Empty status");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_fail_to_update_workstation("test-suite-institution", "test-suite-workstation", "", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution, correct name, incorrect status");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_update_a_workstation(true);
        then().response_is_400(when().getStatusCode());
    }
}
