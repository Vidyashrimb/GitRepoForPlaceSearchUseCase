package com.here.maps.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.here.maps.app.constants.PlaceSearchApiConstants;
import com.here.maps.app.dtos.ResponseDTO;

/**
 * @author Vidyashri
 *
 */
@Component
@CacheConfig(cacheNames = { "PoIsOfCity" })
public class PlaceSearchApiServiceImpl implements PlaceSearchApiService {

	private static final Logger logger = LoggerFactory.getLogger(PlaceSearchApiServiceImpl.class);

	private String OPENCAGEDATA_API_KEY = System.getenv("OPENCAGEDATA_API_KEY");
	private String PLACE_SEARCH_HERE_API_KEY = System.getenv("PLACE_SEARCH_HERE_API_KEY");
	private int NO_OF_POIS_FOR_EACH_TYPE;
	private String CATEGORIES = System.getenv("CATEGORIES");

	private ResponseDTO responseDTO = new ResponseDTO();

	/**
	 * Returns the Response DTO to the controller
	 *
	 * @param receivedCityName
	 * @return ResponseDTO
	 */
	@Override
	@Cacheable(value = "responseDTO", key = "#receivedCityName")
	public ResponseDTO getTheRequiredPOIsBasedOnCityName(String receivedCityName) {
		logger.trace("Enter inside :getTheRequiredPOIsBasedOnCityName");
		String cityName = receivedCityName.replace(" ", "");
		JSONObject finalJsonObject = getPoIsBasedOnCityName(cityName);
		responseDTO.setStatusCode(HttpStatus.OK.value());
		responseDTO.setMessage(finalJsonObject.toString());
		logger.debug("{} Prepared the API Response and Sending back : {}",
				PlaceSearchApiConstants.LOG_PREFIX_PLACE_API_PROCESSOR, responseDTO);
		logger.trace("Exit from :getTheRequiredPOIsBasedOnCityName");
		return responseDTO;
	}

	/**
	 * Based on the received CityName processes the result after hitting the URL and
	 * returns the Response DTO to the controller
	 *
	 * @param cityName
	 * @return modifiedJsonObject
	 */
	private JSONObject getPoIsBasedOnCityName(String cityName) {
		logger.trace("Enter inside :getPointsOfInterestForCityName");
		String[] categories = CATEGORIES.split(",");
		try {
			NO_OF_POIS_FOR_EACH_TYPE = Integer
					.parseInt(System.getenv().get(PlaceSearchApiConstants.NO_OF_POIS_FOR_EACH_TYPE).trim());
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException occured : {}", e.getMessage());
		}
		JSONObject modifiedJsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONArray newJsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObj = getTheLattitudeAndLongitudeOfTheCity(cityName);
		JSONObject geometryObj = jsonObj.has(PlaceSearchApiConstants.GEOMETRY)
				? jsonObj.getJSONObject(PlaceSearchApiConstants.GEOMETRY)
				: null;
		String lat = "";
		String lng = "";
		if (geometryObj != null && !geometryObj.isEmpty()) {
			lat = geometryObj.has(PlaceSearchApiConstants.LAT) ? geometryObj.get(PlaceSearchApiConstants.LAT).toString()
					: "";
			lng = geometryObj.has(PlaceSearchApiConstants.LONG)
					? geometryObj.get(PlaceSearchApiConstants.LONG).toString()
					: "";
		}
		logger.debug("The Coordinates of the mentioned City Lat : {}, Long : {}", lat, lng);

		String cat = "";
		for (int i = 0; i < categories.length; i++) {
			cat = cat + categories[i].trim() + ",";
		}

		cat = cat.substring(0, cat.lastIndexOf(","));
		String url = "https://places.ls.hereapi.com/places/v1/discover/explore?at=" + lat + "," + lng + "&" + "cat="
				+ cat + "&apiKey=" + PLACE_SEARCH_HERE_API_KEY;

		logger.info("The URL used to get the POI's of Category {}, is : {}", cat, url);
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		HttpResponse response;
		try {
			response = client.execute(request);

			HttpEntity responseEntity = response.getEntity();
			// Read the contents of an entity and return it as a String.
			String content = EntityUtils.toString(responseEntity);
			JSONObject receivedSuccessRsp = new JSONObject(content);
			if (receivedSuccessRsp != null && !receivedSuccessRsp.isEmpty()) {
				jsonObject = receivedSuccessRsp.getJSONObject(PlaceSearchApiConstants.RESULTS);
				jsonArray = jsonObject.getJSONArray(PlaceSearchApiConstants.ITEMS);
				for (int i = 0; i < categories.length; i++) {
					getPoIsOfIndividualType(jsonArray, categories[i].replace(",", "").trim(), newJsonArray);
					if (newJsonArray != null && !newJsonArray.isEmpty()) {
						modifiedJsonObject.put(categories[i].replace(",", "").trim(), getSortedJsonArray(newJsonArray));
					} else {
						modifiedJsonObject.put(categories[i].replace(",", "").trim(), new JSONArray());
					}
					newJsonArray = new JSONArray();
				}
			}
		} catch (IOException e) {
			logger.error("IOException occured : {}", e.getMessage());
		}
		logger.trace("Exit from :getPointsOfInterestForCityName");
		return modifiedJsonObject;
	}

