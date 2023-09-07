package soot.hermeser.instructions;

import java.util.Arrays;
import java.util.Map;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.hermeser.members.HbcBlock;
import soot.hermeser.members.HbcBody;
import soot.hermeser.members.HbcInstruction;
import soot.hermeser.text.HbcInstructionFormat;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.AssignStmt;

public class HbcGetByValInstruction implements HbcInstruction {
    private HbcInstructionFormat instruction;
    private HbcBody hbcBody;
    private HbcBlock hbcBlock;
    private String instructionJimpleName;
    
    public HbcGetByValInstruction(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock, String instructionJimpleName) {
        this.instruction = instruction;
        this.hbcBody = hbcBody;
        this.hbcBlock = hbcBlock;
        this.instructionJimpleName = instructionJimpleName;
    }


    @Override
    public void jimplify(Body jb) {
        RefType temRefType;
        Local temLocal;
        Unit stmt; 
        boolean isStoredInRegisterMap = true;

        Map<String, Unit> registerMap = hbcBlock.getRegisterMap();
        AssignStmt objectStmtDetected = (AssignStmt)registerMap.get((String) instruction.value_);
        AssignStmt propertyStmtDetected = (AssignStmt)registerMap.get((String) instruction.valueInstance_);

        

        if (objectStmtDetected != null && propertyStmtDetected != null){
            temRefType = HbcTypeFactory.toSooType(objectStmtDetected.getLeftOp().getType().toString() + ".GetByVal." + propertyStmtDetected.getLeftOp().getType().toString());
        } else {              
            temRefType = HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT + ".GetByVal."  + HbcTypeFactory.JAVASCRIPT_PROPERTY);
            isStoredInRegisterMap = false;
            hbcBlock.removeRegisterMapValue(instruction.getLeftRegisterLabel());
        }

        temLocal =HbcInstruction.getOrCreateLocal(
            instruction.getLeftRegisterLabel(),
            temRefType,
            hbcBody
        );
        
        stmt = HbcInstruction.generateAssignStmt(
            temLocal,
            "Hbc.Opcode",
            temLocal.getType(),
            instructionJimpleName,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
            }),
            Arrays.asList(new Value[] {
                HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
                HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[3], null, hbcBody),
            }),
            true
        );
        if(!isStoredInRegisterMap){
            hbcBlock = null;
        }
        stmt = HbcInstruction.createRegisterRecordUnit(stmt, hbcBlock);
        
        hbcBody.addJimpleStmt(stmt);
    }
}