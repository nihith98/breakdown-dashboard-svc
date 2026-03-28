package com.nihith.breakdown.controller;

import com.nihith.breakdown.dashboard.service.GroupViewService;
import com.nihith.breakdown.model.response.ResponseStructure;
import com.nihith.breakdown.model.transactions.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes the Group View endpoints under the {@code /group} base path.
 * The OpenAPI contract is defined in {@link GroupViewApi}; this class contains only
 * routing and delegation logic.
 */
@RestController
@RequestMapping("/group")
public class GroupViewRestController implements GroupViewApi {

    private static final Logger logger = LogManager.getLogger(GroupViewRestController.class);

    @Autowired
    private GroupViewService groupViewService;

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupViewService#getTransactions(String)} to retrieve
     * all expense transactions for the specified group.</p>
     */
    @Override
    @GetMapping("/{groupId}/transaction-list")
    public ResponseStructure fetchTransactionList(@PathVariable String groupId) {
        logger.info("Entered fetchTransactionList");
        return groupViewService.getTransactions(groupId);
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link GroupViewService#getSettlements(String)} to retrieve
     * the computed settlement list for the specified group.</p>
     */
    @Override
    @GetMapping("/{groupId}/settlement-list")
    public ResponseStructure fetchSettlementList(@PathVariable String groupId) {
        logger.info("Entered fetchSettlementList");
        return groupViewService.getSettlements(groupId);
    }

    /**
     * {@inheritDoc}
     * <p>Sets the {@code groupId} on the transaction from the path variable, then delegates
     * to {@link GroupViewService#insertTransaction(Transaction)} to persist the new expense
     * and recompute the settlement list.</p>
     */
    @Override
    @PostMapping("/{groupId}/insert-transaction")
    public ResponseStructure insertTransaction(@PathVariable String groupId,
                                               @Validated @RequestBody Transaction transaction) {
        logger.info("Entered insertTransaction");
        transaction.setGroupId(groupId);
        return groupViewService.insertTransaction(transaction);
    }

}
