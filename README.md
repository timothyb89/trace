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

Model Manipulation REPL
-----------------------
Running the built `.jar` will start an interactive REPL that performs basic
model manipulation. To run from the build directory:

```bash
java -jar target/trace-1.0-SNAPSHOT.jar [input] [output]
```

... where `[input]` is the source PLY file, and `[output]` is the destination.

For command information, run '?' from the REPL. The commands supported are:
 * `T [x] [y] [z]`: translate the object by `x`, `y`, and `z` units along the
   associated axes.
 * `S [x] [y] [z]`: scale the object by factors `x`, `y`, and `z` on the
   associated axes.
 * `R [x] [y] [z] [t]`: rotate the object by `t` degrees about the axis defined
   by the vector `[x, y, z]`.
 * `W`: perform the transformations, write the model to the destination file,
   and exit.

 Note that the transformations are not applied until the `W` command is run.
 The different transforms will be combined to speed up the computation process.
