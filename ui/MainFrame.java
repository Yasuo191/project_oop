package ui;

import model.*;
import service.EmployeeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    JTextField txtId, txtName, txtSalary, txtHours, txtSearch;
    JComboBox<String> cbType;
    JTable table;

    JLabel lblSalary;
    JLabel lblExtra;

    EmployeeManager manager = new EmployeeManager();

    DefaultTableModel tableModel;
    TableRowSorter<DefaultTableModel> sorter;

    public MainFrame(){

        setTitle("Employee Management");
        setSize(900,550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel(new BorderLayout(10,10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Employee Info"));

        JPanel formPanel = new JPanel(new GridLayout(6,2,8,8));

        formPanel.add(new JLabel("ID"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Name"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Type"));
        cbType = new JComboBox<>(new String[]{
                "Fulltime","PartTime","Manager"
        });
        formPanel.add(cbType);

        lblSalary = new JLabel("Salary");
        formPanel.add(lblSalary);

        txtSalary = new JTextField();
        formPanel.add(txtSalary);

        lblExtra = new JLabel("Extra");
        formPanel.add(lblExtra);

        txtHours = new JTextField();
        formPanel.add(txtHours);

        leftPanel.add(formPanel,BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new GridLayout(2,2,10,10));

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");
        JButton btnSort = new JButton("Sort");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnSort);

        leftPanel.add(btnPanel,BorderLayout.SOUTH);

        leftPanel.setPreferredSize(new Dimension(300,0));
        add(leftPanel,BorderLayout.WEST);

        // ===== TABLE =====
        String[] columns = {"ID","Name","Type","Salary"};

        tableModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(180,220,255));

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee List"));

        add(scrollPane,BorderLayout.CENTER);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search"));

        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        JButton btnShowAll = new JButton("Show All");
        searchPanel.add(btnShowAll);

        add(searchPanel,BorderLayout.SOUTH);

        // ===== EVENTS =====

        btnAdd.addActionListener(e -> addEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnShowAll.addActionListener(e -> loadTable());

        btnSort.addActionListener(e -> showSortMenu());

        cbType.addActionListener(e -> updateFormByType());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e){ searchEmployee(); }
            public void removeUpdate(DocumentEvent e){ searchEmployee(); }
            public void changedUpdate(DocumentEvent e){ searchEmployee(); }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    fillForm();
                }
            }
        });

        manager.loadFromFile();
        loadTable();
        updateFormByType();
    }

    // ===== UPDATE FORM =====
    private void updateFormByType(){

        String type = cbType.getSelectedItem().toString();

        if(type.equals("Fulltime")){

            lblSalary.setText("Salary");
            lblExtra.setVisible(false);
            txtHours.setVisible(false);

        }
        else if(type.equals("PartTime")){

            lblSalary.setText("Rate");

            lblExtra.setText("Hours");
            lblExtra.setVisible(true);
            txtHours.setVisible(true);

        }
        else{

            lblSalary.setText("Salary");

            lblExtra.setText("Bonus");
            lblExtra.setVisible(true);
            txtHours.setVisible(true);

        }

        revalidate();
        repaint();
    }

    // ===== ADD =====
    private void addEmployee(){

        String id = txtId.getText();
        String name = txtName.getText();
        String type = cbType.getSelectedItem().toString();

        Employee emp;

        try{

            if(type.equals("Fulltime")){

                double salary = Double.parseDouble(txtSalary.getText());
                emp = new FulltimeEmployee(id,name,salary);

            }
            else if(type.equals("PartTime")){

                int hours = Integer.parseInt(txtHours.getText());
                double rate = Double.parseDouble(txtSalary.getText());

                emp = new PartTimeEmployee(id,name,hours,rate);

            }
            else{

                double salary = Double.parseDouble(txtSalary.getText());
                double bonus = Double.parseDouble(txtHours.getText());

                emp = new Manager(id,name,salary,bonus);

            }

        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid number input");
            return;
        }

        manager.addEmployee(emp);
        manager.saveToFile();

        loadTable();
        clearForm();
    }

    // ===== DELETE =====
    private void deleteEmployee(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select employee first");
            return;
        }

        row = table.convertRowIndexToModel(row);

        manager.deleteEmployee(row);
        manager.saveToFile();

        loadTable();
        clearForm();
    }

    // ===== UPDATE =====
    private void updateEmployee(){

        int row = table.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select employee first");
            return;
        }

        row = table.convertRowIndexToModel(row);

        String id = txtId.getText();
        String name = txtName.getText();
        String type = cbType.getSelectedItem().toString();

        Employee emp;

        try{

            if(type.equals("Fulltime")){

                double salary = Double.parseDouble(txtSalary.getText());
                emp = new FulltimeEmployee(id,name,salary);

            }
            else if(type.equals("PartTime")){

                int hours = Integer.parseInt(txtHours.getText());
                double rate = Double.parseDouble(txtSalary.getText());

                emp = new PartTimeEmployee(id,name,hours,rate);

            }
            else{

                double salary = Double.parseDouble(txtSalary.getText());
                double bonus = Double.parseDouble(txtHours.getText());

                emp = new Manager(id,name,salary,bonus);

            }

        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid number input");
            return;
        }

        manager.getEmployees().set(row, emp);

        manager.saveToFile();
        loadTable();
        clearForm();
    }

    // ===== SORT MENU =====
    private void showSortMenu(){

        String[] options = {
                "Sort ID Ascending",
                "Sort ID Descending",
                "Sort Salary Ascending",
                "Sort Salary Descending"
        };

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose sort type",
                "Sort Employees",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch(choice){

            case 0: manager.sortIdAsc(); break;
            case 1: manager.sortIdDesc(); break;
            case 2: manager.sortSalaryAsc(); break;
            case 3: manager.sortSalaryDesc(); break;

        }

        manager.saveToFile();
        loadTable();
    }

    // ===== SEARCH =====
    private void searchEmployee(){

        String keyword = txtSearch.getText();

        tableModel.setRowCount(0);

        for(Employee e : manager.searchByName(keyword)){

            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    e.getClass().getSimpleName(),
                    e.calculateSalary()
            });

        }
    }

    // ===== LOAD TABLE =====
    private void loadTable(){

        tableModel.setRowCount(0);

        for(Employee e : manager.getEmployees()){

            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    e.getClass().getSimpleName(),
                    e.calculateSalary()
            });

        }
    }

    // ===== FILL FORM =====
    private void fillForm(){

        int row = table.getSelectedRow();
        if(row==-1) return;

        row = table.convertRowIndexToModel(row);

        Employee e = manager.getEmployees().get(row);

        txtId.setText(e.getId());
        txtName.setText(e.getName());

        if(e instanceof FulltimeEmployee){

            cbType.setSelectedItem("Fulltime");
            txtSalary.setText(String.valueOf(((FulltimeEmployee)e).getSalary()));
            txtHours.setText("");

        }
        else if(e instanceof PartTimeEmployee){

            cbType.setSelectedItem("PartTime");

            PartTimeEmployee p = (PartTimeEmployee)e;

            txtSalary.setText(String.valueOf(p.getRate()));
            txtHours.setText(String.valueOf(p.getHours()));

        }
        else if(e instanceof Manager){

            cbType.setSelectedItem("Manager");

            Manager m = (Manager)e;

            txtSalary.setText(String.valueOf(m.getSalary()));
            txtHours.setText(String.valueOf(m.getBonus()));

        }

        updateFormByType();
    }

    // ===== CLEAR =====
    private void clearForm(){

        txtId.setText("");
        txtName.setText("");
        txtSalary.setText("");
        txtHours.setText("");
    }
}