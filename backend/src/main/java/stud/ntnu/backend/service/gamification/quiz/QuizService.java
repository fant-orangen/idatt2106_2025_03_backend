package stud.ntnu.backend.service.gamification.quiz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.backend.dto.quiz.CreateQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.CreateQuizDto;
import stud.ntnu.backend.dto.quiz.CreateQuizQuestionDto;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizPreviewDto;
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.model.gamification.quiz.Quiz;
import stud.ntnu.backend.model.gamification.quiz.QuizAnswer;
import stud.ntnu.backend.model.gamification.quiz.QuizQuestion;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAnswer;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAttempt;
import stud.ntnu.backend.repository.gamification.quiz.QuizAnswerRepository;
import stud.ntnu.backend.repository.gamification.quiz.QuizQuestionRepository;
import stud.ntnu.backend.repository.gamification.quiz.QuizRepository;
import stud.ntnu.backend.repository.gamification.quiz.UserQuizAnswerRepository;
import stud.ntnu.backend.repository.gamification.quiz.UserQuizAttemptRepository;

/**
 * Service class for managing quizzes, quiz questions, answers, and user attempts. Provides business
 * logic for quiz-related operations including: - Quiz creation, retrieval, update, and deletion -
 * Question and answer management - User quiz attempts tracking - Quiz statistics and reporting
 */
@Service
public class QuizService {

  /**
   * Repository for Quiz entities.
   */
  private final QuizRepository quizRepository;

  /**
   * Repository for UserQuizAttempt entities.
   */
  private final UserQuizAttemptRepository userQuizAttemptRepository;

  /**
   * Repository for UserQuizAnswer entities.
   */
  private final UserQuizAnswerRepository userQuizAnswerRepository;

  /**
   * Repository for QuizQuestion entities.
   */
  private final QuizQuestionRepository quizQuestionRepository;

  /**
   * Repository for QuizAnswer entities.
   */
  private final QuizAnswerRepository quizAnswerRepository;

  /**
   * Constructs a new QuizService with the required repositories.
   *
   * @param quizRepository            the quiz repository
   * @param userQuizAttemptRepository the user quiz attempt repository
   * @param userQuizAnswerRepository  the user quiz answer repository
   * @param quizQuestionRepository    the quiz question repository
   * @param quizAnswerRepository      the quiz answer repository
   */
  @Autowired
  public QuizService(QuizRepository quizRepository,
      UserQuizAttemptRepository userQuizAttemptRepository,
      UserQuizAnswerRepository userQuizAnswerRepository,
      QuizQuestionRepository quizQuestionRepository,
      QuizAnswerRepository quizAnswerRepository) {
    this.quizRepository = quizRepository;
    this.userQuizAttemptRepository = userQuizAttemptRepository;
    this.userQuizAnswerRepository = userQuizAnswerRepository;
    this.quizQuestionRepository = quizQuestionRepository;
    this.quizAnswerRepository = quizAnswerRepository;
  }

  /**
   * Creates a new quiz and saves it to the repository.
   *
   * @param createQuizDto the DTO containing quiz creation data
   * @param userId        the ID of the user creating the quiz
   * @return the ID of the created quiz
   */
  public Long createQuiz(CreateQuizDto createQuizDto, Long userId) {
    Quiz quiz = new Quiz();
    quiz.setName(createQuizDto.getName());
    quiz.setDescription(createQuizDto.getDescription());
    quiz.setStatus(createQuizDto.getStatus() != null ? createQuizDto.getStatus() : "active");
    quiz.setCreatedByUserId(userId);
    quiz.setCreatedAt(LocalDateTime.now());
    quizRepository.save(quiz);
    return quiz.getId();
  }

