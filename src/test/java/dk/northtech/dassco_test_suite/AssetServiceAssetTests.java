package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

//@SpringBootTest
public class AssetServiceAssetTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(AssetServiceAssetTests.class);

    @Value("${test-asset}")
    private String mainAsset;

    @Test
    public void get_assets(){
        logger.info("Getting assets");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_assets();
        then().response_is_200(when().getStatusCode());
    }

    @Test
    public void failed_get_asset_status(){
        logger.info("Getting asset status for empty asset to trigger a 404 response");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_an_assets_status("");
        then().response_is_404(when().getStatusCode());
    }

    @Test
    public void get_asset_status(){
        logger.info("Getting asset status");
        given().dassco_asset_service_server_is_up();
        when().a_GET_request_is_sent_to_get_an_assets_status(mainAsset);
        then().response_is_200(when().getStatusCode());
    }

}
