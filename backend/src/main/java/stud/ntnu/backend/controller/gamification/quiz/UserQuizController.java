package stud.ntnu.backend.controller.gamification.quiz;

import java.security.Principal;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.service.gamification.quiz.UserQuizService;
import stud.ntnu.backend.service.user.UserService;

/**
 * REST controller for managing user quiz interactions.
 * Provides endpoints for creating quiz attempts, recording answers, and retrieving quiz history.
 * All endpoints require user authentication and operate on behalf of the authenticated user.
 */
@RestController
@RequestMapping("/api/user/quizzes")
public class UserQuizController {

  private final UserQuizService userQuizService;
  private final UserService userService;

  /**
   * Constructs a new UserQuizController with the required services.
   *
   * @param userQuizService service for managing user quiz interactions
   * @param userService service for managing user operations
   */
  public UserQuizController(UserQuizService userQuizService, UserService userService) {
    this.userQuizService = userQuizService;
    this.userService = userService;
  }

  /**
   * Creates a new quiz attempt for the authenticated user.
   *
   * @param quizId the ID of the quiz to attempt
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with:
   *         - 200 OK if attempt creation is successful
   *         - 400 Bad Request with error message if creation fails
   */
  @PostMapping("/attempts/{quiz_id}")
  public ResponseEntity<?> createUserQuizAttempt(@PathVariable("quiz_id") Long quizId,
      Principal principal) {
    try {
      Integer userId = userService.getUserIdByEmail(principal.getName());
      userQuizService.createUserQuizAttempt(quizId, userId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Records a user's answer for a specific quiz question during an attempt.
   *
   * @param dto the DTO containing attempt ID, quiz ID, question ID, and selected answer ID
   * @return ResponseEntity with:
   *         - 200 OK if answer recording is successful
   *         - 400 Bad Request with error message if recording fails
   */
  @PostMapping("/attempts/answer")
  public ResponseEntity<?> createUserQuizAnswer(@RequestBody CreateUserQuizAnswerDto dto) {
    try {
      userQuizService.createUserQuizAnswer(dto);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves a paginated list of all attempts made by the current user for a specific quiz.
   * Each attempt record includes only the attempt ID and completion timestamp.
   *
   * @param quizId the ID of the quiz to get attempts for
   * @param principal the Principal object representing the current user
   * @param pageable the pagination information including page number, size, and sorting
   * @return ResponseEntity with:
   *         - 200 OK and a page of attempt summaries if successful
   *         - 400 Bad Request with error message if retrieval fails
   */
  @GetMapping("/attempts/{quiz_id}")
  public ResponseEntity<?> getQuizAttemptsByQuizId(@PathVariable("quiz_id") Long quizId,
      Principal principal, Pageable pageable) {
    try {
      Integer userId = userService.getUserIdByEmail(principal.getName());
      return ResponseEntity.ok(userQuizService.getQuizAttemptsByQuizId(quizId, userId, pageable));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves a paginated list of all quizzes that the current user has attempted at least once.
   * For each quiz, returns basic information including ID, name, description, status,
   * question count, and creation timestamp.
   *
   * @param principal the Principal object representing the current user
   * @param pageable the pagination information including page number, size, and sorting
   * @return ResponseEntity with:
   *         - 200 OK and a page of QuizPreviewDto objects if successful
   *         - 400 Bad Request with error message if retrieval fails
   */
  @GetMapping("/attempted")
  public ResponseEntity<?> getAttemptedQuizHistory(Principal principal, Pageable pageable) {
    try {
      Integer userId = userService.getUserIdByEmail(principal.getName());
      return ResponseEntity.ok(userQuizService.getBasicInfoForAttemptedQuizzes(userId, pageable));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Retrieves the total number of correct answers for a specific quiz attempt.
   *
   * @param attemptId the ID of the quiz attempt to get the correct answer count for
   * @return ResponseEntity with:
   *         - 200 OK and the number of correct answers if successful
   *         - 400 Bad Request with error message if retrieval fails
   */
  @GetMapping("/attempts/{attempt_id}/correct-count")
  public ResponseEntity<?> getTotalCorrectAnswersForAttempt(
      @PathVariable("attempt_id") Long attemptId) {
    try {
      int correctCount = userQuizService.getTotalCorrectAnswers(attemptId);
      return ResponseEntity.ok(correctCount);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
