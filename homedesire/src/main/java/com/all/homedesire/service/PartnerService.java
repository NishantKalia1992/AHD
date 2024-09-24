package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.Partner;
import com.all.homedesire.resources.dto.DesireSearchRequest;

public interface PartnerService {
	public DesireStatus partners(String authToken, DesireSearchRequest request);
	public DesireStatus viewPartner(String authToken, long partnerId);
	public DesireStatus addPartner(String authToken, Partner partner);
	public DesireStatus editPartner(String authToken, Partner partner);
	public DesireStatus deletePartner(String authToken, long partnerId);

}
