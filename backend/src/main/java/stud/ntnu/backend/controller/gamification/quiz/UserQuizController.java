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

@RestController
@RequestMapping("/api/quizzes/user")
public class UserQuizController {

  private final UserQuizService userQuizService;
  private final UserService userService;

  public UserQuizController(UserQuizService userQuizService, UserService userService) {
    this.userQuizService = userQuizService;
    this.userService = userService;
  }

  /**
   * Creates a user quiz attempt for a given quiz.
   *
   * @param quizId    the quiz id
   * @param principal the Principal object representing the current user
   * @return ResponseEntity with 200 OK or error message
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
   * Records a user's answer to a quiz question for a specific attempt.
   *
   * @param dto the answer information (userQuizAttemptId, quizId, questionId, answerId)
   * @return ResponseEntity with 200 OK or error message
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
   * Gets all attempts for a quiz by the current user, returning only id and completedAt.
   *
   * @param quizId    the quiz id
   * @param principal the Principal object representing the current user
   * @param pageable  the pagination information
   * @return ResponseEntity with a page of attempt summaries
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
   * Gets paginated basic info about quizzes with at least one attempt by the current user. Returns
   * quizId, name, status, and questionCount for each quiz.
   *
   * @param principal the Principal object representing the current user
   * @param pageable  the pagination information
   * @return ResponseEntity with a page of QuizBasicInfoDto
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
   * Gets the total number of correct answers for a given quiz attempt.
   *
   * @param attemptId the user quiz attempt id
   * @return ResponseEntity with the total number of correct answers (integer)
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
