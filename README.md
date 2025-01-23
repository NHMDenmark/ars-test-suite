# Dassco Test Suite
This test suite is made to make calls to all the API endpoints in the DaSSCo Asset Service and DaSSCo File Proxy projects, both for main success scenarios and (some) corner cases. It creates an Institution, a Pipeline, two Workstations, a Collection, and thirteen assets. It also creates two more Institutions with its own Collections, Pipelines and Workstations, as well as 5 new Assets, for the testing of Asset Groups. Files are uploaded, some are deleted, some are synchronized with ERDA, and assets have their statuses or properties changed during the test.

## Running the test suite:
* Use the command ``mvn test``

## About the Workflows:
The test suite starts by creating the Institutions, Pipelines, Collections and Workstations. All assets created during the execution of the test suite are deleted at the end of it. Assets created in the "WorkflowTests" class are deleted in the "CleanUpTests" class. Assets created for Asset Groups, for example, are deleted at the end of the AssetServiceAssetGroupsTest class.

## Asset Groups:
For the Asset Group tests to work, there has to be Keycloak Clients and Realm Roles created.
test-suite-institution-2 is Restricted to Role "test-suite-role-1", while test-suite-collection-3 (part of test-suite-institution-3) is restricted to "test-suite-role-2".
This means that to be able to read or write to these institutions or collection, a User needs to have either the Service User role or the name of the role prefixed with either WRITE_ or READ_ (for example, to be able to read assets from test-suite-institution-2, the user has to have a READ_test-suite-role-1). This has to be prepared beforehand and can be done by accessing the users and changing their Role Mappings in the Keycloak admin page.
The test suite uses service-user, WRITE_test-suite-role-1 and READ_test-suite-role-1.

## Application.properties file:
In application.properties file, the environmental variables are set for fileproxy, assetservice, keycloak, and client id and secret for service user, write role 1 and read role 1. Please set them accordingly.

`fileproxy.url=${FILEPROXY_LOCATION:}` FileProxy URL (can be either local or on the server)

`assetservice.url=${ASSETSERVICE_LOCATION:}` AssetService URL

`assetservice.health=${ASSETSERVICE_HEALTH:}` AssetService Health Endpoint

`keycloak.hostname=${KEYCLOAK_LOCATION:}` Keycloak URL

`spring.config.import=optional:file:./application-local.properties` An Application-Local Properties is needed to manage the clients and their secrets

`test-asset=test-suite-main-asset` The main asset created by the test suite, present in many tests. In Application Properties to allow the user to choose the name they want for it.

## Application-local.properties file:
Has to be located in the root folder.

`client.id=${CLIENT_ID:}`

`client.secret=${CLIENT_SECRET:}`

`read.role.1.client.id=${READ_ROLE_1_CLIENT_ID:}`

`read.role.1.client.secret=${READ_ROLE_1_CLIENT_SECRET:}`

`write.role.1.client.id=${WRITE_ROLE_1_CLIENT_ID:}`

`write.role.1.client.secret=${WRITE_ROLE_1_CLIENT_SECRET:}`

## In case of Test Failure:
The test suite closes shares and deletes asset metadata automatically, but in some cases there might be issues with this.
If a test fails in the cleanup, it is recommended to take a note of the failed assets and close shares /  delete asset metadata automatically, as the Test Suite needs a "clean slate" to run again.

This is the list of Assets created by the Test Suite in case they need to be checked individually:

>test-suite-asset-group-1
> 
>test-suite-asset-group-2
>test-suite-asset-group-3
> 
>test-suite-asset-group-4
> 
>test-suite-asset-group-5
> 
>test-suite-asset-created
> 
>test-suite-asset-updated
> 
>test-suite-asset-deleted
> 
>test-suite-asset-received
> 
>test-suite-asset-completed
> 
>test-suite-asset-audited
> 
>test-suite-asset-status
> 
>test-suite-asset-unlocked
> 
>test-suite-bulk-update-1
> 
>test-suite-bulk-update-2
> 
>bulk-update-errors-1
> 
>test-suite-asset-zip-csv-file
> 
>test-suite-main-asset
> 
>test-suite-asset-created-and-uploaded
>
>test-suite-asset-files-to-be-deleted
>
>test-suite-asset-file-to-delete-from-list

## Deployment
As discussed, we need to be able to run the test suite via Github Actions.
To do this, we need to move the following properties from application-local.properties to application.properties (if it has not been done before):