	/**
	 * Obtains the Latitude and Longitude of the city by using the google maps API.
	 * returns the newJson which will have the Latitude and Longitude details of the
	 * city.
	 * 
	 * @param cityName
	 * @return newJson
	 */

	private JSONObject getTheLattitudeAndLongitudeOfTheCity(String cityName) {
		logger.trace("Enter inside :getTheLattitudeAndLongitudeOfTheCity");
		String url = "https://api.opencagedata.com/geocode/v1/json?q=" + cityName + "&key=" + OPENCAGEDATA_API_KEY
				+ "&language=en&pretty=1";

		logger.info("The URL used to get the Coordinates of the City {}, is : {}", cityName, url);
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		JSONObject newJson = new JSONObject();
		HttpResponse response;
		try {
			response = client.execute(request);

			HttpEntity responseEntity = response.getEntity();
			// Read the contents of an entity and return it as a String.
			String content = EntityUtils.toString(responseEntity);
			JSONObject receivedSuccessRsp = new JSONObject(content);
			if (receivedSuccessRsp != null && !receivedSuccessRsp.isEmpty()) {
				jsonArray = receivedSuccessRsp.getJSONArray(PlaceSearchApiConstants.RESULTS);
				jsonObj = jsonArray.getJSONObject(0);
				newJson.put(PlaceSearchApiConstants.CITY_NAME,
						jsonObj.has(PlaceSearchApiConstants.FORMATTED)
								? jsonObj.get((PlaceSearchApiConstants.FORMATTED))
								: "");
				newJson.put(PlaceSearchApiConstants.GEOMETRY,
						jsonObj.has(PlaceSearchApiConstants.GEOMETRY) ? jsonObj.get(PlaceSearchApiConstants.GEOMETRY)
								: "");

			}
		} catch (IOException e) {
			logger.error("IOException occured : {}", e.getMessage());
		}
		logger.trace("Exit from :getTheLattitudeAndLongitudeOfTheCity");
		return newJson;
	}

	/**
	 * Obtains the PoIs of a particular type for the city by grouping JSONObject
	 * data from the jsonArray to individual type and adds the grouped JSONObject
	 * data to the newJsonArray.
	 * 
	 * @param cityName
	 * @return newJson
	 */

