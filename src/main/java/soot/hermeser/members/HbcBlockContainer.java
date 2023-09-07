package soot.hermeser.members;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.*;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;



public class HbcBlockContainer implements HbcInstruction {
  private HbcBody hbcBody;
  private List<HbcBlock> blocks_ = new ArrayList<>();

  private Map<Unit, HbcBlock> startUnitMap = new HashMap<>();
  private Map<Unit, HbcBlock> endUnitMap = new HashMap<>();

  private Map<Unit, RegisterMapRecord> blockSensitiveUnitMap = new HashMap<>();



  public HbcBlockContainer( HbcBody hbcBody) {
    this.hbcBody = hbcBody;
  }

  public void clear() {
    blocks_.clear();
    startUnitMap.clear();
    endUnitMap.clear();
    blockSensitiveUnitMap.clear();
  }

  public HbcBody getHbcBody() {
      return hbcBody;
  }


  public void putBlockSensitiveUnitMap(Unit registerRecordUnit, RegisterMapRecord map) {
    blockSensitiveUnitMap.put(registerRecordUnit, map);
  }

  public Map<Unit, RegisterMapRecord> getBlockSensitiveUnitMap() {
      return blockSensitiveUnitMap;
  }


  public Map<Unit, HbcBlock> getStartUnitMap() {
      return startUnitMap;
  }


  @Override
  public void jimplify(Body jb) {

    if (blocks_.size() == 0
    || (blocks_.get(0).getListOfHbcInstructionsCount() == 0 && blocks_.size() == 1)) {
      return;
    }

    if (blocks_.get(0).getListOfHbcInstructionsCount() == 0) {

      blocks_.get(0).setStartUnit(jb.getUnits().getFirst());

      blocks_.get(0).setEndUnit(jb.getUnits().getLast());


      startUnitMap.put(jb.getUnits().getFirst(), blocks_.get(0));
      endUnitMap.put(jb.getUnits().getLast(), blocks_.get(0));
    }


    for (HbcBlock hbcBlock : blocks_) {
      hbcBlock.setHbcBody(hbcBody);
      // get last unit before jimplify block
      Unit lastUnitInLastBlock = jb.getUnits().getLast();

      hbcBlock.jimplify(jb);

      // set block start and end unit
      hbcBlock.setStartUnit(jb.getUnits().getSuccOf(lastUnitInLastBlock));

      hbcBlock.setEndUnit(jb.getUnits().getLast());
    }


    // swap labels with nop stmt to the real target
    swapJumpEntriesInJBody();
    createTraps();

    for (HbcBlock hbcBlock : blocks_) {
      startUnitMap.put(hbcBlock.getStartUnit(), hbcBlock);      
      endUnitMap.put(hbcBlock.getEndUnit(), hbcBlock);
      blockSensitiveUnitMap.put(hbcBlock.getEndUnit(), new RegisterMapRecord(hbcBlock, hbcBlock.getRegisterMap()));
    }
  }



  


  public void addBcBlock(HbcBlock item) {
    blocks_.add(item);
  }

  public List<HbcBlock> getBlocksList() {
    return blocks_;
  }
  
  public HbcBlock getBlock(String blockName) {
    for (HbcBlock element : blocks_) {
      if (element.getBlockName().equals(blockName)) {
        return element;
      }
    }
    return null;
  }




  private HashMap<Unit, String> gotoTargetsInBody = new HashMap<>();


  public HashMap<Unit, String> getGotoTargetsInBody() {
      return gotoTargetsInBody;
  }

  public void swapJumpEntriesInJBody() {
    for (Map.Entry<Unit, String> entry : gotoTargetsInBody.entrySet()) {      
      Unit unit = entry.getKey();


      String targetName = entry.getValue();
      HbcBlock hbcBlock = getBlock(targetName);

      Unit targetStmt = hbcBlock.getStartUnit();
      

      if (unit instanceof IfStmt) {
        ((IfStmt) unit).setTarget(targetStmt);
      }
      if (unit instanceof GotoStmt) {
        ((GotoStmt) unit).setTarget(targetStmt);
      }

    }
  }



  public void createTraps() {
    List<String[]> handlersArray = hbcBody.getHbcMethod().getExceptionHandlersArray();

    HbcBlock hbcBlock;
    Unit beginStmt, endStmt, catchStmt;
    Trap trap;

    if (handlersArray != null) {
      for (String[] hander : handlersArray) {
        hbcBlock = getBlock(hander[0]);
        beginStmt = hbcBlock.getStartUnit();

        hbcBlock = getBlock(hander[1]);
        endStmt = hbcBlock.getStartUnit();

        hbcBlock = getBlock(hander[2]);
        catchStmt = hbcBlock.getStartUnit();

        trap = Jimple.v().newTrap(Scene.v().getSootClassUnsafe(HbcTypeFactory.JAVASCRIPT_EXCEPTION), beginStmt, endStmt,
            catchStmt);
        hbcBody.getJimpleBody().getTraps().add(trap);
      }
    }
  }
}
