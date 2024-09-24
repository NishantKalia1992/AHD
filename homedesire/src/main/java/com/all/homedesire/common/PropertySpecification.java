package com.all.homedesire.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import com.all.homedesire.entities.Property;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PropertySpecification implements Specification<Property> {
	Logger LOGGER = LoggerFactory.getLogger(PropertySpecification.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5717558656043438122L;
	private SearchProperty filter;

	public PropertySpecification(SearchProperty filter) {
		super();
		this.filter = filter;
	}

	@Override
	public Predicate toPredicate(Root<Property> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate p = cb.disjunction();
		LOGGER.info("PropertySpecification >> filter.getKeywords() >> " + filter.getKeywords());
		if (filter.getKeywords() != null && filter.getKeywords().length() > 0) {
			LOGGER.info("PropertySpecification >> filter.getKeywords() >> 1>> " + filter.getKeywords());
			p.getExpressions().add(cb.like(root.get("name"), filter.getKeywords()));
			p.getExpressions().add(cb.or(cb.like(root.get("description"), filter.getKeywords())));
			p.getExpressions().add(cb.or(cb.like(root.get("landMark"), filter.getKeywords())));
		}
		/*
		 * if (filter.getAddress() != null && filter.getAddress().length() > 0) {
		 * p.getExpressions().add(cb.and(cb.like(root.get("address"),
		 * filter.getAddress()))); } if (filter.getPropertyType() != null) {
		 * p.getExpressions().add(cb.and(cb.equal(root.get("propertyType"),
		 * filter.getPropertyType()))); } if (filter.getPropertyPurpose() != null) {
		 * p.getExpressions().add(cb.and(cb.equal(root.get("propertyPurpose"),
		 * filter.getPropertyPurpose()))); } if (filter.getMinPrice() != null &&
		 * filter.getMinPrice() > 0) {
		 * p.getExpressions().add(cb.and(cb.greaterThanOrEqualTo(root.get("price"),
		 * filter.getMinPrice()))); } if (filter.getMaxPrice() != null &&
		 * filter.getMaxPrice() > 0) {
		 * p.getExpressions().add(cb.and(cb.lessThanOrEqualTo(root.get("price"),
		 * filter.getMaxPrice()))); }
		 */
		LOGGER.info("PropertySpecification >> p >> " + p.toString());
		return p;
	}

}
