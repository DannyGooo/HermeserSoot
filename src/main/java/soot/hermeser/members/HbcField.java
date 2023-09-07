package soot.hermeser.members;

import soot.*;
import soot.hermeser.text.HbcTypeFactory;


public class HbcField {
  String name;
  String className;

  public HbcField(String fieldName, String typeName) {
    this.name = fieldName;
    this.className = typeName;
  }

  public SootField makeSootField() {
    int modifier = Modifier.PUBLIC | Modifier.STATIC;

    Type type = HbcTypeFactory.toSootType(className);


    return new SootField(name, type, modifier);
  }
}
