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
import stud.ntnu.backend.dto.quiz.QuizQuestionResponseDto;
import stud.ntnu.backend.dto.quiz.QuizAnswerResponseDto;
import stud.ntnu.backend.dto.quiz.CreateQuizQuestionDto;
import stud.ntnu.backend.dto.quiz.CreateQuizAnswerDto;
import stud.ntnu.backend.model.Quiz;
import stud.ntnu.backend.model.UserQuizAttempt;
import stud.ntnu.backend.model.UserQuizAnswer;
import stud.ntnu.backend.model.QuizQuestion;
import stud.ntnu.backend.model.QuizAnswer;
import stud.ntnu.backend.repository.QuizRepository;
import stud.ntnu.backend.repository.UserQuizAttemptRepository;
import stud.ntnu.backend.repository.UserQuizAnswerRepository;
import stud.ntnu.backend.repository.QuizQuestionRepository;
import stud.ntnu.backend.repository.QuizAnswerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

  private final QuizRepository quizRepository;
  private final UserQuizAttemptRepository userQuizAttemptRepository;
  private final UserQuizAnswerRepository userQuizAnswerRepository;
  private final QuizQuestionRepository quizQuestionRepository;
  private final QuizAnswerRepository quizAnswerRepository;

  @Autowired
  public QuizService(QuizRepository quizRepository,
      UserQuizAttemptRepository userQuizAttemptRepository,
      UserQuizAnswerRepository userQuizAnswerRepository,
      QuizQuestionRepository quizQuestionRepository, QuizAnswerRepository quizAnswerRepository) {
    this.quizRepository = quizRepository;
    this.userQuizAttemptRepository = userQuizAttemptRepository;
    this.userQuizAnswerRepository = userQuizAnswerRepository;
    this.quizQuestionRepository = quizQuestionRepository;
    this.quizAnswerRepository = quizAnswerRepository;
  }

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

  public void archiveQuiz(Long quizId) {
    Optional<Quiz> quizOpt = quizRepository.findById(quizId);
    if (quizOpt.isEmpty()) {
      throw new IllegalArgumentException("Quiz not found");
    }
    Quiz quiz = quizOpt.get();
    quiz.setStatus("archived");
    quizRepository.save(quiz);
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

  public List<QuizQuestionResponseDto> getQuestionsById(Long quizId) {
    List<QuizQuestion> questions = quizQuestionRepository.findAllByQuizId(quizId);
    return questions.stream()
        .map(q -> new QuizQuestionResponseDto(q.getId(), q.getQuestionBody()))
        .collect(Collectors.toList());
  }

  public List<QuizAnswerResponseDto> getAnswersByQuestionId(Long questionId) {
    List<QuizAnswer> answers = quizAnswerRepository.findAllByQuestionId(questionId);
    return answers.stream()
        .map(a -> new QuizAnswerResponseDto(a.getId(), a.getQuizId(), a.getQuestionId(),
            a.getAnswerBody()))
        .collect(Collectors.toList());
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

  public void saveQuizQuestion(CreateQuizQuestionDto dto) {
    QuizQuestion question = new QuizQuestion();
    question.setQuizId(dto.getQuizId());
    question.setQuestionBody(dto.getQuestionBody());
    question.setPosition(dto.getPosition());
    quizQuestionRepository.save(question);
  }

  public void saveQuizAnswer(CreateQuizAnswerDto dto) {
    QuizAnswer answer = new QuizAnswer();
    answer.setQuizId(dto.getQuizId());
    answer.setQuestionId(dto.getQuestionId());
    answer.setAnswerBody(dto.getAnswerBody());
    answer.setIsCorrect(dto.getIsCorrect());
    quizAnswerRepository.save(answer);
  }

  public void deleteQuizQuestion(Long questionId) {
    if (!quizQuestionRepository.existsById(questionId)) {
      throw new IllegalArgumentException("Quiz question not found");
    }
    quizQuestionRepository.deleteById(questionId);
  }

  public QuizQuestion getQuizQuestionById(Long questionId) {
    return quizQuestionRepository.findById(questionId)
        .orElseThrow(() -> new IllegalArgumentException("Quiz question not found"));
  }

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
}
