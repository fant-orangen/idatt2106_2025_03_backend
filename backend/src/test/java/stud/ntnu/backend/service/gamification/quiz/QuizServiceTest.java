package stud.ntnu.backend.service.gamification.quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import stud.ntnu.backend.dto.quiz.*;
import stud.ntnu.backend.model.gamification.quiz.*;
import stud.ntnu.backend.repository.gamification.quiz.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

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
    private QuizService quizService;

    private Quiz testQuiz;
    private QuizQuestion testQuestion;
    private QuizAnswer testAnswer;
    private UserQuizAttempt testAttempt;
    private CreateQuizDto createQuizDto;
    private CreateQuizQuestionDto createQuestionDto;
    private CreateQuizAnswerDto createAnswerDto;

    @BeforeEach
    void setUp() {
        // Set up test quiz
        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setName("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setStatus("active");
        testQuiz.setCreatedByUserId(1L);
        testQuiz.setCreatedAt(LocalDateTime.now());

        // Set up test question
        testQuestion = new QuizQuestion();
        testQuestion.setId(1L);
        testQuestion.setQuizId(1L);
        testQuestion.setQuestionBody("Test Question");
        testQuestion.setPosition(1);
        testQuestion.setCreatedAt(LocalDateTime.now());

        // Set up test answer
        testAnswer = new QuizAnswer();
        testAnswer.setId(1L);
        testAnswer.setQuizId(1L);
        testAnswer.setQuestionId(1L);
        testAnswer.setAnswerBody("Test Answer");
        testAnswer.setIsCorrect(true);
        testAnswer.setCreatedAt(LocalDateTime.now());

        // Set up test attempt
        testAttempt = new UserQuizAttempt();
        testAttempt.setId(1L);
        testAttempt.setUserId(1);
        testAttempt.setQuizId(1L);
        testAttempt.setCompletedAt(null);

        // Set up DTOs
        createQuizDto = new CreateQuizDto();
        createQuizDto.setName("New Quiz");
        createQuizDto.setDescription("New Description");
        createQuizDto.setStatus("active");

        createQuestionDto = new CreateQuizQuestionDto();
        createQuestionDto.setQuizId(1L);
        createQuestionDto.setQuestionBody("New Question");
        createQuestionDto.setPosition(1);

        createAnswerDto = new CreateQuizAnswerDto();
        createAnswerDto.setQuizId(1L);
        createAnswerDto.setQuestionId(1L);
        createAnswerDto.setAnswerBody("New Answer");
        createAnswerDto.setIsCorrect(true);
    }

    @Nested
    class CreateQuizTests {
        @Test
        void shouldCreateQuizSuccessfully() {
            // Arrange
            doAnswer(invocation -> {
                Quiz quiz = invocation.getArgument(0);
                quiz.setId(1L);
                return quiz;
            }).when(quizRepository).save(any(Quiz.class));

            // Act
            Long quizId = quizService.createQuiz(createQuizDto, 1L);

            // Assert
            assertNotNull(quizId);
            assertEquals(1L, quizId);
            verify(quizRepository).save(any(Quiz.class));
        }

        @Test
        void shouldThrowExceptionWhenQuizCreationFails() {
            // Arrange
            when(quizRepository.save(any(Quiz.class))).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> quizService.createQuiz(createQuizDto, 1L));
        }
    }

    @Nested
    class AddQuestionTests {
        @Test
        void shouldAddQuestionSuccessfully() {
            // Arrange
            when(quizQuestionRepository.save(any(QuizQuestion.class))).thenReturn(testQuestion);

            // Act
            quizService.saveQuizQuestion(createQuestionDto);

            // Assert
            verify(quizQuestionRepository).save(any(QuizQuestion.class));
        }
    }

    @Nested
    class AddAnswerTests {
        @Test
        void shouldAddAnswerSuccessfully() {
            // Arrange
            when(quizAnswerRepository.save(any(QuizAnswer.class))).thenReturn(testAnswer);

            // Act
            quizService.saveQuizAnswer(createAnswerDto);

            // Assert
            verify(quizAnswerRepository).save(any(QuizAnswer.class));
        }
    }

    @Nested
    class GetQuizTests {
        @Test
        void shouldGetQuizSuccessfully() {
            // Arrange
            when(quizQuestionRepository.findAllByQuizId(1L)).thenReturn(Arrays.asList(testQuestion));

            // Act
            List<QuizQuestionResponseDto> result = quizService.getQuestionsById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testQuestion.getQuestionBody(), result.get(0).getQuestionBody());
        }

        @Test
        void shouldReturnEmptyListWhenNoQuestionsFound() {
            // Arrange
            when(quizQuestionRepository.findAllByQuizId(1L)).thenReturn(Arrays.asList());

            // Act
            List<QuizQuestionResponseDto> result = quizService.getQuestionsById(1L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetQuizPreviewsTests {
        @Test
        void shouldGetQuizPreviewsSuccessfully() {
            // Arrange
            List<Quiz> quizzes = Arrays.asList(testQuiz);
            Page<Quiz> quizPage = new PageImpl<>(quizzes);
            when(quizRepository.findAllByStatus(eq("active"), any(Pageable.class))).thenReturn(quizPage);
            when(quizQuestionRepository.countByQuizId(1L)).thenReturn(1L);

            // Act
            Page<QuizPreviewDto> result = quizService.getAllActiveQuizzes(Pageable.unpaged());

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(testQuiz.getName(), result.getContent().get(0).getName());
        }
    }

    @Nested
    class DeleteQuizTests {
        @Test
        void shouldArchiveQuizSuccessfully() {
            // Arrange
            when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

            // Act
            quizService.updateQuizStatus(1L, "archived");

            // Assert
            verify(quizRepository).save(argThat(quiz -> 
                quiz.getId().equals(1L) && 
                quiz.getStatus().equals("archived")
            ));
        }

        @Test
        void shouldThrowExceptionWhenQuizNotFound() {
            // Arrange
            when(quizRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> quizService.updateQuizStatus(1L, "archived"));
        }
    }
} 