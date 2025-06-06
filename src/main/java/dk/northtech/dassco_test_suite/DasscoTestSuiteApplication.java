package dk.northtech.dassco_test_suite;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import specify.SpecifyClient;
import specify.SpecifyCredentials;

@SpringBootApplication
public class DasscoTestSuiteApplication {

	public static void main(String[] args) {
		System.out.println("Hello DaSSCo Test Suite!");

		String baseUrl = "https://specify-test3.science.ku.dk";
		SpecifyCredentials credentials = new SpecifyCredentials(
				"specifybridge",
				"Xf2mHFxDECW3cx48",
				688130
		);

		SpecifyClient specifyClient = new SpecifyClient(baseUrl, credentials);

		var res = specifyClient.get("specifyuser").limit(10).execute();

		System.out.println(res.size());

		System.out.println(res);

		specifyClient.logout();

	}
}