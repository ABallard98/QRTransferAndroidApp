package ballard.ayden.dissertationproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class extends ArrayAdapater to provide a custom Adapter for the FilelistDisplay class. It
 * takes all previously downloaded files and allocates its name, size and a thumbnail to its row.
 * @author Ayden Ballard
 */

public class FileListAdapter extends ArrayAdapter<String> {

    public static ArrayList<FileWithThumbnail> fwtList; //acts as a cache for file/thumbnail combo
    private ArrayList<String> fileNames; //ArrayList of file names
    private ArrayList<File> files; //ArrayList of files
    private Context context; //Application context

    /**
     * Constructor for file list adapter object
     * @param context - Context
     * @param file_display
     * @param fileNames - ArrayList of file names
     */
    public FileListAdapter(Context context, int file_display, ArrayList<String> fileNames, ArrayList<File> files){
        super(context, file_display, fileNames);
        this.files = files;
        this.fileNames = fileNames;
        this.fwtList = new ArrayList<>();
        this.context = context;

        fileNames.sort(String::compareToIgnoreCase); //sort files alphabetically by default
    }

    /**
     * Method to sort the list of files in alphabetical order
     */
    public void sortAlphabetically(){
       fileNames.sort(String::compareToIgnoreCase); //sort files alphabetically
    }

    /**
     * Method to sort the list of files by file extension
     */
   public void sortFileType(){
        files.sort((o1, o2) -> {
           String f1 = FilenameUtils.getExtension(o1.getName());
           String f2 = FilenameUtils.getExtension(o2.getName());
           return String.valueOf(f1).compareTo(f2);
        });
        //replace filenames array list with sorted list of names
        this.fileNames.clear();
        for(File f: files){
            this.fileNames.add(f.getName());
        }
   }

    /**
     * Method to sort the list of files by date created
     */
    public void sortDateCreated(){
        fileNames.sort(String::compareToIgnoreCase); //alphabetical sort first
        files.sort((o1, o2) -> {
            long f1 = getFileDateCreated(o1);
            long f2 = getFileDateCreated(o2);
            return Long.valueOf(f1).compareTo(f2);
        });
        //replace filenames array list with sorted list of names
        this.fileNames.clear();
        for(File f : files){
            this.fileNames.add(f.getName());
        }
    }

    /**
     * Method to get the date created of a file from its meta-data
     * @param file - file to find its date of creation
     * @return Long - date created
     */
    private long getFileDateCreated(File file){
        try{
            BasicFileAttributes attr =
                    Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return attr.creationTime().toInstant().toEpochMilli();
        } catch(Exception e){
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
   }

    /**
     * Method to generate rows for the ListView
     * @param position - row position
     * @param convertView - View
     * @param parent - ViewGroup
     * @return rowView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.file_display, parent, false);

        File fileToDisplay = FileManager.findFile(fileNames.get(position));

        TextView firstLine = rowView.findViewById(R.id.firstLine);
        TextView secondLine = rowView.findViewById(R.id.secondLine);
        ImageView imageView = rowView.findViewById(R.id.icon);

        //set name label
        String fileName = fileToDisplay.getName();
        if(fileToDisplay.getName().length() >= 20){  //if name of file is larger than 20 chars
            fileName = fileName.substring(0,12) + "..."
                    + FileManager.getFileExtension(fileToDisplay);
        }

        firstLine.setText(fileName);
        //set file size label
        secondLine.setText(getFileSizeToString(fileToDisplay));

        //set thumbnail
        //imageView.setImageResource(R.drawable.ic_play_dark); //default thumbnail

        File file = FileManager.findFile(fileNames.get(position));
        if(file != null){ //assign thumbnail
            if(getFwt(file) != null){
                imageView.setImageBitmap(getFwt(file).getBitmap());
            } else {
                if (fileNames.get(position).contains(".pdf")) { //if pdf file
                    imageView.setImageResource(R.drawable.ic_pdf);
                    //add file to ftw ArrayList here
                    FileWithThumbnail fwt = new FileWithThumbnail(file,
                            ((BitmapDrawable) imageView.getDrawable()).getBitmap());
                    fwtList.add(fwt);
                } else if (fileNames.get(position).contains(".mp4")) { //if video file
                    new ThumbnailCreatorTask(imageView, file).execute(file);
                } else {
                    new ThumbnailCreatorTask(imageView, file).execute(file);
                }
            }
        }
        return rowView;
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

    private FileWithThumbnail getFwt(File file){
        for(FileWithThumbnail f : fwtList){
            if(f.getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                return f;
            }
        }
        return null;
    }

}
