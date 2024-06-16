import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Fund {

	private String id;
	private String name;
	private String description;
	private long target;
	private List<Donation> donations;	
	// task 2.3
    private Map<String, AggregateDonation> aggregatedDonations;
    private boolean isAggregatedDirty;  // Flag to check if donations have changed since last aggregation

	
    public Fund(String id, String name, String description, long target) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.target = target;
        this.donations = new LinkedList<>();
    	// task 2.3
        this.aggregatedDonations = new HashMap<>();
        this.isAggregatedDirty = true;  

    }

    
    public Map<String, AggregateDonation> getAggregatedDonations() {
        if (isAggregatedDirty || aggregatedDonations.isEmpty()) {
            aggregateDonations();  
        }
        return aggregatedDonations;
    }

    
    private void aggregateDonations() {
        aggregatedDonations.clear();  
        for (Donation donation : donations) {
            AggregateDonation agg = aggregatedDonations.getOrDefault(donation.getContributorName(), new AggregateDonation(0, 0));
            agg.incrementCount();
            agg.addToTotal(donation.getAmount());
            aggregatedDonations.put(donation.getContributorName(), agg);
        }
        isAggregatedDirty = false;  // Reset the flag as aggregation is done
    }

    public void addDonation(Donation donation) {
        this.donations.add(donation);
        this.isAggregatedDirty = true;  // Mark if donations list is modified
    }

    
    
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getTarget() {
		return target;
	}

	public void setDonations(List<Donation> donations) {
		this.donations = donations;
	}
	
	public List<Donation> getDonations() {
		return donations;
	}
	
	
	
}


// task 2.3

class AggregateDonation {
    private int count;
    private long total;

    public AggregateDonation(int count, long total) {
        this.count = count;
        this.total = total;
    }

    public void incrementCount() {
        this.count++;
    }

    public void addToTotal(long amount) {
        this.total += amount;
    }

    public int getCount() {
        return count;
    }

    public long getTotal() {
        return total;
    }
}
