package com.baselukasz.ui;

import com.baselukasz.core.User;
import com.baselukasz.dao.DBConnection;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.jasypt.digest.config.SimpleDigesterConfig;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPanel extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private DBConnection con;
    private User user;

    private JPanel userPanel;

    private JTextField username;
    private JPasswordField password;

    private JButton confirm;
    private JButton cancel;


    public LoginPanel(DBConnection conInit){
        con = conInit;

        // Reakcja na zamkniecie okna
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                con.disconnect(true, null);
                System.exit(0);
            }
        });
        setTitle("Panel logowania");
        setBounds(100,100,450,168);

        // Utworzenie layoutu
        getContentPane().setLayout(new BorderLayout());
        userPanel = new JPanel();
        userPanel.setBorder(new EmptyBorder(5,5,5,5));
        getContentPane().add(userPanel, BorderLayout.CENTER);
        userPanel.setLayout(new FormLayout(new ColumnSpec[]{
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.DEFAULT_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow")
            },
            new RowSpec[]{
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
            }
            ));

        // Dane uzytkownika
        JLabel lblUser = new JLabel("Nazwa");
        // dodanie do pola JPanel etykietki z prawej
        userPanel.add(lblUser, "2, 2, right, default");
        username = new JTextField();
        // dodanie do pola JPanel pola tekstowego wypełniającego komórkę
        userPanel.add(username, "4, 2, fill, default");

        // Dane haslo
        JLabel lblPassword = new JLabel("Haslo");
        // dodanie do pola JPanel etykietki z prawej
        userPanel.add(lblPassword, "2, 4, right, default");
        password = new JPasswordField();
        // dodanie do pola JPanel pola tekstowego wypełniającego komórkę
        userPanel.add(password, "4, 4, fill, default");

        // Panel z przyciskami
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        // Przycisk potwierdzajacy wprowadzone dane
        confirm = new JButton("Potwierdz");
        confirm.addActionListener(this);
        confirm.setActionCommand("Potwierdz");
        buttonPane.add(confirm);

        // Przycisk zamykajacy okno
        cancel = new JButton("Anuluj");
        cancel.addActionListener(this);
        cancel.setActionCommand("Anuluj");
        buttonPane.add(cancel);
    }

    public void setCon(DBConnection conInit){
        con = conInit;
    }

    public boolean checkLoginData() {

        SimpleDigesterConfig md5Config = new SimpleDigesterConfig();
        md5Config.setAlgorithm("MD5");
        md5Config.setIterations(1);
        md5Config.setSaltSizeBytes(0);

        ConfigurablePasswordEncryptor md5Encryptor = new ConfigurablePasswordEncryptor();
        md5Encryptor.setConfig(md5Config);
        md5Encryptor.setStringOutputType("hexadecimal");

        String encryptedPassword = md5Encryptor.encryptPassword( new String(password.getPassword()));

        //String Query = "SELECT * FROM Users WHERE Users.name = '"+username.getText()+"' AND Users.password = '"+encryptedPassword+"'";
        String Query = "SELECT * FROM Users WHERE Users.name = '"+username.getText()+"'";

        ResultSet rs = con.load(Query);
        if(rs != null){
            try{
                if(rs.next()) {
                    int id = rs.getInt("id");
                    String nazwa = rs.getString("name");
                    String haslo = rs.getString("password");
                    user = new User(id, nazwa, haslo);
                    con.destroyRS(rs);
                    return true;
                }
                else
                {
                    con.destroyRS(rs);
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("SprawdzDane: Problem z przetworzeniem danych");
                System.out.println("SQLException: " + e.getMessage());
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Vendor Error: " + e.getErrorCode());
                return false;
            }
        }
        else{
            return false;
        }
    }

    private void login() {
        // Ukryj okno
        setVisible(false);
        dispose();

        // Otworz okienko glowne aplikacji
        TaskList ts = new TaskList(con, user);
        ts.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "Potwierdz"){
            if(checkLoginData()){
                System.out.println("sprawdzDane() == true");
                login();
            }
            else{
                System.out.println("sprawdzDane() == false");
                JOptionPane.showMessageDialog(LoginPanel.this, "Bledne dane uzytkownika", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if(e.getActionCommand() == "Anuluj"){
            con.disconnect(true, null);
            System.exit(0);
        }
    }
}
