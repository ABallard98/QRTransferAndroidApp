package ballard.ayden.dissertationproject;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileManager {

    public static final String EXTERNAL_FILES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + "/QRTransfer/";

    public static File copyFileToExternalStorage(Context context, File fileToMove){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + fileToMove.getName());
        File dstFile = new File(dstPath);
        try {
            FileChannel inChannel = new FileInputStream(fileToMove).getChannel();
            FileChannel outChannel = new FileOutputStream(dstFile).getChannel();

            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
            System.out.println("MOVED FILE (" + fileToMove.getName() + ") to external storage");
        } catch (Exception  e) {
            e.printStackTrace();
        }
        return dstFile;
    }

    public static File findFile(String filename){
        File downloadedFilesFolder = new File(MainActivity.DB_PATH);
        File[] listOfFiles = downloadedFilesFolder.listFiles();
        for(File f : listOfFiles){
            if(f.getName().equals(filename)){
                return f;
            }
        }
        return null;
    }

    public static File findFileExternalStorage(String filename){
        try{
            String dstPath = (EXTERNAL_FILES_PATH + "/QRTransfer/" + filename);
            File dstFile = new File(dstPath);
            return dstFile;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }





}
