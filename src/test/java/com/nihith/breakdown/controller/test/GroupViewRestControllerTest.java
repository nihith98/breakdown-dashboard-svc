package com.nihith.breakdown.controller.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihith.breakdown.controller.GroupViewRestController;
import com.nihith.breakdown.dashboard.converter.GroupViewConverter;
import com.nihith.breakdown.dashboard.delegator.DBDelegator;
import com.nihith.breakdown.dashboard.service.GroupViewService;
import com.nihith.breakdown.model.constants.MessageConstants;
import com.nihith.breakdown.model.exceptions.SystemException;
import com.nihith.breakdown.model.individuals.PaidFor;
import com.nihith.breakdown.model.response.MessageType;
import com.nihith.breakdown.model.response.ResponseMessages;
import com.nihith.breakdown.model.response.ResponseStatus;
import com.nihith.breakdown.model.response.ResponseStructure;
import com.nihith.breakdown.model.service.TransactionDBService;
import com.nihith.breakdown.model.transactions.SplitType;
import com.nihith.breakdown.model.transactions.Transaction;
import com.nihith.breakdown.model.transactions.TransactionList;
import com.nihith.breakdown.model.transactions.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GroupViewRestControllerTest {

    @Mock
    private GroupViewService groupViewService;

    @InjectMocks
    private GroupViewRestController groupViewRestController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupViewRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void fetchTransactionList_ValidGroupId_ReturnsTransactions() throws Exception {
        // Arrange
        String groupId = "testGroupId";
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Transaction inserted successfully");
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(transactions);
        when(groupViewService.getTransactions(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/group/{groupId}/transaction-list", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Transaction inserted successfully"))
                .andExpect(jsonPath("$.payload").exists());

        verify(groupViewService, times(1)).getTransactions(groupId);
    }

    @Test
    void fetchTransactionList_NoTransactions_ReturnsEmptyList() throws Exception {
        // Arrange
        String groupId = "testGroupId";
        List<Transaction> transactions = new ArrayList<>();
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Transaction inserted successfully");
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(transactions);
        when(groupViewService.getTransactions(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/group/{groupId}/transaction-list", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Transaction inserted successfully"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload").isEmpty());

        verify(groupViewService, times(1)).getTransactions(groupId);
    }

    @Test
    void insertTransaction_ValidTransaction_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "testGroupId";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setTransactionDescription("Test Transaction");
        transaction.setPaidById("testId1");
        transaction.setSplitType(SplitType.EQUAL);
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setGroupId(groupId);
        PaidFor paidFor1 = new PaidFor();
        paidFor1.setPaidForId("testId2");
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Transaction inserted successfully");
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(true);

        when(groupViewService.insertTransaction(any(Transaction.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/group/{groupId}/insert-transaction", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"transactionName\": \"testtransactionName2\",\n" +
                                "  \"transactionDescription\": \"testtransactionDescription2\",\n" +
                                "  \"transactionType\": \"EXPENSE\",\n" +
                                "  \"amount\": 23.04,\n" +
                                "  \"paidById\": \"paid2\",\n" +
                                "  \"paidForList\": [\n" +
                                "    {\"paidForId\":\"paidFor1\"}\n" +
                                "  ],\n" +
                                "  \"timestamp\": null,\n" +
                                "  \"groupId\":\"testGroup\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Transaction inserted successfully"))
                .andExpect(jsonPath("$.payload").exists());

        verify(groupViewService, times(1)).insertTransaction(any(Transaction.class));
    }

    @Test
    void insertTransaction_NullTransaction_ReturnsError() throws Exception {
        // Arrange
        String groupId = "testGroupId";
        // Act & Assert
        mockMvc.perform(post("/group/{groupId}/insert-transaction", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
        verify(groupViewService, times(0)).insertTransaction(any(Transaction.class));
    }

    @Test
    void insertTransaction_InvalidTransaction_ReturnsError() throws Exception {
        // Arrange
        String groupId = "testGroupId";
        Transaction transaction = new Transaction();
        transaction.setTransactionDescription("Test Transaction"); // Missing amount, for example
        // Act & Assert
        mockMvc.perform(post("/group/{groupId}/insert-transaction", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest());
        verify(groupViewService, times(0)).insertTransaction(any(Transaction.class));
    }

}