  /**
   * Deletes a quiz and all its associated questions and answers.
   *
   * @param quizId the ID of the quiz to delete
   */
  @Transactional
  public void deleteQuiz(Long quizId) {
    // Check if the quiz exists
    if (!quizRepository.existsById(quizId)) {
      throw new IllegalArgumentException("Quiz not found");
    }

    // Delete all user quiz answers associated with the quiz
    List<Long> attemptIds = userQuizAttemptRepository.findAllByQuizId(quizId)
        .stream()
        .map(UserQuizAttempt::getId)
        .toList();
    for (Long attemptId : attemptIds) {
      userQuizAnswerRepository.deleteAll(
          userQuizAnswerRepository.findAllByUserQuizAttemptId(attemptId));
    }

    // Delete all user quiz attempts associated with the quiz
    userQuizAttemptRepository.deleteAll(userQuizAttemptRepository.findAllByQuizId(quizId));

    // Delete all answers associated with the quiz's questions
    List<Long> questionIds = quizQuestionRepository.findAllByQuizId(quizId)
        .stream()
        .map(QuizQuestion::getId)
        .toList();
    for (Long questionId : questionIds) {
      quizAnswerRepository.deleteAll(quizAnswerRepository.findAllByQuestionId(questionId));
    }

    // Delete all questions associated with the quiz
    quizQuestionRepository.deleteAll(quizQuestionRepository.findAllByQuizId(quizId));

    // Delete the quiz itself
    quizRepository.deleteById(quizId);
  }

  /**
   * Creates a new user quiz attempt and saves it to the repository.
   *
   * @param quizId the ID of the quiz being attempted
   * @param userId the ID of the user attempting the quiz
   */
  public Long createUserQuizAttempt(Long quizId, Integer userId) {
    UserQuizAttempt attempt = new UserQuizAttempt();
    attempt.setUserId(userId);
    attempt.setQuizId(quizId);
    attempt.setCompletedAt(LocalDateTime.now());
    userQuizAttemptRepository.save(attempt);
    return attempt.getId();
  }

  /**
   * Creates a new user quiz answer and saves it to the repository.
   *
   * @param dto the DTO containing user quiz answer data
   */
  public void createUserQuizAnswer(CreateUserQuizAnswerDto dto) {
    UserQuizAnswer answer = new UserQuizAnswer();
    answer.setUserQuizAttemptId(dto.getUserQuizAttemptId());
    answer.setQuizId(dto.getQuizId());
    answer.setQuestionId(dto.getQuestionId());
    answer.setAnswerId(dto.getAnswerId());
    userQuizAnswerRepository.save(answer);
  }

  /**
   * Retrieves a paginated list of quiz attempt summaries for a given quiz and user.
   *
   * @param quizId   the ID of the quiz
   * @param userId   the ID of the user
   * @param pageable the pagination information
   * @return a page of QuizAttemptSummaryDto objects
   */
  public Page<QuizAttemptSummaryDto> getQuizAttemptsByQuizId(Long quizId, Integer userId,
      Pageable pageable) {
    return userQuizAttemptRepository.findByUserIdAndQuizIdOrderByIdDesc(userId, quizId, pageable)
        .map(a -> new QuizAttemptSummaryDto(a.getId(), a.getCompletedAt()));
  }

  /**
   * Retrieves basic information for all quizzes that a user has attempted, paginated.
   *
   * @param userId   the ID of the user
   * @param pageable the pagination information
   * @return a page of QuizPreviewDto objects
   */
  public Page<QuizPreviewDto> getBasicInfoForAttemptedQuizzes(Integer userId, Pageable pageable) {
    // Find all quiz IDs for which the user has at least one attempt
    List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserId(userId);
    Set<Long> quizIds = attempts.stream().map(UserQuizAttempt::getQuizId)
        .collect(Collectors.toSet());
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
    List<QuizPreviewDto> dtos = quizzes.stream()
        .map(q -> new QuizPreviewDto(
            q.getId(),
            q.getName(),
            q.getDescription(),
            q.getStatus(),
            quizQuestionRepository.countByQuizId(q.getId()),
            q.getCreatedAt()
        ))
        .collect(Collectors.toList());
    return new PageImpl<>(dtos, pageable, quizIds.size());
  }

