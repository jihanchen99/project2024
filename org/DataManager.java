import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class DataManager {

    private final WebClient client;

    private final Map<String, String> contributorNameCache;
    private final Map<List<String>, Organization> organizationCache;


    public DataManager(WebClient client) {
        if (client == null) {
            throw new IllegalStateException();
        }
        this.client = client;
        this.contributorNameCache = new HashMap<>();
        this.organizationCache = new HashMap<>();
    }

    /**
     * Attempt to log the user into an Organization account using the login and password.
     * This method uses the /findOrgByLoginAndPassword endpoint in the API
     *
     * @return an Organization object if successful; null if unsuccessful
     */
    public Organization attemptLogin(String login, String password) {
        if (login == null || password == null) {
            throw new IllegalArgumentException();
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            if (organizationCache.containsKey(List.of(login, password))) {
                return organizationCache.get(List.of(login, password));
            }

            String response = client.makeRequest("/findOrgByLoginAndPassword", map);

//            if (response == "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}") {
//                throw new IllegalStateException();
//            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");


            if (status.equals("success")) {
                JSONObject data = (JSONObject) json.get("data");
                String fundId = (String) data.get("_id");
                String name = (String) data.get("name");
                String description = (String) data.get("description");
                Organization org = new Organization(fundId, name, description);

                JSONArray funds = (JSONArray) data.get("funds");
                Iterator it = funds.iterator();
                while (it.hasNext()) {
                    JSONObject fund = (JSONObject) it.next();
                    fundId = (String) fund.get("_id");
                    name = (String) fund.get("name");
                    description = (String) fund.get("description");
                    long target = (Long) fund.get("target");

                    Fund newFund = new Fund(fundId, name, description, target);

                    JSONArray donations = (JSONArray) fund.get("donations");
                    List<Donation> donationList = new LinkedList<>();
                    Iterator it2 = donations.iterator();
                    while (it2.hasNext()) {
                        JSONObject donation = (JSONObject) it2.next();
                        String contributorId = (String) donation.get("contributor");
                        String contributorName = this.getContributorName(contributorId);
                        long amount = (Long) donation.get("amount");
                        String date = (String) donation.get("date");
                        donationList.add(new Donation(fundId, contributorName, amount, date));
                    }

                    newFund.setDonations(donationList);

                    org.addFund(newFund);

                }
                organizationCache.put(List.of(login, password), org);
                return org;
            } else if (status.equals("error")) {
                throw new IllegalStateException();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    /**
     * Look up the name of the contributor with the specified ID.
     * This method uses the /findContributorNameById endpoint in the API.
     *
     * @return the name of the contributor on success; null if no contributor is found
     */
    public String getContributorName(String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }


        try {

            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            if (contributorNameCache.containsKey(id)) {
                return contributorNameCache.get(id);
            }

            String response = client.makeRequest("/findContributorNameById", map);
//            if (response == null) {
//                throw new IllegalStateException();
//            } else if (response == "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}") {
//                throw new IllegalStateException();
//            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                String name = (String) json.get("data");
                contributorNameCache.put(id, name);
                return name;
            } else if(status.equals("error")){
                throw new IllegalStateException();
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    /**
     * This method creates a new fund in the database using the /createFund endpoint in the API
     *
     * @return a new Fund object if successful; null if unsuccessful
     */
    public Fund createFund(String orgId, String name, String description, long target) {
        if (orgId == null || name == null || description == null) {
            throw new IllegalArgumentException();
        }

        try {

            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);
            map.put("name", name);
            map.put("description", description);
            map.put("target", target);
            String response = client.makeRequest("/createFund", map);
//			if (response == null
//					|| response.equals("{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}")
//					|| response.equals("I AM NOT JSON!")) {
//				throw new IllegalStateException();
//			}

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                JSONObject fund = (JSONObject) json.get("data");
                String fundId = (String) fund.get("_id");
                return new Fund(fundId, name, description, target);
            } else if (status.equals("error")) {
                throw new IllegalStateException();
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    // Task 2.7
    public boolean deleteFund(String fundID) {
        if (fundID == null) {
            throw new IllegalArgumentException();
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", fundID);
            String response = client.makeRequest("/deleteFund", map);
//            if (response == null
//                    || response.equals("{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}")
//                    || response.equals("I AM NOT JSON!")) {
//                throw new IllegalStateException();
//            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");
            return status.equals("success");
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
}
