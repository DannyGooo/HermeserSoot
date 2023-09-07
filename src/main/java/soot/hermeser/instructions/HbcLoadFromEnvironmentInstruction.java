package soot.hermeser.instructions;

import java.util.Arrays;

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
import soot.jimple.IntConstant;

public class HbcLoadFromEnvironmentInstruction implements HbcInstruction {
    private HbcInstructionFormat instruction;
    private HbcBody hbcBody;
    private HbcBlock hbcBlock;
    private String instructionJimpleName;
    private String objectTypeString; 

    public HbcLoadFromEnvironmentInstruction(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock, String instructionJimpleName) {
        this.instruction = instruction;
        this.hbcBody = hbcBody;
        this.hbcBlock = hbcBlock;
        this.instructionJimpleName = instructionJimpleName;
    }


    @Override
    public void jimplify(Body jb) {
        RefType temRefType;
        Local temLocal;
        String valueInstance;
        Unit stmt; 
        boolean isStoredInRegisterMap = true;
        
        valueInstance = (String) instruction.valueInstance_;


        if (!hbcBlock.getRegisterMap().containsKey((String) instruction.value_)){
            temRefType = HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_ENVIRONMENT + "."  + valueInstance);
            // temRefType = HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_UNKNOWN + "."  + valueInstance);            
            isStoredInRegisterMap = false;
            hbcBlock.removeRegisterMapValue(instruction.getLeftRegisterLabel());
        } else {  
            // create local
            objectTypeString  = HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody).getType().toString();                                  
            temRefType = HbcTypeFactory.toSooType(objectTypeString + "." + valueInstance);                       
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
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
            }),
            Arrays.asList(new Value[] {
                HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[3])),
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
