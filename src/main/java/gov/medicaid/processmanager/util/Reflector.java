package gov.medicaid.processmanager.util;

import gov.medicaid.processmanager.exceptions.ProcessManagerException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * N.B.: primitive arguments must be boxed. See {@code invoke()}.
 */
public class Reflector {
    private Reflector() {
    }

    /**
     * Invoke a method on a supplied instance, which may not be null.
     * The method may not be static.
     *
     * @param targetInstance may not be null
     * @param methodName     may not be the name of a static method
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(
            final Object targetInstance,
            final String methodName,
            final Class<T> resultClass,
            final Object... args
    ) throws ProcessManagerException {
        if (null == targetInstance) {
            throw new ProcessManagerException("null targetInstance");
        }
        T result;
        try {
            Class<?> targetClass = targetInstance.getClass();
            Method method = ClassUtil.getMethod(targetClass,
                    methodName, resultClass, args);
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                throw new Exception(methodName + " is a static method");
            }
            result = invoke(targetInstance, method, resultClass, args);
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return result;
    }

    /**
     * If <code>targetInstance</code> is null <code>method</code>
     * must be a static method.
     * <p/>
     * This method will fail on methods whose signatures include
     * primitive argument types because the autoboxing changes the
     * signature. That is,
     * when invoking {@code int add(int x, int y)}
     * {@code method.invoke(targetInstance,{1,2}} will be called as
     * {@code add(Integer x, Integer y)}, which doesn't exist.
     *
     * @param targetInstance if null, the method must be static
     * @return - Result of the method call
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(
            final Object targetInstance,
            final Method method,
            final Class<T> resultClass,
            final Object... args
    ) throws ProcessManagerException {
        T result;
        try {
            assert (null != method);
            Object resultObj = method.invoke(targetInstance, args);
            Class _resultClass = (resultClass == null || resultClass == void.class) ?
                    Void.class : resultClass;
            if (_resultClass == Void.class && null != resultObj) {
                throw new Exception("expected null result, got " + resultObj.getClass());
            } else {
                if (null != resultObj && !_resultClass.isAssignableFrom(resultObj.getClass())) {
                    throw new Exception("unexpected result type: " + resultObj.getClass().getName());
                }
                result = (T) _resultClass.cast(resultObj);
            }
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
        return result;
    }
}
