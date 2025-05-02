package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.model.Quiz;
import stud.ntnu.backend.model.UserQuizAttempt;
import stud.ntnu.backend.model.UserQuizAnswer;
import stud.ntnu.backend.repository.QuizRepository;
import stud.ntnu.backend.repository.UserQuizAttemptRepository;
import stud.ntnu.backend.repository.UserQuizAnswerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final UserQuizAnswerRepository userQuizAnswerRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserQuizAttemptRepository userQuizAttemptRepository, UserQuizAnswerRepository userQuizAnswerRepository) {
        this.quizRepository = quizRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.userQuizAnswerRepository = userQuizAnswerRepository;
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

    public UserQuizAnswer createUserQuizAnswer(CreateUserQuizAnswerDto dto) {
        UserQuizAnswer answer = UserQuizAnswer.builder()
                .userQuizAttemptId(dto.getUserQuizAttemptId())
                .quizId(dto.getQuizId())
                .questionId(dto.getQuestionId())
                .answerId(dto.getAnswerId())
                .build();
        return userQuizAnswerRepository.save(answer);
    }

    public Page<QuizAttemptSummaryDto> getQuizAttemptsByQuizId(Long quizId, Long userId, Pageable pageable) {
        return userQuizAttemptRepository.findByUserIdAndQuizId(userId, quizId, pageable)
                .map(a -> new QuizAttemptSummaryDto(a.getId(), a.getCompletedAt()));
    }
}
