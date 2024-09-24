package com.all.homedesire.resources.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropFeature {
	@JsonProperty("id")
	private long id;

	@JsonProperty("swimming_pool")
	private boolean swimingPool;

	@JsonProperty("gas_connection")
	private boolean gasConection;

	@JsonProperty("ro")
	private boolean ro;

	@JsonProperty("club_house")
	private boolean clubHouse;

	@JsonProperty("basketball_court")
	private boolean baketballCourt;

	@JsonProperty("tennis_court")
	private boolean tennisCourt;

	@JsonProperty("gym")
	private boolean gym;

	@JsonProperty("indoor_games")
	private boolean indoorGames;

	@JsonProperty("child_play_area")
	private boolean childPlayArea;

	@JsonProperty("hospital_near_by")
	private boolean hospitalNearBy;

	@JsonProperty("mall_near_by")
	private boolean mallNearBy;

	@JsonProperty("market_near_by")
	private boolean marketNearBy;

	@JsonProperty("schoold_near_by")
	private boolean schoolNearBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

}
