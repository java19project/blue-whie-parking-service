package com.telran.parking.setfinecost.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telran.parking.setfinecost.consuner.OwnerDataTopicListener;
import com.telran.parking.setfinecost.dto.FineDto;
import com.telran.parking.setfinecost.dto.OwnerDto;
import com.telran.parking.setfinecost.producer.FineDataTopicSender;
import com.telran.parking.setfinecost.service.SetFineCostServiceImpl;

@Import(TestChannelBinderConfiguration.class)
@SpringBootTest
public class SetFineParkingServiceTest {

	@MockBean
	private OwnerDataTopicListener ownerDataTopicListener;

	@Autowired
	private SetFineCostServiceImpl setFineCostService;

	@MockBean
	private FineDataTopicSender fineDataSender;

	@Autowired
	private InputDestination inputDestination;

	@Autowired
	private OutputDestination outputDestination;

	private static OwnerDto paidParkingOwner;
	private static OwnerDto freeParkingOwner;
	private static FineDto fineForPaidParkingOwner;

	@BeforeAll
	static public void setup() throws Exception {
		paidParkingOwner = new OwnerDto(2, "paidParking", "Address 2", 987654321, "test2@example.com");
		freeParkingOwner = new OwnerDto(1, "freeParking", "Address 1", 123456789, "test1@example.com");
		fineForPaidParkingOwner = new FineDto(2, "paidParking", "Address 2", 987654321, "test2@example.com", 250);
	}

	@Test
	public void testByteLengthInOutputDestination() {

		inputDestination.send(new GenericMessage<>(paidParkingOwner), "set-fine-cost-in-0");
		
		try {
			outputDestination.wait(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		byte[] messageBytes = outputDestination.receive(0, "set-fine-cost-out-0").getPayload();
		byte[] messageBytes = outputDestination.receive(100).getPayload();
		assertNotEquals(messageBytes.length, 0);
	}

	@Test
	public void testNotNullInOutputDestination() {

		ObjectMapper mapper = new ObjectMapper();
		byte[] paidParkingOwnerAsBytes = null;

		try {
			paidParkingOwnerAsBytes = mapper.writeValueAsBytes(paidParkingOwner);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// Send the message to the input destination
		inputDestination.send(new GenericMessage<>(paidParkingOwnerAsBytes), "set-fine-cost-in-0");

		// Wait for a short period to allow processing
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Receive the message from the output destination
//		byte[] messageBytes = outputDestination.receive(0, "set-fine-cost-out-0").getPayload();
		byte[] messageBytes = outputDestination.receive(100).getPayload();

		// Assert that a non-null message has been received
		assertNotNull(messageBytes);
	}

	@Test
	public void testSetFineCostService() {

		assertNull(setFineCostService.setFineAmount(freeParkingOwner));

		assertEquals(setFineCostService.setFineAmount(paidParkingOwner), fineForPaidParkingOwner);

	}

	@Test
	public void testOwnerTopicListener() {
		ObjectMapper mapper = new ObjectMapper();
		byte[] paidParkingOwnerAsBytes = null;
		try {
			paidParkingOwnerAsBytes = mapper.writeValueAsBytes(paidParkingOwner);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// Mock the behavior of ownerTopicListener to perform an action when invoked
		doAnswer(invocation -> {
			byte[] argument = invocation.getArgument(0); // This line might throw ArrayIndexOutOfBoundsException if no
															// arguments are provided
			OwnerDto receivedOwner = mapper.readValue(argument, OwnerDto.class);

			assertEquals(paidParkingOwner, receivedOwner);

			FineDto fineDto = new FineDto(2, "paidParking", "Address 2", 987654321, "test2@example.com", 250);
			fineDataSender.sendMessage(fineDto);

			return null;
		}).when(ownerDataTopicListener).ownerTopicListener();

		// Invoke the tested method that triggers the ownerTopicListener() with valid
		// arguments
		ownerDataTopicListener.ownerTopicListener().accept(paidParkingOwnerAsBytes);

		// Verify that ownerTopicListener was invoked
		verify(ownerDataTopicListener).ownerTopicListener();
	}

}
