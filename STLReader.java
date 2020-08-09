import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.*;

class STLReader
{
    public static Mesh readMesh(String path) throws IOException
    {
        byte[] allBytes = Files.readAllBytes(Paths.get(path));
        // read file to array of triangles
        ArrayList<Triangle> mesh;
        mesh = readBinary(allBytes);
        return new Mesh(mesh);
    }

    public static ArrayList<Triangle> readBinary(byte[] allBytes)
    {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(allBytes));
        ArrayList<Triangle> triangles = new ArrayList<>();
        HashMap<Vector, Vertex> map = new HashMap<Vector, Vertex>();
        try{
            // skip the header
            byte[] header = new byte[80];
            in.read(header);
            // get number triangles (not really needed)
            // WARNING: STL FILES ARE SMALL-ENDIAN
            int numberTriangles = Integer.reverseBytes(in.readInt());
            triangles.ensureCapacity(numberTriangles);
            // read triangles
            try{
                while(in.available() > 0 ){
                    float[] nvec = new float[3];
                    for(int i = 0; i < nvec.length; i++){
                        nvec[i] = Float.intBitsToFloat(Integer.reverseBytes(in.readInt()));
                    }
                    Vector normal = new Vector(nvec[0],nvec[1],nvec[2]); // not used (yet)
                    Vertex[] vertices = new Vertex[3];
                    for (int v = 0; v < vertices.length; v++) {
                        float[] vals = new float[3];
                        for (int d = 0; d < vals.length; d++) {
                            vals[d] = Float.intBitsToFloat(Integer.reverseBytes(in.readInt()));
                        }
                        Vector tmp = new Vector(vals[0], vals[1], vals[2]);
                        if(!map.containsKey(tmp))
                            map.put(tmp, new Vertex(tmp));
                        vertices[v] = map.get(tmp);;

                    }
                    short attribute = Short.reverseBytes(in.readShort()); // not used (yet)
                    Triangle t = new Triangle(vertices[0], vertices[1], vertices[2]);
                    triangles.add(t);
                }
            }catch(Exception ex){}
        }catch(IOException ex){}
        return triangles;
    }

    public static void writeMesh(Mesh mesh, String path)
    {
        try{
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            byte [] header,byte4;
            ByteBuffer buf;

            header=new byte[80];

            buf = ByteBuffer.allocate(200);
            
            
            header=new byte[80];
            buf.get(header,0,80);
            boolean isColored = true;
            String tmp = "STLB ASM 218.00.00.0000 COLOR=....";
            for(int i=0;  isColored && i<80; i++){
                if(i < tmp.length()){
                    header[i] = (byte)tmp.charAt(i);
                    if(header[i]==0x2e)
                        header[i] = 0x19;
                }
                else if(i == tmp.length() )
                    header[i] = -1;
                else
                    header[i] = 0x20;
                
            }
            out.write(header);
            buf.rewind();

            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(mesh.triangles.size());
            buf.rewind();
            buf.get(header,0,4);
            out.write(header,0,4);
            buf.rewind();

            buf.clear();
            header=new byte[50];         //blue...
            int[] colors = new int[]{    (1024*24+32*0+0), (1024*31+32*0+0), (1024*31+32*15+0), (1024*31+32*31+0), (1024*15+32*31+0),
                                        (1024*0+32*31+0), (1024*0+32*31+15), (1024*0+32*31+31), (1024*0+32*15+31), (1024*0+32*0+31)}; //...red
            for(Triangle t : mesh.triangles) {

                buf.rewind();

                buf.putFloat((float)t.normal.x);
                buf.putFloat((float)t.normal.y);
                buf.putFloat((float)t.normal.z);
                
                
                buf.putFloat((float)t.vertices[0].v.x);
                buf.putFloat((float)t.vertices[0].v.y);
                buf.putFloat((float)t.vertices[0].v.z);

                buf.putFloat((float)t.vertices[1].v.x);
                buf.putFloat((float)t.vertices[1].v.y);
                buf.putFloat((float)t.vertices[1].v.z);

                buf.putFloat((float)t.vertices[2].v.x);
                buf.putFloat((float)t.vertices[2].v.y);
                buf.putFloat((float)t.vertices[2].v.z);
                buf.putShort( (short)colors[Math.min( Math.max((int)(10*(1- Math.pow(2, -t.curv()*EdgePair.alpha))), 0 ), 9)] );
                
                buf.rewind();
                buf.get(header);
                out.write(header);
            }
        } catch(Exception e){e.printStackTrace();}
    }

}