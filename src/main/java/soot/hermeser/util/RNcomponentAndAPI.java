package soot.hermeser.util;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Scene;
import soot.SootClass;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.FileReader;

import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.json.simple.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.google.gson.JsonIOException;



public class RNcomponentAndAPI {
    
    public static String[] constructComponentAndAPI( String sha256, String resultsDir) {
        String jsonFilePath = resultsDir +"/" + sha256 + ".json";
        
        String[] result = new String[6];

        Map<SootClass, Collection<SootMethod>> reactMethods = ReactUtil.getReactModules();
        Map<SootClass, Collection<SootMethod>> reactViews = ReactUtil.getReactViews();

        List<String> reactNewNativeModules= new ArrayList<String>();
        List<String> reactOldNativeModules= new ArrayList<String>();




        JSONObject outputObject = new JSONObject();
        List<JSONObject> reactNativeModulesList = new ArrayList<JSONObject>();
        List<JSONObject> reactNativeViewList = new ArrayList<JSONObject>();
        
        try {
            for(SootClass sc : reactMethods.keySet()){
                JSONObject nativeModulesClassItem = new JSONObject();
                List<String> reactNativeModulesItemString= new ArrayList<String>();

                for(SootMethod sm : reactMethods.get(sc)){
                    reactNativeModulesItemString.add(sm.getName());
                }
                nativeModulesClassItem.put(sc.getName(), reactNativeModulesItemString);
                reactNativeModulesList.add(nativeModulesClassItem);
            }


            for(SootClass sc : reactViews.keySet()){
                JSONObject nativeViewClassItem = new JSONObject();
                List<String> reactNativeModulesItemString= new ArrayList<String>();

                for(SootMethod sm : reactViews.get(sc)){
                    reactNativeModulesItemString.add(sm.getName());
                }
                nativeViewClassItem.put(sc.getName(), reactNativeModulesItemString);
                reactNativeViewList.add(nativeViewClassItem);
            }

            for(SootClass sootClassItem : ReactUtil.reactNewModulesList){
                reactNewNativeModules.add(sootClassItem.getName());
            }

            for(SootClass sootClassItem : ReactUtil.reactOldModulesList){
                reactOldNativeModules.add(sootClassItem.getName());
            }






            outputObject.put("ReactModules", reactNativeModulesList); 
            outputObject.put("ReactViews", reactNativeViewList);            

            outputObject.put("numReactModules", reactMethods.size());
            outputObject.put("numNewReactModules", ReactUtil.reactNewModulesList.size());
            outputObject.put("numOldReactModules", ReactUtil.reactOldModulesList.size());
            outputObject.put("NewReactModules",reactNewNativeModules);
            outputObject.put("OldReactModules", reactOldNativeModules);
            outputObject.put("numReactViews", reactViews.size());

            result[0] = sha256;
            result[1] = Integer.toString(reactMethods.size());
            result[2] = Integer.toString(ReactUtil.reactNewModulesList.size());
            result[3] = Integer.toString(ReactUtil.reactOldModulesList.size());
            result[4] = Integer.toString(reactViews.size());

            
           
        } catch (JsonIOException e) {
            e.printStackTrace();
            result[0] = sha256;
            result[1] = "unknown";
            result[2] = "unknown";
            result[3] = "unknown";
            result[4] = "unknown";            

        }
 
        try (PrintWriter out = new PrintWriter(new FileWriter(jsonFilePath))) {
            out.write(outputObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

}
