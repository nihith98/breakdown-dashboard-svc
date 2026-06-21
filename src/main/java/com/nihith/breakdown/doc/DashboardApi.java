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
 * OpenAPI contract for dashboard summary endpoints.
 */
@Tag(name = "Dashboard", description = "APIs for fetching the user's dashboard summary data")
public interface DashboardApi {

    String EXAMPLE_SUMMARY_SUCCESS = "{\"responseStatus\":\"SUCCESS\",\"responseMessage\":\"Dashboard summary fetched\",\"responseObject\":{\"displayName\":\"nihith\",\"youOwe\":-50.00,\"owedToYou\":20.00,\"net\":-30.00,\"recentGroups\":[{\"id\":\"goa-trip-uuid\",\"name\":\"Goa Trip\",\"memberCount\":6,\"expenseCount\":14,\"net\":-42.50,\"isFamily\":false}],\"recentFamilies\":[]}}";
    String EXAMPLE_FAILURE = "{\"responseStatus\":\"FAILURE\",\"responseMessage\":\"Failed to fetch dashboard summary\",\"responseObject\":null}";

    @Operation(
            summary = "Get dashboard summary",
            description = "Returns the authenticated user's dashboard summary including aggregate balances and the 3 most recently active groups."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard summary returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Successful response", value = EXAMPLE_SUMMARY_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Failure response", value = EXAMPLE_FAILURE)
                            }))
    })
    ResponseStructure getDashboardSummary(
            @Parameter(description = "Authenticated user ID forwarded by the API gateway", required = true)
            String userId);
}
