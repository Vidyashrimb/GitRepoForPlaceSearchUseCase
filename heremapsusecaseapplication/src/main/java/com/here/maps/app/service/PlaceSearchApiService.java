package com.here.maps.app.service;

import com.here.maps.app.dtos.ResponseDTO;

/**
 * @author Vidyashri
 *
 */
public interface PlaceSearchApiService {

	/**
	 * @param cityName
	 * @return ResponseDTO
	 */
	ResponseDTO getTheRequiredPOIsBasedOnCityName(String cityName);
}