package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.model.Quiz;
import stud.ntnu.backend.model.UserQuizAttempt;
import stud.ntnu.backend.repository.QuizRepository;
import stud.ntnu.backend.repository.UserQuizAttemptRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserQuizAttemptRepository userQuizAttemptRepository) {
        this.quizRepository = quizRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
    }

    public Quiz createQuiz(CreateQuizDto createQuizDto, Long userId) {
        Quiz quiz = Quiz.builder()
                .name(createQuizDto.getName())
                .description(createQuizDto.getDescription())
                .status(createQuizDto.getStatus() != null ? createQuizDto.getStatus() : "active")
                .createdByUserId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return quizRepository.save(quiz);
    }

    public Quiz archiveQuiz(Long quizId) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new IllegalArgumentException("Quiz not found");
        }
        Quiz quiz = quizOpt.get();
        quiz.setStatus("archived");
        return quizRepository.save(quiz);
    }

    public UserQuizAttempt createUserQuizAttempt(Long quizId, Long userId) {
        UserQuizAttempt attempt = UserQuizAttempt.builder()
                .userId(userId)
                .quizId(quizId)
                .completedAt(null)
                .build();
        return userQuizAttemptRepository.save(attempt);
    }
}
