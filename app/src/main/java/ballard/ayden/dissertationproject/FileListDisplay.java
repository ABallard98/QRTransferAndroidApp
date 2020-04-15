package ballard.ayden.dissertationproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

/**
 * This class acts as the controller for the FileListDisplay activity. This class provides a list
 * view of all files transferred using the application. FileListAdapater is used to implement the
 * list view. Files can be deleted from the system by doing a long click on the file to be deleted.
 * Files can also be opened and are launched using an internal intent.
 * @author Ayden Ballard
 */

public class FileListDisplay extends AppCompatActivity {

//    private File downloadedFilesFolder; //Parent file of all downloaded files
//    private File[] listOfFiles; //list of the downloaded files
    private ArrayList<String> fileNames; //ArrayList of the file names
    private ArrayList<String> fileSizes; //ArrayList of the file sizes (in bytes)

    /**
     * Method to initializer FileListDisplay
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_view);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //ArrayLists for file names and sizes
        fileNames = new ArrayList();
        fileSizes = new ArrayList();
        initializeFileArrays();

        //action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing FileListAdapter
        final FileListAdapter fileListAdapter = new FileListAdapter(this,
                R.layout.file_display,fileNames);
        final ListView listView = findViewById(R.id.fileListView);
        listView.setAdapter(fileListAdapter);

        //on click listener to open downloaded files
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try{
                    String filename = fileNames.get(position);
                    File fileToOpen = FileManager.findFile(filename);
                    if(filename.contains(".pdf")){ //If the file is a PDF
                       launchPdfFile(fileToOpen);
                    }
                    //image MIME types
                    else if (filename.contains(".png") || filename.contains(".jpg") ||
                            filename.contains(".jpeg")) { //If the file is an image
                        launchImageFile(fileToOpen);
                    }
                    else if (filename.contains(".mp4")){
                        launchVideoFile(fileToOpen);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //on long click listener to delete a downloaded file
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                try{
                    final int filePos = pos; //must be final for internal method
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case(DialogInterface.BUTTON_POSITIVE):

                                    //Delete file from internal and external storage
                                    FileManager.deleteFileInternalExternal(fileNames.get(filePos));
                                    //remove from names and size array lists
                                    fileNames.remove(filePos);
                                    fileSizes.remove(filePos);

                                    //re-initialize the array of downloaded files
                                    fileListAdapter.notifyDataSetChanged();

                                    //alert user file was deleted
                                    Toast.makeText(getApplicationContext(),
                                            "File Deleted ", Toast.LENGTH_LONG)
                                            .show();
                                    break;
                                case(DialogInterface.BUTTON_NEGATIVE):
                                    break;
                            }
                        }
                    };
                    //Building dialog popup for file deletion confirmation
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileListDisplay.this);
                    builder.setTitle(R.string.delete_dialog_title);
                    builder.setPositiveButton("Yes", dialogClickListener);
                    builder.setNegativeButton("No", dialogClickListener);
                    builder.show();
                } catch(Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    /**
     * Method to launch a pdf file
     * @param pdfFile - pdfFile to be opened
     */
    private void launchPdfFile(File pdfFile){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + pdfFile.getName());
        File dstFile = new File(dstPath);
        if (dstFile.exists()){ //if file exists
            Uri path = Uri.fromFile(dstFile); //path of file
            Intent objIntent = new Intent(Intent.ACTION_VIEW); //intent to launch pdf file
            objIntent.setDataAndType(path, "application/pdf");
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(objIntent);//Starting the pdf viewer
        } else {
            Toast.makeText(this, "Error: This file does not exists.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to launch a video MIME type file
     * @param videoFile - video file to be opened
     */
    private void launchVideoFile(File videoFile){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + videoFile.getName());
        File dstFile = new File(dstPath);
        if (dstFile.exists()){
            Uri path = Uri.fromFile(dstFile);
            Intent objIntent = new Intent(Intent.ACTION_VIEW);
            objIntent.setDataAndType(path, "video/*");
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(objIntent);//Starting the video viewer
        } else {
            Toast.makeText(this, "Error: This file does not exists! ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to launch an image MIME type file
     * @param imageFile - image file to be launched
     */
    private void launchImageFile(File imageFile){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + imageFile.getName());
        File dstFile = new File(dstPath);
        if (dstFile.exists()){
            try{
                Uri path = Uri.fromFile(dstFile);
                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                objIntent.setDataAndType(path, "image/*");
                objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(objIntent);//Starting the video viewer
            } catch(Exception e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Error: This file does not exists! ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to initialize file arrays which contain the file names and sizes
     */
    private void initializeFileArrays(){
        File downloadedFilesFolder = new File(MainActivity.DB_PATH);
        File[] files = downloadedFilesFolder.listFiles();
        System.out.println("*** " + files.length + " ***");
        if(files != null){
            System.out.println("*** " + files.length + " ***");
            for(File f : files){
                if(f != null){
                    System.out.println("***\n FILE FOUND - " + f.getAbsolutePath()+"\nSIZE - " +
                            f.length() + " BYTES \n***");
                    fileNames.add(f.getName());

                    fileSizes.add(f.length()+"");
                }
            }
        }
    }

    /**
     * Activity to take user back to homepage
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
