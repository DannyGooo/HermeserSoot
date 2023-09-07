package soot.hermeser.text.hasmBlock;

import java.util.*;

import soot.hermeser.members.HbcBlock;
import soot.hermeser.members.HbcMethod;
import soot.hermeser.text.HbcInstructionFormat;

public class FunctionHasmBlock extends AbstractHasmBlock {

    private List<HbcMethod> functionArrayList = new ArrayList<>();
    public HbcBlock currentBcBlock;

    
    public FunctionHasmBlock() {
        super();
    }

    public List<HbcMethod> getHbcMethods(){
        return functionArrayList;
    }

    public List<String> countFunctionNameAndRenameFunction() {
        Map<String, Integer> mp = new HashMap<>();
        int noNameIndex = 0;

        for (HbcMethod hbcMethod : functionArrayList) {
            String functionName = hbcMethod.getName();

            if (functionName.isEmpty()) {
                functionName = "hermesNoNameFunction_" + noNameIndex;
                hbcMethod.setName(functionName);
                noNameIndex += 1;
                hbcMethod.setHbcMethodKind(HbcMethod.HbcMethodKind.ANONYMOUS);
            } else {
                hbcMethod.setHbcMethodKind(HbcMethod.HbcMethodKind.NORMAL);
            }

            if (mp.containsKey(functionName)) {
                mp.put(functionName, mp.get(functionName) + 1);
            } else {
                mp.put(functionName, 1);
            }            
        }
        
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (HbcMethod hbcMethod : functionArrayList) {
            String functionName = hbcMethod.getName();
            if (functionName.matches(".*\\s+.*")) {
                functionName = functionName.replaceAll("\\s+", "_");

                while (mp.containsKey(functionName)) {
                    functionName = functionName + "_";
                }

                hbcMethod.setName(functionName);
            }


            if (nameCountMap.containsKey(functionName)) {
                nameCountMap.put(functionName, nameCountMap.get(functionName) + 1);
            } else {
                nameCountMap.put(functionName, 1);
            }
        }

        List<String> resultedFunctionNameList = renameFunction(nameCountMap);        

        mp = null;
        nameCountMap = null;

        return resultedFunctionNameList;
    }

    public List<String> renameFunction(Map<String, Integer> functionNameFrequencyMap) {
        Map<String, Integer> mp = new HashMap<>();
        List<String> resultedFunctionNameList = new ArrayList<>();
        for (HbcMethod hbcMethod : functionArrayList) {
            String functionName = hbcMethod.getName();
            if (functionNameFrequencyMap.get(functionName) > 1) {
                hbcMethod.setHbcMethodKind(HbcMethod.HbcMethodKind.NORMAL);

                if (mp.containsKey(functionName)) {
                    mp.put(functionName, mp.get(functionName) + 1);
                } else {
                    mp.put(functionName, 1);
                }

                hbcMethod.setName("hermesDuplicatedFunction_" + functionName + "_"
                        + (mp.get(functionName)));

            }
            resultedFunctionNameList.add(hbcMethod.getName());
        }
        
        return resultedFunctionNameList;
    }


