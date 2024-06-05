package org.konceptosociala.kareladventures.utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;

public class DebugLine extends Node {
    public DebugLine(Vector3f from, Vector3f to, ColorRGBA color, AssetManager assetManager) {
        super();

        Mesh lineMesh = new Mesh();
        lineMesh.setMode(Mesh.Mode.Lines);
        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{ 
            from.x, from.y, from.z, 
            to.x, to.y, to.z
        });
        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });

        Geometry lineGeometry = new Geometry("line", lineMesh);
        Material lineMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lineMaterial.setColor("Color", color);
        lineGeometry.setMaterial(lineMaterial);
        attachChild(lineGeometry);
    }
}
