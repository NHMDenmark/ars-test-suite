package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(0)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SetUpTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(SetUpTests.class);

    // Pre-requisite: Have an Institution, Workstation, Pipeline and Collection created:
    @Test
    @Order(0)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institutionAlreadyExists")
    public void create_institution(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("institution", "", "", "test-suite-institution", null, null, null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(1)
    @EnabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#noWorkstationExists")
    public void create_workstation(){
        logger.info("Creating Workstation");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("workstation", "", "", "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(2)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institutionHasTwoWorkstations")
    public void create_workstation_out_of_service(){
        logger.info("Creating Workstation out of service");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_out_of_service_workstation();
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(3)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#pipelineAlreadyExists")
    public void create_pipeline(){
        logger.info("Creating Pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("pipeline", "", "", "test-suite-institution", null, "test-suite-pipeline", null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(4)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#collectionAlreadyExists")
    public void create_collection(){
        logger.info("Creating Collection");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("collection", "", "", "test-suite-institution", "test-suite-collection", null, null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(5)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institution2AlreadyExists")
    public void create_institution_2(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("institution", "test-suite-role-1", "", "test-suite-institution-2", null, null, null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(6)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institution3AlreadyExists")
    public void create_institution_3(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("institution", "", "", "test-suite-institution-3", null, null, null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(7)
    @EnabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#noWorkstationExistsInstitution2")
    public void create_workstation_institution_2(){
        logger.info("Creating Workstation");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("workstation", "", "", "test-suite-institution-2", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation-2");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(8)
    @EnabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#noWorkstationExistsInstitution3")
    public void create_workstation_institution_3(){
        logger.info("Creating Workstation");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("workstation", "", "", "test-suite-institution-3", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation-3");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(9)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#pipelineAlreadyExistsInstitution2")
    public void create_pipeline_institution_2(){
        logger.info("Creating Pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("pipeline", "", "", "test-suite-institution-2", null, "test-suite-pipeline-2", null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(10)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#pipelineAlreadyExistsInstitution3")
    public void create_pipeline_institution_3(){
        logger.info("Creating Pipeline");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("pipeline", "", "", "test-suite-institution-3", null, "test-suite-pipeline-3", null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(11)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#collectionAlreadyExistsInstitution2")
    public void create_collection_institution_2(){
        logger.info("Creating Collection");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("collection", "", "", "test-suite-institution-2", "test-suite-collection-2", null, null);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(12)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#collectionAlreadyExistsInstitution3")
    public void create_collection_institution_3(){
        logger.info("Creating Collection");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("collection", "", "test-suite-role-2", "test-suite-institution-3", "test-suite-collection-3", null, null);
        then().response_is_200(when().getStatusCode());
    }
}
