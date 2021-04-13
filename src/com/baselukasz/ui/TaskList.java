package com.baselukasz.ui;

import com.baselukasz.core.Employee;
import com.baselukasz.core.Task;
import com.baselukasz.core.User;
import com.baselukasz.dao.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TaskList extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;

    private DBConnection con;
    private User user;
    private Employee employee;

    private final JPanel taskListPanel;
    // private final JScrollPane panelZadania;

    private JTable tasksTable;
    private JLabel lblLogged;
    private JLabel loggedUser;
    private JLabel lblFirstName;
    private JLabel lblLastName;
    private JLabel lblEmail;
    private JLabel lblPosition;

    private JButton btnDone;
    private JButton btnDescription;
    private JButton btnLogout;

    public static void main(String[] args) {
        DBConnection komInit = DBConnection.getInstance();
        loginWindow( komInit);
    }

    public TaskList(DBConnection c, User u){
        con = c;
        user = u;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                con.disconnect(true, null);
                System.exit(0);
            }
        });

        setTitle("ListaZadan");

        setBounds(100, 100, 584, 300);
        getContentPane().setLayout(new BorderLayout());

        taskListPanel = new JPanel();
        taskListPanel.setBorder(new EmptyBorder(5,5,5,5));
        taskListPanel.setLayout(new BorderLayout());
        setContentPane(taskListPanel);

        JPanel panelGora = new JPanel();
        taskListPanel.add(panelGora, BorderLayout.NORTH);
        panelGora.setLayout(new BorderLayout());

        // Panel z nazwa uzytkownika
        JPanel panelUzytkownik = new JPanel();
        panelGora.add(panelUzytkownik, BorderLayout.NORTH);

        lblLogged = new JLabel("Zalogowany: ");
        panelUzytkownik.add(lblLogged);

        loggedUser = new JLabel(user.getUsername());
        panelUzytkownik.add(loggedUser);

        // Panel z danymi pracownika
        JPanel panelPracownika = new JPanel();
        panelGora.add(panelPracownika);

        lblFirstName = new JLabel();
        panelPracownika.add(lblFirstName);

        lblLastName = new JLabel();
        panelPracownika.add(lblLastName);

        lblEmail = new JLabel();
        panelPracownika.add(lblEmail);

        lblPosition = new JLabel();
        panelPracownika.add(lblPosition);

        // Panel z zadaniami
        JScrollPane panelZadania = new JScrollPane();
        taskListPanel.add(panelZadania, BorderLayout.CENTER);

        tasksTable = new JTable();
        panelZadania.setViewportView(tasksTable);

        // Dane do widoku
        loadEmployeeData();
        updateView();

        // Panel opcji
        JPanel panelMenu = new JPanel();
        taskListPanel.add(panelMenu, BorderLayout.SOUTH);

        btnDone = new JButton("Zrobione");
        btnDone.addActionListener((ActionListener) this);
        panelMenu.add(btnDone);

        btnDescription = new JButton("Opis");
        btnDescription.addActionListener((ActionListener) this);
        panelMenu.add(btnDescription);

        btnLogout = new JButton("Zrobione");
        btnLogout.addActionListener((ActionListener) this);
        btnLogout.setActionCommand("Wyloguj");
        panelMenu.add(btnLogout);
    }

    private TaskList() {
        this.taskListPanel = new JPanel();
    }

    static void loginWindow(DBConnection conInit){
        LoginPanel log = new LoginPanel(conInit);

        log.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        log.setVisible(true);
    }

    public void loadEmployeeData(){

        String QueryPrac = "SELECT * FROM `Employees` WHERE user_id= "+user.getId();
        ResultSet rsPrac = con.load(QueryPrac);
        if(rsPrac != null) {
            try {
                if(rsPrac.next()){
                    int id = rsPrac.getInt("id");
                    String imie = rsPrac.getString("first_name");
                    String nazwisko = rsPrac.getString("last_name");
                    String email = rsPrac.getString("email");
                    String position = rsPrac.getString("position");
                    employee = new Employee(id, imie, nazwisko, email, position);
                    con.destroyRS(rsPrac);
                }
                else{
                    con.destroyRS(rsPrac);
                }
            } catch (SQLException e) {
                System.out.println("ListaZadanFirmy: Problem z przetworzeniem danych");
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Vendor Error: " + e.getErrorCode());
                return ;
            }
        }
        // Wczytaj dane z tabeli zadania
        String QueryZad = "SELECT * FROM `Tasks` WHERE employee_id= " +employee.getId();
        ResultSet rsZad = con.load(QueryZad);
        if(rsZad != null) {
            try {
                ArrayList<Task> listazad = new ArrayList<>();
                while (rsZad.next()){
                    int id = rsZad.getInt("id");
                    String tytul = rsZad.getString("title");
                    String opis = rsZad.getString("description");
                    boolean status = rsZad.getBoolean("status");
                    listazad.add(new Task(id, tytul, opis, status));
                }
                employee.setTasks(listazad);
                con.destroyRS(rsZad);

            } catch (SQLException e) {
                System.out.println("ListaZadanFirmy: Problem z przetworzeniem danych");
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Vendor Error: " + e.getErrorCode());
                return ;
            }
        }
    }

    public void updateView() {

        lblFirstName.setText(employee.getFirstName());
        lblLastName.setText(employee.getLastName());
        lblEmail.setText(employee.getEmail());
        lblPosition.setText(employee.getPosition());

        TaskTableModel model = new TaskTableModel(employee.getTasks());
        tasksTable.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e ){
        if(e.getActionCommand() == "Wyloguj"){
            loginWindow(con);
            setVisible(false);
            dispose();
        } else if(e.getActionCommand() == "Opis"){

            // Wybrany wiersz
            int wiersz = tasksTable.getSelectedRow();

            // Jezeli nie zostal wybrany
            if (wiersz < 0){
                JOptionPane.showMessageDialog(TaskList.this, "Wybierz zadanie", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Wybierz zadanie
            Task task = (Task) tasksTable.getValueAt(wiersz, TaskTableModel.OBJECT_COL);

            TaskDescription opis = new TaskDescription(task);
            opis.setVisible(true);
        }
        else if(e.getActionCommand() == "Zrobione"){

            // Wybrany wiersz
            int wiersz = tasksTable.getSelectedRow();

            // Jezeli nie zostal wybrany
            if (wiersz < 0){
                JOptionPane.showMessageDialog(TaskList.this, "Wybierz zadanie", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Wybierz zadanie
            Task zad = (Task) tasksTable.getValueAt(wiersz, TaskTableModel.OBJECT_COL);

            int nowyStatus = (!zad.isStatus()) ? 1 : 0;

            String Query = "UPDATE `Tasks` SET `status` = "+nowyStatus+" WHERE Tasks.id = "+zad.getId()+";";

            if(con.update(Query)) {
                employee.getTasks().get(wiersz).setStatus( !zad.isStatus());
                updateView();
            }
            else {
                JOptionPane.showMessageDialog(TaskList.this, "Blad przy modyfikacji", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
}
