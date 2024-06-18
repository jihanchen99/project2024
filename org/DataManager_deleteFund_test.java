import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_deleteFund_test {
    @Test
    public void testDeleteFundSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });
        boolean result = dm.deleteFund("testFundId");
        assertTrue(result);
    }

    @Test
    public void testDeleteFundError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });
        boolean result = dm.deleteFund("testFundId");
        assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFundNullId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.deleteFund(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFundCommunicationError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Communication error");
            }
        });
        dm.deleteFund("testFundId");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFundUnexpectedResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.deleteFund("testFundId");
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.createFund("orgId", "name", "description", 100);
        fail("DataManager.createFund does not throw IllegalStateException when WebClient returns null");
    }

    @Test(expected = IllegalStateException.class)
    public void testResponseError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
            }
        });
        dm.createFund("orgId", "name", "description", 100);
        fail("DataManager.createFund does not throw IllegalStateException when WebClient returns null");
    }
}
