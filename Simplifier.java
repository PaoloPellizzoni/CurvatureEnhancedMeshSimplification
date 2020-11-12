import java.util.*;

public class Simplifier
{
    public static Mesh simplifyMesh(Mesh m, double factor)
    {
        System.out.println("mesh loaded");
        HashSet<Triangle> newMesh = new HashSet<Triangle>();
        HashMap<Edge, EdgePair> edgeMap = new HashMap<Edge, EdgePair>();
        IndexedPriorityQueue<EdgePair> edgeQueue = new IndexedPriorityQueue<EdgePair>();
        
        for(Triangle t : m.triangles)
        {
            t.vertices[0].quadric = t.vertices[0].quadric.plus(t.errorQuadric());
            t.vertices[1].quadric = t.vertices[1].quadric.plus(t.errorQuadric());
            t.vertices[2].quadric = t.vertices[2].quadric.plus(t.errorQuadric());
            t.vertices[0].angle += t.angle[0];
            t.vertices[1].angle += t.angle[1];
            t.vertices[2].angle += t.angle[2];
            t.vertices[0].area += t.area;
            t.vertices[1].area += t.area;
            t.vertices[2].area += t.area;
        }

        for(Triangle t : m.triangles)
        {
            newMesh.add(t); //adds temporarily t to the new mesh

            Edge e0 = new Edge(t.vertices[0], t.vertices[1]);
            Edge e1 = new Edge(t.vertices[1], t.vertices[2]);
            Edge e2 = new Edge(t.vertices[2], t.vertices[0]);

            if(!edgeMap.containsKey(e0)){
                EdgePair ep0 = new EdgePair(
                            e0,
                            t.vertices[0].quadric.plus(t.vertices[1].quadric));
                edgeMap.put(e0, ep0);
                edgeQueue.add(ep0);
            }
            if(!edgeMap.containsKey(e1)){
                EdgePair ep1 = new EdgePair(
                            e1,
                            t.vertices[1].quadric.plus(t.vertices[2].quadric));
                edgeMap.put(e1, ep1);
                edgeQueue.add(ep1);
            }
            if(!edgeMap.containsKey(e2)){
                EdgePair ep2 = new EdgePair(
                            e2,
                            t.vertices[0].quadric.plus(t.vertices[2].quadric));
                edgeMap.put(e2, ep2);
                edgeQueue.add(ep2);
            }
            
            t.vertices[0].triangles.add(t);
            t.vertices[1].triangles.add(t);
            t.vertices[2].triangles.add(t);

            t.vertices[0].edges.add(e0);
            t.vertices[0].edges.add(e2);
            t.vertices[1].edges.add(e0);
            t.vertices[1].edges.add(e1);
            t.vertices[2].edges.add(e1);
            t.vertices[2].edges.add(e2);
        }
        
        System.out.println("start to simplify");
        // start to decimate here
        int best = newMesh.size();
        while(newMesh.size() > factor * m.triangles.size())
        {
            if(newMesh.size()%10000 == 0 && best!=newMesh.size())
                System.out.println("Reduced to: "+(best = newMesh.size()));

            boolean valid = true;

            EdgePair p =  edgeQueue.poll();
            if(p.isRemoved)
                continue;

            p.isRemoved = true;
            //add triangles that are connected to the edge p
            HashSet<Triangle> trgToDel = new HashSet<Triangle>();
            for(Triangle t : p.e.v1.triangles)
                trgToDel.add(t);
            for(Triangle t : p.e.v2.triangles)
                trgToDel.add(t);
            //add edges that are connected to the edge p
            HashSet<Edge> edgToDel = new HashSet<Edge>();
            for(Edge e : p.e.v1.edges)
                edgToDel.add(e);
            for(Edge e : p.e.v2.edges)
                edgToDel.add(e);

            HashSet<Triangle> newTrgs = new HashSet<Triangle>();

            Vertex v = p.bestV;
            v.quadric = p.e.v1.quadric.plus(p.e.v2.quadric);

            for(Triangle t : trgToDel){
                Vertex v0 = t.vertices[0];
                Vertex v1 = t.vertices[1];
                Vertex v2 = t.vertices[2];

                if(v0.equals(p.e.v1) || v0.equals(p.e.v2))
                    v0 = v;
                if(v1.equals(p.e.v1) || v1.equals(p.e.v2))
                    v1 = v;
                if(v2.equals(p.e.v1) || v2.equals(p.e.v2))
                    v2 = v;

                Triangle newFace = new Triangle(v0, v1, v2);

                if(newFace.isDegenerate())
                    continue;
                if(t.getNormal().dot(newFace.getNormal()) < 1e-9) //has the normal flipped?
                {
                    valid = false;
                    break;
                }

                newTrgs.add(newFace);
            }

            if(!valid)
                continue;
            

            // removes old trgs
            for(Triangle t : trgToDel){
                newMesh.remove(t);

                t.vertices[0].triangles.remove(t);
                t.vertices[0].angle -= t.angle[0];
                t.vertices[0].area -= t.area;
                t.vertices[1].triangles.remove(t);
                t.vertices[1].angle -= t.angle[1];
                t.vertices[1].area -= t.area;
                t.vertices[2].triangles.remove(t);
                t.vertices[2].angle -= t.angle[2];
                t.vertices[2].area -= t.area;
            }
            // and add new ones
            for(Triangle t : newTrgs){
                newMesh.add(t);

                t.vertices[0].triangles.add(t);
                t.vertices[0].angle += t.angle[0];
                t.vertices[0].area += t.area;
                t.vertices[1].triangles.add(t);
                t.vertices[1].angle += t.angle[1];
                t.vertices[1].area += t.area;
                t.vertices[2].triangles.add(t);
                t.vertices[2].angle += t.angle[2];
                t.vertices[2].area += t.area;
            }

            for(Edge e : edgToDel){
                EdgePair ep = edgeMap.get(e);
                Vertex v1 = e.v1, v2 = e.v2;
                //...and from vertexToEdge
                v1.edges.remove(e);
                v2.edges.remove(e);

                if(v1.equals(p.e.v1) || v1.equals(p.e.v2))
                    v1 = v;
                if(v2.equals(p.e.v1) || v2.equals(p.e.v2))
                    v2 = v;

                ep.isRemoved = true;
                edgeQueue.remove(ep); //logn
                edgeMap.remove(ep.edge);
                if(!v1.equals(v2)){
                    Edge newE = new Edge(v1, v2);
                    if(!edgeMap.containsKey(newE)){
                        EdgePair newEp = new EdgePair(
                                        newE,
                                        v1.quadric.plus(v2.quadric));
                        edgeMap.put(newE, newEp);
                        //add newE to the queue...
                        edgeQueue.add(newEp);
                        //...and to vertexToEdge
                        v1.edges.add(newE);
                        v2.edges.add(newE);
                    }
                }
            }

        }
        ArrayList<Triangle> newM = new ArrayList<Triangle>();
        for(Triangle t : newMesh)
            newM.add(t);
        return new Mesh(newM);
    }
}
