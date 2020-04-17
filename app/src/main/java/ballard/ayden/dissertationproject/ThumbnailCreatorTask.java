package ballard.ayden.dissertationproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * This class is used to create the thumbnails for the ListView of downloaded files as an
 * async task.
 * @author Ayden Ballard
 */
public class ThumbnailCreatorTask extends AsyncTask<File, Void, Bitmap> {

    private File file; //file to be loaded
    private final WeakReference<ImageView> imageView; //Weak Reference to image view

    /**
     * Constructor
     * @param imageView - reference to image view
     * @param file - file to generate thumbnail of
     */
    public ThumbnailCreatorTask(ImageView imageView, File file){
        this.file = file;
        this.imageView = new WeakReference(imageView);
    }

    /**
     * Method to generate thumbnails in background
     * @param files
     * @return Bitmap - thumbnail
     */
    @Override
    protected Bitmap doInBackground(File... files) {
        return getThumbnail(files[0]);
    }

    /**
     * On post execute, set the ImageView image to bitmap of generate thumbnail
     * @param bitmap - bitmap to be allocated
     */
    @Override
    protected void onPostExecute(Bitmap bitmap){
      if(imageView != null){
        ImageView temp = imageView.get();
        if(temp != null && bitmap != null){
            temp.setImageBitmap(bitmap);
        }
      }
    }

    /**
     * Method to create thumbnail of file
     * @param file
     * @return Bitmap - generated thumbnail
     */
    private Bitmap getThumbnail(File file){
        Bitmap thumbnail = null;
        if(file.getName().contains(".mp4")){
            thumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            return thumbnail;
        } else {
            thumbnail = BitmapFactory.decodeFile(file.getAbsolutePath());
            return thumbnail;
         }
    }
}
