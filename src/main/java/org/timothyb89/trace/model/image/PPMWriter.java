package org.timothyb89.trace.model.image;

import org.timothyb89.trace.math.Image;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author timothyb
 */
public class PPMWriter {

	public static void write(Image image, PrintWriter out) {
		out.println("P3");
		out.printf("%d %d 255\n", image.width(), image.height());

		for (int x = 0; x < image.width(); x++) {
			for (int y = 0; y < image.height(); y++) {
				out.print(image.red(x, y));
				out.print(" ");
				out.print(image.green(x, y));
				out.print(" ");
				out.print(image.blue(x, y));
				out.print(" ");
			}

			out.println();
		}
	}

	public static void write(Image image, Path path) {
		try (BufferedWriter w = Files.newBufferedWriter(path)) {
			PrintWriter pw = new PrintWriter(w);

			write(image, pw);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
