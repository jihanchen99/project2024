import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DataManager_updateOrganizationInfo_Test {
    @Test(expected = IllegalArgumentException.class)
    public void nullOrgID() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateOrganizationInfo(null, "name", "des");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullName() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateOrganizationInfo("orgID", null, "des");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDescription() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.updateOrganizationInfo("orgID", "name", null);
    }

    @Test(expected = IllegalStateException.class)
    public void nullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.updateOrganizationInfo("orgID", "name", "des");
    }

    @Test
    public void updateSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });
        boolean result = dm.updateOrganizationInfo("orgID", "name", "des");
        assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void updateFail() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Communication error");
            }
        });
        boolean result = dm.updateOrganizationInfo("orgID", "name", "des");
    }
}
