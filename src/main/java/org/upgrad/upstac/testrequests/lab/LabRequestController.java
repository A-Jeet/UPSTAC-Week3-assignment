package org.upgrad.upstac.testrequests.lab;


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


@RestController@RequestMapping("/api/labrequests")
public class LabRequestController {

    Logger log = LoggerFactory.getLogger(org.upgrad.upstac.testrequests.lab.LabRequestController.class);

    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;@Autowired
    private TestRequestFlowService testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;

    // This method returns the list of test requests having status as 'INITIATED'
    @GetMapping("/to-be-tested")@PreAuthorize("hasAnyRole('TESTER')")
    public List < TestRequest > getForTests() {

        User tester = userLoggedInService.getLoggedInUser(); // Getting the user who is logged in the application.
        return testRequestQueryService.findBy(RequestStatus.INITIATED); // Finding the INITIATED requests and getting the payload.

    }

    // This method returns the list of test requests assigned to current tester.
    @GetMapping@PreAuthorize("hasAnyRole('TESTER')")
    public List < TestRequest > getForTester() {

        User tester = userLoggedInService.getLoggedInUser(); // Getting the user who is logged in the application.
        return testRequestQueryService.findByTester(tester); // Find all the testrequest objects with the user passed.

    }

    // This method is used to assign a particular test request to the current tester(logged in user).
    @PreAuthorize("hasAnyRole('TESTER')")@PutMapping("/assign/{id}")
    public TestRequest assignForLabTest(@PathVariable Long id) {

        try {
            User tester = userLoggedInService.getLoggedInUser(); // Getting the user who is logged in the application.
            TestRequest testRequest = testRequestUpdateService.assignForLabTest(id, tester); // This assigns the test to the current user.
            return testRequest;

        } catch(AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

    // This method is used to update the result of the current test request id with test results
    @PreAuthorize("hasAnyRole('TESTER')")@PutMapping("/update/{id}")
    public TestRequest updateLabTest(@PathVariable Long id, @RequestBody CreateLabResult createLabResult) {

        try {
            User tester = userLoggedInService.getLoggedInUser(); // Getting the user who is logged in the application.
            TestRequest testRequest = testRequestUpdateService.updateLabTest(id, createLabResult, tester); // This updates the current test request id with the createLabResult details by the current user.
            return testRequest;

        } catch(ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch(AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

}