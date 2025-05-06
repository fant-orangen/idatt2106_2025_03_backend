package stud.ntnu.backend.controller.gamification.quiz;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.quiz.CreateQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateQuizQuestionDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.dto.quiz.QuizPreviewDto;
import stud.ntnu.backend.security.AdminChecker;
import stud.ntnu.backend.service.gamification.quiz.QuizService;
import stud.ntnu.backend.service.user.UserService;

/**
 * Admin controller for managing quizzes. Supports creation, archiving, and user attempts.
 */
@RestController
@RequestMapping("/api")
public class QuizController {

  private final QuizService quizService;
  private final UserService userService;

  public QuizController(QuizService quizService, UserService userService) {
    this.quizService = quizService;
    this.userService = userService;
  }

    // -------------------- ADMIN ENDPOINTS --------------------

    /**
     * Creates a new empty quiz. Only admins should be allowed (add check if needed).
     *
     * @param createQuizDto the quiz information (name, description, status)
     * @param principal     the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */

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
     * Archives a quiz by id.
     *
     * @param id        the quiz id
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Saves a quiz question.
     *
     * @param dto       the CreateQuizQuestionDto containing question data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Partially updates an existing quiz question.
     *
     * @param questionId the id of the quiz question to update
     * @param dto        the CreateQuizQuestionDto containing updated question data
     * @param principal  the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Partially updates an existing quiz answer.
     *
     * @param answerId  the id of the quiz answer to update
     * @param dto       the CreateQuizAnswerDto containing updated answer data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Saves a quiz answer.
     *
     * @param dto       the CreateQuizAnswerDto containing answer data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Gets all correct answers for a quiz question by its id.
     *
     * @param questionId the id of the quiz question
     * @return ResponseEntity with a list of QuizAnswerDto
     */
    @GetMapping("/quizzes/admin/{question_id}/answers/correct")
    public ResponseEntity<?> getCorrectQuizAnswersByQuestionId(
        @PathVariable("question_id") Long questionId) {
        try {
            List<QuizAnswerDto> correctAnswers = quizService.getAnswersByQuestionIdAdmin(questionId);
            return ResponseEntity.ok(correctAnswers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a quiz question by its id.
     *
     * @param questionId the id of the quiz question to delete
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Deletes a quiz answer by its id.
     *
     * @param answerId  the id of the quiz answer to delete
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
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
     * Gets all questions for a quiz by quiz id.
     *
     * @param quizId the quiz id
     * @return List of QuizQuestionResponseDto
     */
    @GetMapping("/quizzes/user/{quiz_id}/questions")
    public ResponseEntity<List<QuizQuestionResponseDto>> getQuizQuestionsByQuizId(
        @PathVariable("quiz_id") Long quizId) {
        List<QuizQuestionResponseDto> questions = quizService.getQuestionsById(quizId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Gets all answers for a quiz question by question id.
     *
     * @param questionId the quiz question id
     * @return List of QuizAnswerResponseDto
     */
    @GetMapping("/quizzes/user/questions/{question_id}/answers")
    public ResponseEntity<List<QuizAnswerResponseDto>> getAnswersByQuestionId(
        @PathVariable("question_id") Long questionId) {
        List<QuizAnswerResponseDto> answers = quizService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }

    /**
     * Gets all active quizzes in paginated format.
     *
     * @param pageable the pagination information
     * @return ResponseEntity with a page of QuizPreviewDto (id, name, description, createdAt)
     */
    @GetMapping("/quizzes/user/all/previews/active")
    public ResponseEntity<Page<QuizPreviewDto>> getAllActiveQuizzes(Pageable pageable) {
        try {
            return ResponseEntity.ok(quizService.getAllActiveQuizzes(pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets all archived quizzes in paginated format.
     *
     * @param pageable the pagination information
     * @return ResponseEntity with a page of QuizPreviewDto (id, name, description, createdAt)
     */
    @GetMapping("/quizzes/user/all/previews/archived")
    public ResponseEntity<Page<QuizPreviewDto>> getAllArchivedQuizzes(Pageable pageable) {
        try {
            Page<QuizPreviewDto> archivedQuizzes = quizService.getAllArchivedQuizzes(pageable);
            return ResponseEntity.ok(archivedQuizzes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
