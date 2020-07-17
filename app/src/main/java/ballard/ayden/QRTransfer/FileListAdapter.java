package ballard.ayden.QRTransfer;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class extends ArrayAdapater to provide a custom Adapter for the FilelistDisplay class. It
 * takes all previously downloaded files and allocates its name, size and a thumbnail to its row.
 * @author Ayden Ballard
 */

public class FileListAdapter extends ArrayAdapter<String> implements Filterable {

    public static ArrayList<FileWithThumbnail> fwtList; //acts as a cache for file/thumbnail combo
    private ArrayList<String> fileNames; //ArrayList of file names
    private ArrayList<File> files; //ArrayList of files
    private ArrayList<String> filteredFiles; //filtered files based on name of file
    private Context context; //Application context

    private LayoutInflater mInflater; //inflater
    private ItemFilter mFilter = new ItemFilter(); //custom filter

    /**
     * Constructor for file list adapter object
     * @param context - Context
     * @param file_display - not sure what this is
     * @param fileNames - ArrayList of file names
     */
    public FileListAdapter(Context context, int file_display, ArrayList<String> fileNames, ArrayList<File> files) {
        super(context, file_display, fileNames);
        this.files = files;
        this.fileNames = fileNames;
        this.filteredFiles = fileNames;
        this.fwtList = new ArrayList<>();
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

        fileNames.sort(String::compareToIgnoreCase); //sort files alphabetically by default
    }

    /**
     * Method to return custom filter
     * @return - mFilter
     */
    public Filter getFilter(){
        return mFilter;
    }

    /**
     * Method to return the number of files in FilteredFiles
     * @return int - size
     */
    public int getCount(){
        return filteredFiles.size();
    }

    /**
     * This class is used to create a custom filter for the FileListAdapter,
     * the filter is based on the file name typed in the edit text in the FileListDisplay
     */
    private class ItemFilter extends Filter {

        /**
         * Method to perform the filtering of files
         * @param constraint - String
         * @return FilterResults - results
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final ArrayList<String> nlist = new ArrayList<>();

            //perform filtering
            for(File f : files){
                if(f.getName().toLowerCase().contains(filterString)){
                    nlist.add(f.getName());
                }
            }

            //result properties
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        /**
         * Method to publish the results of the filtering
         * @param constraint - Constraint entered by user
         * @param results - results published by performFiltering
         */
        @SuppressWarnings("Unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            filteredFiles = (ArrayList<String>) results.values;
            notifyDataSetChanged();

        }
    }

    /**
     * Method to sort the list of files in alphabetical order
     */
    public void sortAlphabetically() {
        fileNames.sort(String::compareToIgnoreCase); //sort files alphabetically
    }

    /**
     * Method to sort the list of files by file extension
     */
    public void sortFileType() {
        files.sort((o1, o2) -> {
            String f1 = FilenameUtils.getExtension(o1.getName());
            String f2 = FilenameUtils.getExtension(o2.getName());
            return f1.compareTo(f2);
        });
        //replace filenames array list with sorted list of names
        this.fileNames.clear();
        for (File f : files) {
            this.fileNames.add(f.getName());
        }
    }

    /**
     * Method to sort the list of files by date created
     */
    public void sortDateCreated() {
        fileNames.sort(String::compareToIgnoreCase); //alphabetical sort first
        files.sort((o1, o2) -> {
            long f1 = getFileDateCreated(o1);
            long f2 = getFileDateCreated(o2);
            return Long.valueOf(f1).compareTo(f2);
        });
        //replace filenames array list with sorted list of names
        this.fileNames.clear();
        for (File f : files) {
            this.fileNames.add(f.getName());
        }
    }

    /**
     * Method to sort the list of files by file size
     */
    public void sortFileSize() {
        files.sort((o1, o2) ->  {
            long f1 = o1.length();
            long f2 = o2.length();
            return Long.valueOf(f1).compareTo(f2);
        });
        Collections.reverse(files);
        this.fileNames.clear();
        for (File f : files) {
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

        fileNames = filteredFiles;

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.file_display, parent, false);

        try {
            File fileToDisplay = FileManager.findFile(fileNames.get(position));

            //setting the properties of the view of file
            TextView firstLine = rowView.findViewById(R.id.firstLine);
            TextView secondLine = rowView.findViewById(R.id.secondLine);
            ImageView imageView = rowView.findViewById(R.id.icon);

            //set name label
            String fileName = fileToDisplay.getName();
            //if name of file is larger than 20 chars -
            //make the display name smaller to fit on screen
            if (fileToDisplay.getName().length() >= 20) {
                fileName = fileName.substring(0, 12) + "..."
                        + FileManager.getFileExtension(fileToDisplay);
            }
            firstLine.setText(fileName);

            //set file size label
            secondLine.setText(getFileSizeToString(fileToDisplay));

            File file = FileManager.findFile(fileNames.get(position));
            //assign thumbnail of file
            if (file != null) {
                if (getFwt(file) != null) {
                    imageView.setImageBitmap(getFwt(file).getBitmap());
                } else {
                    if (fileNames.get(position).contains(".pdf")) { //if pdf file
                        imageView.setImageResource(R.drawable.ic_pdf);
                        //add file to ftw ArrayList here
                        FileWithThumbnail fwt = new FileWithThumbnail(file,
                                ((BitmapDrawable) imageView.getDrawable()).getBitmap());
                        fwtList.add(fwt);
                    } else if (fileNames.get(position).contains(".mp4")) { //if video file
                        new ThumbnailCreatorTask(imageView, file, context).execute(file);
                    } else if (fileNames.get(position).contains(".apk")) { //if apk file
                        PackageManager pm = context.getPackageManager();
                        PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
                        imageView.setImageDrawable(pi.applicationInfo.loadIcon(pm));
                    } else {
                        new ThumbnailCreatorTask(imageView, file, context).execute(file);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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

    /**
     * Method to get FileWithThumbnail object from the FileWithThumbnail ArrayList
     * @param file - file to be found
     * @return FileWithThumbnail - f
     */
    private FileWithThumbnail getFwt(File file){
        for(FileWithThumbnail f : fwtList){
            if(f.getFile().getAbsolutePath().equals(file.getAbsolutePath())){
                return f;
            }
        }
        return null;
    }

}
