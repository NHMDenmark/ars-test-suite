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

		String username = "username";
		String password = "password";
		int collection = 688130;

		String baseUrl = "https://specify-test3.science.ku.dk";
		SpecifyCredentials credentials = new SpecifyCredentials(
				username,
				password,
				collection
		);

		SpecifyClient specifyClient = new SpecifyClient(baseUrl, credentials);

		var res = specifyClient.get("specifyuser").limit(10).execute();

		System.out.println(res.size());

		System.out.println(res);

		specifyClient.logout();

	}
}