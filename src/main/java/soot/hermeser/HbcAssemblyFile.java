package soot.hermeser;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.hermeser.text.HasmFileDefinition;
import soot.options.Options;


import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class HbcAssemblyFile {
  private static final Logger logger = LoggerFactory.getLogger(HbcAssemblyFile.class);



  private HasmFileDefinition hasmFileDefinition;


  private final String fullyQualifiedAssemblyPathFilename;
  

  private final String hermesToolDir;

  private File assemblyFile;

  static String hbcCommandOption = "-c \"disassemble;quit\"";


  public HbcAssemblyFile(File assemblyFileInput) {
    assemblyFile = assemblyFileInput;
    this.fullyQualifiedAssemblyPathFilename = assemblyFileInput.getAbsolutePath();
    this.hermesToolDir = Options.v().hbc_nativehost_path();
  }


  public File getAssemblyFile() {
      return assemblyFile;
  }

  public void setAssemblyFile(File assemblyFile) {
      this.assemblyFile = assemblyFile;
  }

  public boolean isAssembly(File file) {
    return true;
  }



  public HasmFileDefinition getHasmFileDefinition() {
    if (hasmFileDefinition == null) {
      extractHasmFileDefinition();
    }

    return hasmFileDefinition;
  }


  public void extractHasmFileDefinition() {
    try {
      hasmFileDefinition = resolve(
        hermesToolDir,
        this.fullyQualifiedAssemblyPathFilename
      );
    } catch (Exception e) {
      if (Options.v().verbose()) {
        logger.warn(getTypeDefinitionFullName() + " has no types. Error of protobuf message: " + e.getMessage());
      }
    }
  }

  public String getTypeDefinitionFullName() {
    return getHasmFileDefinition().getClassName();
  }




  public static HasmFileDefinition resolve(String pathToHermesToolDir, String fullyQualifiedAssemblyPathFilename) throws IOException {      
      String fileTypeInfo = detectFileTypeInfo(fullyQualifiedAssemblyPathFilename);

      String disassembledString = disassemblyHermesBytecode(fileTypeInfo, pathToHermesToolDir, fullyQualifiedAssemblyPathFilename);
      
  
      return new HasmFileDefinition(
          disassembledString,
          fullyQualifiedAssemblyPathFilename
      );
  }




  public static String disassemblyHermesBytecode(String fileInfo, String hbcToolDir, String hbcFileDir){
    List<String> fileTypeInfoList = Arrays.asList(fileInfo.split(" "));
    List<String> commandParams;
    
    String hbcToolOS = hbcToolDir + "/" + getOSType() + "/";
    if(fileTypeInfoList.get(fileTypeInfoList.size()-2).equals("version") && fileTypeInfoList.get(fileTypeInfoList.size()-5).matches("Hermes")){
      int hbcVersion = Integer.valueOf(fileTypeInfoList.get(fileTypeInfoList.size()-1));
      String hbcToolVersionFile = hbcToolOS +  "version-" + hbcVersion + "/";
      if(hbcVersion == 51){
        commandParams = Arrays.asList(hbcToolVersionFile + "hermesc", "-dump-bytecode", "-O0", hbcFileDir);;
      } else if (hbcVersion >=60 && hbcVersion <= 94){
        commandParams = Arrays.asList(hbcToolVersionFile + "hbcdump" ,hbcFileDir, "-c","disassemble;quit");
      } else {
        throw new RuntimeException("Cannot find proper version for hbc tool:"  +  hbcVersion);
      }
      logger.warn("Use the version " + hbcVersion  + " hbctool to decompile the Hermes bytecode.");
    }else{
      commandParams = Arrays.asList(hbcToolOS + "general/hermesc" , "-dump-bytecode", "-O0", hbcFileDir);
      logger.warn("Use the version 94 hbctool to compile the JS code into Hermes bytecode.");
    }

    return runCommandForOutput(commandParams);
  }

  public static String detectFileTypeInfo(String hbcFileDir){
    List<String> params = Arrays.asList("file",hbcFileDir);
    return runCommandForOutput(params);
  }




  public static String runCommandForOutput(List<String> params) {
      ProcessBuilder pb = new ProcessBuilder(params);
      Process p;
      String result = "";
      try {
          p = pb.start();
          final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
  
          StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
          reader.lines().iterator().forEachRemaining(sj::add);
          result = sj.toString();
  
          p.waitFor();
          p.destroy();
      } catch (Exception e) {
          e.printStackTrace();
      }
      return result;
  }


  public static String getOSType() {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Windows")) {
      osName = "windows";
    } else if (osName.startsWith("Linux")) {
      osName = "ubuntu";
    } else if (osName.startsWith("Mac")) {
      osName = "macOS";
    } else {
      throw new RuntimeException("Cannot find proper OS type for hbc tool");
    }
    return osName;
  }
}
