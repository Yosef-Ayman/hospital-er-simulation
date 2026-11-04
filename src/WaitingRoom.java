import java.util.LinkedList;
import java.util.Queue;

public class WaitingRoom {

    private final Queue<Patient> waitingPatients = new LinkedList<>(); // inside
    private final Queue<Patient> outsidePatients = new LinkedList<>(); // outside

    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private final int numberOfDoctors;
    private boolean closed;

    public WaitingRoom(int capacity, int doctors) {
        if (capacity <= 0 || doctors <= 0) {
            throw new IllegalArgumentException("Capacity and number of doctors must be positive");
        }
        this.numberOfDoctors = doctors;
        this.empty = new Semaphore(capacity);
        this.full = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.closed = false;
    }

    public void addPatient(Patient patient) {
        mutex.acquire();
         System.out.println("P" + patient.getPatientId() + " arrived.");
        if (closed) {
            mutex.release();
            throw new IllegalStateException("Waiting room is closed");
        }
        if (outsidePatients.isEmpty() && empty.tryAcquire()) {
            waitingPatients.add(patient);
            System.out.println("P" + patient.getPatientId() + " entered the waiting room.");
            full.release();                 
            mutex.release();
        } else {
            outsidePatients.add(patient);
            System.out.println("The waiting room is full. P" + patient.getPatientId()
                    + " is waiting outside.");
            mutex.release();                
            patient.waitForAdmission();     
        }
    }

    public Patient getPatient(int doctorId) {
        full.acquire();                     
        mutex.acquire();

        if (waitingPatients.isEmpty()) {    
            mutex.release();
            return null;
        }
        Patient patient = waitingPatients.remove();
        System.out.println("Doctor " + doctorId + " is assigned to P" + patient.getPatientId());
        admitNextPatient();         
        mutex.release();
        return patient;
    }

    public void returnPatient(Patient patient) {
        mutex.acquire();
        if (closed) {
            mutex.release();
            throw new IllegalStateException("Cannot return a patient after closing");
        }

        if (outsidePatients.isEmpty() && empty.tryAcquire()) {
            waitingPatients.add(patient);
            System.out.println("P" + patient.getPatientId() + " went back to the waiting room.");
            full.release();
        } else {
            outsidePatients.add(patient);
            System.out.println("The waiting room is full. P" + patient.getPatientId()
                    + " is waiting outside to go back in.");
        }
        mutex.release();
    }

   
    private void admitNextPatient() {
        if (!outsidePatients.isEmpty()) {
            Patient next = outsidePatients.remove();
            waitingPatients.add(next);
            System.out.println("P" + next.getPatientId() + " entered the waiting room.");
            full.release();                  
            next.signalAdmission();         
        } else {
            empty.release();              
        }
    }

   
    public void close() {
        mutex.acquire();
        if (!outsidePatients.isEmpty() || !waitingPatients.isEmpty()) {
            mutex.release();
            throw new IllegalStateException("Cannot close while patients are still waiting");
        }
        closed = true;
        for (int i = 0; i < numberOfDoctors; i++) {
            full.release();
        }
        mutex.release();
    }
}