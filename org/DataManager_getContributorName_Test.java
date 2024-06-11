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
    public void testFailedCreation() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"fail\"}";
            }

        });

        String name = dm.getContributorName("12345");

        assertNull(name);

    }

    @Test
    public void testGetContributorNameException() {

        WebClient wc = new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Unknown error");

            }

        };

        DataManager dm = new DataManager(wc);
        String name = dm.getContributorName("12345");
        assertNull(name);

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

}
