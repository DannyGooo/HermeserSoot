package soot.hermeser;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ClassSource;
import soot.Scene;
import soot.SootClass;
import soot.hermeser.text.HasmFileDefinition;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

public class HbcClassSource extends ClassSource {
  private static final Logger logger = LoggerFactory.getLogger(HbcClassSource.class);
  protected HbcAssemblyFile assemblyFile;

  public HbcClassSource(String className) {
    super(className);
    this.assemblyFile = Scene.v().getHermesScene().getHermesAssemblyFile();
  }

  @Override
  public Dependencies resolve(SootClass sc) {
    if (Options.v().verbose()) {
      logger.info("resolving " + className + " type definition from file " + assemblyFile.getAssemblyFile().getPath());
    }


    if (assemblyFile != null) {
      sc.setApplicationClass();
    }

    HasmFileDefinition hasmFileDefinition = assemblyFile.getHasmFileDefinition();

    return hasmFileDefinition.resolveSootClass(sc);
  }
}