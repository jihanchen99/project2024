import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_makeDonation_Test {
    @Test
    public void makeDonationSucceed() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String endpoint, Map<String, Object> params) {
                return "{\"status\":\"success\",\"data\":{\"date\":\"2024-07-01\"}}";
            }
        });
        Fund fund = new Fund("001", "Fund Star", "Mars", 1000);
        String fundId = "001";
        String contributorName = "Jihan";
        long amount = 100;

        Donation result = dm.makeDonation(fund, fundId, contributorName, amount);
        assertNotNull(result);
        assertEquals("2024-07-01", result.getDate());
        assertEquals(fundId, result.getFundId());
        assertEquals(contributorName, result.getContributorName());
        assertEquals(amount, result.getAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFund() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.makeDonation(null, "ID", "name", 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContributorID() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        Fund fund = new Fund("001", "Fund Star", "Mars", 1000);
        dm.makeDonation(fund, null, "name", 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeAmount() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        Fund fund = new Fund("001", "Fund Star", "Mars", 1000);
        dm.makeDonation(fund, "ID", "name", -100);
    }

    @Test(expected = IllegalStateException.class)
    public void nullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        Fund fund = new Fund("001", "Fund Star", "Mars", 1000);
        dm.makeDonation(fund, "ID", "name", 100);
    }

    @Test
    public void statusUnsuccessful() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String endpoint, Map<String, Object> params) {
                return "{\"status\":\"error\"}";
            }
        });
        Fund fund = new Fund("001", "Fund Star", "Mars", 1000);
        Donation result = dm.makeDonation(fund, "ID", "name", 100);
        assertNull(result);
    }
}
