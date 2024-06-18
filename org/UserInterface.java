import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserInterface {


    private final DataManager dataManager;
    private Organization org;
    private final Scanner in = new Scanner(System.in);

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    private void displayOrganization() {
        // display info of org
        if (org.getFunds().size() > 0) {
            System.out.println("There are " + org.getFunds().size() + " funds in this organization:");

            int count = 1;
            for (Fund f : org.getFunds()) {

                System.out.println(count + ": " + f.getName());

                count++;
            }
            System.out.println("Enter the fund number to see more information.");
        }
    }

    // user's operation
    public void start() {

        while (true) {
            while (org == null) {
                loginUI();
            }

            System.out.println("\n\n");

            displayOrganization();

            // print options
            System.out.println("Enter 0 to create a new fund");
            // task 2.9
            System.out.println("Enter 'a' to view all contributions across all funds.");
            System.out.println("Enter 'q' or 'quit' to exit");
            System.out.println("Enter 'logout' to log out");


            String input = in.nextLine();
            if (input.equals("q") || input.equals("quit")) {
                System.out.println("Good bye!");
                break;
            } else if (input.equalsIgnoreCase("a")) {
                displayAllContributions();
                continue;
            } else if (input.equals("logout")) {
                org = null;
                continue;
            }

            // display after log in
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
                    // task 2.2: do something here?
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

    public void displayFund(int fundNumber) {
        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());

        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());

        System.out.println("Choose display option: (1) Individual Donations (2) Aggregated Donations (3) Delete This Fund");
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
        } else if (displayOption == 3) {
            System.out.println("Are you sure you want to delete this fund? Yes/No: ");
            String confirmation = in.nextLine();
            if (confirmation.equalsIgnoreCase("yes")) {
                deleteFund(fundNumber);
            } else {
                System.out.println("Deletion of fund cancelled.");
            }

        }

        double percentageOfTarget = (double) totalDonations / fund.getTarget() * 100;
        System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonations, percentageOfTarget);

        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
    }

    public void deleteFund(int fundNumber) {
        Fund fundToDelete = org.getFunds().get(fundNumber - 1);
        boolean success = dataManager.deleteFund(fundToDelete.getId());
        if (success) {
            org.getFunds().remove(fundToDelete);
            System.out.println("Fund deleted successfully!");
        } else {
            System.out.println("Failed to delete the fund.");
        }
    }

    private void loginUI() {
        System.out.println("enter login");
        String login = in.nextLine();
        System.out.println("enter password");
        String password = in.nextLine();
        Organization newOrg = attemptLogIn(login, password, dataManager);
        if (newOrg != null) {
            org = newOrg;
        }
    }

    private static Organization attemptLogIn(String login, String password, DataManager ds) {

        Organization org = null;
        try {
            org = ds.attemptLogin(login, password);
            // Task 2.2: do sth here?
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server");
        }

        if (org == null) {
            // failed login
            System.out.println("Login failed.");
        }
        return org;
    }

    public static void main(String[] args) {
        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        String login = args[0];
        String password = args[1];
        Organization org = attemptLogIn(login, password, ds);

        UserInterface ui = new UserInterface(ds, org);
        ui.start();
    }

}
