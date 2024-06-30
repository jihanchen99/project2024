import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

            if (response == null) {
                throw new IllegalStateException();
            }

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
            if (response == null) {
                throw new IllegalStateException();
            }

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
			if (response == null) {
				throw new IllegalStateException();
			}

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
            if (response == null) {
                throw new IllegalStateException();
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");
            if (status.equals("success")) {
                return true;
            } else if (status.equals("error")) {
                return false;
            } else {
                throw new IllegalStateException("Unexpected status value: " + status);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    // Task 3.2
    public boolean changePassword(String orgId, String currentPassword, String newPassword) {
        if (orgId == null || currentPassword == null || newPassword == null) {
            throw new IllegalArgumentException();
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);
            map.put("currentPassword", currentPassword);
            map.put("newPassword", newPassword);

            String response = client.makeRequest("/changePassword", map);
            if (response == null) {
                throw new IllegalStateException("No response from server");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            return status.equals("success");

        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    //3.3
    public boolean updateOrganizationInfo(String orgId, String name, String description) {

        if (orgId == null || name == null || description == null) {
            throw new IllegalArgumentException();
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);
            map.put("name", name);
            map.put("description", description);

//            // Print the map for debugging
//            System.out.println("Request payload: " + map);

            String response = client.makeRequest("/updateOrgInfo", map); 
            if (response == null) {
                throw new IllegalStateException();
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            return status.equals("success");

        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    //3.3
    public String getOrgLoginById(String orgId) {
        if (orgId == null) {
            throw new IllegalArgumentException();
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);

            String response = client.makeRequest("/findOrgLoginById", map);
            if (response == null) {
                throw new IllegalStateException();
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                return (String) json.get("data");
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    //3.4
    public boolean contributorExists(String contributorId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", contributorId);

            String response = client.makeRequest("/findContributorNameById", map);
            if (response == null) {
                throw new IllegalStateException("Null response from server");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            return status.equals("success");

        } catch (Exception e) {
            System.out.println("Error in contributorExists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //3.4
    public boolean makeDonation(String fundId, String contributorId, String contributorName, long amount) {
        if (fundId == null || contributorId == null || amount < 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("fund", fundId);
            map.put("contributor", contributorId);
            map.put("amount", amount);

            System.out.println("Making donation request with parameters: " + map);

            String response = client.makeRequest("/makeDonation", map);
            if (response == null) {
                throw new IllegalStateException("Null response from server");
            }

            System.out.println("Response from server: " + response);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                JSONObject donationData = (JSONObject) json.get("data");
                String date = (String) donationData.get("date");

                Donation newDonation = new Donation(fundId, contributorName, amount, date);
                Fund fund = findFundById(fundId);
                if (fund != null) {
                    fund.addDonation(newDonation);
                }

                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error in makeDonation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Fund findFundById(String fundId) {
        for (Organization organization : organizationCache.values()) {
            for (Fund fund : organization.getFunds()) {
                if (fund.getId().equals(fundId)) {
                    return fund;
                }
            }
        }
        return null;
    }
}
