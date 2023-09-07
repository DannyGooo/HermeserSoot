package soot.hermeser.members;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;


import java.util.Arrays;


import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.hermeser.members.HbcMethod;
import soot.hermeser.text.HbcTypeFactory;
import soot.shimple.internal.PhiNodeManager;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;

/**
 * Represents a .hbc Method SourceLocator -> ClassProvider -> ClassSource ->
 * MethodSource (HbcMethod)
 */
public class HbcMethod {
  private static final Logger logger = LoggerFactory.getLogger(HbcMethod.class);

  private SootMethod sootMethod = null;

  private HbcMethodKind hbcMethodKind;

  public static final String MAIN_METHOD_SIGNATURE = "JavaScript.FunctionOutput global(JavaScript.Object)";

  private HbcBody hbcBody_ = new HbcBody(this);
  private String functionName;
  private String hasmFunctionName;

  private int parameterNum;
  private int parameterLoadedNum = 1;

  private int registerNum;
  private int symbolsNum;
  private String offsetDebugTableSource;
  private String offsetDebugTableLexical;

  private boolean isModuleEntryMethod = false;

  private List<String[]> exceptionHandlersArray;
  private List<String> jumTableArray;


  private List<HbcMethod> declaredClosureItemsList = new ArrayList<>();
  private List<HbcMethod> parentClosureItemsList = new ArrayList<>();


  private boolean isPossibleNativeModules = false;
  private boolean isPossibleUi = false;
  private boolean isPossilbeTurboModule = false;
  private boolean isSystemModule = false;

  public enum HbcMethodKind {
    NORMAL,
    ANONYMOUS, 
    DUPLICATED, 
  }


  public HbcMethod(String name, int parameterNum, int registerNum, int symbolsNum) {
    this.functionName = name;
    this.hasmFunctionName = name;

    this.parameterNum = parameterNum;
    this.registerNum = registerNum;
    this.symbolsNum = symbolsNum;
    this.hbcMethodKind = HbcMethodKind.NORMAL;
  }

  public void setIsSystemModule(boolean isSystemModule) {
      this.isSystemModule = isSystemModule;
  }

  public boolean isSystemModule() {
      return isSystemModule;
  }

  public void setPossibleUi(boolean isPossibleUi) {
      this.isPossibleUi = isPossibleUi;
  }

  public boolean isPossibleUi() {
      return isPossibleUi;
  }

  public void setPossibleNativeModules(boolean isPossibleNativeModules) {
      this.isPossibleNativeModules = isPossibleNativeModules;
  }

  public boolean isPossibleNativeModules() {
      return isPossibleNativeModules;
  }

  public void setPossilbeTurboModule(boolean isPossilbeTurboModule) {
      this.isPossilbeTurboModule = isPossilbeTurboModule;
  }

  public boolean isPossilbeTurboModule() {
      return isPossilbeTurboModule;
  }



  public void setParameterLoadedNum(int parameterLoaded) {
    int temValue = parameterLoaded+1;
    if(temValue > this.parameterLoadedNum){
      this.parameterLoadedNum = temValue;
    }
  }

  public int getParameterLoadedNum() {
      return parameterLoadedNum;
  }

  public void clear(){
    hbcBody_ = new HbcBody(this, sootMethod.getActiveBody());

    parentClosureItemsList.clear();
    declaredClosureItemsList.clear();

    if(jumTableArray != null){
      jumTableArray.clear();
      jumTableArray = null;
    }

    if(exceptionHandlersArray != null){
      exceptionHandlersArray.clear();
      exceptionHandlersArray = null;
    }    
  }

  public void cleanMemory(){
    hbcBody_ = null;
    parentClosureItemsList = null;
    declaredClosureItemsList = null;
  }


  public void addDeclaredClosureItem(HbcMethod declaredClosureItems) {
    this.declaredClosureItemsList.add(declaredClosureItems);
  }

  public void addParentClosureItem(HbcMethod parentClosureItems) {
    this.parentClosureItemsList.add(parentClosureItems);
  }

  public List<HbcMethod> getDeclaredClosureItemsList() {
      return declaredClosureItemsList;
  }

  public List<HbcMethod> getParentClosureItemsList() {
      return parentClosureItemsList;
  }


  public static void handleClosureDeclaration(HbcMethod declaredHbcMethod, HbcMethod targetedHbcMethod) {
    declaredHbcMethod.addDeclaredClosureItem(targetedHbcMethod);
    targetedHbcMethod.addParentClosureItem(declaredHbcMethod);
  }

