package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AssetServiceInstitutionTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {
    // GET INSTITUTION BY NAME

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceInstitutionTests.class);

    @Test
    public void get_institution_by_name(){
        logger.info("Getting institution by name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_an_institution("test-suite-institution");
        then().response_is_200(when().getStatusCode());
    }

    // GET INSTITUTION LIST
    @Test
    public void get_institution_list(){
        logger.info("Listing institutions");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_a_list_of_institutions_or_workstations_or_pipelines_or_collections("institution", "");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_create_institution(){
        logger.info("Testing creation of institution with faulty metadata to trigger expected errors:");
        logger.info("Creating institution with empty body:");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_institution("", false);
        then().response_is_400(when().getStatusCode());

        logger.info("Creating institution with body, null name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_institution(null, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Creating institution with body, empty name");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_institution("", true);
        then().response_is_400(when().getStatusCode());

        logger.info("Creating already existing institution");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("institution", "", "", "test-suite-institution", null, null, null);
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_get_institution(){
        logger.info("Testing the returning of an institution with an incorrect name");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_an_institution("incorrect-institution");
        then().response_is_204(when().getStatusCode());
    }
}
