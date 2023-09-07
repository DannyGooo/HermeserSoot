package soot.hermeser.instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.hermeser.members.HbcBlock;
import soot.hermeser.members.HbcBody;
import soot.hermeser.members.HbcInstruction;
import soot.hermeser.members.HbcMethod;
import soot.hermeser.text.HbcInstructionFormat;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.options.Options;

public class HbcGetInstruction implements HbcInstruction {
    private HbcInstructionFormat instruction;
    private HbcBody hbcBody;
    private HbcBlock hbcBlock;
    private String instructionJimpleName;
    private String objectTypeString; 
    
    public HbcGetInstruction(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock, String instructionJimpleName) {
        this.instruction = instruction;
        this.hbcBody = hbcBody;
        this.hbcBlock = hbcBlock;
        this.instructionJimpleName = instructionJimpleName;
    }

    public HbcGetInstruction(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock, String instructionJimpleName, String  objectType) {
        this(instruction, hbcBody, hbcBlock, instructionJimpleName);
        this.objectTypeString = objectType;
    }

    public static void detectPossibleType(String objectInstance, HbcBody inputHbcMethod){
        switch(objectInstance){
            case "NativeModules":
                inputHbcMethod.getHbcMethod().setPossibleNativeModules(true);
                break;
            case "TurboModuleRegistry":
                inputHbcMethod.getHbcMethod().setPossilbeTurboModule(true);
                break;
            case "jsxs":
            case "jsx":
            case "createElement":
                inputHbcMethod.getHbcMethod().setPossibleUi(true);
                break;
          }
    }

    @Override
    public void jimplify(Body jb) {
        RefType temRefType;
        Local temLocal;
        String valueInstance;
        Unit stmt; 

        valueInstance = (String) instruction.valueInstance_;
        boolean isStoredInRegisterMap = true;

        if(objectTypeString!= null || !Options.v().hbc_control_flow_sensitive()){
        // if(objectTypeString!= null){
            jb.getLocals().forEach(local -> {
                if(local.getName().equals((String) instruction.value_)){
                    objectTypeString = local.getType().toString();
                }
            });
            temRefType = HbcTypeFactory.toSooType(objectTypeString + "." + valueInstance);
        } else if (!hbcBlock.getRegisterMap().containsKey((String) instruction.value_)){
            temRefType = HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_UNKNOWN + "."  + valueInstance);            
            isStoredInRegisterMap = false;
            hbcBlock.removeRegisterMapValue(instruction.getLeftRegisterLabel());
        } else {  
            // create local
            objectTypeString  =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody).getType().toString();
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
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
            }),
            Arrays.asList(new Value[] {
                HbcInstruction.getOrCreateLocal( instruction.opcodeDetailList[2], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[3])),
                StringConstant.v(valueInstance),
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