```
client.id=${CLIENT_ID:clientid}
client.secret=${CLIENT_SECRET:clientsecret}

read.role.1.client.id=${READ_ROLE_1_CLIENT_ID:readrole1clientid}
read.role.1.client.secret=${READ_ROLE_1_CLIENT_SECRET:readrole1clientsecret}

write.role.1.client.id=${WRITE_ROLE_1_CLIENT_ID:writerole1clientid}
write.role.1.client.secret=${WRITE_ROLE_1_CLIENT_SECRET:writerole1clientsecret}
```

For security reasons, the ID's and Passwords are placeholders.
For running the test suite and feeding the parameters, instead of running `mvn test`, we now need to pass the properties:

```
mvn test -Dproperty1=value1 -Dproperty2=value2
```

Spring Boot automatically merges these -D values into the environment and uses them to override matching keys in application.properties.
It would end up looking something like this:
```
mvn test -DCLIENT_ID=actualClientId -DCLIENT_SECRET=actualClientSecret -DREAD_ROLE_1_CLIENT_ID=actualReadRole1ClientId -DREAD_ROLE_1_CLIENT_SECRET=actualReadRole1ClientSecret -DWRITE_ROLE_1_CLIENT_ID=actualWriteRole1ClientId -DWRITE_ROLE_1_CLIENT_SECRET=actualWriteRole1ClientSecret
```

## How the test suite works:

The test suite works with JGiven, so the tests have a Given State, an Action, and an Outcome.

### Set Up Tests:
The set up tests create three institutions ('test-suite-institution', 'test-suite-institution-2' and 'test-suite-institution-3'), four workstations (two in test-suite-institution (one out of service) and one each for test-suite-institution 2 and 3), three pipelines, and three collections.
'test-suite-institution-2' has a Role of 'test-suite-role-1' (making all assets under that institution have that role). 'test-suite-collection-3' has a Role of 'test-suite-role-2', making all Assets under that collection have that role as well. This is relevant for tests regarding roles.
These tests cannot be re-run, as there is no way to delete the institutions, workstations, collections and pipelines once they have been created. For this reason, a check for each test is created in the "Conditions" class.
So, for example, the "Create Institution" test has these Annotations:
``` 
@Test
@Order(0)
@DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institutionAlreadyExists")
```
The test is the first test in the test suite, and checks if the institution already exists.
In the 'Conditions' class, there's a method called 'institutionAlreadyExists' that makes an API call to check on the existence of the Institution. If the institution already exists, returns true, and the Test in SetUpTests is ignored.

### Workflow tests
The workflow tests are tests created to mock a normal workflow for DaSSCo (workflow test 1) or prepare Assets for complex tests that will come later (workflow tests 2 to 4)
For example, workflow test 1 creates an Asset, uploads a file, synchronizes ERDA and expects the asset to change its Internal Status to ASSET COMPLETE. Workflow test 3 creates an Asset that will later have a file uploaded and deleted before synchronizing.

### Cleanup tests
Cleanup tests take care of closing shares and deleting assets so the test suite can be re-run.

### Other tests:
Each section of the FileProxy and AssetService has their own class for tests (for example: Asset Service Asset Groups test -> tests pertaining the Asset Group functionality of the Asset Service, File Proxy Asset Files test -> tests pertaining the Asset Files in the File Proxy).
Future tests should follow this convention to maintain order.

### Given, WhenAction, ThenOutcome
These are JGiven classes. They have methods that check status, make actions, and depending on the result of those actions, give outcomes.
For example:

```
    @Test
    @Order(0)
    @DisabledIf("dk.northtech.dassco_test_suite.conditions.Conditions#institutionAlreadyExists")
    public void create_institution(){
        given().dassco_asset_service_server_is_up();
        when().a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection("institution", "", "", "test-suite-institution", null, null, null);
        then().response_is_200(when().getStatusCode());
    }
```

GivenState: Dassco Asset Service Server is up.

``` 
public GivenState dassco_asset_service_server_is_up(){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceHealth + "/actuator/health"))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            String status = jsonResponse.getString("status");

            if (status.equals("UP")){
                return self();
            } else {
                throw new IllegalStateException("Server is not running or it's unhealthy");
            }
        } catch (Exception e){
            throw new IllegalStateException("Error occurred while checking Server health: " + e.getMessage());
        }
    }
```

If Asset Service is up, further tests can be run. If it's not running, an error message should be displayed, and the rest of the test should fail.

WhenAction: a POST request is sent to create an institution, workstation, pipeline, or collection

