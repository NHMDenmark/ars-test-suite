package dk.northtech.dassco_test_suite;

import com.tngtech.jgiven.junit5.ScenarioTest;
import dk.northtech.dassco_test_suite.states.GivenState;
import dk.northtech.dassco_test_suite.states.ThenOutcome;
import dk.northtech.dassco_test_suite.states.WhenAction;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseTest<GivenType extends GivenState, WhenType extends WhenAction, ThenType extends ThenOutcome> extends ScenarioTest<GivenType, WhenType, ThenType> {
    // Environmental Variables are passed to GivenState which propagates it to WhenAction and ThenOutcome:
    @Value("${fileproxy.url}")
    String fileProxyUrl;
    @Value("${assetservice.url}")
    String assetServiceUrl;
    @Value("${keycloak.hostname}")
    String keycloakHostname;
    @Value("${assetservice.health}")
    String assetServiceHealth;
    @Value("${client.id}")
    String clientId;
    @Value("${client.secret}")
    String clientSecret;
    @Value("${read.role.1.client.id}")
    String readRole1ClientId;
    @Value("${read.role.1.client.secret}")
    String readRole1ClientSecret;
    @Value("${write.role.1.client.id}")
    String writeRole1ClientId;
    @Value("${write.role.1.client.secret}")
    String writeRole1ClientSecret;
    @Value("${test-asset}")
    private String mainAsset;

    @BeforeEach
    protected void setupScenario(){
        given().setup(fileProxyUrl, assetServiceUrl, assetServiceHealth, keycloakHostname, clientId, clientSecret,
                readRole1ClientId, readRole1ClientSecret, writeRole1ClientId, writeRole1ClientSecret, mainAsset);
    }
}
