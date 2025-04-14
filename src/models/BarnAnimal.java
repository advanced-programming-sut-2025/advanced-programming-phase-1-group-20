package models;

import models.enums.Cages;

public class BarnAnimal extends Animal{
    private int price;
    private boolean barn;
    private int speed;
    private Cages cage;
    public BarnAnimal(String name, boolean barn, int speed , Cages cage , int price) {
        super(name , price);
        this.barn = barn;
        this.speed = speed;
        this.cage = cage;
    }
}