    private static int nthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0)
            return string.length();
        return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    public String[] getLineOpcodeDetail(String line) {
        int firstQuoteIndex = line.indexOf("\"");
        int lastQuoteIndex = line.lastIndexOf("\"");
        int firstCommaIndex = line.indexOf(",");
        int lastCommaIndex = line.lastIndexOf(",");
        int secondLastCommaIndex;

        ArrayList<String> outputStringArray = new ArrayList<>();
        String[] output;

        if (line.startsWith("CreateRegExp")) {
            secondLastCommaIndex = nthLastIndexOf(2, ",", line);

            outputStringArray.add("CreateRegExp");
            outputStringArray.add(line.substring("CreateRegExp".length(), firstCommaIndex).trim());
            outputStringArray.add(line.substring(firstCommaIndex + 1, secondLastCommaIndex).trim());
            outputStringArray.add(line.substring(secondLastCommaIndex + 1, lastCommaIndex).trim());
            outputStringArray.add(line.substring(lastCommaIndex + 1).trim());
            output = outputStringArray.toArray(new String[] {});
            return output;
        }

        if (firstQuoteIndex >= 0) {
            outputStringArray = new ArrayList<String>(
                    Arrays.asList(line.substring(0, firstQuoteIndex - 1).replace(",", "").trim().split("\\s+"))
            );
            if (firstQuoteIndex == lastQuoteIndex) {
                outputStringArray.add(line.substring(firstQuoteIndex));
            } else {
                outputStringArray.add(line.substring(firstQuoteIndex));
            }
            output = outputStringArray.toArray(new String[] {});
            return output;
        } else {
            output = line.replace(",", "").trim().split("\\s+");
            return output;
        }
    }

    public boolean checkIsOpcode(String line) {
        if (line.matches("(\\s){4}[A-Z].*")){
            return true;
        }else{
            return false;
        }
    }

    public HbcMethod currentHasmFunction;
    public String currentTargetName;

    public void readLine(String line) {
        super.readLine(line);


        if (line.startsWith("Function<") || line.startsWith("NCFunction<")) {

            String functionName;
            int parameterNum;
            int registerNum;
            int symbolsNum;

            functionName = line.substring(line.indexOf("<") + 1, line.indexOf(">"));

            String[] temFunctionInfoList = line.substring(line.indexOf(">") + 1).split(" ");

            parameterNum = Integer.parseInt(temFunctionInfoList[0].substring(temFunctionInfoList[0].indexOf("(") + 1));
            registerNum = Integer.parseInt(temFunctionInfoList[2]);
            symbolsNum = Integer.parseInt(temFunctionInfoList[4]);

            currentHasmFunction = new HbcMethod(functionName, parameterNum, registerNum, symbolsNum);

            currentTargetName = "L0";
            currentBcBlock = new HbcBlock(currentTargetName);
            currentHasmFunction.getHbcBody().getHbcBlockContainer().addBcBlock(currentBcBlock);

            functionArrayList.add(currentHasmFunction);
            return;
        }

        if (line.startsWith("L") & line.endsWith(":")) {
            currentTargetName = line.substring(0, line.indexOf(":"));
            currentBcBlock = new HbcBlock(currentTargetName);
            currentHasmFunction.getHbcBody().getHbcBlockContainer().addBcBlock(currentBcBlock);
            return;
        }

        if (line.trim().startsWith("Offset in debug table:")) {
            String source;
            String lexical;
            String[] temOffsetInDebugTableList = line.replace(",", "").split(" ");
            source = temOffsetInDebugTableList[2];
            lexical = temOffsetInDebugTableList[4];
            currentHasmFunction.setOffsetDebugTableInfo(source, lexical);
            return;
        }

        if (line.startsWith("/") && line.endsWith("]") && line.contains("[")) {
            return;
        }

        if (line.trim().startsWith("Jump Tables:")) {
            currentHasmFunction.initializeJumpTableArray();
            return;
        }

        if (line.trim().equals("Exception Handlers:")) {
            currentHasmFunction.initializeExceptionalHandlersArray();
            return;
        }

        if (currentHasmFunction.getExceptionHandlersArray() == null && currentHasmFunction.getJumpTable() != null) {
            if (line.contains("offset"))
                return;

            String[] temExceptionHandlersList = line.split(":");
            currentHasmFunction.getJumpTable().add(temExceptionHandlersList[1].trim());
            return;
        }

        if (currentHasmFunction.getExceptionHandlersArray() != null) {
            String[] temExceptionHandlersList = line.replace(",", "").split(" ");
            currentHasmFunction.getExceptionHandlersArray().add(new String[] { temExceptionHandlersList[3],
                    temExceptionHandlersList[6], temExceptionHandlersList[9] });
            return;
        }

        List<HbcInstructionFormat> listOfHbcInstructions = currentBcBlock.getListOfHbcInstructionsList();

        if (!checkIsOpcode(line)) {
            HbcInstructionFormat hbcInstructionFormat = listOfHbcInstructions.get(listOfHbcInstructions.size() - 1);
            int indexOfLastArg = hbcInstructionFormat.opcodeDetailList.length - 1;
            hbcInstructionFormat.opcodeDetailList[indexOfLastArg] = hbcInstructionFormat.opcodeDetailList[indexOfLastArg]
                    + "\\x0A" + line;
            return;
        }

        line = line.trim();
        String[] opcodeDetailList = getLineOpcodeDetail(line);

        switch (opcodeDetailList[0]) {
            case "LoadParam":
            case "LoadParamLong":                ;
                currentHasmFunction.setParameterLoadedNum(Integer.valueOf(opcodeDetailList[2]));
            case "CompleteGenerator":
            case "SwitchImm":
            case "Unreachable":
            case "Debugger":
            case "AsyncBreakCheck":
            case "StartGenerator":
            case "SaveGenerator":
            case "ResumeGenerator":
            case "Store8":
            case "Store16":
            case "Store32":
            case "PutByVal":
            case "PutOwnGetterSetterByVal":
            case "DelById":
            case "DelByIdLong":
            case "Construct":
            case "ConstructLong":
            case "IteratorNext":
            case "IteratorBegin":
            case "Negate":
            case "Not":
            case "BitNot":
            case "TypeOf":
            case "ToNumeric":
            case "Dec":
            case "Inc":
            case "GetByVal":
            case "DelByVal":
            case "GetPNameList":
            case "GetNextPName":
            case "DirectEval":
            case "ThrowIfEmpty":
            case "CreateThis":
            case "SelectObject":
            case "CoerceThisNS":
            case "ToNumber":
            case "ToInt32":
            case "AddEmptyString":
            case "GetArgumentsPropByVal":
            case "GetArgumentsLength":
            case "Loadi8":
            case "Loadu8":
            case "Loadi16":
            case "Loadu16":
            case "Loadi32":
            case "Loadu32":
            case "NewObjectWithParent":
            case "NewObject":
            case "NewArray":
            case "GetEnvironment":
            case "GetNewTarget":
            case "CallDirect":
            case "CallDirectLongIndex":
            case "CallBuiltin":
            case "CallBuiltinLong":
            case "ProfilePoint":
            case "ReifyArguments":
            case "LoadConstEmpty":
            case "LoadThisNS":
            case "CreateRegExp":
            case "IteratorClose":
            case "NewArrayWithBuffer":
            case "NewArrayWithBufferLong":
            case "NewObjectWithBuffer":
            case "NewObjectWithBufferLong":
            case "DeclareGlobalVar":
            case "LoadConstUndefined":
            case "LoadConstNull":
            case "LoadConstTrue":
            case "LoadConstFalse":
            case "LoadConstZero":
            case "LoadConstUInt8":
            case "LoadConstInt":
            case "LoadConstDouble":
            case "LoadConstString":
            case "LoadConstStringLongIndex":
            case "GetGlobalObject":
            case "Eq":
            case "StrictEq":
            case "Neq":
            case "StrictNeq":
            case "Less":
            case "LessEq":
            case "Greater":
            case "GreaterEq":
            case "InstanceOf":
            case "IsIn":
            case "Mul":
            case "MulN":
            case "Div":
            case "DivN":
            case "Mod":
            case "Sub":
            case "SubN":
            case "LShift":
            case "RShift":
            case "URshift":
            case "BitAnd":
            case "BitXor":
            case "BitOr":
            case "Sub32":
            case "Mul32":
            case "Divi32":
            case "Divu32":
            case "Mov":
            case "MovLong":
            case "PutNewOwnByIdShort":
            case "PutNewOwnById":
            case "PutNewOwnByIdLong":
            case "PutNewOwnNEById":
            case "PutNewOwnNEByIdLong":
            case "PutOwnByIndex":
            case "PutOwnByIndexL":
            case "PutOwnByVal":
            case "PutById":
            case "PutByIdLong":
            case "TryPutById":
            case "TryPutByIdLong":
            case "GetByIdShort":
            case "GetById":
            case "GetByIdLong":
            case "TryGetById":
            case "TryGetByIdLong":
            case "Add":
            case "AddN":
            case "Add32":
            case "Ret":
            case "CreateEnvironment":
            case "CreateClosure":
            case "CreateClosureLongIndex":
            case "CreateGeneratorClosure":
            case "CreateGeneratorClosureLongIndex":
            case "CreateGenerator":
            case "CreateGeneratorLongIndex":
            case "CreateAsyncClosure":
            case "CreateAsyncClosureLongIndex":
            case "GetBuiltinClosure":            
            case "StoreToEnvironment":
            case "StoreToEnvironmentL":
            case "StoreNPToEnvironment":
            case "StoreNPToEnvironmentL":
            case "LoadFromEnvironment":
            case "LoadFromEnvironmentL":
            case "Call":
            case "CallLong":
            case "Call1":
            case "Call2":
            case "Call3":
            case "Call4":
            case "JLess":
            case "JNotLess":
            case "JLessN":
            case "JNotLessN":
            case "JLessEqual":
            case "JNotLessEqual":
            case "JLessEqualN":
            case "JNotLessEqualN":
            case "JGreater":
            case "JNotGreater":
            case "JGreaterN":
            case "JNotGreaterN":
            case "JGreaterEqual":
            case "JNotGreaterEqual":
            case "JGreaterEqualN":
            case "JNotGreaterEqualN":
            case "JEqual":
            case "JNotEqual":
            case "JStrictEqual":
            case "JStrictNotEqual":
            case "JmpTrue":
            case "JmpFalse":
            case "JmpUndefined":
            case "Jmp":
            case "JLessLong":
            case "JNotLessLong":
            case "JLessNLong":
            case "JNotLessNLong":
            case "JLessEqualLong":
            case "JNotLessEqualLong":
            case "JLessEqualNLong":
            case "JNotLessEqualNLong":
            case "JGreaterLong":
            case "JNotGreaterLong":
            case "JGreaterNLong":
            case "JNotGreaterNLong":
            case "JGreaterEqualLong":
            case "JNotGreaterEqualLong":
            case "JGreaterEqualNLong":
            case "JNotGreaterEqualNLong":
            case "JEqualLong":
            case "JNotEqualLong":
            case "JStrictEqualLong":
            case "JStrictNotEqualLong":
            case "JmpTrueLong":
            case "JmpFalseLong":
            case "JmpUndefinedLong":
            case "JmpLong":
            case "Catch":
            case "Throw":

                listOfHbcInstructions.add(
                    new HbcInstructionFormat(opcodeDetailList)
                );
                break;
            default:
                HbcInstructionFormat hbcInstructionFormat = listOfHbcInstructions.get(listOfHbcInstructions.size() - 1);
                int indexOfLastArg = hbcInstructionFormat.opcodeDetailList.length - 1;
                hbcInstructionFormat.opcodeDetailList[indexOfLastArg] = hbcInstructionFormat.opcodeDetailList[indexOfLastArg]
                        + "\\x0A" + line;
                return;

        }
    }
}