package stud.ntnu.backend.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import stud.ntnu.backend.dto.quiz.CreateQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateQuizQuestionDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.service.QuizService;
import org.springframework.web.bind.annotation.DeleteMapping;
import stud.ntnu.backend.service.UserService;
import stud.ntnu.backend.security.AdminChecker;

/**
 * Admin controller for managing quizzes. Supports creation, archiving, and user attempts.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    /**
     * Creates a new empty quiz. Only admins should be allowed (add check if needed).
     * TODO: test
     * @param createQuizDto the quiz information (name, description, status)
     * @param principal     the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping("/admin")
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizDto createQuizDto, Principal principal) {
        try {
            if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            Long quizId = quizService.createQuiz(createQuizDto, userId);
            return ResponseEntity.ok(Collections.singletonMap("quizId", quizId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    /**
     * Archives a quiz by id.
     * TODO: test
     * @param id the quiz id
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PatchMapping("/admin/{id}/archive")
    public ResponseEntity<?> archiveQuiz(@PathVariable Long id, Principal principal) {
        try {
            if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            quizService.archiveQuiz(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /**
     * Gets all questions for a quiz by quiz id.
     * TODO: test
     * @param quizId the quiz id
     * @return List of QuizQuestionResponseDto
     */
    @GetMapping("/{quiz_id}/questions")
    public ResponseEntity<List<QuizQuestionResponseDto>> getQuizQuestionsByQuizId(
            @PathVariable("quiz_id") Long quizId) {
        List<QuizQuestionResponseDto> questions = quizService.getQuestionsById(quizId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Gets all answers for a quiz question by question id.
     * TODO: test
     * @param questionId the quiz question id
     * @return List of QuizAnswerResponseDto
     */
    @GetMapping("/questions/{question_id}/answers")
    public ResponseEntity<List<QuizAnswerResponseDto>> getAnswersByQuestionId(
            @PathVariable("question_id") Long questionId) {
        List<QuizAnswerResponseDto> answers = quizService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }

    /**
     * Saves a quiz question.
     * TODO: test
     * @param dto the CreateQuizQuestionDto containing question data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping("/admin/questions")
    public ResponseEntity<?> saveQuizQuestion(@RequestBody CreateQuizQuestionDto dto, Principal principal) {
        try {
            if (!AdminChecker.isCurrentUserAdmin(principal, userService)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            quizService.saveQuizQuestion(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Partially updates an existing quiz question.
     * TODO: test
     * @param questionId the id of the quiz question to update
     * @param dto the CreateQuizQuestionDto containing updated question data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PatchMapping("/admin/questions/{question_id}")
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
     * TODO: test
     * @param answerId the id of the quiz answer to update
     * @param dto the CreateQuizAnswerDto containing updated answer data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PatchMapping("/admin/answers/{answer_id}")
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
     * TODO: test
     * @param dto the CreateQuizAnswerDto containing answer data
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping("/admin/answers")
    public ResponseEntity<?> saveQuizAnswer(@RequestBody CreateQuizAnswerDto dto, Principal principal) {
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
     * Deletes a quiz question by its id.
     * TODO: test
     * @param questionId the id of the quiz question to delete
     * @return ResponseEntity with 200 OK or error message
     */
    @DeleteMapping("/admin/questions/{question_id}")
    public ResponseEntity<?> deleteQuizQuestion(@PathVariable("question_id") Long questionId, Principal principal) {
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
}