  public SootMethod getSootMethod() {
    return sootMethod;
  }

  public void setSootMethod(SootMethod sootMethod) {
    this.sootMethod = sootMethod;
  }

  public String getName() {
    return functionName;
  }

  public void setName(String name) {
    this.functionName = name;
  }

  public String getHasmName() {
    return hasmFunctionName;
  }

  public void setHasmName(String hasmName) {
    this.hasmFunctionName = hasmName;
  }

  public HbcMethodKind getHbcMethodKind() {
    return hbcMethodKind;
  }

  public void setHbcMethodKind(HbcMethodKind hbcMethodKind) {
    this.hbcMethodKind = hbcMethodKind;
  }


  public void initializeExceptionalHandlersArray() {
      exceptionHandlersArray = new ArrayList<String[]>();
  }

  public void initializeJumpTableArray() {
      jumTableArray = new ArrayList<String>();
  }

  public List<String[]> getExceptionHandlersArray() {
      return exceptionHandlersArray;
  }

  public List<String> getJumpTable() {
      return jumTableArray;
  }

  public void setOffsetDebugTableInfo(String source, String lexical) {
      offsetDebugTableSource = source;
      offsetDebugTableLexical = lexical;
  }


  public HbcBody getHbcBody() {
      return hbcBody_;
  }

  public Body jimplify() {
    JimpleBody b;
    try {
      b = hbcBody_.jimplify(sootMethod);    
    } catch (Exception e) {
      logger.warn("Error while generating jimple body of " + " " + sootMethod.getName()
          + " declared in class " + sootMethod.getDeclaringClass().getName() + "!");
      logger.warn(e.getMessage());
      if (Options.v().ignore_methodsource_error()) {
        logger.warn("Ignore errors in generation due to the set parameter. Generate empty Jimple Body.");
        b = Jimple.v().newBody(sootMethod);
        hbcBody_.resolveEmptyJimpleBody(sootMethod);
      } else {
        throw e;
      }
    }
    return b;
  }

  public SootMethod createEmptySootMethod(SootClass declaringClass) {
    List<Type> parameters = new ArrayList<Type>();
    for(int i=0; i<parameterLoadedNum; i++){
      parameters.add(HbcTypeFactory.toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT));
    }

    Type return_type = HbcInstruction.getOrCreateType("JavaScript.Function." + declaringClass.getName() + "." + getName() + "." + HbcTypeFactory.JAVASCRIPT_FUNCTION_OUTPUT);
    int modifier = Modifier.PUBLIC | Modifier.STATIC;
    SootMethod sm = Scene.v().makeSootMethod(getName(), parameters, return_type, modifier);
    
    setSootMethod(sm);
    declaringClass.addMethod(sootMethod);
    return sm;
  }

  public Body createZeroBodyForHbc(){
    Body jb = hbcBody_.jimplify(sootMethod);
    
    Type temType = sootMethod.getReturnType();
    Local local = Jimple.v().newLocal("emptyReturn", temType);
    jb.getLocals().add(local);
    

    jb.getUnits().add(
      Jimple.v().newAssignStmt(
        local, 
        jb.getLocals().getFirst()
      )
    );


    jb.getUnits().add(
      Jimple.v().newReturnStmt(local)
    );
    return jb;
  }


 

  public static List<List<Block>> searchPath(Block targetedBlock, ExceptionalBlockGraph cfg, List<Block> currentBlockPath){
    Block currentBlock = currentBlockPath.get(currentBlockPath.size()-1);

    List<Block> predBlocks = cfg.getSuccsOf(currentBlock);
    List<List<Block>> blockPathList = new ArrayList<List<Block>>();

    for(Block block : predBlocks){
      if(block == targetedBlock){
        List<Block> temBlockPath = new ArrayList<Block>(currentBlockPath);
        temBlockPath.add(block);
        blockPathList.add(temBlockPath);        
      }else if (currentBlockPath.contains(block)){
        continue;
      }else{
        List<Block> temBlockPath = new ArrayList<Block>(currentBlockPath);
        temBlockPath.add(block);
        List<List<Block>> temPath = searchPath(targetedBlock, cfg, temBlockPath);
        blockPathList.addAll(temPath);
      }
    }
    return blockPathList;
  }
}