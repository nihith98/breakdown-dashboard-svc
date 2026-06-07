package com.nihith.breakdown.controller;

import com.nihith.breakdown.dashboard.service.GroupListService;
import com.nihith.breakdown.doc.GroupListApi;
import com.nihith.breakdown.model.response.ResponseStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for group list operations.
 * {@link GroupListApi}
 */
@RestController
@RequestMapping("/group")
public class GroupListRestController implements GroupListApi {

    private static final Logger logger = LogManager.getLogger(GroupListRestController.class);

    @Autowired
    private GroupListService groupListService;

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupListService#getGroupList()}.</p>
     */
    @Override
    @GetMapping("/list")
    public ResponseStructure getGroupList(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        logger.info("Entered getGroupList");
        return groupListService.getGroupList();
    }
}
