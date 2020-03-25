package model;

public class Variable {
    private int myIndex;
    private String myName;
    private double myValue;

    /**
     * unique variable object that
     * user is able to change its name and value
     * @param index the unique value given to each new variable so that if the user changes its name it is the same variable
     * @param name the name associated with the variable
     * @param value the value associated with the variable
     */
    public Variable(int index, String name, double value){
        myIndex = index;
        myName = name;
        myValue = value;
    }

    /**
     * changes the value of the variable keeps the old name
     * @param value the new value
     */
    public void updateVariable( double value){
        myValue = value;
    }

    /**
     * updates the name of the variable keeps the old value
     * @param name the new name
     */
    public void updateVariable(String name){
        myName = name;
    }

    /**
     *
     * @return the name associated with the variable
     */
    public String getName(){
        return myName;
    }

    /**
     *
     * @return the value associated with the variable
     */
    public double getVal(){
        return myValue;
    }

}
