import java.time.ZonedDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserInterface {


    private final DataManager dataManager;
    private final Organization org;
    private final Scanner in = new Scanner(System.in);

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public void start() {

        while (true) {
            System.out.println("\n\n");
            if (org.getFunds().size() > 0) {
                System.out.println("There are " + org.getFunds().size() + " funds in this organization:");

                int count = 1;
                for (Fund f : org.getFunds()) {

                    System.out.println(count + ": " + f.getName());

                    count++;
                }
                System.out.println("Enter the fund number to see more information.");
            }
            System.out.println("Enter 0 to create a new fund");
            // task 2.9
            System.out.println("Enter 'a' to view all contributions across all funds.");
            System.out.println("Enter 'q' or 'quit' to exit");
            

            String input = in.nextLine();
            if (input.equals("q") || input.equals("quit")) {
                System.out.println("Good bye!");
                break;
            } else if (input.equalsIgnoreCase("a")) {
                displayAllContributions();
                continue;
            }

            try {
                int option = Integer.parseInt(input);
                if (option < 0 || option > org.getFunds().size()) {
                    System.out.println("The fund number is out of range.");
                } else if (option == 0) {
                    createFund();
                } else {
                    displayFund(option);
                }
            } catch (NumberFormatException e) {
                System.out.println("Input must be a numeric value.");
            }
        }

    }

    
    
    public void createFund() {
        while (true) {
            System.out.print("Enter the fund name: ");
            String name = in.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Fund name cannot be empty. Please reenter.");
                continue;
            }

            System.out.print("Enter the fund description: ");
            String description = in.nextLine().trim();
            if (description.isEmpty()) {
                System.out.println("Fund description cannot be empty. Please reenter: ");
                continue;
            }

            while (true) {
                System.out.print("Enter the fund target: ");
                String input = in.nextLine();
                try {
                    long target = Long.parseLong(input);
                    if (target < 0) {
                        System.out.println("Fund target cannot be negative. Please reenter: ");
                        continue;
                    }

                    Fund fund = dataManager.createFund(org.getId(), name, description, target);
                    org.getFunds().add(fund);
                    System.out.println("Fund created successfully!");
                    return;
                } catch (NumberFormatException e) {
                    System.out.println("Fund target must be numeric. Please reenter: ");
                }
            }
        }
    }

    private void waitForEnter() {
        System.out.println("Press the Enter key to go back to the listing of funds.");
        in.nextLine();  
    }
    private void displayAllContributions() {
        System.out.println("\nAll Contributions Across All Funds:");

        List<Donation> allDonations = new ArrayList<>();
        for (Fund fund : org.getFunds()) {
            allDonations.addAll(fund.getDonations());
        }

        // Sort
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        allDonations.sort((d1, d2) -> {
            ZonedDateTime date1 = ZonedDateTime.parse(d1.getDate(), inputFormatter);
            ZonedDateTime date2 = ZonedDateTime.parse(d2.getDate(), inputFormatter);
            return date2.compareTo(date1);
        });
        for (Donation donation : allDonations) {
            ZonedDateTime parsedDate = ZonedDateTime.parse(donation.getDate(), inputFormatter);
            String formattedDate = parsedDate.format(outputFormatter);
            System.out.println("Fund: " + donation.getFundId() + ", Amount: $" + donation.getAmount() + ", Date: " + formattedDate);
        }
        waitForEnter();
    }
    

    
//    // task 2.3
//    private void calculateAggregates(Fund fund) {
//        if (!fund.getAggregatedDonations().isEmpty()) return; // Already calculated
//
//        List<Donation> donations = fund.getDonations();
//        Map<String, AggregateDonation> aggregated = new HashMap<>();
//
//        for (Donation donation :2 donations) {
//            AggregateDonation agg = aggregated.getOrDefault(donation.getContributorName(),
//                new AggregateDonation(0, 0));
//            agg.incrementCount();
//            agg.addToTotal(donation.getAmount());
//            aggregated.put(donation.getContributorName(), agg);
//        }
//
//        fund.getAggregatedDonations().putAll(aggregated);
//    }
//    
    
  /*
	// task 4+10
	public void displayFund(int fundNumber) {
		Fund fund = org.getFunds().get(fundNumber - 1);
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
	
		long totalDonations = 0;
		//task 10
		DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		
		// task 4
		for (Donation donation : donations) {
			totalDonations += donation.getAmount();
			ZonedDateTime parsedDate = ZonedDateTime.parse(donation.getDate(), inputFormatter);
			String formattedDate = parsedDate.format(outputFormatter);
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + formattedDate);
		}
	
		double percentageOfTarget = (double) totalDonations / fund.getTarget() * 100;
		System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonations, percentageOfTarget);
	
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}

    */
    
    public void displayFund(int fundNumber) {
        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());

        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());

        System.out.println("Choose display option: (1) Individual Donations (2) Aggregated Donations");
        int displayOption = in.nextInt();
        in.nextLine();

        long totalDonations = 0;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        if (displayOption == 1) {
            // original code
    		for (Donation donation : donations) {
    			totalDonations += donation.getAmount();
    			ZonedDateTime parsedDate = ZonedDateTime.parse(donation.getDate(), inputFormatter);
    			String formattedDate = parsedDate.format(outputFormatter);
    			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + formattedDate);
    		}
        } else if (displayOption == 2) {
            Map<String, AggregateDonation> aggregates = fund.getAggregatedDonations();

            // Create a stream and sort in descending order
            Stream<Map.Entry<String, AggregateDonation>> sortedAggregatesStream = aggregates.entrySet().stream()
                .sorted(Map.Entry.<String, AggregateDonation>comparingByValue((a, b) -> Long.compare(b.getTotal(), a.getTotal())));

            List<Map.Entry<String, AggregateDonation>> sortedAggregates = sortedAggregatesStream.collect(Collectors.toList());

            for (Map.Entry<String, AggregateDonation> entry : sortedAggregates) {
                System.out.println(entry.getKey() + ", " + entry.getValue().getCount() + " donations, $" + entry.getValue().getTotal() + " total");
                totalDonations += entry.getValue().getTotal();
            }
        }

        double percentageOfTarget = (double) totalDonations / fund.getTarget() * 100;
        System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonations, percentageOfTarget);

        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
    }
    
    
    public static void main(String[] args) {

        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        String login = args[0];
        String password = args[1];
        Organization org = null;
        try {
            org = ds.attemptLogin(login, password);
        }catch (IllegalStateException e){
            System.out.println("Error in communicating with server");
        }
        if (org == null) {
            System.out.println("Login failed.");
        } else {

            UserInterface ui = new UserInterface(ds, org);

            ui.start();

        }
    }

}
