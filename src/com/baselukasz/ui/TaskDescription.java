package com.baselukasz.ui;

import com.baselukasz.core.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskDescription extends JDialog implements ActionListener {

    private final JPanel panelDescription;
    private JLabel lblTitle;
    private JTextArea txtrDescription;
    private JButton btnClose;

    /**
     * Create the dialog
     */
    public TaskDescription(Task task){

        setBounds(100,100,450,300);
        getContentPane().setLayout(new BorderLayout());
        panelDescription = new JPanel();
        panelDescription.setBorder(new EmptyBorder(5,5,5,5));
        getContentPane().add(panelDescription, BorderLayout.CENTER);
        panelDescription.setLayout(new BorderLayout(0,0));
        {
            lblTitle = new JLabel(task.getTitle());
            panelDescription.add(lblTitle, BorderLayout.NORTH);
        }
        {
            txtrDescription = new JTextArea(task.getDescription());
            txtrDescription.setLineWrap(true);
            panelDescription.add(txtrDescription, BorderLayout.CENTER);
        }
        {
            JPanel panelPrzyciskow = new JPanel();
            panelPrzyciskow.setLayout(new FlowLayout(FlowLayout.RIGHT));
            panelDescription.add(panelPrzyciskow, BorderLayout.SOUTH);
            {
                btnClose = new JButton("Zamknij");
                btnClose.setActionCommand("Zamknij");
                btnClose.addActionListener(this);
                panelPrzyciskow.add(btnClose);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Zamknij") {
            setVisible(false);
            dispose();
        }
    }
}
