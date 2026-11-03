import java.util.concurrent.TimeUnit;

public class TreatmentRoom {
    private final Semaphore rooms;

    public TreatmentRoom(int numberOfRooms) {
        this.rooms = new Semaphore(numberOfRooms);
    }

   
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return rooms.tryAcquire(timeout, unit);
    }

    public void release() {
        rooms.release();
    }
}