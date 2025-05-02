package stud.ntnu.backend.service.gamification.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizBasicInfoDto;
import stud.ntnu.backend.model.gamification.quiz.Quiz;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAttempt;
import stud.ntnu.backend.model.gamification.quiz.UserQuizAnswer;
import stud.ntnu.backend.model.gamification.quiz.QuizAnswer;
import stud.ntnu.backend.repository.gamification.quiz.QuizRepository;
import stud.ntnu.backend.repository.gamification.quiz.UserQuizAttemptRepository;
import stud.ntnu.backend.repository.gamification.quiz.UserQuizAnswerRepository;
import stud.ntnu.backend.repository.gamification.quiz.QuizQuestionRepository;
import stud.ntnu.backend.repository.gamification.quiz.QuizAnswerRepository;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user quiz attempts, answers, and retrieving quiz-related information for users.
 * Provides business logic for creating attempts, recording answers, and fetching quiz attempt summaries and statistics.
 */
@Service
public class UserQuizService {

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
   * Constructs a new UserQuizService with the required repositories.
   *
   * @param quizRepository             the quiz repository
   * @param userQuizAttemptRepository  the user quiz attempt repository
   * @param userQuizAnswerRepository   the user quiz answer repository
   * @param quizQuestionRepository     the quiz question repository
   * @param quizAnswerRepository       the quiz answer repository
   */
  @Autowired
  public UserQuizService(QuizRepository quizRepository,
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
   * Creates a new user quiz attempt and saves it to the repository.
   *
   * @param quizId the ID of the quiz being attempted
   * @param userId the ID of the user attempting the quiz
   */
  public void createUserQuizAttempt(Long quizId, Integer userId) {
    UserQuizAttempt attempt = new UserQuizAttempt();
    attempt.setUserId(userId);
    attempt.setQuizId(quizId);
    attempt.setCompletedAt(null);
    userQuizAttemptRepository.save(attempt);
  }

  /**
   * Records a user's answer to a quiz question for a specific attempt.
   *
   * @param dto the DTO containing user quiz answer data (userQuizAttemptId, quizId, questionId, answerId)
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
   * @return a page of QuizAttemptSummaryDto objects containing attempt id and completion time
   */
  public Page<QuizAttemptSummaryDto> getQuizAttemptsByQuizId(Long quizId, Integer userId,
      Pageable pageable) {
    return userQuizAttemptRepository.findByUserIdAndQuizId(userId, quizId, pageable)
        .map(a -> new QuizAttemptSummaryDto(a.getId(), a.getCompletedAt()));
  }

  /**
   * Retrieves basic information for all quizzes that a user has attempted, paginated.
   *
   * @param userId   the ID of the user
   * @param pageable the pagination information
   * @return a page of QuizBasicInfoDto objects containing quiz id, name, status, and question count
   */
  public Page<QuizBasicInfoDto> getBasicInfoForAttemptedQuizzes(Integer userId, Pageable pageable) {
    List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserId(userId);
    Set<Long> quizIds = attempts.stream().map(UserQuizAttempt::getQuizId)
        .collect(Collectors.toSet());
    if (quizIds.isEmpty()) {
      return Page.empty(pageable);
    }
    List<Long> quizIdList = new ArrayList<>(quizIds);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), quizIdList.size());
    if (start > end) {
      return Page.empty(pageable);
    }
    List<Long> pagedQuizIds = quizIdList.subList(start, end);
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

  /**
   * Calculates the total number of correct answers for a given user quiz attempt.
   *
   * @param attemptId the ID of the user quiz attempt
   * @return the number of correct answers for the attempt
   */
  public int getTotalCorrectAnswers(Long attemptId) {
    List<UserQuizAnswer> userAnswers = userQuizAnswerRepository.findAllByUserQuizAttemptId(
        attemptId);
    int correctCount = 0;
    for (UserQuizAnswer userAnswer : userAnswers) {
      Optional<QuizAnswer> quizAnswerOpt = quizAnswerRepository.findById(userAnswer.getAnswerId());
      if (quizAnswerOpt.isPresent() && Boolean.TRUE.equals(quizAnswerOpt.get().getIsCorrect())) {
        correctCount++;
      }
    }
    return correctCount;
  }
} 