```
public WhenAction a_POST_request_is_sent_to_create_an_institution_or_workstation_or_pipeline_or_collection(String entityType, String i_role, String c_role, String i_name, String c_name, String p_name, String w_name){

        getToken();

        request = postRequestBuilder(entityType, i_role, c_role, i_name, c_name, p_name, w_name);

        makeApiCall(request);

        return self();
    }
```

In this specific case, as the methods for creating an institution, pipeline, workstation or collection were pretty similar, a PostRequestBuilder is used, where we pass the type, the institution and collection role, and the name we want to pass.
This then makes an API call, which fails if the returned code is not a success one.

Then: The expected response is 200.

``` 
    public ThenOutcome response_is_200(int statusCode){
        assertEquals("OK. Expected Code: 200", 200, statusCode);
        return self();
    }
```

If anything fails, subsequent tests will fail, messages will be printed, and the test suite will move on to the next tests.

## Creating new tests:
Creating new tests is straightforward, we need a State, an Action to perform, and an expected Outcome.

### GivenState:
If the functionality to be tested belongs to the Asset Service, call ```given().dassco_asset_service_server_is_up();```. If belongs to File Proxy, call ```given().dassco_file_proxy_server_is_up();```.

### WhenAction:
Main test functionality. 
If the functionality to be tested is new, a new WhenAction method needs to be created for it. If a part of the test already exists (create an Asset, update an Asset, change an asset's status, etc, etc) the chance that an Action already exists is big, so a search in the WhenAction class should be useful.

The naming convention for tests is to keep them as colloquial as possible: for example, an action to get an asset is called `a_GET_request_is_sent_to_get_an_asset`, while an action to delete an assets metadata is called `a_DELETE_request_is_sent_to_delete_an_assets_metadata`. The API request is included in the name for extra clarity.

Most WhenAction methods have three parts:
- Getting the Keycloak Token:

`getToken();` for Service Users, `getReadRole1Token()` for Read Role 1 users and `getWriteRole1Token()` for Write Role 1 users. If new users are created, then methods that get the token for those users *must* be created as well.

These methods will make further API calls pass or fail depending on the users permissions.

- Creating the HttpRequest object:

```
            request = HttpRequest.newBuilder()
                .uri(URI.create(assetServiceUrl + "/v1/assetmetadata/test-suite-asset-audited/audit"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
```

Different API request types have different needs:
POST and PUT requests often require a body, GET and DELETE are empty. 
But all of them require the same headers to work.

- Make the API call:

Once the HttpRequest object is built, it's passed to `makeApiCall()`, that takes care of making the call and getting the response.

More work might be required to create the expected functionality of the test.

### ThenOutcome

The expected outcome of the test.
Some tests are expected to work, some are expected to fail. In most of the cases we expect some specific statusCode, so after the WhenAction we would do `then().response_is_200(when().getStatusCode())` and pass the code that we got in the WhenAction. Sometimes, instead of Status Codes, we expect different Http Allocation Status (success, bad request) or internal status of assets, as well as number of elements in an array. What we expect depends on the test we are running.
We currently have ThenOutcome ready for status code 200, 204, 400, 403, 404, 405, 500 and 507.

### Creating new tests: Example 1

Let's say that we have a new endpoint that needs testing. The endpoint is for the Asset Service, under /test-endpoint-1. It's a regular GET request.

We will need to create new methods to accomodate the new test:

Test method:
```
@Test
public void test_new_endpoint_1() {
    given().dassco_asset_service_server_is_up(); // Given: Dassco Asset Service needs to be up.
    
    when().a_GET_request_is_sent_to_test_the_new_endpoint() // When: We need to create this method in the WhenAction class.
    
    then().response_is_200(when().getStatusCode()); // We tell the test that we are expecting a 200 from the endpoint.
}
```

a_GET_request_is_sent_to_test_the_new_endpoint method:
```
public WhenAction a_GET_request_is_sent_to_test_the_new_endpoint_method(){
    getToken(); // To get the Keycloak token. Passing an incorrect token can lead to auth errors.
    
    request = HttpRequest.newBuilder() // create the request
    .uri(URI.create(assetServiceUrl + "/v1/test-endpoint-1) // Pass the URL
    .header("Content-Type, "application/json") // Add the headers
    .header("Authorization", "Bearer " + token)
    .GET() // Type of request
    .build();
    
    makeApiCall(request); // Make the API call. There's a dedicated method that takes a request and makes the API call, saving the response.
    
    return self();
}
```

