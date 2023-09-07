package soot.hermeser.members;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Unit;
import soot.hermeser.text.HbcInstructionFormat;


public class HbcBlock implements HbcInstruction {

  private  HbcBody hbcBody;


  private java.util.List<HbcInstructionFormat> listOfHbcInstructions_;
  private String blockName_;
  private Unit startUnit, endUnit;
  private Map<String, Unit> registerMap = new HashMap<>();

  private List<Unit> ifStmts = new ArrayList<>();

  public HbcBlock(String blockName) {
    listOfHbcInstructions_ = new ArrayList<HbcInstructionFormat>();
    blockName_ = blockName;
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

  public boolean isContainIfStmts(){
    return !ifStmts.isEmpty();
  }

  public void addToIfStmts(Unit unit){
    ifStmts.add(unit);
  }

  public void setHbcBody(HbcBody hbcBody) {
    this.hbcBody = hbcBody;
  }

  public void setBlockName(String blockName) {
    blockName_ = blockName;
  }

  public void putRegisterMapValue(String key, Unit value) {
    registerMap.put(key, value);
  }

  public void removeRegisterMapValue(String key) {
    registerMap.remove(key);
  }

  public Unit getRegisterMapValue(String key) {
    return registerMap.get(key);
  }

  public Map<String, Unit> getRegisterMap() {
      return registerMap;
  }


  @Override
  public void jimplify(Body jb) {
    for (HbcInstructionFormat instruction : getListOfHbcInstructionsList()) {
      try { 
        instruction.prepareForInstruction();
        HbcInstruction hbcInstruction = HbcInstructionFactory.fromInstructionMsg(instruction, hbcBody, this);
        hbcInstruction.jimplify(jb);  
        
      } catch (Exception e) {
        System.out.println("Exception in HbcBlock jimplify");
        e.printStackTrace();
      }
    }
  }





  public int getListOfHbcInstructionsCount() {
    return listOfHbcInstructions_.size();
  }

  public HbcInstructionFormat getHbcInstructionMsg(int index) {
    return listOfHbcInstructions_.get(index);
  }

  public String getBlockName() {
    return blockName_;
  }

  public java.util.List<HbcInstructionFormat> getListOfHbcInstructionsList() {
    return listOfHbcInstructions_;
  }
}
