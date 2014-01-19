package com.uniwin.webkey.infoExtra.core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

 public class JQueryCode {
    public static String getHtmlText(String strUrl, String strEncoding) {  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection connection = (HttpURLConnection) url  
                    .openConnection();  
            DataInputStream in = new DataInputStream(connection.getInputStream());  
            return new String(JQueryCode.getBytes(in), strEncoding);  
        } catch (Exception e) {  
            return "";  
        }  
    }  
  
    public static byte[] downBinaryFile(String s) {  
        try {  
            URL url = new URL(s);  
            HttpURLConnection connection = (HttpURLConnection) url  
                    .openConnection();  
            DataInputStream in = new DataInputStream(connection  
                    .getInputStream());  
            return JQueryCode.getBytes(in);  
        } catch (Exception e) {  
            return null;  
        }  
    }  
  
    public static byte[] getBytes(InputStream is) throws Exception {  
        byte[] data = null;  
        Collection chunks = new ArrayList();  
        byte[] buffer = new byte[1024 * 1000];  
        int read = -1;  
        int size = 0;  
        while ((read = is.read(buffer)) != -1) {  
            if (read > 0) {  
                byte[] chunk = new byte[read];  
                System.arraycopy(buffer, 0, chunk, 0, read);  
                chunks.add(chunk);  
                size += chunk.length;  
            }  
        }  
        if (size > 0) {  
            ByteArrayOutputStream bos = null;  
            try {  
                bos = new ByteArrayOutputStream(size);  
                for (Iterator itr = chunks.iterator(); itr.hasNext();) {  
                    byte[] chunk = (byte[]) itr.next();  
                    bos.write(chunk);  
                }  
                data = bos.toByteArray();  
            } finally {  
                if (bos != null) {  
                    bos.close();  
                }  
            }  
        }  
        return data;  
    }  
}