package soot.hermeser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;

public class HermesScene {
    private HbcAssemblyFile hermesAssembly;
    private static final Logger logger = LoggerFactory.getLogger(Scene.class);
    public static File indexAndroidBundleFile;

    public HermesScene() {
        
    }


    public void setHermesAssemblyFile(HbcAssemblyFile assemblyFile) {
      hermesAssembly = assemblyFile;
    }

    public HbcAssemblyFile getHermesAssemblyFile() {
      return hermesAssembly;
    }

    public static File extractedIndexJSFileFromAPK(String apkFile) {
      ZipFile archive = null;
      try {
        InputStream indexAndroidBundleIS = null;
        try {
          archive = new ZipFile(apkFile);
          for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".bundle")) {
              indexAndroidBundleIS = archive.getInputStream(entry);              
              indexAndroidBundleFile  = constructAssemblyFile(indexAndroidBundleIS);
              break;
            }
          }
        } catch (Exception e) {
          throw new RuntimeException("Error when looking for manifest in apk: " + e);
        }
      } finally {
        if (archive != null) {
          try {
            archive.close();
          } catch (IOException e) {
            throw new RuntimeException("Error when looking for manifest in apk: " + e);
          }
        }
      }
      return indexAndroidBundleFile;
    }

    public static File constructAssemblyFile(InputStream indexAndroidBundleIS) {
      File indexAndroidBundleFile = null;
      try {
        indexAndroidBundleFile = File.createTempFile("temp", null);
        indexAndroidBundleFile.deleteOnExit();

        try (
        OutputStream outputStream = new FileOutputStream(indexAndroidBundleFile)) {
          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = indexAndroidBundleIS.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

      return indexAndroidBundleFile;
      
    }
    
}