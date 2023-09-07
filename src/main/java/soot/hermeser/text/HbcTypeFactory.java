package soot.hermeser.text;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.Type;
import soot.VoidType;
import soot.dotnet.types.DotnetBasicTypes;


public class HbcTypeFactory {
  public static final String JAVASCRIPT_BOOLEAN = "JavaScript.Boolean";
  public static final String JAVASCRIPT_NULL = "JavaScript.Null";
  public static final String JAVASCRIPT_UNDEFINED = "JavaScript.Undefined";
  public static final String JAVASCRIPT_NUMBER = "JavaScript.Number";
  public static final String JAVASCRIPT_BIGINT = "JavaScript.BigInt";
  public static final String JAVASCRIPT_STRING = "JavaScript.String";
  public static final String JAVASCRIPT_OBJECT = "JavaScript.Object";
  public static final String JAVASCRIPT_PROPERTY = "JavaScript.Property";

  public static final String JAVASCRIPT_ARRAY = "JavaScript.Array";

  public static final String JAVASCRIPT_THIS = "JavaScript.This";


  public static final String JAVASCRIPT_UNKNOWN = "JavaScript.Unknown";
  public static final String JAVASCRIPT_HERMES = "HermesByteCode";

  public static final String JAVASCRIPT_ENVIRONMENT = "JavaScript.Environment";
  public static final String HBC_GLOBAL_OBJECT = "Hbc.GlobalObject";
  public static final String JAVASCRIPT_FUNCTION_OUTPUT = "JavaScript.FunctionOutput";
  public static final String JAVASCRIPT_FUNCTION = "JavaScript.Function";

  

  public static final String JAVASCRIPT_VOID = "JavaScript.Void";
  public static final String JAVASCRIPT_PHI = "JavaScript.Phi";



  public static final String JAVASCRIPT_EXCEPTION = "JavaScript.Exception";
  public static final String JAVASCRIPT_ARITHMETICEXCEPTION = "JavaScript.ArithmeticException";
  public static final String JAVASCRIPT_SYSTEMEXCEPTION = "JavaScript.SystemException";
  public static final String JAVASCRIPT_ARRAYTYPEMISMATCHEXCEPTION = "JavaScript.ArrayTypeMismatchException";
  public static final String JAVASCRIPT_INVALIDCASTEXCEPTION = "JavaScript.InvalidCastException";
  public static final String JAVASCRIPT_INDEXOUTOFRANGEEXCEPTION = "JavaScript.IndexOutOfRangeException";
  public static final String JAVASCRIPT_OVERFLOWEXCEPTION = "JavaScript.OverflowException";
  public static final String JAVASCRIPT_NULLREFERENCEEXCEPTION = "JavaScript.NullReferenceException";
  public static final String JAVASCRIPT_OUTOFMEMORYEXCEPTION = "JavaScript.OutOfMemoryException";


  public static Type toSootType(String type) {
    if (type.equals(IntType.v().getTypeAsString()) || type.equals(DotnetBasicTypes.SYSTEM_INTPTR)
        || type.equals(DotnetBasicTypes.SYSTEM_UINTPTR) || type.equals("nint") || type.equals("nuint")) {
      return IntType.v();
    }
    if (type.equals(ByteType.v().getTypeAsString())) {
      return ByteType.v();
    }
    if (type.equals(CharType.v().getTypeAsString())) {
      return CharType.v();
    }
    if (type.equals(DoubleType.v().getTypeAsString())) {
      return DoubleType.v();
    }
    if (type.equals(FloatType.v().getTypeAsString())) {
      return FloatType.v();
    }
    if (type.equals(LongType.v().getTypeAsString())) {
      return LongType.v();
    }
    if (type.equals(ShortType.v().getTypeAsString())) {
      return ShortType.v();
    }
    if (type.equals(BooleanType.v().getTypeAsString())) {
      return BooleanType.v();
    }
    if (type.equals(DotnetBasicTypes.SYSTEM_VOID)) {
      return VoidType.v();
    }
    if (type.equals(HbcTypeFactory.JAVASCRIPT_VOID)) {
      return VoidType.v();
    }

    if (type.equals(DotnetBasicTypes.SYSTEM_UINT32)) {
      return IntType.v();
    }
    if (type.equals(DotnetBasicTypes.SYSTEM_SBYTE)) {
      return ByteType.v();
    }
    if (type.equals(DotnetBasicTypes.SYSTEM_DECIMAL)) {
      return DoubleType.v();
    }
    if (type.equals(DotnetBasicTypes.SYSTEM_UINT64)) {
      return LongType.v();
    }
    if (type.equals(DotnetBasicTypes.SYSTEM_UINT16)) {
      return ShortType.v();
    }

    if (type.equals(HbcTypeFactory.JAVASCRIPT_OBJECT)) {
      return toSooType(HbcTypeFactory.JAVASCRIPT_OBJECT);
    }

    return toSooType(type);
  }

  public static RefType toSooType(String type) {
    String typeName = type;
    if (typeName.charAt(0) == '[') {
      typeName = typeName.replace("[", "LEFT_BRACKET");
    }
    if (typeName.indexOf('/') >= 0) {
      typeName = typeName.replace("/", "SLASH");
    }
    if (typeName.indexOf(';') >= 0) {
      typeName = typeName.replace(";", "SEMICOLON");
    }
    return RefType.v(typeName);
  }

  public static Type toSootType(HasmFileDefinition hasmFileDefinition) {
    Type type = toSootType(hasmFileDefinition.getClassName());

    Scene.v().getSootClassUnsafe(type.toString());

    return type;

  }
}
