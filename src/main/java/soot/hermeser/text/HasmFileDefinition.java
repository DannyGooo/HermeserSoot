package soot.hermeser.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.MethodSource;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.hermeser.members.HbcBlock;
import soot.hermeser.members.HbcField;
import soot.hermeser.members.HbcMethod;
import soot.hermeser.text.hasmBlock.BufferHasmBlock;
import soot.hermeser.text.hasmBlock.FunctionHasmBlock;
import soot.hermeser.text.hasmBlock.FunctionSourceTableBlock;
import soot.javaToJimple.IInitialResolver.Dependencies;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasmFileDefinition {
    private static final Logger logger = LoggerFactory.getLogger(HasmFileDefinition.class);

    public String originalHasmText_;
    private String fullname_;
    private String className_ = HbcTypeFactory.JAVASCRIPT_HERMES;

    private BufferHasmBlock arrayBufferHasmBlock, objectKeyBufferHasmBlock, objectValueBufferHasmBlock;
    private FunctionSourceTableBlock functionSourceTableHasmBlock;

    private FunctionHasmBlock functionHasmBlock = new FunctionHasmBlock();

    private SootClass sootClass;
    private SootClass polymorphicClass;

    private List<String> functionNameList;

    private Map<SootMethod, HbcMethod> sootMethodToHbcMethodMap = new HashMap<>();


    private Map<String, List<InvocationItem>> calleeTypeMap = new HashMap<>();
    private List<InvocationItem> invocationItemList = new ArrayList<>();

    public HasmFileDefinition(String hasmText, String hbcFilePath) {
      fullname_ = hbcFilePath;
      originalHasmText_ = hasmText;
      parsing();
    }

    public String getClassName(){
      return className_;
    }

    public Map<String, List<InvocationItem>> getCalleeTypeMap() {
        return calleeTypeMap;
    }

    public void storeToCalleeTypeList(String calleeType, InvocationItem invocationItem) {
      if(invocationItem == null)
          return;
      invocationItemList.add(invocationItem);
    }

    public List<InvocationItem> getCalleeTypeList() {
        return invocationItemList;
    }

    public Map<SootMethod, HbcMethod> getSootMethodToHbcMethodMap() {
        return sootMethodToHbcMethodMap;
    }

    public void setPolymorphicSootClass(SootClass polymorphicClass) {
      this.polymorphicClass = polymorphicClass;
    }
  
    public SootClass getPolymorphicSootClass() {
      Scene.v().getSootClass(getClassName()+"$PolymorphicClass");
      return polymorphicClass;
    }

    public void parsing() {
      String[] hasmContent;
      hasmContent = originalHasmText_.split("\n");
      

      loadingHbc(hasmContent);
      functionNameList = functionHasmBlock.countFunctionNameAndRenameFunction();

      originalHasmText_ = null;
      hasmContent = null;
    }


    public List<HbcMethod> getHbcMethods(){
      return functionHasmBlock.getHbcMethods();
    }

    
    public BufferHasmBlock getArrayBufferHasmBlock() {
      return arrayBufferHasmBlock;
    }


    public BufferHasmBlock getObjectKeyBufferHasmBlock() {
      return objectKeyBufferHasmBlock;
    }


    public BufferHasmBlock getObjectValueBufferHasmBlock() {
      return objectValueBufferHasmBlock;
    }



    public List<String> getFunctionNameList() {
      return functionNameList;
    }


    public void loadingHbc(String[] hasmContent) {
      String codeBlockLoading = "";
      for (String line : hasmContent) {
        try {
          if (line.trim().isEmpty()) {
            continue;
          }
          if (line.equals("Bytecode File Information:")) {
            codeBlockLoading = "Bytecode File Information Block";
          } else if (line.equals("Global String Table:")) {
            codeBlockLoading = "Global String Table Block";
            continue;
          } else if (line.equals("Array Buffer:")) {
            codeBlockLoading = "Array Buffer Table Block";
            arrayBufferHasmBlock = new BufferHasmBlock();
            continue;
          } else if (line.equals("Object Key Buffer:")) {
            codeBlockLoading = "Object Key Buffer Table Block";
            objectKeyBufferHasmBlock = new BufferHasmBlock();
            continue;
          } else if (line.equals("Object Value Buffer:")) {
            codeBlockLoading = "Object Value Buffer Table Block";
            objectValueBufferHasmBlock = new BufferHasmBlock();
            continue;
          } else if (line.equals("Function Source Table:")) {
            codeBlockLoading = "Function Source Table Block";
            functionSourceTableHasmBlock = new FunctionSourceTableBlock();
            continue;
          } else if (line.startsWith("Function<global>")) {
            codeBlockLoading = "Function Block";
          } else if (line.equals("Debug filename table:")) {
            codeBlockLoading = "Debug filename table Block";
          } else if (line.equals("Debug file table:")) {
            codeBlockLoading = "Debug file table Block";
          } else if (line.equals("Debug source table:")) {
            codeBlockLoading = "Debug source table Block";
          } else if (line.equals("Debug lexical table:")) {
            codeBlockLoading = "Debug lexical table Block";
          } else if (line.equals("RegExp Bytecodes:")) {
            return;
          }

          if (codeBlockLoading.equals("Bytecode File Information Block")) {
            continue;
          } else if (codeBlockLoading.equals("Global String Table Block")) {
            continue;
          } else if (codeBlockLoading.equals("Array Buffer Table Block")) {
            arrayBufferHasmBlock.readLine(line);
            continue;
          } else if (codeBlockLoading.equals("Object Key Buffer Table Block")) {
            objectKeyBufferHasmBlock.readLine(line);
            continue;
          } else if (codeBlockLoading.equals("Object Value Buffer Table Block")) {
            objectValueBufferHasmBlock.readLine(line);
            continue;
          } else if (codeBlockLoading.equals("Function Source Table Block")) {
            functionSourceTableHasmBlock.readLine(line);
            continue;
          } else if (codeBlockLoading.equals("Function Block")) {
            functionHasmBlock.readLine(line);
          }
        } catch (Exception e) {
          System.out.println(e);
          System.out.println(line);
        }
      }
    }



    /**
     * get full name of the type, format: path 2 file name
     *
     * @return Whether the typeKind field is set.
     */
    public String getFullname() {
      if (fullname_.contains("/") & fullname_.contains(".hbc")) {
        return  fullname_.substring(
                  fullname_.lastIndexOf("/") + 1,
                  fullname_.lastIndexOf(".hbc")
                );
      } else {
        return fullname_;
      }
    }


  public void setSootClass(SootClass sootClass) {
    if(this.sootClass == null){
      this.sootClass = sootClass;
    }
  }

  public SootClass getSootClass() {
    return sootClass;
  }

  public Dependencies resolveSootClass(SootClass sootClass) {
    Dependencies dependencies = new Dependencies();
    setSootClass(sootClass);

    resolveModifier(sootClass);
    resolveFunctions(sootClass);

    return dependencies;
  }

  private void resolveModifier(SootClass sootClass) {
    sootClass.setModifiers(Modifier.PUBLIC);
  }

  private void resolveFields(SootClass declaringClass) {          
    HbcField hbcField = new HbcField("globalObject", HbcTypeFactory.HBC_GLOBAL_OBJECT);

    SootField sootField = hbcField.makeSootField();
    if (!declaringClass.declaresField(sootField.getSubSignature())) {
      declaringClass.addField(sootField);
    }   
  }

  /**
   * Visit Method Header of a hbc and generate sootMethod
   *
   * @param declaringClass
   */
  private void resolveFunctions(SootClass declaringClass) {
    List<HbcMethod> functionDefinitions = getHbcMethods();
    
    HbcMethod hbcMethod;
    SootMethod sootMethod;

    for(int i=0;i<functionDefinitions.size();i++){

      hbcMethod = functionDefinitions.get(i);

      sootMethod = hbcMethod.createEmptySootMethod(declaringClass);

      
      sootMethodToHbcMethodMap.put(sootMethod, hbcMethod);

      sootMethod.setSource(createMethodSource(hbcMethod));  
    }
  }


  /**
   * MethodSource for .hbc Method (this)
   *
   * @return
   */
  public MethodSource createMethodSource(HbcMethod hbcMethod) {
    return (m, phaseName) -> {
      Body b =null; 
      List<HbcBlock> hbcBlocks = hbcMethod.getHbcBody().getHbcBlockContainer().getBlocksList();
      if(hbcBlocks.size()==1 && hbcBlocks.get(0).getListOfHbcInstructionsCount()==0){
        b = hbcMethod.createZeroBodyForHbc();
      }else{
        b = hbcMethod.jimplify();
      }


      try {
        b.validate();
      } catch (Exception e) {
        if (!e.toString().equals("java.util.ConcurrentModificationException")) {
          logger.error("Method body validation failed for method: " + e + m.getName());
        }
      }
      m.setActiveBody(b);

      return m.getActiveBody();
    };
  }
}