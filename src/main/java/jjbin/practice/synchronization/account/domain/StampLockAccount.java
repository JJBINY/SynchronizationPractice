package jjbin.practice.synchronization.account.domain;

import java.util.concurrent.locks.StampedLock;

public class StampLockAccount extends NaiveAccount {

    private StampedLock lock = new StampedLock();

    public StampLockAccount(Long id, long balance) {
        super(id, balance);
    }

    @Override
    public void deposit(long amount) {
        long stamp = lock.writeLock();
        try {
            balance += amount;
        } finally {
            lock.unlockWrite(stamp);
        }

    }

    @Override
    public void withdraw(long amount) {
        long stamp = lock.writeLock();
        try {
            if (balance < amount) {
                throw new IllegalStateException();
            }
            balance -= amount;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public long getBalance() {
        long stamp = lock.tryOptimisticRead();
        long curBalance = this.balance;
        if(!lock.validate(stamp)){
            stamp = lock.readLock();
            try {
                curBalance = this.balance;
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return curBalance;
    }

    @Override
    public void transfer(Account to, long amount) {
        this.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public String toString() {
        return "StampLockAccount";
    }
}
