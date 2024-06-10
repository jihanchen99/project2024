import java.util.List;
import java.util.Scanner;

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
            System.out.println("Enter 'q' or 'quit' to exit");

            String input = in.nextLine();
            if (input.equals('q') || input.equals("quit")) {
                break;
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


    public void displayFund(int fundNumber) {

        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());

        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());
        for (Donation donation : donations) {
            System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
        }


        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();


    }


    public static void main(String[] args) {

        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        String login = args[0];
        String password = args[1];


        Organization org = ds.attemptLogin(login, password);

        if (org == null) {
            System.out.println("Login failed.");
        } else {

            UserInterface ui = new UserInterface(ds, org);

            ui.start();

        }
    }

}
