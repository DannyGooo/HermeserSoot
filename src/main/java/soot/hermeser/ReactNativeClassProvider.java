package soot.hermeser;

import soot.ClassProvider;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import soot.ClassSource;
import soot.Scene;
import soot.SourceLocator;
import soot.hermeser.text.HasmFileDefinition;
import soot.hermeser.text.HbcTypeFactory;
import soot.options.Options;


public class ReactNativeClassProvider implements ClassProvider {
    private static final Logger logger = LoggerFactory.getLogger(HbcClassProvider.class);

    @Override
    public ClassSource find(String className) {
      if(className.equals(HbcTypeFactory.JAVASCRIPT_HERMES)){
        ensureAssemblyIndex();
        return new HbcClassSource(className);
      }else{
        return null;
      }
    }


    private void ensureAssemblyIndex() {
      for (String path : SourceLocator.v().classPath()) {
        if(path.endsWith(".apk")){
          Scene.v().getHermesScene().setHermesAssemblyFile(
            new HbcAssemblyFile(HermesScene.extractedIndexJSFileFromAPK(path))
          );
        }
      }
    }
  


  }
  