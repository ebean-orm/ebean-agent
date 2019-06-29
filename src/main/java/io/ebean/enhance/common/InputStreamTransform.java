package io.ebean.enhance.common;

import io.ebean.enhance.Transformer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.IllegalClassFormatException;


/**
 * Utility object that handles input streams for reading and writing.
 */
public class InputStreamTransform {

  private final Transformer transformer;
  private final ClassLoader classLoader;

  public InputStreamTransform(Transformer transformer, ClassLoader classLoader){
    this.transformer = transformer;
    this.classLoader = classLoader;
  }

  public void log(int level, String msg) {
    transformer.log(level, msg);
  }

  /**
  * Transform a file.
  */
  public byte[] transform(String className, File file) throws IOException, IllegalClassFormatException {
    try {
      return transform(className, new FileInputStream(file));

    } catch (FileNotFoundException e){
      throw new RuntimeException(e);
    }
  }

  /**
  * Transform a input stream.
  */
  public byte[] transform(String className, InputStream is) throws IOException, IllegalClassFormatException {

    try {

      byte[] classBytes = readBytes(is);

      return transformer.transform(classLoader, className, null, null, classBytes);

    } finally {
      if (is != null){
        is.close();
      }
    }
  }

  /**
  * Helper method to write bytes to a file.
  */
  public static void writeBytes(byte[] bytes, File file) throws IOException {
    try (final FileOutputStream fos = new FileOutputStream(file)) {
      writeBytes(bytes, fos);
    }
  }

  /**
  * Helper method to write bytes to a OutputStream.
  */
  public static void writeBytes(byte[] bytes, OutputStream os) throws IOException {

    try (BufferedOutputStream bos = new BufferedOutputStream(os)) {
      try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
        byte[] buf = new byte[1028];

        int len;
        while ((len = bis.read(buf, 0, buf.length)) > -1){
          bos.write(buf, 0, len);
        }
      }
    }
  }


  public static byte[] readBytes(InputStream is) throws IOException {

    try (BufferedInputStream bis = new BufferedInputStream(is)) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(4096)) {
        byte[] buf = new byte[1028];

        int len;
        while ((len = bis.read(buf, 0, buf.length)) > -1){
          baos.write(buf, 0, len);
        }

        return baos.toByteArray();
      }
    }
  }
}
