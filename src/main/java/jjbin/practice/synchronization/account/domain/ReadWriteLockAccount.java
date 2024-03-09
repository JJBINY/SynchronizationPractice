package jjbin.practice.synchronization.account.domain;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockAccount extends NaiveAccount {

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ReadWriteLockAccount(Long id, long balance) {
        super(id, balance);
    }

    @Override
    public void deposit(long amount) {
        rwLock.writeLock().lock();
        try {
            balance += amount;
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    @Override
    public void withdraw(long amount) {
        rwLock.writeLock().lock();
        try {
            if (balance < amount) {
                throw new IllegalStateException();
            }
            balance -= amount;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public long getBalance() {
        rwLock.readLock().lock();
        try {
            return balance;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void transfer(Account to, long amount) {
        this.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public String toString() {
        return "ReadWriteLockAccount";
    }
}
