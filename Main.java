import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        try{
            String in = args[0];
            String out = args[1];
            double f = Double.parseDouble(args[2]);
            double alp = Double.parseDouble(args[3]);
            Mesh m = STLReader.readMesh(in);
            EdgePair.alpha = alp;
            Mesh newM = Simplifier.simplifyMesh(m, f);
            STLReader.writeMesh(newM, out);
        } catch(Exception e){e.printStackTrace();}
    }
}
