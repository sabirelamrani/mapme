package com.db4o.android;

import android.location.Address;

import com.google.android.maps.GeoPoint;

public class MapBookmark {
	
	public String name;
	public String description;
	public int latitude;
	public int longitude;
	public int zoomLevel;
	public boolean satellite;
	public boolean traffic;
	
	public MapBookmark(){
		
	}
	
	public MapBookmark(String name, double latitude, double longitude){
		this.name = name;
		this.latitude = (int)(latitude*1E6);
		this.longitude = (int)(longitude*1E6);
		this.zoomLevel = 15;
	}
	
	public MapBookmark(String name){
		this.name = name;
	}
	
	public MapBookmark(
			String name, 
			String description, 
			int latitude, 
			int longitude,
			int zoomLevel,
			boolean sattellite,
			boolean traffic){
		this.name = name;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zoomLevel = zoomLevel;
		this.satellite = sattellite;
		this.traffic = traffic;
	}

	public MapBookmark(Address address) {
		this.name = address.getAddressLine(0);
		this.latitude = (int)(1E6 * address.getLatitude());
		this.longitude = (int)(1E6 * address.getLongitude());
		this.zoomLevel = 15;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		if(description == null)
			description = "";
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the traffic
	 */
	public boolean isTraffic() {
		return traffic;
	}

	/**
	 * @param traffic the traffic to set
	 */
	public void setTraffic(boolean traffic) {
		this.traffic = traffic;
	}

	/**
	 * @return the satellite
	 */
	public boolean isSatellite() {
		return satellite;
	}

	/**
	 * @param satellite the satellite to set
	 */
	public void setSatellite(boolean satellite) {
		this.satellite = satellite;
	}

	/**
	 * @return the zoomLevel
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * @param zoomLevel the zoomLevel to set
	 */
	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	/**
	 * @return the latitude
	 */
	public int getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public int getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public GeoPoint getPoint(){
		return new GeoPoint(latitude, longitude);
	}

}
