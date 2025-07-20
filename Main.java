import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Nice modern UI
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerUI ui = new ExpenseTrackerUI();
            ui.setVisible(true);
        });
    }
}
