package org.htmlparser.lexer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.zip.*;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.util.ParserException;



public class Page
    implements Serializable
{

    public Page()
    {
        this("");
    }

    public Page(URLConnection connection)
        throws ParserException
    {
        if(null == connection)
        {
            throw new IllegalArgumentException("connection cannot be null");
        } else
        {
            setConnection(connection);
            mBaseUrl = null;
            return;
        }
    }

    public Page(InputStream stream, String charset)
        throws UnsupportedEncodingException
    {
        if(null == stream)
            throw new IllegalArgumentException("stream cannot be null");
        if(null == charset)
            charset = "ISO-8859-1";
        mSource = new InputStreamSource(stream, charset);
        mIndex = new PageIndex(this);
        mConnection = null;
        mUrl = null;
        mBaseUrl = null;
    }

    public Page(String text, String charset)
    {
        if(null == text)
            throw new IllegalArgumentException("text cannot be null");
        if(null == charset)
            charset = "ISO-8859-1";
        mSource = new StringSource(text, charset);
        mIndex = new PageIndex(this);
        mConnection = null;
        mUrl = null;
        mBaseUrl = null;
    }

    public Page(String text)
    {
        this(text, null);
    }

    public Page(Source source)
    {
        if(null == source)
        {
            throw new IllegalArgumentException("source cannot be null");
        } else
        {
            mSource = source;
            mIndex = new PageIndex(this);
            mConnection = null;
            mUrl = null;
            mBaseUrl = null;
            return;
        }
    }

    public static ConnectionManager getConnectionManager()
    {
        return mConnectionManager;
    }

    public static void setConnectionManager(ConnectionManager manager)
    {
        mConnectionManager = manager;
    }

    public String getCharset(String content)
    {
        String CHARSET_STRING = "charset";
        String ret;
        if(null == mSource)
            ret = "ISO-8859-1";
        else
            ret = mSource.getEncoding();
        if(null != content)
        {
            int index = content.indexOf("charset");
            if(index != -1)
            {
                content = content.substring(index + "charset".length()).trim();
                if(content.startsWith("="))
                {
                    content = content.substring(1).trim();
                    index = content.indexOf(";");
                    if(index != -1)
                        content = content.substring(0, index);
                    if(content.startsWith("\"") && content.endsWith("\"") && 1 < content.length())
                        content = content.substring(1, content.length() - 1);
                    if(content.startsWith("'") && content.endsWith("'") && 1 < content.length())
                        content = content.substring(1, content.length() - 1);
                    ret = findCharset(content, ret);
                }
            }
        }
        return ret;
    }

    public static String findCharset(String name, String fallback)
    {
        String ret;
        try
        {
            Class cls = Class.forName("java.nio.charset.Charset");
            Method method = cls.getMethod("forName", new Class[] {
                java.lang.String.class
            });
            Object object = method.invoke(null, new Object[] {
                name
            });
            method = cls.getMethod("name", new Class[0]);
            object = method.invoke(object, new Object[0]);
            ret = (String)object;
        }
        catch(ClassNotFoundException cnfe)
        {
            ret = name;
        }
        catch(NoSuchMethodException nsme)
        {
            ret = name;
        }
        catch(IllegalAccessException ia)
        {
            ret = name;
        }
        catch(InvocationTargetException ita)
        {
            ret = fallback;
            System.out.println("unable to determine cannonical charset name for " + name + " - using " + fallback);
        }
        return ret;
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException
    {
        if(null != getConnection())
        {
            out.writeBoolean(true);
            out.writeInt(mSource.offset());
            String href = getUrl();
            out.writeObject(href);
            setUrl(getConnection().getURL().toExternalForm());
            Source source = getSource();
            mSource = null;
            PageIndex index = mIndex;
            mIndex = null;
            out.defaultWriteObject();
            mSource = source;
            mIndex = index;
        } else
        {
            out.writeBoolean(false);
            String href = getUrl();
            out.writeObject(href);
            setUrl(null);
            out.defaultWriteObject();
            setUrl(href);
        }
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        boolean fromurl = in.readBoolean();
        if(fromurl)
        {
            int offset = in.readInt();
            String href = (String)in.readObject();
            in.defaultReadObject();
            if(null != getUrl())
            {
                URL url = new URL(getUrl());
                try
                {
                    setConnection(url.openConnection());
                }
                catch(ParserException pe)
                {
                    throw new IOException(pe.getMessage());
                }
            }
            Cursor cursor = new Cursor(this, 0);
            for(int i = 0; i < offset; i++)
                try
                {
                    getCharacter(cursor);
                }
                catch(ParserException pe)
                {
                    throw new IOException(pe.getMessage());
                }

            setUrl(href);
        } else
        {
            String href = (String)in.readObject();
            in.defaultReadObject();
            setUrl(href);
        }
    }

    public void reset()
    {
        getSource().reset();
        mIndex = new PageIndex(this);
    }

    public void close()
        throws IOException
    {
        if(null != getSource())
            getSource().destroy();
    }

    protected void finalize()
        throws Throwable
    {
        close();
    }

    public URLConnection getConnection()
    {
        return mConnection;
    }

    public void setConnection(URLConnection connection)
        throws ParserException
    {
        mConnection = connection;
        mConnection.setConnectTimeout(6000);
        mConnection.setReadTimeout(6000);
        try
        {
            getConnection().connect();
        }
        catch(UnknownHostException uhe)
        {
            throw new ParserException("Connect to " + mConnection.getURL().toExternalForm() + " failed.", uhe);
        }
        catch(IOException ioe)
        {
            throw new ParserException("Exception connecting to " + mConnection.getURL().toExternalForm() + " (" + ioe.getMessage() + ").", ioe);
        }
        String type = getContentType();
        String charset = getCharset(type);
        try
        {
            String contentEncoding = connection.getContentEncoding();
//          System.out.println("contentEncoding="+contentEncoding);
            Stream stream;
            if(null != contentEncoding && -1 != contentEncoding.indexOf("gzip"))
                stream = new Stream(new GZIPInputStream(getConnection().getInputStream()));
            else
            if(null != contentEncoding && -1 != contentEncoding.indexOf("deflate"))
                stream = new Stream(new InflaterInputStream(getConnection().getInputStream(), new Inflater(true)));
            else{
                stream = new Stream(getConnection().getInputStream());
            }

            try
            {
            	if(charset.indexOf("ISO-8859-1")!=-1){
            		
            		charset =getGaoBinDEFAULT_CHARSET() ;
            		
            	}
		mSource = new InputStreamSource(stream, charset);
            }
            catch(UnsupportedEncodingException uee)
            {
                charset = "ISO-8859-1";
                mSource = new InputStreamSource(stream, charset);
            }
        }
        catch(IOException ioe)
        {
            throw new ParserException("Exception getting input stream from " + mConnection.getURL().toExternalForm() + " (" + ioe.getMessage() + ").", ioe);
        }
        mUrl = connection.getURL().toExternalForm();
		mIndex = new PageIndex(this);
    }

    public String getUrl()
    {
        return mUrl;
    }

    public void setUrl(String url)
    {
        mUrl = url;
    }

    public String getBaseUrl()
    {
        return mBaseUrl;
    }

    public void setBaseUrl(String url)
    {
        mBaseUrl = url;
    }

    public Source getSource()
    {
        return mSource;
    }

    public String getContentType()
    {
        String ret = "text/html";
        URLConnection connection = getConnection();
        if(null != connection)
        {
            String content = connection.getHeaderField("Content-Type");
            if(null != content)
                ret = content;
        }
        return ret;
    }

    public char getCharacter(Cursor cursor)
        throws ParserException
    {
        int i = cursor.getPosition();
        int offset = mSource.offset();
        char ret;
        if(offset == i)
            try
            {
                i = mSource.read();
                if(-1 == i)
                {
                    ret = '\uFFFF';
                } else
                {
                    ret = (char)i;
                    cursor.advance();
                }
            }
            catch(IOException ioe)
            {
                throw new ParserException("problem reading a character at position " + cursor.getPosition(), ioe);
            }
        else
        if(offset > i)
        {
            try
            {
                ret = mSource.getCharacter(i);
            }
            catch(IOException ioe)
            {
                throw new ParserException("can't read a character at position " + i, ioe);
            }
            cursor.advance();
        } else
        {
            throw new ParserException("attempt to read future characters from source " + i + " > " + mSource.offset());
        }
        if('\r' == ret)
        {
            ret = '\n';
            if(mSource.offset() == cursor.getPosition())
                try
                {
                    i = mSource.read();
                    if(-1 != i)
                        if('\n' == (char)i)
                            cursor.advance();
                        else
                            try
                            {
                                mSource.unread();
                            }
                            catch(IOException ioe)
                            {
                                throw new ParserException("can't unread a character at position " + cursor.getPosition(), ioe);
                            }
                }
                catch(IOException ioe)
                {
                    throw new ParserException("problem reading a character at position " + cursor.getPosition(), ioe);
                }
            else
                try
                {
                    if('\n' == mSource.getCharacter(cursor.getPosition()))
                        cursor.advance();
                }
                catch(IOException ioe)
                {
                    throw new ParserException("can't read a character at position " + cursor.getPosition(), ioe);
                }
        }
        if('\n' == ret)
            mIndex.add(cursor);
        return ret;
    }

    public void ungetCharacter(Cursor cursor)
        throws ParserException
    {
        cursor.retreat();
        int i = cursor.getPosition();
        try
        {
            char ch = mSource.getCharacter(i);
            if('\n' == ch && 0 != i)
            {
                ch = mSource.getCharacter(i - 1);
                if('\r' == ch)
                    cursor.retreat();
            }
        }
        catch(IOException ioe)
        {
            throw new ParserException("can't read a character at position " + cursor.getPosition(), ioe);
        }
    }

    public String getEncoding()
    {
        return getSource().getEncoding();
    }

    public void setEncoding(String character_set)
        throws ParserException
    {
    	Page.GaoBinDEFAULT_CHARSET = character_set;
        getSource().setEncoding(character_set);
    }

    public URL constructUrl(String link, String base)
        throws MalformedURLException
    {
        return constructUrl(link, base, false);
    }

    public URL constructUrl(String link, String base, boolean strict)
        throws MalformedURLException
    {
        int index;
        URL url;
        if(!strict && '?' == link.charAt(0))
        {
            if(-1 != (index = base.lastIndexOf('?')))
                base = base.substring(0, index);
            url = new URL(base + link);
        } else
        {
            url = new URL(new URL(base), link);
        }
        String path = url.getFile();
        boolean modified = false;
        boolean absolute = link.startsWith("/");
        if(!absolute)
            do
            {
                if(!path.startsWith("/."))
                    break;
                if(path.startsWith("/../"))
                {
                    path = path.substring(3);
                    modified = true;
                    continue;
                }
                if(!path.startsWith("/./") && !path.startsWith("/."))
                    break;
                path = path.substring(2);
                modified = true;
            } while(true);
        while(-1 != (index = path.indexOf("/\\"))) 
        {
            path = path.substring(0, index + 1) + path.substring(index + 2);
            modified = true;
        }
        if(modified)
            url = new URL(url, path);
        return url;
    }

    public String getAbsoluteURL(String link)
    {
        return getAbsoluteURL(link, false);
    }

    public String getAbsoluteURL(String link, boolean strict)
    {
        String ret;
        if(null == link || "".equals(link))
            ret = "";
        else
            try
            {
                String base = getBaseUrl();
                if(null == base)
                    base = getUrl();
                if(null == base)
                {
                    ret = link;
                } else
                {
                    URL url = constructUrl(link, base, strict);
                    ret = url.toExternalForm();
                }
            }
            catch(MalformedURLException murle)
            {
                ret = link;
            }
        return ret;
    }

    public int row(Cursor cursor)
    {
        return mIndex.row(cursor);
    }

    public int row(int position)
    {
        return mIndex.row(position);
    }

    public int column(Cursor cursor)
    {
        return mIndex.column(cursor);
    }

    public int column(int position)
    {
        return mIndex.column(position);
    }

    public String getText(int start, int end)
        throws IllegalArgumentException
    {
        String ret;
        try
        {
            ret = mSource.getString(start, end - start);
        }
        catch(IOException ioe)
        {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
        return ret;
    }

    public void getText(StringBuffer buffer, int start, int end)
        throws IllegalArgumentException
    {
        if(mSource.offset() < start || mSource.offset() < end)
            throw new IllegalArgumentException("attempt to extract future characters from source" + start + "|" + end + " > " + mSource.offset());
        int length;
        if(end < start)
        {
            length = end;
            end = start;
            start = length;
        }
        length = end - start;
        try
        {
            mSource.getCharacters(buffer, start, length);
        }
        catch(IOException ioe)
        {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
    }

    public String getText()
    {
        return getText(0, mSource.offset());
    }

    public void getText(StringBuffer buffer)
    {
        getText(buffer, 0, mSource.offset());
    }

    public void getText(char array[], int offset, int start, int end)
        throws IllegalArgumentException
    {
        if(mSource.offset() < start || mSource.offset() < end)
            throw new IllegalArgumentException("attempt to extract future characters from source");
        int length;
        if(end < start)
        {
            length = end;
            end = start;
            start = length;
        }
        length = end - start;
        try
        {
            mSource.getCharacters(array, offset, start, end);
        }
        catch(IOException ioe)
        {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
    }

    public String getLine(Cursor cursor)
    {
        int line = row(cursor);
        int size = mIndex.size();
        int start;
        int end;
        if(line < size)
        {
            start = mIndex.elementAt(line);
            if(++line <= size)
                end = mIndex.elementAt(line);
            else
                end = mSource.offset();
        } else
        {
            start = mIndex.elementAt(line - 1);
            end = mSource.offset();
        }
        return getText(start, end);
    }

    public String getLine(int position)
    {
        return getLine(new Cursor(this, position));
    }

    public String toString()
    {
        String ret;
        if(mSource.offset() > 0)
        {
            StringBuffer buffer = new StringBuffer(43);
            int start = mSource.offset() - 40;
            if(0 > start)
                start = 0;
            else
                buffer.append("...");
            getText(buffer, start, mSource.offset());
            ret = buffer.toString();
        } else
        {
            ret = super.toString();
        }
        return ret;
    }

    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static String GaoBinDEFAULT_CHARSET;
	public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final char EOF = 65535;
    protected String mUrl;
    protected String mBaseUrl;
    protected Source mSource;
    protected PageIndex mIndex;
    protected transient URLConnection mConnection;
    protected static ConnectionManager mConnectionManager = new ConnectionManager();
    
    public static String getGaoBinDEFAULT_CHARSET() {
		return GaoBinDEFAULT_CHARSET;
	}

	public static void setGaoBinDEFAULT_CHARSET(String gaoBinDEFAULT_CHARSET) {
		GaoBinDEFAULT_CHARSET = gaoBinDEFAULT_CHARSET;
	}

}
