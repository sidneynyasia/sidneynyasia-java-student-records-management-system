import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class StudentGUI {
    private static final Color NAVY = new Color(21, 43, 77);
    private static final Color BLUE = new Color(35, 99, 185);
    private static final Color BG = new Color(245, 248, 252);
    private static final Color MUTED = new Color(92, 105, 123);
    private final StudentDatabase db;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField firstName, lastName, id, major, birthdate, address, gpa, search;
    private JLabel totalLabel, averageLabel, majorsLabel, statusLabel;
    private String selectedId;

    public StudentGUI(StudentDatabase db) { this.db = db; }

    public void createGUI() {
        frame = new JFrame("Student Records | Administration Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1050, 680));
        frame.setSize(1200, 780);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(header(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(24, 26, 20, 26));
        content.add(stats(), BorderLayout.NORTH);

        JPanel workspace = new JPanel(new BorderLayout(18, 0));
        workspace.setOpaque(false);
        workspace.add(tablePanel(), BorderLayout.CENTER);
        JPanel form = formPanel();
        form.setPreferredSize(new Dimension(350, 500));
        workspace.add(form, BorderLayout.EAST);
        content.add(workspace, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready  •  Records are saved locally to students.txt");
        statusLabel.setForeground(MUTED);
        statusLabel.setBorder(new EmptyBorder(0, 26, 15, 0));
        root.add(statusLabel, BorderLayout.SOUTH);
        frame.setContentPane(root);
        refresh();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(NAVY);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));
        JLabel title = new JLabel("STUDENT RECORDS  /  ADMINISTRATION");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("University Records Management  •  Java Swing");
        subtitle.setForeground(new Color(190, 207, 228));
        JPanel stack = new JPanel(new GridLayout(2, 1, 0, 5));
        stack.setOpaque(false);
        stack.add(title); stack.add(subtitle);
        panel.add(stack, BorderLayout.WEST);
        return panel;
    }

    private JPanel stats() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setOpaque(false);
        totalLabel = statCard(panel, "TOTAL STUDENTS");
        averageLabel = statCard(panel, "AVERAGE GPA");
        majorsLabel = statCard(panel, "MAJORS REPRESENTED");
        return panel;
    }

    private JLabel statCard(JPanel parent, String caption) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(224, 231, 240)), new EmptyBorder(16, 20, 16, 20)));
        JLabel top = new JLabel(caption);
        top.setForeground(MUTED);
        top.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel value = new JLabel("—");
        value.setForeground(NAVY);
        value.setFont(new Font("SansSerif", Font.BOLD, 30));
        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        parent.add(card);
        return value;
    }

    private JPanel tablePanel() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 14));
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        JLabel heading = new JLabel("Student Directory");
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));
        heading.setForeground(NAVY);
        top.add(heading, BorderLayout.WEST);
        search = new JTextField();
        search.setToolTipText("Search by student ID, first name, last name, or major");
        search.putClientProperty("JTextField.placeholderText", "Search students...");
        search.setPreferredSize(new Dimension(255, 36));
        top.add(search, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Student ID", "First Name", "Last Name", "Major", "GPA"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(234, 240, 249));
        table.getTableHeader().setForeground(NAVY);
        table.setSelectionBackground(new Color(216, 231, 251));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(237, 241, 247));
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String q = search.getText().trim();
                sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q), 0, 1, 2, 3));
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            String key = String.valueOf(model.getValueAt(row, 0));
            Student s = db.findById(key);
            if (s != null) populate(s);
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(224, 231, 240)));
        card.add(scroll, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton all = button("View All Students", false);
        all.addActionListener(e -> { search.setText(""); refresh(); message("Showing all student records."); });
        JButton byId = button("Search by ID", false);
        byId.addActionListener(e -> findFromField(id.getText().trim()));
        JButton byFirst = button("Search by First Name", false);
        byFirst.addActionListener(e -> search.setText(firstName.getText().trim()));
        JButton byLast = button("Search by Last Name", false);
        byLast.addActionListener(e -> search.setText(lastName.getText().trim()));
        actions.add(all); actions.add(byId); actions.add(byFirst); actions.add(byLast);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel formPanel() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 12));
        JLabel title = new JLabel("Student Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 19));
        title.setForeground(NAVY);
        card.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(7, 1, 0, 8));
        fields.setOpaque(false);
        firstName = field(fields, "First Name");
        lastName = field(fields, "Last Name");
        id = field(fields, "Student ID");
        major = field(fields, "Major");
        birthdate = field(fields, "Birthdate (MM/DD/YYYY)");
        address = field(fields, "Address");
        gpa = field(fields, "GPA (0.00–4.00)");
        card.add(fields, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new GridLayout(2, 2, 9, 9));
        buttons.setOpaque(false);
        JButton add = button("Add Student", true);
        JButton update = button("Update Student", false);
        JButton delete = button("Delete Student", false);
        JButton clear = button("Clear Fields", false);
        add.addActionListener(e -> runAction(() -> { db.add(readForm()); refresh(); clearForm(); message("Student added successfully."); }));
        update.addActionListener(e -> runAction(() -> {
            if (selectedId == null) throw new IllegalArgumentException("Select a student from the table before updating.");
            db.update(selectedId, readForm()); refresh(); clearForm(); message("Student updated successfully.");
        }));
        delete.addActionListener(e -> {
            if (selectedId == null) { error("Select a student from the table before deleting."); return; }
            if (JOptionPane.showConfirmDialog(frame, "Delete student " + selectedId + "?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                runAction(() -> { db.delete(selectedId); refresh(); clearForm(); message("Student deleted."); });
            }
        });
        clear.addActionListener(e -> clearForm());
        buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(clear);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JTextField field(JPanel parent, String name) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 3));
        wrapper.setOpaque(false);
        JLabel label = new JLabel(name);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        JTextField input = new JTextField();
        input.setFont(new Font("SansSerif", Font.PLAIN, 13));
        input.setBorder(new CompoundBorder(new LineBorder(new Color(212, 221, 233)), new EmptyBorder(5, 9, 5, 9)));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(input, BorderLayout.CENTER);
        parent.add(wrapper);
        return input;
    }

    private JPanel card() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(224, 231, 240)), new EmptyBorder(20, 20, 20, 20)));
        return card;
    }

    private JButton button(String label, boolean primary) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBackground(primary ? BLUE : new Color(233, 240, 249));
        b.setForeground(primary ? Color.WHITE : NAVY);
        b.setBorder(new EmptyBorder(11, 10, 11, 10));
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private Student readForm() {
        String[] values = {firstName.getText().trim(), lastName.getText().trim(), id.getText().trim(),
                major.getText().trim(), birthdate.getText().trim(), address.getText().trim(), gpa.getText().trim()};
        for (String value : values) {
            if (value.isEmpty()) throw new IllegalArgumentException("Please complete all student fields.");
            if (value.contains("|") || value.contains("\n") || value.contains("\r"))
                throw new IllegalArgumentException("Fields cannot contain | or line breaks.");
        }
        double score;
        try { score = Double.parseDouble(values[6]); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("GPA must be a number between 0 and 4."); }
        if (!Double.isFinite(score) || score < 0 || score > 4) throw new IllegalArgumentException("GPA must be between 0 and 4.");
        try {
            java.time.LocalDate.parse(values[4], java.time.format.DateTimeFormatter.ofPattern("MM/dd/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT));
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException("Birthdate must be a valid date in MM/DD/YYYY format.");
        }
        return new Student(values[0], values[1], values[2], values[3], values[4], values[5], score);
    }

    private void refresh() {
        model.setRowCount(0);
        Set<String> majors = new HashSet<>();
        double sum = 0;
        for (Student s : db.all()) {
            model.addRow(new Object[]{s.id, s.firstName, s.lastName, s.major, String.format(Locale.US, "%.2f", s.gpa)});
            majors.add(s.major.toLowerCase(Locale.ROOT));
            sum += s.gpa;
        }
        totalLabel.setText(String.valueOf(db.all().size()));
        averageLabel.setText(db.all().isEmpty() ? "—" : String.format(Locale.US, "%.2f", sum / db.all().size()));
        majorsLabel.setText(String.valueOf(majors.size()));
    }

    private void populate(Student s) {
        selectedId = s.id;
        firstName.setText(s.firstName); lastName.setText(s.lastName); id.setText(s.id);
        major.setText(s.major); birthdate.setText(s.birthdate); address.setText(s.address);
        gpa.setText(String.valueOf(s.gpa));
    }

    private void clearForm() {
        selectedId = null;
        table.clearSelection();
        for (JTextField field : new JTextField[]{firstName, lastName, id, major, birthdate, address, gpa}) field.setText("");
    }

    private void findFromField(String key) {
        if (key.isEmpty()) { error("Enter a Student ID first."); return; }
        Student s = db.findById(key);
        if (s == null) { error("No student found with that ID."); return; }
        search.setText("");
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, 0).equals(s.id)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
                break;
            }
        }
        populate(s);
        message("Student found: " + s.firstName + " " + s.lastName);
    }

    private interface Action { void execute() throws IOException; }
    private void runAction(Action action) {
        try { action.execute(); }
        catch (IllegalArgumentException | IOException ex) { error(ex.getMessage()); }
    }
    private void error(String message) { JOptionPane.showMessageDialog(frame, message, "Student Records", JOptionPane.ERROR_MESSAGE); }
    private void message(String message) { statusLabel.setText(message); }
}
