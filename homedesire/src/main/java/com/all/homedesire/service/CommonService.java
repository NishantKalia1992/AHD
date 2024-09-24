package com.all.homedesire.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.all.homedesire.entities.Lead;

public interface CommonService {

	public Page<Lead> findAllPost(String type, String purpose, String state, String city, String area, double price, String keyboardSearch,
			String orderBy, Pageable pageable);

	public Page<Lead> findAllPostByStateCityArea(String state, String city, String area, String orderBy,
			Pageable pageable);

	public Page<Lead> findAllPostByTextAddressStatusTypePrice(String searchText, String address, String status,
			String type, double price, String orderBy, Pageable pageable);

	public Page<Lead> postByPurposeTypeStateCityAreaBudget(String type, String purpose, String state, String city,
			String area, double price, String orderBy, Pageable pageable);

	public Page<Lead> postByPurposeTypeStateCityArea(String type, String purpose, String state, String city,
			String area, String orderBy, Pageable pageable);

	public Page<Lead> postByPurposeTypeStateCity(String type, String purpose, String state, String city, String orderBy,
			Pageable pageable);

	public Page<Lead> postByPurposeTypeState(String type, String purpose, String state, String orderBy,
			Pageable pageable);

	public Page<Lead> postByPurposeType(String type, String purpose, String orderBy, Pageable pageable);

	public Page<Lead> postByPurposeStateCityAreaBudget(String purpose, String state, String city, String area,
			double price, String orderBy, Pageable pageable);

	public Page<Lead> postByPurposeStateCityArea(String purpose, String state, String city, String area, String orderBy,
			Pageable pageable);

	public Page<Lead> postByPurposeStateCity(String purpose, String state, String city, String orderBy,
			Pageable pageable);

	public Page<Lead> postByPurposeState(String purpose, String state, String orderBy, Pageable pageable);

	public Page<Lead> postByTypeStateCityAreaBudget(String type, String state, String city, String area, double price,
			String orderBy, Pageable pageable);

	public Page<Lead> postByTypeStateCityArea(String type, String state, String city, String area, String orderBy,
			Pageable pageable);

	public Page<Lead> postByTypeStateCity(String type, String state, String city, String orderBy, Pageable pageable);

	public Page<Lead> postByTypeState(String type, String state, String orderBy, Pageable pageable);

	public Page<Lead> postByStateCityAreaBudget(String state, String city, String area, double price, String orderBy,
			Pageable pageable);

	public Page<Lead> postByStateCityArea(String state, String city, String area, String orderBy, Pageable pageable);

	public Page<Lead> postByStateCity(String state, String city, String orderBy, Pageable pageable);

	public Page<Lead> postByCityAreaBudget(String city, String area, double price, String orderBy, Pageable pageable);

	public Page<Lead> postByCityArea(String city, String area, String orderBy, Pageable pageable);

	public Page<Lead> postByAreaBudget(String area, double price, String orderBy, Pageable pageable);

	public Page<Lead> postByPurpose(String purpose, String orderBy, Pageable pageable);

	public Page<Lead> postByType(String type, String orderBy, Pageable pageable);

	public Page<Lead> postByState(String state, String orderBy, Pageable pageable);

	public Page<Lead> postByCity(String city, String orderBy, Pageable pageable);

	public Page<Lead> postByArea(String area, String orderBy, Pageable pageable);

	public Page<Lead> postByBudget(double price, String orderBy, Pageable pageable);

	public Page<Lead> postAll(String orderBy, Pageable pageable);

	public Page<Lead> postByTextTypeAddressStatusBudget(String searchText, String type, String address, String status,
			double price, String orderBy, Pageable pageable);

	public Page<Lead> postByTextTypeAddressStatus(String searchText, String type, String address, String status,
			String orderBy, Pageable pageable);

	public Page<Lead> postByTextAddressStatusBudget(String searchText, String address, String status, double price,
			String orderBy, Pageable pageable);

	public Page<Lead> postByTypeAddressStatusBudget(String type, String address, String status, double price,
			String orderBy, Pageable pageable);

	public Page<Lead> postByTypeAddressStatus(String type, String address, String status, String orderBy,
			Pageable pageable);

	public Page<Lead> postByTextAddressStatus(String searchText, String address, String status, String orderBy,
			Pageable pageable);

	public Page<Lead> postByAddressStatusBudget(String address, String status, double price, String orderBy,
			Pageable pageable);

	public Page<Lead> postByTextTypeAddress(String searchText, String type, String address, String orderBy,
			Pageable pageable);

	public Page<Lead> postByAddressStatus(String address, String status, String orderBy, Pageable pageable);

	public Page<Lead> postByStatusBudget(String status, double price, String orderBy, Pageable pageable);

	public Page<Lead> postByTextType(String searchText, String type, String orderBy, Pageable pageable);

	public Page<Lead> postByTextAddress(String searchText, String address, String orderBy, Pageable pageable);

	public Page<Lead> postByTypeAddress(String type, String address, String orderBy, Pageable pageable);

	public Page<Lead> postByStatus(String status, String orderBy, Pageable pageable);

	public Page<Lead> postByText(String searchText, String orderBy, Pageable pageable);

	public Page<Lead> postByAddress(String address, String orderBy, Pageable pageable);

}
