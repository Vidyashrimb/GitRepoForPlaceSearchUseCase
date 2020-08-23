Use-case:
Use the Here Maps Places Search API’s to find Parking spots, Charging Stations and Restaurants near the
user provided location.

Assumptions:
The city name will be with the country For Ex : (Moscow, Russia), (Berlin, Germany), (Docklands, London). 
The same will be converted to Latitude and Longitude values using the google's opencage data URL which requires an API key to get the details.
So we have 2 API keys since HERE maps place search API also requires an API key.
If the PoI's for any type is less than the required no of PoI's, it will return the available PoI's

The Project structure:
There are total 5 packages.
com.here.maps.app - Main Application
com.here.maps.app.constants - Used to define the constants required in the project
com.here.maps.app.controller - Used to define the controller which will have the REST API to get the PoI's based on the city name.
com.here.maps.app.dtos - Used to define the DTO's used, only one DTO used as per the requirement (ResponseDTO.java).
com.here.maps.app.service - Used to define the service classes, which have the main business Logic.

Project Setup and steps to run
Once the Docker Image of the project is down-loaded from the Git, we can see that the the main folder has 3 contents.
1. Main project - heremapsusecaseapplication
2. Config file for the container - env.config
2. README.txt

The env.config is used to pass the environment variables to the docker during run time. The content of this file will be as below.
OPENCAGEDATA_API_KEY=249203ff348e4fc7a6d7462ed59936ad;
PLACE_SEARCH_HERE_API_KEY=U7wh8mY0QQKDeQI9HuY_hiIzRPEymd9XxZM2YFcS7Ow;
NO_OF_POIS_FOR_EACH_TYPE=5
CATEGORIES=restaurant, shopping, going-out

OPENCAGEDATA_API_KEY = Opencagedata API key used for getting the Latitude and Longitude of the city.
PLACE_SEARCH_HERE_API_KEY = HERE maps API key used for getting the PoI details.
NO_OF_POIS_FOR_EACH_TYPE =  We can set the number of PoI's of Each type as required.
CATEGORIES = We can give the required categories to this variable.

We have to use the Ubuntu OS for running the Docker usiing below steps.

We have to build the image by using the command below,
sudo docker build -t <imagename> <Docker file location>
sudo docker build -t appforgettinglocationdetails .

After building the image we should start the docker container using the below commands,

sudo docker run --env-file env.config -p <HostIp>:<PortNumber>:8000 appforgettinglocationdetails

We should replace the IP of the ubuntu machine, <HostIp> and the required port number, <PortNumber> in the above command.

Once the docker is up we can hit the below api to get the required PoI's of the city.

http://<HostIp>:<PortNumber>/placeApiGetPois/{cityName}.
