package com.example.dto;

/** Result of the JPQL report: properties count and average rent per city. */
public record CityStatsDTO(String city, Long propertyCount, Double averageRent) 
{
	
}
