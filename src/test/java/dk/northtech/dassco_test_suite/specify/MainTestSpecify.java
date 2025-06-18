package dk.northtech.dassco_test_suite.specify;

public class MainTestSpecify {

	public static void main(String[] args) {
		System.out.println("Hello DaSSCo Test Suite!");
		
		SpecifyCredentials credentials = new SpecifyCredentials();
		System.out.println(credentials.getSpecifyId());
		SpecifyClient specifyClient = new SpecifyClient(credentials);
		
		var res = specifyClient.get("collectionobject", null).limit(1).execute();

		System.out.println(res.size());

		System.out.println(res);
		
		var response = specifyClient.get("collectionobject", null).filter("catalognumber", "077777777").execute();

		System.out.println(response);

		specifyClient.logout();
	}
}

