package stud.ntnu.backend.service.gamification.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stud.ntnu.backend.dto.quiz.CreateUserQuizAnswerDto;
import stud.ntnu.backend.dto.quiz.QuizAttemptSummaryDto;
import stud.ntnu.backend.dto.quiz.QuizBasicInfoDto;
import stud.ntnu.backend.model.Quiz;
import stud.ntnu.backend.model.UserQuizAttempt;
import stud.ntnu.backend.model.UserQuizAnswer;
import stud.ntnu.backend.model.QuizAnswer;
import stud.ntnu.backend.repository.QuizRepository;
import stud.ntnu.backend.repository.UserQuizAttemptRepository;
import stud.ntnu.backend.repository.UserQuizAnswerRepository;
import stud.ntnu.backend.repository.QuizQuestionRepository;
import stud.ntnu.backend.repository.QuizAnswerRepository;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserQuizService {

  private final QuizRepository quizRepository;
  private final UserQuizAttemptRepository userQuizAttemptRepository;
  private final UserQuizAnswerRepository userQuizAnswerRepository;
  private final QuizQuestionRepository quizQuestionRepository;
  private final QuizAnswerRepository quizAnswerRepository;

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

  public void createUserQuizAttempt(Long quizId, Integer userId) {
    UserQuizAttempt attempt = new UserQuizAttempt();
    attempt.setUserId(userId);
    attempt.setQuizId(quizId);
    attempt.setCompletedAt(null);
    userQuizAttemptRepository.save(attempt);
  }

  public void createUserQuizAnswer(CreateUserQuizAnswerDto dto) {
    UserQuizAnswer answer = new UserQuizAnswer();
    answer.setUserQuizAttemptId(dto.getUserQuizAttemptId());
    answer.setQuizId(dto.getQuizId());
    answer.setQuestionId(dto.getQuestionId());
    answer.setAnswerId(dto.getAnswerId());
    userQuizAnswerRepository.save(answer);
  }

  public Page<QuizAttemptSummaryDto> getQuizAttemptsByQuizId(Long quizId, Integer userId,
      Pageable pageable) {
    return userQuizAttemptRepository.findByUserIdAndQuizId(userId, quizId, pageable)
        .map(a -> new QuizAttemptSummaryDto(a.getId(), a.getCompletedAt()));
  }

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