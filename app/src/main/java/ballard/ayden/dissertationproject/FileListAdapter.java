package ballard.ayden.dissertationproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.*;

/**
 * This class extends ArrayAdapater to provide a custom Adapter for the FilelistDisplay class. It
 * takes all previously downloaded files and allocates its name, size and a thumbnail to its row.
 * @author Ayden Ballard
 */

public class FileListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> fileNames; //ArrayList of file names
    private Context context;

    /**
     * Constructor for file list adapter object
     * @param context - Context
     * @param file_display
     * @param fileNames - ArrayList of file names
     */
    public FileListAdapter(Context context, int file_display, ArrayList<String> fileNames){
        super(context, file_display, fileNames);
        this.fileNames = fileNames;
        this.context = context;
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

        //set memory label
        final int BYTES_IN_MEGABYTE = 1000000;
        String fileSize = (fileToDisplay.length()+" Bytes");
        if(fileToDisplay.length()/BYTES_IN_MEGABYTE > 1){ //if file size > 1mb
            long megaBytes = (fileToDisplay.length()/BYTES_IN_MEGABYTE);
            fileSize = (megaBytes+" MB");
        }
        secondLine.setText(fileSize);

        //set thumbnail
        imageView.setImageResource(R.drawable.ic_play_dark); //default thumbnail
        File file = FileManager.findFile(fileNames.get(position));
        if(file != null){ //assign thumbnail
            if(fileNames.get(position).contains(".pdf")){ //if pdf file
                imageView.setImageResource(R.drawable.ic_pdf);
            } else { //else its an image
                Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(thumbnail);
            }
        }

        return rowView;
    }





}
