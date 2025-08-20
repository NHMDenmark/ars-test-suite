package dk.northtech.dassco_test_suite;

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

    private String bridge_asset_guid = this.specifyBridge.getAsset_guid();
    private String bridge_institution = this.specifyBridge.getInstitution();
    private String bridge_collection = this.specifyBridge.getCollection();
    private String mso_asset_guid = this.msoMetadata.getAsset_guid();
    private String mso_institution = this.msoMetadata.getInstitution();
    private String mso_collection = this.msoMetadata.getCollection();

    @Test
    @Order(0)
    public void check_specimens_is_in_specify() throws JSONException, JsonProcessingException {
        logger.info("Checking specimens exist in specify.");
        when().specimen_is_in_specify(this.collection_object_id);
        then().response_is_true();
        when().specimen_is_in_specify(this.first_mso_collection_object_id);
        then().response_is_true();        
        when().specimen_is_in_specify(this.second_mso_collection_object_id);
        then().response_is_true();
    }
    
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#specifyBridgeAssetAlreadyExists") // TODO 
    @Test
    @Order(1)
    public void create_bridge_metadata_from_model() throws JSONException, JsonProcessingException{
        // create bridge model asset, including uploading file and syncing with erda.
        logger.info("Creating specify bridge asset from model.");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(this.specifyBridge);
        then().response_is_200(when().getStatusCode()).and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        logger.info("Adding file.");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file_to_NHMD_Vascular_Plants("cat.png", "129932955", this.bridge_asset_guid, 1);
        then().response_is_200(when().getStatusCode());
        logger.info("Syncing with ERDA for bridge asset.");
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.bridge_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.bridge_asset_guid).getInternalStatus());         
        }

    @Test
    @Order(2)
    public void open_bridge_share_after_erda_sync() throws JSONException, JsonProcessingException{
        logger.info("Reopen bridge share.");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_open_a_share(this.bridge_asset_guid, this.bridge_institution, this.bridge_collection);
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
    }
    
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#modelBridgeAssetNotExists") // TODO
    @Test
    @Order(3)
    public void update_specify_bridge_model_and_check_values() throws JSONException, JsonProcessingException{
        logger.info("Updating specify bridge model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.updateBridgeString, this.bridge_asset_guid);
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking bridge status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.bridge_asset_guid);
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.bridge_asset_guid).getInternalStatus());
    }

    @Test
    @Order(4)
    public void check_attachment_data_match() throws JSONException, JsonProcessingException {
        logger.info("Checking data match for relevant fields between specify attachent and model data.");
        // // logger.info(this.collection_object_id + " :: " + this.updateBridge);
        given().dassco_asset_service_server_is_up();
        when().get_and_compare_specify_data_with_model_data(this.collection_object_id, this.updateBridge);
        then().response_is_true();
    }

    @Test
    @Order(5)
    public void update_bridge_model_second_time() throws JSONException, JsonProcessingException{
        logger.info("Updating specify bridge model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.updateSecondBridgeString, this.bridge_asset_guid);
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking asset status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.bridge_asset_guid);
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.bridge_asset_guid).getInternalStatus());
    }

    @Test
    @Order(6)
    public void check_updated_data_match() throws JSONException, JsonProcessingException {
        logger.info("Checking data match for updated fields between specify attachment and model data.");
        when().get_and_compare_specify_updated_data_with_model_data(this.collection_object_id, this.updateSecondBridge);
        then().response_is_true();
    }
    
    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#msoAssetAlreadyExists") // TODO
    @Test
    @Order(7) 
    public void create_mso_metadata_from_model() throws JSONException, JsonProcessingException{
        // create mso model asset, including uploading file and syncing with erda.
        logger.info("Creating mso asset from model.");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_based_on_model_data_to_create_an_assets_metadata(this.msoMetadata);
        then().response_is_200(when().getStatusCode()).and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        logger.info("Adding file.");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file_to_NHMD_Vascular_Plants("cat.png", "129932955", this.mso_asset_guid, 1);
        then().response_is_200(when().getStatusCode());
        logger.info("Syncing with ERDA for mso asset.");
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.mso_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.mso_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.mso_asset_guid).getInternalStatus());    
    }

    @Test
    @Order(8)
    public void open_mso_share_after_erda_sync() throws JSONException, JsonProcessingException{
        logger.info("Reopen mso share.");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_open_a_share(this.mso_asset_guid, this.mso_institution, this.mso_collection);
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());
    }

    // @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#msoAssetNotExists") // TODO 
    @Test
    @Order(9)
    public void update_mso_model_and_check_values() throws JSONException, JsonProcessingException{
        logger.info("Updating mso model with new values.");
        given().dassco_asset_service_server_is_up();
        when().update_asset_from_model(metaMapper.msoUpdateString, this.mso_asset_guid);
        then().response_is_200(when().getStatusCode());
        logger.info("Waiting and checking mso status changing to synced with specify.");
        given().dassco_file_proxy_server_is_up();
        when().waiting_to_sync_with_specify(this.mso_asset_guid);
        then().asset_status_is_specify_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.mso_asset_guid).getInternalStatus());
    }

    @Test
    @Order(10)
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

    // specify api is bugged, we are detaching the collectionobjectattachment from the collection object, 2/7-25 https://discourse.specifysoftware.org/t/deleting-collectionobjectattachment-via-api-endpoints/2670/2
    @Test
    @Order(Integer.MAX_VALUE - 7)
    public void delete_specify_attachments() throws JSONException, JsonProcessingException{
        logger.info("Delete the bridge attachment from specify.");       
        when().deleting_from_specify_by_deattaching(this.collection_object_id);
        then().response_is_true();
    }
    
    @Test
    @Order(Integer.MAX_VALUE - 6)
    public void delete_mso_attachments() throws JSONException, JsonProcessingException{
        logger.info("Delete the mso attachments from specify.");       
        when().deleting_from_specify_by_deattaching(this.first_mso_collection_object_id);
        then().response_is_true();
        when().deleting_from_specify_by_deattaching(this.second_mso_collection_object_id);
        then().response_is_true();
    }    

    @Test
    @Order(Integer.MAX_VALUE - 5)
    public void unlock_asset(){
        logger.info("Unlock bridge asset in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset(this.bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 4)
    public void unlock_mso_asset(){
        // mso
        logger.info("Unlock mso asset in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_PUT_request_is_sent_to_unlock_an_asset(this.mso_asset_guid);
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 3)
    public void delete_assets_files_and_resync_ERDA() throws JSONException, JsonProcessingException{
        logger.info("Delete bridge asset files and resync with ERDA.");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(this.bridge_asset_guid, this.bridge_institution, this.bridge_collection);
        then().response_is_204(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.bridge_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.bridge_asset_guid).getInternalStatus());
    }
    // mso
    @Test
    @Order(Integer.MAX_VALUE - 2)
    public void delete_mso_assets_files_and_resync_ERDA() throws JSONException, JsonProcessingException{
        logger.info("Delete mso asset files and resync with ERDA.");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset(this.mso_asset_guid, this.mso_institution, this.mso_collection);
        then().response_is_204(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda(this.mso_asset_guid);
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize(this.mso_asset_guid);
        then().asset_status_is_erda_synchronised(when().a_GET_request_is_sent_to_get_an_asset(this.mso_asset_guid).getInternalStatus());
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    public void delete_mso_asset_metadata(){
        logger.info("Delete the mso asset metadata in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata(this.mso_asset_guid);
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void delete_asset_metadata(){
        logger.info("Delete the bridge asset metadata in ARS.");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata(this.bridge_asset_guid);
        then().response_is_204(when().getStatusCode());
    }

}
