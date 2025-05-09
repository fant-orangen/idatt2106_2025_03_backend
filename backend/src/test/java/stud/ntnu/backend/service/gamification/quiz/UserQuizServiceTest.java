package stud.ntnu.backend.service.gamification.quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizPreviewDto;
import stud.ntnu.backend.model.gamification.quiz.*;
import stud.ntnu.backend.repository.gamification.quiz.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserQuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserQuizAttemptRepository userQuizAttemptRepository;

    @Mock
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @InjectMocks
    private UserQuizService userQuizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateUserQuizAttemptTests {
        @Test
        void shouldCreateUserQuizAttemptSuccessfully() {
            // Arrange
            Long quizId = 1L;
            Integer userId = 1;

            // Act
            userQuizService.createUserQuizAttempt(quizId, userId);

            // Assert
            verify(userQuizAttemptRepository).save(any(UserQuizAttempt.class));
        }
    }

    @Nested
    class CreateUserQuizAnswerTests {
        @Test
        void shouldCreateUserQuizAnswerSuccessfully() {
            // Arrange
            CreateUserQuizAnswerDto dto = new CreateUserQuizAnswerDto(1L, 1L, 1L, 1L);

            // Act
            userQuizService.createUserQuizAnswer(dto);

            // Assert
            verify(userQuizAnswerRepository).save(any(UserQuizAnswer.class));
        }
    }

    @Nested
    class GetQuizAttemptsByQuizIdTests {
        @Test
        void shouldReturnQuizAttemptsSuccessfully() {
            // Arrange
            Long quizId = 1L;
            Integer userId = 1;
            Pageable pageable = PageRequest.of(0, 10);
            
            UserQuizAttempt attempt = new UserQuizAttempt();
            attempt.setId(1L);
            attempt.setUserId(userId);
            attempt.setQuizId(quizId);
            attempt.setCompletedAt(LocalDateTime.now());
            
            Page<UserQuizAttempt> attemptPage = new PageImpl<>(List.of(attempt));
            when(userQuizAttemptRepository.findByUserIdAndQuizIdOrderByIdDesc(userId, quizId, pageable))
                .thenReturn(attemptPage);

            // Act
            Page<QuizAttemptSummaryDto> result = userQuizService.getQuizAttemptsByQuizId(quizId, userId, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(attempt.getId(), result.getContent().get(0).getId());
            assertEquals(attempt.getCompletedAt(), result.getContent().get(0).getCompletedAt());
        }

        @Test
        void shouldReturnEmptyPageWhenNoAttempts() {
            // Arrange
            Long quizId = 1L;
            Integer userId = 1;
            Pageable pageable = PageRequest.of(0, 10);

            when(userQuizAttemptRepository.findByUserIdAndQuizIdOrderByIdDesc(userId, quizId, pageable))
                .thenReturn(Page.empty(pageable));

            // Act
        Page<QuizAttemptSummaryDto> result = userQuizService.getQuizAttemptsByQuizId(quizId, userId, pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetBasicInfoForAttemptedQuizzesTests {
        @Test
        void shouldReturnAttemptedQuizzesSuccessfully() {
            // Arrange
            Integer userId = 1;
            Pageable pageable = PageRequest.of(0, 10);
            
            UserQuizAttempt attempt = new UserQuizAttempt();
            attempt.setQuizId(1L);
            
            Quiz quiz = new Quiz();
            quiz.setId(1L);
            quiz.setName("Test Quiz");
            quiz.setDescription("Test Description");
            quiz.setStatus("active");
            quiz.setCreatedAt(LocalDateTime.now());
            
            when(userQuizAttemptRepository.findByUserId(userId))
                .thenReturn(List.of(attempt));
            when(quizRepository.findAllById(any()))
                .thenReturn(List.of(quiz));
            when(quizQuestionRepository.countByQuizId(quiz.getId()))
                .thenReturn(5L);

            // Act
            Page<QuizPreviewDto> result = userQuizService.getBasicInfoForAttemptedQuizzes(userId, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            QuizPreviewDto dto = result.getContent().get(0);
            assertEquals(quiz.getId(), dto.getId());
            assertEquals(quiz.getName(), dto.getName());
            assertEquals(quiz.getDescription(), dto.getDescription());
            assertEquals(quiz.getStatus(), dto.getStatus());
            assertEquals(5L, dto.getQuestionCount());
            assertEquals(quiz.getCreatedAt(), dto.getCreatedAt());
        }

        @Test
        void shouldReturnEmptyPageWhenNoAttempts() {
            // Arrange
            Integer userId = 1;
            Pageable pageable = PageRequest.of(0, 10);
            
            when(userQuizAttemptRepository.findByUserId(userId))
                .thenReturn(List.of());

            // Act
            Page<QuizPreviewDto> result = userQuizService.getBasicInfoForAttemptedQuizzes(userId, pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetTotalCorrectAnswersTests {
        @Test
        void shouldReturnCorrectAnswerCount() {
            // Arrange
            Long attemptId = 1L;
            
            UserQuizAnswer userAnswer1 = new UserQuizAnswer();
            userAnswer1.setAnswerId(1L);
            
            UserQuizAnswer userAnswer2 = new UserQuizAnswer();
            userAnswer2.setAnswerId(2L);
            
            QuizAnswer correctAnswer = new QuizAnswer();
            correctAnswer.setId(1L);
            correctAnswer.setIsCorrect(true);
            
            QuizAnswer incorrectAnswer = new QuizAnswer();
            incorrectAnswer.setId(2L);
            incorrectAnswer.setIsCorrect(false);
            
            when(userQuizAnswerRepository.findAllByUserQuizAttemptId(attemptId))
                .thenReturn(Arrays.asList(userAnswer1, userAnswer2));
            when(quizAnswerRepository.findById(1L))
                .thenReturn(Optional.of(correctAnswer));
            when(quizAnswerRepository.findById(2L))
                .thenReturn(Optional.of(incorrectAnswer));

            // Act
            int result = userQuizService.getTotalCorrectAnswers(attemptId);

            // Assert
            assertEquals(1, result);
        }

        @Test
        void shouldReturnZeroWhenNoAnswers() {
            // Arrange
            Long attemptId = 1L;
            
            when(userQuizAnswerRepository.findAllByUserQuizAttemptId(attemptId))
                .thenReturn(List.of());

            // Act
            int result = userQuizService.getTotalCorrectAnswers(attemptId);

            // Assert
            assertEquals(0, result);
        }
    }
} 