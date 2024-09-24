package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.Customer;
import com.all.homedesire.resources.dto.DesireSearchRequest;

public interface CustomerService {
	public DesireStatus customers(String authToken, DesireSearchRequest request);
	public DesireStatus viewCustomer(String authToken, long customerId);
	public DesireStatus addCustomer(String authToken, Customer customer);
	public DesireStatus editCustomer(String authToken, Customer customer);
	public DesireStatus deleteCustomer(String authToken, long customerId);

}
