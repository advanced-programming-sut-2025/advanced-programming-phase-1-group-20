package org.example.common.models.Items;


import org.example.common.models.enums.Types.MineralType;

public class Mineral extends Item {
    private MineralType type;
    private boolean isMined = false;

    public Mineral(MineralType type) {
        super(type.getName(), type.getBaseSellPrice() , type.getImageFilepath());
        this.type = type;
    }

    public String getDescription() {
        return type.getDescription();
    }

    public MineralType getType() {
        return type;
    }

    public void setType(MineralType type) {
        this.type = type;
    }

    public boolean isMined() {
        return isMined;
    }

    public void setMined(boolean mined) {
        isMined = mined;
    }

    @Override
    public String getImageFilepath() {
        if(isMined) {
            return "content/Minerals/" + type.getImageFilepath() + "_Bar.png";
        }else{
            return "content/Minerals/" + type.getImageFilepath() + "_Ore.png";
        }
    }

    @Override
    public void showInfo() {
        type.showInfo();
    }
}
