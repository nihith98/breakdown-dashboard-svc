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

@Tag(
    name = "Group List",
    description = "APIs for retrieving and managing group list operations"
)
public interface GroupListApi {

    String EXAMPLE_LIST_SUCCESS = """
        {
          "status": "SUCCESS",
          "responseStatus": "SUCCESS",
          "message": "Successfully Fetched Group List",
          "messageType": "INFORMATION",
          "data": {
            "groups": [
              {
                "groupId": "group1",
                "groupName": "Trip to Europe",
                "memberCount": 3,
                "lastUpdatedTimestamp": 1717779600000,
                "settlementAmount": 150.50,
                "transactionCount": 5
              },
              {
                "groupId": "group2",
                "groupName": "Roommates",
                "memberCount": 2,
                "lastUpdatedTimestamp": 1717865000000,
                "settlementAmount": -75.25,
                "transactionCount": 8
              }
            ],
            "totalExpenses": 13,
            "requireSettling": 1,
            "effectiveAmount": 75.25
          }
        }
        """;

    String EXAMPLE_LIST_FAILURE = """
        {
          "status": "FAILURE",
          "responseStatus": "FAILURE",
          "message": "Failed to Fetch Group List",
          "messageType": "ERROR",
          "data": null
        }
        """;

    @Operation(
        summary = "Retrieve list of all groups",
        description = "Fetches a list of all groups with member counts, settlement amounts, and transaction counts. " +
                      "Includes aggregate statistics such as total expenses, number of groups requiring settlement, " +
                      "and the effective (net) amount across all groups."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of groups retrieved successfully or operation failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseStructure.class),
                examples = {
                    @ExampleObject(name = "success", summary = "Groups retrieved successfully", value = EXAMPLE_LIST_SUCCESS),
                    @ExampleObject(name = "failure", summary = "Failed to retrieve groups", value = EXAMPLE_LIST_FAILURE)
                }))
    })
    ResponseStructure getGroupList(
        @Parameter(description = "The unique identifier of the user making the request", required = true)
        String userId);
}
