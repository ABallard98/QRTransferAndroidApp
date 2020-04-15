package ballard.ayden.dissertationproject;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is used to implement the file download process. The FileTransferDownload class takes in an
 * IP address, a port number, the file name and the files size in bytes. When the run() method is
 * called, the application connects to the server using a socket. The files information is then
 * sent and reconstructed on this device.
 * @author Ayden Ballard
 */
public class FileTransferDownload {

    private String address; //IP Address of server
    private int port; //Port to connect to
    private String filename; //name of file to transfer
    private int byteSize; //file size of selected file

    /**
     * Constructor for FileTransferDownload object
     * @param address - address of server
     * @param port - port to connect to
     * @param filename - name of file
     * @param byteSize - size of file
     */
    public FileTransferDownload(String address, int port, String filename, int byteSize) {
        this.address = address;
        this.port = port;
        this.filename = filename;
        this.byteSize = byteSize;
    }

    /**
     * Method to download a file from the server onto the user's device
     * @param context - Context
     */
    public void run(Context context) {
        try {
            System.out.println("connecting...");
            Socket sock = new Socket("109.148.220.112", port);
            System.out.println("connected");

            //sending instructions to the server
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

            System.out.println("DOWNLOAD-"+filename+"-"+byteSize);
            dos.writeUTF("DOWNLOAD-"+filename+"-"+byteSize);

            DataInputStream dis = new DataInputStream(sock.getInputStream());

            filename = filename.replace(" ",""); //remove whitespaces in filename
            filename = filename.toLowerCase();
            FileOutputStream fos = context.openFileOutput(filename,Context.MODE_PRIVATE);

            //copy file to internal storage of application
            int fileSize = dis.readInt();
            byte[] buffer = new byte[4096];
            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                fos.write(buffer, 0, read);
            }
            System.out.println("read " + totalRead + " bytes.");

            //close input and output streams
            fos.close();
            dis.close();

            //move file to external storage for other apps
            FileManager.copyFileToExternalStorage(context, FileManager.findFile(filename));

        } catch (IOException e){
            e.printStackTrace();
        }
    }

}