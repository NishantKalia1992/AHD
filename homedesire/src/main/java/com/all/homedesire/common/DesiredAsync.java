package com.all.homedesire.common;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.all.homedesire.entities.DesireEmail;
import com.all.homedesire.entities.Property;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.repository.DesireEmailRepository;
import com.all.homedesire.repository.PropertyRepository;
import com.all.homedesire.repository.PropertyStatusRepository;
import com.all.homedesire.service.EmailService;

import io.jsonwebtoken.io.IOException;

@Transactional
@Component
public class DesiredAsync {

	Logger LOGGER = LoggerFactory.getLogger(DesiredAsync.class);

	@Autowired
	EmailService emailService;

	@Autowired
	DesireEmailRepository desireEmailRepository;
	
	@Autowired
	PropertyStatusRepository propertyStatusRepository;
	
	@Autowired
	PropertyRepository propertyRepository;

	@Scheduled(cron = "1 * * * * *")
	public void sendEmailInBackground() {
		// LOGGER.info("sendEmailInBackground >> Called!");
		try {
			ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
			emailExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						sendEmail();
					} catch (IOException e) {
						LOGGER.error("failed", e);
					}
				}
			});
			emailExecutor.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	@Transactional
	public void sendEmail() {
		// LOGGER.info("sendEmailInBackground >> Called!");
		try {

			List<DesireEmail> desireEmails = desireEmailRepository.findAllByActiveAndDeleted(true, false);
			//LOGGER.info("sendEmailInBackground >> total email found >> " + desireEmails.size());
			desireEmails.stream().forEach(email -> email.setActive(false));
			desireEmailRepository.saveAll(desireEmails);
			for (DesireEmail desireEmail : desireEmails) {
				try {
					boolean emailStatus = emailService.sendMailWithAttachment(desireEmail);
					if (emailStatus) {
						desireEmail.setActive(false);
						desireEmailRepository.save(desireEmail);
					}
				} catch (Exception e) {
					e.printStackTrace();
					desireEmail.setActive(false);
					desireEmailRepository.save(desireEmail);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	 @Scheduled(cron = "0 0 0 */15 * ?") 
	public void updatePropertyStatusInBackground() {
		// LOGGER.info("sendEmailInBackground >> Called!");
		try {
			ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
			emailExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						updatePropertyStatus();
					} catch (IOException e) {
						LOGGER.error("failed", e);
					}
				}
			});
			emailExecutor.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Async
	@Transactional

	public void updatePropertyStatus() {
	    LOGGER.info("Scheduled: updatePropertyStatus called!");

	    try {
	        // Fetch the property statuses with ID 5 (current "New" status) and ID 1 (target "Available" status)
	        Optional<PropertyStatus> currentStatusOpt = propertyStatusRepository.findById(5L);  // Status with ID 5
	        
	        Optional<PropertyStatus> changeStatusOpt = propertyStatusRepository.findById(1L);   // Status with ID 1

	        if (currentStatusOpt.isPresent() && changeStatusOpt.isPresent()) {
	            PropertyStatus changeStatus = changeStatusOpt.get();

	            // Find all properties with the current status ID 5
	            List<Property> propertiesWithCurrentStatus = propertyRepository.findByStatusId(5L);
	            LOGGER.info("Number of properties with status ID 5: " + propertiesWithCurrentStatus.size());

	            for (Property property : propertiesWithCurrentStatus) {
	               
	            	LOGGER.info("Property ID: " + property.getId() + " current status: " + property.getPropertyStatus().getId());
	            	
	                // Change the property status to ID 1 (Available)
	                property.setPropertyStatus(changeStatus);
	                property.setUpdatedOn(new Date());  // Set updated timestamp to current time

	                // Save the updated property
	                Property updatedProperty = propertyRepository.save(property);
	                LOGGER.info("Property with ID: " + updatedProperty.getId() + " status updated to ID 1 (Available).");
	            }
	        } else {
	            LOGGER.warn("Either the property status with ID 5 or ID 1 not found in the database.");
	        }
	    } catch (Exception e) {
	        LOGGER.error("Error during scheduled task updatePropertyStatus: ", e);
	    }
	}


}
