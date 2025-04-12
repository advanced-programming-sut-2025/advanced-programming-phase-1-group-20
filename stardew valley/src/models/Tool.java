package models;

public class Tool extends Item{
    int level = 0;
    int energyCost;
    public Tool(String name , int energyCost){
        super(name);
        this.energyCost = energyCost;
    }

    public void upgrade(){
        level++;
    }
}
