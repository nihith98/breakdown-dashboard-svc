package com.nihith.breakdown.controller;

import com.nihith.breakdown.dashboard.service.GroupViewService;
import com.nihith.breakdown.doc.GroupApi;
import com.nihith.breakdown.model.response.ResponseStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes RESTful Group resource endpoints under the {@code /groups} base path.
 * The OpenAPI contract is defined in {@link GroupApi}; this class contains only routing and delegation logic.
 */
@RestController
@RequestMapping("/groups")
public class GroupRestController implements GroupApi {

    private static final Logger logger = LogManager.getLogger(GroupRestController.class);

    @Autowired
    private GroupViewService groupViewService;

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupViewService#getGroupDetails(String)} to retrieve
     * the full details of the specified group.</p>
     */
    @Override
    @GetMapping("/{groupId}")
    public ResponseStructure fetchGroupDetails(@PathVariable String groupId) {
        logger.info("Entered fetchGroupDetails");
        return groupViewService.getGroupDetails(groupId);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupViewService#getTransactions(String)} to retrieve
     * all expense transactions for the specified group.</p>
     */
    @Override
    @GetMapping("/{groupId}/expenses")
    public ResponseStructure fetchExpenseList(@PathVariable String groupId) {
        logger.info("Entered fetchExpenseList");
        return groupViewService.getTransactions(groupId);
    }
}
