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
import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;

@SpringBootTest(classes=dk.northtech.dassco_test_suite.configurations.Configurations.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpecifyTests extends BaseTest<GivenState, WhenAction, ThenOutcome>{
    
    private static final Logger logger = LoggerFactory.getLogger(SpecifyTests.class);

    private final MetadataMapper metaMapper = new MetadataMapper();
    private final Metadata specifyBridge = metaMapper.bridge;
    private final Metadata msoMetadata = metaMapper.mso;
    private final UpdateMetadata updateBridge = metaMapper.updateBridge;
    private final UpdateMetadata updateSecondBridge = metaMapper.updateSecondBridge;
    private final UpdateMetadata msoUpdate = metaMapper.msoUpdate;


    // specimen ids in specify - set for dummy specimens - if a new test specimen is created this needs to be updated here before running these tests
    private final String collection_object_id = "6105988"; // catalogue number 077777777
    private final String first_mso_collection_object_id = "6106005"; // catalogue number 088888888
    private final String second_mso_collection_object_id = "6106006"; // catalogue number 099999999

    @Test
    @Order(0)
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#specifyBridgeAssetAlreadyExists") // TODO 
    public void create_bridge_metadata_from_model() throws JSONException, JsonProcessingException{
        String bridge_asset_guid = this.specifyBridge.getAsset_guid();
        // create bridge model asset, including uploading file and syncing with erda.
        logger.info("Creating specify bridge asset from model.");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(this.specifyBridge);
        then().response_is_200(when().getStatusCode()).and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        logger.info("Adding file.");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file_to_NHMD_Vascular_Plants("cat.png", "129932955", bridge_asset_guid, 1);
        then().response_is_200(when().getStatusCode());
        logger.info("Syncing with ERDA for bridge asset.");
        when().a_POST_request_is_sent_to_synchronize_with_erda(bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(bridge_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(bridge_asset_guid).getInternalStatus());
        logger.info("Synced with ERDA.");        
    }

    @Test
    @Order(1)
    public void check_specimens_is_in_specify() throws JSONException, JsonProcessingException {
        logger.info("Checking specimens exist in specify.");
        when().specimen_is_in_specify(this.collection_object_id);
        then().response_is_true();
        when().specimen_is_in_specify(this.second_collection_object_id);
        then().response_is_true();
    }

    @Test
    @Order(2)
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
    @Order(3)
    public void check_attachment_data_match() throws JSONException, JsonProcessingException {
        logger.info("Checking data match for relevant fields between specify attachent and model data.");
        // // logger.info(this.collection_object_id + " :: " + this.updateBridge);
        given().dassco_asset_service_server_is_up();
        when().get_and_compare_specify_data_with_model_data(this.collection_object_id, this.updateBridge);
        then().response_is_true();
    }

    @Test
    @Order(4)
    public void update_bridge_model_second_time() throws JSONException, JsonProcessingException{
        logger.info("Updating specify bridge model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.updateSecondBridgeString, this.updateSecondBridge.getAsset_guid());
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking asset status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.updateSecondBridge.getAsset_guid());
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.updateSecondBridge.getAsset_guid()).getInternalStatus());
        logger.info("Updates synced with specify.");
    }

    @Test
    @Order(5)
    public void check_updated_data_match() throws JSONException, JsonProcessingException {
        logger.info("Checking data match for updated fields between specify attachment and model data.");
        when().get_and_compare_specify_updated_data_with_model_data(this.collection_object_id, this.updateSecondBridge);
        then().response_is_true();
    }
    
    @Test
    @Order(6)
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#msoAssetAlreadyExists") // TODO 
    public void create_mso_metadata_from_model() throws JSONException, JsonProcessingException{
        String mso_guid = this.msoMetadata.getAsset_guid();
        // create mso model asset, including uploading file and syncing with erda.
        logger.info("Creating mso asset from model.");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(this.msoMetadata);
        then().response_is_200(when().getStatusCode()).and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        logger.info("Adding file.");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file_to_NHMD_Vascular_Plants("cat.png", "129932955", mso_guid, 1);
        then().response_is_200(when().getStatusCode());
        logger.info("Syncing with ERDA for mso asset.");
        when().a_POST_request_is_sent_to_synchronize_with_erda(mso_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(mso_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(mso_guid).getInternalStatus());
        logger.info("Synced with ERDA.");        
    }

    @Test
    @Order(7)
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#msoAssetNotExists") // TODO 
    public void update_mso_model_and_check_values() throws JSONException, JsonProcessingException{
        logger.info("Updating mso model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.msoUpdateString, this.msoUpdate.getAsset_guid());
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking mso status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.msoUpdate.getAsset_guid());
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.msoUpdate.getAsset_guid()).getInternalStatus());
        logger.info("MSO synced with specify.");
    }

    @Test
    @Order(8)
    public void check_mso_attachment_data_match() throws JSONException, JsonProcessingException {
        logger.info("Checking data match for relevant fields between mso attachment and model data.");
        // logger.info(this.collection_object_id + " :: " + this.msoUpdate);
        given().dassco_asset_service_server_is_up();
        when().get_and_compare_specify_data_with_model_data(this.first_mso_collection_object_id, this.msoUpdate);
        then().response_is_true();
        given().dassco_asset_service_server_is_up();
        when().get_and_compare_specify_data_with_model_data(this.second_mso_collection_object_id, this.msoUpdate);
        then().response_is_true();
    }

    // untested - specify api is bugged, will have to manually remove the attachment through the specify ui 2/7-25 https://discourse.specifysoftware.org/t/deleting-collectionobjectattachment-via-api-endpoints/2670/2
    @Test
    @Order(Integer.MAX_VALUE - 4)
    public void delete_specify_attachments() throws JSONException, JsonProcessingException{
        logger.info("Delete the attachments from specify.");
        when().a_DELETE_request_is_sent_to_delete_an_attachment_from_a_speciment(this.collection_object_id);
        then().response_is_true();
        // mso // TODO figure out how this actually work in specify before we can delete
        when().a_DELETE_request_is_sent_to_delete_an_attachment_from_a_speciment(this.first_mso_collection_object_id);
        then().response_is_true();
        when().a_DELETE_request_is_sent_to_delete_an_attachment_from_a_speciment(this.second_mso_collection_object_id);
        then().response_is_true();
    }
     

    @Test
    @Order(Integer.MAX_VALUE - 3)
    public void unlock_assets(){
        logger.info("Unlock assets in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset(this.updateSecondBridge.getAsset_guid());
        then().response_is_204(when().getStatusCode());
        // mso
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset(this.msoUpdate.getAsset_guid());
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 2)
    public void open_shares() throws JSONException, JsonProcessingException{
        logger.info("Reopen shares.");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_open_a_share(this.updateSecondBridge.getAsset_guid(), this.updateSecondBridge.getInstitution(), this.updateSecondBridge.getCollection());
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
        // mso
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_open_a_share(this.msoUpdate.getAsset_guid(), this.msoUpdate.getInstitution(), this.msoUpdate.getCollection());
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());      
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    public void delete_assets_files_and_resync_ERDA() throws JSONException, JsonProcessingException{
        logger.info("Delete files and resync with ERDA.");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(this.updateSecondBridge.getAsset_guid(), this.updateSecondBridge.getInstitution(), this.updateSecondBridge.getCollection());
        then().response_is_204(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.updateSecondBridge.getAsset_guid());
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.updateSecondBridge.getAsset_guid());
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.updateSecondBridge.getAsset_guid()).getInternalStatus());
        // mso
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(this.updateSecondBridge.getAsset_guid(), this.updateSecondBridge.getInstitution(), this.updateSecondBridge.getCollection());
        then().response_is_204(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.updateSecondBridge.getAsset_guid());
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.updateSecondBridge.getAsset_guid());
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.updateSecondBridge.getAsset_guid()).getInternalStatus());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void delete_asset_metadata(){
        logger.info("Delete the asset metadata in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata(this.updateSecondBridge.getAsset_guid());
        then().response_is_204(when().getStatusCode());
    }

}
