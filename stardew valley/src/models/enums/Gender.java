package models.enums;

public enum Gender {
    Male("male"), Female("female");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public static Gender getGenderByName(String name) {
        for (Gender gender : Gender.values()) {
            if (gender.name.equals(name)) {
                return gender;
            }
        }
        return null;
    }

}
