import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_getContributorName_Test {

    /*
     * This is a test class for the DataManager.getContributorName method.
     */
    @Test
    public void testSuccessfulCreation() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"new fund\"}";

            }

        });

        String name = dm.getContributorName("12345");

        assertNotNull(name);
        assertEquals("new fund", name);

    }

    @Test
    public void testFailedCreationNotFound() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"not found\"}";
            }

        });

        String name = dm.getContributorName("12345");
        assertNull(name);

    }

    @Test(expected = IllegalStateException.class)
    public void testFailedCreation() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });

        String name = dm.getContributorName("12345");


    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameException() {

        WebClient wc = new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Unknown error");

            }

        };

        DataManager dm = new DataManager(wc);
        String name = dm.getContributorName("12345");

    }

    @Test
    public void testContributorBugs() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if ("/findContributorNameById".equals(resource)) {
                    return "{\"status\":\"success\",\"data\":\"new fund\"}";
                }
                else
                    return null;
            }

        });

        String name = dm.getContributorName("12345");

        assertNotNull(name);
        assertEquals("new fund", name);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetContributorNameNullId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) );

        dm.getContributorName(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameNullResponse() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.getContributorName("id");
    }

    @Test
    public void testGetContributorNameCachePresent() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                    return "{\"status\":\"success\",\"data\":\"ContributorName\"}";
            }
        });

        dm.getContributorName("id1");
        dm.getContributorName("id1");
    }

}
