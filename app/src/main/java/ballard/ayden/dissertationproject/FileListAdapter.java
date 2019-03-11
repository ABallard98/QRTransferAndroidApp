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

public class FileListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> fileNames;
    private ArrayList<String> fileSizes;
    private Context context;

    public FileListAdapter(Context context, int file_display, ArrayList<String> fileNames){
        super(context, file_display, fileNames);
        this.fileNames = fileNames;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.file_display, parent, false);

        File fileToDisplay = findFile(fileNames.get(position));

        String fileSize = (Long.toString(fileToDisplay.length())+" Bytes");

        TextView firstLine = rowView.findViewById(R.id.firstLine);
        TextView secondLine = rowView.findViewById(R.id.secondLine);
        ImageView imageView = rowView.findViewById(R.id.icon);

        firstLine.setText(fileToDisplay.getName());
        secondLine.setText(fileSize);

        imageView.setImageResource(R.drawable.ic_play_dark);

        File file = findFile(fileNames.get(position));

        if(file != null){ //assign thumbnail
            if(fileNames.get(position).contains(".pdf")){
                System.out.println("***ASSIGNING PDF ICON");
                imageView.setImageResource(R.drawable.ic_pdf);
            } else {
                Bitmap thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(thumbnail);
            }
        }

        return rowView;
    }

    private File findFile(String filename){
        File downloadedFilesFolder = new File(MainActivity.DB_PATH);
        File[] listOfFiles = downloadedFilesFolder.listFiles();
        for(File f : listOfFiles){
            if(f.getName().equals(filename)){
                return f;
            }
        }
        return null;
    }

}
