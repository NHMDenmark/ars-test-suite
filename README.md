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