package reunify.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import soot.G;
import soot.SourceLocator;
import soot.hermeser.HbcAssemblyFile;
import soot.hermeser.HermesScene;
import soot.options.Options;
import java.io.FileWriter;
import java.io.IOException;
import com.opencsv.CSVWriter;


public class DetectReactNative {


    public static void main(String[] args) {


        String folderPath = "/your/dir/to/VirusShare-2";
        
        File folder = new File(folderPath);
        String[] childPath = folder.list();
        folder = null;
        List<String[]> statisticData = new ArrayList<String[]>();
        
        int count = 0;
        int countRN = 0;
        int countRNNoFile = 0;


        // iterate over the files using a for loop
        for (String fileName : childPath) {
            System.out.println(fileName);

            Options.v().set_process_multiple_dex(true);

            String filePath = folderPath + "/" + fileName;
            String[] data = new String[4];
            
            String sha256 = fileName.substring(0, fileName.length() - 4);
            boolean isReactNative = false;
            String fileTypeInfo = "";
            boolean isDecompiled = false;
            try{
                if (filePath.endsWith(".apk")) {

                    List<String> className  = SourceLocator.v().getClassesUnder(filePath,".apk");
                    SourceLocator.v().clearDexClassPathExtensions();
                    

                    G.reset();
                    isDecompiled = true;
                    for(String name: className){
                        if(name.startsWith("com.facebook.react")){
                            isReactNative= true;
                            countRN++;
                            break;
                        }
                    }
                    className = null;
                    if(isReactNative){
                        try{
                            File indexAndroidBundleFile = HermesScene.extractedIndexJSFileFromAPK(filePath);
                            fileTypeInfo= HbcAssemblyFile.detectFileTypeInfo(indexAndroidBundleFile.getAbsolutePath());                            
                            indexAndroidBundleFile = null;                            
                        } catch (Exception e) {
                            fileTypeInfo = "";
                            countRNNoFile++;
                        }
                    }                 
                }
                

            }catch(Exception e){
                System.out.println("Error: " + e);
            }   
            count++;
            
            data[0] = sha256;
            data[1] = String.valueOf(isReactNative);
            data[2] = fileTypeInfo;
            data[3] = String.valueOf(isDecompiled);
            statisticData.add(data);
            System.out.println("count: " + count);   
            System.out.println("countRN: " + countRN);
            System.out.println("countRNNoFile: " + countRNNoFile);
        }

        String outFile = "/your/dir/to/VirusShare-2.csv";
        try {
            // create the CSVWriter object
            CSVWriter writer = new CSVWriter(new FileWriter(outFile));
            
            // write the header row
            String[] header = {"sha256", "isReactNative", "fileTypeInfo", "isDecompiled"};
            writer.writeNext(header);
            
            for(String[] rowData: statisticData){
                writer.writeNext(rowData);
            }
            // close the writer
            writer.close();
            System.out.println("Data written to CSV file successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
