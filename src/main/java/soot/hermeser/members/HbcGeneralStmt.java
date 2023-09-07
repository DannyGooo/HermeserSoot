package soot.hermeser.members;


import soot.*;
import soot.hermeser.text.HasmFileDefinition;
import soot.hermeser.text.HbcInstructionFormat;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * AssignStmt - Store ValueTypes to a local
 */
public class HbcGeneralStmt implements HbcInstruction {
  protected HbcInstructionFormat instruction;
  protected HbcBody hbcBody;
  protected HbcBlock hbcBlock;
  protected Unit registerRecordUnit = null;

  public HbcGeneralStmt(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock) {
    this.instruction = instruction;
    this.hbcBody = hbcBody;
    this.hbcBlock = hbcBlock;
  }





  public SootField getOrCreateField(SootClass sootClass) {
    SootField temField = sootClass.getFieldByNameUnsafe((String) instruction.valueInstance_);

    if (temField == null) {
      temField = Scene.v().makeSootField((String) instruction.valueInstance_,
          HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT), Modifier.PUBLIC | Modifier.STATIC);
      temField = sootClass.getOrAddField(temField);
    }
    return temField;
  }

  @Override
  public void jimplify(Body jb) {

    SootMethod temMethod;
    SootMethodRef temMethodRef;
    Value leftValue, rightValue;
    Local temLocal;
    RefType temRefType;
    SootClass currentClass;
    currentClass = jb.getMethod().getDeclaringClass();
    String opcodeLabel = instruction.opcodeDetailList[0];
    EqExpr equalExpr;
    List<Type> temTypes;
    List<Value> temValues;
    int temFunctionIndex;
    HasmFileDefinition hasmFileDefinition;

    switch (opcodeLabel) {
      case "CompleteGenerator":
      case "StartGenerator":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
          "Hbc.Opcode",
          opcodeLabel,
          Arrays.asList(new Type[] {
           
          }), 
          VoidType.v(),
          jb
        );

        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {})
        );

        registerRecordUnit = HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "SwitchImm":
        // get sootMethod Ref
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
            }),
            VoidType.v(),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[2])),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[4])),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[5])),
            }));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        hbcBody.addJimpleStmt(registerRecordUnit);

        setSwitchStmtTarget(jb, hbcBlock);
        newJumpStmt(null, "GOTO", instruction.opcodeDetailList[3], hbcBlock);
        break;
      case "Unreachable":
      case "Debugger":
      case "AsyncBreakCheck":
      case "PutOwnGetterSetterByVal":
      case "IteratorClose":
        temTypes = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length - 1; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
        }
        temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_BOOLEAN));

        temValues = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length - 1; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody));
        }
        temValues.add(
            IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[instruction.opcodeDetailList.length - 1])));

        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "ResumeGenerator":
        temTypes = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length - 1; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
        }
        temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_BOOLEAN));

        temValues = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody));
        }

        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "Store8":
      case "Store16":
      case "Store32":
      case "PutByVal":
        temTypes = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
        }

        temValues = new ArrayList<>();
        for (int i = 1; i < instruction.opcodeDetailList.length; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody));
        }

        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "DelById":
      case "DelByIdLong":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        temTypes = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length - 1; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
        }
        temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER));

        temValues = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length - 1; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody));
        }
        temValues.add(
           HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[instruction.opcodeDetailList.length - 1], null, hbcBody));

        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);

        break;
      case "IteratorNext":
      case "IteratorBegin":
      case "Negate":
      case "Not":
      case "BitNot":
      case "TypeOf":
      case "GetByVal":
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
      case "LoadConstTrue":
      case "LoadConstFalse":
      case "ToNumeric":
      case "Dec":
      case "Inc":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        temTypes = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
        }

        temValues = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody));
        }
        // get sootMethod Ref
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "LoadConstZero":
      case "LoadConstDouble":
      case "LoadConstUInt8":
      case "LoadConstInt":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        temTypes = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER));
        }

        temValues = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temValues.add(HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody));
        }
        // get sootMethod Ref
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcInstruction.getOrCreateType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
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
      case "ProfilePoint":
      case "ReifyArguments":
      case "LoadConstEmpty":
      case "LoadThisNS":
      case "CreateEnvironment":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        temTypes = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temTypes.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER));
        }

        temValues = new ArrayList<>();
        for (int i = 2; i < instruction.opcodeDetailList.length; i++) {
          temValues.add(IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[i])));
        }
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            temTypes,
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            temValues);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "CreateRegExp":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
            }),
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
                HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
                StringConstant
                    .v(instruction.opcodeDetailList[2].substring(1, instruction.opcodeDetailList[2].length() - 1)),
                StringConstant
                    .v(instruction.opcodeDetailList[3].substring(1, instruction.opcodeDetailList[3].length() - 1)),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[4])),
            }));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;

      case "LoadConstString":
      case "LoadConstStringLongIndex":

        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode", opcodeLabel,
            Arrays.asList(new Type[] {
              HbcTypeFactory.toSooType(instruction.valueType_)
            }),
            HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
            jb
        );

        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
              StringConstant.v((String) instruction.value_)
            })
        );

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "LoadConstUndefined":
      case "LoadConstNull":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);
        // get sootMethod
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode", opcodeLabel,
            Arrays.asList(new Type[] {}),
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);

        // construct sootMethodRef
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {}));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
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
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(instruction.valueType_), hbcBody);

        // get sootMethod
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] { HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT) }),
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal((String) instruction.operatorLeft_, null, hbcBody),
               HbcInstruction.getOrCreateLocal((String) instruction.operatorRight_, null, hbcBody)
            }));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "DeclareGlobalVar":
        temMethod =HbcInstruction.getOrCreateMethodInClass("Hbc.Opcode", "declareGlobalVar",
            Arrays.asList(new Type[] { HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT) }), VoidType.v(), jb);

        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] { StringConstant.v(instruction.getLeftRegisterLabel()) }));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit( Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "GetGlobalObject":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
                    "Hbc.Opcode",
                    opcodeLabel,
                    Arrays.asList(new Type[] {}),
                    HbcTypeFactory.toSooType(HbcTypeFactory.HBC_GLOBAL_OBJECT),
                    jb
        );

        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {})
        );

        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), rightValue.getType(), hbcBody);
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);

        break;
      case "Mov":
      case "MovLong":  
        rightValue = HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        // leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_UNKNOWN), hbcBody);
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), rightValue.getType(), hbcBody);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "PutNewOwnByIdShort":
      case "PutNewOwnById":
      case "PutNewOwnByIdLong":
      case "PutNewOwnNEById":
      case "PutNewOwnNEByIdLong":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
            }),
            VoidType.v(),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
                StringConstant.v(instruction.getObjectLabel()),
            }));
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "PutOwnByIndex":
      case "PutOwnByIndexL":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
            }),
            VoidType.v(),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[3])),
            }));
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "PutOwnByVal":
        // get sootMethod Ref
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_BOOLEAN)
            }),
            VoidType.v(),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[3], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[4]))
            }));

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;
      case "PutById":
      case "PutByIdLong":
      case "TryPutById":
      case "TryPutByIdLong":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] {
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
            }),
            VoidType.v(),
            jb);
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
               HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[2], null, hbcBody),
                IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[3])),
                StringConstant.v(instruction.getObjectLabel()),
            }));
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit( Jimple.v().newInvokeStmt(rightValue), hbcBlock);

        break;
      case "Ret":
        leftValue =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        if (leftValue == null) {
          leftValue = StringConstant.v((String) instruction.value_);
        }
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit( Jimple.v().newReturnStmt(leftValue), hbcBlock);
        break;
      case "GetBuiltinClosure":
        temRefType = HbcTypeFactory.toSooType(instruction.value_.toString());
        leftValue =HbcInstruction.getOrCreateLocal(
          instruction.getLeftRegisterLabel(),
          temRefType,
          hbcBody
        );
        
        temMethod =HbcInstruction.getOrCreateMethodInClass(
          "Hbc.Opcode",
          opcodeLabel,
          Arrays.asList(new Type[] {
              HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_ENVIRONMENT),
              HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_STRING),
            }),
          temRefType,
          jb
        );

        // construct sootMethodRef
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody),
                StringConstant.v(instruction.value_.toString())
            }));


        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        
        break;
      case "CreateClosure":
      case "CreateClosureLongIndex":
      case "CreateGeneratorClosure":
      case "CreateGeneratorClosureLongIndex":
      case "CreateAsyncClosure":
      case "CreateAsyncClosureLongIndex":
      case "CreateGenerator":
      case "CreateGeneratorLongIndex":
        temFunctionIndex = (int) instruction.valueInstance_;
        hasmFileDefinition = Scene.v().getHermesScene().getHermesAssemblyFile().getHasmFileDefinition();
        switch (hasmFileDefinition.getHbcMethods().get(temFunctionIndex).getHbcMethodKind()) {
          case NORMAL:
          case ANONYMOUS:
          case DUPLICATED:
            temRefType = (RefType)HbcInstruction.getOrCreateType("JavaScript.Function." + currentClass.getName() + "."
                + hasmFileDefinition.getHbcMethods().get(temFunctionIndex).getName());
            break;
          default:
            throw new RuntimeException("CreateClosure: " + instruction.valueInstance_ + " is unknown function");
        }

        leftValue =HbcInstruction.getOrCreateLocal(
          instruction.getLeftRegisterLabel(),
          temRefType,
          hbcBody
        );

        temMethod =HbcInstruction.getOrCreateMethodInClass(
          "Hbc.Opcode",
          opcodeLabel,
          Arrays.asList(new Type[] {
              HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_ENVIRONMENT),
              IntType.v()
          }),
          // HbcTypeFactory.toSooType("JavaScript.Function"),
          temRefType,
          jb
        );

        // construct sootMethodRef
        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody),
                IntConstant.v(temFunctionIndex)
            }));


        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);
        break;
      case "LoadParam":
      case "LoadParamLong":
        leftValue =HbcInstruction.getOrCreateLocal(instruction.getLeftRegisterLabel(), HbcTypeFactory.toSooType(HbcInstruction.getParameterClassName(instruction.opcodeDetailList[2])), hbcBody);

        rightValue = jb.getParameterLocal(
          Integer.valueOf(instruction.opcodeDetailList[2])
        );

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(leftValue, rightValue), hbcBlock);

        break;
      case "StoreToEnvironment":
      case "StoreToEnvironmentL":
      case "StoreNPToEnvironment":
      case "StoreNPToEnvironmentL":
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] { 
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_ENVIRONMENT), 
                IntType.v(),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
            }),
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);

        temMethodRef = temMethod.makeRef();

        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
                HbcInstruction.getOrCreateLocal(instruction.getObjectLabel(), null, hbcBody),
                IntConstant.v((int) instruction.valueKey_),
                HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody)
            }));

        registerRecordUnit = HbcInstruction.createRegisterRecordUnit( Jimple.v().newInvokeStmt(rightValue), hbcBlock);
        break;      
      case "JmpTrue":
      case "JmpTrueLong":
        temLocal =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        equalExpr = Jimple.v().newEqExpr(temLocal, IntConstant.v(1));
        newJumpStmt(equalExpr, "IF", instruction.opcodeDetailList[1], hbcBlock);
        break;
      case "JmpFalse":
      case "JmpFalseLong":
        temLocal =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        equalExpr = Jimple.v().newEqExpr(temLocal, IntConstant.v(0));
        newJumpStmt(equalExpr, "IF", instruction.opcodeDetailList[1], hbcBlock);
        break;
      case "JmpUndefined":
      case "JmpUndefinedLong":
        temLocal =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        equalExpr = Jimple.v().newEqExpr(temLocal, NullConstant.v());
        newJumpStmt(equalExpr, "IF", instruction.opcodeDetailList[1], hbcBlock);
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
        // get right value invoke sootMethod
        temMethod =HbcInstruction.getOrCreateMethodInClass(
            "Hbc.Opcode",
            opcodeLabel,
            Arrays.asList(new Type[] { HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT) }),
            HbcTypeFactory.toSooType(instruction.valueType_),
            jb);

        temMethodRef = temMethod.makeRef();
        rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            Arrays.asList(new Value[] {
               HbcInstruction.getOrCreateLocal((String) instruction.operatorLeft_, null, hbcBody),
               HbcInstruction.getOrCreateLocal((String) instruction.operatorRight_, null, hbcBody)
            }));

        temLocal = hbcBody.newTemp(HbcTypeFactory.toSooType(instruction.valueType_));
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(temLocal, rightValue), hbcBlock);
        hbcBody.addJimpleStmt(registerRecordUnit);

        equalExpr = Jimple.v().newEqExpr(temLocal, IntConstant.v(1));
        newJumpStmt(equalExpr, "IF", instruction.opcodeDetailList[1], hbcBlock);
        break;
      case "Jmp":
      case "JmpLong":
      case "SaveGenerator":
        newJumpStmt(null, "GOTO", instruction.opcodeDetailList[1], hbcBlock);
        break;
      case "Catch":
        temLocal =HbcInstruction.getOrCreateLocal(opcodeLabel, null, hbcBody);

        registerRecordUnit =HbcInstruction.createRegisterRecordUnit( Jimple.v().newIdentityStmt(temLocal, Jimple.v().newCaughtExceptionRef()), hbcBlock);
        temLocal.setType(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_EXCEPTION));

        if (hbcBody.getHbcMethod().getExceptionHandlersArray() == null) {
          hbcBody.addJimpleStmt(registerRecordUnit);
          Trap trap = Jimple.v().newTrap(
              Scene.v().getSootClassUnsafe(HbcTypeFactory.JAVASCRIPT_EXCEPTION),
              jb.getUnits().getPredOf(jb.getUnits().getPredOf(jb.getUnits().getLast())),
              jb.getUnits().getPredOf(jb.getUnits().getLast()),
              jb.getUnits().getLast());
          jb.getTraps().add(trap);
          registerRecordUnit = null;
        }
        break;
      case "Throw":
        temLocal =HbcInstruction.getOrCreateLocal((String) instruction.value_, null, hbcBody);
        registerRecordUnit =HbcInstruction.createRegisterRecordUnit( Jimple.v().newThrowStmt(temLocal), hbcBlock);
        break;
      default:
        throw new RuntimeException("Unknown opcode: " + opcodeLabel);
    }
    // if (registerRecordUnit != null && registerRecordUnit.getUnit() != null) {
    if (registerRecordUnit != null) {
      hbcBody.addJimpleStmt(registerRecordUnit);
    } else {
      if (!opcodeLabel.equals("Catch")) {
        System.out.println("--------------------registerRecordUnit is null------------------------------------------------");
      }
    }

  }






  public Unit newJumpStmt(Expr expr, String jumpType, String targetLabel, HbcBlock hbcBlock) {
    NopStmt nop = Jimple.v().newNopStmt();
    switch (jumpType) {
      case "IF":
        registerRecordUnit = Jimple.v().newIfStmt(expr, nop);  
        break;
      case "GOTO":
        registerRecordUnit = Jimple.v().newGotoStmt(nop);
        break;
    }
    
    hbcBody.getHbcBlockContainer().putBlockSensitiveUnitMap(registerRecordUnit, new RegisterMapRecord(hbcBlock, new HashMap<>(hbcBlock.getRegisterMap())));
    
    // set goto target
    hbcBody.getHbcBlockContainer().getGotoTargetsInBody().put(registerRecordUnit, targetLabel);

    return registerRecordUnit;
  }

  public void setSwitchStmtTarget(Body jb , HbcBlock hbcBlock) {
    Local temLocal = hbcBody.newTemp(BooleanType.v());

    List<String> jumpTable = hbcBody.getHbcMethod().getJumpTable();

    SootMethod temMethod;
    SootMethodRef temMethodRef;
    Value rightValue;

    // get right value invoke sootMethod
    temMethod = HbcInstruction.getOrCreateMethodInClass(
      "Hbc.Opcode",
      "getSwitchImmCase",
      Arrays.asList(new Type[] {
          HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
          HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
          HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
          HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_NUMBER),
      }),
      HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_BOOLEAN),
      jb
    );

    temMethodRef = temMethod.makeRef();

    rightValue = Jimple.v().newStaticInvokeExpr(
    temMethodRef,
    Arrays.asList(new Value[] {
        HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[1], null, hbcBody),
        IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[2])),
        IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[4])),
        IntConstant.v(Integer.valueOf(instruction.opcodeDetailList[5])),
      }
    )
    );
    registerRecordUnit = HbcInstruction.createRegisterRecordUnit(HbcInstruction.createAssignStmt(temLocal, rightValue), hbcBlock);
    hbcBody.addJimpleStmt(registerRecordUnit);

    for (int i = 0; i < jumpTable.size(); i++) {
      Expr equalExpr = Jimple.v().newEqExpr(temLocal, IntConstant.v(1));
      hbcBody.addJimpleStmt(
        newJumpStmt(equalExpr, "IF", jumpTable.get(i), hbcBlock)
      );
    }
  }
}