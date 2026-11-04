package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final String TRANSACTIONS_FILE = "transactions.csv";
    private static final String FILE_HEADER = "date|time|description|vendor|amount";
    private static final double ZERO_TOLERANCE = 0.005;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String ROW_FORMAT = "%-10s  %-8s  %-30s  %-22s  %10s%n";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        loadTransactions();
        runHomeScreen();
    }

    private static void runHomeScreen() {
        String command;
        do {
            System.out.println();
            System.out.println("=== Home ===");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
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
            System.out.println("=== Ledger ===");
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
            System.out.println("=== Reports ===");
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
        System.out.println("=== Add Deposit ===");
        Transaction deposit = askForTransaction(1);

        if (saveTransaction(deposit)) {
            System.out.printf("Saved deposit of $%.2f from %s.%n", deposit.getAmount(), deposit.getVendor());
        }
        pressEnterToContinue();
    }

    private static void makePayment() {
        System.out.println();
        System.out.println("=== Make Payment ===");
        Transaction payment = askForTransaction(-1);

        if (saveTransaction(payment)) {
            System.out.printf("Saved payment of $%.2f to %s.%n", -payment.getAmount(), payment.getVendor());
        }
        pressEnterToContinue();
    }

    private static void displayAllEntries() {
        displayEntries("All Transactions", transactions);
        pressEnterToContinue();
    }

    private static void displayDeposits() {
        displayEntries("Deposits", findDeposits());
        pressEnterToContinue();
    }

    private static void displayPayments() {
        displayEntries("Payments", findPayments());
        pressEnterToContinue();
    }

    private static void displayMonthToDate() {
        LocalDate today = LocalDate.now();

        displayEntries("Month To Date", findByMonth(today.getYear(), today.getMonthValue()));
        pressEnterToContinue();
    }

    private static void displayPreviousMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        displayEntries("Previous Month", findByMonth(lastMonth.getYear(), lastMonth.getMonthValue()));
        pressEnterToContinue();
    }

    private static void displayYearToDate() {
        displayEntries("Year To Date", findByYear(LocalDate.now().getYear()));
        pressEnterToContinue();
    }

    private static void displayPreviousYear() {
        displayEntries("Previous Year", findByYear(LocalDate.now().getYear() - 1));
        pressEnterToContinue();
    }

    private static void searchByVendor() {
        String vendor = promptForString("Vendor name: ");

        displayEntries("Vendor: " + vendor, findByVendor(vendor));
        pressEnterToContinue();
    }

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

    private static boolean saveTransaction(Transaction transaction) {
        File file = new File(TRANSACTIONS_FILE);
        boolean isNewFile = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            if (isNewFile) {
                writer.write(FILE_HEADER + "\n");
            }
            writer.write(buildLine(transaction) + "\n");
            transactions.add(transaction);
            return true;
        } catch (IOException e) {
            System.out.println("ERROR: Could not save to " + TRANSACTIONS_FILE + ": " + e.getMessage());
            return false;
        }
    }

    private static String buildLine(Transaction transaction) {
        return transaction.getDate() + "|"
                + transaction.getTime().format(TIME_FORMAT) + "|"
                + transaction.getDescription() + "|"
                + transaction.getVendor() + "|"
                + String.format("%.2f", transaction.getAmount());
    }

    private static void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    addTransactionFromLine(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No " + TRANSACTIONS_FILE + " found, so the ledger starts empty.");
        } catch (IOException e) {
            System.out.println("ERROR: Could not read " + TRANSACTIONS_FILE + ": " + e.getMessage());
        }
    }

    private static void addTransactionFromLine(String line) {
        String[] parts = line.split("\\|");

        if (parts.length != 5) {
            System.out.println("Skipping a line that isn't in date|time|description|vendor|amount format: " + line);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(parts[0]);
            LocalTime time = LocalTime.parse(parts[1]);
            String description = parts[2];
            String vendor = parts[3];
            double amount = Double.parseDouble(parts[4]);

            transactions.add(new Transaction(date, time, description, vendor, amount));
        } catch (DateTimeParseException e) {
            System.out.println("Skipping a line that isn't in date|time|description|vendor|amount format: " + line);
        } catch (NumberFormatException e) {
            System.out.println("Skipping a line that isn't in date|time|description|vendor|amount format: " + line);
        }
    }

    private static List<Transaction> findByMonth(int year, int month) {
        List<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year && transaction.getDate().getMonthValue() == month) {
                matches.add(transaction);
            }
        }
        return matches;
    }

    private static List<Transaction> findByYear(int year) {
        List<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().getYear() == year) {
                matches.add(transaction);
            }
        }
        return matches;
    }

    private static List<Transaction> findDeposits() {
        List<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                matches.add(transaction);
            }
        }
        return matches;
    }

    private static List<Transaction> findPayments() {
        List<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                matches.add(transaction);
            }
        }
        return matches;
    }

    private static List<Transaction> findByVendor(String vendor) {
        List<Transaction> matches = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                matches.add(transaction);
            }
        }
        return matches;
    }

    private static void displayEntries(String title, List<Transaction> entries) {
        System.out.println();
        System.out.println("=== " + title + " ===");

        if (entries.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf(ROW_FORMAT, "Date", "Time", "Description", "Vendor", "Amount");
        for (Transaction transaction : entries) {
            System.out.printf(ROW_FORMAT,
                    transaction.getDate(),
                    transaction.getTime().format(TIME_FORMAT),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    String.format("%.2f", transaction.getAmount()));
        }

        displayTotals(entries);
    }

    private static void displayTotals(List<Transaction> entries) {
        System.out.println();
        if (entries.size() == 1) {
            System.out.println("1 transaction");
        } else {
            System.out.println(entries.size() + " transactions");
        }
        System.out.println("Total: " + formatMoney(calculateTotal(entries)));
    }

    private static double calculateTotal(List<Transaction> entries) {
        double total = 0;
        for (Transaction transaction : entries) {
            total += transaction.getAmount();
        }
        return total;
    }

    private static String formatMoney(double amount) {
        String text = String.format("$%,.2f", Math.abs(amount));
        if (amount < -ZERO_TOLERANCE) {
            return "-" + text;
        }
        return text;
    }

    private static void pressEnterToContinue() {
        System.out.println();
        System.out.print("Press Enter to go back to the menu...");
        scanner.nextLine();
    }

    private static double promptForAmount() {
        while (true) {
            System.out.print("Amount: ");
            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());
                if (Double.isNaN(amount) || Double.isInfinite(amount)) {
                    System.out.println("Please enter a number, like 89.50.");
                } else {
                    return amount;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, like 89.50.");
            }
        }
    }

    private static String promptForString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("This can't be blank.");
            } else if (input.contains("|")) {
                System.out.println("Please don't use the | character.");
            } else {
                return input;
            }
        }
    }
}
