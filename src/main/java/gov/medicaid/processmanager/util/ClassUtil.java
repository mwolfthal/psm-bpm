package gov.medicaid.processmanager.util;

import gov.medicaid.processmanager.exceptions.ProcessManagerException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marvin Wolfthal
 * @since 7/22/2011
 * Time: 12:12 AM
 */
public class ClassUtil {

    /**
     * Returns a list of ClassLoaders to search.
     */
    public static List<ClassLoader> getClassLoaderSearchOrder(
        Object caller
    ) {
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
        classLoaders.add(ClassLoader.getSystemClassLoader());
        classLoaders.add(Thread.currentThread().getContextClassLoader());
        if (caller != null) {
            classLoaders.add(caller.getClass().getClassLoader());
            classLoaders.add(caller.getClass().getProtectionDomain().getClassLoader());
        }
        return classLoaders;
    }

    public static <T> Method getMethod(
        Class<?> targetClass,
        String methodName,
        Class<T> resultClass,
        Object... args
    ) throws ProcessManagerException {
        Method method;
        try {
            assert (null != methodName);
            Class[] argClasses = null;
            if (null != args) {
                argClasses = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argClasses[i] = args[i].getClass();
                }
            }
            method = targetClass.getMethod(methodName, argClasses);
            Class<?> returnType = method.getReturnType();
            if ((returnType == void.class ||
                returnType == Void.class) &&
                (null != resultClass &&
                    void.class != resultClass &&
                    Void.class != resultClass)) {
                throw new ProcessManagerException("method " + method.getName() +
                    " does not have void return type ");
            }
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return method;
    }
}
