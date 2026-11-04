import java.util.Random;
import java.util.concurrent.TimeUnit;

class Doctor implements Runnable {
    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRoom treatmentRooms;
    private final Semaphore completedPatients;
    private final Random random = new Random();

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRoom treatmentRooms, Semaphore completedPatients) 
    {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.completedPatients = completedPatients;
    }

    @Override
    public void run() {
        while (true) {
            Patient patient = waitingRoom.getPatient(id);
            if (patient == null) 
            {
                break;
            }
            System.out.println("Doctor " + id + " is trying to acquire a treatment room");
            boolean acquired;
            try 
            {
                acquired = treatmentRooms.tryAcquire(3, TimeUnit.SECONDS);
            }
             catch (InterruptedException e)
            {
                waitingRoom.returnPatient(patient);
                Thread.currentThread().interrupt();
                break;
            }
            if (!acquired)
            {
                System.out.println("Doctor " + id + " could not acquire a treatment room");
                System.out.println("Doctor " + id + " is sending P" + patient.getPatientId() + " back to the waiting room");
                waitingRoom.returnPatient(patient);
                System.out.println("Doctor " + id + " is now available");
                continue;
            }

            System.out.println("Doctor " + id + " acquired a treatment room");
            System.out.println("Doctor " + id + " begins treating P" + patient.getPatientId());

            try
            {
                Thread.sleep(1000 + random.nextInt(3000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Doctor " + id + " finished treating P" + patient.getPatientId());
            System.out.println("Doctor " + id + " released the treatment room");
            treatmentRooms.release();
            System.out.println("Doctor " + id + " is now available");
            System.out.println("P" + patient.getPatientId() + " left");
            completedPatients.release();
        }
    }

}
