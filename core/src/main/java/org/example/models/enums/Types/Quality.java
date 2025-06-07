package org.example.models.enums.Types;

public enum Quality {
    Normal(1.0),
    Silver(1.25),
    Golden(1.5),
    Iridium(2.0);

    private final double percentage;
    Quality(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

}
