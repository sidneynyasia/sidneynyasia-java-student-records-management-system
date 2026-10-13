import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new StudentGUI(new StudentDatabase(Paths.get("students.txt"))).createGUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Could not open student records: " + ex.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
