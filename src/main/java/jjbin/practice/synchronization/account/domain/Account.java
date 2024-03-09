package jjbin.practice.synchronization.account.domain;

public interface Account {
    void deposit(long amount);
    void withdraw(long amount);

    long getBalance();
    void transfer(Account to, long amount);
}
