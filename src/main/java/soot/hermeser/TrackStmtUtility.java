package soot.hermeser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.InvokeExpr;

public class TrackStmtUtility {
    public static AssignStmt trackAsignStmt(Unit invokedStmt, Local local, SootMethod method){
        return trackAsignStmt(invokedStmt, local, method.getActiveBody());
    }

    public static AssignStmt trackAsignStmt(Unit invokedStmt, Local local, Body jb){
        UnitPatchingChain unitPatchingChain = jb.getUnits();
        Unit currentUnit = invokedStmt;

        while(currentUnit != null){
            if(currentUnit instanceof AssignStmt
                && (((AssignStmt) currentUnit).getLeftOp()).equals(local)){
                return (AssignStmt) currentUnit;
            }

            currentUnit = unitPatchingChain.getPredOf(currentUnit);            
        }    
        return null;                        
    }

    public static String trackStringValueFromSpecifiedArgOfGivenBody(Unit stmt, Body jb){
        try{
            Map<String, AssignStmt> argumentsValueExprMap = trackLocalArgMapFromGivenBody(stmt, jb);
            AssignStmt assignedStmt = argumentsValueExprMap.get("1");
            Value rightVal = assignedStmt.getRightOp();
            while(rightVal instanceof Local){
                assignedStmt = trackAsignStmt(assignedStmt, (Local) rightVal, jb);
                if(assignedStmt == null){
                    return null;
                }
                rightVal = assignedStmt.getRightOp();
            }

            if(rightVal instanceof StringConstant){
                String moduleName = rightVal.toString();
                moduleName = moduleName.substring(1, moduleName.length()-1);
                return moduleName;
            }
        }catch(Exception e){    
            return null;
        }

        return null;
    }




    public static Map<String, AssignStmt> trackLocalArgMapFromGivenBody(Unit invokedStmt, Body jb){
        UnitPatchingChain unitPatchingChain = jb.getUnits();
        Unit currentUnit =  unitPatchingChain.getLast();

        Local argLocal = (Local) ((InvokeExpr) ((AssignStmt) invokedStmt).getRightOp()).getArg(0);

        return trackArgAssignStmtsMap( argLocal, currentUnit, unitPatchingChain);
    }

    public static Map<String, AssignStmt> trackArgAssignStmtsMap(Unit invokedStmt, SootMethod method){        
        return trackArgAssignStmtsMap(invokedStmt, method.getActiveBody());
    }

    public static Map<String, AssignStmt> trackArgAssignStmtsMap(Unit invokedStmt, Body jb){

        UnitPatchingChain unitPatchingChain = jb.getUnits();
        Unit currentUnit =  unitPatchingChain.getPredOf(invokedStmt);

        Local argLocal = (Local) ((InvokeExpr) ((Stmt) invokedStmt).getInvokeExpr()).getArg(0);

        return trackArgAssignStmtsMap( argLocal, currentUnit, unitPatchingChain);
    }

    

    public static Map<String, AssignStmt> trackArgAssignStmtsMap(Local argLocal, Unit currentUnit, UnitPatchingChain unitPatchingChain){
        ArrayList<AssignStmt> argAssignStmts = new ArrayList<AssignStmt>();

        while(currentUnit != null){
            if( currentUnit instanceof AssignStmt
                && ((AssignStmt) currentUnit).getLeftOp() instanceof ArrayRef
                && ((ArrayRef)((AssignStmt) currentUnit).getLeftOp()).getBase().equals(argLocal)){
                    argAssignStmts.add((AssignStmt) currentUnit);
            }

            if(((AssignStmt) currentUnit).getLeftOp().equals(argLocal)){
                break;
            }
            currentUnit = unitPatchingChain.getPredOf(currentUnit);            
        }

        Map<String, AssignStmt> argumentsValueExprMap = new HashMap<String, AssignStmt>();
        for(AssignStmt argAssignUnit : argAssignStmts){
            if(!(argAssignUnit.getRightOp() instanceof Local)){
                continue;
            }
            Local temLocal = (Local) argAssignUnit.getRightOp();
            ArrayRef argArrayRef = (ArrayRef) argAssignUnit.getLeftOp();                
            
            
            currentUnit =  unitPatchingChain.getPredOf(currentUnit);
            while(currentUnit != null){
                if(
                    currentUnit instanceof AssignStmt
                    && ((AssignStmt) currentUnit).getLeftOp().equals(temLocal)
                ){
                    argumentsValueExprMap.put(argArrayRef.getIndex().toString(),(AssignStmt) currentUnit);
                    break;
                }
    
                currentUnit = unitPatchingChain.getPredOf(currentUnit);            
            }                
        }

        return argumentsValueExprMap;
    }
}
