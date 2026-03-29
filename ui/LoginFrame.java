package ui;
import javax.swing.*;
import java.awt.*;
public class LoginFrame extends JFrame {
    JTextField txtUser;
    JPasswordField txtPass;
    public LoginFrame(){
        setTitle("Login");
        setSize(350,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,2,10,10));
        add(new JLabel("Username:"));
        txtUser = new JTextField();
        add(txtUser);
        add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        add(txtPass);
        JButton btnLogin = new JButton("Login");
        add(new JLabel());
        add(btnLogin);
        btnLogin.addActionListener(e -> login());
    }

    private void login(){
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        //  VALIDATE
        if(user.isEmpty()){
            JOptionPane.showMessageDialog(this,"Username cannot be empty");
            return;
        }

        if(pass.length() < 6){
            JOptionPane.showMessageDialog(this,"Password must be at least 6 characters");
            return;
        }

        // LOGIN SUCCESS
        JOptionPane.showMessageDialog(this,"Login successful!");

        new MainFrame().setVisible(true);
        this.dispose();
    }
}