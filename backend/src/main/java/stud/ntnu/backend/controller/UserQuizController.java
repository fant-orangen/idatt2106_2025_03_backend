package stud.ntnu.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.service.QuizService;
import java.security.Principal;

@RestController
@RequestMapping("/api/quizzes/user")
public class UserQuizController {

    private final QuizService quizService;

    public UserQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Creates a user quiz attempt for a given quiz.
     * TODO: test
     * @param quizId    the quiz id
     * @param principal the Principal object representing the current user
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping("/attempts/{quiz_id}")
    public ResponseEntity<?> createUserQuizAttempt(@PathVariable("quiz_id") Long quizId, Principal principal) {
        try {
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            quizService.createUserQuizAttempt(quizId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Records a user's answer to a quiz question for a specific attempt.
     * TODO: test
     * @param dto the answer information (userQuizAttemptId, quizId, questionId, answerId)
     * @return ResponseEntity with 200 OK or error message
     */
    @PostMapping("/attempts/answer")
    public ResponseEntity<?> createUserQuizAnswer(@RequestBody CreateUserQuizAnswerDto dto) {
        try {
            quizService.createUserQuizAnswer(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gets all attempts for a quiz by the current user, returning only id and completedAt.
     * TODO: test
     * @param quizId    the quiz id
     * @param principal the Principal object representing the current user
     * @param pageable  the pagination information
     * @return ResponseEntity with a page of attempt summaries
     */
    @GetMapping("/attempts/{quiz_id}")
    public ResponseEntity<?> getQuizAttemptsByQuizId(@PathVariable("quiz_id") Long quizId, Principal principal, Pageable pageable) {
        try {
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            return ResponseEntity.ok(quizService.getQuizAttemptsByQuizId(quizId, userId, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /**
     * Gets paginated basic info about quizzes with at least one attempt by the current user.
     * Returns quizId, name, status, and questionCount for each quiz.
     * TODO: test
     * @param principal the Principal object representing the current user
     * @param pageable  the pagination information
     * @return ResponseEntity with a page of QuizBasicInfoDto
     */
    @GetMapping("/attempted")
    public ResponseEntity<?> getAttemptedQuizHistory(Principal principal, Pageable pageable) {
        try {
            Long userId = Long.valueOf(principal.getName()); // Adjust if needed
            return ResponseEntity.ok(quizService.getBasicInfoForAttemptedQuizzes(userId, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Gets the total number of correct answers for a given quiz attempt.
     * TODO: test
     * @param attemptId the user quiz attempt id
     * @return ResponseEntity with the total number of correct answers (integer)
     */
    @GetMapping("/attempts/{attempt_id}/correct-count")
    public ResponseEntity<?> getTotalCorrectAnswersForAttempt(@PathVariable("attempt_id") Long attemptId) {
        try {
            int correctCount = quizService.getTotalCorrectAnswers(attemptId);
            return ResponseEntity.ok(correctCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
