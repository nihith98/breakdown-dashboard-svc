package com.nihith.breakdown.doc;

import com.nihith.breakdown.model.groups.Group;
import com.nihith.breakdown.model.response.ResponseStructure;
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
 * OpenAPI contract for the Group Admin REST endpoints.
 * All Swagger documentation, descriptions, and examples are defined here,
 * keeping GroupAdminRestController free of annotation noise.
 */
@Tag(
        name = "Group Admin",
        description = "Administrative APIs for managing expense groups — create groups, manage members, and manage families."
)
public interface GroupAdminApi {

    // ── Example constants ─────────────────────────────────────────────────────

    String EXAMPLE_CREATE_SUCCESS =
            "{\"data\":{\"groupId\":\"a3f1c2d4-5e6f-7890-abcd-ef1234567890\",\"groupName\":\"Trip 2025\"," +
            "\"personList\":[\"alice\",\"bob\",\"carol\"],\"familyList\":[]}," +
            "\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Group created successfully\"}]}";

    String EXAMPLE_CREATE_FAILURE =
            "{\"data\":null,\"responseStatus\":\"FAILURE\"," +
            "\"messages\":[{\"messageType\":\"ERROR\",\"message\":\"Internal server error\"}]}";

    String EXAMPLE_CREATE_REQUEST =
            "{\"groupName\":\"Trip 2025\",\"personList\":[\"alice\",\"bob\",\"carol\"],\"familyList\":[]}";

    String EXAMPLE_ADD_MEMBERS_SUCCESS =
            "{\"data\":null,\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Members added successfully\"}]}";

    String EXAMPLE_ADD_MEMBERS_FAILURE =
            "{\"data\":null,\"responseStatus\":\"FAILURE\"," +
            "\"messages\":[{\"messageType\":\"ERROR\",\"message\":\"Internal server error\"}]}";

    String EXAMPLE_ADD_MEMBERS_REQUEST =
            "{\"personList\":[\"dave\",\"eve\"],\"familyList\":[]}";  

    String EXAMPLE_ADD_MEMBERS_VALIDATION_ERROR =
            "{\"timestamp\":\"2026-03-29T10:00:00.000+00:00\",\"status\":400,\"error\":\"Bad Request\"," +
            "\"message\":\"Person dave is already a member of the group\"}";

    String EXAMPLE_MANAGE_FAMILIES_REQUEST =
            "{\"familyList\":[" +
            "{\"familyName\":\"Trip Crew\",\"personIds\":[\"alice\",\"bob\"]}" +
            ",{\"familyId\":\"fam-001\",\"personIds\":[\"carol\",\"dave\"]}" +
            ",{\"familyId\":\"fam-002\",\"personIds\":[]}" +
            "]}";

    String EXAMPLE_MANAGE_FAMILIES_SUCCESS =
            "{\"data\":null,\"responseStatus\":\"SUCCESS\",\"messages\":[{\"messageType\":\"INFORMATION\"," +
            "\"message\":\"Families Updated Successfully\"}]}";

    String EXAMPLE_MANAGE_FAMILIES_FAILURE =
            "{\"data\":null,\"responseStatus\":\"FAILURE\"," +
            "\"messages\":[{\"messageType\":\"ERROR\",\"message\":\"Failed to Update Families\"}]}";

    String EXAMPLE_MANAGE_FAMILIES_VALIDATION_ERROR =
            "{\"timestamp\":\"2026-03-29T10:00:00.000+00:00\",\"status\":400,\"error\":\"Bad Request\"," +
            "\"message\":\"Person alice is already a member of family fam-001\"}";

    // ── Endpoint contracts ────────────────────────────────────────────────────

