package io.ebean.enhance.entity;

import io.ebean.enhance.common.ClassBytesReader;
import io.ebean.enhance.common.InputStreamTransform;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Implementation of ClassBytesReader based on URLClassLoader.
 */
public class ClassPathClassBytesReader implements ClassBytesReader {
	

	private final URL[] urls;

	public ClassPathClassBytesReader(URL[] urls) {
		this.urls = urls == null ? new URL[0]: urls;
	}
	
	public byte[] getClassBytes(String className, ClassLoader classLoader) {

		try (URLClassLoader cl = new URLClassLoader(urls, classLoader)) {

			InputStream is = null;
			try {

				// read the class bytes, and define the class
				String resource = className.replace('.', '/') + ".class";

				URL url = cl.getResource(resource);
				if (url == null) {
					return null;
				}

				is = url.openStream();
				return InputStreamTransform.readBytes(is);

			} catch (IOException e){
				throw new RuntimeException("IOException reading bytes for "+className, e);

			} finally {
				if (is != null){
					try {
						is.close();
					} catch (IOException e) {
						throw new RuntimeException("Error closing InputStream for "+className, e);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error closing URLClassLoader for "+className, e);
		}
	}
	
}
