package gui;

import logic.Individual;
import logic.Population;

import javax.swing.*;
import java.awt.event.*;

public class PenaltiesWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSlider authorPen;
    private JSlider genrePen;
    private JSlider datePen;
    private JSlider heightPen;
    private JTextField maxNumPens;

    public PenaltiesWindow() {

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
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

        maxNumPens.setText(String.valueOf(Individual.getMaxNumPens()));
        authorPen.setValue((int) (Individual.getAuthorPenValue()*100));
        datePen.setValue((int) (Individual.getDatePenValue()*100));
        heightPen.setValue((int) (Individual.getHeightPenValue()*100));
        genrePen.setValue((int) (Individual.getGenrePenValue()*100));

        this.pack();
        this.setModal(true);
    }

    private void onOK() {
        // add your code here
        int numPens = Integer.parseInt(maxNumPens.getText());
        double authorPenValue = (double) authorPen.getValue()/ 100.0;
        double datePenValue = (double) datePen.getValue()/ 100.0;
        double genrePenValue = (double) genrePen.getValue()/ 100.0;
        double heightPenValue = (double) heightPen.getValue()/ 100.0;

        if(authorPenValue + datePenValue + genrePenValue + heightPenValue >= 1) {
            JOptionPane.showMessageDialog(null, "The Sum of all Penalization Values must be less than 100%!");
        }
        else{
            Individual.changePenaltiesValues(numPens, authorPenValue, datePenValue, heightPenValue, genrePenValue);
            dispose();
        }
//        Individual.changePenaltiesValues();

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
