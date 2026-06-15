package com.nihith.breakdown.controller.test;

import com.nihith.breakdown.controller.GroupRestController;
import com.nihith.breakdown.dashboard.service.GroupViewService;
import com.nihith.breakdown.model.constants.MessageConstants;
import com.nihith.breakdown.model.groups.Group;
import com.nihith.breakdown.model.response.ResponseMessages;
import com.nihith.breakdown.model.response.ResponseStatus;
import com.nihith.breakdown.model.response.ResponseStructure;
import com.nihith.breakdown.model.transactions.Transaction;
import com.nihith.breakdown.model.transactions.TransactionList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GroupRestControllerTest {

    @Mock
    private GroupViewService groupViewService;

    @InjectMocks
    private GroupRestController groupRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupRestController).build();
    }

    @Test
    void fetchGroupDetails_ValidGroupId_ReturnsGroupDetails() throws Exception {
        // Arrange
        String groupId = "trip2025";
        Group group = new Group();
        group.setGroupId(groupId);
        group.setGroupName("Europe Trip");
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add(MessageConstants.GROUP_DETAILS_FETCH_SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(group);
        when(groupViewService.getGroupDetails(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value(MessageConstants.GROUP_DETAILS_FETCH_SUCCESS))
                .andExpect(jsonPath("$.payload").exists());

        verify(groupViewService, times(1)).getGroupDetails(groupId);
    }

    @Test
    void fetchGroupDetails_ServiceFailure_ReturnsFailureResponse() throws Exception {
        // Arrange
        String groupId = "trip2025";
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(MessageConstants.INTERNAL_SERVER_ERROR);
        ResponseMessages messages = new ResponseMessages();
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);
        when(groupViewService.getGroupDetails(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.messages.errorMessages[0]").value(MessageConstants.INTERNAL_SERVER_ERROR));

        verify(groupViewService, times(1)).getGroupDetails(groupId);
    }

    @Test
    void fetchExpenseList_ValidGroupId_ReturnsExpenses() throws Exception {
        // Arrange
        String groupId = "trip2025";
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        TransactionList transactionList = new TransactionList(groupId);
        transactionList.setTransactionList(transactions);
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add(MessageConstants.TRANSACTION_LIST_FETCH_SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(transactionList);
        when(groupViewService.getTransactions(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}/expenses", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value(MessageConstants.TRANSACTION_LIST_FETCH_SUCCESS))
                .andExpect(jsonPath("$.payload").exists());

        verify(groupViewService, times(1)).getTransactions(groupId);
    }

    @Test
    void fetchExpenseList_NoExpenses_ReturnsEmptyList() throws Exception {
        // Arrange
        String groupId = "trip2025";
        TransactionList transactionList = new TransactionList(groupId);
        transactionList.setTransactionList(new ArrayList<>());
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add(MessageConstants.TRANSACTION_LIST_FETCH_SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(transactionList);
        when(groupViewService.getTransactions(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}/expenses", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.payload").exists());

        verify(groupViewService, times(1)).getTransactions(groupId);
    }

    @Test
    void fetchExpenseList_ServiceFailure_ReturnsFailureResponse() throws Exception {
        // Arrange
        String groupId = "trip2025";
        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(MessageConstants.INTERNAL_SERVER_ERROR);
        ResponseMessages messages = new ResponseMessages();
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);
        when(groupViewService.getTransactions(groupId)).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}/expenses", groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.messages.errorMessages[0]").value(MessageConstants.INTERNAL_SERVER_ERROR));

        verify(groupViewService, times(1)).getTransactions(groupId);
    }
}
