package com.telran.parking.setfinecost.consuner;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.parking.setfinecost.dto.FineDto;
import com.telran.parking.setfinecost.dto.OwnerDto;
import com.telran.parking.setfinecost.producer.FineDataTopicSender;
import com.telran.parking.setfinecost.service.SetFineCostServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OwnerDataTopicListener {

	@Autowired
	private SetFineCostServiceImpl setFineCostService;

	@Autowired
	private FineDataTopicSender fineDataSender;


	public Consumer<byte[]> ownerTopicListener() {
		return (messageBytes) -> {
			String message = new String(messageBytes, StandardCharsets.UTF_8);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				OwnerDto owner = objectMapper.readValue(message, OwnerDto.class);
				FineDto fine = setFineCostService.setFineAmount(owner);
				if (fine != null) {
					fineDataSender.sendMessage(fine);
				}
			} catch (Exception e) {
				log.error("Error processing the message: " + e.getMessage());
			}
		};
	}
}

