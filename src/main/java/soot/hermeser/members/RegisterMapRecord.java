package soot.hermeser.members;

import java.util.HashMap;
import java.util.Map;

import soot.Unit;

public class RegisterMapRecord {
    private Map<String, Unit> registerMap = new HashMap<>();
    private HbcBlock hbcBlock;
    private Unit startUnit, endUnit;

    public RegisterMapRecord(HbcBlock hbcBlock, Map<String, Unit> registerMap) {
        this.hbcBlock = hbcBlock;
        this.registerMap = registerMap;
    }


    public void setStartUnit(Unit startUnit) {
        this.startUnit = startUnit;
    }

    public void setEndUnit(Unit endUnit) {
        this.endUnit = endUnit;
    }

    public Unit getStartUnit() {
        return startUnit;
    }

    public Unit getEndUnit() {
        return endUnit;
    }

    public HbcBlock getHbcBlock() {
        return hbcBlock;
    }

    public Map<String, Unit> getRegisterMap() {
        return registerMap;
    }
}