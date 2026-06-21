package com.nihith.breakdown.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihith.breakdown.controller.GroupAdminRestController;
import com.nihith.breakdown.dashboard.service.GroupAdminService;
import com.nihith.breakdown.model.groups.Group;
import com.nihith.breakdown.model.groups.JoinGroupRequest;
import com.nihith.breakdown.model.groups.ManageFamiliesRequest;
import com.nihith.breakdown.model.groups.ManageFamilyEntry;
import com.nihith.breakdown.model.groups.PersonEntry;
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
        ManageFamiliesRequest manageFamiliesRequest = new ManageFamiliesRequest();
        List<ManageFamilyEntry> familyList = new ArrayList<>();
        ManageFamilyEntry newFamily = new ManageFamilyEntry();
        newFamily.setFamilyName("Trip Crew");
        newFamily.setFamilyHex("#5B9BD5");
        List<PersonEntry> personIds = new ArrayList<>();
        PersonEntry alice = new PersonEntry(); alice.setPersonId("alice"); alice.setDisplayName("Alice");
        PersonEntry bob = new PersonEntry(); bob.setPersonId("bob"); bob.setDisplayName("Bob");
        personIds.add(alice); personIds.add(bob);
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

        when(groupAdminService.manageFamilies(eq(groupId), any(ManageFamiliesRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyList\":[{\"familyName\":\"Trip Crew\",\"familyHex\":\"#5B9BD5\"," +
                                "\"personIds\":[{\"personId\":\"alice\",\"displayName\":\"Alice\"},{\"personId\":\"bob\",\"displayName\":\"Bob\"}]}]}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(ManageFamiliesRequest.class));
    }

    @Test
    void manageFamilies_UpdateExistingFamily_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        ManageFamiliesRequest manageFamiliesRequest = new ManageFamiliesRequest();
        List<ManageFamilyEntry> familyList = new ArrayList<>();
        ManageFamilyEntry existingFamily = new ManageFamilyEntry();
        existingFamily.setFamilyId("fam-001");
        existingFamily.setFamilyName("Home Team");
        existingFamily.setFamilyHex("#4CAF7D");
        List<PersonEntry> personIds = new ArrayList<>();
        PersonEntry carol = new PersonEntry(); carol.setPersonId("carol"); carol.setDisplayName("Carol");
        PersonEntry dave = new PersonEntry(); dave.setPersonId("dave"); dave.setDisplayName("Dave");
        personIds.add(carol); personIds.add(dave);
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

        when(groupAdminService.manageFamilies(eq(groupId), any(ManageFamiliesRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyList\":[{\"familyId\":\"fam-001\",\"familyName\":\"Home Team\",\"familyHex\":\"#4CAF7D\"," +
                                "\"personIds\":[{\"personId\":\"carol\",\"displayName\":\"Carol\"},{\"personId\":\"dave\",\"displayName\":\"Dave\"}]}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(ManageFamiliesRequest.class));
    }

    @Test
    void manageFamilies_DeleteFamily_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        ManageFamiliesRequest manageFamiliesRequest = new ManageFamiliesRequest();
        List<ManageFamilyEntry> familyList = new ArrayList<>();
        ManageFamilyEntry familyToDelete = new ManageFamilyEntry();
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

        when(groupAdminService.manageFamilies(eq(groupId), any(ManageFamiliesRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyList\":[{\"familyId\":\"fam-002\",\"personIds\":[]}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(ManageFamiliesRequest.class));
    }

    @Test
    void manageFamilies_InvalidPerson_ReturnsFailure() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        ManageFamiliesRequest manageFamiliesRequest = new ManageFamiliesRequest();
        List<ManageFamilyEntry> familyList = new ArrayList<>();
        ManageFamilyEntry newFamily = new ManageFamilyEntry();
        newFamily.setFamilyName("New Family");
        newFamily.setFamilyHex("#E07B54");
        List<PersonEntry> personIds = new ArrayList<>();
        PersonEntry invalid = new PersonEntry(); invalid.setPersonId("invalid-person"); invalid.setDisplayName("Unknown");
        personIds.add(invalid);
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

        when(groupAdminService.manageFamilies(eq(groupId), any(ManageFamiliesRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyList\":[{\"familyName\":\"New Family\",\"familyHex\":\"#E07B54\"," +
                                "\"personIds\":[{\"personId\":\"invalid-person\",\"displayName\":\"Unknown\"}]}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.errorMessages[0]").value("Person invalid-person is not a member of the group"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(ManageFamiliesRequest.class));
    }

    @Test
    void manageFamilies_MixedCreateUpdateDelete_ReturnsSuccess() throws Exception {
        // Arrange
        String groupId = "a3f1c2d4-5e6f-7890-abcd-ef1234567890";
        ManageFamiliesRequest manageFamiliesRequest = new ManageFamiliesRequest();
        List<ManageFamilyEntry> familyList = new ArrayList<>();

        // Create
        ManageFamilyEntry newFamily = new ManageFamilyEntry();
        newFamily.setFamilyName("Trip Crew");
        newFamily.setFamilyHex("#5B9BD5");
        List<PersonEntry> personIds1 = new ArrayList<>();
        PersonEntry alice = new PersonEntry(); alice.setPersonId("alice"); alice.setDisplayName("Alice");
        PersonEntry bob = new PersonEntry(); bob.setPersonId("bob"); bob.setDisplayName("Bob");
        personIds1.add(alice); personIds1.add(bob);
        newFamily.setPersonIds(personIds1);
        familyList.add(newFamily);

        // Update
        ManageFamilyEntry existingFamily = new ManageFamilyEntry();
        existingFamily.setFamilyId("fam-001");
        existingFamily.setFamilyName("Home Team");
        existingFamily.setFamilyHex("#4CAF7D");
        List<PersonEntry> personIds2 = new ArrayList<>();
        PersonEntry carol = new PersonEntry(); carol.setPersonId("carol"); carol.setDisplayName("Carol");
        PersonEntry dave = new PersonEntry(); dave.setPersonId("dave"); dave.setDisplayName("Dave");
        personIds2.add(carol); personIds2.add(dave);
        existingFamily.setPersonIds(personIds2);
        familyList.add(existingFamily);

        // Delete
        ManageFamilyEntry familyToDelete = new ManageFamilyEntry();
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

        when(groupAdminService.manageFamilies(eq(groupId), any(ManageFamiliesRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/{groupId}/manage-families", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyList\":[" +
                                "{\"familyName\":\"Trip Crew\",\"familyHex\":\"#5B9BD5\",\"personIds\":[{\"personId\":\"alice\",\"displayName\":\"Alice\"},{\"personId\":\"bob\",\"displayName\":\"Bob\"}]}," +
                                "{\"familyId\":\"fam-001\",\"familyName\":\"Home Team\",\"familyHex\":\"#4CAF7D\",\"personIds\":[{\"personId\":\"carol\",\"displayName\":\"Carol\"},{\"personId\":\"dave\",\"displayName\":\"Dave\"}]}," +
                                "{\"familyId\":\"fam-002\",\"personIds\":[]}" +
                                "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Families Updated Successfully"));

        verify(groupAdminService, times(1)).manageFamilies(eq(groupId), any(ManageFamiliesRequest.class));
    }

    @Test
    void joinGroupByCode_ValidCode_ReturnsSuccess() throws Exception {
        // Arrange
        JoinGroupRequest joinRequest = new JoinGroupRequest();
        joinRequest.setJoiningCode("c24aad90-25bc-43f8-bade-637c8c775024");

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.SUCCESS);
        ResponseMessages messages = new ResponseMessages();
        List<String> infoMessages = new ArrayList<>();
        infoMessages.add("Joined group successfully");
        messages.setInformationMessages(infoMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.joinGroupByCode(any(JoinGroupRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"joiningCode\": \"c24aad90-25bc-43f8-bade-637c8c775024\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messages.informationMessages[0]").value("Joined group successfully"));

        verify(groupAdminService, times(1)).joinGroupByCode(any(JoinGroupRequest.class));
    }

    @Test
    void joinGroupByCode_InvalidCode_ReturnsFailure() throws Exception {
        // Arrange
        JoinGroupRequest joinRequest = new JoinGroupRequest();
        joinRequest.setJoiningCode("invalid-code");

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        ResponseMessages messages = new ResponseMessages();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Failed to join group");
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.joinGroupByCode(any(JoinGroupRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"joiningCode\": \"invalid-code\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.errorMessages[0]").value("Failed to join group"));

        verify(groupAdminService, times(1)).joinGroupByCode(any(JoinGroupRequest.class));
    }

    @Test
    void joinGroupByCode_AlreadyMember_ReturnsFailure() throws Exception {
        // Arrange
        JoinGroupRequest joinRequest = new JoinGroupRequest();
        joinRequest.setJoiningCode("c24aad90-25bc-43f8-bade-637c8c775024");

        ResponseStructure responseStructure = new ResponseStructure();
        responseStructure.setStatus(ResponseStatus.FAILURE);
        ResponseMessages messages = new ResponseMessages();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Failed to join group");
        messages.setErrorMessages(errorMessages);
        responseStructure.setMessages(messages);
        responseStructure.setPayload(null);

        when(groupAdminService.joinGroupByCode(any(JoinGroupRequest.class))).thenReturn(responseStructure);

        // Act & Assert
        mockMvc.perform(post("/admin/group/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"joiningCode\": \"c24aad90-25bc-43f8-bade-637c8c775024\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.errorMessages[0]").value("Failed to join group"));

        verify(groupAdminService, times(1)).joinGroupByCode(any(JoinGroupRequest.class));
    }
}
