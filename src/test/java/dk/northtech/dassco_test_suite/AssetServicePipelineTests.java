package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AssetServicePipelineTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(AssetServicePipelineTests.class);

    // GET PIPELINE LIST:
    @Test
    public void list_pipelines(){
        logger.info("Listing pipelines");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("pipeline", "test-suite-institution");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_to_list_pipelines(){
        logger.info("Fail to list pipelines to trigger expected errors:");
        logger.info("Empty institution name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("pipeline", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("pipeline", "incorrect-institution");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_create_pipelines(){
        logger.info("Fail to create pipelines to trigger expected errors:");
        logger.info("No institution name, no body");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline("", false, "failing-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution name, no body");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline("incorrect-institution", false, "failing-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution name, no body");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline("test-suite-institution", false, "failing-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution name, body");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline("incorrect-institution", true, "failing-pipeline");
        then().response_is_400(when().getStatusCode());

        logger.info("No pipeline name:");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_pipeline("test-suite-institution", true, "");
        then().response_is_400(when().getStatusCode());

        logger.info("Correct institution name, existing pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("pipeline", "", "", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", null);
        then().response_is_400(when().getStatusCode());
    }
}
