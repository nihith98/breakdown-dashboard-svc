package com.nihith.breakdown.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nihith.breakdown.dashboard.service.GroupAdminService;
import com.nihith.breakdown.doc.GroupAdminApi;
import com.nihith.breakdown.model.groups.Group;
import com.nihith.breakdown.model.groups.JoinGroupRequest;
import com.nihith.breakdown.model.response.ResponseStructure;

/**
 * REST controller that exposes the Group Admin endpoints under the {@code /admin/group} base path.
 * The OpenAPI contract is defined in {@link GroupAdminApi}; this class contains only
 * routing and delegation logic.
 */
@RestController
@RequestMapping("/admin/group")
public class GroupAdminRestController implements GroupAdminApi {

    private static final Logger logger = LogManager.getLogger(GroupAdminRestController.class);

    @Autowired
    private GroupAdminService groupAdminService;

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#createGroup(Group)} to persist the new group.</p>
     */
    @Override
    @PostMapping("/create")
    public ResponseStructure createGroup(@Validated @RequestBody Group request) {
        logger.info("Entered createGroup");
        if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "groupName is required");
        }
        return groupAdminService.createGroup(request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#addMembers(String, Group)} to add members to the group.</p>
     */
    @Override
    @PostMapping("/{groupId}/add-members")
    public ResponseStructure addMembers(@PathVariable String groupId, @RequestBody Group request) {
        logger.info("Entered addMembers");
        return groupAdminService.addMembers(groupId, request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#manageFamilies(String, Group)} to process family operations.
     * Family definitions directly impact settlement computation — when families exist, the settlement engine
     * operates at family level with aggregated member balances. Future settlements reflect the updated family
     * structure; existing settlements are preserved.</p>
     */
    @Override
    @PostMapping("/{groupId}/manage-families")
    public ResponseStructure manageFamilies(@PathVariable String groupId, @RequestBody Group request) {
        logger.info("Entered manageFamilies");
        return groupAdminService.manageFamilies(groupId, request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#joinGroupByCode(JoinGroupRequest)} to add the user to the group.</p>
     */
    @Override
    @PostMapping("/join")
    public ResponseStructure joinGroupByCode(@Validated @RequestBody JoinGroupRequest request) {
        logger.info("Entered joinGroupByCode");
        return groupAdminService.joinGroupByCode(request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#removeMember(String, String)} to remove the member
     * from the group.</p>
     */
    @Override
    @DeleteMapping("/{groupId}/remove-member/{userId}")
    public ResponseStructure removeMember(@PathVariable String groupId, @PathVariable String userId) {
        logger.info("Entered removeMember");
        return groupAdminService.removeMember(groupId, userId);
    }

}