	private void getPoIsOfIndividualType(JSONArray jsonArray, String categoryType, JSONArray newJsonArray) {
		logger.trace("Enter inside :getPoIsOfIndividualType");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject category = jsonArray.getJSONObject(i).has(PlaceSearchApiConstants.CATEGORY)
					? jsonArray.getJSONObject(i).getJSONObject(PlaceSearchApiConstants.CATEGORY)
					: null;
			if (category.getString(PlaceSearchApiConstants.ID).equalsIgnoreCase(categoryType)
					|| (categoryType.equalsIgnoreCase("Shopping")
							&& (category.getString(PlaceSearchApiConstants.ID).equalsIgnoreCase("mall")
									|| category.getString(PlaceSearchApiConstants.ID).equalsIgnoreCase("shop")
									|| category
											.getString(PlaceSearchApiConstants.ID)
											.equalsIgnoreCase("clothing-accessories-shop"))
							|| (categoryType.equalsIgnoreCase("restaurant") && (category
									.getString(PlaceSearchApiConstants.ID).equalsIgnoreCase("dance-night-club")
									|| category.getString(PlaceSearchApiConstants.ID).equalsIgnoreCase("bar-pub"))))) {
				newJsonArray.put(jsonArray.getJSONObject(i));
				jsonArray.remove(i);
			}
		}
		logger.trace("Exit from :getPoIsOfIndividualType");
	}

	/**
	 * Sorts the jsonArray based on the distance and returns the jsonArray with the
	 * no of objects as expected (NO_OF_POIS_FOR_EACH_TYPE)
	 * 
	 * @param cityName
	 * @return newJson
	 */

	private JSONArray getSortedJsonArray(JSONArray jsonArray) {
		logger.trace("Enter inside :getSortedJsonArray");
		JSONArray sortedJsonArray = new JSONArray();
		JSONArray modifiedJsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		List<JSONObject> list = new ArrayList<JSONObject>();
		logger.debug("Json Array of the category is : {}", jsonArray);
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getJSONObject(i));
		}
		Collections.sort(list, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				int distance1 = 0;
				int distance2 = 0;
				try {
					distance1 = (int) a.get(PlaceSearchApiConstants.DISTANCE);
					distance2 = (int) b.get(PlaceSearchApiConstants.DISTANCE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return Integer.compare(distance1, distance2);
			}
		});
		for (int i = 0; i < jsonArray.length(); i++) {
			if (i == NO_OF_POIS_FOR_EACH_TYPE) {
				break;
			}
			sortedJsonArray.put(list.get(i));
			jsonObj.put(PlaceSearchApiConstants.POSITION,
					sortedJsonArray.getJSONObject(i).has(PlaceSearchApiConstants.POSITION)
							? sortedJsonArray.getJSONObject(i).get(PlaceSearchApiConstants.POSITION)
							: null);
			jsonObj.put(PlaceSearchApiConstants.DISTANCE,
					sortedJsonArray.getJSONObject(i).has(PlaceSearchApiConstants.DISTANCE)
							? sortedJsonArray.getJSONObject(i).getInt(PlaceSearchApiConstants.DISTANCE)
							: 0);
			jsonObj.put(PlaceSearchApiConstants.TITLE,
					sortedJsonArray.getJSONObject(i).has(PlaceSearchApiConstants.TITLE)
							? sortedJsonArray.getJSONObject(i).get(PlaceSearchApiConstants.TITLE)
							: "");
			JSONObject category = sortedJsonArray.getJSONObject(i).has(PlaceSearchApiConstants.CATEGORY)
					? sortedJsonArray.getJSONObject(i).getJSONObject(PlaceSearchApiConstants.CATEGORY)
					: null;
			JSONObject requiredCategory = new JSONObject();
			requiredCategory.put(PlaceSearchApiConstants.ID,
					category.has(PlaceSearchApiConstants.ID) ? category.getString(PlaceSearchApiConstants.ID) : "");
			requiredCategory.put(PlaceSearchApiConstants.TITLE,
					category.has(PlaceSearchApiConstants.TITLE) ? category.getString(PlaceSearchApiConstants.TITLE)
							: "");
			requiredCategory.put(PlaceSearchApiConstants.HREF,
					category.has(PlaceSearchApiConstants.HREF) ? category.getString(PlaceSearchApiConstants.HREF) : "");
			jsonObj.put(PlaceSearchApiConstants.CATEGORY, requiredCategory);
			modifiedJsonArray.put(jsonObj);
			jsonObj = new JSONObject();
		}
		logger.trace("Exit from :getSortedJsonArray");
		return modifiedJsonArray;
	}
}
