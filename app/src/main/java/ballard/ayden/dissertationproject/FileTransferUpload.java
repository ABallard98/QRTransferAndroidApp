package ballard.ayden.dissertationproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

public class FileTransferUpload implements Runnable {

    private File fileToTransfer; //File to transfer
    private final String IP_ADDRESS = "80.2.250.205"; //IP Address of Server
    private final int PORT = 8007; //Port to connect to

    /**
     * Constructor for FileTransferDownload
     * @param fileToTransfer - File to transfer
     */
    public FileTransferUpload(File fileToTransfer){
        super();
        this.fileToTransfer = fileToTransfer;
    }


    @Override
    public void run() {
        handleRequest();
    }

    /**
     * Method to handle the request of uploading a file to the server for transfer
     */
    private synchronized void handleRequest(){
        try{
            byte[] byteArray = new byte[(int) fileToTransfer.length()];

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            Socket socket = new Socket(IP_ADDRESS,PORT); //connect to server

            //sending instructions to the server
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            //generate unique file name
            String uniqueFileName = System.currentTimeMillis()+fileToTransfer.getName();
            uniqueFileName.replace("-","");

            //instruction to the server
            String instructionToSend = "SENDING-"+uniqueFileName +"-"+fileToTransfer.length();

            //send instruction to the server
            dos.writeUTF(instructionToSend);

            //send file to server
            InputStream in = new FileInputStream(fileToTransfer);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            long fileLength = fileToTransfer.length();
            if(fileLength > Integer.MAX_VALUE){
                System.out.println("File too large...");
            } else {
                int count;
                while ((count = in.read(byteArray)) > 0) {
                    out.write(byteArray, 0, count);
                }
                in.close();
                out.flush();
                out.close();
                System.out.println("Sent.");
            }

            //close socket
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
