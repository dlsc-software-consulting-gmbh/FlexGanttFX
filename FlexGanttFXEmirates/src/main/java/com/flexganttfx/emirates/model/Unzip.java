package com.flexganttfx.emirates.model;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {

    public static void unzip(InputStream in, OutputStream out) {

        try (ZipInputStream zip = new ZipInputStream(in)) {

            ZipEntry ze;

            while ((ze = zip.getNextEntry()) != null) {
                System.out.println("Extracting " + ze.getName());

                int size;
                byte[] buffer = new byte[2048];

                BufferedOutputStream bos = new BufferedOutputStream(out, buffer.length);

                while ((size = zip.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, size);
                }

                bos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
