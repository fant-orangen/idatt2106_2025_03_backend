package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.service.QuizService;

import java.security.Principal;

/**
 * Admin controller for managing quizzes. Supports creation, archiving, and user attempts.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Creates a new quiz. Only admins should be allowed (add check if needed).
     *
     * @param createQuizDto the quiz information (name, description, status)
     * @param principal     the Principal object representing the current user
     * @return ResponseEntity with the created quiz or error message
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizDto createQuizDto, Principal principal) {
        try {
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            return ResponseEntity.ok(quizService.createQuiz(createQuizDto, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Archives a quiz by id.
     *
     * @param id the quiz id
     * @return ResponseEntity with the archived quiz or error message
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<?> archiveQuiz(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quizService.archiveQuiz(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Creates a user quiz attempt for a given quiz.
     *
     * @param quizId    the quiz id
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with the created attempt or error message
     */
    @PostMapping("/attempts/{quiz_id}")
    public ResponseEntity<?> createUserQuizAttempt(@PathVariable("quiz_id") Long quizId, Principal principal) {
        try {
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            return ResponseEntity.ok(quizService.createUserQuizAttempt(quizId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
