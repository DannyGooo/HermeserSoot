package soot.hermeser.instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.hermeser.members.HbcBlock;
import soot.hermeser.members.HbcBody;
import soot.hermeser.members.HbcInstruction;
import soot.hermeser.members.HbcMethod;
import soot.hermeser.text.HbcInstructionFormat;
import soot.hermeser.text.HbcTypeFactory;
import soot.hermeser.text.InvocationItem;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;

public class HbcCallInstruction implements HbcInstruction {
    private HbcInstructionFormat instruction;
    private HbcBody hbcBody;
    private HbcBlock hbcBlock;
    private String instructionJimpleName;
    
    public HbcCallInstruction(HbcInstructionFormat instruction, HbcBody hbcBody, HbcBlock hbcBlock, String instructionJimpleName) {
        this.instruction = instruction;
        this.hbcBody = hbcBody;
        this.hbcBlock = hbcBlock;
        this.instructionJimpleName = instructionJimpleName;
    }

    @Override
    public void jimplify(Body jb) {
        Value leftValue;

        
        String calleeTypeName = HbcTypeFactory.JAVASCRIPT_OBJECT;

        for(Local local : jb.getLocals()){
            if(local.getName().equals(instruction.functionRegister_)){
                calleeTypeName = local.getType().toString();
                break;
            }
        }

        createInvokeFunctionArg();


        leftValue =HbcInstruction.getOrCreateLocal(
            instruction.getLeftRegisterLabel(),
            HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_FUNCTION_OUTPUT),
            hbcBody
        );


        Unit stmt = solveMethodFromCallee(calleeTypeName,  leftValue, hbcBody.getJimpleBody());   

        

        stmt = HbcInstruction.createRegisterRecordUnit(stmt, null);
        
        Scene.v().getHermesScene().getHermesAssemblyFile().getHasmFileDefinition().storeToCalleeTypeList(
            calleeTypeName,
            new InvocationItem(
                calleeTypeName,
                jb.getMethod(),
                stmt
            )
        );

        hbcBlock.removeRegisterMapValue(instruction.getLeftRegisterLabel());

