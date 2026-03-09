package model;
public class Manager extends Employee {
    private double baseSalary;
    private double bonus;
    public Manager(String id, String name, double baseSalary, double bonus){
        super(id, name);
        this.baseSalary = baseSalary;
        this.bonus = bonus;  
    }
    public double getSalary(){
        return baseSalary;
    }
    public double getBonus(){
        return bonus;
    }
    @Override
    public double calculateSalary(){
        return baseSalary + bonus;
    }
}