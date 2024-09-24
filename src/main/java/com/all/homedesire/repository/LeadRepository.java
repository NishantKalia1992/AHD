package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.Property;

public interface LeadRepository extends JpaRepository<Lead, Long> {
	@Query("SELECT d FROM Lead d WHERE d.id =:id AND d.isActive=true AND d.isDeleted=false")
	public Optional<Lead> findByObjectId(@Param("id") long id);

	@Query("SELECT d FROM Lead d WHERE d.id =:id AND d.isDeleted=false")
	public Optional<Lead> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT d FROM Lead d WHERE d.isActive =true AND d.isDeleted =false Order By d.updatedOn ASC")
	public Page<Lead> findAllASC(Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.isActive =true AND d.isDeleted =false Order By d.updatedOn DESC")
	public Page<Lead> findAllDESC(Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.customer.id =:customerId AND d.isActive =true AND d.isDeleted =false Order By d.updatedOn ASC")
	public Page<Lead> findAllByCustomerASC(@Param("customerId") long customerId, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.customer.id =:customerId AND d.isActive =true AND d.isDeleted =false Order By d.updatedOn DESC")
	public Page<Lead> findAllByCustomerDESC(@Param("customerId") long customerId, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.id not in (:leadIds) AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllLeadASC(@Param("leadIds") List<Long> leadIds, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.id not in (:leadIds) AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllLeadDESC(@Param("leadIds") List<Long> leadIds, Pageable pageable);
	
	@Query("SELECT d FROM Lead d WHERE d.id in (:leadIds) AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllFollowLeadASC(@Param("leadIds") List<Long> leadIds, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.id in (:leadIds) AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllFollowLeadDESC(@Param("leadIds") List<Long> leadIds, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property != null AND d.property.isActive=true AND d.property.isDeleted=false ORDER BY d.property.updatedOn ASC")
	public Page<Lead> findAllPropertyASC(Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property != null AND d.property.isActive=true AND d.property.isDeleted=false ORDER BY d.property.updatedOn DESC")
	public Page<Lead> findAllPropertyDESC(Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.customer.id =:customerId AND d.property != null AND d.property.isActive =true AND d.property.isDeleted =false Order By d.property.updatedOn ASC")
	public Page<Lead> findAllPropertyByCustomerASC(@Param("customerId") long customerId, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.customer.id =:customerId AND d.property != null AND d.property.isActive =true AND d.property.isDeleted =false Order By d.property.updatedOn DESC")
	public Page<Lead> findAllPropertyByCustomerDESC(@Param("customerId") long customerId, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property != null AND d.property.isActive=true AND d.property.isDeleted=false ORDER BY d.property.updatedOn desc")
	public List<Lead> listLatestProperties();

	@Query("SELECT d FROM Lead d WHERE d.property != null AND d.property in (:properties) AND d.property.isActive=true AND d.property.isDeleted=false ORDER BY d.property.updatedOn ASC")
	public Page<Lead> findAllByPropertiesASC(@Param("properties") List<Property> properties, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property != null AND d.property in (:properties) AND d.property.isActive=true AND d.property.isDeleted=false ORDER BY d.property.updatedOn DESC")
	public Page<Lead> findAllByPropertiesDESC(@Param("properties") List<Property> properties, Pageable pageable);

	// ASC
	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypePurposeStateCityAreaBudgetASC(@Param("type") String type,
			@Param("purpose") String purpose, @Param("state") String state, @Param("city") String city,
			@Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypePurposeStateCityAreaASC(@Param("type") String type, @Param("purpose") String purpose,
			@Param("state") String state, @Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypePurposeStateCityASC(@Param("type") String type, @Param("purpose") String purpose,
			@Param("state") String state, @Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypePurposeStateASC(@Param("type") String type, @Param("purpose") String purpose,
			@Param("state") String state, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypePurposeASC(@Param("type") String type, @Param("purpose") String purpose,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByPurposeStateCityAreaBudgetASC(@Param("purpose") String purpose,
			@Param("state") String state, @Param("city") String city, @Param("area") String area,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByPurposeStateCityAreaASC(@Param("purpose") String purpose, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByPurposeStateCityASC(@Param("purpose") String purpose, @Param("state") String state,
			@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByPurposeStateASC(@Param("purpose") String purpose, @Param("state") String state,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeStateCityAreaBudgetASC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeStateCityAreaASC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeStateCityASC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeStateASC(@Param("type") String type, @Param("state") String state,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStateCityAreaBudgetASC(@Param("state") String state, @Param("city") String city,
			@Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStateCityAreaASC(@Param("state") String state, @Param("city") String city,
			@Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStateCityASC(@Param("state") String state, @Param("city") String city,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByCityAreaBudgetASC(@Param("city") String city, @Param("area") String area,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByCityAreaASC(@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByAreaBudgetASC(@Param("area") String area, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeASC(@Param("type") String type, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByPurposeASC(@Param("purpose") String purpose, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStateASC(@Param("state") String state, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByCityASC(@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByAreaASC(@Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByBudgetASC(@Param("price") double price, Pageable pageable);

	// DESC
	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypePurposeStateCityAreaBudgetDESC(@Param("type") String type,
			@Param("purpose") String purpose, @Param("state") String state, @Param("city") String city,
			@Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypePurposeStateCityAreaDESC(@Param("type") String type,
			@Param("purpose") String purpose, @Param("state") String state, @Param("city") String city,
			@Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypePurposeStateCityDESC(@Param("type") String type, @Param("purpose") String purpose,
			@Param("state") String state, @Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypePurposeStateDESC(@Param("type") String type, @Param("purpose") String purpose,
			@Param("state") String state, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.propertyPurpose.name=:purpose AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypePurposeDESC(@Param("type") String type, @Param("purpose") String purpose,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByPurposeStateCityAreaBudgetDESC(@Param("purpose") String purpose,
			@Param("state") String state, @Param("city") String city, @Param("area") String area,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByPurposeStateCityAreaDESC(@Param("purpose") String purpose, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByPurposeStateCityDESC(@Param("purpose") String purpose, @Param("state") String state,
			@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByPurposeStateDESC(@Param("purpose") String purpose, @Param("state") String state,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeStateCityAreaBudgetDESC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeStateCityAreaDESC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeStateCityDESC(@Param("type") String type, @Param("state") String state,
			@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeStateDESC(@Param("type") String type, @Param("state") String state,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStateCityAreaBudgetDESC(@Param("state") String state, @Param("city") String city,
			@Param("area") String area, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStateCityAreaDESC(@Param("state") String state, @Param("city") String city,
			@Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStateCityDESC(@Param("state") String state, @Param("city") String city,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByCityAreaBudgetDESC(@Param("city") String city, @Param("area") String area,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByCityAreaDESC(@Param("city") String city, @Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.name=:area AND d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByAreaBudgetDESC(@Param("area") String area, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeDESC(@Param("type") String type, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertyPurpose.name=:purpose AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByPurposeDESC(@Param("purpose") String purpose, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.state.name=:state AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStateDESC(@Param("state") String state, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.city.name=:city AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByCityDESC(@Param("city") String city, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.area.name=:area AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByAreaDESC(@Param("area") String area, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.price=:price AND d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByBudgetDESC(@Param("price") double price, Pageable pageable);

	// ASC for outer Lead
	// ---------------------------------------------------------------------
	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextTypeAddressStatusBudgetASC(@Param("text") String searchText,
			@Param("type") String type, @Param("address") String address, @Param("status") String status,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextTypeAddressStatusASC(@Param("text") String searchText, @Param("type") String type,
			@Param("address") String address, @Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextAddressStatusBudgetASC(@Param("text") String searchText,
			@Param("address") String address, @Param("status") String status, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeAddressStatusBudgetASC(@Param("type") String type, @Param("address") String address,
			@Param("status") String status, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeAddressStatusASC(@Param("type") String type, @Param("address") String address,
			@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextAddressStatusASC(@Param("text") String searchText, @Param("address") String address,
			@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByAddressStatusBudgetASC(@Param("address") String address, @Param("status") String status,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextTypeAddressASC(@Param("text") String searchText, @Param("type") String type,
			@Param("address") String address, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByAddressStatusASC(@Param("address") String address, @Param("status") String status,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property.propertyStatus.name=:status AND d.price=:price AND "
			+ "d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStatusBudgetASC(@Param("status") String status, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextTypeASC(@Param("text") String searchText, @Param("type") String type,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextAddressASC(@Param("text") String searchText, @Param("address") String address,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTypeAddressASC(@Param("type") String type, @Param("address") String address,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByStatusASC(@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByTextASC(@Param("text") String searchText, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn ASC")
	public Page<Lead> findAllByAddressASC(@Param("address") String address, Pageable pageable);

	// DESC for outer Lead --------
	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.property.isPublished = true AND d.isActive=true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextTypeAddressStatusBudgetDESC(@Param("text") String searchText,
			@Param("type") String type, @Param("address") String address, @Param("status") String status,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextTypeAddressStatusDESC(@Param("text") String searchText, @Param("type") String type,
			@Param("address") String address, @Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.property.isPublished = true AND d.isActive=true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextAddressStatusBudgetDESC(@Param("text") String searchText,
			@Param("address") String address, @Param("status") String status, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.property.isPublished = true AND d.isActive=true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeAddressStatusBudgetDESC(@Param("type") String type, @Param("address") String address,
			@Param("status") String status, @Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeAddressStatusDESC(@Param("type") String type, @Param("address") String address,
			@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextAddressStatusDESC(@Param("text") String searchText, @Param("address") String address,
			@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.price=:price AND d.property.isPublished = true AND d.isActive=true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByAddressStatusBudgetDESC(@Param("address") String address, @Param("status") String status,
			@Param("price") double price, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextTypeAddressDESC(@Param("text") String searchText, @Param("type") String type,
			@Param("address") String address, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND "
			+ "d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByAddressStatusDESC(@Param("address") String address, @Param("status") String status,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property.propertyStatus.name=:status AND d.price=:price AND d.property.isPublished = true AND "
			+ "d.isActive=true AND d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStatusBudgetDESC(@Param("status") String status, @Param("price") double price,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "d.propertySubType.propertyType.name=:type AND d.isActive=true AND d.property.isPublished = true AND d.isDeleted=false "
			+ "ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextTypeDESC(@Param("text") String searchText, @Param("type") String type,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextAddressDESC(@Param("text") String searchText, @Param("address") String address,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.propertySubType.propertyType.name=:type AND "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTypeAddressDESC(@Param("type") String type, @Param("address") String address,
			Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE d.property.propertyStatus.name=:status AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByStatusDESC(@Param("status") String status, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE (d.title like %:text% OR d.description like %:text%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByTextDESC(@Param("text") String searchText, Pageable pageable);

	@Query("SELECT d FROM Lead d WHERE "
			+ "(d.property.address like %:address% OR d.property.landMark like %:address%) AND d.isActive=true AND d.property.isPublished = true AND "
			+ "d.isDeleted=false ORDER BY d.updatedOn DESC")
	public Page<Lead> findAllByAddressDESC(@Param("address") String address, Pageable pageable);
}
