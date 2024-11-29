# User Service API Documentation

## Introduction

This project uses **Swagger** to generate interactive API documentation for the User Service
Swagger provides an interface to test our API endpoints.

## What is Swagger?

Swagger is a set of open-source tools built around the OpenAPI Specification that can help you design,
build, document, and consume REST APIs.

## How to Access Swagger UI

After running the application, you can access the Swagger UI at:
http://localhost:8080/api/v1/swagger-ui/index.html

*Note:* If you have modified the server port or context path, adjust the URL (currently set to **api/v1**)

**Please, update this file if context path changed**

## How to improve Swagger documentation?

To improve the Swagger UI try the following improvements (based on my code from RecommendationRequestController:

1. Use Swagger Annotations (@Tag)

```
@RestController
@RequestMapping("/recommendation-request")
@Tag(name = "Recommendation Requests", description = "Operations related to recommendation requests")
@RequiredArgsConstructor
public class RecommendationRequestController {
    // some code
}
```

2. Use @Operation annotation to add summaries and descriptions to your API methods.
3. Use @ApiResponses to document possible responses.

```
@PostMapping
@Operation(
    summary = "Create a new recommendation request",
    description = "Allows a user to request a recommendation."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Recommendation request created successfully",
        content = @Content(schema = @Schema(implementation = RecommendationRequestDto.class))),
    @ApiResponse(responseCode = "400", description = "Invalid input data")
})
public ResponseEntity<RecommendationRequestDto> requestRecommendation(
        @Valid @RequestBody RecommendationRequestDto recommendationRequestDto) {
    // some code
}
```

4. Use @Parameter to describe method parameters, to annotate DTO.

```
public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(
        @Parameter(description = "ID of the recommendation request", example = "1")
        @PathVariable @NotNull @Min(1) Long id) {
    // some code
}
```

5. You can also annotate DTO with @Schema"

```
public class RecommendationRequestDto {

    @Schema(description = "Unique identifier of the recommendation request", example = "1")
    private Long id;
}
```

**Keep coding!**
