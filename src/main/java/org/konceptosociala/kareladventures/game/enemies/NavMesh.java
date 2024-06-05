package org.konceptosociala.kareladventures.game.enemies;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;


import java.nio.FloatBuffer;

public class NavMesh {
    Geometry graph;
    FloatBuffer vertices;

    public NavMesh(Geometry g){
        graph = g;
        vertices = (FloatBuffer)(graph.getMesh().getBuffer(VertexBuffer.Type.Position).getData());
        //graph.getMesh().getBuffer(VertexBuffer.Type.)
        Mesh m = graph.getMesh();
        //m.setMode(Mesh.Mode.Lines);
        System.out.println(m.getIndicesAsList());
    }
}
