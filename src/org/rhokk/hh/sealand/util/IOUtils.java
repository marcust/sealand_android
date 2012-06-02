package org.rhokk.hh.sealand.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtils {

	public static String toString( final InputStream in ) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy( in, bos );
		final byte[] byteArray = bos.toByteArray();
		
		return new String( byteArray, Charset.forName("UTF-8") );
		
	}

	private static void copy( final InputStream in, final OutputStream out ) throws IOException {
		int value;
		while ( ( value = in.read() ) != -1 ) {
			out.write( value );
		}
	}
}
