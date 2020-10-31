package org.upgrad.upstac.testrequests.consultation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.users.User;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service@Validated
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    private static Logger logger = LoggerFactory.getLogger(ConsultationService.class);

    // This method allows doctor to assign the requested to himself.
    @Transactional
    public Consultation assignForConsultation(TestRequest testRequest, User doctor) {

        // Set doctor details:
        Consultation consultation = new Consultation();
        consultation.setUpdatedOn(LocalDate.now());
        consultation.setDoctor(doctor);

        // Set testRequests details:
        consultation.setRequest(testRequest);
        consultation.setId(testRequest.getRequestId());

        return consultationRepository.save(consultation); // Save the consultation in the DB.

    }

    // This method works with the update consultation tab for the requested consultations to the doctors.
    public Consultation updateConsultation(TestRequest testRequest, CreateConsultationRequest createConsultationRequest) {

        Consultation consultation = testRequest.getConsultation();
        // Creating the consultation Result of the Doctor.
        consultation.setSuggestion(createConsultationRequest.getSuggestion());
        consultation.setComments(createConsultationRequest.getComments());
        consultation.setUpdatedOn(LocalDate.now());

        // Updating the labresult with Doctor's Suggestion in the DB.
        LabResult labResult = testRequest.getLabResult();
        labResult.setComments(consultation.getComments());

        return consultationRepository.save(consultation);
    }
}