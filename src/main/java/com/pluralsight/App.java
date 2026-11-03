package com.pluralsight;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final String TransactionsFile = "transactions.csv";
    private static final String FileHeader = "date|time|description|vendor|amount";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        runHomeScreen();
    }

    private static void runHomeScreen() {
        String command;
        do {
            System.out.println();
            System.out.println(" Home ");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
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

    private static void runLedgerScreen() {
        String command;
        do {
            System.out.println();
            System.out.println(" Ledger ");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
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

    private static void runReportsScreen() {
        String command;
        do {
            System.out.println();
            System.out.println(" Reports ");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("Enter a command: ");
            command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "1" -> displayMonthToDate();
                case "2" -> displayPreviousMonth();
                case "3" -> displayYearToDate();
                case "4" -> displayPreviousYear();
                case "5" -> searchByVendor();
                case "0" -> System.out.println("Back to Ledger");
                default -> System.out.println("That isn't a command. Try again.");
            }
        } while (!command.equals("0"));
    }

    private static void addDeposit() {
        System.out.println();
        System.out.println(" Add Deposit ");
        Transaction deposit = askForTransation(1);

        if (saveTransaction(deposit)) {
            System.out.printf(Locale.US, "Saved deposit of $%.2f from %s.%n", deposit.getAmount(), deposit.getVendor());
        }
        pressEnterToContinue();
    }

    private static void makePayment() {
        System.out.println();
        System.out.println(" Make Payment ");
        Transaction payment = askForTransaction(-1);

        if (saveTransaction(payment)) {
            System.out.printf(Locale.US, "Saved payment of $%.2f to %s.%n", -payment.getAmount(), payment.getVendor());
        }
        pressEnterToContinue();
    }

    private static void displayAllEntries() {
        System.out.println("TODO: display all entries");
        pressEnterToContinue();
    }

    private static void displayDeposits() {
        System.out.println("TODO: display deposits");
        pressEnterToContinue();
    }

    private static void displayPayments() {
        System.out.println("TODO: display payments");
        pressEnterToContinue();
    }

    private static void displayMonthToDate() {
        System.out.println("TODO: display month to date");
        pressEnterToContinue();
    }

    private static void displayPreviousMonth() {
        System.out.println("TODO: display previous month");
        pressEnterToContinue();
    }

    private static void displayYearToDate() {
        System.out.println("TODO: display year to date");
        pressEnterToContinue();
    }

    private static void displayPreviousYear() {
        System.out.println("TODO: display previous year");
        pressEnterToContinue();
    }

    private static void searchByVendor() {
        System.out.println("TODO: search by vendor");
        pressEnterToContinue();
    }

    private static Transaction askForTransaction(int sign) {
        String description = promptForString("Description: ");
        String vendor = promptForString("Vendor: ");

        double amount = promptForDouble("Amount: ");
        while (amount <= 0) {
            System.out.println("The amount must be more than 0.");
            amount = promptForDouble("Amount: ");
        }

        return new Transaction(LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
                description, vendor, sign * amount);
    }

    private static boolean saveTransaction(Transaction transaction) {
        Path file = Path.of(TransactionsFile);
        try {
            boolean newFile = !Files.exists(file) || Files.size(file) == 0;
            boolean needsLineBreak = !newFile && !endsWithLineBreak(file);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
                if (newFile) {
                    writer.write(FILE_HEADER + "\n");
                }
                if (needsLineBreak) {
                    writer.write("\n");
                }
                writer.write(transaction.toFileLine() + "\n");
            }

            transactions.add(transaction);
            return true;
        } catch (IOException e) {
            System.out.println("ERROR: Could not save to " + TRANSACTIONS_FILE + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean endsWithLineBreak(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        return bytes[bytes.length - 1] == '\n';
    }

    private static void pressEnterToContinue() {
        System.out.println();
        System.out.print("Press Enter to go back to the menu...");
        scanner.nextLine();
    }
}
