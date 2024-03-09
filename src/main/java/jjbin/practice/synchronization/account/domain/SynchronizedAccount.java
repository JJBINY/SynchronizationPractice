package jjbin.practice.synchronization.account.domain;

public class SynchronizedAccount implements Account {
    private Long id;
    private volatile long balance;

    public SynchronizedAccount(Long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    @Override
    public synchronized void deposit(long amount) {
        balance += amount;
    }

    @Override
    public synchronized void withdraw(long amount) {
        if (balance < amount) {
            throw new IllegalStateException();
        }
        balance -= amount;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    @Override
    public void transfer(Account to, long amount) {
        this.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public String toString() {
        return "SynchronizedAccount";
    }
}
