# Bank Ledger System

## Overview

A simple command-line bank program written in Java. It keeps account balances in memory while the program runs.

## Features

- Create an account
- Deposit money
- Withdraw money
- Show all balances

## Rules

- Account names can't be empty or duplicate an existing account. Leading and trailing spaces are ignored.
- Deposits and withdrawals must be positive numbers.
- Withdrawals can't exceed the account balance.
- Invalid input shows an error message and returns to the menu instead of crashing.

## How to run

```
javac Main.java
java Main
```

Then pick an option from the menu by typing its number (1–5).