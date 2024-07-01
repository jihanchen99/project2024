import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class DataManager_changePassword_Test {
    @Test(expected = IllegalArgumentException.class)
    public void testNullOrgID() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword(null,"login", "currentP", "newP");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword("orgID",null, "currentP", "newP");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCurrentPassword() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword("orgID","login", null, "newP");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNewPassword() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword("orgID","login", "currentP", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.changePassword("orgID", "login", "currentP", "newP");
    }

    @Test
    public void changePasswordSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });
        boolean result = dm.changePassword("orgID", "login", "currentP", "newP");
        assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void changePasswordFail() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Communication error");
            }
        });
        dm.changePassword("orgID", "login", "currentP", "newP");
    }
}
