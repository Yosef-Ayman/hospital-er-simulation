public class Patient implements Runnable {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final Semaphore admission = new Semaphore(0);

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    public int getPatientId() {
        return id;
    }
    
    public void waitForAdmission() {
        admission.acquire();
    }

    public void signalAdmission() {
        admission.release();
    }

    @Override
    public void run() {
        waitingRoom.addPatient(this);
    }
}
