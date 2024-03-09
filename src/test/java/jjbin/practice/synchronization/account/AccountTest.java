package jjbin.practice.synchronization.account;

import jjbin.practice.synchronization.account.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class AccountTest {
    @Test
    void 단건_계좌_입금() throws Exception {
        // given
        Account sut = new NaiveAccount(1L, 0);

        // when
        sut.deposit(1000);

        // then
        Assertions.assertThat(sut.getBalance()).isEqualTo(1000);
    }

    @Test
    void 단건_계좌_출금() throws Exception {
        // given
        Account sut = new NaiveAccount(1L, 1000);

        // when
        sut.withdraw(1000);

        // then
        Assertions.assertThat(sut.getBalance()).isEqualTo(0);
    }

    @Test
    void 단건_계좌_송금() throws Exception {
        // given
        Account sut1 = new NaiveAccount(1L, 1000);
        Account sut2 = new NaiveAccount(1L, 0);

        // when
        sut1.transfer(sut2, 1000);

        // then
        Assertions.assertThat(sut1.getBalance()).isEqualTo(0);
        Assertions.assertThat(sut2.getBalance()).isEqualTo(1000);
    }

    static Stream<Account> 동시_계좌_입금() {
        return Stream.of(
//                new NaiveAccount(1L, 0),
                new SynchronizedAccount(1L, 0),
                new AtomicAccount(1L, 0),
                new LockAccount(1L, 0),
                new ReadWriteLockAccount(1L, 0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("동시 입금 요청을 처리할 수 있다.")
    void 동시_계좌_입금(Account account) throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 1_000_000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account sut = account;

        // when
        for (int i = 0; i < nTasks; i++) {
            executorService.execute(() -> {
                try {
                    sut.deposit(1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Assertions.assertThat(sut.getBalance()).isEqualTo(1_000_000);
    }

    static Stream<Account> 동시_계좌_출금() {
        return Stream.of(
//                new NaiveAccount(1L, 100_000),
                new SynchronizedAccount(1L, 1_000_000),
                new AtomicAccount(1L, 1_000_000),
                new LockAccount(1L, 1_000_000),
                new ReadWriteLockAccount(1L, 1_000_000)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("동시 출금 요청을 처리할 수 있다.")
    void 동시_계좌_출금(Account account) throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 1_000_000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account sut = account;

        // when
        for (int i = 0; i < nTasks; i++) {
            executorService.execute(() -> {
                try {
                    sut.withdraw(1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Assertions.assertThat(account.getBalance()).isEqualTo(0);
    }

    static Stream<Account> 동시_계좌_입출금() {
        return Stream.of(
//                new NaiveAccount(1L, 1_000_000),
                new SynchronizedAccount(1L, 1_000_000),
                new AtomicAccount(1L, 1_000_000),
                new LockAccount(1L, 1_000_000),
                new ReadWriteLockAccount(1L, 1_000_000)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("동시 입출금 요청을 처리할 수 있다.")
    void 동시_계좌_입출금(Account account) throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 1_000_000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account sut = account;

        // when
        for (int i = 0; i < nTasks; i++) {
            if (i % 2 == 0) {
                executorService.execute(() -> {
                    try {
                        sut.deposit(1);
                    } finally {
                        latch.countDown();
                    }
                });
            } else {
                executorService.execute(() -> {
                    try {
                        sut.withdraw(1);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();

        // then
        Assertions.assertThat(account.getBalance()).isEqualTo(1_000_000);
    }

    static Stream<Arguments> 동시_계좌_송금() {
        return Stream.of(
//                Arguments.of(new NaiveAccount(1L, 1_000_000),new NaiveAccount(1L, 0)),
                Arguments.of(new SynchronizedAccount(1L, 1_000_000), new SynchronizedAccount(1L, 0)),
                Arguments.of(new AtomicAccount(1L, 1_000_000), new AtomicAccount(1L, 0)),
                Arguments.of(new LockAccount(1L, 1_000_000), new LockAccount(1L, 0)),
                Arguments.of(new ReadWriteLockAccount(1L, 1_000_000), new ReadWriteLockAccount(1L, 0))
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("동시 송금 요청을 처리할 수 있다.")
    void 동시_계좌_송금(Account from, Account to) throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 1_000_000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account sut1 = from;
        Account sut2 = to;

        // when
        for (int i = 0; i < nTasks; i++) {
            executorService.execute(() -> {
                try {
                    sut1.transfer(sut2, 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Assertions.assertThat(sut1.getBalance()).isEqualTo(0);
        Assertions.assertThat(sut2.getBalance()).isEqualTo(1_000_000);
    }

    static Stream<Account> 동시_계좌_입출금_잔고확인() {
        return Stream.of(
//                new NaiveAccount(1L, 1_000_000),
                new SynchronizedAccount(1L, 1_000_000),
                new AtomicAccount(1L, 1_000_000),
                new LockAccount(1L, 1_000_000),
                new ReadWriteLockAccount(1L, 1_000_000),
                new StampLockAccount(1L, 1_000_000)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("쓰기 작업 드문 경우")
    void 동시_계좌_입출금_잔고확인(Account account) throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 1_000_000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account sut = account;

        // when
        for (int i = 0; i < nTasks; i++) {
            if(i%10000 == 0) {
                executorService.execute(() -> {
                    try {
                        sut.withdraw(1);
                    } finally {
                        latch.countDown();
                    }
                });
            } else if (i%10000 == 1) {
                executorService.execute(() -> {
                    try {
                        sut.deposit(1);
                    } finally {
                        latch.countDown();
                    }
                });
            } else{
                executorService.execute(() -> {
                    try {
                        sut.getBalance();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();

        // then
        Assertions.assertThat(sut.getBalance()).isEqualTo(1_000_000);
    }

    @Test
    @DisplayName("식사하는 철학자들")
    void 데드락() throws Exception {
        // given
        int nThreads = 32;
        int nTasks = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nTasks);

        Account a = new DeadlockAccount(1L, 1_000_000);
        Account b = new DeadlockAccount(2L, 1_000_000);
        Account c = new DeadlockAccount(3L, 1_000_000);
        Account d = new DeadlockAccount(4L, 1_000_000);
        Account e = new DeadlockAccount(5L, 1_000_000);

        // when
        for (int i = 0; i < nTasks/5; i++) {
            execute(executorService, latch, a, b);
            execute(executorService, latch, b, c);
            execute(executorService, latch, c, d);
            execute(executorService, latch, d, e);
            execute(executorService, latch, e, a);
        }
        boolean isDeadlock = !latch.await(5, TimeUnit.SECONDS);

        //then
        Assertions.assertThat(isDeadlock).isTrue();
    }

    private static void execute(ExecutorService executorService, CountDownLatch latch, Account a, Account b) {
        executorService.execute(() -> {
            try {
                a.transfer(b, 1);
            } finally {
                latch.countDown();
            }
        });
    }
}