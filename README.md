trace
=====
A raytracer implemented in Java.

Currently only PLY-formatted models are supported.

Requirements
------------
 * Java 8
 * Maven

Building
--------
Run:

```bash
mvn package
```

An executable `.jar` will be written to `target/trace-1.0-SNAPSHOT.jar`.

(Alternatively, run `make`.)

Note that some dependencies are required, in particular
[Lombok preprocessor annotations](https://projectlombok.org/) and the usual
testing libraries (JUnit, Hamcrest). None of these are included in the final
`.jar` file.

Running the Tracer
------------------
Running the built `.jar` will start a ray trace.

```bash
java -jar target/trace-1.0-SNAPSHOT.jar [camera] [scene] [model...] [output]
```

(Alternatively, run `./run.sh [camera] [scene] [model...] [output]`)

Arguments are as follows:
 * `camera`: a camera configuration, see below
 * `scene`: a scene configuration, see below
 * `model...`: one or more `.ply` models to render
  * Argument order determines index for use in scene config, e.g. first model is
    #0, second is #1, etc.
 * `output`: the output path for the rendered `.ppm`

While running, status updates will be written to the console every 3 seconds.
An appropriate number of threads will be used (1 per logical core,
hyperthreaded cores included) for a speed boost.

Pre-made Scenes
---------------
There's a few scenes available for use. They all use a common filesystem layout,
so in general you can, for a scene named `$SCENE`, run:

```bash
./run.sh scenes/$SCENE/scene.{cam,mat} scenes/$SCENE/*.ply scenes/$SCENE.ppm
```

The output file will be written to `scenes/$SCENE.ppm`. For example, to render
the `moo` scene, the above will expand to the following:

```bash
./run.sh scenes/moo/scene.cam scenes/moo/scene.mat \
scenes/moo/0-floor.ply scenes/moo/1-grass.ply scenes/moo/2-wall-left.ply \
scenes/moo/3-wall-right.ply scenes/moo/4-cow.ply scenes/moo/5-shittyclouds.ply \
scenes/moo.ppm
```

Scenes available include `moo`, `airplane`, and `bunny`.

As a reference point, the `moo` scene takes just under 2 minutes to render on an
i7 4790k (200x200 image, no anti-aliasing). A `c4.8xlarge` EC2 instance should
take about half as long.

With 20 rounds of anti-aliasing on a 400x400 image
(using `scenes/walls-400px.cam`), a `c4.8xlarge` instance will render the `moo`
scene in around 35 minutes. [Example output](http://i.imgur.com/nAFNJPz.png)

Some performance considerations:
 * No fancy CPU vector instructions
 * Not using native matrix math libraries
 * No GPU acceleration
 * No renderer tricks to be smart about ray intersections (i.e. I'm lazy)
 * It's Java, so ... yeah.

All in all, performance is ¯\\\_(ツ)\_/¯.

File formats
------------

### Camera Configuration
Consists of 5 lines:
 1. `x y z` (position coords, doubles)
 2. `x y z` (look-at coords, doubles)
 3. `x y z` (up vector, `0 0 -1` to match Blender's viewport, doubles)
 4. `f` (focal length, double)
 5. `-x -y +x +y` (viewport bounds centered about zero, integers)

Due to how the renderer internals work, you'll need very large models along with
a large focal length to get good results. Generally a focal length of 150 units
for an image bounded with `-100 -100 100 100` should yield good results, but
you'll need to scale models to be ~1000 units wide.

For examples, see the `*.cam` files in the `scenes/` directory.

### Scene Configuration

The scene configuration is where you can define light sources and materials, one
per line.

* `L`: Light sources, e.g. `L [r] [g] [b] [x] [y] [z]`
 * `r`, `g`, and `b` are doubles ranging from 0.0 - 1.0 that define the color
   emitted by the source.
 * `x`, `y`, and `z` define the position. For ambient light, use `A A A`
 * All lights are point light sources
* `M`: Materials, e.g. `M [index] [start] [end] [r] [g] [b] [ks] [alpha] [kt]`
 * `index` specifies the model index to apply the material to. 0 is the first
   model specified as a command line argument, 1 is the second, ...
 * `start` and `end` define the start and faces to apply the material to
 * `r`, `g`, and `b` define the color of the material for diffuse lighting
 * `ks` is the specularity constant; 0 has no specular reflectivity, 1 is a
   perfect mirror (double, 0.0 - 1.0)
 * `alpha` is the Phong reflection exponent (integer, > 0)
 * `kt` is the translucency constant; 0 is fully opaque, 1 is clear (double,
   0.0 - 1.0)
 * Textures aren't currently supported.

For examples, see the `*.mat` files in the `scenes/` directory.

### Input and Output Formats

Input models should be in
[Standford PLY format](http://paulbourke.net/dataformats/ply/). Blender fully
supports this format; see the "Import PLY" and "Export PLY" commands. Note that
the renderer doesn't support actual scene positioning, so input models will need
to be transformed and positioned ahead of time.

Output images are in [PPM format](http://netpbm.sourceforge.net/doc/ppm.html).
Most Unix-y image viewers support this, including ImageMagick, the GIMP, Okular,
EoG, etc. To convert to something less obscure, try:

```bash
convert image.ppm image.png
```

Testing
-------
Unit tests can be run using:

```bash
mvn test
```
