package service;
import java.util.ArrayList;
import java.io.*;
import model.*;
public class EmployeeManager {
    private ArrayList<Employee> employees = new ArrayList<>();
    private String fileName = "employees.txt";
    public void addEmployee(Employee e){
        employees.add(e);
    }
    public ArrayList<Employee> getEmployees(){
        return employees;
    }

    // SEARCH
    public ArrayList<Employee> searchByName(String keyword){
        ArrayList<Employee> result = new ArrayList<>();
        for(Employee e : employees){
            if(e.getName().toLowerCase().contains(keyword.toLowerCase())){
                result.add(e);
            }
        }
        return result;
    }

    // DELETE
    public void deleteEmployee(int index){
        if(index >= 0 && index < employees.size()){
            employees.remove(index);
        }
    }

    // SORT BY ID ASC
    public void sortIdAsc(){
        employees.sort((a,b) ->
                a.getId().compareToIgnoreCase(b.getId()));
    }

    // SORT BY ID DESC
    public void sortIdDesc(){
        employees.sort((a,b) ->
                b.getId().compareToIgnoreCase(a.getId()));
    }

    // SORT SALARY ASC
    public void sortSalaryAsc(){
        employees.sort((a,b) ->
                Double.compare(a.calculateSalary(), b.calculateSalary()));
    }

// SORT SALARY DESC
public void sortSalaryDesc(){
    employees.sort((a,b) ->
            Double.compare(b.calculateSalary(), a.calculateSalary()));
}

    // SAVE FILE
    public void saveToFile(){

        try{

            PrintWriter pw = new PrintWriter(new FileWriter(fileName));

            for(Employee e : employees){

                if(e instanceof FulltimeEmployee){
                    pw.println("FULL," + e.getId() + "," + e.getName() + "," + e.calculateSalary());
                }

                else if(e instanceof PartTimeEmployee){
                    pw.println("PART," + e.getId() + "," + e.getName() + "," + e.calculateSalary());
                }

                else if(e instanceof Manager){
                    pw.println("MANAGER," + e.getId() + "," + e.getName() + "," + e.calculateSalary());
                }

            }

            pw.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    // LOAD FILE
    public void loadFromFile(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");
                String type = data[0];
                String id = data[1];
                String name = data[2];
                double salary = Double.parseDouble(data[3]);
                if(type.equals("FULL")){
                    employees.add(new FulltimeEmployee(id,name,salary));
                }
                else if(type.equals("PART")){
                    employees.add(new PartTimeEmployee(id,name,8,salary));
                }
                else if(type.equals("MANAGER")){
                    employees.add(new Manager(id,name,salary,500));
                }
            }

            br.close();
        }
        catch(Exception e){
            System.out.println("No data file yet");
        }

    }
}