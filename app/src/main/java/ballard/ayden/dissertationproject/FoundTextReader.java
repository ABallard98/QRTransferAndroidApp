package ballard.ayden.dissertationproject;

import java.util.Scanner;

/**
 * This class is used to extract information from the code embedded in the generated QR Codes.
 * @author Ayden Ballard
 */
public class FoundTextReader {

    //QR Code Format = <IP Address>-<Port>-<File name>-<File size>

    /**
     * Method to return the port number embedded in the QR code.
     * @param foundText - embedded QR code
     * @return int - value of port
     */
    public static int readPort(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        String port = in.next();
        in.close();
        return Integer.valueOf(port);
    }

    /**
     * Method to return the IP address embedded in the QR code.
     * @param foundText - embedded QR code
     * @return String - IP address
     */
    public static String readIpAddress(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        String ipAddress = in.next();
        in.close();
        return ipAddress;
    }

    /**
     * Method to return the file name embedded in the QR code.
     * @param foundText - embedded QR code
     * @return String - file name
     */
    public static String readFileName(String foundText){
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        in.next();//skip port
        String filename = in.next();
        in.close();
        return filename;
    }

    /**
     * Method to return the file size in bytes
     * @param foundText - embedded QR code
     * @return int - bytes
     */
    public static int readFileSizeBytes(String foundText){
        System.out.println(foundText);
        Scanner in = new Scanner(foundText);
        in.useDelimiter("-");
        in.next();//skip ip address
        in.next();//skip port
        in.next();//skip filename

        int fileSizeBytes = in.nextInt();
        in.close();
        return Integer.valueOf(fileSizeBytes);
    }




}
