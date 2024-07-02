import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public UserInterface(DataManager dataManager) {
        this.dataManager = dataManager;
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
            if (org == null) {
                break;
            }

            System.out.println("\n");

            displayOrganization();

            // print options
            System.out.println("Enter 0 to create a new fund");
            // task 2.9
            System.out.println("Enter 'a' to view all contributions across all funds.");
            System.out.println("Enter 'q' or 'quit' to exit");
            System.out.println("Enter 'logout' to log out");
            //3.3
            System.out.println("Enter 'e' to edit organization information");

            // task 3.2
            System.out.println("Enter 'c' to change password");
            //3.4
            System.out.println("Enter 'd' to make a donation");

            String input = in.nextLine();
            if (input.equals("q") || input.equals("quit")) {
                System.out.println("Good bye!");
                break;
            } else if (input.equalsIgnoreCase("a")) {
                displayAllContributions();
                continue;
            } else if (input.equals("logout")) {
                org = null;
                loginUI();
                continue;
            } else if (input.equalsIgnoreCase("e")) {
                editOrganizationInfo();
                continue;
            } else if (input.equalsIgnoreCase("c")) {
                changePassword();
                continue;
            } else if (input.equalsIgnoreCase("d")) {
                makeDonation();
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


    // 3.4
    private void makeDonation() {
        System.out.println("Enter the fund number to donate to:");
        String fundNumberInput = in.nextLine();
        int fundNumber;

        try {
            fundNumber = Integer.parseInt(fundNumberInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid fund number.");
            return;
        }

        if (fundNumber <= 0 || fundNumber > org.getFunds().size()) {
            System.out.println("Fund number out of range.");
            return;
        }

        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("Enter the contributor ID:");
        String contributorId = in.nextLine().trim();

        if (contributorId.isEmpty()) {
            System.out.println("Contributor ID cannot be empty.");
            return;
        }
        try {
            boolean isContributorExist = dataManager.contributorExists(contributorId);
            if (!isContributorExist) {
                System.out.println("Contributor ID does not exist.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error while finding contributor.");
            return;
        }

        System.out.println("Enter the donation amount:");
        String amountInput = in.nextLine();
        long amount;

        try {
            amount = Long.parseLong(amountInput);
            if (amount < 0) {
                System.out.println("Donation amount cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Donation amount must be a numeric value.");
            return;
        }

        Donation donation = null;
        try {
            String contributorName = dataManager.getContributorName(contributorId);

            donation = dataManager.makeDonation(fund, contributorId, contributorName, amount);

            if (donation != null) {
                System.out.println("Donation successful!");
                displayFund(fundNumber);
            } else {
                System.out.println("Failed to make donation.");

            }
        } catch (Exception e) {
            System.out.println("Error making donation: " + e.getMessage());
        }
    }


    // Task 3.2
    private void changePassword() {
        System.out.println("Enter current password: ");
        String currentPassword = in.nextLine();

        // todo try catch
        String login = null;
        try {
            login = dataManager.getOrgLoginById(org.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid organization ID provided.");
            return;
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server while fetching login information.");
            return;
        }

        // todo try catch
        Organization orgAttempt = null;
        try {
            orgAttempt = dataManager.attemptLogin(login, currentPassword);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid login credentials provided.");
            return;
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server while attempting login.");
            return;
        }

        if (orgAttempt == null) {
            System.out.println("Incorrect password. Returning to main menu.");
            return;
        }

        System.out.println("Enter new password: ");
        String newPassword1 = in.nextLine();
        System.out.println("Re-enter new password: ");
        String newPassword2 = in.nextLine();
        if (!newPassword1.equals(newPassword2)) {
            System.out.println("The two entries do not match. Returning to main menu.");
            return;
        }
        try {
            boolean success = dataManager.changePassword(org.getId(), login, currentPassword, newPassword1);
            if (success) {
                System.out.println("You have successfully changed the password!");
            } else {
                System.out.println("Failed to change the password. Please try again!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Missing or invalid orgID, currentPassword, or newPassword.");
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server.");
        }
    }

    //3.3
    private void editOrganizationInfo() {
        System.out.print("Enter current password: ");
        String password = in.nextLine();

        // todo try catch
        String login = null;
        try {
        	login = dataManager.getOrgLoginById(org.getId());
        }catch (IllegalArgumentException e) {
            System.out.println("Invalid organization ID provided.");
            return;    
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server while fetching login information.");
            return;
            }

        // todo try catch
        
        try {
        if (dataManager.attemptLogin(login, password) == null) {
            System.out.println("Incorrect password. Returning to main menu.");
            return;
        }
        }catch (IllegalArgumentException e) {
            System.out.println("Invalid login credentials provided.");
            return;
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server while attempting login.");
            return;
        }
        
        System.out.println("Current organization name: " + org.getName());
        System.out.println("Current organization description: " + org.getDescription());


        System.out.print("Enter new organization name (leave blank to keep current): ");
        String newName = in.nextLine().trim();
        if (newName.isEmpty()) {
            newName = org.getName();
        }

        System.out.print("Enter new organization description (leave blank to keep current): ");
        String newDescription = in.nextLine().trim();
        if (newDescription.isEmpty()) {
            newDescription = org.getDescription();
        }

        while (true) {
            try {
                boolean success = dataManager.updateOrganizationInfo(org.getId(), newName, newDescription);
                if (success) {
                    org.setName(newName);
                    org.setDescription(newDescription);
                    System.out.println("Organization information updated successfully!");
                    return;
                } else {
                    System.out.println("Failed to update organization information.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Missing or invalid organization ID.");
            } catch (IllegalStateException e) {
                System.out.println("Error in communicating with server.");
            }

            System.out.print("Would you like to retry? (Y/N): ");
            String retry = in.nextLine().trim();
            if (!"Y".equalsIgnoreCase(retry)) {
                return;
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
                    // Task 2.2
                    try {
                        Fund fund = dataManager.createFund(org.getId(), name, description, target);
                        org.getFunds().add(fund);
                        System.out.println("Fund created successfully!");
                        return;
                    } catch (Exception e) {
                        System.out.println("Error creating fund: " + e.getMessage());
                        System.out.print("Would you like to retry? (Y/N): ");
                        String retry = in.nextLine().trim();
                        if (!"Y".equalsIgnoreCase(retry)) {
                            return;
                        }
                    }
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
            System.out.println("Fund: " + donation.getFundName() + ", Amount: $" + donation.getAmount() + ", Date: " + formattedDate);
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
                    .sorted(Map.Entry.comparingByValue((a, b) -> Long.compare(b.getTotal(), a.getTotal())));

            List<Map.Entry<String, AggregateDonation>> sortedAggregates = sortedAggregatesStream.collect(Collectors.toList());

            for (Map.Entry<String, AggregateDonation> entry : sortedAggregates) {
                System.out.println(entry.getKey() + ", " + entry.getValue().getCount() + " donations, $" + entry.getValue().getTotal() + " total");
                totalDonations += entry.getValue().getTotal();
            }
        } else if (displayOption == 3) {
            System.out.println("Are you sure you want to delete this fund? Y/N: ");
            String confirmation = in.nextLine();
            if ("Y".equalsIgnoreCase(confirmation)) {
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
        while (true) {
            try {
                boolean success = dataManager.deleteFund(fundToDelete.getId());
                if (success) {
                    org.getFunds().remove(fundToDelete);
                    System.out.println("Fund deleted successfully!");
                    return;
                } else {
                    System.out.println("Failed to delete the fund.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Missing or invalid Fund ID.");
            } catch (IllegalStateException e) {
                System.out.println("Error in communicating with server.");
            }

            System.out.print("Would you like to retry? (Y/N): ");
            String retry = in.nextLine().trim();
            if (!"Y".equals(retry)) {
                return;
            }

        }
    }

    // Task 2.8
    private void loginUI() {
        System.out.println("enter login");
        String login = in.nextLine();
        System.out.println("enter password");
        String password = in.nextLine();
        org = attemptLogIn(login, password);
    }

    // Task 2.8
    private Organization attemptLogIn(String login, String password) {

        Organization org = null;
        while (true) {
            try {
                org = dataManager.attemptLogin(login, password);
                if (org != null) {
                    System.out.println("Login successful!");
                    return org;
                } else {
                    System.out.println("Login failed. Invalid login or password.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Missing login or password.");
            } catch (IllegalStateException e) {
                System.out.println("Error in communicating with server.");
            }

            System.out.print("Would you like to retry? (Y/N): ");
            String retry = new Scanner(System.in).nextLine().trim();
            if (!"Y".equalsIgnoreCase(retry)) {
                return null;
            }

            System.out.print("Enter login: ");
            login = new Scanner(System.in).nextLine().trim();
            System.out.print("Enter password: ");
            password = new Scanner(System.in).nextLine().trim();
        }

    }

    // Task 3.1
    private Organization createOrgUI() {
        while (true) {
            String login;
            do {
                System.out.print("Enter login name: ");
                login = in.nextLine().trim();
                if (login.isEmpty()) {
                    System.out.println("Login name cannot be empty. Please re-enter.");
                }
            } while (login.isEmpty());

            String password;
            do {
                System.out.print("Enter password: ");
                password = in.nextLine().trim();
                if (password.isEmpty()) {
                    System.out.println("Password cannot be empty. Please re-enter.");
                }
            } while (password.isEmpty());

            String orgName;
            do {
                System.out.print("Enter organization name: ");
                orgName = in.nextLine().trim();
                if (orgName.isEmpty()) {
                    System.out.println("Organization name cannot be empty. Please re-enter.");
                }
            } while (orgName.isEmpty());

            String description;
            do {
                System.out.print("Enter organization description: ");
                description = in.nextLine().trim();
                if (description.isEmpty()) {
                    System.out.println("Organization description cannot be empty. Please re-enter.");
                }
            } while (description.isEmpty());

            try {
                Organization newOrg = dataManager.createOrganization(login, password, orgName, description);

                if (newOrg == null) {
                    throw new IllegalStateException("Error in communicating with server");
                }
                System.out.println("Organization created successfully!");
                System.out.println("Name: " + newOrg.getName());
                System.out.println("Description: " + newOrg.getDescription());
                return newOrg;
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Failed to create Organization: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error in communicating with server");
            }

            System.out.println("Would you like to retry? (Y/N): ");
            String retry = in.nextLine().trim().toLowerCase();
            if (!"Y".equalsIgnoreCase(retry)) {
                return null;
            }

        }
    }

    // Task 3.1
    public void createOrLoginUI() {
        while (true) {
            System.out.println("Create an  organization or Log in?(enter 'C' or 'L')");
            String answer = in.nextLine().trim();
            if ("C".equalsIgnoreCase(answer)) {
                org = createOrgUI();
                break;
            } else if ("L".equalsIgnoreCase(answer)) {
                loginUI();
                break;
            } else {
                System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    public static void main(String[] args) {
        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        UserInterface ui = new UserInterface(ds);

        if (args == null || args.length == 0) {
            ui.createOrLoginUI();

        } else {
            String login = args[0];
            String password = args[1];
            Organization org = ui.attemptLogIn(login, password);
            ui.org = org;
        }

        // after log in
        ui.start();
    }
}