    /**
     * Creates a new expense group and persists it in the database.
     * A unique {@code groupId} is generated server-side; any {@code groupId} supplied
     * in the request body is ignored.
     *
     * @param request the group details to persist
     * @return a {@link ResponseStructure} indicating success or failure
     */
    @Operation(
            summary = "Create a new expense group",
            description = "Persists a new group with the provided name, member list, and optional family definitions. " +
                          "The `groupId` is auto-generated — do not include it in the request body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group created successfully or creation failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Group created", value = EXAMPLE_CREATE_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Creation failed", value = EXAMPLE_CREATE_FAILURE)
                            }))
    })
    ResponseStructure createGroup(
            @RequestBody(
                    description = "Group details. `groupId` must not be included — it is auto-generated.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class),
                            examples = @ExampleObject(name = "example", summary = "New group", value = EXAMPLE_CREATE_REQUEST)))
            Group request);

    /**
     * Adds new members to an existing group.
     *
     * @param groupId the unique identifier of the group to update
     * @param request a {@link Group} object containing the updated member list and family definitions
     * @return a {@link ResponseStructure} indicating success or failure
     */
    @Operation(
            summary = "Add members to an existing group",
            description = "Appends the members provided in the request body to the specified group. " +
                          "**Validation (HTTP 400):** no supplied person ID may already be a member of the group."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Members added successfully or operation failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Members added", value = EXAMPLE_ADD_MEMBERS_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Operation failed", value = EXAMPLE_ADD_MEMBERS_FAILURE)
                            })),
            @ApiResponse(responseCode = "400", description = "Validation error — person already a member of the group",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "validation-error", summary = "Duplicate member",
                                    value = EXAMPLE_ADD_MEMBERS_VALIDATION_ERROR)))
    })
    ResponseStructure addMembers(
            @Parameter(description = "Unique identifier of the group", example = "a3f1c2d4-5e6f-7890-abcd-ef1234567890", required = true)
            String groupId,
            @RequestBody(
                    description = "Members to add. Only `personList` and `familyList` are used.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class),
                            examples = @ExampleObject(name = "example", summary = "New members", value = EXAMPLE_ADD_MEMBERS_REQUEST)))
            Group request);

    /**
     * Processes a list of family operations for the specified group in a single request.
     * Supports creating, updating, and deleting families simultaneously.
     * Validation failures return HTTP 400.
     *
     * <p><b>Impact on Settlements:</b> Family definitions directly affect how the settlement engine
     * computes outstanding balances. When families are added, updated, or removed, future settlement
     * computations will reflect the new family structure. Existing (previously computed) settlements
     * are not retroactively recalculated; only new expenses trigger settlement recomputation with the
     * updated family definitions.</p>
     *
     * @param groupId the unique identifier of the group to update
     * @param request a {@link Group} object whose {@code familyList} contains the operations to perform
     * @return a {@link ResponseStructure} indicating success or failure
     */
    @Operation(
            summary = "Create, update, or delete families in a group",
            description = "Processes each entry in `familyList` as a single operation:\n\n" +
                          "- **No `familyId`** → create a new family (UUID auto-generated).\n" +
                          "- **`familyId` + `personIds` non-empty** → replace the family's member list.\n" +
                          "- **`familyId` + `personIds` empty/null** → delete the family.\n\n" +
                          "**Settlement Impact:** Family definitions control settlement computation boundaries. " +
                          "When families exist, settlements are computed at the family level with aggregated member balances. " +
                          "When families are deleted or modified, future settlements reflect the new structure. " +
                          "Existing settlements are preserved; only new expenses trigger recomputation.\n\n" +
                          "**Validation (HTTP 400):** every `personId` must be a group member " +
                          "and may only belong to one family at a time."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Families updated successfully or server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStructure.class),
                            examples = {
                                    @ExampleObject(name = "success", summary = "Families updated", value = EXAMPLE_MANAGE_FAMILIES_SUCCESS),
                                    @ExampleObject(name = "failure", summary = "Server error", value = EXAMPLE_MANAGE_FAMILIES_FAILURE)
                            })),
            @ApiResponse(responseCode = "400", description = "Validation error — person not in group or already in another family",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "validation-error", summary = "Validation failed",
                                    value = EXAMPLE_MANAGE_FAMILIES_VALIDATION_ERROR)))
    })
    ResponseStructure manageFamilies(
            @Parameter(description = "Unique identifier of the group", example = "a3f1c2d4-5e6f-7890-abcd-ef1234567890", required = true)
            String groupId,
            @RequestBody(
                    description = "Family operations. Omit `familyId` to create; include for update or delete.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Group.class),
                            examples = @ExampleObject(name = "example", summary = "Mixed create/update/delete",
                                    value = EXAMPLE_MANAGE_FAMILIES_REQUEST)))
            Group request);

}
