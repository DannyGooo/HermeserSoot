package soot.hermeser.members;


/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.*;
import soot.hermeser.text.HbcTypeFactory;
import soot.jimple.*;
import soot.util.Chain;

public class HbcBody {
  private JimpleBody jb;

  public Local parameterLocal;
  private int nextTempIndex = 0;
  private int highestIndexRegister = 0;
  private Local thisName;
  HbcBlockContainer hbcBlockContainer = new HbcBlockContainer(this);




  public HbcBody(HbcMethod hbcMethod) {
    this.hbcMethod = hbcMethod;
  }

  public HbcBody(HbcMethod hbcMethod, Body jb) {
    this.hbcMethod = hbcMethod;
    this.jb = (JimpleBody) jb;
  }

  public void clear(){
    hbcBlockContainer.clear();
  }


  public void addJimpleStmt(Unit stmt){
    jb.getUnits().add(
      stmt
    );
  }


  public HbcBlockContainer getHbcBlockContainer() {
    return hbcBlockContainer;
  }



  public int getHighestIndexRegister() {
    return highestIndexRegister;
  }

  public void setHighestIndexRegister(int highestIndexRegister) {
    this.highestIndexRegister = highestIndexRegister;
  }

  public JimpleBody getJimpleBody() {
    return jb;
  }

  public Local newTemp(soot.Type type) {
    Local local = Jimple.v().newLocal("temp$" + nextTempIndex++, type);
    jb.getLocals().add(local);
    return local;
  }

  public Local newTemp(soot.Value v, HbcBlock hbcBlock) {
    Local local = newTemp(v.getType());
    if (v instanceof soot.jimple.ParameterRef) {
      addJimpleStmt(
        HbcInstruction.createRegisterRecordUnit(
          Jimple.v().newIdentityStmt(local, (soot.jimple.ParameterRef) v), 
          hbcBlock
        )
      );
    } else {
      addJimpleStmt(
        HbcInstruction.createRegisterRecordUnit(
          Jimple.v().newAssignStmt(local, v), 
          hbcBlock
        )
      );
    }
    return local;
  }


  public Local newLocal(String name, Type type) {
    Local local = Jimple.v().newLocal(name, type);
    jb.getLocals().add(local);
    if (name.equals("this") && thisName == null)
      thisName = local;
    return local;
  }

  public Local newLocal(String name, Value value, Type type, HbcBlock hbcBlock) {
    Local local = newLocal(name, type);
    addJimpleStmt(HbcInstruction.createRegisterRecordUnit(
        Jimple.v().newAssignStmt(local, value), 
        hbcBlock)
    );

    return local;
  }

  /**
   * Get method signature of this method body
   *
   * @return method signature
   */
  public HbcMethod getHbcMethod() {
    return hbcMethod;
  }

  private final HbcMethod hbcMethod;


  public JimpleBody jimplify(SootMethod sootMethod) {
    this.jb = Jimple.v().newBody(sootMethod);

    addCompulsoryInitStmt();

    hbcBlockContainer.jimplify(this.jb);


    return this.jb;
  }
  
  

  // add compulsory init stmt
  private void addCompulsoryInitStmt() {
    for(int i=0; i<hbcMethod.getParameterLoadedNum(); i++){
      String temName = "JavaScript.Parameter_"+i;

      SootClass sootClass = Scene.v().getSootClassUnsafe(temName);
      Type temType = sootClass.getType();

      Local temLocal = newLocal("arg"+i, temType);
      Value rightValue = Jimple.v().newParameterRef(temType, i);
      Stmt temStmt = Jimple.v().newIdentityStmt(temLocal, rightValue);
      addJimpleStmt(HbcInstruction.createRegisterRecordUnit(
        temStmt, 
        null
      ));
    }

    // set to parameterLocal
    // parameterLocal = temLocal;
  }


  public Local getLocal(String localName) {
    Chain<Local> chainLocals = jb.getLocals();
    for (Local localItem : chainLocals) {
      if (localItem.getName().equals(localName)) {
        return localItem;
      }
    }
    return null;
  }

  public void resolveEmptyJimpleBody(SootMethod m) {
    if (!m.isStatic()) {
      RefType thisType = m.getDeclaringClass().getType();
      Local temLocal = Jimple.v().newLocal("this", thisType);
      IdentityStmt identityStmt = Jimple.v().newIdentityStmt(temLocal, Jimple.v().newThisRef(thisType));
      jb.getLocals().add(temLocal);
      addJimpleStmt(HbcInstruction.createRegisterRecordUnit(
        identityStmt, 
        null
      ));
    }

    addCompulsoryInitStmt();

    addJimpleStmt(HbcInstruction.createRegisterRecordUnit(
        Jimple.v().newReturnStmt(newTemp(m.getReturnType())), 
        null
    ));
  }
}
