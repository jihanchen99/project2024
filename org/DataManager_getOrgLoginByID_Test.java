import org.junit.Test;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class DataManager_getOrgLoginByID_Test {
    @Test
    public void getOrgLoginByIDSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String endpoint, Map<String, Object> params) {
                return "{\"status\":\"success\",\"data\":\"loginData\"}";
            }
        });
        String expected = "loginData";
        String result = dm.getOrgLoginById("orgID");
        assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOrgID() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.getOrgLoginById(null);
    }

    @Test(expected = IllegalStateException.class)
    public void nullResponse(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        dm.getOrgLoginById("orgID");
    }

    @Test
    public void statusUnsuccessful() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String endpoint, Map<String, Object> params) {
                return "{\"status\":\"error\",\"data\":\"loginData\"}";
            }
        });
        String login = dm.getOrgLoginById("orgID");
        assertEquals(null, login);
    }

}
