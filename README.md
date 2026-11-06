# Pocket Ledger (Accounting Ledger)

A command-line app that tracks every deposit and payment for a business or for personal use.
Transactions are saved in transactions.csv, and you can look at them in a ledger or run reports by month, year, or vendor.

### Features

- Add a deposit or make a payment, and each one is saved to transactions.csv
- Ledger screen: all transactions, deposits only, or payments only, with the oldest first and the newest at the bottom
- Reports: Month To Date, Previous Month, Year To Date, Previous Year, and Search by Vendor
- Custom Search: answer only the questions you care about and press Enter to skip the rest
- A count and a total under every list, like 4 transactions and Total: $6,410.00
- Bad input never crashes the app: it explains the problem and asks again
- A line in the data file that can't be read is skipped with a message, instead of stopping the app
- A welcome banner when the app starts
- Long lists show 20 at a time, with Enter for more or Q to stop
- Color: deposits show in green and payments in red (set `USE_COLOR` to `false` in `App.java` to turn it off)
- Every screen has a title badge, and every list has gray dividers
- - Search by Description: type part of a description and see every match
- Partial Search: type part of a word and see every transaction whose description or vendor contains it

### How to Run It
1. Clone the repository and open it in IntelliJ
2. Make sure transactions.csv is in the project's top folder, next to pom.xml
3. Run App.java
4. Type a letter from the menu, like L for the ledger

### Screen Map

![screenMap.png](screenshots/screenMap.png)

### Screenshots

    private static void displayWelcomeBanner() {
        System.out.println(style("P O C K E T L E D G E R", BOLD));
        System.out.println(style(TITLE_LINE, GRAY));
        System.out.println(style(" Every dollar in, Every dollar out  ", GRAY));
    }

    private static void runHomeScreen() {
        String command;
        do {
            displayTitle("Home");
            double balance = calculateTotal(transactions);
            System.out.println("Current balance: " + colorAmount(formatMoney(balance), balance));
            System.out.println();
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.println();
            System.out.print("Enter a command: ");
            command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "D" -> addDeposit();
                case "P" -> makePayment();
                case "L" -> runLedgerScreen();
                case "X" -> System.out.println("Goodbye!");
                default -> System.out.println("That isn't a command. Try again.");
            }
        } while (!command.equals("X"));
    }

![homeScreen.png](screenshots/homeScreen.png)

    private static void runLedgerScreen() {
        String command;
        do {
            displayTitle("Ledger");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.println();
            System.out.print("Enter a command: ");
            command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "A" -> displayAllEntries();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> runReportsScreen();
                case "H" -> System.out.println("Back to Home");
                default -> System.out.println("That isn't a command. Try again.");
            }
        } while (!command.equals("H"));
    }

![ledgerScreen.png](screenshots/ledgerScreen.png)

    private static void runReportsScreen() {
        String command;
        do {
            displayTitle("Reports");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search By Vendor");
            System.out.println("6) Search By Description");
            System.out.println("7) Custom Search");
            System.out.println("8) Partial Search");
            System.out.println("0) Back");
            System.out.println();
            System.out.print("Enter a command: ");
            command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "1" -> displayMonthToDate();
                case "2" -> displayPreviousMonth();
                case "3" -> displayYearToDate();
                case "4" -> displayPreviousYear();
                case "5" -> searchByVendor();
                case "6" -> searchByDescription();
                case "7" -> customSearch();
                case "8" -> partialSearch();
                case "0" -> System.out.println("Back to Ledger");
                default -> System.out.println("That isn't a command. Try again.");
            }
        } while (!command.equals("0"));
    }

![reportScreen.png](screenshots/reportScreen.png)

### Custom Search

Option 6) Custom Search on the Reports screen asks five questions: start date, end date, description, vendor, and amount. 
Answer the ones you care about and press Enter to skip the others. A skipped question doesn't filter anything, and a transaction has to match every answer you gave to show up.

![customSearchScreen.png](screenshots/customSearchScreen.png)

For example, what happened between October 18 and October 31, 2026? Only the two dates are filled in, so the description, vendor, and amount are skipped:

- Dates include the start and end days
- Description and vendor match part of the text, ignoring capitals (`star` finds Starbucks)
- Amount must match exactly, so type `-89.50` to find a payment
- A typo asks again, and a start date after the end date shows a message

### Interesting Code
A deposit and a payment are the same kind of thing, so the app has one transaction class and one list. 
The only difference is the sign of the amount: positive for a deposit, negative for a payment. 
askForTransaction() asks the same questions for both and multiplies the amount by sign, which is 1 for a deposit and -1 for a payment. 
The user always types a positive number, so a payment can't be saved as a deposit by mistake: 

    private static Transaction askForTransaction(int sign) {
        String description = promptForString("Description: ");
        String vendor = promptForString("Vendor: ");

        double amount = promptForAmount();
        while (amount <= 0) {
            System.out.println("The amount must be more than 0.");
            amount = promptForAmount();
        }

        return new Transaction(LocalDate.now(), LocalTime.now(),
                description, vendor, sign * amount);
    }

Because payments are negative, the rest of the app needs no special cases.
The Deposits list keeps the amounts above 0, the Payments list keeps the amounts below 0, and the balance is just every amount added together:

    private static double calculateTotal(List<Transaction> entries) {
        double total = 0;
        for (Transaction transaction : entries) {
            total += transaction.getAmount();
        }
        return total;
    }

The same method gives the total under every list and every report, from Deposits (all positive) to Search by Vendor (a mix).

### Author
Lena Rhodes, Year Up Java Academy

