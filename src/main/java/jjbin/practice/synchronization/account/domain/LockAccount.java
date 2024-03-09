package jjbin.practice.synchronization.account.domain;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAccount extends NaiveAccount {
    private Lock lock = new ReentrantLock();

    public LockAccount(Long id, long balance) {
        super(id, balance);
    }

    @Override
    public void deposit(long amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void withdraw(long amount) {
        lock.lock();
        try {
            if (balance < amount) {
                throw new IllegalStateException();
            }
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void transfer(Account to, long amount) {
        this.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public String toString() {
        return "LockAccount";
    }
}
