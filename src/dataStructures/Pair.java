package dataStructures;

public class Pair {
    private double first;
    private double second;

    public Pair(int id, double value)
    {
        this.first = id;
        this.second = value;
    }

    public void sortPair(){
        if (second < first){
            double aux = first;
            first = second;
            second = aux;
        }
    }

    public double getFirst()
    {
        return this.first;
    }

    public double getSecond()
    {
        return this.second;
    }

    public void setFirst(double first)
    {
        this.first = first;
    }

    public void setSecond(double second)
    {
        this.second = second;
    }
}