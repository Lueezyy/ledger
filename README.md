# Bank Ledger System

## Overview

A simple command-line bank program written in Java. It uses double-entry 
bookkeeping so balances are never stored or edited. Every deposit and withdrawal 
is recorded as a transaction, and each account's balance is calculated from 
its transaction history. All data is saved to a CSV file.

## How it works

- Each transaction is made of entries that move money between accounts, and 
its entries must add up to zero.
- A built-in `External` account that represents the world outside the bank 
is used for deposits and withdrawals.
- Transactions are only add, not changed or removed. The full history 
is always available.
- Money is stored as `BigDecimal` instead of `double` since `double` can't 
represent exact amounts like 0.10 and its rounding errors can cause wrong 
balances.
- Every transaction is timestamped, and an account's statement shows each 
transaction with a running balance.
- The ledger is saved after every change; saves write to a temporary file first 
and then replace the real file in one step.

## Features

- Create an account
- Deposit money
- Withdraw money
- Transfer money between accounts
- Show all balances
- Show transaction history, with a check that all balances net to zero
- View an account's statement with dates and a running balance
- Automatic saving and loading of all accounts and history

## Rules

- Account names can't be empty or duplicate an existing account. Leading and 
trailing spaces are ignored.
- `External` is a reserved name and can't be used for an account.
- Deposits, withdrawals, and transfers must be positive numbers with at most 2 
decimal places.
- Withdrawals and transfers can't exceed the account balance.
- You can't transfer money from an account to itself.
- Every rule violation has its own `LedgerException` subtype, rejected 
operations make no changes to balances or transactions.

## How to run

```
mvn compile
java -cp target\classes Main
```

Data is saved to `ledger.csv` in the current folder. To use a different file, 
pass its path: 

```
java -cp target\classes Main data.csv
```

Then pick an option from the menu by typing its number (1–8).

## How to test

```
mvn test
```

This compiles the code and runs all JUnit tests.