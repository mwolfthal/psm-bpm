package gov.medicaid.processmanager;

import gov.medicaid.processmanager.exceptions.ProcessManagerException;
import gov.medicaid.processmanager.util.Reflector;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.util.logging.Level.ALL;

/**
 * @author Marvin Wolfthal
 * @since 7/4/2018
 */
public class EnrollmentProcessManager {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public EnrollmentProcessManager() {
    }

    public void executeProcess(EnrollmentProcess enrollmentProcess)
        throws ProcessManagerException {
        try {
            assert (null != enrollmentProcess);
            logger.fine("got EnrollmentProcess, processId = " +
                enrollmentProcess.getProcessId());
            Task task = enrollmentProcess.getTask();
            assert (null != task);
            String taskName = task.getName();
            assert (null != taskName);

            logger.fine("got task " + taskName);

            Object data = enrollmentProcess.enrollmentData;

            logger.fine("calling executeTask");
            executeTask(task, data);
            TaskResult taskResult = task.getResult();
            assert (null != taskResult);
            EnrollmentProcessResult enrollmentProcessResult =
                enrollmentProcess.getResult();
            assert (null != enrollmentProcessResult);
            enrollmentProcessResult.setStatus(taskResult.getStatus());
            enrollmentProcessResult.getMessages().addAll(taskResult.getMessages());
            logger.fine("EnrollmentProcess status is " +
                enrollmentProcess.getResult().getStatus().name());
        } catch (Exception e) {
            throw new ProcessManagerException(e);
        }
    }

    private void executeTask(Task task, Object data) throws ProcessManagerException {
        EnrollmentProcessExecutor enrollmentProcessExecutor =
            new EnrollmentProcessExecutor();
        String startMethod = task.getStartMethod();
        assert (null != startMethod && 0 != startMethod.length());
        String taskName = task.getName();
        assert (null != taskName);
        logger.fine("executing Task " + taskName +
            ", startMethod = " + startMethod);
        if (null == data) {
            Reflector.invoke(enrollmentProcessExecutor, startMethod, Void.class, task);
        } else {
            Reflector.invoke(enrollmentProcessExecutor, startMethod, Void.class, task, data);
        }
        TaskResult taskResult = task.getResult();
        assert (null != taskResult);
        ResultStatus resultStatus = taskResult.getStatus();
        logger.fine("startMethod result status is " + resultStatus.name());
        String nextMethod;
        while (ResultStatus.SUCCESS == (resultStatus = taskResult.getStatus()) &&
            null != (nextMethod = task.getNextMethod())) {
            logger.fine("executing Task " + taskName +
                ", nextMethod = " + nextMethod);
            if (null == data) {
                Reflector.invoke(enrollmentProcessExecutor, nextMethod, Void.class, task);
            } else {
                Reflector.invoke(enrollmentProcessExecutor, nextMethod, Void.class, task, data);
            }
            resultStatus = taskResult.getStatus();
            logger.fine("nextMethod result status is " + resultStatus.name());
        }
        logger.fine("no more methods in task");
        logger.fine("task completed with status " + resultStatus.name());
    }
}
