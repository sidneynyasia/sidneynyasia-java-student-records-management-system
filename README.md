# Student Records Administration Dashboard

A Java Swing desktop application for managing fictional university student records. Built as a portfolio project demonstrating object-oriented programming, file I/O, input validation, CRUD operations, and GUI development.

## Features
- Add, view, update, and delete student records
- Search by student ID, first name, last name, or major
- Sortable student directory table
- Summary metrics: student count, average GPA, and number of majors
- Duplicate ID prevention, GPA/date validation, and deletion confirmation
- Persistent local storage in `students.txt` (no database server required)

## Run
Requires **Java 11 or newer**.

```bash
javac Main.java Student.java StudentDatabase.java StudentGUI.java
java Main
```

Run the commands from this folder. Records are read from and written to `students.txt` in the working directory.

## Files
- `Main.java` — launches the application
- `Student.java` — student record model and file serialization
- `StudentDatabase.java` — local storage and CRUD operations
- `StudentGUI.java` — Swing dashboard interface and event handling
- `students.txt` — fictional sample records

## Data privacy
All included student records are fictional. The public version does not store Social Security numbers. The loader can read the earlier eight-field format and discards its SSN field; the next save writes the new seven-field format. **Do not upload real student records or sensitive information.**

## Limitations
This is a local desktop learning project, not a production student information system. The plain-text file is not encrypted, and the app has no authentication or multi-user synchronization.
