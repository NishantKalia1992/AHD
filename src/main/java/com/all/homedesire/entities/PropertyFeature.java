package com.all.homedesire.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "property_features")
public class PropertyFeature extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5781699928700564687L;

	@Column(name = "SWIMMING_POOL")
	@JsonProperty("swimming_pool")
	private boolean swimingPool;

	@Column(name = "GAS_CONNECTION")
	@JsonProperty("gas_connection")
	private boolean gasConection;

	@Column(name = "RO")
	@JsonProperty("ro")
	private boolean ro;

	@Column(name = "CLUB_HOUSE")
	@JsonProperty("club_house")
	private boolean clubHouse;

	@Column(name = "BASKETBALL_COURT")
	@JsonProperty("basketball_court")
	private boolean baketballCourt;

	@Column(name = "TENNIS_COURT")
	@JsonProperty("tennis_court")
	private boolean tennisCourt;

	@Column(name = "GYM")
	@JsonProperty("gym")
	private boolean gym;

	@Column(name = "INDOOR_GAMES")
	@JsonProperty("indoor_games")
	private boolean indoorGames;

	@Column(name = "CHILD_PLAY_AREA")
	@JsonProperty("child_play_area")
	private boolean childPlayArea;

	@Column(name = "HOSPITAL_NEAR_BY")
	@JsonProperty("hospital_near_by")
	private boolean hospitalNearBy;

	@Column(name = "MALL_NEAR_BY")
	@JsonProperty("mall_near_by")
	private boolean mallNearBy;

	@Column(name = "MARKET_NEAR_BY")
	@JsonProperty("market_near_by")
	private boolean marketNearBy;

	@Column(name = "SCHOOL_NEAR_BY")
	@JsonProperty("schoold_near_by")
	private boolean schoolNearBy;

	@OneToMany(mappedBy = "propertyFeature", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Property> properties;

	public boolean isSwimingPool() {
		return swimingPool;
	}

	public void setSwimingPool(boolean swimingPool) {
		this.swimingPool = swimingPool;
	}

	public boolean isGasConection() {
		return gasConection;
	}

	public void setGasConection(boolean gasConection) {
		this.gasConection = gasConection;
	}

	public boolean isRo() {
		return ro;
	}

	public void setRo(boolean ro) {
		this.ro = ro;
	}

	public boolean isClubHouse() {
		return clubHouse;
	}

	public void setClubHouse(boolean clubHouse) {
		this.clubHouse = clubHouse;
	}

	public boolean isBaketballCourt() {
		return baketballCourt;
	}

	public void setBaketballCourt(boolean baketballCourt) {
		this.baketballCourt = baketballCourt;
	}

	public boolean isTennisCourt() {
		return tennisCourt;
	}

	public void setTennisCourt(boolean tennisCourt) {
		this.tennisCourt = tennisCourt;
	}

	public boolean isGym() {
		return gym;
	}

	public void setGym(boolean gym) {
		this.gym = gym;
	}

	public boolean isIndoorGames() {
		return indoorGames;
	}

	public void setIndoorGames(boolean indoorGames) {
		this.indoorGames = indoorGames;
	}

	public boolean isChildPlayArea() {
		return childPlayArea;
	}

	public void setChildPlayArea(boolean childPlayArea) {
		this.childPlayArea = childPlayArea;
	}

	public boolean isHospitalNearBy() {
		return hospitalNearBy;
	}

	public void setHospitalNearBy(boolean hospitalNearBy) {
		this.hospitalNearBy = hospitalNearBy;
	}

	public boolean isMallNearBy() {
		return mallNearBy;
	}

	public void setMallNearBy(boolean mallNearBy) {
		this.mallNearBy = mallNearBy;
	}

	public boolean isMarketNearBy() {
		return marketNearBy;
	}

	public void setMarketNearBy(boolean marketNearBy) {
		this.marketNearBy = marketNearBy;
	}

	public boolean isSchoolNearBy() {
		return schoolNearBy;
	}

	public void setSchoolNearBy(boolean schoolNearBy) {
		this.schoolNearBy = schoolNearBy;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
