package model;
public class FulltimeEmployee extends Employee {
    private double salary;
    public FulltimeEmployee(String id, String name, double salary){
        super(id,name);
        this.salary = salary;
    }
    public double getSalary(){
        return salary;
    }
    @Override
    public double calculateSalary(){
        return salary;
    }

    @Override
    public String toFileString(){
        return "FULL," + id + "," + name + "," + salary;
    }
}