package jjbin.practice.synchronization.account.domain;

public class NaiveAccount implements Account {
    protected Long id;
    protected long balance;

    public NaiveAccount(Long id, long balance) {
        this.id = id;
        this.balance = balance;
    }


    @Override
    public void deposit(long amount) {
        balance += amount;
    }

    @Override
    public void withdraw(long amount) {
        if (balance < amount) {
            throw new IllegalStateException("잔액 부족");
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
        return "NaiveAccount";
    }
}
