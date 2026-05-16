package com.nihith.breakdown.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihith.breakdown.controller.GroupAdminRestController;
import com.nihith.breakdown.dashboard.service.GroupAdminService;
import com.nihith.breakdown.model.groups.Family;
import com.nihith.breakdown.model.groups.Group;
import com.nihith.breakdown.model.response.MessageType;
import com.nihith.breakdown.model.response.ResponseMessages;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for GroupAdminRestController.
 * Tests REST endpoints for group administration — create, manage members, and manage families.
 */
@ExtendWith(MockitoExtension.class)
public class GroupAdminRestControllerTest {

    @Mock
    private GroupAdminService groupAdminService;

    @InjectMocks
    private GroupAdminRestController groupAdminRestController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupAdminRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createGroup_ValidGroupRequest_ReturnsSuccess() throws Exception {
        // Arrange
        Group groupRequest = new Group();
        groupRequest.setGroupName("Trip 2025");
        List<String> personList = new ArrayList<>();
        personList.add("alice");
        personList.add("bob");
        personList.add("carol");
        groupRequest.setPersonList(personList);
        groupRequest.setFamilyList(new ArrayList<>());

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Group created successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);

        Group responseGroup = new Group();
        responseGroup.setGroupId("a3f1c2d4-5e6f-7890-abcd-ef1234567890");
        responseGroup.setGroupName("Trip 2025");
        responseGroup.setPersonList(personList);
        responseGroup.setFamilyList(new ArrayList<>());
        responseStructure.setPayload(responseGroup);

        when(groupAdminService.createGroup(any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"groupName\": \"Trip 2025\",\n" +
                                "  \"personList\": [\"alice\", \"bob\", \"carol\"],\n" +
                                "  \"familyList\": []\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Group created successfully"))
                .andExpect(jsonPath("$.payload.groupName").value("Trip 2025"))
                .andExpect(jsonPath("$.payload.personList[0]").value("alice"));

