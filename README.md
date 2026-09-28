# Bank Ledger System

## Overview

A simple command-line bank program written in Java. It uses double-entry 
bookkeeping so balances are never stored or edited. Every deposit and withdrawal 
is recorded as a transaction, and each account's balance is calculated from 
its transaction history.

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

## Features

- Create an account
- Deposit money
- Withdraw money
- Show all balances
- Show transaction history, with a check that all balances net to zero

## Rules

- Account names can't be empty or duplicate an existing account. Leading and 
trailing spaces are ignored.
- `External` is a reserved name and can't be used for an account.
- Deposits and withdrawals must be positive numbers with at most 2 decimal places.
- Withdrawals can't exceed the account balance.
- Invalid input shows an error message and returns to the menu instead of crashing.

## How to run

```
javac *.java
java Main
```

Then pick an option from the menu by typing its number (1–6).