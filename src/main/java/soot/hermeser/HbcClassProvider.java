package soot.hermeser;

import com.google.common.base.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ClassProvider;
import soot.ClassSource;
import soot.Scene;
import soot.SourceLocator;
import soot.hermeser.text.HasmFileDefinition;
import soot.hermeser.text.HbcTypeFactory;
import soot.options.Options;


public class HbcClassProvider implements ClassProvider {
  private static final Logger logger = LoggerFactory.getLogger(HbcClassProvider.class);

  @Override
  public ClassSource find(String className) {
    ensureAssemblyIndex();

    File assemblyFile = SourceLocator.v().dexClassIndex().get(className);

    return assemblyFile == null ? null : new HbcClassSource(className);
  }

  private void ensureAssemblyIndex() {
    Map<String, File> index = SourceLocator.v().dexClassIndex();
    if (index == null) {
      if (Options.v().verbose()) {
        logger.info("Creating assembly index");
      }
      index = new HashMap<>();
      buildAssemblyIndex(index, SourceLocator.v().classPath());
      SourceLocator.v().setDexClassIndex(index);
      if (Options.v().verbose()) {
        logger.info("Created assembly index");
      }
    }
  }

  private void buildAssemblyIndex(Map<String, File> index, List<String> classPath) {
    if (Strings.isNullOrEmpty(Options.v().hbc_nativehost_path())) {
      throw new RuntimeException("hbc NativeHost Path is not set! Use -dotnet-nativehost-path Soot parameter!");
    }

    for (String path : classPath) {
      try {
        File file = new File(path);
        if (file.exists()) {
          File[] listFiles = file.isDirectory() ? file.listFiles(File::isFile) : new File[] { file };
          for (File f : Objects.requireNonNull(listFiles)) {
            String canonicalPath = f.getCanonicalPath();
            if (Options.v().verbose()) {
              logger.info("Process " + canonicalPath + " file");
            }
            if (!canonicalPath.endsWith(".hbc")) {
              continue;
            }
            HbcAssemblyFile assemblyFile = new HbcAssemblyFile(f);    
            if (!index.containsKey(HbcTypeFactory.JAVASCRIPT_HERMES)) {
              index.put(
                HbcTypeFactory.JAVASCRIPT_HERMES,
                f
              );
            }

            Scene.v().getHermesScene().setHermesAssemblyFile(assemblyFile);
          }
        }
      } catch (Exception e) {
        logger.warn("exception while processing assembly file '" + path + "'");
        logger.warn("Exception: " + e);
      }
    }

  }
}
