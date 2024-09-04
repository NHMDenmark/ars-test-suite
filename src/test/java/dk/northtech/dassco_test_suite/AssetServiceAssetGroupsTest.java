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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AssetServiceAssetGroupsTest extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceAssetGroupsTest.class);

    @Test
    @Order(0)
    public void setup_assets(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions("test-suite-asset-group-1", false, "test-suite-institution", "test-suite-collection", "test-suite-pipeline", "test-suite-workstation");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(1)
    public void setup_asset_2(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions("test-suite-asset-group-2", false, "test-suite-institution-2", "test-suite-collection-2", "test-suite-pipeline-2", "test-suite-workstation-2");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(2)
    public void setup_asset_3(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions("test-suite-asset-group-3", false, "test-suite-institution-2", "test-suite-collection-2", "test-suite-pipeline-2", "test-suite-workstation-2");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(3)
    public void setup_asset_4(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions("test-suite-asset-group-4", false, "test-suite-institution-3", "test-suite-collection-3", "test-suite-pipeline-3", "test-suite-workstation-3");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    @Order(4)
    public void setup_asset_5(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_assets_metadata_different_institutions("test-suite-asset-group-5", false, "test-suite-institution-3", "test-suite-collection-3", "test-suite-pipeline-3", "test-suite-workstation-3");
        then().response_is_200(when().getStatusCode());
    }

    @Test
    // Service user can create a group with restricted access:
    public void test_create_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("test-suite-asset-group-1", assets, false);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("test-suite-asset-group-1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    // Role 1 can create a Group with assets that have role 1:
    public void test_create_asset_group_read_role_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("testCreateAssetGroupReadRole1", assets, false);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1("testCreateAssetGroupReadRole1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_fail_create_asset_group_forbidden(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-4");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("testFailCreateAssetGroupForbidden", assets, false);
        then().response_is_403(when().getStatusCode());
    }

    @Test
    public void test_create_asset_group_shared_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-4");
        when().a_POST_request_is_sent_to_create_an_asset_group("testCreateAssetGroupSharedServiceUser", assets, true);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("testCreateAssetGroupSharedServiceUser");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_create_asset_group_shared_read_role(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("failTestCreateAssetGroupSharedReadRole", assets, true);
        then().response_is_403(when().getStatusCode());
    }

    @Test
    public void test_create_asset_group_shared_write_role(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_write_role_1("testCreateAssetGroupSharedWriteRole", assets);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1("testCreateAssetGroupSharedWriteRole");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_create_asset_group(){
        logger.info("Calling the endpoint with faulty information to trigger expected errors:");

        String groupName = "";
        List<String> assets = new ArrayList<>();
        List<String> hasAccess = new ArrayList<>();

        logger.info("No body:");
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess, false);

        logger.info("No asset group name:");
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess, true);
        then().response_is_400(when().getStatusCode());

        logger.info("No assets:");
        groupName = "error-asset-group";
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess,true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more assets are incorrect:");
        assets.add("test-suite-asset-group-1");
        assets.add("faulty-asset");
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess,true);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset group already exists:");
        groupName = "test-suite-asset-group";
        assets.remove(1);
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess, true);
        then().response_is_200(when().getStatusCode());
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more users cannot be found:");
        groupName = "new-test-suite-asset-group";
        hasAccess.add("test-user-non-existent");
        when().a_POST_request_is_sent_to_fail_the_creation_of_an_asset_group(groupName, assets, hasAccess, true);
        then().response_is_400(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("test-suite-asset-group");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_read_asset_group(){
        logger.info("Failing to read an asset group:");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_read_an_asset_group("non-existent-asset-group");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void fail_delete_asset_group(){
        logger.info("Fail to delete asset group");
        logger.info("Asset group does not exist:");
        given().dassco_asset_service_server_is_up();
        when().a_DELETE_request_is_sent_to_delete_an_asset_group("non-existent-group");
        then().response_is_400(when().getStatusCode());
    }

    @Test
    public void test_add_asset_to_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("test_add_asset_to_asset_group_service_user", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.add("test-suite-asset-group-5");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user("test_add_asset_to_asset_group_service_user", assets, true);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("test_add_asset_to_asset_group_service_user");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_add_asset_to_asset_group_read_role_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("test_add_asset_to_asset_group_read_role_1", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.add("test-suite-asset-group-3");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_read_role_1("test_add_asset_to_asset_group_read_role_1", assets);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1("test_add_asset_to_asset_group_read_role_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_add_asset_to_asset_group_write_role_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_write_role_1("test_add_asset_to_asset_group_write_role_1", assets);
        then().response_is_200(when().getStatusCode());

        assets.add("test-suite-asset-group-3");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_write_role_1("test_add_asset_to_asset_group_write_role_1", assets);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1("test_add_asset_to_asset_group_write_role_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_add_asset_to_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("fail_test_add_asset_to_asset_group_service_user", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.add("test-suite-asset-group-3");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_read_role_1("fail_test_add_asset_to_asset_group_service_user", assets);
        then().response_is_403(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("fail_test_add_asset_to_asset_group_service_user");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_add_assets_to_asset_group(){
        logger.info("Trying to add assets to asset groups with faulty data to trigger expected errors:");
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("fail_test_add_assets_to_asset_group", assets, false);
        then().response_is_200(when().getStatusCode());

        logger.info("No body:");
        assets.add("test-suite-asset-group-5");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user("fail_test_add_assets_to_asset_group", assets, false);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset group does not exist:");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user("fail_test_add_assets_to_asset_group_non_existent", assets, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset group has to have assets!");
        assets = new ArrayList<>();
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user("fail_test_add_assets_to_asset_group", assets, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more assets were not found:");
        assets.add("non-existent-asset-2025");
        when().a_PUT_request_is_sent_to_add_assets_to_asset_group_service_user("fail_test_add_assets_to_asset_group", assets, true);
        then().response_is_400(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("fail_test_add_assets_to_asset_group");
        then().response_is_204(when().getStatusCode());
    }

    // Remove assets: only creator (and service user) can do it.
    @Test
    public void test_remove_assets_from_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-1");
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("test_remove_assets_from_asset_group_service_user", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.remove(0);
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user("test_remove_assets_from_asset_group_service_user", assets, true);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("test_remove_assets_from_asset_group_service_user");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_remove_assets_from_asset_group_read_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        assets.add("test-suite-asset-group-3");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("test_remove_assets_from_asset_group_read_1", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.remove(0);
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_read_role_1("test_remove_assets_from_asset_group_read_1", assets);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1("test_remove_assets_from_asset_group_read_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_remove_assets_from_asset_group_write_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        assets.add("test-suite-asset-group-3");
        when().a_POST_request_is_sent_to_create_an_asset_group_write_role_1("test_remove_assets_from_asset_group_write_1", assets);
        then().response_is_200(when().getStatusCode());

        assets.remove(0);
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_write_role_1("test_remove_assets_from_asset_group_write_1", assets);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1("test_remove_assets_from_asset_group_write_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_remove_assets_from_asset_group_forbidden(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        assets.add("test-suite-asset-group-3");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("fail_test_remove_assets_from_asset_group_forbidden", assets, false);
        then().response_is_200(when().getStatusCode());

        assets.remove(0);
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_write_role_1("fail_test_remove_assets_from_asset_group_forbidden", assets);
        then().response_is_403(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1("fail_test_remove_assets_from_asset_group_forbidden");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_remove_assets_from_asset_group(){
        logger.info("Trying to remove assets from asset group with faulty metadata to trigger expected errors:");
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        assets.add("test-suite-asset-group-1");
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("fail_test_remove_assets_from_asset_group", assets, false);
        then().response_is_200(when().getStatusCode());

        logger.info("Empty body:");
        assets.remove(0);
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user("fail_test_remove_assets_from_asset_group", assets, false);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset Group does not exist:");
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user("fail_test_remove_assets_from_asset_group_non_existent", assets, true);
        then().response_is_400(when().getStatusCode());

        logger.info("Asset group has to have assets:");
        assets = new ArrayList<>();
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user("fail_test_remove_assets_from_asset_group", assets, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more assets were not found:");
        assets.add("test-suite-asset-group-6");
        when().a_PUT_request_is_sent_to_remove_assets_from_asset_group_service_user("fail_test_remove_assets_from_asset_group", assets, true);
        then().response_is_400(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("fail_test_remove_assets_from_asset_group");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_grant_access_to_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("test_grant_access_to_asset_group_service_user", assets, false);
        then().response_is_200(when().getStatusCode());

        users.add("test-suite");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_service_user("test_grant_access_to_asset_group_service_user", users, true);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("test_grant_access_to_asset_group_service_user");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_grant_access_to_asset_group_read_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_read_role_1("fail_test_grant_access_to_asset_group_read_1", assets, false);
        then().response_is_200(when().getStatusCode());

        users.add("test-suite");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_read_role_1("fail_test_grant_access_to_asset_group_read_1", users);
        then().response_is_403(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_read_role_1("fail_test_grant_access_to_asset_group_read_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void test_grant_access_to_asset_group_write_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_write_role_1("test_grant_access_to_asset_group_write_1", assets);
        then().response_is_200(when().getStatusCode());

        users.add("test-suite");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_write_role_1("test_grant_access_to_asset_group_write_1", users);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1("test_grant_access_to_asset_group_write_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_test_grant_access_to_asset_group(){
        logger.info("Trying to grant access to asset group with faulty metadata to trigger expected issues:");
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group("fail_test_grant_access_to_asset_group", assets, false);
        then().response_is_200(when().getStatusCode());

        logger.info("No users:");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_service_user("fail_test_grant_access_to_asset_group", users, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more users were not found:");
        users.add("non-existent-user");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_service_user("fail_test_grant_access_to_asset_group", users, true);
        then().response_is_400(when().getStatusCode());

        users.clear();
        users.add("test-suite");
        logger.info("Asset group does not exist:");
        when().a_PUT_request_is_sent_to_grant_access_to_asset_group_service_user("fail_test_grant_access_to_asset_group_non_existent", users, true);
        then().response_is_400(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("fail_test_grant_access_to_asset_group");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void revoke_access_to_asset_group_service_user(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-4");
        when().a_POST_request_is_sent_to_create_an_asset_group("revoke_access_to_asset_group_service_user", assets, true);
        then().response_is_200(when().getStatusCode());

        users.add("service-account-test-suite-read-role-1");
        when().a_PUT_request_is_sent_to_revoke_access_to_asset_group_service_user("revoke_access_to_asset_group_service_user", users, true);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("revoke_access_to_asset_group_service_user");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void revoke_access_to_asset_group_write_1(){
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-2");
        when().a_POST_request_is_sent_to_create_an_asset_group_write_role_1("revoke_access_to_asset_group_read_1", assets);
        then().response_is_200(when().getStatusCode());

        users.add("test-suite");

        when().a_PUT_request_is_sent_to_revoke_access_to_asset_group_write_role_1("revoke_access_to_asset_group_read_1", users);
        then().response_is_200(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group_write_role_1("revoke_access_to_asset_group_read_1");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    public void fail_revoke_access_to_asset_group(){
        logger.info("Trying to revoke access to asset group with faulty metadata to trigger expected exceptions:");
        given().dassco_asset_service_server_is_up();
        List<String> assets = new ArrayList<>();
        List<String> users = new ArrayList<>();
        assets.add("test-suite-asset-group-4");
        when().a_POST_request_is_sent_to_create_an_asset_group("fail_revoke_access_to_asset_group", assets, true);
        then().response_is_200(when().getStatusCode());

        logger.info("No users:");
        when().a_PUT_request_is_sent_to_revoke_access_to_asset_group_service_user("fail_revoke_access_to_asset_group", users, true);
        then().response_is_400(when().getStatusCode());

        logger.info("One or more users where not found:");
        users.add("test-user-fail");
        when().a_PUT_request_is_sent_to_revoke_access_to_asset_group_service_user("fail_revoke_access_to_asset_group", users, true);
        then().response_is_400(when().getStatusCode());

        users.clear();
        users.add("test-suite");
        logger.info("Asset group does not exist:");
        when().a_PUT_request_is_sent_to_revoke_access_to_asset_group_service_user("fail_revoke_access_to_asset_group_fail", users, true);
        then().response_is_400(when().getStatusCode());

        when().a_DELETE_request_is_sent_to_delete_an_asset_group("fail_revoke_access_to_asset_group");
        then().response_is_204(when().getStatusCode());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void closeSharesAndDeleteAssets() throws JSONException {
        for (int i = 1; i < 6; i ++){
            logger.info("deleting asset : " + i);
            when().a_DELETE_request_is_sent_to_delete_a_share("test-suite-asset-group-" + i);
            then().response_is_200(when().getStatusCode())
                    .and().http_allocation_status_returns_success(when().getShareHttpAllocationStatus());

            when().a_DELETE_request_is_sent_to_delete_an_assets_metadata("test-suite-asset-group-" + i);
            then().response_is_204(when().getStatusCode());
        }
    }
}
