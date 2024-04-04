package com.telran.parking.setfinecost.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.telran.parking.setfinecost.dto.FineDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FineDataTopicSender {

    @Autowired
    StreamBridge sb;
    
    public void sendMessage(FineDto fine) {
        try {
            sb.send("fine-data", fine);
        } catch (Exception e) {
            log.error("Error while trying to send the message", e);
        }
    }
}
