package dk.northtech.dassco_test_suite;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileProxyAssetFilesTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(FileProxyAssetFilesTests.class);

    @Test
    @Order(0)
    public void create_zip_csv_asset() throws JSONException {
        logger.info("Creating asset for creating .csv and .zip files");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata("test-suite-asset-zip-csv-file", false);
        then().response_is_200(when().getStatusCode())
                .and().http_allocation_status_returns_success(when().getHttpAllocationStatus())
                .and().asset_internal_status_is_metadata_received(when().getInternalStatus());
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", "test-suite-asset-zip-csv-file", 1);
        then().response_is_200(when().getStatusCode());
        when().a_POST_request_is_sent_to_synchronize_with_erda("test-suite-asset-zip-csv-file");
        then().response_is_204(when().getStatusCode());
        when().waiting_for_erda_to_synchronize("test-suite-asset-zip-csv-file");
        then().asset_status_is_completed(when().a_GET_request_is_sent_to_get_an_asset("test-suite-asset-zip-csv-file"). getInternalStatus());
    }

    @Test
    public void delete_files_associated_with_asset() throws JSONException, InterruptedException {
        logger.info("Deleting all files associated to an asset");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", "test-suite-asset-files-to-be-deleted", 1);
        then().response_is_200(when().getStatusCode());
        when().a_GET_request_is_sent_to_get_list_of_asset_files("test-suite-asset-files-to-be-deleted");
        then().response_is_200(when().getStatusCode())
                        .and().response_has_populated_array(when().getResponseArray());

        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset("test-suite-asset-files-to-be-deleted");
        then().response_is_204(when().getStatusCode());
        when().a_GET_request_is_sent_to_get_list_of_asset_files("test-suite-asset-files-to-be-deleted");
        then().response_is_200(when().getStatusCode())
                .and().response_has_empty_array(when().getResponseArray());
    }

    @Test
    public void get_asset_file_by_path(){
        logger.info("Getting an asset file based on path");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_get_a_single_file_from_the_asset();
        then().response_is_200(when().getStatusCode());
    }

    // UPLOAD FILE is used extensively in this class as well as in WorkflowTests, so it is not present here.

    @Test
    public void delete_single_asset_file() throws JSONException, InterruptedException {
        logger.info("Delete a single file from an asset");
        // Upload two assets:
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "129932955", "test-suite-asset-file-to-delete-from-list", 1);
        then().response_is_200(when().getStatusCode());

        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat2.png", "129932955", "test-suite-asset-file-to-delete-from-list", 1);
        then().response_is_200(when().getStatusCode());
        when().a_GET_request_is_sent_to_get_list_of_asset_files("test-suite-asset-file-to-delete-from-list");
        then().response_is_200(when().getStatusCode())
                .and().response_array_has_two_elements(when().getResponseArrayLength());

        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_a_single_file_from_the_asset();
        then().response_is_204(when().getStatusCode());
        when().a_GET_request_is_sent_to_get_list_of_asset_files("test-suite-asset-file-to-delete-from-list");
        then().response_is_200(when().getStatusCode())
                .and().response_array_has_one_element(when().getResponseArrayLength());

        // Clean the files so this test can be repeated:
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset("test-suite-asset-file-to-delete-from-list");
        then().response_is_204(when().getStatusCode());
        when().a_GET_request_is_sent_to_get_list_of_asset_files("test-suite-asset-file-to-delete-from-list");
        then().response_is_200(when().getStatusCode())
                .and().response_has_empty_array(when().getResponseArray());
    }


    @Test
    public void fail_to_get_list_of_asset_files(){
        logger.info("Try to get list of asset files without asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_get_list_of_asset_files("");
        then().response_is_404(when().getStatusCode());
    }

    @Test
    public void fail_to_delete_files_for_an_asset(){
        logger.info("Try to delete files without asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset("");
        then().response_is_404(when().getStatusCode());

        logger.info("Try to delete files with incorrect asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_all_files_for_an_asset("incorrect-guid");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_get_file_by_path(){
        logger.info("Try to get file with incorrect file");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_fail_to_get_a_single_file_from_the_asset("test-suite-institution", "test-suite-collection", "test-suite-asset-created-and-uploaded", "cat2.jpg");
        then().response_is_404(when().getStatusCode());

        logger.info("Try to get file with incorrect asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_fail_to_get_a_single_file_from_the_asset("test-suite-institution", "test-suite-collection", "incorrect-asset", "cat.png");
        then().response_is_404(when().getStatusCode());

        logger.info("Try to get file without asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_fail_to_get_a_single_file_from_the_asset("test-suite-institution", "test-suite-collection", "", "cat.png");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_to_upload_file(){
        logger.info("Try to upload file without specifying a file:");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("", "123", "test-suite-asset-created-and-uploaded", 1);
        then().response_is_405(when().getStatusCode());

        logger.info("Try to upload file without allocation:");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "123", "test-suite-asset-created-and-uploaded", 0);
        then().response_is_400(when().getStatusCode());

        logger.info("Try to upload file without crc:");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat.png", "", "test-suite-asset-created-and-uploaded", 1);
        then().response_is_400(when().getStatusCode());

        logger.info("Try to upload file with incorrect crc:");
        given().dassco_file_proxy_server_is_up();
        when().a_PUT_request_is_sent_to_upload_a_file("cat2.png", "123", "test-suite-asset-created-and-uploaded", 1);
        then().response_is_507(when().getStatusCode());
    }

    @Test
    public void fail_to_delete_single_file(){
        logger.info("Trying to delete file with incorrect file name");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_fail_to_delete_a_single_file_from_the_asset("cat4.jpg", "test-suite-asset-created-and-uploaded");
        then().response_is_404(when().getStatusCode());

        logger.info("Trying to delete file with no asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_fail_to_delete_a_single_file_from_the_asset("cat4.jpg", "");
        then().response_is_400(when().getStatusCode());
    }

    @Order(2)
    @Test
    public void test_create_csv_file() throws JsonProcessingException {
        logger.info("Creating CSV file:");
        given().dassco_file_proxy_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-zip-csv-file");
        when().a_POST_request_is_sent_to_create_a_csv_file(assets);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_test_create_csv_file() throws JsonProcessingException {
        logger.info("Fail to create csv file by passing faulty params:");
        logger.info("Null assets:");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_create_a_csv_file(null);
        then().response_is_500(when().getStatusCode());

        logger.info("Empty assets:");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_create_a_csv_file(new ArrayList<>());
        then().response_is_500(when().getStatusCode());
    }

    @Order(3)
    @Test
    public void test_create_zip_file() throws JsonProcessingException {
        logger.info("Creating ZIP file");
        given().dassco_file_proxy_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-zip-csv-file");
        when().a_POST_request_is_sent_to_create_a_zip_file(assets);
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_test_create_zip_file() throws JsonProcessingException {
        logger.info("Fail to create zip file by passing faulty params:");
        logger.info("Null assets:");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_create_a_zip_file(null);
        then().response_is_500(when().getStatusCode());

        logger.info("Empty assets:");
        given().dassco_file_proxy_server_is_up();
        when().a_POST_request_is_sent_to_create_a_zip_file(new ArrayList<>());
        then().response_is_500(when().getStatusCode());
    }

    @Order(4)
    @Test
    public void test_download_temp_file(){
        logger.info("Test download temp file:");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_get_a_temp_file("assets.zip");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void fail_test_download_zip_file(){
        logger.info("Fail to download zip file by passing faulty params:");
        logger.info("File does not exist");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_get_a_temp_file("non-existent.csv");
        then().response_is_404(when().getStatusCode());
    }

    @Test
    public void fail_delete_local_files(){
        logger.info("Fail to delete local file by passing faulty params:");
        logger.info("Incorrect file");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_local_files("test-suite-institution", "test-suite-collection", "test-suite-asset-zip-csv-file", "incorrect-file.jpeg");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect asset_guid");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_local_files("test-suite-institution", "test-suite-collection", "non-existent", "test-suite-asset-zip-csv-file.zip");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect collection");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_local_files("test-suite-institution", "incorrect-collection", "test-suite-asset-zip-csv-file", "test-suite-asset-zip-csv-file.zip");
        then().response_is_400(when().getStatusCode());

        logger.info("Incorrect institution");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_local_files("incorrect_institution", "test-suite-collection", "test-suite-asset-zip-csv-file", "test-suite-asset-zip-csv-file.zip");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    public void test_delete_local_files() throws JSONException {
        logger.info("Deleting temp folder:");
        given().dassco_file_proxy_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_temp_folder();
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void test_delete_asset(){
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-zip-csv-file");
        then().response_is_204(when().getStatusCode());
    }
}
