package com.here.maps.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.here.maps.app.constants.PlaceSearchApiConstants;
import com.here.maps.app.dtos.ResponseDTO;
import com.here.maps.app.service.PlaceSearchApiService;

/**
 * @author Vidyashri
 *
 */
@RestController
public class PlaceSearchApiController {

	private static final Logger logger = LoggerFactory.getLogger(PlaceSearchApiController.class);

	// Instance of the PlaceAPIService
	@Autowired
	private PlaceSearchApiService placeSearchApiService;

	// Instance of the ResponseDTO
	private ResponseDTO responseDTO = new ResponseDTO();

	/**
	 * @return Returns POI's on success else error message.
	 */
	@GetMapping(value = "/{cityName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPOIsDetailsBasedOnCityName(@PathVariable(value = "cityName") String cityName) {
		logger.trace("Enter inside :getPOIsDetailsBasedOnCityName");
		ResponseEntity<String> responseEntity = null;
		logger.info("{} API invoked to retrieve the Points of Interest details based on the cityName: {}",
				PlaceSearchApiConstants.LOGGER_PREFIX_API_INVOCATION, cityName);
		responseDTO = placeSearchApiService.getTheRequiredPOIsBasedOnCityName(cityName.toLowerCase());
		if (responseDTO.getStatusCode() == HttpStatus.OK.value()) {
			responseEntity = ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO.getMessage());
		} else {
			responseEntity = ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO.toString());
		}
		logger.trace("Exit from :getPOIsDetailsBasedOnCityName");
		return responseEntity;
	}
}