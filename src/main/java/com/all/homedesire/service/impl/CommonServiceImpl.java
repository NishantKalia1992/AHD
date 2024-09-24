package com.all.homedesire.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.all.homedesire.common.RestValidation;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.repository.LeadRepository;
import com.all.homedesire.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	LeadRepository leadRepository;

	@Override
	public Page<Lead> findAllPost(String type, String purpose, String state, String city, String area, double price, String keyboardSearch,
			String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		boolean isType = RestValidation.isTextData(type);
		boolean isPurpose = RestValidation.isTextData(purpose);
		boolean isState = RestValidation.isTextData(state);
		boolean isCity = RestValidation.isTextData(city);
		boolean isArea = RestValidation.isTextData(area);
		boolean isBudget = RestValidation.isDoubleData(price);
		isArea = (isArea && isCity && isState) ? true : false;
		isCity = (isCity && isState) ? true : false;

		if (isPurpose && isType && isState && isCity && isArea && isBudget) {
			page = postByPurposeTypeStateCityAreaBudget(type, purpose, state, city, area, price, orderBy, pageable);
		} else if (isPurpose && isType && isState && isCity && isArea) {
			page = postByPurposeTypeStateCityArea(type, purpose, state, city, area, orderBy, pageable);
		} else if (isPurpose && isState && isCity && isArea && isBudget) {
			page = postByPurposeStateCityAreaBudget(purpose, state, city, area, price, orderBy, pageable);
		} else if (isType && isState && isCity && isArea && isBudget) {
			page = postByTypeStateCityAreaBudget(type, state, city, area, price, orderBy, pageable);
		} else if (isPurpose && isType && isState && isCity) {
			page = postByPurposeTypeStateCity(type, purpose, state, city, orderBy, pageable);
		} else if (isType && isState && isCity && isArea) {
			page = postByTypeStateCityArea(type, state, city, area, orderBy, pageable);
		} else if (isPurpose && isState && isCity && isArea) {
			page = postByPurposeStateCityArea(purpose, state, city, area, orderBy, pageable);
		} else if (isState && isCity && isArea && isBudget) {
			page = postByStateCityAreaBudget(state, city, area, price, orderBy, pageable);
		} else if (isPurpose && isType && isState) {
			page = postByPurposeTypeState(type, purpose, state, orderBy, pageable);
		} else if (isPurpose && isState && isCity) {
			page = postByPurposeStateCity(purpose, state, city, orderBy, pageable);
		} else if (isType && isState && isCity) {
			page = postByTypeStateCity(type, state, city, orderBy, pageable);
		} else if (isState && isCity && isArea) {
			page = postByStateCityArea(state, city, area, orderBy, pageable);
		} else if (isCity && isArea && isBudget) {
			page = postByCityAreaBudget(city, area, price, orderBy, pageable);
		} else if (isPurpose && isType) {
			page = postByPurposeType(type, purpose, orderBy, pageable);
		} else if (isPurpose && isState) {
			page = postByPurposeState(purpose, state, orderBy, pageable);
		} else if (isType && isState) {
			page = postByTypeState(type, state, orderBy, pageable);
		} else if (isState && isCity) {
			page = postByStateCity(state, city, orderBy, pageable);
		} else if (isCity && isArea) {
			page = postByCityArea(city, area, orderBy, pageable);
		} else if (isArea && isBudget) {
			page = postByAreaBudget(area, price, orderBy, pageable);
		} else if (isPurpose) {
			page = postByPurpose(purpose, orderBy, pageable);
		} else if (isType && isState && isCity && isArea && isBudget) {
			page = postByType(type, orderBy, pageable);
		} else if (isState) {
			page = postByState(state, orderBy, pageable);
		} else if (isCity) {
			page = postByCity(city, orderBy, pageable);
		} else if (isArea) {
			page = postByArea(area, orderBy, pageable);
		} else if (isBudget) {
			page = postByBudget(price, orderBy, pageable);
		} else {
			page = postAll(orderBy, pageable);
		}

		return page;
	}

	@Override
	public Page<Lead> findAllPostByStateCityArea(String state, String city, String area, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;

		boolean isState = RestValidation.isTextData(state);
		boolean isCity = RestValidation.isTextData(city);
		boolean isArea = RestValidation.isTextData(area);
		isArea = (isArea && isCity && isState) ? true : false;
		isCity = (isCity && isState) ? true : false;

		if (isState && isCity && isArea) {
			page = postByStateCityArea(state, city, area, orderBy, pageable);
		} else if (isState && isCity) {
			page = postByStateCity(state, city, orderBy, pageable);
		} else if (isCity && isArea) {
			page = postByCityArea(city, area, orderBy, pageable);
		} else if (isState) {
			page = postByState(state, orderBy, pageable);
		} else if (isCity) {
			page = postByCity(city, orderBy, pageable);
		} else if (isArea) {
			page = postByArea(area, orderBy, pageable);
		} else {
			page = postAll(orderBy, pageable);
		}

		return page;
	}

	@Override
	public Page<Lead> findAllPostByTextAddressStatusTypePrice(String searchText, String address, String status,
			String type, double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;

		boolean isText = RestValidation.isTextData(searchText);
		boolean isAddress = RestValidation.isTextData(address);
		boolean isStatus = RestValidation.isTextData(status);
		boolean isType = RestValidation.isTextData(type);
		boolean isBudget = RestValidation.isDoubleData(price);
		// searchText, type, address, status, price,
		if (isText && isType && isAddress && isStatus && isBudget) {
			page = postByTextTypeAddressStatusBudget(searchText, type, address, status, price, orderBy, pageable);
		} else if (isText && isType && isAddress && isStatus) {
			page = postByTextTypeAddressStatus(searchText, type, address, status, orderBy, pageable);
		} else if (isText && isAddress && isStatus && isBudget) {
			page = postByTextAddressStatusBudget(searchText, address, status, price, orderBy, pageable);
		} else if (isType && isAddress && isStatus && isBudget) {
			page = postByTypeAddressStatusBudget(type, address, status, price, orderBy, pageable);
		} else if (isType && isAddress && isStatus) {
			page = postByTypeAddressStatus(type, address, status, orderBy, pageable);
		} else if (isText && isAddress && isStatus) {
			page = postByTextAddressStatus(searchText, address, status, orderBy, pageable);
		} else if (isAddress && isStatus && isBudget) {
			page = postByAddressStatusBudget(address, status, price, orderBy, pageable);
		} else if (isText && isType && isAddress) {
			page = postByTextTypeAddress(searchText, type, address, orderBy, pageable);
		} else if (isAddress && isStatus) {
			page = postByAddressStatus(address, status, orderBy, pageable);
		} else if (isStatus && isBudget) {
			page = postByStatusBudget(status, price, orderBy, pageable);
		} else if (isText && isType) {
			page = postByTextType(searchText, type, orderBy, pageable);
		} else if (isText && isAddress) {
			page = postByTextAddress(searchText, address, orderBy, pageable);
		} else if (isType && isAddress) {
			page = postByTypeAddress(type, address, orderBy, pageable);
		} else if (isStatus) {
			page = postByStatus(status, orderBy, pageable);
		} else if (isBudget) {
			page = postByBudget(price, orderBy, pageable);
		} else if (isText) {
			page = postByText(searchText, orderBy, pageable);
		} else if (isAddress) {
			page = postByAddress(address, orderBy, pageable);
		} else {
			page = postAll(orderBy, pageable);
		}

		return page;
	}
	// -------------------LEAD-------------------------------------

	@Override
	public Page<Lead> postByPurposeTypeStateCityAreaBudget(String type, String purpose, String state, String city,
			String area, double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypePurposeStateCityAreaBudgetASC(type, purpose, state, city, area, price,
					pageable);
		} else {
			page = leadRepository.findAllByTypePurposeStateCityAreaBudgetDESC(type, purpose, state, city, area, price,
					pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeTypeStateCityArea(String type, String purpose, String state, String city,
			String area, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypePurposeStateCityAreaASC(type, purpose, state, city, area, pageable);
		} else {
			page = leadRepository.findAllByTypePurposeStateCityAreaDESC(type, purpose, state, city, area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeTypeStateCity(String type, String purpose, String state, String city, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypePurposeStateCityASC(type, purpose, state, city, pageable);
		} else {
			page = leadRepository.findAllByTypePurposeStateCityDESC(type, purpose, state, city, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeTypeState(String type, String purpose, String state, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypePurposeStateASC(type, purpose, state, pageable);
		} else {
			page = leadRepository.findAllByTypePurposeStateDESC(type, purpose, state, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeType(String type, String purpose, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypePurposeASC(type, purpose, pageable);
		} else {
			page = leadRepository.findAllByTypePurposeDESC(type, purpose, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeStateCityAreaBudget(String purpose, String state, String city, String area,
			double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByPurposeStateCityAreaBudgetASC(purpose, state, city, area, price, pageable);
		} else {
			page = leadRepository.findAllByPurposeStateCityAreaBudgetDESC(purpose, state, city, area, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeStateCityArea(String purpose, String state, String city, String area, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByPurposeStateCityAreaASC(purpose, state, city, area, pageable);
		} else {
			page = leadRepository.findAllByPurposeStateCityAreaDESC(purpose, state, city, area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeStateCity(String purpose, String state, String city, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByPurposeStateCityASC(purpose, state, city, pageable);
		} else {
			page = leadRepository.findAllByPurposeStateCityDESC(purpose, state, city, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurposeState(String purpose, String state, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByPurposeStateASC(purpose, state, pageable);
		} else {
			page = leadRepository.findAllByPurposeStateDESC(purpose, state, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeStateCityAreaBudget(String type, String state, String city, String area, double price,
			String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeStateCityAreaBudgetASC(type, state, city, area, price, pageable);
		} else {
			page = leadRepository.findAllByTypeStateCityAreaBudgetDESC(type, state, city, area, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeStateCityArea(String type, String state, String city, String area, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeStateCityAreaASC(type, state, city, area, pageable);
		} else {
			page = leadRepository.findAllByTypeStateCityAreaDESC(type, state, city, area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeStateCity(String type, String state, String city, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeStateCityASC(type, state, city, pageable);
		} else {
			page = leadRepository.findAllByTypeStateCityDESC(type, state, city, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeState(String type, String state, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeStateASC(type, state, pageable);
		} else {
			page = leadRepository.findAllByTypeStateDESC(type, state, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByStateCityAreaBudget(String state, String city, String area, double price, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStateCityAreaBudgetASC(state, city, area, price, pageable);
		} else {
			page = leadRepository.findAllByStateCityAreaBudgetDESC(state, city, area, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByStateCityArea(String state, String city, String area, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStateCityAreaASC(state, city, area, pageable);
		} else {
			page = leadRepository.findAllByStateCityAreaDESC(state, city, area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByStateCity(String state, String city, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStateCityASC(state, city, pageable);
		} else {
			page = leadRepository.findAllByStateCityDESC(state, city, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByCityAreaBudget(String city, String area, double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByCityAreaBudgetASC(city, area, price, pageable);
		} else {
			page = leadRepository.findAllByCityAreaBudgetDESC(city, area, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByCityArea(String city, String area, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByCityAreaASC(city, area, pageable);
		} else {
			page = leadRepository.findAllByCityAreaDESC(city, area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByAreaBudget(String area, double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByAreaBudgetASC(area, price, pageable);
		} else {
			page = leadRepository.findAllByAreaBudgetDESC(area, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByPurpose(String purpose, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByPurposeASC(purpose, pageable);
		} else {
			page = leadRepository.findAllByPurposeDESC(purpose, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByType(String type, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeASC(type, pageable);
		} else {
			page = leadRepository.findAllByTypeDESC(type, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByState(String state, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStateASC(state, pageable);
		} else {
			page = leadRepository.findAllByStateDESC(state, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByCity(String city, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByCityASC(city, pageable);
		} else {
			page = leadRepository.findAllByCityDESC(city, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByArea(String area, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByAreaASC(area, pageable);
		} else {
			page = leadRepository.findAllByAreaDESC(area, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByBudget(double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByBudgetASC(price, pageable);
		} else {
			page = leadRepository.findAllByBudgetDESC(price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postAll(String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllASC(pageable);
		} else {
			page = leadRepository.findAllDESC(pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextTypeAddressStatusBudget(String searchText, String type, String address, String status,
			double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextTypeAddressStatusBudgetASC(searchText, type, address, status, price,
					pageable);
		} else {
			page = leadRepository.findAllByTextTypeAddressStatusBudgetDESC(searchText, type, address, status, price,
					pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextTypeAddressStatus(String searchText, String type, String address, String status,
			String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextTypeAddressStatusASC(searchText, type, address, status, pageable);
		} else {
			page = leadRepository.findAllByTextTypeAddressStatusDESC(searchText, type, address, status, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextAddressStatusBudget(String searchText, String address, String status, double price,
			String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextAddressStatusBudgetASC(searchText, address, status, price, pageable);
		} else {
			page = leadRepository.findAllByTextAddressStatusBudgetDESC(searchText, address, status, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeAddressStatusBudget(String type, String address, String status, double price,
			String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeAddressStatusBudgetASC(type, address, status, price, pageable);
		} else {
			page = leadRepository.findAllByTypeAddressStatusBudgetDESC(type, address, status, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeAddressStatus(String type, String address, String status, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeAddressStatusASC(type, address, status, pageable);
		} else {
			page = leadRepository.findAllByTypeAddressStatusDESC(type, address, status, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextAddressStatus(String searchText, String address, String status, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextAddressStatusASC(searchText, address, status, pageable);
		} else {
			page = leadRepository.findAllByTextAddressStatusDESC(searchText, address, status, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByAddressStatusBudget(String address, String status, double price, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByAddressStatusBudgetASC(address, status, price, pageable);
		} else {
			page = leadRepository.findAllByAddressStatusBudgetDESC(address, status, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextTypeAddress(String searchText, String type, String address, String orderBy,
			Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextTypeAddressASC(searchText, type, address, pageable);
		} else {
			page = leadRepository.findAllByTextTypeAddressDESC(searchText, type, address, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByAddressStatus(String address, String status, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByAddressStatusASC(address, status, pageable);
		} else {
			page = leadRepository.findAllByAddressStatusDESC(address, status, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByStatusBudget(String status, double price, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStatusBudgetASC(status, price, pageable);
		} else {
			page = leadRepository.findAllByStatusBudgetDESC(status, price, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextType(String searchText, String type, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextTypeASC(searchText, type, pageable);
		} else {
			page = leadRepository.findAllByTextTypeDESC(searchText, type, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTextAddress(String searchText, String address, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextAddressASC(searchText, address, pageable);
		} else {
			page = leadRepository.findAllByTextAddressDESC(searchText, address, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByTypeAddress(String type, String address, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTypeAddressASC(type, address, pageable);
		} else {
			page = leadRepository.findAllByTypeAddressDESC(type, address, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByStatus(String status, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByStatusASC(status, pageable);
		} else {
			page = leadRepository.findAllByStatusDESC(status, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByText(String searchText, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByTextASC(searchText, pageable);
		} else {
			page = leadRepository.findAllByTextDESC(searchText, pageable);
		}
		return page;
	}

	@Override
	public Page<Lead> postByAddress(String address, String orderBy, Pageable pageable) {
		Page<Lead> page = null;
		if (orderBy.equalsIgnoreCase("ASC")) {
			page = leadRepository.findAllByAddressASC(address, pageable);
		} else {
			page = leadRepository.findAllByAddressDESC(address, pageable);
		}
		return page;
	}
}
