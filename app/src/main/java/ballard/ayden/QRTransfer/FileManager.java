package ballard.ayden.QRTransfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This class is used to manage the files downloaded using the application.
 * @author Ayden Ballard
 */

public class FileManager {

    public static final String EXTERNAL_FILES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + "/QRTransfer/"; //path for external files

    /**
     * Method to copy a file to external storage
     * @param context - Context
     * @param fileToMove - File to be moved to external storage
     * @return File in external storage
     */
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

    /**
     * Method to find and return a file in internal storage
     * @param filename - name of file to be found
     * @return File in internal storage
     */
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

    /**
     * Method to find and return a file in external storage
     * @param filename - name of file to be found
     * @return Fine in external storage
     */
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

    /**
     * Method to find and delete files in external and internal storage
     * @param filename - filename to be deleted
     */
    public static void deleteFileInternalExternal(String filename){
        try{
            File internalFile = findFile(filename);
            if(internalFile != null){
                internalFile.delete();
            }
            File externalFile = findFileExternalStorage(filename);
            if(externalFile != null){
                externalFile.delete();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to return the file type of a given file as a string
     * @param file - file
     * @return String - type of file
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else{
            return "";
        }
    }

    /**
     * Method to convert byte size of file into a more readable format
     * @param file - file to get size of
     * @return String - file size in a readable format
     */
    public static String getFileSizeToString(File file){
        int fileSizeBytes = (int) file.length();
        if(fileSizeBytes > 1000000){ //if file size is larger than 1mb
            long fileSizeMb = Math.round(fileSizeBytes / Math.pow(1024,2));
            String toReturn = fileSizeMb + "mb";
            return toReturn;
        } else if(fileSizeBytes > 1000){ //if file size is larger than 1kb
            long fileSizeKb = fileSizeBytes / 1000;
            String toReturn = fileSizeKb + "kb";
            return toReturn;
        }
        else { //else return as bytes
            String toReturn = fileSizeBytes + " bytes";
            return toReturn;
        }
    }

    /**
     * Method to get the date created of a specified file
     * @param file - file to collect the date created
     * @return String - date and time of file created
     */
    public static String getFileDateCreated(File file){
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),BasicFileAttributes.class);
            return attr.creationTime()+"";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
