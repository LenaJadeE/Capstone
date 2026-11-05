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

### How to Run It
1. Clone the repository and open it in IntelliJ
2. Make sure transactions.csv is in the project's top folder, next to pom.xml
3. Run App.java
4. Type a letter from the menu, like L for the ledger

### Screen Map

![img_12.png](img_12.png)

### Screenshots

![img_16.png](img_16.png)
![img_14.png](img_14.png)
![img_2.png](img_2.png)
![img_9.png](img_9.png)
![img_13.png](img_13.png)
![img_15.png](img_15.png)

### Custom Search

Option 6) Custom Search on the Reports screen asks five questions: start date, end date, description, vendor, and amount. 
Answer the ones you care about and press Enter to skip the others. A skipped question doesn't filter anything, and a transaction has to match every answer you gave to show up.
![img_17.png](img_17.png)
For example, what happened between October 18 and October 31, 2026? Only the two dates are filled in, so the description, vendor, and amount are skipped:

Custom Search

The start and end dates include the days themselves
Description and vendor match any part of the text, and capital letters don't matter, so star finds Starbucks
Amount must match exactly, so type -89.50 to find a payment
A mistyped date or amount asks again, and a start date after the end date is explained instead of showing an empty list

### Interesting Code
A deposit and a payment are the same kind of thing, so the app has one Transaction class and one list. 
The only difference is the sign of the amount: positive for a deposit, negative for a payment. askForTransaction() asks the same questions for both and multiplies the amount by sign, which is 1 for a deposit and -1 for a payment. 
The user always types a positive number, so a payment can't be saved as a deposit by mistake: 

![img_4.png](img_4.png)

Because payments are negative, the rest of the app needs no special cases.
The Deposits list keeps the amounts above 0, the Payments list keeps the amounts below 0, and the balance is just every amount added together:

![img_5.png](img_5.png)

The same method gives the total under every list and every report, from Deposits (all positive) to Search by Vendor (a mix).

### Author
Lena Rhodes, Year Up Java Academy

