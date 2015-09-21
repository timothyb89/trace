package org.timothyb89.trace.model.ply;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.timothyb89.trace.math.Model;

/**
 *
 * @author timothyb
 */
public class PLYWriter {
	
	public static void write(Model model, Path path) {
		try (BufferedWriter w = Files.newBufferedWriter(path)) {
			PrintWriter pw = new PrintWriter(w);
			
			pw.println("ply");
			pw.println("format ascii 1.0");
			
			pw.println("element vertex " + model.countVertices());
			pw.println("property float32 x");
			pw.println("property float32 y");
			pw.println("property float32 z");
			
			pw.println("element face " + model.faces().size());
			pw.println("property list uint8 int32 vertex_indices");
			pw.println("end_header");
			
			model.vertexData()
					.colStream()
					.forEach(v -> pw.printf("%f %f %f\n", v[0], v[1], v[2]));
			
			model.faces().forEach(f -> pw.printf("%d %s\n",
					f.size(),
					f.stream()
							.map(String::valueOf)
							.collect(Collectors.joining(" "))));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
