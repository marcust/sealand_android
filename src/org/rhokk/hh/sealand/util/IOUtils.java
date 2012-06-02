package org.rhokk.hh.sealand.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtils {

	private static final Charset UTF_8 = Charset.forName("UTF-8");
	
	public static String toString( final InputStream in ) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copy( in, bos );
		final byte[] byteArray = bos.toByteArray();
		bos.close();
		return new String( byteArray, UTF_8 );
	}

	public static String toString( File file ) throws IOException {
		final FileInputStream fileInputStream = new FileInputStream( file );
		try {
			return toString( fileInputStream );
		} finally {
			closeQuietly( fileInputStream );
		}
	
	}
	
	private static void copy( final InputStream in, final OutputStream out ) throws IOException {
		int value;
		while ( ( value = in.read() ) != -1 ) {
			out.write( value );
		}
	}

	public static void write(String string, OutputStream output ) throws IOException {
		final ByteArrayInputStream bis = new ByteArrayInputStream( string.getBytes( UTF_8 ) );
		copy( bis, output );
		bis.close();
	}

	public static void closeQuietly( OutputStream out ) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeQuietly( InputStream out ) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
