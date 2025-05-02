package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.service.QuizService;
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import java.util.List;
import java.util.Collections;

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
     * Creates a new empty quiz. Only admins should be allowed (add check if needed).
     * TODO: test
     * @param createQuizDto the quiz information (name, description, status)
     * @param principal     the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody CreateQuizDto createQuizDto, Principal principal) {
        try {
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
     * @return ResponseEntity with 200 OK or error message
     */
    @PatchMapping("/{id}/archive")
    public ResponseEntity<?> archiveQuiz(@PathVariable Long id) {
        try {
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
}
