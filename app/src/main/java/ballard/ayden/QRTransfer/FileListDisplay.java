package ballard.ayden.QRTransfer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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


    private enum SortOrder { //enum for current sort order
        ALPHABETICAL,
        DATE,
        FILE_TYPE,
        FILE_SIZE
    }

    private ArrayList<String> fileNames; //ArrayList of the file names
    private ArrayList<String> fileSizes; //ArrayList of the file sizes (in bytes)
    private ArrayList<File> files; //ArrayList of files
    private FileListAdapter fileListAdapter; //Adapter for file view
    private ListView listView; //ListView to contain files downloaded
    private SortOrder sortOrder; //sort order enum
    private Menu sortMenu; //sort menu dropdown

    private FrameLayout selectedFileFrame;

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

        this.selectedFileFrame = findViewById(R.id.selectedFileFrame);

        //ArrayLists for file names and sizes
        fileNames = new ArrayList();
        fileSizes = new ArrayList();
        files = new ArrayList<>();
        initializeFileArrays();

        //Sort order alphabetical by default
        this.sortOrder = SortOrder.ALPHABETICAL;

        //action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing FileListAdapter
        this.fileListAdapter =
                new FileListAdapter(this, R.layout.file_display,fileNames, files);
        this.listView = findViewById(R.id.fileListView);
        listView.setAdapter(fileListAdapter);

        //todo here
        initializeFileListListeners(listView);
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
            Toast.makeText(this, "Error: This file does not exist.",
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
            Toast.makeText(this, "Error: This file does not exist.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void launchAPKFile(File apkFile){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + apkFile.getName());
        File dstFile = new File(dstPath);
        if(dstFile.exists()){
            try{
                Uri path = Uri.fromFile(dstFile);
                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                objIntent.setDataAndType(path,"application/vnd.android.package-archive");
                objIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(objIntent);
            } catch(Exception e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Error: This file does not exist.",
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
                    this.files.add(f);
                }
            }
        }
    }

    /**
     * Method to inflate action bar with sorting options for file selection
     * @param menu - menu to be inflated
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_sorting_options, menu);
        sortMenu = menu;
        MenuItem alphabeticalSort = sortMenu.findItem(R.id.menuSortAlphabetically);
        alphabeticalSort.setChecked(true); //check alphabetical sort by default
        return true;
    }

    /**
     * Activity to determine what item on the action bar is clicked
     * @param item - item clicked
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case(android.R.id.home): //if home (back) button is pressed
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                break;
            case(R.id.menuSortDate): //if sort by date selected
                Toast.makeText(this,"Sorted by date created", Toast.LENGTH_LONG).show();
                fileListAdapter.sortDateCreated(); //sort files by date created
                fileListAdapter.notifyDataSetChanged();
                sortOrder = SortOrder.DATE; //update sort order
                break;
            case(R.id.menuSortAlphabetically): //if sort alphabetically selected
                Toast.makeText(this,"Sorted alphabetically", Toast.LENGTH_LONG).show();
                fileListAdapter.sortAlphabetically(); //sort files by alphabetical order
                fileListAdapter.notifyDataSetChanged();
                sortOrder = SortOrder.ALPHABETICAL; //update sort order
                break;
            case(R.id.menuSortFileType): //if sort by file type selected
                Toast.makeText(this,"Sorted by file type", Toast.LENGTH_LONG).show();
                fileListAdapter.sortFileType();
                fileListAdapter.notifyDataSetChanged();
                sortOrder = SortOrder.FILE_TYPE; //update sort order
                break;
            case(R.id.menuSortFileSize):
                Toast.makeText(this,"Sorted by file size", Toast.LENGTH_LONG).show();
                fileListAdapter.sortFileSize();
                fileListAdapter.notifyDataSetChanged();
                sortOrder = SortOrder.FILE_SIZE;
                break;
//                MenuItem fileSizeSortItem = sortMenu.findItem(R.id.menuSortFileSize);
//                fileSizeSortItem.setChecked(true);
            default:
                return super.onOptionsItemSelected(item);
        }
        updateSortOrderCheckBox(); //update checkboxes of sort order dropdown
        return true;
    }

    /**
     * Method to update the sort order checkboxes
     */
    private void updateSortOrderCheckBox(){
        MenuItem alphabeticalSortItem = sortMenu.findItem(R.id.menuSortAlphabetically);
        MenuItem dateSortItem = sortMenu.findItem(R.id.menuSortDate);
        MenuItem fileTypeSortItem = sortMenu.findItem(R.id.menuSortFileType);
        MenuItem fileSizeSortItem = sortMenu.findItem(R.id.menuSortFileSize);
        if(sortOrder == SortOrder.ALPHABETICAL){
            alphabeticalSortItem.setChecked(true);
            dateSortItem.setChecked(false);
            fileTypeSortItem.setChecked(false);
            fileSizeSortItem.setChecked(false);
        } else if (sortOrder == SortOrder.DATE){
            alphabeticalSortItem.setChecked(false);
            dateSortItem.setChecked(true);
            fileTypeSortItem.setChecked(false);
            fileSizeSortItem.setChecked(false);
        } else if (sortOrder == SortOrder.FILE_TYPE){
            alphabeticalSortItem.setChecked(false);
            dateSortItem.setChecked(false);
            fileTypeSortItem.setChecked(true);
            fileSizeSortItem.setChecked(false);
        }  else if(sortOrder == SortOrder.FILE_SIZE){
            alphabeticalSortItem.setChecked(false);
            dateSortItem.setChecked(false);
            fileTypeSortItem.setChecked(false);
            fileSizeSortItem.setChecked(true);
        }
    }

    private void loadSelectedFileFrame(File file, int pos){

        ImageView selectedFileImage = findViewById(R.id.selectedFileImage);
        ImageView selectedFileBackButton = findViewById(R.id.selectedFileBackButton);
        ImageView selectedFileDeleteIcon = findViewById(R.id.selectedFileDeleteIcon);

        TextView selectedFileName = findViewById(R.id.selectedFileName);
        TextView selectedFileSize = findViewById(R.id.selectedFileSize);
        TextView selectedFileDateCreated = findViewById(R.id.selectedFileDateCreated);
        TextView selectedFileDelete = findViewById(R.id.selectedFileDelete);

        this.runOnUiThread(new Thread(() -> {
            //set text fields
            selectedFileName.setText("Name: " + file.getName());
            selectedFileSize.setText("Size: " + FileManager.getFileSizeToString(file));
            selectedFileDateCreated.setText("Date: " + FileManager.getFileDateCreated(file).split("T")[0]);
            selectedFileDelete.setText("Delete File");

            //set thumbnail of file
            new ThumbnailCreatorTask(selectedFileImage, file).execute(file);
            //make fileFrame visible
            selectedFileFrame.setVisibility(View.VISIBLE);
            //disable listView listeners while fileFrame is open
            listView.setOnItemClickListener(null);
            listView.setOnItemLongClickListener(null);
        }));

        //onClickListener for delete button
        selectedFileDelete.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch(which){
                    case(DialogInterface.BUTTON_POSITIVE): //if user selects yes

                        //Delete file from internal and external storage
                        FileManager.deleteFileInternalExternal(fileNames.get(pos));
                        //remove from names and size array lists
                        fileNames.remove(pos);
                        fileSizes.remove(pos);

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
            };

            //Building dialog popup for file deletion confirmation
            AlertDialog.Builder builder1 =
                    new AlertDialog.Builder(FileListDisplay.this);
            builder1.setTitle(R.string.delete_dialog_title);
            builder1.setPositiveButton("Yes", dialogClickListener);
            builder1.setNegativeButton("No", dialogClickListener);
            builder1.show(); //show popup dialog confirmation

            //return user to list view
            runOnUiThread(new Thread(() -> {
                initializeFileListListeners(listView);
                selectedFileFrame.setVisibility(View.INVISIBLE);

            }));
        });

        //back button to take the user back to the ListView
        selectedFileBackButton.setOnClickListener(view -> {
            //return user to list view
            runOnUiThread(new Thread(() -> {
                initializeFileListListeners(listView);
                selectedFileFrame.setVisibility(View.INVISIBLE);
            }));
        });


    }//end of load selected file frame

    /**
     * Method to initialize the onItemClick and onItemLongClick listeners to the list view
     * @param listView - list view to set listeners on
     */
    private void initializeFileListListeners(ListView listView){
        //on click listener to open downloaded files
        listView.setOnItemClickListener((parent, view, position, id) -> {
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
                else if (filename.contains(".mp4")){ //video file types
                    launchVideoFile(fileToOpen);
                }
                else if (filename.contains(".apk")){
                    launchAPKFile(fileToOpen);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        //on long click listener to view more details of the file
        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
            try{
                final int filePos = pos; //must be final for internal method
                String filename = fileNames.get(pos);
                File file = FileManager.findFile(filename);
                loadSelectedFileFrame(file, pos);

            } catch(Exception e){
                e.printStackTrace();
            }
            return true;
        });
    }
}
