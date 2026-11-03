import java.util.concurrent.TimeUnit;

public class Semaphore {
    private int permits;

    public Semaphore() {
        this.permits = 0;
    }

    public Semaphore(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException("Permits cannot be negative.");
        }

        this.permits = permits;
    }

    public synchronized void acquire() {
        boolean interrupted = false;

        while (permits == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        permits--;

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void release() {
        permits++;
        notify();
    }

    public synchronized boolean tryAcquire() {
        if (permits == 0) {
            return false;
        }

        permits--;
        return true;
    }

    public synchronized boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException(
                "Timeout cannot be negative."
            );
        }

        if (unit == null) {
            throw new NullPointerException("TimeUnit cannot be null.");
        }

        long timeoutNanos = unit.toNanos(timeout), startTime = System.nanoTime();

        while (permits == 0) {
            long remaining = timeoutNanos - (System.nanoTime() - startTime);

            if (remaining <= 0) {
                return false;
            }

            long millis = remaining / 1000000;
            int nanos = (int) (remaining % 1000000);

            wait(millis, nanos);
        }

        permits--;
        return true;
    }

    public synchronized int getPermits() {
        return permits;
    }
}