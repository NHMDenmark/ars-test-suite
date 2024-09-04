package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@SpringBootTest
public class AssetServiceCollectionTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {
    // GET COLLECTION LIST:

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceCollectionTests.class);

    @Test
    public void list_collections(){
        logger.info("Listing collections");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("collection", "test-suite-institution");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_list_collections(){
        logger.info("Failing to get collections:");
        logger.info("Empty institution_name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("collection", "");
        then().response_is_400(when().getStatusCode());

        logger.info("Non existing institution");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("collection", "this-institution-does-not-exist");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_create_collection(){

        logger.info("Failing to create collections:");
        logger.info("No body and no institution name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("", "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Empty body and incorrect institution");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("institution-does-not-exist", "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Empty body, correct institution");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("test-suite-institution", "", false);
        then().response_is_400(when().getStatusCode());

        logger.info("With body, no collection name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("test-suite-institution", "", true);
        then().response_is_400(when().getStatusCode());

        logger.info("With body, incorrect institution");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("institution-does-not-exist", "new-test-collection", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Correct body and existing collection");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("collection", "", "", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation");
        then().response_is_400(when().getStatusCode());

        logger.info("With body, no institution in URL");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_a_collection("", "new-test-collection", true);
        then().response_is_400(when().getStatusCode());
        
    }
}
