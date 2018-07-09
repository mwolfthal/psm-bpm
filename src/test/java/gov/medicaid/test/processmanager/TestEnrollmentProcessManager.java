package gov.medicaid.test.processmanager;

import gov.medicaid.processmanager.Constants;
import gov.medicaid.processmanager.Enrollment;
import gov.medicaid.processmanager.EnrollmentProcess;
import gov.medicaid.processmanager.EnrollmentProcessManager;
import gov.medicaid.processmanager.util.JAXBUnmarshaller;
import gov.medicaid.processmanager.ResultStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author Marvin Wolfthal
 * @since 7/4/2018
 */
public class TestEnrollmentProcessManager {
    private EnrollmentProcessManager enrollmentProcessManager =
        new EnrollmentProcessManager();
    private EnrollmentProcess enrollmentProcess;

    @Before
    public void setUp() throws Exception {
        try {
            System.setProperty( "java.util.logging.config.file",
                "src/test/resources/logging.properties" );
            LogManager logManager = LogManager.getLogManager();
            logManager.readConfiguration();
            JAXBUnmarshaller<EnrollmentProcess> jaxbUnmarshaller =
                new JAXBUnmarshaller<>(EnrollmentProcess.class);
            assertNotNull(jaxbUnmarshaller);
            enrollmentProcess = jaxbUnmarshaller.unmarshalResource(Constants.PROCESS_XML);
            assertNotNull(enrollmentProcess);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test
    public void testProcess() {
        int result = 1;
        try {
            enrollmentProcess.setProcessId(101);
            Map<String, Object> map = new HashMap<String, Object>();
            Enrollment enrollment = new Enrollment();
            enrollment.setProviderName("OTS");
            map.put(Constants.ENROLLMENT_INSTANCE, enrollment);
            enrollmentProcess.setEnrollmentData(map);
            enrollmentProcessManager.executeProcess(enrollmentProcess);
            assertSame(enrollmentProcess.getResult().getStatus(),
                ResultStatus.SUCCESS);
            result = 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assertEquals(result, 0);
    }
}
