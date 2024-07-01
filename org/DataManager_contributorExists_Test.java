import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DataManager_contributorExists_Test {
    @Test
    public void success() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String endpoint, Map<String, Object> params) {
                return "{\"status\":\"success\"}";
            }
        });
        boolean result = dm.contributorExists("contributorID");
        assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void nullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.contributorExists("contributorID");
    }

    @Test(expected = IllegalStateException.class)
    public void fail() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Communication error");
            }
        });
        dm.contributorExists("contributorID");
    }
}
