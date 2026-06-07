package com.nihith.breakdown.controller.test;

import com.nihith.breakdown.controller.GroupListRestController;
import com.nihith.breakdown.dashboard.service.GroupListService;
import com.nihith.breakdown.model.response.ResponseStatus;
import com.nihith.breakdown.model.response.ResponseStructure;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GroupListRestControllerTest {

    @Mock
    private GroupListService groupListService;

    @InjectMocks
    private GroupListRestController groupListRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupListRestController).build();
    }

    @Test
    void getGroupList_ValidUserId_ReturnsSuccess() throws Exception {
        // Arrange
        ResponseStructure mockResponse = new ResponseStructure();
        mockResponse.setStatus(ResponseStatus.SUCCESS);
        when(groupListService.getGroupList()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/group/list")
                .header("X-User-Id", "user123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(groupListService, times(1)).getGroupList();
    }

    @Test
    void getGroupList_ServiceFailure_ReturnsFailure() throws Exception {
        // Arrange
        ResponseStructure mockResponse = new ResponseStructure();
        mockResponse.setStatus(ResponseStatus.FAILURE);
        when(groupListService.getGroupList()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/group/list")
                .header("X-User-Id", "user123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(groupListService, times(1)).getGroupList();
    }
}
