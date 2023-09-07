package reunify;
import soot.*;



public class HermesJimpleFileGenerator {
    public static String outputDir = "/your/dir/to/reactNativeInput/jimpleDataset";
    public static String hermesDirectory = "/your/dir/to/hbcTool-readyToUse";

    public static String sourceDirectory = "/your/dir/to/newArchitechtureCounterBundle.hbc";


    public static void main(String[] args) {
        String[] argsInput = {
                "-src-prec", "hbc",
                "-hbc-nativehost-path", hermesDirectory,
                "-f", "J",
                "-d", outputDir,
                "-ire",
                "-pp",
                "-allow-phantom-refs",
                "-process-dir", sourceDirectory
        };

        G.reset();    
        
        soot.Main.main(argsInput);        
    }
}