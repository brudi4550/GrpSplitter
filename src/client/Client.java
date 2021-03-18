package client;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    private static final Model model = new Model();
    private static final JFrame frame = new JFrame("Students");
    private static final String textDefault = "1-6 mehrere Gruppen getrennt mit \",\"";
    private static final JPanel rootPanel = new JPanel(new BorderLayout());
    private static final JPanel panel = new JPanel(new FlowLayout());
    private static final JTextArea textArea = new JTextArea(20, 80);
    private static final JScrollPane scroll = new JScrollPane(textArea);
    private static final JTextField grp = new JFormattedTextField(textDefault);
    private static final JFileChooser fc = new JFileChooser();
    private static List<Integer> nrs;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::createGui);
    }

    private static void createGui() {
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(rootPanel);
        rootPanel.add(panel, BorderLayout.NORTH);
        rootPanel.add(scroll, BorderLayout.CENTER);
        JButton button = new JButton("Choose Folder");
        button.addActionListener(e -> {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            int ret = fc.showOpenDialog(frame);
            if (ret == JFileChooser.APPROVE_OPTION) model.setFolder(fc.getSelectedFile());
            updateText();
        });
        panel.add(button);
        grp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                grp.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (grp.getText().equals("")) grp.setText(textDefault);
            }
        });
        grp.addActionListener(l -> {
            String text = grp.getText();
            try {
                nrs = Arrays.stream(text.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                showError();
                return;
            }
            nrs.sort(Comparator.naturalOrder());
            if (nrs.stream().anyMatch(i -> i < 1 || i > 6)) {
                showError();
                nrs = null;
                return;
            }
            updateText();
        });
        panel.add(grp);
        frame.pack();
        frame.setVisible(true);
    }

    private static void showError() {
        JOptionPane.showMessageDialog(frame, "only numbers from 1-6", "input error", JOptionPane.ERROR_MESSAGE);
        grp.setText(textDefault);
        frame.requestFocusInWindow();
    }

    private static void updateText() {
        textArea.setText("");
        List<List<Path>> files = model.getFiles();
        int i = 1;
        if (nrs == null) {
            for (List<Path> group : files) {
                textArea.append("Gruppe " + i + " :\n");
                i++;
                for (Path path : group) {
                    File file = path.toFile();
                    textArea.append(file.getParentFile().getName()+file.getName() + "\n");
                }
            }
        } else {
            for (Integer nr : nrs) {
                List<Path> curr = files.get(nr-1);
                textArea.append("Gruppe " + nr + " :\n");
                for (Path path : curr) {
                    File file = path.toFile();
                    textArea.append(file.getParentFile().getName()+file.getName() + "\n");
                }
            }
        }
    }
}
