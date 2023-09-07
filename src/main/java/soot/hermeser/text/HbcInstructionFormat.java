package soot.hermeser.text;

public class HbcInstructionFormat {
  private volatile java.lang.Object targetLabel_;

  public java.lang.String getLeftRegisterLabel() {
    java.lang.Object ref = targetLabel_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      java.lang.String s = "JSTargeLabel";
      targetLabel_ = s;
      return s;
    }
  }

  public String getObjectLabel(){
    return objectLabel_;
  }

  public String[] opcodeDetailList;
  public HbcInstructionFormat valueInstruction_;
  public String valueType_, opCodeName_, functionRegister_, objectLabel_;
  public int numParam_;
  public Object value_, operatorLeft_, operatorRight_, valueInstance_, valueKey_;

  public HbcInstructionFormat(String[] opcodeDetailListInput) {
    opcodeDetailList = opcodeDetailListInput;
    opCodeName_ = opcodeDetailList[0];
  }

  public void prepareForInstruction() {
    switch (opcodeDetailList[0]) {
      case "Unreachable":
      case "Debugger":
      case "AsyncBreakCheck":
      case "ResumeGenerator":
      case "Store8":
      case "Store16":
      case "Store32":
      case "PutByVal":
      case "PutOwnGetterSetterByVal":
      case "IteratorClose":
        valueType_ = "JavaScript." + opcodeDetailList[0];
        break;
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
      case "AddEmptyString":
      case "GetArgumentsPropByVal":
      case "GetArgumentsLength":
      case "NewObjectWithParent":
      case "GetNewTarget":
      case "CallBuiltin":
      case "CallBuiltinLong":
      case "ProfilePoint":
      case "ReifyArguments":
      case "LoadConstEmpty":
      case "CreateRegExp":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = "JavaScript." + opcodeDetailList[0];
        break;
      case "LoadThisNS":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_THIS;
        break;
      case "ToNumber":
      case "ToInt32":
      case "Loadi8":
      case "Loadu8":
      case "Loadi16":
      case "Loadu16":
      case "Loadi32":
      case "Loadu32":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NUMBER;
        break;
      case "NewObjectWithBuffer":
      case "NewObjectWithBufferLong":
      case "NewObject":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_OBJECT;
        break;
      case "NewArray":
      case "NewArrayWithBuffer":
      case "NewArrayWithBufferLong":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_ARRAY;
        break;
      case "DeclareGlobalVar":
        targetLabel_ = opcodeDetailList[1].substring(1, opcodeDetailList[1].length() - 1);
        valueType_ = HbcTypeFactory.HBC_GLOBAL_OBJECT;
        break;
      case "LoadConstString":
      case "LoadConstStringLongIndex":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_STRING;
        value_ = opcodeDetailList[2].substring(1, opcodeDetailList[2].length() - 1); 
        break;
      case "LoadConstUInt8":
      case "LoadConstInt":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NUMBER + "_" +opcodeDetailList[2];
        value_ = Integer.valueOf(opcodeDetailList[2]);
        break;
      case "LoadConstDouble":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NUMBER + "_" + opcodeDetailList[2];
        value_ = Double.valueOf(opcodeDetailList[2]);
      case "LoadConstZero":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NUMBER + "_0";
        break;
      case "LoadConstTrue":
      case "LoadConstFalse":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_BOOLEAN;
        break;
      case "LoadConstUndefined":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_UNDEFINED;
        break;
      case "LoadConstNull":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NULL;
        break;
      case "GetGlobalObject":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.HBC_GLOBAL_OBJECT;
        break;
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
        targetLabel_ = opcodeDetailList[1];
        operatorLeft_ = opcodeDetailList[2];
        operatorRight_ = opcodeDetailList[3];
        valueType_ = HbcTypeFactory.JAVASCRIPT_BOOLEAN;
        break;
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
      case "Add":
      case "AddN":
      case "Add32":
        targetLabel_ = opcodeDetailList[1];
        operatorLeft_ = opcodeDetailList[2];
        operatorRight_ = opcodeDetailList[3];
        valueType_ = HbcTypeFactory.JAVASCRIPT_NUMBER;
        break;
      case "Mov":
      case "MovLong":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_OBJECT;
        value_ = opcodeDetailList[2];
        break;
      case "PutNewOwnByIdShort":
      case "PutNewOwnById":
      case "PutNewOwnByIdLong":
      case "PutNewOwnNEById":
      case "PutNewOwnNEByIdLong":
      case "PutOwnByIndex":
      case "PutOwnByIndexL":
      case "PutById":
      case "PutByIdLong":
      case "TryPutById":
      case "TryPutByIdLong":
        if (opcodeDetailList[opcodeDetailList.length - 1].indexOf("\"") >= 0) {
          objectLabel_ = opcodeDetailList[opcodeDetailList.length - 1].substring(1,
              opcodeDetailList[opcodeDetailList.length - 1].length() - 1);
        } else {
          objectLabel_ = opcodeDetailList[opcodeDetailList.length - 1];
        }
        valueType_ = HbcTypeFactory.JAVASCRIPT_OBJECT;
        value_ = opcodeDetailList[2];
        break;
      case "PutOwnByVal":
        valueInstance_ = opcodeDetailList[3];
        valueType_ = HbcTypeFactory.JAVASCRIPT_OBJECT;
        value_ = opcodeDetailList[2];
        break;
      case "TryGetById":
      case "TryGetByIdLong":
      case "GetByIdShort":
      case "GetById":
      case "GetByIdLong":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[2];
        valueInstance_ = opcodeDetailList[4].substring(1, opcodeDetailList[4].length() - 1);
        break;
      case "GetByVal":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[2];
        valueInstance_ = opcodeDetailList[3];
        break;
      case "LoadFromEnvironment":
      case "LoadFromEnvironmentL":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[2];
        valueInstance_ = "index_"+ opcodeDetailList[3];
        break;
      case "Ret":
        value_ = opcodeDetailList[1];
        break;
      case "CreateEnvironment":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_ENVIRONMENT + "_this";          
        break;
      case "GetEnvironment":
        targetLabel_ = opcodeDetailList[1];
        valueType_ = HbcTypeFactory.JAVASCRIPT_ENVIRONMENT + "_"+ opcodeDetailList[2];          
        break;
      case "CreateClosure":
      case "CreateClosureLongIndex":
      case "CreateGeneratorClosure":
      case "CreateGeneratorClosureLongIndex":
      case "CreateAsyncClosure":
      case "CreateAsyncClosureLongIndex":
      case "CreateGenerator":
      case "CreateGeneratorLongIndex":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[2];
        String functionIndex;
        if(opcodeDetailList[3].contains("Function<")){
          if(opcodeDetailList[3].contains(">")){
            functionIndex = opcodeDetailList[3].substring(opcodeDetailList[3].lastIndexOf('>')+1);            
          }else{
            functionIndex = opcodeDetailList[4].substring(opcodeDetailList[4].lastIndexOf('>')+1);                        
          }
        } else {
          functionIndex = opcodeDetailList[3];
        }
        
        
        valueInstance_ = Integer.valueOf(functionIndex);        
        break;
      case "GetBuiltinClosure":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[opcodeDetailList.length - 1].substring(1,
        opcodeDetailList[opcodeDetailList.length - 1].length() - 1);
        break;
      case "LoadParam":
      case "LoadParamLong":
        targetLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[2];
        break;
      case "StoreToEnvironment":
      case "StoreToEnvironmentL":
      case "StoreNPToEnvironment":
      case "StoreNPToEnvironmentL":
        objectLabel_ = opcodeDetailList[1];
        value_ = opcodeDetailList[3];
        valueKey_ = Integer.valueOf(opcodeDetailList[2]);
        valueType_ = HbcTypeFactory.JAVASCRIPT_ENVIRONMENT;
        break;      
      case "Construct":
      case "ConstructLong":
      case "Call":
      case "CallLong":
        targetLabel_ = opcodeDetailList[1];
        functionRegister_ = opcodeDetailList[2];
        numParam_ = Integer.parseInt(opcodeDetailList[3]);
        break;
      case "CallDirect":
      case "CallDirectLongIndex":
        targetLabel_ = opcodeDetailList[1];
        numParam_ = Integer.parseInt(opcodeDetailList[2]);
        functionRegister_ = opcodeDetailList[3];
        break;
      case "Call1":
      case "Call2":
      case "Call3":
      case "Call4":
        targetLabel_ = opcodeDetailList[1];
        functionRegister_ = opcodeDetailList[2];
        numParam_ = Integer.parseInt(opcodeDetailList[0].substring(4));
        break;
      case "JmpTrue":
      case "JmpFalse":
      case "JmpUndefined":
      case "JmpTrueLong":
      case "JmpFalseLong":
      case "JmpUndefinedLong":
        value_ = opcodeDetailList[2];
        break;
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
        operatorLeft_ = opcodeDetailList[2];
        operatorRight_ = opcodeDetailList[3];
        valueType_ = HbcTypeFactory.JAVASCRIPT_BOOLEAN;
        break;
      case "SwitchImm":
        valueType_ = "JavaScript." + opcodeDetailList[0];
        break;
      case "Jmp":
      case "JmpLong":
      case "SaveGenerator":
        valueType_ = "JavaScript." + opcodeDetailList[0];
        break;
      case "Throw":
        value_ = opcodeDetailList[1];
        break;
      case "StartGenerator":
      case "CompleteGenerator":
      case "Catch":
        break;
      default:
        throw new RuntimeException("Unknown opcode: " + opcodeDetailList[0]);
    }
  }
}

