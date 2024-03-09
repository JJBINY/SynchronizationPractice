package jjbin.practice.synchronization.account.domain;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicAccount implements Account {
    private Long id;
    private AtomicLong balance;

    public AtomicAccount(Long id, long balance) {
        this.id = id;
        this.balance = new AtomicLong(balance);
    }

    @Override
    public void deposit(long amount) {
        balance.addAndGet(amount);
    }

    @Override
    public void withdraw(long amount) {
        while (true) {
            long temp = balance.get();
            if (temp < amount) {
                throw new IllegalStateException();
            }
            if(balance.compareAndSet(temp, temp - amount)){
                return;
            }
        }
    }

    @Override
    public long getBalance() {
        return balance.get();
    }

    @Override
    public void transfer(Account to, long amount) {
        this.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public String toString() {
        return "AtomicAccount";
    }
}
