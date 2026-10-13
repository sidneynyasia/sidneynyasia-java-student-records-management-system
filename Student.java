public class Student {
    public final String firstName, lastName, id, major, birthdate, address;
    public final double gpa;

    public Student(String firstName, String lastName, String id, String major,
                   String birthdate, String address, double gpa) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.major = major;
        this.birthdate = birthdate;
        this.address = address;
        this.gpa = gpa;
    }

    public String toFileString() {
        return String.join("|", firstName, lastName, id, major, birthdate,
                address, String.valueOf(gpa));
    }

    public static Student fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        // Support the original eight-column format, but deliberately discard SSN.
        if (p.length == 8) {
            return new Student(p[0], p[1], p[2], p[4], p[5], p[6], Double.parseDouble(p[7]));
        }
        if (p.length == 7) {
            return new Student(p[0], p[1], p[2], p[3], p[4], p[5], Double.parseDouble(p[6]));
        }
        throw new IllegalArgumentException("Invalid record format");
    }
}
