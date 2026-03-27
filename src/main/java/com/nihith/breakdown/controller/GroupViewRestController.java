package com.nihith.breakdown.controller;

import com.nihith.breakdown.dashboard.service.GroupViewService;
import com.nihith.breakdown.model.response.ResponseStructure;
import com.nihith.breakdown.model.transactions.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupViewRestController {

    private static final Logger logger = LogManager.getLogger(GroupViewRestController.class);

    @Autowired
    private GroupViewService groupViewService;

    @GetMapping("/{groupId}/transaction-list")
    public ResponseStructure fetchTransactionList(@PathVariable String groupId) {
        logger.info("Entered fetchTransactionList");
        return groupViewService.getTransactions(groupId);

    }

    @GetMapping("/{groupId}/settlement-list")
    public ResponseStructure fetchSettlementList(@PathVariable String groupId) {
        logger.info("Entered fetchSettlementList");
        return groupViewService.getSettlements(groupId);

    }

    @PostMapping("/{groupId}/insert-transaction")
    public ResponseStructure insertTransaction(@PathVariable String groupId, @Validated @RequestBody Transaction transaction) {
        logger.info("Entered insertTransaction");
        transaction.setGroupId(groupId);
        return groupViewService.insertTransaction(transaction);
    }


}
