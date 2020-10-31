package org.upgrad.upstac.testrequests.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.users.User;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@Validated
public class LabResultService {

    @Autowired
    private LabResultRepository labResultRepository;

    private static Logger logger = LoggerFactory.getLogger(LabResultService.class);

    // This method is used to create the lab result module service.
    public LabResult createLabResult(User tester, TestRequest testRequest) {

        // This creates LabResult in the testrequest usecase.
        LabResult labResult = new LabResult();
        labResult.setTester(tester);
        labResult.setUpdatedOn(LocalDate.now());
        labResult.setRequest(testRequest);

        return saveLabResult(labResult); // Return the LabResult.

    }

    @Transactional
    LabResult saveLabResult(LabResult labResult) {
        return labResultRepository.save(labResult);
    }

    public LabResult assignForLabTest(TestRequest testRequest, User tester) {

        return createLabResult(tester, testRequest);

    }

    // This method is used to update the lab test
    public LabResult updateLabTest(TestRequest testRequest, CreateLabResult createLabResult) {

        // Setting all the parameters of the created object of LabResult class.
        LabResult labResult = testRequest.getLabResult();
        labResult.setBloodPressure(createLabResult.getBloodPressure());
        labResult.setComments(createLabResult.getComments());
        labResult.setHeartBeat(createLabResult.getHeartBeat());
        labResult.setOxygenLevel(createLabResult.getOxygenLevel());
        labResult.setTemperature(createLabResult.getTemperature());
        labResult.setResult(createLabResult.getResult());
        labResult.setUpdatedOn(LocalDate.now());

        saveLabResult(labResult); // Save the result.

        return labResult; // Return the updated object.

    }

}