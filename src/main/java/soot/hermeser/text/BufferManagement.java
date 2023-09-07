package soot.hermeser.text;

import java.util.HashMap;
import java.util.Map;

public class BufferManagement{
    private Map<Integer, Integer> buffer2arrayIndexMap = new HashMap<Integer, Integer>(); 
    private Integer blankBufferIndex = 0;
    private Integer blankArrayIndex = 0;


    
    public BufferManagement(){
        buffer2arrayIndexMap.put(blankBufferIndex, blankArrayIndex);
    }

     
    public Integer getArrayIndex(Integer bufferIndex, Integer numberOfStaticElements){
        if(bufferIndex > blankBufferIndex){
            return null;
        }else if(bufferIndex.equals(blankBufferIndex)){            
            blankArrayIndex = blankArrayIndex + numberOfStaticElements;

            blankBufferIndex = blankBufferIndex + numberOfStaticElements*4 + calculateElementsLengthBuffer(numberOfStaticElements);

            buffer2arrayIndexMap.put(blankBufferIndex, blankArrayIndex);
        }
        return buffer2arrayIndexMap.get(bufferIndex);        
    }


    public Integer calculateElementsLengthBuffer(int numberOfStaticElements){        
        if((int) Math.ceil((double) Integer.toBinaryString(numberOfStaticElements).length()/4 ) > 1){
            return 2;
        }else{
            return 1;
        }
    }
}