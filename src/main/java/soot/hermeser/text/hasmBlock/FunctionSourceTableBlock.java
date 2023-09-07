package soot.hermeser.text.hasmBlock;

import java.util.HashMap;
import java.util.Map;

public class FunctionSourceTableBlock extends AbstractHasmBlock {
    public static Map<String, String> functionSourceTable = new HashMap<>();
    
    public FunctionSourceTableBlock() {
        super();
    }

    public Map<String, String> getArrayBufferMap() {
        return functionSourceTable;
    }

    public void readLine(String line) {
        super.readLine(line);

        line = line.replace("Function ID", "");
        line = line.replace("->", " ");

        String[] temArrayBufferInfoList = line.split(" ");

        functionSourceTable.put(temArrayBufferInfoList[0], temArrayBufferInfoList[1]);
    }
}
