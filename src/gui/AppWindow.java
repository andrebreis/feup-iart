package gui;

import javax.swing.*;
import java.awt.event.*;

public class AppWindow extends JDialog{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel label_numiter;
    private JButton OKButton;
    private JButton cancelButton;
    private JSlider crossover;
    private JSlider mutation;
    private JSlider elitism;
    private JSlider time_exec;
    private JSlider num_iter;
    private JCheckBox enableCheckBox;
    private JCheckBox enableCheckBox1;
    private JTextArea console;
    private JTextField book_dataset;
    private JTextField shelves_dataset;


    public AppWindow() {



        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);



        elitism.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }
        });

        //buttons

        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
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

    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AppWindow dialog = new AppWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
