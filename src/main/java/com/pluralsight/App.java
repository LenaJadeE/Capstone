package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    private static final String TransactionsFile = "transactions.csv";
    private static final DateTimeFormatter TimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String RowFormat = "%-10s  %-8s  %-30s  %-22s  %10s%n";
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
            System.out.println(" Home ");
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
        Transaction deposit = askForTransaction(1);

        if (saveTransaction(deposit)) {
            System.out.printf("Saved deposit of $%.2f from %s.%n", deposit.getAmount(), deposit.getVendor());
        }
        pressEnterToContinue();
    }

    private static void makePayment() {
        System.out.println();
        System.out.println(" Make Payment ");
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
        List<Transaction> deposits = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                deposits.add(transaction);
            }
        }

        displayEntries("Deposits", deposits);
        pressEnterToContinue();
    }

    private static void displayPayments() {
        List<Transaction> payments = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                payments.add(transaction);
            }
        }

        displayEntries("Payments", payments);
        pressEnterToContinue();
    }

    private static void displayMonthToDate() {
        LocalDate today = LocalDate.now();

        displayEntries("Month To Date", findByMonth(today.getYear(), today.getMonthValue()));
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

        return new Transaction(LocalDate.now(), LocalTime.now(),
                description, vendor, sign * amount);
    }

    private static boolean saveTransaction(Transaction transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TransactionsFile, true))) {
            String line = transaction.getDate() + "|"
                    + transaction.getTime().format(TimeFormat) + "|"
                    + transaction.getDescription() + "|"
                    + transaction.getVendor() + "|"
                    + String.format("%.2f", transaction.getAmount());
            writer.write(line + "\n");
            transactions.add(transaction);
            return true;
        } catch (IOException e) {
            System.out.println("ERROR: Could not save to " + TransactionsFile + ": " + e.getMessage());
            return false;
        }
    }

    private static void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TransactionsFile))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    addTransactionFromLine(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No " + TransactionsFile + " found, so the ledger starts empty.");
        } catch (IOException e) {
            System.out.println("ERROR: Could not read " + TransactionsFile + ": " + e.getMessage());
        }
    }

    private static void addTransactionFromLine(String line) {
        try {
            String[] parts = line.split("\\|");

            LocalDate date = LocalDate.parse(parts[0]);
            LocalTime time = LocalTime.parse(parts[1]);
            String description = parts[2];
            String vendor = parts[3];
            double amount = Double.parseDouble(parts[4]);

            transactions.add(new Transaction(date, time, description, vendor, amount));
        } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
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



    private static void displayEntries(String title, List<Transaction> entries) {
        System.out.println();
        System.out.println("=== " + title + " ===");

        if (entries.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf(RowFormat, "Date", "Time", "Description", "Vendor", "Amount");
        for (Transaction transaction : entries) {
            System.out.printf(RowFormat,
                    transaction.getDate(),
                    transaction.getTime().format(TimeFormat),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    String.format("%.2f", transaction.getAmount()));
        }

        System.out.println();
        System.out.println(entries.size() + (entries.size() == 1 ? " transaction" : " transactions"));
        System.out.println("Total: " + formatMoney(sumOf(entries)));
    }

    private static double sumOf(List<Transaction> entries) {
        double total = 0;
        for (Transaction transaction : entries) {
            total += transaction.getAmount();
        }
        return total;
    }

    private static String formatMoney(double amount) {
        String text = String.format("$%,.2f", Math.abs(amount));
        if (amount < 0) {
            return "-" + text;
        }
        return text;
    }

    private static void pressEnterToContinue() {
        System.out.println();
        System.out.print("Press Enter to go back to the menu...");
        scanner.nextLine();
    }

    private static double promptForDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
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
