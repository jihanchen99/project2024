import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataManager_createOrganization_Test {
    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithNullLogin() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization(null, "password", "name", "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithEmptyLogin() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("", "password", "name", "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithNullPassword() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", null, "name", "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithEmptyPassword() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", "", "name", "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithNullName() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", "password", null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithEmptyName() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", "password", "", "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithNullDescription() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", "password", "name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganizationWithEmptyDescription() throws org.json.simple.parser.ParseException {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("login", "password", "name", "");
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateOrganizationNullResponse() throws org.json.simple.parser.ParseException {
        WebClient wc = new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        };

        DataManager dm = new DataManager(wc);
        Organization org = dm.createOrganization("login", "password", "name", "description");
    }

    @Test
    public void testSuccessfulCreation() throws org.json.simple.parser.ParseException {
        WebClient wc = new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                return "{\"status\":\"success\",\"data\":{\"target\":1,\"_id\":\"66820079f068ad0047b1b60c\",\"name\":\"1\",\"description\":\"1\",\"org\":\"6681ffbff068ad0047b1b60a\",\"donations\":[],\"__v\":0}}";

            }

        };
        DataManager dm = new DataManager(wc);
        Organization org = dm.createOrganization("login", "password", "name", "description");


    }

    @Test(expected = IllegalStateException.class)
    public void testFailedCreationBecauseOfExisting() throws org.json.simple.parser.ParseException {
        WebClient wc = new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                return "{\"status\":\"error\",\"data\":{\"driver\":true,\"name\":\"MongoError\",\"index\":0,\"code\":11000,\"keyPattern\":{\"login\":1},\"keyValue\":{\"login\":\"1\"}}}";

            }

        };
        DataManager dm = new DataManager(wc);
        Organization org = dm.createOrganization("login", "password", "name", "description");
    }

    @Test(expected = IllegalStateException.class)
    public void testFailedCreationBecauseOfOtherUnknownError() throws org.json.simple.parser.ParseException {
        WebClient wc = new WebClient("localhost", 3001) {


            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {

                return "{\"status\":\"error\",\"data\":{\"driver\":true,\"name\":\"MongoError\",\"index\":0,\"code\":11001,\"keyPattern\":{\"login\":1},\"keyValue\":{\"login\":\"1\"}}}";

            }

        };
        DataManager dm = new DataManager(wc);
        Organization org = dm.createOrganization("login", "password", "name", "description");
    }
}
