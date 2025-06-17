package dk.northtech.dassco_test_suite;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import dk.northtech.dassco_test_suite.metadata_model.Metadata;
import dk.northtech.dassco_test_suite.metadata_model.MetadataMapper;
import dk.northtech.dassco_test_suite.metadata_model.UpdateMetadata;
import dk.northtech.dassco_test_suite.specify.SpecifyClient;
import dk.northtech.dassco_test_suite.specify.SpecifyCredentials;
import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;

@SpringBootTest(classes=dk.northtech.dassco_test_suite.configurations.Configurations.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpecifyTests extends BaseTest<GivenState, WhenAction, ThenOutcome>{
    
    private static final Logger logger = LoggerFactory.getLogger(SpecifyTests.class);

    private final MetadataMapper metaMapper = new MetadataMapper();
    private final Metadata specifyBridge = metaMapper.bridge;
    private final UpdateMetadata updateBridge = metaMapper.updateBridge;

    // specimen id in specify - set for dummy specimen
    private final int collection_object_id = 6105988;
    // collection id in specify - set for "NHMD Vascular Plants"
    private final int collection = 688130;

	private final SpecifyCredentials credentials = new SpecifyCredentials(this.collection);
	private final SpecifyClient specifyClient = new SpecifyClient(this.credentials);

    @Test
    @Order(0)
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#specifyBridgeAssetAlreadyExists") // TODO 
    public void create_bridge_metadata_from_model() throws JSONException, JsonProcessingException{
        String bridge_asset_guid = this.specifyBridge.getAsset_guid();
        // create bridge model asset, including uploading file and syncing with erda.
        logger.info("Creating specify bridge asset from model.");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(specifyBridge);
        then().response_is_200(when().getStatusCode()).and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        logger.info("Adding file and syncing with ERDA for bridge asset.");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", bridge_asset_guid, 1);
        then().response_is_200(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda(bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(bridge_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(bridge_asset_guid). getInternalStatus());
        logger.info("Synced with ERDA.");        
    }

    @Test
    @Order(1)
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#modelBridgeAssetNotExists") // TODO 
    public void update_specify_bridge_model_and_check_values() throws JSONException, JsonProcessingException{
        logger.info("Updating specify bridge model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.updateBridgeString, this.updateBridge.getAsset_guid());
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking bridge status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.updateBridge.getAsset_guid());
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.updateBridge.getAsset_guid()).getInternalStatus());
        logger.info("Synced with specify.");
    }

    @Test
    @Order(2)
    // #TODO add potential disable
    public void get_bridge_metadata_from_specify() throws JSONException, JsonProcessingException {
        logger.info("Getting bridge metadata from specify and comparing with model data.");
        given().specimen_is_in_specify(this.collection_object_id);
        when().get_metadata_from_specify(this.collection_object_id, "guid");
        then().response_is_200(when().getStatusCode());
        when().compare_specify_data(this.metaMapper.updateBridgeString);
        then().response_is_true();
    }
}
