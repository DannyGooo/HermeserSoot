package soot.hermeser.text;

import soot.SootMethod;
import soot.Unit;

public class InvocationItem {
    private String calleeType;
    private SootMethod sootMethodDeclaredIn;
    private Unit invocationUnit;

    public InvocationItem(String typeString, SootMethod sootMethod, Unit unit) {
        this.calleeType = typeString;
        this.sootMethodDeclaredIn = sootMethod;
        this.invocationUnit = unit;
    }

    public String getCalleeType() {
        return calleeType;
    }

    public SootMethod getSootMethodDeclaredIn() {
        return sootMethodDeclaredIn;
    }

    public Unit getInvocationUnit() {
        return invocationUnit;
    }

}
