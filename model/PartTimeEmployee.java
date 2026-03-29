package model;
public class PartTimeEmployee extends Employee {
    private int hours;
    private double rate;
    public PartTimeEmployee(String id,String name,int hours,double rate){
        super(id,name);
        this.hours = hours;
        this.rate = rate;
    }
    public int getHours(){
        return hours;
    }
    public double getRate(){
        return rate;
    }
    @Override
    public double calculateSalary(){
        return hours * rate;
    }
    
    @Override
    public String toFileString(){
        return "PART," + id + "," + name + "," + hours + "," + rate;
    }
}