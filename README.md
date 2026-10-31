# Hospital Emergency Room Simulation

Hospital Emergency Room Simulation is a Java-based concurrent system that models how patients and doctors interact in a hospital emergency department.

The project applies the **Producer-Consumer** pattern and **Bounded Buffer** concept to manage patient flow through a limited-capacity waiting room. Each patient and doctor runs in a separate thread, allowing multiple operations to happen concurrently.

A custom Semaphore implementation handles synchronization and resource access without relying on Java's built-in Semaphore class.

The simulation includes:

- Patient Management: Patients arrive and enter the waiting room or wait outside when it is full.

- Doctor Management: Doctors retrieve patients from the waiting room and attempt to acquire a treatment room.

- Resource Control: Exactly two treatment rooms are available, managed through semaphore permits.

- Synchronization: Custom counting and binary semaphores coordinate shared resources and prevent race conditions.

- Timed Acquisition: Doctors have up to 3 seconds to acquire a treatment room before returning the patient to the waiting line.

- Concurrent Execution: Multiple patient and doctor threads operate simultaneously.