package stud.ntnu.backend.controller.gamification.quiz;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import stud.ntnu.backend.dto.quiz.CreateQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateQuizQuestionDto;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizPreviewDto;
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.gamification.quiz.QuizService;
import stud.ntnu.backend.service.user.UserService;

/**
 * Controller responsible for managing quiz-related operations including creation, modification, and
 * user interactions with quizzes. Provides separate endpoints for admin and user operations.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Quizzes", description = "Operations related to quiz management and user interactions")
public class QuizController {

  private final QuizService quizService;
  private final UserService userService;

  /**
   * Constructs a new QuizController with the required services.
   *
   * @param quizService service handling quiz-related operations
   * @param userService service handling user-related operations
   */
  public QuizController(QuizService quizService, UserService userService) {
    this.quizService = quizService;
    this.userService = userService;
  }

  // -------------------- ADMIN ENDPOINTS --------------------

  /**
   * Creates a new empty quiz. This endpoint is restricted to admin users only. The quiz is created
   * with the provided name, description, and initial status.
   *
   * @param createQuizDto DTO containing the quiz information (name, description, status)
   * @param principal     the authenticated user's principal
   * @return ResponseEntity containing the created quiz ID if successful, or an error message
   * @throws IllegalArgumentException if the quiz data is invalid
   */
  @Operation(summary = "Create quiz", description = "Creates a new empty quiz. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quiz created successfully", 
          content = @Content(schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can create quizzes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid quiz data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/quizzes/admin")
  public ResponseEntity<?> createQuiz(@RequestBody CreateQuizDto createQuizDto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      Integer userId = userService.getUserIdByEmail(principal.getName());
      Long quizId = quizService.createQuiz(createQuizDto, userId.longValue());
      return ResponseEntity.ok(Collections.singletonMap("quizId", quizId));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Deletes a quiz and all its associated questions and answers. This endpoint is restricted to
   * admin users only.
   *
   * @param quizId    the ID of the quiz to delete
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the quiz doesn't exist
   */
  @Operation(summary = "Delete quiz", description = "Deletes a quiz and all its associated questions and answers. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quiz deleted successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can delete quizzes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @DeleteMapping("/quizzes/admin/{quiz_id}")
  public ResponseEntity<?> deleteQuiz(@PathVariable("quiz_id") Long quizId, Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.deleteQuiz(quizId);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }

  /**
   * Archives a quiz, making it inaccessible to regular users but preserving its data. This endpoint
   * is restricted to admin users only.
   *
   * @param id        the ID of the quiz to archive
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the quiz doesn't exist
   */
  @Operation(summary = "Archive quiz", description = "Archives a quiz, making it inaccessible to regular users but preserving its data. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quiz archived successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can archive quizzes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/quizzes/admin/{id}/archive")
  public ResponseEntity<?> archiveQuiz(@PathVariable Long id, Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.updateQuizStatus(id, "archived");
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Unarchives a previously archived quiz, making it accessible to users again. This endpoint is
   * restricted to admin users only.
   *
   * @param id        the ID of the quiz to unarchive
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the quiz doesn't exist
   */
  @Operation(summary = "Unarchive quiz", description = "Unarchives a previously archived quiz, making it accessible to users again. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quiz unarchived successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can unarchive quizzes", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/quizzes/admin/{id}/unarchive")
  public ResponseEntity<?> unArchiveQuiz(@PathVariable Long id, Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.updateQuizStatus(id, "active");
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Creates a new quiz question for an existing quiz. This endpoint is restricted to admin users
   * only.
   *
   * @param dto       DTO containing the question data (quiz ID, question text, type, etc.)
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing the created question ID if successful, or an error message
   * @throws IllegalArgumentException if the question data is invalid or the quiz doesn't exist
   */
  @Operation(summary = "Create quiz question", description = "Creates a new quiz question for an existing quiz. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Question created successfully", 
          content = @Content(schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can create questions", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid question data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/quizzes/admin/questions")
  public ResponseEntity<?> saveQuizQuestion(@RequestBody CreateQuizQuestionDto dto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      Long questionId = quizService.saveQuizQuestion(dto);

      return ResponseEntity.ok(Map.of("id", questionId));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing quiz question. This endpoint is restricted to admin users only.
   *
   * @param questionId the ID of the question to update
   * @param dto        DTO containing the updated question data
   * @param principal  the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the question doesn't exist or the data is invalid
   */
  @Operation(summary = "Update quiz question", description = "Updates an existing quiz question. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Question updated successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can update questions", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid update data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/quizzes/admin/questions/{question_id}")
  public ResponseEntity<?> updateQuizQuestion(
      @PathVariable("question_id") Long questionId,
      @RequestBody CreateQuizQuestionDto dto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.updateQuizQuestion(questionId, dto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Updates an existing quiz answer. This endpoint is restricted to admin users only.
   *
   * @param answerId  the ID of the answer to update
   * @param dto       DTO containing the updated answer data
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the answer doesn't exist or the data is invalid
   */
  @Operation(summary = "Update quiz answer", description = "Updates an existing quiz answer. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can update answers", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid update data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PatchMapping("/quizzes/admin/answers/{answer_id}")
  public ResponseEntity<?> updateQuizAnswer(
      @PathVariable("answer_id") Long answerId,
      @RequestBody CreateQuizAnswerDto dto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.updateQuizAnswer(answerId, dto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Creates a new answer option for a quiz question. This endpoint is restricted to admin users
   * only.
   *
   * @param dto       DTO containing the answer data (question ID, answer text, isCorrect flag)
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the answer data is invalid or the question doesn't exist
   */
  @Operation(summary = "Create quiz answer", description = "Creates a new answer option for a quiz question. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Answer created successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can create answers", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid answer data", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/quizzes/admin/answers")
  public ResponseEntity<?> saveQuizAnswer(@RequestBody CreateQuizAnswerDto dto,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.saveQuizAnswer(dto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves all correct answers for a specific quiz question. This endpoint is restricted to
   * admin users only.
   *
   * @param questionId the ID of the question to get answers for
   * @return ResponseEntity containing a list of correct answers if successful, or an error message
   * @throws IllegalArgumentException if the question doesn't exist
   */
  @Operation(summary = "Get correct answers", description = "Retrieves all correct answers for a specific quiz question. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved correct answers", 
          content = @Content(schema = @Schema(implementation = QuizAnswerDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - question not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/quizzes/admin/{question_id}/answers/correct")
  public ResponseEntity<?> getCorrectQuizAnswersByQuestionId(
      @PathVariable("question_id") Long questionId) {
    try {
      List<QuizAnswerDto> correctAnswers =
          quizService.getAnswersByQuestionIdAdmin(questionId);
      return ResponseEntity.ok(correctAnswers);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Deletes a quiz question and all its associated answers. This endpoint is restricted to admin
   * users only.
   *
   * @param questionId the ID of the question to delete
   * @param principal  the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the question doesn't exist
   */
  @Operation(summary = "Delete quiz question", description = "Deletes a quiz question and all its associated answers. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can delete questions", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - question not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @DeleteMapping("/quizzes/admin/questions/{question_id}")
  public ResponseEntity<?> deleteQuizQuestion(@PathVariable("question_id") Long questionId,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.deleteQuizQuestion(questionId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Deletes a quiz answer option. This endpoint is restricted to admin users only.
   *
   * @param answerId  the ID of the answer to delete
   * @param principal the authenticated user's principal
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the answer doesn't exist
   */
  @Operation(summary = "Delete quiz answer", description = "Deletes a quiz answer option. This endpoint is restricted to admin users only.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Answer deleted successfully"),
      @ApiResponse(responseCode = "403", description = "Access forbidden - only administrators can delete answers", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "400", description = "Bad request - answer not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @DeleteMapping("/quizzes/admin/answers/{answer_id}")
  public ResponseEntity<?> deleteQuizAnswer(@PathVariable("answer_id") Long answerId,
      Principal principal) {
    try {
      if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
        return ResponseEntity.status(403).body("Forbidden");
      }
      quizService.deleteQuizAnswer(answerId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // -------------------- USER ENDPOINTS --------------------

  /**
   * Retrieves all questions for a specific quiz. This endpoint is accessible to all authenticated
   * users.
   *
   * @param quizId the ID of the quiz to retrieve questions for
   * @return ResponseEntity containing a list of quiz questions with their details
   * @throws IllegalArgumentException if the quiz doesn't exist
   */
  @Operation(summary = "Get quiz questions", description = "Retrieves all questions for a specific quiz. This endpoint is accessible to all authenticated users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz questions", 
          content = @Content(schema = @Schema(implementation = QuizQuestionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found")
  })
  @GetMapping("/quizzes/user/{quiz_id}/questions")
  public ResponseEntity<List<QuizQuestionResponseDto>> getQuizQuestionsByQuizId(
      @PathVariable("quiz_id") Long quizId) {
    List<QuizQuestionResponseDto> questions = quizService.getQuestionsById(quizId);
    return ResponseEntity.ok(questions);
  }

  /**
   * Retrieves all possible answers for a specific quiz question. This endpoint is accessible to all
   * authenticated users.
   *
   * @param questionId the ID of the question to retrieve answers for
   * @return ResponseEntity containing a list of possible answers for the question
   * @throws IllegalArgumentException if the question doesn't exist
   */
  @Operation(summary = "Get question answers", description = "Retrieves all possible answers for a specific quiz question. This endpoint is accessible to all authenticated users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved question answers", 
          content = @Content(schema = @Schema(implementation = QuizAnswerResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - question not found")
  })
  @GetMapping("/quizzes/user/questions/{question_id}/answers")
  public ResponseEntity<List<QuizAnswerResponseDto>> getAnswersByQuestionId(
      @PathVariable("question_id") Long questionId) {
    List<QuizAnswerResponseDto> answers = quizService.getAnswersByQuestionId(questionId);
    return ResponseEntity.ok(answers);
  }

  /**
   * Retrieves a paginated list of all active quizzes. This endpoint is accessible to all
   * authenticated users.
   *
   * @param pageable pagination information including page number, size, and sorting
   * @return ResponseEntity containing a page of active quiz previews
   */
  @Operation(summary = "Get active quizzes", description = "Retrieves a paginated list of all active quizzes. This endpoint is accessible to all authenticated users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved active quizzes", 
          content = @Content(schema = @Schema(implementation = QuizPreviewDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid pagination parameters")
  })
  @GetMapping("/quizzes/user/all/previews/active")
  public ResponseEntity<Page<QuizPreviewDto>> getAllActiveQuizzes(Pageable pageable) {
    try {
      return ResponseEntity.ok(quizService.getAllActiveQuizzes(pageable));
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a paginated list of all archived quizzes. This endpoint is accessible to all
   * authenticated users.
   *
   * @param pageable pagination information including page number, size, and sorting
   * @return ResponseEntity containing a page of archived quiz previews
   */
  @Operation(summary = "Get archived quizzes", description = "Retrieves a paginated list of all archived quizzes. This endpoint is accessible to all authenticated users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved archived quizzes", 
          content = @Content(schema = @Schema(implementation = QuizPreviewDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - invalid pagination parameters")
  })
  @GetMapping("/quizzes/user/all/previews/archived")
  public ResponseEntity<Page<QuizPreviewDto>> getAllArchivedQuizzes(Pageable pageable) {
    try {
      Page<QuizPreviewDto> archivedQuizzes = quizService.getAllArchivedQuizzes(pageable);
      return ResponseEntity.ok(archivedQuizzes);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Creates a new quiz attempt for the authenticated user. This represents the start of a user
   * taking a quiz.
   *
   * @param quizId    the ID of the quiz to attempt
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing the created attempt ID if successful, or an error message
   * @throws IllegalArgumentException if the quiz doesn't exist or user has exceeded attempt limits
   */
  @Operation(summary = "Create quiz attempt", description = "Creates a new quiz attempt for the authenticated user. This represents the start of a user taking a quiz.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quiz attempt created successfully", 
          content = @Content(schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found or attempt limit exceeded", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/quizzes/user/{quiz_id}/attempts")
  public ResponseEntity<?> createUserQuizAttempt(@PathVariable("quiz_id") Long quizId,
      Principal principal) {
    try {
      // Retrieve the user ID from the authenticated user's email
      Integer userId = userService.getUserIdByEmail(principal.getName());

      // Call the service method to create the quiz attempt and get the attempt ID
      Long attemptId = quizService.createUserQuizAttempt(quizId, userId);

      return ResponseEntity.ok(Collections.singletonMap("attemptId", attemptId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }

  /**
   * Records a user's answer to a quiz question during an attempt.
   *
   * @param dto DTO containing the answer data (attempt ID, question ID, selected answers)
   * @return ResponseEntity with 200 OK if successful, or an error message
   * @throws IllegalArgumentException if the attempt or question doesn't exist
   */
  @Operation(summary = "Submit quiz answer", description = "Records a user's answer to a quiz question during an attempt.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Answer recorded successfully"),
      @ApiResponse(responseCode = "400", description = "Bad request - attempt or question not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @PostMapping("/quizzes/user/attempts/answer")
  public ResponseEntity<?> createUserQuizAnswer(@RequestBody CreateUserQuizAnswerDto dto) {
    try {
      // Call the service method to create the user quiz answer
      quizService.createUserQuizAnswer(dto);

      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }

  /**
   * Retrieves a paginated list of quiz attempts for a specific quiz by the authenticated user.
   *
   * @param quizId    the ID of the quiz to get attempts for
   * @param principal the authenticated user's principal
   * @param pageable  pagination information including page number, size, and sorting
   * @return ResponseEntity containing a page of quiz attempt summaries
   * @throws IllegalArgumentException if the quiz doesn't exist
   */
  @Operation(summary = "Get quiz attempts", description = "Retrieves a paginated list of quiz attempts for a specific quiz by the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz attempts", 
          content = @Content(schema = @Schema(implementation = QuizAttemptSummaryDto.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/quizzes/user/attempts/{quiz_id}")
  public ResponseEntity<Page<QuizAttemptSummaryDto>> getQuizAttemptsByQuizId(
      @PathVariable("quiz_id") Long quizId,
      Principal principal,
      Pageable pageable) {
    try {
      // Retrieve the user ID from the authenticated user's email
      Integer userId = userService.getUserIdByEmail(principal.getName());

      // Fetch the paginated quiz attempts for the user
      Page<QuizAttemptSummaryDto> attempts =
          quizService.getQuizAttemptsByQuizId(quizId, userId, pageable);

      return ResponseEntity.ok(attempts);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Page.empty());
    }
  }

  /**
   * Retrieves the total number of correct answers for a specific quiz attempt.
   *
   * @param attemptId the ID of the quiz attempt to get results for
   * @return ResponseEntity containing the count of correct answers if successful, or an error
   * message
   * @throws IllegalArgumentException if the attempt doesn't exist
   */
  @Operation(summary = "Get correct answers count", description = "Retrieves the total number of correct answers for a specific quiz attempt.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved correct answers count", 
          content = @Content(schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - attempt not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/quizzes/user/attempts/{attempt_id}/correct-count")
  public ResponseEntity<?> getTotalCorrectAnswers(@PathVariable("attempt_id") Long attemptId) {
    try {
      // Call the service method to get the total correct answers
      int correctAnswers = quizService.getTotalCorrectAnswers(attemptId);
      return ResponseEntity.ok(Collections.singletonMap("correctAnswers", correctAnswers));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }

  /**
   * Retrieves the most recent quiz attempt for the authenticated user.
   *
   * @param quizId    the ID of the quiz to get the latest attempt for
   * @param principal the authenticated user's principal
   * @return ResponseEntity containing the latest attempt summary if found, or an error message
   * @throws IllegalArgumentException if the quiz doesn't exist or no attempts are found
   */
  @Operation(summary = "Get latest quiz attempt", description = "Retrieves the most recent quiz attempt for the authenticated user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved latest attempt", 
          content = @Content(schema = @Schema(implementation = QuizAttemptSummaryDto.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found or no attempts", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/quizzes/user/{quiz_id}/attempts/latest")
  public ResponseEntity<?> getLatestQuizAttempt(@PathVariable("quiz_id") Long quizId,
      Principal principal) {
    try {
      Integer userId = userService.getUserIdByEmail(principal.getName());
      QuizAttemptSummaryDto latestAttempt = quizService.getLatestQuizAttempt(quizId, userId);
      return ResponseEntity.ok(latestAttempt);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }

  /**
   * Retrieves the name of a quiz by its ID.
   *
   * @param quizId the ID of the quiz to retrieve the name for
   * @return ResponseEntity containing the quiz name if found, or an error message if not found
   */
  @Operation(summary = "Get quiz name", description = "Retrieves the name of a quiz by its ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz name", 
          content = @Content(schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "400", description = "Bad request - quiz not found", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal server error", 
          content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))
  })
  @GetMapping("/quizzes/{quiz_id}/name")
  public ResponseEntity<?> getQuizNameById(@PathVariable("quiz_id") Long quizId) {
    try {
      String quizName = quizService.getQuizNameById(quizId);
      return ResponseEntity.ok(Collections.singletonMap("name", quizName));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("An unexpected error occurred");
    }
  }
}
