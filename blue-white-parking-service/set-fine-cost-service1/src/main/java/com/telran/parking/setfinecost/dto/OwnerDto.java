package com.telran.parking.setfinecost.dto;

public record OwnerDto (
		int ownerID, 
		String ownerStatus, 
		String ownerAddress, 
		int numbeTelefon, 
		String mail) {}