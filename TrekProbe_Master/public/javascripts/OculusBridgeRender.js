function OculusBridgeRender() {
    var oculusBridge;

//    var showingView = false;
    var scene, secondScene, camera, renderer, element;
    var useRift = false;
    var cameraLoaded = false;
    var aspectRatio, windowHalf;
    var riftCam;
    var video, videoImage, videoImageContext, videoTexture;
    var videoSecond, videoImageSecond, videoImageContextSecond, videoTextureSecond;

    var stereoVision = false;

    init();
    animate();
/*
    this.setShowingView = function(b) {
        showingView = b;
    }
*/

    this.toggleRender = function(stereoscopic) {
        stereoVision = stereoscopic;

        if (!cameraLoaded){
            initVideoMaterial();
            cameraLoaded = true;
        }

        useRift = !useRift;
        onResize();
    }

    function init() {
        initScene();

        window.addEventListener('resize', onResize, false);

        onResize();

        oculusBridge = new OculusBridge({
            "debug" : true,
            "onOrientationUpdate" : bridgeOrientationUpdated,
            "onConfigUpdate"      : bridgeConfigUpdated,
            "onConnect"           : bridgeConnected,
            "onDisconnect"        : bridgeDisconnected
        });
        oculusBridge.connect();

        riftCam = new THREE.OculusRiftEffect(renderer);

     /*   riftCam.separation = 20;
        riftCam.distortion = 0.1;
        riftCam.fov = 110;
        */
    }

    function initScene() {

        windowHalf = new THREE.Vector2(window.innerWidth / 2, window.innerHeight / 2);
        aspectRatio = window.innerWidth / window.innerHeight;

        scene = new THREE.Scene();
        secondScene = new THREE.Scene();

        camera = new THREE.PerspectiveCamera( 75, aspectRatio, 1, 10000 );
        camera.position.z = 200; // 200
        camera.position.y = 50;

        /*     camera.useQuaternion = true;

         camera.position.set(100, 150, 100);
         camera.lookAt(scene.position);
         */
        /*    geometry = new THREE.BoxGeometry( 200, 200, 200 );
         material = new THREE.MeshBasicMaterial( { color: 0xff0000, wireframe: true } );
         mesh = new THREE.Mesh( geometry, material );
         scene.add( mesh );
         */
        //     initVideoMaterial();

        renderer = new THREE.WebGLRenderer({antialias:true});
        renderer.setClearColor(0xdbf7ff);
        renderer.setSize(window.innerWidth, window.innerHeight);

        element = document.getElementById('oculusView');
        element.appendChild(renderer.domElement);
        //  document.appendChild(renderer.domElement);
    }

    function animate(){
        if (cameraLoaded) {
            videoImageContext.drawImage(video, 0, 0);
            if (videoTexture)
                videoTexture.needsUpdate = true;

            if(stereoVision){
                videoImageContextSecond.drawImage(videoSecond, 0, 0);
                if (videoTextureSecond)
                    videoTextureSecond.needsUpdate = true;
            }
        }

        if (render()) {
            requestAnimationFrame(animate);
        }
    }

    function render() {
        try {
            if (useRift) {
                if(stereoVision){
                    riftCam.renderTwoCam(scene, secondScene, camera);
                }
                else {
                    riftCam.render(scene, camera);
                }
            } else {
                // controls.update();
                renderer.render(scene, camera);
            }
        } catch (e) {
            console.log(e);
            if (e.name == "SecurityError") {
                //    crashSecurity(e);
            } else {
                //   crashOther(e);
            }
            return false;
        }
        return true;
    }

    function onResize() {
        if(!useRift){
            windowHalf = new THREE.Vector2(window.innerWidth / 2, window.innerHeight / 2);
            aspectRatio = window.innerWidth / window.innerHeight;

            camera.aspect = aspectRatio;
            camera.updateProjectionMatrix();

            renderer.setSize(window.innerWidth, window.innerHeight);
        } else {
            riftCam.setSize(window.innerWidth, window.innerHeight);
        }
    }

    function bridgeConnected(){
        document.getElementById("logo").className = "";
    }

    function bridgeDisconnected(){
        document.getElementById("logo").className = "offline";
    }

    function bridgeConfigUpdated(config){
        console.log("Oculus config updated.");
        riftCam.setHMD(config);
        // riftCam.setHMD(config);
    }

    function bridgeOrientationUpdated(quatValues) {

        // Do first-person style controls (like the Tuscany demo) using the rift and keyboard.

        // TODO: Don't instantiate new objects in here, these should be re-used to avoid garbage collection.

        // make a quaternion for the the body angle rotated about the Y axis.
        var quat = new THREE.Quaternion();
        quat.setFromAxisAngle(bodyAxis, bodyAngle);

        // make a quaternion for the current orientation of the Rift
        var quatCam = new THREE.Quaternion(quatValues.x, quatValues.y, quatValues.z, quatValues.w);

        // multiply the body rotation by the Rift rotation.
        quat.multiply(quatCam);


        // Make a vector pointing along the Z axis and rotate it accoring to the combined look/body angle.
        var xzVector = new THREE.Vector3(0, 0, 1);
        xzVector.applyQuaternion(quat);

        // Compute the X/Z angle based on the combined look/body angle.  This will be used for FPS style movement controls
        // so you can steer with a combination of the keyboard and by moving your head.
        viewAngle = Math.atan2(xzVector.z, xzVector.x) + Math.PI;

        // Apply the combined look/body angle to the camera.
        camera.quaternion.copy(quat);
    }

    function initVideoMaterial(){
        // VIDEO
        video = document.getElementById( 'SlaveTrekProbe' );
        videoSecond = document.getElementById( 'SecondCameraTrekProbe' );

        videoImage = document.createElement( 'canvas' );
        videoImage.width = 640;
        videoImage.height = 480;

        videoImageSecond = document.createElement( 'canvas' );
        videoImageSecond.width = 640;
        videoImageSecond.height = 480;

        videoImageContext = videoImage.getContext( '2d' );
        // background color if no video present
        videoImageContext.fillStyle = '#000000';
        videoImageContext.fillRect( 0, 0, videoImage.width, videoImage.height );

        videoImageContextSecond = videoImageSecond.getContext( '2d' );
        // background color if no video present
        videoImageContextSecond.fillStyle = '#000000';
        videoImageContextSecond.fillRect( 0, 0, videoImageSecond.width, videoImageSecond.height );

        videoTexture = new THREE.Texture( videoImage );
        videoTexture.minFilter = THREE.LinearFilter;
        videoTexture.magFilter = THREE.LinearFilter;
        videoTexture.needsUpdate = true;

        videoTextureSecond = new THREE.Texture( videoImageSecond );
        videoTextureSecond.minFilter = THREE.LinearFilter;
        videoTextureSecond.magFilter = THREE.LinearFilter;
        videoTextureSecond.needsUpdate = true;

        var movieMaterial = new THREE.MeshBasicMaterial( { map: videoTexture, overdraw: true, side:THREE.flipSided } );
        var movieMaterialSecond = new THREE.MeshBasicMaterial( { map: videoTextureSecond, overdraw: true, side:THREE.flipSided } );

        // the geometry on which the movie will be displayed;
        // movie image will be scaled to fit these dimensions.
        var movieGeometry = new THREE.PlaneGeometry( 640, 480, 1, 1 );
        var movieScreen = new THREE.Mesh( movieGeometry, movieMaterial );

        var movieGeometrySecond = new THREE.PlaneGeometry( 640, 480, 1, 1 );
        var movieScreenSecond = new THREE.Mesh( movieGeometrySecond, movieMaterialSecond );


        movieScreen.position.set(0,50,0);
        scene.add(movieScreen);

        movieScreenSecond.position.set(0,50,0);
        secondScene.add(movieScreenSecond);
        // VIDEO
    }
}