package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "desire_emails")
public class DesireEmail extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "RECIPIENT")
	@JsonProperty("recipient")
	private String recipient;

	@Column(name = "MESSAGE_BODY", columnDefinition = "TEXT")
	@JsonProperty("message_body")
	private String messageBody;

	@Column(name = "SUBJECT")
	@JsonProperty("subject")
	private String subject;

	@Column(name = "ATTACHMENT")
	@JsonProperty("attachment")
	private String attachment;

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
