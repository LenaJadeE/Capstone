# Accounting Ledger

A command-line app that tracks every deposit and payment for a business or for personal use.
Transactions are saved in transactions.csv, and you can look at them in a ledger or run reports by month, year, or vendor.

### Features

- Add a deposit or make a payment, and each one is saved to transactions.csv
- Ledger screen: all transactions, deposits only, or payments only, with the oldest first and the newest at the bottom
- Reports: Month To Date, Previous Month, Year To Date, Previous Year, and Search by Vendor
- A count and a total under every list, like 4 transactions and Total: $6,410.00
- Bad input never crashes the app: it explains the problem and asks again
- A line in the data file that can't be read is skipped with a message, instead of stopping the app

### How to run it
1. Clone the repository and open it in IntelliJ
2. Make sure transactions.csv is in the project's top folder, next to pom.xml
3. Run App.java
4. Type a letter from the menu, like L for the ledger

### Screen Map

![img.png](img.png)

### Screenshots

![img_1.png](img_1.png) 
![img_2.png](img_2.png)
![img_3.png](img_3.png)

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

