package gov.medicaid.processmanager.util;

import gov.medicaid.processmanager.exceptions.ProcessManagerException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * @author Marvin Wolfthal
 * @since 7/17/11
 */
public class IoUtil {
    private static final String CLASSPATH_PROTOCOL = "classpath:";
    private static final String FILE_URL_PROTOCOL = "file:";
    private static final String EMPTY_STR = "";

    private IoUtil() {
    }

    /**
     * @param filespec classpath, URL or file system
     *                 if there is no classpath or file protocol it is
     *                 assumed to be a file system path
     */
    public static InputStream getInputStream(String filespec)
        throws ProcessManagerException {
        assert (null != filespec);
        InputStream is;
        try {
            if (filespec.startsWith(CLASSPATH_PROTOCOL)) {
                is = IoUtil.getResourceAsStream(filespec);
            } else if (filespec.startsWith(FILE_URL_PROTOCOL)) {
                is = new URL(filespec).openStream();
            } else {
                String absPath = new File(filespec).getAbsolutePath();
                is = new FileInputStream(absPath);
            }
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return is;
    }

    /**
     * Returns an InputStream from a CLASSPATH resource
     */
    public static InputStream getResourceAsStream(String path)
        throws ProcessManagerException {
        return IoUtil.getResourceAsStream(path, null);
    }

    /**
     * Returns an InputStream from a file represented by a file or jar URL
     * or a "classpath:" specification.
     */
    public static InputStream getResourceAsStream(
        final String path,
        final Class<?> cls
    ) throws ProcessManagerException {
        assert (null != path);
        InputStream is;
        try {
            String _path = path;
            if (path.startsWith(CLASSPATH_PROTOCOL)) {
                _path = path.replace(
                    CLASSPATH_PROTOCOL, EMPTY_STR);
            }
            // use a class-specific classloader
            ClassLoader classLoader;
            if (null == cls) {
                classLoader = Thread.currentThread().getContextClassLoader();
            } else {
                classLoader = cls.getClassLoader();
            }
            is = classLoader.getResourceAsStream(_path);
            if (null == is) {
                throw new ProcessManagerException("unable to lookup InputStream for " + _path);
            }
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return is;
    }

    /**
     * Returns a String from a classpath resource
     */

    public static String getResourceAsString(String path) throws
        ProcessManagerException {
        return IoUtil.getResourceAsString(path, null);
    }

    /**
     * Returns a String from a classpath resource
     */
    public static String getResourceAsString(
        String path,
        Class<?> cls
    ) throws ProcessManagerException {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = IoUtil.getResourceAsStream(path, cls);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        } finally {
            closeReader(reader);
        }
        return sb.toString();
    }

    /**
     * Return a file system file as a String.
     *
     * @param filePath file system path
     */
    public static String readFileAsString(String filePath)
        throws ProcessManagerException {
        assert (null != filePath);
        BufferedInputStream bis = null;
        byte[] buffer = null;
        try {
            File f = new File(filePath);
            assert (f.exists() && f.canRead());
            String path = f.getAbsolutePath();
            f = new File(path);
            buffer = new byte[(int) f.length()];
            bis = new BufferedInputStream(new FileInputStream(f));
            int bytesRead = bis.read(buffer);
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        } finally {
            closeIs(bis);
        }
        return new String(buffer);
    }

    /**
     * @param is InputStream
     */
    public static void closeIs(InputStream is) {
        try {
            if (null != is) {
                is.close();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * @param reader
     */
    public static void closeReader(Reader reader) {
        try {
            if (null != reader) {
                reader.close();
            }
        } catch (IOException ignored) {
        }
    }
}
