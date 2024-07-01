public class Donation {
	
	private String fundId;
	private String contributorName;
	private long amount;
	private String date;
	private String fundName;

	// keep it for other app
	public Donation(String fundId, String contributorName, long amount, String date) {
		this.fundId = fundId;
		this.contributorName = contributorName;
		this.amount = amount;
		this.date = date;
	}

	// use it in Organization App
	public Donation(String fundId, String fundName, String contributorName, long amount, String date) {
		this.fundId = fundId;
		this.contributorName = contributorName;
		this.amount = amount;
		this.date = date;
		this.fundName = fundName;
	}

	public String getFundId() {
		return fundId;
	}

	public String getContributorName() {
		return contributorName;
	}

	public long getAmount() {
		return amount;
	}

	public String getDate() {
		return date;
	}

	public void setFundName(String fundName) { this.fundName = fundName; }
	public String getFundName() { return this.fundName;  }


}
