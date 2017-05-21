package gui;

import logic.Population;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AppWindow extends JDialog{
    private JPanel contentPane;
    private JPanel label_numiter;
    private JButton runButton;
    private JButton closeButton;
    private JSlider crossover;
    private JSlider mutation;
    private JSlider elitism;
    private JSlider time_exec;
    private JSlider num_iter;
    private JCheckBox iterEnd;
    private JCheckBox timeEnd;
    private JTextArea console;
    private JTextField book_dataset;
    private JTextField shelves_dataset;
    private JButton advancedSettingsButton;
    private JTabbedPane tabbedPane1;
    private JSlider slider1;

    private Population pop;


    public AppWindow() {



        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(runButton);


        //buttons

        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        time_exec.addMouseListener(new MouseAdapter() {
        });
        time_exec.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

            }
        });

//        contentPane.add(new JScrollPane(console));
        MessageConsole mc = new MessageConsole(console);
        mc.redirectOut();
        mc.redirectErr(Color.RED, null);
        mc.setMessageLines(100);

        advancedSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

//                contentPane.setVisible(false);
                new PenaltiesWindow().setVisible(true);
//                frame.add(panel);
            }
        });
    }

    private void run() {
        // add your code here
        pop = new Population();

        Population.setAlgorithmParameters(elitism.getValue(), 200, num_iter.getValue(),
                (double) mutation.getValue() / 100.0, (double) crossover.getValue() / 100.0,
                iterEnd.isSelected(), timeEnd.isSelected());

        pop.initiatePopulation(book_dataset.getText(), shelves_dataset.getText());
        new Thread(() -> pop.evolve()).start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AppWindow dialog = new AppWindow();
        dialog.pack();
        dialog.setVisible(true);
//        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
