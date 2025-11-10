package dataStructures;

public class PairGeneric<T1, T2> {
    private T1 first;
    private T2 second;

    public PairGeneric()
    {}

    public PairGeneric(T1 id, T2 value)
    {
        this.first = id;
        this.second = value;
    }

    public T1 getFirst()
    {
        return this.first;
    }

    public T2 getSecond()
    {
        return this.second;
    }

    public void setFirst(T1 first)
    {
        this.first = first;
    }

    public void setSecond(T2 second)
    {
        this.second = second;
    }
}