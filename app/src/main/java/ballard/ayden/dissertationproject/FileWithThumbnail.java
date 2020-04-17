package ballard.ayden.dissertationproject;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;

public class FileWithThumbnail {

    private File file;
    private Bitmap thumbnail;

    public FileWithThumbnail(File file){
        this.file = file;
        this.thumbnail = null;
    }

    public FileWithThumbnail(File file, Bitmap thumbnail){
        this.file = file;
        this.thumbnail = thumbnail;
    }

    public void FileWithThumbnail(Bitmap thumbnail){
        this.thumbnail = thumbnail;
    }

    public void setFile(File file){
        this.file = file;
    }

    public Bitmap getBitmap(){
        return this.thumbnail;
    }

    public File getFile(){
        return this.file;
    }




}
