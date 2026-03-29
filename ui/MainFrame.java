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
import java.util.ArrayList;

public class MainFrame extends JFrame {
    JComboBox<String> cbSearchType;
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

        // ===== BUTTON =====
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

        add(new JScrollPane(table),BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search"));

        cbSearchType = new JComboBox<>(new String[]{"ID","Name"});
        searchPanel.add(cbSearchType);

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

        String id = txtId.getText().trim();
        String name = txtName.getText().trim();

        if(id.isEmpty() || name.isEmpty()){
            JOptionPane.showMessageDialog(this,"ID and Name cannot be empty");
            return;
        }

        Employee emp;

        try{
            String type = cbType.getSelectedItem().toString();

            if(type.equals("Fulltime")){
                emp = new FulltimeEmployee(id,name,
                        Double.parseDouble(txtSalary.getText()));
            }
            else if(type.equals("PartTime")){
                emp = new PartTimeEmployee(id,name,
                        Integer.parseInt(txtHours.getText()),
                        Double.parseDouble(txtSalary.getText()));
            }
            else{
                emp = new Manager(id,name,
                        Double.parseDouble(txtSalary.getText()),
                        Double.parseDouble(txtHours.getText()));
            }
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid number input");
            return;
        }

        if(!manager.addEmployee(emp)){
            JOptionPane.showMessageDialog(this,"ID already exists!");
            return;
        }

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

        Employee emp;

        try{
            String type = cbType.getSelectedItem().toString();
            String id = txtId.getText();
            String name = txtName.getText();

            if(type.equals("Fulltime")){
                emp = new FulltimeEmployee(id,name,
                        Double.parseDouble(txtSalary.getText()));
            }
            else if(type.equals("PartTime")){
                emp = new PartTimeEmployee(id,name,
                        Integer.parseInt(txtHours.getText()),
                        Double.parseDouble(txtSalary.getText()));
            }
            else{
                emp = new Manager(id,name,
                        Double.parseDouble(txtSalary.getText()),
                        Double.parseDouble(txtHours.getText()));
            }
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Invalid number input");
            return;
        }

        manager.updateEmployee(row, emp);

        manager.saveToFile();
        loadTable();
        clearForm();
    }

    // ===== SORT =====
    private void showSortMenu(){

        String type = (String) JOptionPane.showInputDialog(
                this,"Choose type","Filter",
                JOptionPane.QUESTION_MESSAGE,
                null,new String[]{"Fulltime","PartTime","Manager"},"Fulltime"
        );

        if(type == null) return;

        int order = JOptionPane.showOptionDialog(
                this,"Sort by salary","Sort",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,new String[]{"Ascending","Descending"},"Ascending"
        );

        ArrayList<Employee> list = manager.filterByType(type);
        manager.sortSalary(list, order == 0);

        tableModel.setRowCount(0);

        for(Employee e : list){
            tableModel.addRow(new Object[]{
                    e.getId(), e.getName(),
                    e.getClass().getSimpleName(),
                    e.calculateSalary()
            });
        }
    }

    // ===== SEARCH =====
private void searchEmployee(){

    String keyword = txtSearch.getText();
    String type = cbSearchType.getSelectedItem().toString();

    tableModel.setRowCount(0);

    ArrayList<Employee> result;

    if(type.equals("ID")){
        result = manager.searchById(keyword);
    } else {
        result = manager.searchByName(keyword);
    }

    for(Employee e : result){
        tableModel.addRow(new Object[]{
                e.getId(),
                e.getName(),
                e.getClass().getSimpleName(),
                e.calculateSalary()
        });
    }
}

    // ===== LOAD =====
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

    // ===== FILL =====
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
        else{
            cbType.setSelectedItem("Manager");
            Manager m = (Manager)e;
            txtSalary.setText(String.valueOf(m.getSalary()));
            txtHours.setText(String.valueOf(m.getBonus()));
        }

        updateFormByType();
    }

    private void clearForm(){
        txtId.setText("");
        txtName.setText("");
        txtSalary.setText("");
        txtHours.setText("");
    }
}