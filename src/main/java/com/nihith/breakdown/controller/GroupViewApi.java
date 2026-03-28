package com.nihith.breakdown.controller;

import com.nihith.breakdown.model.response.ResponseStructure;
import com.nihith.breakdown.model.transactions.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI contract for the Group View REST endpoints.
 * All Swagger documentation, descriptions, and examples are defined here,
 * keeping GroupViewRestController free of annotation noise.
 */
@Tag(
        name = "Group View",
        description = "APIs for managing expense groups — record transactions, retrieve expense history, " +
                      "and view the computed settlement list. Supports the Family feature, where a sub-group " +
                      "of members can settle expenses as a single collective entity."
)
public interface GroupViewApi {

    // ── Example constants ─────────────────────────────────────────────────────

    String EXAMPLE_TRANSACTION_LIST_SUCCESS =
            "{\"data\":{\"groupId\":\"trip2025\",\"transactionList\":[{\"uuid\":\"e2552fb1-8727-41fd-9ab9-6da937a5125f\"," +
            "\"transactionName\":\"Dinner\",\"transactionDescription\":\"Restaurant bill\",\"transactionType\":\"EXPENSE\"," +
            "\"amount\":23.04,\"paidById\":\"alice\",\"paidForList\":[{\"paidForId\":\"bob\",\"paidForValue\":10.04}," +
            "{\"paidForId\":\"carol\",\"paidForValue\":13.0}],\"splitType\":\"SHARES\",\"timestamp\":null," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}],\"settlementList\":[]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Transaction list fetched successfully\"}]}";

    String EXAMPLE_SETTLEMENT_LIST_INDIVIDUAL =
            "{\"data\":{\"groupId\":\"trip2025\",\"transactionList\":[],\"settlementList\":[" +
            "{\"uuid\":\"371a0832-9bc8-4405-a7f9-b69877c59a49\",\"transactionType\":\"SETTLEMENT\"," +
            "\"paidById\":\"bob\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}]," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}," +
            "{\"uuid\":\"80f772d8-13e4-4f16-a5f1-7b0b8c5a67c9\",\"transactionType\":\"SETTLEMENT\"," +
            "\"paidById\":\"carol\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}]," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Settlement list fetched successfully\"}]}";

    String EXAMPLE_SETTLEMENT_LIST_FAMILY =
            "{\"data\":{\"groupId\":\"trip2025\",\"transactionList\":[],\"settlementList\":[" +
            "{\"uuid\":\"def-567\",\"transactionType\":\"SETTLEMENT\",\"paidById\":\"familyF\"," +
            "\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":4.0}]," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Settlement list fetched successfully\"}]}";

    String EXAMPLE_INSERT_SUCCESS_EQUAL =
            "{\"data\":{\"groupId\":\"trip2025\",\"transactionList\":[{\"uuid\":\"abc-123\"," +
            "\"transactionName\":\"Dinner\",\"transactionType\":\"EXPENSE\",\"amount\":6.0," +
            "\"paidById\":\"alice\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}," +
            "{\"paidForId\":\"bob\",\"paidForValue\":2.0},{\"paidForId\":\"carol\",\"paidForValue\":2.0}]," +
            "\"splitType\":\"EQUAL\",\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]," +
            "\"settlementList\":[{\"uuid\":\"def-456\",\"transactionType\":\"SETTLEMENT\",\"paidById\":\"bob\"," +
            "\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}],\"groupId\":\"trip2025\"," +
            "\"transactionStatus\":\"INCOMPLETE\"},{\"uuid\":\"ghi-789\",\"transactionType\":\"SETTLEMENT\"," +
            "\"paidById\":\"carol\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}]," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Transaction inserted successfully\"}]}";

    String EXAMPLE_INSERT_SUCCESS_FAMILY =
            "{\"data\":{\"groupId\":\"trip2025\",\"transactionList\":[{\"uuid\":\"abc-234\"," +
            "\"transactionName\":\"Hotel\",\"transactionType\":\"EXPENSE\",\"amount\":6.0," +
            "\"paidById\":\"alice\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.0}," +
            "{\"paidForId\":\"familyF\",\"paidForValue\":4.0}],\"splitType\":\"AMOUNT\"," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]," +
            "\"settlementList\":[{\"uuid\":\"def-567\",\"transactionType\":\"SETTLEMENT\"," +
            "\"paidById\":\"familyF\",\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":4.0}]," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Transaction inserted successfully\"}]}";

    String EXAMPLE_FAILURE =
            "{\"data\":null,\"responseStatus\":\"FAILURE\"," +
            "\"messages\":[{\"messageType\":\"ERROR\",\"message\":\"Operation failed\"}]}";

    String EXAMPLE_VALIDATION_ERROR =
            "{\"timestamp\":\"2026-03-28T10:00:00.000+00:00\",\"status\":400," +
            "\"error\":\"Bad Request\",\"path\":\"/group/trip2025/insert-transaction\"}";

    String EXAMPLE_REQUEST_EQUAL =
            "{\"transactionName\":\"Dinner\",\"transactionDescription\":\"Restaurant bill split equally\"," +
            "\"transactionType\":\"EXPENSE\",\"amount\":6.00,\"paidById\":\"alice\"," +
            "\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.00}," +
            "{\"paidForId\":\"bob\",\"paidForValue\":2.00},{\"paidForId\":\"carol\",\"paidForValue\":2.00}]," +
            "\"splitType\":\"EQUAL\"}";

    String EXAMPLE_REQUEST_FAMILY =
            "{\"transactionName\":\"Hotel\",\"transactionDescription\":\"3-night stay - B and C are in Family F\"," +
            "\"transactionType\":\"EXPENSE\",\"amount\":6.00,\"paidById\":\"alice\"," +
            "\"paidForList\":[{\"paidForId\":\"alice\",\"paidForValue\":2.00}," +
            "{\"paidForId\":\"familyF\",\"paidForValue\":4.00}],\"splitType\":\"AMOUNT\"}";

    String EXAMPLE_REQUEST_SHARES =
            "{\"transactionName\":\"Groceries\",\"transactionDescription\":\"Monthly groceries\"," +
            "\"transactionType\":\"EXPENSE\",\"amount\":90.00,\"paidById\":\"alice\"," +
            "\"paidForList\":[{\"paidForId\":\"bob\",\"paidForValue\":60.00}," +
            "{\"paidForId\":\"carol\",\"paidForValue\":30.00}],\"splitType\":\"SHARES\"}";

    String EXAMPLE_REQUEST_PERCENTAGE =
            "{\"transactionName\":\"Utilities\",\"transactionDescription\":\"Monthly utility bill\"," +
            "\"transactionType\":\"EXPENSE\",\"amount\":100.00,\"paidById\":\"alice\"," +
            "\"paidForList\":[{\"paidForId\":\"bob\",\"paidForValue\":60.00}," +
            "{\"paidForId\":\"carol\",\"paidForValue\":40.00}],\"splitType\":\"PERCENTAGE\"}";

    // ── Endpoint contracts ────────────────────────────────────────────────────

    /**
     * Retrieves all expense transactions recorded for the specified group.
     *
     * @param groupId the unique identifier of the expense group
     * @return a {@link ResponseStructure} containing the transaction list on success,
     *         or a failure response if retrieval fails
     */
    @Operation(
            summary = "Get all expense transactions for a group",
            description = "Returns all **EXPENSE**-type transactions recorded in the specified group. " +
                          "Each transaction includes who paid, the split breakdown per participant " +
                          "(supporting EQUAL, SHARES, PERCENTAGE, and AMOUNT split types), and the current status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Successful retrieval", value = EXAMPLE_TRANSACTION_LIST_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Failed retrieval", value = EXAMPLE_FAILURE)
                            }))
    })
    ResponseStructure fetchTransactionList(
            @Parameter(description = "Unique identifier of the expense group", example = "trip2025", required = true)
            String groupId);

    /**
     * Retrieves the optimised settlement list for the specified group — the minimum set of
     * transfers required to clear all outstanding balances. Supports the Family feature,
     * where members of the same family appear as a single collective debtor.
     *
     * @param groupId the unique identifier of the expense group
     * @return a {@link ResponseStructure} containing the settlement list on success,
     *         or a failure response if retrieval fails
     */
    @Operation(
            summary = "Get the current settlement list for a group",
            description = "Returns the optimised set of **SETTLEMENT** transactions that will bring all member " +
                          "balances to zero using the minimum number of transfers. Automatically recomputed on " +
                          "each new expense insert.\n\n" +
                          "**Family feature:** Members of a Family sub-group appear as a single debtor " +
                          "(e.g. `familyF`) instead of generating separate records per member."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Settlement list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "individual-settlements", summary = "A paid $6 equally; B and C owe $2 each", value = EXAMPLE_SETTLEMENT_LIST_INDIVIDUAL),
                                    @ExampleObject(name = "family-settlement",      summary = "B and C are Family F; F owes Alice $4 collectively", value = EXAMPLE_SETTLEMENT_LIST_FAMILY),
                                    @ExampleObject(name = "failure",               summary = "Failed retrieval", value = EXAMPLE_FAILURE)
                            }))
    })
    ResponseStructure fetchSettlementList(
            @Parameter(description = "Unique identifier of the expense group", example = "trip2025", required = true)
            String groupId);

    /**
     * Inserts a new expense transaction for the specified group and triggers a recomputation
     * of the settlement list. The response includes the updated expense list and the refreshed
     * settlement list.
     *
     * @param groupId     the unique identifier of the expense group (taken from the URL path)
     * @param transaction the expense details; {@code groupId} is injected from the path and
     *                    must not be provided in the request body
     * @return a {@link ResponseStructure} containing the updated transaction and settlement
     *         lists on success, or a failure response if the insert fails
     */
    @Operation(
            summary = "Add a new expense to a group",
            description = "Inserts a new **EXPENSE** transaction. On success the settlement list is recomputed " +
                          "and persisted. The response includes both the updated expense list and the refreshed " +
                          "settlement list.\n\n" +
                          "`groupId` is taken from the path — do **not** include it in the request body.\n\n" +
                          "**Split types:** `EQUAL` · `SHARES` · `PERCENTAGE` · `AMOUNT`\n\n" +
                          "**Family feature:** Use a family ID as `paidForId` to record a family's collective " +
                          "share — e.g. `familyF → alice: $4` instead of two individual records."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense inserted; settlement list recomputed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "equal-split",  summary = "EQUAL — A pays $6 for A, B, C ($2 each)", value = EXAMPLE_INSERT_SUCCESS_EQUAL),
                                    @ExampleObject(name = "family-split", summary = "AMOUNT — B and C are Family F; F owes Alice $4", value = EXAMPLE_INSERT_SUCCESS_FAMILY),
                                    @ExampleObject(name = "failure",      summary = "Transaction could not be persisted", value = EXAMPLE_FAILURE)
                            })),
            @ApiResponse(responseCode = "400", description = "Validation failure — required fields missing or constraints violated",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "validation-error", summary = "Missing transactionName or amount", value = EXAMPLE_VALIDATION_ERROR)))
    })
    ResponseStructure insertTransaction(
            @Parameter(description = "Unique identifier of the expense group", example = "trip2025", required = true)
            String groupId,
            @RequestBody(
                    description = "Expense details. `groupId` is set from the path and must not be included in the body.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class),
                            examples = {
                                    @ExampleObject(name = "equal-split",      summary = "EQUAL — A pays $6, split equally among A, B, C", value = EXAMPLE_REQUEST_EQUAL),
                                    @ExampleObject(name = "family-split",     summary = "AMOUNT — A pays $6; Family F (B+C) owes $4", value = EXAMPLE_REQUEST_FAMILY),
                                    @ExampleObject(name = "shares-split",     summary = "SHARES — A pays $90; B gets 2 shares, C gets 1", value = EXAMPLE_REQUEST_SHARES),
                                    @ExampleObject(name = "percentage-split", summary = "PERCENTAGE — A pays $100; B=60%, C=40%", value = EXAMPLE_REQUEST_PERCENTAGE)
                            }))
            Transaction transaction);
}
