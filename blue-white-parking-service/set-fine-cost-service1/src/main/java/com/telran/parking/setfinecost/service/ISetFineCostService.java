package com.telran.parking.setfinecost.service;

import com.telran.parking.setfinecost.dto.FineDto;
import com.telran.parking.setfinecost.dto.OwnerDto;

public interface ISetFineCostService {
	FineDto setFineAmount (OwnerDto owner);

}
