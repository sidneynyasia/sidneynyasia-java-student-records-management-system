import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class StudentDatabase {
    private final Path file;
    private final List<Student> students = new ArrayList<>();

    public StudentDatabase(Path file) throws IOException {
        this.file = file;
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            try {
                Student s = Student.fromFileString(line);
                if (findById(s.id) == null) students.add(s);
            } catch (IllegalArgumentException ex) {
                throw new IOException("Invalid student record in " + file.getFileName(), ex);
            }
        }
    }

    public List<Student> all() { return Collections.unmodifiableList(students); }

    public Student findById(String id) {
        for (Student s : students) if (s.id.equalsIgnoreCase(id)) return s;
        return null;
    }

    public void add(Student student) throws IOException {
        if (findById(student.id) != null) throw new IllegalArgumentException("Student ID already exists.");
        students.add(student);
        try { save(); } catch (IOException ex) { students.remove(student); throw ex; }
    }

    public void update(String originalId, Student replacement) throws IOException {
        Student old = findById(originalId);
        if (old == null) throw new IllegalArgumentException("Student not found.");
        Student collision = findById(replacement.id);
        if (collision != null && collision != old) throw new IllegalArgumentException("Student ID already exists.");
        int index = students.indexOf(old);
        students.set(index, replacement);
        try { save(); } catch (IOException ex) { students.set(index, old); throw ex; }
    }

    public void delete(String id) throws IOException {
        Student old = findById(id);
        if (old == null) throw new IllegalArgumentException("Student not found.");
        int index = students.indexOf(old);
        students.remove(index);
        try { save(); } catch (IOException ex) { students.add(index, old); throw ex; }
    }

    private void save() throws IOException {
        Path absolute = file.toAbsolutePath();
        Path temp = Files.createTempFile(absolute.getParent(), "students-", ".tmp");
        try {
            List<String> lines = new ArrayList<>();
            for (Student s : students) lines.add(s.toFileString());
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try { Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
}
