package reunify.experiments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.hermeser.util.RNcomponentAndAPI;
import soot.hermeser.util.ReactUtil;
import soot.options.Options;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Map;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class DetectNumOfCrossLanguage {
        static String outFile = "/your/dir/to/popularRNAppsFlowDroid.csv";
    
    
        public static void main(String[] args) {
    
            String jsonFile = "/your/dir/to/popularRNApps.json";        
            String appFolder = "/your/dir/to/apps";
            String outputFolder = "/your/dir/to/results";
    
    
            String apkPath;
            String androidJars = "/your/dir/to/android-platforms";
    
            JSONArray sha256Array; 
    
            try (FileReader reader = new FileReader(jsonFile)) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(reader);
                sha256Array = (JSONArray) obj;              
            } catch (IOException | ParseException e) {
                sha256Array = new JSONArray();
                e.printStackTrace();
            }
    
            int count = 0;
    
            for (Object o : sha256Array) {
                
                count++;
                String sha256 = o.toString(); 
                
                if(readFromCSV(outFile).contains(sha256)){
                    continue;
                }
                ReactUtil.clearReactUtil();

                System.out.println("+++++++++++++++++++++++++++++" +"Currently, working on: " + count + "+++++++++++++++"+ sha256 + "+++++++++++++++++++++++++++++");
    
                String[] data = new String[6];
                data[0] = sha256;
    
                try {
    
                    apkPath = appFolder + "/" + sha256 + ".apk";
    
                    
                    String[] argsInput =
                    {
                            "-process-dir", apkPath,
                            "-android-jars", androidJars,
                            "-allow-phantom-refs",
                    };
    
                    G.reset();
                
                    Options.v().set_whole_program(true);
                    Options.v().set_src_prec(Options.src_prec_apk);
                    Options.v().set_process_multiple_dex(true);



                    Collection<Integer> unitNumList = new ArrayList<Integer>();
                    int  totalNumUnit = 0;
                    
                    PackManager.v().getPack("jtp").add(new Transform("jtp.fixedie", new BodyTransformer() {
                        @Override
                        protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
                            unitNumList.add(b.getUnits().size());
                        }
                    }));
            
                    soot.Main.main(argsInput);
                   
                    for(Integer num : unitNumList){
                        if(num != null)
                            totalNumUnit += num;
                    }
    
                    data = RNcomponentAndAPI.constructComponentAndAPI(sha256, outputFolder);
                    data[5] = Integer.toString(totalNumUnit);
    
                } catch ( ConcurrentModificationException | OutOfMemoryError e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
    
                    data[1] = "unknown";
                    data[2] = "unknown";
                    data[3] = "unknown";
                    data[4] = "unknown";
                    data[5] = "unknown";
    
                }
                            
                addCSVRow(data, outFile);
            }
    
        }
    
        public static List<String> readFromCSV(String fileDir){
            String line = "";
            String csvDelimiter = ",";
            List<String> sha256List = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(new FileReader(fileDir))) {
                            
                // Read the remaining lines (data)
                while ((line = br.readLine()) != null) {
                    // Split the line using the specified delimiter
                    String[] data = line.split(csvDelimiter);
                    
                    sha256List.add(data[0]);
                }
                
            } catch (IOException e) {
                writeAndConstructCSV(fileDir);
                e.printStackTrace();
            }
    
            return sha256List;
        }
    
        
    
        public static void writeAndConstructCSV(String fileDir){
            try {
                // create the CSVWriter object
                FileWriter writer = new FileWriter(fileDir);
                
                // write the header row
                writer.write("sha256,numModule,numNewModule,numOldModule,numViews,numOfUnits");
                // close the writer
                writer.close();
                System.out.println("Data written to CSV file successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    
    
        public static void addCSVRow(String[] rowData, String fileDir){
            String csvFile = fileDir;
            FileWriter writer = null;
            try {
                writer = new FileWriter(csvFile, true); 
                writer.write("\n"); 
                writer.write(rowData[0]+","+rowData[1]+","+rowData[2]+","+rowData[3]+","+rowData[4]+","+rowData[5]); // write the new row
    
                System.out.println("New row added to CSV file");
            } catch (IOException e) {
                System.out.println("Error adding new row to CSV file: " + e.getMessage());
            } finally {
                try {
                    writer.close(); 
                } catch (IOException e) {
                    System.out.println("Error closing CSV file: " + e.getMessage());
                }
            }
        }
    

    }