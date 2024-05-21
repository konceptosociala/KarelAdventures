package org.konceptosociala.kareladventures;

// import java.awt.*;
// import org.konceptosociala.kareladventures.state.IntroState;
// import org.konceptosociala.kareladventures.state.MainMenuState;
// import com.jme3.app.SimpleApplication;
// import com.jme3.system.AppSettings;
// import lombok.Getter;

// @Getter
// public class KarelAdventures extends SimpleApplication {
//     private AppSettings appSettings = new AppSettings(true);

//     private IntroState introState;
//     private MainMenuState mainMenuState;

//     public KarelAdventures() {
//         super();
//         setFullscreen();
//         setDisplayStatView(false);
//         setDisplayFps(false);
//         setShowSettings(false);
//         setSettings(appSettings);
//     }
    
//     @Override
//     public void simpleInitApp() {
//         try {
//             flyCam.setEnabled(false);

//             introState = new IntroState(cam.getWidth(), cam.getHeight());
//             mainMenuState = new MainMenuState();

//             stateManager.attach(introState);
//         } catch (Exception e) {
//             e.printStackTrace();
//             System.exit(-1);
//         }
//     }

//     public static void main(String[] args) {
//         KarelAdventures app = new KarelAdventures();
//         app.start();
//     }
    
//     private void setFullscreen() {
//         GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//         DisplayMode[] modes = device.getDisplayModes();
//         int i = 0;
//         appSettings.setResolution(modes[i].getWidth(), modes[i].getHeight());
//         appSettings.setFrequency(modes[i].getRefreshRate());
//         appSettings.setBitsPerPixel(modes[i].getBitDepth());
//         appSettings.setFullscreen(device.isFullScreenSupported());
//     }
// }
    
    import com.jme3.app.SimpleApplication;
    import com.jme3.bullet.BulletAppState;
    import com.jme3.bullet.collision.shapes.CollisionShape;
    import com.jme3.bullet.control.BetterCharacterControl;
    import com.jme3.bullet.control.RigidBodyControl;
    import com.jme3.bullet.util.CollisionShapeFactory;
    import com.jme3.input.KeyInput;
    import com.jme3.input.controls.ActionListener;
    import com.jme3.input.controls.KeyTrigger;
    import com.jme3.light.AmbientLight;
    import com.jme3.light.DirectionalLight;
    import com.jme3.material.Material;
    import com.jme3.math.ColorRGBA;
    import com.jme3.math.Vector3f;
    import com.jme3.scene.Geometry;
    import com.jme3.scene.Node;
    import com.jme3.scene.Spatial;
    import com.jme3.scene.shape.Box;
    
    /**
     * 
     * @author august
     * Basic FPS style character control using BetterCharacterControl
     */
    public class KarelAdventures extends SimpleApplication
            implements ActionListener {
    
      private Spatial sceneModel;
      private BulletAppState bulletAppState;
      private RigidBodyControl landscape;
     // private CharacterControl player;
      private BetterCharacterControl player;
      private Vector3f walkDirection = new Vector3f();
      private boolean left = false, right = false, up = false, down = false;
      private Geometry PlayerModel;
      //Temporary vectors used on each frame.
      //They here to avoid instanciating new vectors on each frame
      private Vector3f camDir = new Vector3f();
      private Vector3f camLeft = new Vector3f();
      // Our movement speed
      private float speed;
      private float strafeSpeed;
      private float headHeight;
      
      public static void main(String[] args) {
        KarelAdventures app = new KarelAdventures();
        app.start();
      }
    
      public void simpleInitApp() {
          // set player speed
          speed = 6f;
          strafeSpeed = 4f;
          headHeight = 3f;
          
           /** Create a box to use as our player model */
            Box box1 = new Box(1,1,1);
            PlayerModel = new Geometry("Box", box1);
 Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        PlayerModel.setMaterial(mat);    
        PlayerModel.setLocalTranslation(new Vector3f(0,6,0));
            PlayerModel.setLocalTranslation(new Vector3f(0,6,0));
            rootNode.attachChild(PlayerModel);
            
            
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.setLocation(new Vector3f(0,6,0));
        /** Set up Physics */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    
        // We re-use the flyby camera for rotation, while positioning is handled by physics
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    
        setUpKeys();
        setUpLight();
    
        sceneModel = assetManager.loadModel("Scenes/farm.j3o");
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
    
        // create character control parameters (Radius,Height,Weight)
        // Radius and Height determine the size of the collision bubble
        // Weight determines how much gravity effects the control
         player = new BetterCharacterControl(2f,6f,1f);
         // set basic physical properties:
            player.setJumpForce(new Vector3f(0,5f,0)); 
            player.setGravity(new Vector3f(0,1f,0));
            player.warp(new Vector3f(0,6,0)); 
    
        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        /* 
         * Add player control to the box. The box will act as our player model
        * while the camera follows it
        */
        PlayerModel.addControl(player);
        
      }
    
      private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
    
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
      }
    
      /** We over-write some navigational key mappings here, so we can
       * add physics-controlled walking and jumping: */
      private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
      }
    
      /** These are our custom actions triggered by key presses.
       * We do not walk yet, we just keep track of the direction the user pressed. */
      public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
          left = isPressed;
        } else if (binding.equals("Right")) {
          right= isPressed;
        } else if (binding.equals("Up")) {
          up = isPressed;
        } else if (binding.equals("Down")) {
          down = isPressed;
        } else if (binding.equals("Jump")) {
          if (isPressed) { player.jump(); }
        }
      }
    
      /**
       * This is the main event loop--walking happens here.
       * We check in which direction the player is walking by interpreting
       * the camera direction forward (camDir) and to the side (camLeft).
       * The setWalkDirection() command is what lets a physics-controlled player walk.
       * We also make sure here that the camera moves with player.
       */
      @Override
        public void simpleUpdate(float tpf) {
          /*
           * The direction of character is determined by the camera angle
           * the Y direction is set to zero to keep our character from
           * lifting of terrain. For free flying games simply ad speed 
           * to Y axis
           */
            camDir.set(cam.getDirection()).multLocal(speed, 0.0f, speed);
            camLeft.set(cam.getLeft()).multLocal(strafeSpeed);
            walkDirection.set(0, 0, 0);
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }
            player.setWalkDirection(walkDirection);
            
            /*
             * By default the location of the box is on the bottom of the terrain
             * we make a slight offset to adjust for head height.
             */
            cam.setLocation(new Vector3f(PlayerModel.getLocalTranslation().x,PlayerModel.getLocalTranslation().y + headHeight,PlayerModel.getLocalTranslation().z));
        }
    }