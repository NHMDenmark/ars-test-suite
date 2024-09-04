package dk.northtech.dassco_test_suite;

import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileProxyAssetTests extends BaseTest<GivenState, WhenAction, ThenOutcome> {

    private static final Logger logger = LoggerFactory.getLogger(FileProxyAssetTests.class);

    @Value("${test-asset}")
    private String mainAsset;

    @Test
    public void get_list_of_asset_files_metadata() throws JSONException {
        logger.info("Getting a list of asset files metadata");
        given().dassco_file_proxy_server_is_up();
        when().a_GET_request_is_sent_to_get_list_of_asset_files_metadata(mainAsset);
        then().response_is_200(when().getStatusCode())
                .and().response_has_populated_array(when().getResponseArray());

    }
}
