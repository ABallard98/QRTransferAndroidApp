package ballard.ayden.dissertationproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FileListDisplay extends AppCompatActivity {


    private File downloadedFilesFolder;
    private File[] listOfFiles;
    private ArrayList<String> fileNames;
    private ArrayList<String> fileSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_view);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        fileNames = new ArrayList();
        fileSizes = new ArrayList();

        initializeFileArrays();

        final FileListAdapter fileListAdapter = new FileListAdapter(this,
                R.layout.file_display,fileNames);

        final ListView listView = findViewById(R.id.fileListView);

        listView.setAdapter(fileListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try{

                    String filename = fileNames.get(position);
                    File fileToOpen = FileManager.findFile(filename);
                    if(filename.contains(".pdf")){ //LAUNCH PDF
                       launchPdfFile(fileToOpen);
                    } else if (filename.contains(".png") || filename.contains(".jpg") ||
                            filename.contains(".jpeg")) {

                        //launchFile(fileToOpen);
                        //todo replace this intent with intent to launch in external app
                        Intent intent = new Intent(getBaseContext(), ImageViewActivity.class);
                        intent.putExtra("fileName", fileNames.get(position));
                        startActivity(intent);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                //delete file
                try{
                    File toDeleteInternal = FileManager.findFile(fileNames.get(pos));
                    File toDeleteExternal = FileManager.findFileExternalStorage(fileNames.get(pos));
                    toDeleteInternal.delete();
                    toDeleteExternal.delete();
                } catch(Exception e){
                    e.printStackTrace();
                }

                //remove from names and size array lists
                fileNames.remove(pos);
                fileSizes.remove(pos);

                //re-initialize the array of downloaded files
                initializeFileArrays();
                fileListAdapter.notifyDataSetChanged();

                //alert user file was deleted
                Toast.makeText(getApplicationContext(),
                        "File Deleted ", Toast.LENGTH_LONG)
                        .show();
                return true;
            }
        });

    }

    private void launchFile(File fileToLaunch){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + fileToLaunch.getPath()), "image/*");
        startActivity(intent);
    }

    private void launchPdfFile(File pdfFile){
        String dstPath = (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + pdfFile.getName());
        File dstFile = new File(dstPath);

        System.out.println("SEARCHING FOR FILE: " + dstFile.getPath());

        if (dstFile.exists()){
            Uri path = Uri.fromFile(dstFile);
            Intent objIntent = new Intent(Intent.ACTION_VIEW);
            objIntent.setDataAndType(path, "application/pdf");
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(objIntent);//Starting the pdf viewer
        } else {
            Toast.makeText(this, "The file not exists! ", Toast.LENGTH_SHORT).show();
        }
    }

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





}
