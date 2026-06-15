package com.nihith.breakdown.doc;

import com.nihith.breakdown.model.response.ResponseStructure;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI contract for the RESTful Group resource endpoints under {@code /groups}.
 * All Swagger documentation, descriptions, and examples are defined here,
 * keeping {@code GroupRestController} free of annotation noise.
 */
@Tag(
        name = "Group",
        description = "RESTful APIs for accessing group resources — retrieve group details and the expense list for a group."
)
public interface GroupApi {

    // ── Example constants ─────────────────────────────────────────────────────

    String EXAMPLE_GROUP_DETAILS_SUCCESS =
            "{\"payload\":{\"groupId\":\"trip2025\",\"groupName\":\"Europe Trip\"," +
            "\"groupDescription\":\"Summer 2025 Europe trip expenses\"," +
            "\"createdById\":\"alice\",\"personList\":[\"alice\",\"bob\",\"carol\"]," +
            "\"familyList\":[]},\"status\":\"SUCCESS\"," +
            "\"messages\":{\"informationMessages\":[\"Successfully Fetched Group Details\"]}}";

    String EXAMPLE_EXPENSE_LIST_SUCCESS =
            "{\"payload\":{\"groupId\":\"trip2025\",\"transactionList\":[" +
            "{\"uuid\":\"e2552fb1-8727-41fd-9ab9-6da937a5125f\",\"transactionName\":\"Dinner\"," +
            "\"transactionType\":\"EXPENSE\",\"amount\":23.04,\"paidById\":\"alice\"," +
            "\"paidForList\":[{\"paidForId\":\"bob\",\"paidForValue\":10.04}," +
            "{\"paidForId\":\"carol\",\"paidForValue\":13.0}],\"splitType\":\"SHARES\"," +
            "\"groupId\":\"trip2025\",\"transactionStatus\":\"INCOMPLETE\"}],\"settlementList\":[]}," +
            "\"status\":\"SUCCESS\"," +
            "\"messages\":{\"informationMessages\":[\"Successfully Fetched Transaction List\"]}}";

    String EXAMPLE_FAILURE =
            "{\"payload\":null,\"status\":\"FAILURE\"," +
            "\"messages\":{\"errorMessages\":[\"Internal Server Error\"]}}";

    // ── Endpoint contracts ────────────────────────────────────────────────────

    /**
     * Retrieves the full details of the group identified by the given ID.
     *
     * @param groupId the unique identifier of the group
     * @return a {@link ResponseStructure} containing the {@link com.nihith.breakdown.model.groups.Group}
     *         object in {@code payload} on success, or a failure response if retrieval fails
     */
    @Operation(
            summary = "Get group details",
            description = "Returns the full details of the specified group including its name, description, " +
                          "member list, family list, and metadata."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Successful retrieval", value = EXAMPLE_GROUP_DETAILS_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Failed retrieval", value = EXAMPLE_FAILURE)
                            }))
    })
    ResponseStructure fetchGroupDetails(
            @Parameter(description = "Unique identifier of the group", example = "trip2025", required = true)
            String groupId);

    /**
     * Retrieves all expense transactions recorded for the specified group.
     *
     * @param groupId the unique identifier of the group
     * @return a {@link ResponseStructure} containing the transaction list in {@code payload.transactionList}
     *         on success, or a failure response if retrieval fails
     */
    @Operation(
            summary = "Get expense list for a group",
            description = "Returns all **EXPENSE**-type transactions recorded in the specified group. " +
                          "Each transaction includes who paid, the split breakdown per participant, and the current status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Successful retrieval", value = EXAMPLE_EXPENSE_LIST_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Failed retrieval", value = EXAMPLE_FAILURE)
                            }))
    })
    ResponseStructure fetchExpenseList(
            @Parameter(description = "Unique identifier of the group", example = "trip2025", required = true)
            String groupId);
}
