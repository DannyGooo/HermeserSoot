package soot.hermeser.members;


import soot.hermeser.instructions.HbcCallInstruction;
import soot.hermeser.instructions.HbcGetByValInstruction;
import soot.hermeser.instructions.HbcGetInstruction;
import soot.hermeser.instructions.HbcLoadFromEnvironmentInstruction;
import soot.hermeser.text.HbcInstructionFormat;
import soot.hermeser.text.HbcTypeFactory;

/**
 * Factory for creating IL Instruction objects
 */
public class HbcInstructionFactory {
  public static HbcInstruction fromInstructionMsg(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock) {
    if (instruction == null) {
      throw new RuntimeException("Cannot instantiate null instruction!");
    }
    String opcodeLabel = instruction.opcodeDetailList[0];
    switch (opcodeLabel) {
      case "PutOwnGetterSetterByVal":
      case "CompleteGenerator":
      case "SwitchImm":
      case "Unreachable":
      case "Debugger":
      case "AsyncBreakCheck":
      case "StartGenerator":
      case "SaveGenerator":
      case "ResumeGenerator":
      case "Store8":
      case "Store16":
      case "Store32":
      case "PutByVal": 
      case "DelById":
      case "DelByIdLong":
      case "IteratorNext":
      case "IteratorBegin":
      case "Negate":
      case "Not":
      case "BitNot":
      case "TypeOf":
      case "ToNumeric":
      case "Dec":
      case "Inc":
      case "DelByVal":
      case "GetPNameList":
      case "GetNextPName":
      case "DirectEval":
      case "ThrowIfEmpty":
      case "CreateThis":
      case "SelectObject":
      case "CoerceThisNS":
      case "ToNumber":
      case "ToInt32":
      case "AddEmptyString":
      case "GetArgumentsPropByVal":
      case "GetArgumentsLength":
      case "Loadi8":
      case "Loadu8":
      case "Loadi16":
      case "Loadu16":
      case "Loadi32":
      case "Loadu32":
      case "NewObjectWithParent":
      case "DeclareGlobalVar":
      case "NewObjectWithBuffer":
      case "NewObjectWithBufferLong":
      case "NewArrayWithBuffer":
      case "NewArrayWithBufferLong":
      case "NewObject":
      case "NewArray":
      case "GetEnvironment":
      case "GetNewTarget":
      case "CallBuiltin":
      case "CallBuiltinLong":
      case "GetBuiltinClosure":
      case "ProfilePoint":
      case "ReifyArguments":
      case "LoadConstEmpty":
      case "LoadThisNS":
      case "CreateRegExp":
      case "IteratorClose":
      case "LoadConstUInt8":
      case "LoadConstInt":
      case "LoadConstDouble":
      case "LoadConstString":
      case "LoadConstStringLongIndex":
      case "LoadConstUndefined":
      case "LoadConstNull":
      case "LoadConstZero":
      case "LoadConstTrue":
      case "LoadConstFalse":
      case "Eq":
      case "StrictEq":
      case "Neq":
      case "StrictNeq":
      case "Less":
      case "LessEq":
      case "Greater":
      case "GreaterEq":
      case "InstanceOf":
      case "IsIn":
      case "Mul":
      case "MulN":
      case "Div":
      case "DivN":
      case "Mod":
      case "Sub":
      case "SubN":
      case "LShift":
      case "RShift":
      case "URshift":
      case "BitAnd":
      case "BitXor":
      case "BitOr":
      case "Sub32":
      case "Mul32":
      case "Divi32":
      case "Divu32":
      case "Mov":    
      case "MovLong": 
      case "GetGlobalObject":
      case "PutNewOwnByIdShort":
      case "PutNewOwnById":
      case "PutNewOwnByIdLong":
      case "PutNewOwnNEById":
      case "PutNewOwnNEByIdLong":
      case "PutOwnByIndex":
      case "PutOwnByIndexL":
      case "PutOwnByVal":
      case "PutById":
      case "PutByIdLong":
      case "TryPutById":
      case "TryPutByIdLong":      
      case "Add":
      case "AddN":
      case "Add32":
      case "Ret":
      case "CreateEnvironment":
      case "CreateClosure":
      case "CreateClosureLongIndex":
      case "CreateGeneratorClosure":
      case "CreateGeneratorClosureLongIndex":
      case "CreateGenerator":
      case "CreateGeneratorLongIndex":
      case "CreateAsyncClosure":
      case "CreateAsyncClosureLongIndex":
      case "LoadParam":
      case "LoadParamLong":
      case "StoreToEnvironment":
      case "StoreToEnvironmentL":
      case "StoreNPToEnvironment":
      case "StoreNPToEnvironmentL":
      case "JLess":
      case "JNotLess":
      case "JLessN":
      case "JNotLessN":
      case "JLessEqual":
      case "JNotLessEqual":
      case "JLessEqualN":
      case "JNotLessEqualN":
      case "JGreater":
      case "JNotGreater":
      case "JGreaterN":
      case "JNotGreaterN":
      case "JGreaterEqual":
      case "JNotGreaterEqual":
      case "JGreaterEqualN":
      case "JNotGreaterEqualN":
      case "JEqual":
      case "JNotEqual":
      case "JStrictEqual":
      case "JStrictNotEqual":
      case "JmpTrue":
      case "JmpFalse":
      case "JmpUndefined":
      case "Jmp":
      case "JLessLong":
      case "JNotLessLong":
      case "JLessNLong":
      case "JNotLessNLong":
      case "JLessEqualLong":
      case "JNotLessEqualLong":
      case "JLessEqualNLong":
      case "JNotLessEqualNLong":
      case "JGreaterLong":
      case "JNotGreaterLong":
      case "JGreaterNLong":
      case "JNotGreaterNLong":
      case "JGreaterEqualLong":
      case "JNotGreaterEqualLong":
      case "JGreaterEqualNLong":
      case "JNotGreaterEqualNLong":
      case "JEqualLong":
      case "JNotEqualLong":
      case "JStrictEqualLong":
      case "JStrictNotEqualLong":
      case "JmpTrueLong":
      case "JmpFalseLong":
      case "JmpUndefinedLong":
      case "JmpLong":
      case "Catch":
      case "Throw":
        return new HbcGeneralStmt(instruction, hbcBody, hbcBlock);
      case "Construct":
      case "ConstructLong":
        return new HbcCallInstruction(instruction, hbcBody, hbcBlock, "hbcConstruct");
      case "Call":
      case "CallLong":
      case "Call1":
      case "Call2":
      case "Call3":
      case "Call4":
      case "CallDirect":
      case "CallDirectLongIndex":
        return new HbcCallInstruction(instruction, hbcBody, hbcBlock, "hbcCall");
      case "GetByIdShort":
      case "GetById":
      case "GetByIdLong":  
        HbcGetInstruction.detectPossibleType((String)instruction.valueInstance_, hbcBody);      
        return new HbcGetInstruction(instruction, hbcBody, hbcBlock, "hbcGet");
      case "TryGetById":
      case "TryGetByIdLong":
        return new HbcGetInstruction(instruction, hbcBody, hbcBlock, "hbcGet", HbcTypeFactory.HBC_GLOBAL_OBJECT);
      case "LoadFromEnvironment":
      case "LoadFromEnvironmentL":
        return new HbcLoadFromEnvironmentInstruction(instruction, hbcBody, hbcBlock, "LoadFromEnvironment");
      case "GetByVal":
        return new HbcGetByValInstruction(instruction, hbcBody, hbcBlock, "GetByVal");
      default:
        throw new IllegalArgumentException("Opcode " + opcodeLabel + " is not implemented!");
    }
  }
}
