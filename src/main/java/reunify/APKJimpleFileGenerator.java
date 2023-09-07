package reunify;


import java.io.File;

import soot.*;

import soot.options.Options;

public class APKJimpleFileGenerator {
    private static String androidJar = "/your/dir/to/android-platforms";
    public static String outputDir = "/your/dir/to/jimpleDataset/app";

    static String androidDemoPath = System.getProperty("user.dir") + File.separator + "demo" + File.separator + "Android";
    static String apkPath = "/your/dir/to/mostPopularRN/apps/76AAB1681FDC3B7B0A9AAF8FD496A35FA7769192A61A363D230B6527607E0F66.apk";
    

    public static void main(String[] args) {

        String[] argsInput =
        {
                "-process-dir", apkPath,
                "-android-jars", androidJar,
                // "-ire",
                // "-pp",
                "-allow-phantom-refs",
                // "-w",
                // "-p", "cg", "enabled:false",
                "-f", "J",
                "-d", outputDir

        };
        G.reset();

        Options.v().set_whole_program(true);
        Options.v().set_src_prec(Options.src_prec_apk);
        // Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
        // Options.v().set_output_format(Options.output_format_none);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_react_native(true);
        Options.v().set_hbc_nativehost_path("/your/dir/to/hbcTool-readyToUse");
  


        soot.Main.main(argsInput);

        System.out.println("Done!");
    }
}