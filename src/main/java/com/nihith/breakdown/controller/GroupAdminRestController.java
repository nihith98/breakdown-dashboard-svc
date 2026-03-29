package com.nihith.breakdown.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihith.breakdown.dashboard.service.GroupAdminService;
import com.nihith.breakdown.doc.GroupAdminApi;
import com.nihith.breakdown.model.groups.Group;
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
        return groupAdminService.createGroup(request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#addMembers(String, Group)} to add members to the group.</p>
     */
    @Override
    @PostMapping("/{groupId}/add-members")
    public ResponseStructure addMembers(@PathVariable String groupId, @Validated @RequestBody Group request) {
        logger.info("Entered addMembers");
        return groupAdminService.addMembers(groupId, request);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupAdminService#manageFamilies(String, Group)} to process family operations.</p>
     */
    @Override
    @PostMapping("/{groupId}/manage-families")
    public ResponseStructure manageFamilies(@PathVariable String groupId, @Validated @RequestBody Group request) {
        logger.info("Entered manageFamilies");
        return groupAdminService.manageFamilies(groupId, request);
    }

}