        verify(groupAdminService, times(1)).createGroup(any(Group.class));
    }

    @Test
    void createGroup_EmptyPersonList_ReturnsSuccess() throws Exception {
        // Arrange
        Group groupRequest = new Group();
        groupRequest.setGroupName("Solo Trip");
        groupRequest.setPersonList(new ArrayList<>());
        groupRequest.setFamilyList(new ArrayList<>());

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Group created successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);

        Group responseGroup = new Group();
        responseGroup.setGroupId("test-group-id");
        responseGroup.setGroupName("Solo Trip");
        responseGroup.setPersonList(new ArrayList<>());
        responseGroup.setFamilyList(new ArrayList<>());
        responseStructure.setPayload(responseGroup);

        when(groupAdminService.createGroup(any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"groupName\": \"Solo Trip\",\n" +
                                "  \"personList\": [],\n" +
                                "  \"familyList\": []\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Group created successfully"))
                .andExpect(jsonPath("$.payload.groupName").value("Solo Trip"));

        verify(groupAdminService, times(1)).createGroup(any(Group.class));
    }

    @Test
    void addMembers_ValidMembers_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group addMembersRequest = new Group();
        List<String> newMembers = new ArrayList<>();
        newMembers.add("dave");
        newMembers.add("eve");
        addMembersRequest.setPersonList(newMembers);
        addMembersRequest.setFamilyList(new ArrayList<>());

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Members added successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.addMembers(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/add-members", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"personList\": [\"dave\", \"eve\"],\n" +
                                "  \"familyList\": []\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Members added successfully"));

        verify(groupAdminService, times(1)).addMembers(eq(groupId), any(Group.class));
    }

    @Test
    void addMembers_DuplicateMember_ReturnsFailure() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group addMembersRequest = new Group();
        List<String> newMembers = new ArrayList<>();
        newMembers.add("dave");
        addMembersRequest.setPersonList(newMembers);
        addMembersRequest.setFamilyList(new ArrayList<>());

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        ResponseMessages messages = new ResponseMessages();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Person dave is already a member of the group");
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.addMembers(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/add-members", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"personList\": [\"dave\"],\n" +
                                "  \"familyList\": []\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.errorMessages[0]").value("Person dave is already a member of the group"));

        verify(groupAdminService, times(1)).addMembers(eq(groupId), any(Group.class));
    }

    @Test
    void manageFamilies_CreateNewFamily_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group manageFamiliesRequest = new Group();
        List<Family> familyList = new ArrayList<>();
        Family newFamily = new Family();
        newFamily.setFamilyName("Trip Crew");
        List<String> personIds = new ArrayList<>();
        personIds.add("alice");
        personIds.add("bob");
        newFamily.setPersonIds(personIds);
        familyList.add(newFamily);
        manageFamiliesRequest.setFamilyList(familyList);

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Families Updated Successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.manageFamilies(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"familyList\": [\n" +
                                "    {\"familyName\": \"Trip Crew\", \"personIds\": [\"alice\", \"bob\"]}\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(Group.class));
    }

    @Test
    void manageFamilies_UpdateExistingFamily_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group manageFamiliesRequest = new Group();
        List<Family> familyList = new ArrayList<>();
        Family existingFamily = new Family();
        existingFamily.setFamilyId("fam-001");
        List<String> personIds = new ArrayList<>();
        personIds.add("carol");
        personIds.add("dave");
        existingFamily.setPersonIds(personIds);
        familyList.add(existingFamily);
        manageFamiliesRequest.setFamilyList(familyList);

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Families Updated Successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.manageFamilies(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"familyList\": [\n" +
                                "    {\"familyId\": \"fam-001\", \"personIds\": [\"carol\", \"dave\"]}\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(Group.class));
    }

    @Test
    void manageFamilies_DeleteFamily_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group manageFamiliesRequest = new Group();
        List<Family> familyList = new ArrayList<>();
        Family familyToDelete = new Family();
        familyToDelete.setFamilyId("fam-002");
        familyToDelete.setPersonIds(new ArrayList<>()); // Empty personIds indicates delete
        familyList.add(familyToDelete);
        manageFamiliesRequest.setFamilyList(familyList);

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Families Updated Successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.manageFamilies(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"familyList\": [\n" +
                                "    {\"familyId\": \"fam-002\", \"personIds\": []}\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(Group.class));
    }

    @Test
    void manageFamilies_InvalidPerson_ReturnsFailure() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group manageFamiliesRequest = new Group();
        List<Family> familyList = new ArrayList<>();
        Family newFamily = new Family();
        newFamily.setFamilyName("New Family");
        List<String> personIds = new ArrayList<>();
        personIds.add("invalid-person");
        newFamily.setPersonIds(personIds);
        familyList.add(newFamily);
        manageFamiliesRequest.setFamilyList(familyList);

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        ResponseMessages messages = new ResponseMessages();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Person invalid-person is not a member of the group");
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.manageFamilies(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"familyList\": [\n" +
                                "    {\"familyName\": \"New Family\", \"personIds\": [\"invalid-person\"]}\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.errorMessages[0]").value("Person invalid-person is not a member of the group"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(Group.class));
    }

    @Test
    void manageFamilies_MixedCreateUpdateDelete_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        Group manageFamiliesRequest = new Group();
        List<Family> familyList = new ArrayList<>();

        // Create
        Family newFamily = new Family();
        newFamily.setFamilyName("Trip Crew");
        List<String> personIds1 = new ArrayList<>();
        personIds1.add("alice");
        personIds1.add("bob");
        newFamily.setPersonIds(personIds1);
        familyList.add(newFamily);

        // Update
        Family existingFamily = new Family();
        existingFamily.setFamilyId("fam-001");
        List<String> personIds2 = new ArrayList<>();
        personIds2.add("carol");
        personIds2.add("dave");
        existingFamily.setPersonIds(personIds2);
        familyList.add(existingFamily);

        // Delete
        Family familyToDelete = new Family();
        familyToDelete.setFamilyId("fam-002");
        familyToDelete.setPersonIds(new ArrayList<>());
        familyList.add(familyToDelete);

        manageFamiliesRequest.setFamilyList(familyList);

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Families Updated Successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.manageFamilies(eq(groupId), any(Group.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"familyList\": [\n" +
                                "    {\"familyName\": \"Trip Crew\", \"personIds\": [\"alice\", \"bob\"]},\n" +
                                "    {\"familyId\": \"fam-001\", \"personIds\": [\"carol\", \"dave\"]},\n" +
                                "    {\"familyId\": \"fam-002\", \"personIds\": []}\n" +
                                "  ]\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(Group.class));
    }
}
