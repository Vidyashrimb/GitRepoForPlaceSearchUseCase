package com.here.maps.app.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.here.maps.app.constants.PlaceSearchApiConstants;

public class ExecutorServiceHelper {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceHelper.class);

	private static int EXECUTOR_FIXED_THREADPOOL_FOR_PLACE_API;

	private static ExecutorService executorPlaceSearchService = null;

	/**
	 * Method init.
	 */

	public static void init() {
		logger.debug("inside ExecutorServiceHelper helper");
		try {
			EXECUTOR_FIXED_THREADPOOL_FOR_PLACE_API = Integer
					.parseInt(System.getenv().get(PlaceSearchApiConstants.NO_OF_POIS_FOR_EACH_TYPE).trim());
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException occured : {}", e.getMessage());
		}
		executorPlaceSearchService = Executors.newFixedThreadPool(EXECUTOR_FIXED_THREADPOOL_FOR_PLACE_API);

	}

	/**
	 * @return the executorPlaceSearchService
	 */
	public static ExecutorService getExecutorPlaceSearchService() {
		init();
		return executorPlaceSearchService;
	}

	public void cleanup() {
		logger.debug("inside cleanup ExecutorServiceHelper helper");
		executorPlaceSearchService.shutdown();
	}
}
