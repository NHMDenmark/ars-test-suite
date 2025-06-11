package dk.northtech.dassco_test_suite.specify;

public class MainTestSpecify {

	public static void main(String[] args) {
		System.out.println("Hello DaSSCo Test Suite!");
		
		int collection = 688130;
		
		SpecifyCredentials credentials = new SpecifyCredentials(collection);
		System.out.println(credentials.getSpecifyId());
		SpecifyClient specifyClient = new SpecifyClient(credentials);
		var res = specifyClient.get("specifyuser").limit(1).execute();

		System.out.println(res.size());

		System.out.println(res);

		specifyClient.logout();
	}
}