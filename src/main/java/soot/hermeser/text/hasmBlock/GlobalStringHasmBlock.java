package soot.hermeser.text.hasmBlock;

import java.util.ArrayList;


public class GlobalStringHasmBlock extends AbstractHasmBlock{
    public static ArrayList<GlobalStringItem> globalStringArray = new ArrayList<GlobalStringItem>();
    public GlobalStringItem currentGlobalStringItem;
    public GlobalStringHasmBlock(){
        super();
    }

    public void readLine(String line) {
        super.readLine(line);
        if (line.isEmpty()){
            return;
        }
        currentGlobalStringItem = new GlobalStringItem();
        currentGlobalStringItem.setGlobalStringId(line.substring(0,line.indexOf("[")));
        currentGlobalStringItem.setOtherInfo(line.substring(line.indexOf("["),line.indexOf(":")));
        if(line.endsWith(":")){
            currentGlobalStringItem.setGlobalStringContent(" ");
        }else{
            currentGlobalStringItem.setGlobalStringContent(line.substring(line.indexOf(":")+2));
        }
        globalStringArray.add(currentGlobalStringItem);
    }
}