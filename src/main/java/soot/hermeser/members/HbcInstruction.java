package soot.hermeser.members;

import java.util.List;

import soot.Body;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 * Base CIL Instruction
 */
public interface HbcInstruction {
  /**
   * Jimplify a starting statement of ILSpy AST
   *
   * @param jb
   */
  public void jimplify(Body jb);


  public static void compareForHighestRegister(String registerName, HbcBody hbcBody) {
    Chain<Local> chainLocals = hbcBody.getJimpleBody().getLocals();
    for (Local localItem : chainLocals) {
      if (localItem.getName().startsWith("r")
          && Integer.valueOf(localItem.getName().substring(1)) > hbcBody.getHighestIndexRegister()) {
        hbcBody.setHighestIndexRegister(Integer.valueOf(localItem.getName().substring(1)));
      }
    }
  }

  public static Local getOrCreateLocal(String targetLabel, Type refType, HbcBody hbcBody) {
    return getOrCreateLocal(targetLabel, refType, hbcBody, true);
  }

  public static Local getOrCreateLocal(String targetLabel, Type refType, HbcBody hbcBody, boolean checkLocalExistence) {
    Local local = null;
    if (checkLocalExistence) {
      local = hbcBody.getLocal(targetLabel);
    }

    if (local == null) {
      // construct local value
      local = Jimple.v().newLocal(targetLabel, HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
      hbcBody.getJimpleBody().getLocals().add(local);
      compareForHighestRegister(targetLabel,  hbcBody);
    }

    if (refType == null) {
      return local;
    }

    getOrCreateType(refType.toString());
    local.setType(refType);
    return local;
  }

  public static String getParameterClassName(String parameterIndex) {
    return HbcTypeFactory.JAVASCRIPT_OBJECT + ".Funciton.Parameter_" + parameterIndex;
  }

  public static AssignStmt createAssignStmt(Value leftValue, Value rightValue) {
    AssignStmt outputStmt = Jimple.v().newAssignStmt(leftValue, rightValue);
    return outputStmt;
  }



  public static Unit createRegisterRecordUnit(Unit stmt, HbcBlock hbcBlock) {

    if(stmt instanceof AssignStmt && hbcBlock != null){
      hbcBlock.putRegisterMapValue(((AssignStmt) stmt).getLeftOp().toString(), stmt);
    }

    return stmt;
  }


  public static SootMethod getOrCreateMethodInClass(String className, String methodName, List<Type> parameterType,
      Type returnType, int modifiers, boolean isPhantom) {
    SootClass sootClass = Scene.v().getSootClassUnsafe(className);
    return getOrCreateMethodInClass(sootClass, methodName, parameterType, returnType, modifiers, isPhantom);
  }

  public static SootMethod getOrCreateMethodInClass(String className, String methodName, List<Type> parameterType,
      Type returnType, Body jimpleBody) {
    SootClass sootClass = Scene.v().getSootClassUnsafe(className);
    return getOrCreateMethodInClass(sootClass, methodName, parameterType, returnType, Modifier.PUBLIC | Modifier.STATIC,
        true);
  }  


  public static SootMethod getOrCreateMethodInClass(SootClass sootClass, String methodName, List<Type> parameterType,
    Type returnType, int modifiers, boolean isPhantom) {
    SootMethod sootMethod = Scene.v().makeSootMethod(methodName, parameterType, returnType, modifiers);
    sootMethod = sootClass.getOrAddMethod(sootMethod);
    if (isPhantom) {
      if (!sootClass.isPhantomClass()) {
        try {
          sootClass.setPhantomClass();
        } catch (Exception e) {
          System.out.println("have been added to Phantom class.");
        }
      }
      sootMethod.setPhantom(true);
    }else{
      sootClass.setApplicationClass();
    }

    for (Type type : parameterType) {
      getOrCreateType(type.toString());
    }
    getOrCreateType(returnType.toString());
    return sootMethod;
  }


  public static Type getOrCreateType(String typeName) {
    SootClass sootClass = Scene.v().getSootClassUnsafe(typeName);
    if (!sootClass.isInScene()) {
      Scene.v().addBasicClass(sootClass.getName(), SootClass.SIGNATURES);
    }
    return sootClass.getType();
  }

  public static Value  generateInvokeExpr(String sootClassName, Type returnType, String methodName, List<Type> paraTypes, List<Value> args, boolean isPhantom){
    SootMethod temMethod = getOrCreateMethodInClass(
            sootClassName,
            methodName,
            paraTypes,
            returnType,
            Modifier.PUBLIC | Modifier.STATIC,
            isPhantom
    );

    SootMethodRef temMethodRef = temMethod.makeRef();

    Value rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            args
    );

    return rightValue;
  }

  public static Stmt generateAssignStmt(Value leftValue, String sootClassName, Type returnType, String methodName, List<Type> paraTypes, List<Value> args, boolean isPhantom){

    Value invokeExpr = generateInvokeExpr(
        sootClassName,
        returnType,
        methodName,
        paraTypes,
        args,
        isPhantom
    );

    Stmt outputStmt = Jimple.v().newAssignStmt(leftValue, invokeExpr);

    return outputStmt;
  }

  public static Stmt generateAssignStmt(Value leftValue, SootMethod sootMethod, List<Value> args){
    SootMethodRef temMethodRef = sootMethod.makeRef();

    Value rightValue = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            args
    );

    Stmt outputStmt = Jimple.v().newAssignStmt(leftValue, rightValue);

    return outputStmt;
  }

  public static Stmt generateInvokeStmt(String sootClassName, Type returnType, String methodName, List<Type> paraTypes, List<Value> args, boolean isPhantom){

    Value invokeExpr = generateInvokeExpr(
            sootClassName,
            returnType,
            methodName,
            paraTypes,
            args,
            isPhantom
    );

    Stmt outputStmt = Jimple.v().newInvokeStmt(invokeExpr);

    return outputStmt;
  }

  public static Stmt generateInvokeStmt(SootMethod sootMethod, List<Value> args){
    SootMethodRef temMethodRef = sootMethod.makeRef();

    Value invokeExpr = Jimple.v().newStaticInvokeExpr(
            temMethodRef,
            args
    );

    Stmt outputStmt = Jimple.v().newInvokeStmt(invokeExpr);

    return outputStmt;
  }
}