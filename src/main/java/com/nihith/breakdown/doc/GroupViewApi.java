package com.nihith.breakdown.doc;

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
            "\"familyId\":\"familyF\"," +
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
            "\"familyId\":\"familyF\"," +
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
     * <p>Returns a complete list of all EXPENSE-type transactions registered for the group,
     * including transaction details (payer, split breakdown per participant, split type, status, etc.).
     * The response payload ({@code data.transactionList}) contains the full list of transactions
     * as an array.</p>
     *
     * @param groupId the unique identifier of the expense group
     * @return a {@link ResponseStructure} containing the transaction list in {@code data.transactionList}
     *         on success, or a failure response if retrieval fails
     */
    @Operation(
            summary = "Get all expense transactions for a group",
            description = "Returns all **EXPENSE**-type transactions recorded in the specified group. " +
                          "Each transaction includes who paid, the split breakdown per participant " +
                          "(supporting EQUAL, SHARES, PERCENTAGE, and AMOUNT split types), and the current status.\n\n" +
                          "**Response Payload:** The `data.transactionList` field contains the array of transactions. " +
                          "Use this to display the expense history for the group."
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
     * where members of the same family settle collectively at the family level.
     *
     * <p><b>Family-Level Settlements:</b> When a group contains families, settlement transactions
     * are computed at the family level. Each settlement transaction includes a {@code familyId} field
     * identifying which family owes whom, enabling users to distinguish family settlements from
     * individual settlements at a glance.</p>
     *
     * <p><b>Mixed Groups:</b> Groups containing both families and individual members are fully supported.
     * Individual members not part of any family are treated as single-member families for calculation
     * consistency. Settlement transactions for such individuals will have {@code familyId} as null.</p>
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
                          "**Family-Level Settlements:** When families exist in the group, balances are aggregated " +
                          "at the family level and settlements are computed between families. Each settlement includes " +
                          "a `familyId` field indicating which family owes whom.\n\n" +
                          "**Mixed Groups:** Groups can contain both families and individual members. Individual members " +
                          "settle as single-member families; families settle collectively with `familyId` marked."
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
     * <p><b>Settlement Computation:</b> On successful insert, the settlement list is recomputed
     * using the optimized two-pointer algorithm to find the minimum number of transfers needed
     * to clear all outstanding balances. A critical bug fix in the settlement calculation ensures
     * correct payer/receiver ID mapping under all balance comparison scenarios.</p>
     *
     * <p><b>Family-Level Settlements:</b> When the group contains families, the settlement engine
     * automatically routes to family-level settlement computation. Each family member's individual
     * balance is aggregated to the family level, and settlements are computed between families as
     * collective entities. Settlement transactions include the {@code familyId} field to identify
     * which family owns a debt.</p>
     *
     * <p><b>Mixed Groups:</b> Groups containing both families and individual members are fully supported.
     * Individual members not in any family are treated as single-member families and settle individually,
     * with {@code familyId} as null in their settlement records.</p>
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
                          "**Settlement Algorithm:** Uses an optimized two-pointer algorithm on sorted member balances " +
                          "to compute the minimum number of transfers. A critical bug fix ensures correct payer and receiver " +
                          "identification in all balance comparison scenarios.\n\n" +
                          "**Family-Level Settlements:** When families exist in the group, balances are aggregated to family level. " +
                          "Each family member's balance is summed, and settlements are computed between families as single entities. " +
                          "Settlement transactions are marked with `familyId` to identify the settling family.\n\n" +
                          "**Mixed Group Support:** Groups can contain both families and individual members. Individuals not in families " +
                          "settle independently; families settle collectively. Both settlement types coexist in the response.\n\n" +
                          "**Split types:** `EQUAL` · `SHARES` · `PERCENTAGE` · `AMOUNT`\n\n" +
                          "**Family feature:** Use a family ID as `paidForId` to record a family's collective " +
                          "share — e.g. `familyF → alice: $4` instead of two individual records. The family will then settle the debt " +
                          "collectively with all its members' balances aggregated."
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
