package soot.hermeser.text.hasmBlock;

import java.util.ArrayList;

public class BufferHasmBlock extends AbstractHasmBlock {
    public static ArrayList<String[]> arrayBuffer = new ArrayList<String[]>();

    public BufferHasmBlock() {
        super();
    }

    public ArrayList<String[]> getArrayBufferMap() {
        return arrayBuffer;
    }

    public void readLine(String line) {
        super.readLine(line);

        line = line.replace("[", "");
        line = line.replace("]", "");

        String[] temArrayBufferInfoList = line.split(" ");

        String itemType = temArrayBufferInfoList[0];
        switch (itemType) {
            case "String":
            case "int":
            case "double":
                arrayBuffer.add(temArrayBufferInfoList);
                break;
            case "null":
                arrayBuffer.add(temArrayBufferInfoList);
                break;
            case "true":
                arrayBuffer.add(temArrayBufferInfoList);
                break;
            case "false":
                arrayBuffer.add(temArrayBufferInfoList);
                break;
            default:
                System.out.println(itemType + "TYPE did not consider.");
        }
    }
}
