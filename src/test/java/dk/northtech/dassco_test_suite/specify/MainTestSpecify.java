package dk.northtech.dassco_test_suite.specify;

public class MainTestSpecify {

	public static void main(String[] args) {
		System.out.println("Hello DaSSCo Test Suite!");
		
		SpecifyCredentials credentials = new SpecifyCredentials();
		System.out.println(credentials.getSpecifyId());
		SpecifyClient specifyClient = new SpecifyClient(credentials);
		var res = specifyClient.get("collection", null).limit(1).execute();

		System.out.println(res.size());

		System.out.println(res);

		var response = specifyClient.get("collectionobjectattachment", null).filter("collectionobject_id", "6105988").execute();

		System.out.println(response);

		specifyClient.logout();
	}
}

