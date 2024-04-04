package com.telran.parking.setfinecost.dto;


public record FineDto (
		int ownerID, 
		String ownerStatus, 
		String ownerAddress, 
		int numbeTelefon, 
		String mail,
		int fineAmount) {}