  /**
   * Retrieves all questions for a given quiz.
   *
   * @param quizId the ID of the quiz
   * @return a list of QuizQuestionResponseDto objects
   */
  public List<QuizQuestionResponseDto> getQuestionsById(Long quizId) {
    List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quizId);
    return questions.stream()
        .map(q -> new QuizQuestionResponseDto(q.getId(), q.getQuestionBody()))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all answers for a given quiz question.
   *
   * @param questionId the ID of the quiz question
   * @return a list of QuizAnswerResponseDto objects
   */
  public List<QuizAnswerResponseDto> getAnswersByQuestionId(Long questionId) {
    List<QuizAnswer> answers = quizAnswerRepository.findAllByQuestionId(questionId);
    return answers.stream()
        .map(a -> new QuizAnswerResponseDto(a.getId(), a.getQuizId(), a.getQuestionId(),
            a.getAnswerBody()))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves all correct answers for a given quiz.
   *
   * @param questionId the ID of the question
   * @return a list of QuizAnswer objects that are correct
   */
  public List<QuizAnswerDto> getAnswersByQuestionIdAdmin(Long questionId) {
    return quizAnswerRepository.findAllAnswersByQuestionId(questionId);
  }

  /**
   * Calculates the total number of correct answers for a given user quiz attempt.
   *
   * @param attemptId the ID of the user quiz attempt
   * @return the number of correct answers
   */
  public int getTotalCorrectAnswers(Long attemptId) {
    List<UserQuizAnswer> userAnswers = userQuizAnswerRepository.findAllByUserQuizAttemptId(
        attemptId);
    int correctCount = 0;
    for (UserQuizAnswer userAnswer : userAnswers) {
      Optional<QuizAnswer> quizAnswerOpt =
          quizAnswerRepository.findById(userAnswer.getAnswerId());
      if (quizAnswerOpt.isPresent() &&
          Boolean.TRUE.equals(quizAnswerOpt.get().getIsCorrect())) {
        correctCount++;
      }
    }
    return correctCount;
  }

  /**
   * Saves a new quiz question to the repository.
   *
   * @param dto the DTO containing quiz question data
   */
  public Long saveQuizQuestion(CreateQuizQuestionDto dto) {
    QuizQuestion question = new QuizQuestion();
    question.setQuizId(dto.getQuizId());
    question.setQuestionBody(dto.getQuestionBody());
    question.setPosition(dto.getPosition());
    QuizQuestion savedQuestion = quizQuestionRepository.save(question);
    return savedQuestion.getId();
  }

  /**
   * Saves a new quiz answer to the repository.
   *
   * @param dto the DTO containing quiz answer data
   */
  public void saveQuizAnswer(CreateQuizAnswerDto dto) {
    QuizAnswer answer = new QuizAnswer();
    answer.setQuizId(dto.getQuizId());
    answer.setQuestionId(dto.getQuestionId());
    answer.setAnswerBody(dto.getAnswerBody());
    answer.setIsCorrect(dto.getIsCorrect());
    quizAnswerRepository.save(answer);
  }

  /**
   * Deletes a quiz question by its ID.
   *
   * @param questionId the ID of the quiz question to delete
   * @throws IllegalArgumentException if the quiz question does not exist
   */
  public void deleteQuizQuestion(Long questionId) {
    if (!quizQuestionRepository.existsById(questionId)) {
      throw new IllegalArgumentException("Quiz question not found");
    }
    quizQuestionRepository.deleteById(questionId);
  }

  /**
   * Retrieves a quiz question by its ID.
   *
   * @param questionId the ID of the quiz question
   * @return the QuizQuestion entity
   * @throws IllegalArgumentException if the quiz question does not exist
   */
  public QuizQuestion getQuizQuestionById(Long questionId) {
    return quizQuestionRepository.findById(questionId)
        .orElseThrow(() -> new IllegalArgumentException("Quiz question not found"));
  }

  /**
   * Updates an existing quiz question with new data.
   *
   * @param questionId the ID of the quiz question to update
   * @param dto        the DTO containing updated quiz question data
   */
  public void updateQuizQuestion(Long questionId, CreateQuizQuestionDto dto) {
    QuizQuestion question = getQuizQuestionById(questionId);
    if (dto.getQuizId() != null) {
      question.setQuizId(dto.getQuizId());
    }
    if (dto.getQuestionBody() != null) {
      question.setQuestionBody(dto.getQuestionBody());
    }
    if (dto.getPosition() != null) {
      question.setPosition(dto.getPosition());
    }
    quizQuestionRepository.save(question);
  }

  /**
   * Updates an existing quiz answer with new data.
   *
   * @param answerId the ID of the quiz answer to update
   * @param dto      the DTO containing updated quiz answer data
   */
  public void updateQuizAnswer(Long answerId, CreateQuizAnswerDto dto) {
    QuizAnswer answer = quizAnswerRepository.findById(answerId)
        .orElseThrow(() -> new IllegalArgumentException("Quiz answer not found"));
    if (dto.getQuizId() != null) {
      answer.setQuizId(dto.getQuizId());
    }
    if (dto.getQuestionId() != null) {
      answer.setQuestionId(dto.getQuestionId());
    }
    if (dto.getAnswerBody() != null) {
      answer.setAnswerBody(dto.getAnswerBody());
    }
    if (dto.getIsCorrect() != null) {
      answer.setIsCorrect(dto.getIsCorrect());
    }
    quizAnswerRepository.save(answer);
  }

  /**
   * Deletes a quiz answer by its ID.
   *
   * @param answerId the ID of the quiz answer to delete
   * @throws IllegalArgumentException if the quiz answer does not exist
   */
  public void deleteQuizAnswer(Long answerId) {
    if (!quizAnswerRepository.existsById(answerId)) {
      throw new IllegalArgumentException("Quiz answer not found");
    }
    quizAnswerRepository.deleteById(answerId);
  }

  /**
   * Retrieves all active quizzes, paginated.
   *
   * @param pageable the pagination information
   * @return a page of QuizPreviewDto objects for active quizzes
   */
  public Page<QuizPreviewDto> getAllActiveQuizzes(Pageable pageable) {
    return quizRepository.findAllByStatus("active", pageable)
        .map(q -> new QuizPreviewDto(
            q.getId(),
            q.getName(),
            q.getDescription(),
            q.getStatus(),
            quizQuestionRepository.countByQuizId(q.getId()),
            q.getCreatedAt()
        ));
  }

  /**
   * Updates the status of a quiz.
   *
   * @param quizId the ID of the quiz to update
   * @param status the new status to set
   * @throws IllegalArgumentException if the quiz does not exist
   */
  public void updateQuizStatus(Long quizId, String status) {
    Quiz quiz = quizRepository.findById(quizId)
        .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
    quiz.setStatus(status);
    quizRepository.save(quiz);
  }

  /**
   * Retrieves all archived quizzes, paginated.
   *
   * @param pageable the pagination information
   * @return a page of QuizPreviewDto objects for archived quizzes
   */
  public Page<QuizPreviewDto> getAllArchivedQuizzes(Pageable pageable) {
    return quizRepository.findAllByStatus("archived", pageable)
        .map(q -> new QuizPreviewDto(
            q.getId(),
            q.getName(),
            q.getDescription(),
            q.getStatus(),
            quizQuestionRepository.countByQuizId(q.getId()),
            q.getCreatedAt()
        ));
  }

  /**
   * Retrieves the latest quiz attempt for a given quiz and user.
   *
   * @param quizId the ID of the quiz
   * @param userId the ID of the user
   * @return the latest QuizAttemptSummaryDto object, or null if no attempts exist
   */
  public QuizAttemptSummaryDto getLatestQuizAttempt(Long quizId, Integer userId) {
    UserQuizAttempt latestAttempt =
        userQuizAttemptRepository.findFirstByUserIdAndQuizIdOrderByIdDesc(userId, quizId);
    if (latestAttempt == null) {
      return null;
    }
    return new QuizAttemptSummaryDto(latestAttempt.getId(), latestAttempt.getCompletedAt());
  }

  /**
   * Get quiz name by its id.
   *
   * @param quizId the ID of the quiz
   */
  public String getQuizNameById(Long quizId) {
    return quizRepository.findNameById(quizId)
        .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + quizId));
  }
}
