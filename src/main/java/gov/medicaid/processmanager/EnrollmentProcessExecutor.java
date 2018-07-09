package gov.medicaid.processmanager;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.util.logging.Level.ALL;
import static java.util.logging.Level.FINE;

/**
 * @author Marvin Wolfthal
 * @since 7/4/2018
 */
public class EnrollmentProcessExecutor {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void enroll(Task task, HashMap<String, Enrollment> data) {
        assert (null != task);
        Enrollment enrollment = data.get(Constants.ENROLLMENT_INSTANCE);
        String providerName = enrollment.getProviderName();
        assert (null != providerName);
        logger.log(FINE, "got Enrollment, ProviderName = " + providerName);
        task.getResult().setStatus(ResultStatus.SUCCESS);
        task.setNextMethod(Constants.METHOD_PERSIST);
    }

    public void persist(Task task, HashMap<String, Enrollment> data) {
        assert (null != task);
        task.getResult().setStatus(ResultStatus.SUCCESS);
        task.setNextMethod(null);
    }
}
