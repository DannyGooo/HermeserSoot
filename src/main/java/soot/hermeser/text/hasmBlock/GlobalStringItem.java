package soot.hermeser.text.hasmBlock;


public class GlobalStringItem {
    public String globalStringId;
    public String globalStringContent;
    public Boolean isUTF16;
    public String otherInfo;


    public GlobalStringItem(){
    }

    public void setOtherInfo(String otherInfoInput) {
        otherInfo = otherInfoInput;
        this.setUTF16(otherInfoInput.substring(otherInfoInput.indexOf("[")+1,otherInfoInput.indexOf(",")).equals("UTF-16"));
    }


    public void setGlobalStringId(String globalStringIdInput) {
        globalStringId = globalStringIdInput;
    }

    public void setGlobalStringContent(String globalStringContentInput) {
        globalStringContent = globalStringContentInput;
    }

    public void setUTF16(Boolean UTF16) {
        isUTF16 = UTF16;
    }
}