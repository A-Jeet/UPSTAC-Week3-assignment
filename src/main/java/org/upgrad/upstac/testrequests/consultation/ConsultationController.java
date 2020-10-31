package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);

    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;

    @Autowired
    TestRequestFlowService testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;

    // This method is used to get the list of test requests having status as 'LAB_TEST_COMPLETED'.
    @GetMapping("/in-queue")@PreAuthorize("hasAnyRole('DOCTOR')")
    public List < TestRequest > getForConsultations() {

        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);
    }

    // This method to return the list of test requests assigned to current doctor.
    @GetMapping@PreAuthorize("hasAnyRole('DOCTOR')")
    public List < TestRequest > getForDoctor() {

        User doctor = userLoggedInService.getLoggedInUser(); // Get the current logged in user.
        return testRequestQueryService.findByDoctor(doctor); // To get the list of requests.

    }

    // This method is used to assign a particular test request to the current doctor(logged in user).
    @PreAuthorize("hasAnyRole('DOCTOR')")@PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {
        try {
            User doctor = userLoggedInService.getLoggedInUser(); // Get the current logged in user.
            TestRequest testRequest = testRequestUpdateService.assignForConsultation(id, doctor); // Assign the particular id to the current user.
            return testRequest;

        } catch(AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

    // This method is used to update the result of the current test request id with test doctor comments.
    @PreAuthorize("hasAnyRole('DOCTOR')")@PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {

        try {
            User doctor = userLoggedInService.getLoggedInUser(); // Get the logged in user.
            TestRequest result = testRequestUpdateService.updateConsultation(id, testResult, doctor); // To update the current test request id with the testResult details by the current user.
            return result;

        } catch(ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch(AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

}