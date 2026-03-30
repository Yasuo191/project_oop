package service;
import java.util.ArrayList;
import java.io.*;
import model.*;
public class EmployeeManager {

    private ArrayList<Employee> employees = new ArrayList<>();
    private String fileName = "data/employees.txt";

    // ===== ADD =====
    public boolean addEmployee(Employee e){
        for(Employee emp : employees){
            if(emp.getId().equalsIgnoreCase(e.getId())){
                return false; // trùng ID
            }
        }
        employees.add(e);
        return true;
    }

    // ===== GET =====
    public ArrayList<Employee> getEmployees(){
        return employees;
    }

    // ===== UPDATE =====
    public void updateEmployee(int index, Employee e){
        if(index >= 0 && index < employees.size()){
            employees.set(index, e);
        }
    }

    // ===== DELETE =====
    public void deleteEmployee(int index){
        if(index >= 0 && index < employees.size()){
            employees.remove(index);
        }
    }

    // ===== SEARCH BY ID =====
    public ArrayList<Employee> searchById(String keyword){
        ArrayList<Employee> result = new ArrayList<>();

        if(keyword == null || keyword.isEmpty()){
            return new ArrayList<>(employees);
        }

        for(Employee e : employees){
            if(e.getId().toLowerCase().contains(keyword.toLowerCase())){
                result.add(e);
            }
        }

        return result;
    }

    // ===== SEARCH BY NAME =====
    public ArrayList<Employee> searchByName(String keyword){
        ArrayList<Employee> result = new ArrayList<>();

        if(keyword == null || keyword.isEmpty()){
            return new ArrayList<>(employees);
        }

        for(Employee e : employees){
            if(e.getName().toLowerCase().contains(keyword.toLowerCase())){
                result.add(e);
            }
        }

        return result;
    }

    // ===== FILTER BY TYPE =====
    public ArrayList<Employee> filterByType(String type){
        ArrayList<Employee> result = new ArrayList<>();

        for(Employee e : employees){
            if(type.equals("Fulltime") && e instanceof FulltimeEmployee){
                result.add(e);
            }
            else if(type.equals("PartTime") && e instanceof PartTimeEmployee){
                result.add(e);
            }
            else if(type.equals("Manager") && e instanceof Manager){
                result.add(e);
            }
        }

        return result;
    }

    // ===== SORT SALARY =====
    public void sortSalary(ArrayList<Employee> list, boolean asc){
        list.sort((a,b) -> asc ?
                Double.compare(a.calculateSalary(), b.calculateSalary())
                :
                Double.compare(b.calculateSalary(), a.calculateSalary())
        );
    }

    // ===== SAVE FILE (FIX UTF-8) =====
    public void saveToFile(){
        try{
            PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"
                )
            );

            for(Employee e : employees){
                pw.println(e.toFileString());
            }

            pw.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ===== LOAD FILE  =====
    public void loadFromFile(){
        employees.clear(); 
        try{
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"
                )
            );

            String line;

            while((line = br.readLine()) != null){

                if(line.trim().isEmpty()) continue;

                String[] data = line.split(",");

                String type = data[0];
                String id = data[1];
                String name = data[2];

                if(type.equals("FULL")){
                    double salary = Double.parseDouble(data[3]);
                    employees.add(new FulltimeEmployee(id, name, salary));
                }
                else if(type.equals("PART")){
                    int hours = Integer.parseInt(data[3]);
                    double rate = Double.parseDouble(data[4]);
                    employees.add(new PartTimeEmployee(id, name, hours, rate));
                }
                else if(type.equals("MANAGER")){
                    double salary = Double.parseDouble(data[3]);
                    double bonus = Double.parseDouble(data[4]);
                    employees.add(new Manager(id, name, salary, bonus));
                }
            }
            br.close();
        }catch(Exception e){
            System.out.println("No data file yet");
        }
    }
}