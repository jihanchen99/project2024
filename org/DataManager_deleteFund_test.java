import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_deleteFund_test {
    @Test
    public void testDeleteFund_Success() {
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
    public void testDeleteFund_Error() {
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
    public void testDeleteFund_NullId() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.deleteFund(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFund_CommunicationError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Communication error");
            }
        });
        dm.deleteFund("testFundId");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFund_UnexpectedResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "I AM NOT JSON!";
            }
        });
        dm.deleteFund("testFundId");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFund_NullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.deleteFund("testFundId");
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteFund_UnexpectedStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"unknown\"}";
            }
        });
        dm.deleteFund("testFundId");
    }
}
