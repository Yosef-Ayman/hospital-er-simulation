import java.util.Scanner;

public class Hospital {

    private int capacity;
    private int numberOfDoctors;
    private int numberOfPatients;

    private int readPositiveInt(Scanner scanner, String prompt) {
        while(true) {
            System.out.print(prompt);

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter an integer.");
                scanner.next();
                continue;
            }

            int value = scanner.nextInt();

            if (value > 0) {
                return value;
            }

            System.out.println("Please enter a positive integer.");
        }
    }

    public void start() throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        capacity = readPositiveInt(scanner, "Enter waiting room capacity: ");
        numberOfDoctors = readPositiveInt(scanner, "Enter number of doctors: ");
        numberOfPatients = readPositiveInt(scanner, "Enter number of patients: ");

        WaitingRoom waitingRoom = new WaitingRoom(capacity, numberOfDoctors);
        TreatmentRoom treatmentRooms = new TreatmentRoom(2);
        Semaphore completedPatients = new Semaphore(0);

        Thread[] doctorThreads = new Thread[numberOfDoctors];

        for (int i = 0; i < numberOfDoctors; i++) {
            Doctor doctor = new Doctor(
                i + 1,
                waitingRoom,
                treatmentRooms,
                completedPatients
            );

            doctorThreads[i] = new Thread(doctor);
        }

        Thread[] patientThreads = new Thread[numberOfPatients];

        for (int i = 0; i < numberOfPatients; i++) {
            Patient patient = new Patient(i + 1, waitingRoom);
            patientThreads[i] = new Thread(patient);
        }

        for (Thread doctorThread : doctorThreads) {
            doctorThread.start();
        }

        for (Thread patientThread : patientThreads) {
            patientThread.start();
        }

        for (int i = 0; i < numberOfPatients; i++) {
            completedPatients.acquire();
        }

        waitingRoom.close();

        for (Thread patientThread : patientThreads) {
            patientThread.join();
        }

        for (Thread doctorThread : doctorThreads) {
            doctorThread.join();
        }

        scanner.close();
    }
}