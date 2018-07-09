package gov.medicaid.processmanager.util;

import gov.medicaid.processmanager.exceptions.ProcessManagerException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Marvin Wolfthal
 * @since 7/18/2011
 */
public class JAXBUnmarshaller<T> {
    private Class<T> tClass;
    private JAXBContext context;
    private Unmarshaller unmarshaller;

    public JAXBUnmarshaller(Class<T> tClass) throws ProcessManagerException {
        try {
            assert (null != tClass);
            this.tClass = tClass;
            context = JAXBContext.newInstance(tClass);
            unmarshaller = context.createUnmarshaller();
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T unmarshalResource(String path) throws ProcessManagerException {
        assert (null != path);
        T t = null;
        try {
            String xml = IoUtil.getResourceAsString(path);
            t = unmarshalString(xml);
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public T unmarshalFile(String path) throws ProcessManagerException {
        assert (null != path);
        T t = null;
        try {
            String xml = IoUtil.readFileAsString(path);
            t = unmarshalString(xml);
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public T unmarshalString(String xml) throws ProcessManagerException {
        T t = null;
        try {
            InputStream stream = new ByteArrayInputStream(xml.getBytes());
            t = (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(new StreamSource(stream), tClass));
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return t;
    }
}
