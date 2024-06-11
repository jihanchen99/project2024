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
                        "            \"amount\": 100,\n" +
                        "            \"date\": \"June 18, 2021\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"__v\": 0\n" +
                        "  }\n" +
                        "}";
            }

        });

        Organization org = dm.attemptLogin(login,password);
        assertNotNull(org);
        assertEquals(1, org.getFunds().size());
        assertEquals("123456", org.getFunds().get(0).getId());

    }

    @Test
    public void testFailedCreation() {
        String login = "login";
        String password = "password";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\n" +
                        "  \"status\": \"fail\",\n" +
                        "}";
            }

        });

        Organization org = dm.attemptLogin(login,password);
        assertNull(org);
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
        Organization org = dm.attemptLogin(login,password);

        //assertNull(org);

    }

    @Test
    public void testDescriptionBugs() {
        String login = "login";
        String password = "password";
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
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
                        "            \"amount\": 100,\n" +
                        "            \"date\": \"June 18, 2021\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"__v\": 0\n" +
                        "  }\n" +
                        "}";
            }


        });
        Organization org = dm.attemptLogin(login, password);
        assertNotNull(org);
        assertEquals(1, org.getFunds().size());
        assertEquals("this is the new fund", org.getDescription());
    }
}
