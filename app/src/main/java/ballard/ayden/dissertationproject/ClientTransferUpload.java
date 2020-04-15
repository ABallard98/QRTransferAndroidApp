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

public class ClientTransferUpload implements Runnable {

    private File fileToTransfer;
    private final String IP_ADDRESS = "109.148.220.112";
    private final int PORT = 8007;

    public ClientTransferUpload(File fileToTransfer){
        super();
        this.fileToTransfer = fileToTransfer;
    }

    @Override
    public void run() {
        handleRequest();
    }

    private synchronized void handleRequest(){
        try{
            byte[] byteArray = new byte[(int) fileToTransfer.length()];

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            Socket socket = new Socket(IP_ADDRESS,PORT);
            System.out.println("Attempting to send file to server...");

            //sending instructions to the server
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            String instructionToSend = "SENDING-"+fileToTransfer.getName().replace("-","")
                    +"-"+fileToTransfer.length();


            System.out.println(instructionToSend);
            dos.writeUTF(instructionToSend);

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

//            FileInputStream fis = new FileInputStream(fileToTransfer);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            bis.read(byteArray, 0, byteArray.length);
//
//            OutputStream os = socket.getOutputStream();
//
//            System.out.println("Sending...");
//            os.write(byteArray, 0, byteArray.length);
//            os.flush();


            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
