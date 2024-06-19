import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_attemptLogin_Test {
    @Test
    public void testSuccessfulCreation() {
        String login = "login";
        String password = "password";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if ("/findOrgByLoginAndPassword".equals(resource)) {
                    return "{\n" +
                            "  \"status\": \"success\",\n" +
                            "  \"data\": {\n" +
                            "    \"_id\": \"12345\",\n" +
                            "    \"name\": \"new fund\",\n" +
                            "    \"description\": \"this is the new fund\",\n" +
                            "    \"target\": 10000,\n" +
                            "    \"org\": \"5678\",\n" +
                            "    \"funds\": [\n" +
                            "      {\n" +
                            "        \"_id\": \"123456\",\n" +
                            "        \"name\": \"new fund\",\n" +
                            "        \"description\": \"this is the new fund\",\n" +
                            "        \"target\": 10000,\n" +
                            "        \"donations\": [\n" +
                            "          {\n" +
                            "            \"contributorName\": \"donation1\",\n" +
                            "            \"contributor\": \"contributorId\",\n" +
                            "            \"amount\": 100,\n" +
                            "            \"date\": \"June 18, 2021\"\n" +
                            "          }\n" +
                            "        ]\n" +
                            "      }\n" +
                            "    ],\n" +
                            "    \"__v\": 0\n" +
                            "  }\n" +
                            "}";
                } else if ("/findContributorNameById".equals(resource)) {
                    return "{\n" +
                            "  \"status\": \"success\",\n" +
                            "  \"data\": \"Sally\"\n" +
                            "}";

                } else {
                    return null;
                }
            }
        });

        Organization org = dm.attemptLogin(login, password);
        assertNotNull(org);
        assertEquals(1, org.getFunds().size());
        assertEquals("123456", org.getFunds().get(0).getId());
        assertEquals("this is the new fund", org.getDescription());

    }

    @Test
    public void testSuccessfulCreationCachePresent() {
        String login = "login";
        String password = "password";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if ("/findOrgByLoginAndPassword".equals(resource)) {
                    return "{\n" +
                            "  \"status\": \"success\",\n" +
                            "  \"data\": {\n" +
                            "    \"_id\": \"12345\",\n" +
                            "    \"name\": \"new fund\",\n" +
                            "    \"description\": \"this is the new fund\",\n" +
                            "    \"target\": 10000,\n" +
                            "    \"org\": \"5678\",\n" +
                            "    \"funds\": [\n" +
                            "      {\n" +
                            "        \"_id\": \"123456\",\n" +
                            "        \"name\": \"new fund\",\n" +
                            "        \"description\": \"this is the new fund\",\n" +
                            "        \"target\": 10000,\n" +
                            "        \"donations\": [\n" +
                            "          {\n" +
                            "            \"contributorName\": \"donation1\",\n" +
                            "            \"contributor\": \"contributorId\",\n" +
                            "            \"amount\": 100,\n" +
                            "            \"date\": \"June 18, 2021\"\n" +
                            "          }\n" +
                            "        ]\n" +
                            "      }\n" +
                            "    ],\n" +
                            "    \"__v\": 0\n" +
                            "  }\n" +
                            "}";
                } else if ("/findContributorNameById".equals(resource)) {
                    return "{\n" +
                            "  \"status\": \"success\",\n" +
                            "  \"data\": \"Sally\"\n" +
                            "}";

                } else {
                    return null;
                }
            }
        });

        Organization org = dm.attemptLogin(login, password);
        Organization org2 = dm.attemptLogin(login, password);

        //Verify value from cache
        assertNotNull(org2);
        assertEquals(org.getFunds().size(), org2.getFunds().size());
        assertEquals(org.getFunds().get(0).getId(), org2.getFunds().get(0).getId());
        assertEquals(org.getDescription(), org2.getDescription());
    }

    @Test
    public void testFailedCreationLoginFailed() {
        String login = "login";
        String password = "wrongPassword";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\n" +
                        "  \"status\": \"login failed\",\n" +
                        "}";
            }

        });

        Organization org = dm.attemptLogin(login, password);
        assertNull(org);
    }

    @Test(expected = IllegalStateException.class)
    public void testFailedCreation() {
        String login = "login";
        String password = "password";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\n" +
                        "  \"status\": \"error\",\n" +
                        "}";
            }

        });

        Organization org = dm.attemptLogin(login, password);
    }

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginException() {

        String login = "login";
        String password = "password";
        WebClient wc = new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Unknown error");

            }

        };

        DataManager dm = new DataManager(wc);
        Organization org = dm.attemptLogin(login, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAttemptLoginNullLogin() {
        String password = "password";
        WebClient wc = new WebClient("localhost", 3001);
        DataManager dm = new DataManager(wc);
        Organization org = dm.attemptLogin(null, password);
    }



    @Test(expected = IllegalStateException.class)
    public void testNullWebClient() {
        DataManager dm = new DataManager(null);
    }

}
