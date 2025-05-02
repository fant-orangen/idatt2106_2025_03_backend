package stud.ntnu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizBasicInfoDto;
import stud.ntnu.backend.model.Quiz;
import stud.ntnu.backend.model.UserQuizAttempt;
import stud.ntnu.backend.model.UserQuizAnswer;
import stud.ntnu.backend.repository.QuizRepository;
import stud.ntnu.backend.repository.UserQuizAttemptRepository;
import stud.ntnu.backend.repository.UserQuizAnswerRepository;
import stud.ntnu.backend.repository.QuizQuestionRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final UserQuizAnswerRepository userQuizAnswerRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserQuizAttemptRepository userQuizAttemptRepository, UserQuizAnswerRepository userQuizAnswerRepository, QuizQuestionRepository quizQuestionRepository) {
        this.quizRepository = quizRepository;
        this.userQuizAttemptRepository = userQuizAttemptRepository;
        this.userQuizAnswerRepository = userQuizAnswerRepository;
        this.quizQuestionRepository = quizQuestionRepository;
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

    public Page<QuizBasicInfoDto> getBasicInfoForAttemptedQuizzes(Long userId, Pageable pageable) {
        // Find all quiz IDs for which the user has at least one attempt
        List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserId(userId);
        Set<Long> quizIds = attempts.stream().map(UserQuizAttempt::getQuizId).collect(Collectors.toSet());
        if (quizIds.isEmpty()) {
            return Page.empty(pageable);
        }
        // Paginate the quiz IDs manually
        List<Long> quizIdList = new ArrayList<>(quizIds);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quizIdList.size());
        if (start > end) {
            return Page.empty(pageable);
        }
        List<Long> pagedQuizIds = quizIdList.subList(start, end);
        // Fetch quizzes and build DTOs
        List<Quiz> quizzes = quizRepository.findAllById(pagedQuizIds);
        List<QuizBasicInfoDto> dtos = quizzes.stream()
            .map(q -> new QuizBasicInfoDto(
                q.getId(),
                q.getName(),
                q.getStatus(),
                quizQuestionRepository.countByQuizId(q.getId())
            ))
            .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, quizIds.size());
    }
}