        hbcBody.addJimpleStmt(stmt);
    }





    public static Stmt solveMethodFromCallee(String calleeTypeName, Value leftValue, Body sootMethodBOdyDeclaredFrom){

                 
        List<String> stringArray = new ArrayList<>(Arrays.asList(calleeTypeName.split("\\.")));

        String className = null, methodName = null;
        boolean isPhantom = false;
        List<Value> args;
        SootMethod invokedSootMethod;
        Type returnType;
        
        args = getFunctionArgs(sootMethodBOdyDeclaredFrom);

        if (stringArray.size()==4 && calleeTypeName.startsWith("JavaScript.Function.")) {
            methodName = stringArray.get(3);
            className = stringArray.get(2);

            invokedSootMethod = Scene.v().getSootClassUnsafe(className).getMethodByName(methodName);
            returnType = invokedSootMethod.getReturnType();
            
            int paraNum = invokedSootMethod.getParameterCount();
            
            if(paraNum > args.size()){
                int diff = paraNum - args.size();
                for(int i = 0; i < diff; i++){
                    args.add(NullConstant.v());
                }
            } else {
                args = args.subList(0, paraNum);
            }
                
        } else {
            methodName = stringArray.get(stringArray.size() - 1);
            className = calleeTypeName.substring(0, calleeTypeName.length() - methodName.length() - 1);
            isPhantom = true;
            returnType = HbcInstruction.getOrCreateType(calleeTypeName + "." + HbcTypeFactory.JAVASCRIPT_FUNCTION_OUTPUT);
  
            List<Type> paraType = new ArrayList<>();

            for(Value arg : args){
                paraType.add(HbcInstruction.getOrCreateType(HbcTypeFactory.JAVASCRIPT_OBJECT));
            }
            
            invokedSootMethod = HbcInstruction.getOrCreateMethodInClass(
                    className,
                    methodName,
                    paraType,
                    returnType,
                    Modifier.PUBLIC | Modifier.STATIC,
                    isPhantom
            );
        }


        
        
        Stmt newStmt = HbcInstruction.generateAssignStmt(
                    leftValue, 
                    invokedSootMethod, 
                    args
        );
        return newStmt;
    }


    public static List<Value> getFunctionArgs(Body jb){
        List<Value> args = new ArrayList<>();
        UnitPatchingChain unitPatchingChain = jb.getUnits();

        List<Unit> temUnitList = new ArrayList<>();

        Unit temStmt = unitPatchingChain.getLast();
        while(!((temStmt instanceof AssignStmt) && ((AssignStmt) temStmt).getLeftOp() instanceof JArrayRef)){
            temStmt = unitPatchingChain.getPredOf(temStmt);
        }

        while(((temStmt instanceof AssignStmt) && ((AssignStmt) temStmt).getLeftOp() instanceof JArrayRef)){
            temUnitList.add(temStmt);
            temStmt = unitPatchingChain.getPredOf(temStmt);
        }
        Stmt arrayInitStmt = (Stmt) temStmt;
        SootMethod sootMethod = jb.getMethod();
        for(int i = 1;  i<=  sootMethod.getParameterCount(); i ++){    
            if(temUnitList.size() >= i){
                temStmt = temUnitList.get(temUnitList.size()-i);
            }else{
                temStmt = temUnitList.get(temUnitList.size()-1);
            }
            args.add(((AssignStmt) temStmt).getRightOp());
        }

        temUnitList.add(arrayInitStmt);
        for(Unit unit : temUnitList){
            unitPatchingChain.remove(unit);
        }

        return args;
    }


    private Local createInvokeFunctionArg() {
        int numberOfArguments;
        switch (instruction.opcodeDetailList[0]) {
            case "Call":
            case "CallLong":
            case "Construct":
            case "ConstructLong":
                numberOfArguments = Integer.valueOf(instruction.opcodeDetailList[3]);
                break;
            case "CallDirect":
            case "CallDirectLongIndex":
                numberOfArguments = Integer.valueOf(instruction.opcodeDetailList[2]);
                break;
            case "Call1":
            case "Call2":
            case "Call3":
            case "Call4":
                numberOfArguments = Integer.valueOf(instruction.opcodeDetailList[0].substring(4));
                break;
            default:
                numberOfArguments = 1;
        }

        Value temFunctionParamValue = Jimple.v().newNewArrayExpr(
            HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
            IntConstant.v(numberOfArguments)
        );
        Local temLocal = hbcBody.newTemp(temFunctionParamValue, hbcBlock);

        addArg(temLocal, numberOfArguments);    
        return temLocal;
    }


    private void addArg(Local parameterLocal, int numberOfArguments) {
        switch (instruction.opcodeDetailList[0]) {
            case "Call":
            case "CallLong":
            case "Construct":
            case "ConstructLong":
                for (int i = 0; i <= Integer.valueOf(instruction.opcodeDetailList[3]); i++) {
                    assignArrayIndex(
                        parameterLocal, 
                        i,
                        HbcInstruction.getOrCreateLocal(
                            "r" + (hbcBody.getHighestIndexRegister() - i),
                            HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                            hbcBody
                        )
                    );
                }
                break;
            case "CallDirect":
            case "CallDirectLongIndex":
                for (int i = 0; i <= Integer.valueOf(instruction.opcodeDetailList[2]); i++) {
                    assignArrayIndex(
                        parameterLocal, 
                        i,
                        HbcInstruction.getOrCreateLocal(
                            "r" + (hbcBody.getHighestIndexRegister() - i),
                            HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT),
                            hbcBody
                        )
                    );
                }
                break;
            case "Call1":
            case "Call2":
            case "Call3":
            case "Call4":
                for (int i = 3; i < instruction.opcodeDetailList.length; i++) {
                    assignArrayIndex(
                        parameterLocal, 
                        i - 3,
                        HbcInstruction.getOrCreateLocal(instruction.opcodeDetailList[i], null, hbcBody)
                    );
                }
                break;
            default:
                assignArrayIndex(
                    parameterLocal,
                     0,
                    hbcBody.newTemp(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_UNDEFINED))
                );
                break;
        }
    }

    private void assignArrayIndex(Local parameterLocal, int index, Value value) {
        Unit stmt = HbcInstruction.createRegisterRecordUnit(
            HbcInstruction.createAssignStmt(
                Jimple.v().newArrayRef(parameterLocal, IntConstant.v(index)),
                value
            ),
            hbcBlock
        );

        hbcBody.addJimpleStmt(stmt);
    }
}
