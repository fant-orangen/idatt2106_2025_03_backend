package stud.ntnu.backend.dto.user;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning a user's history (completed activities and reflections).
 */
@Setter
@Getter
public class UserHistoryDto {
  // Getters and setters
  private List<GamificationActivityDto> completedActivities;
    private List<ReflectionDto> reflections;
    
    // Default constructor
    public UserHistoryDto() {
    }
    
    // Constructor with all fields
    public UserHistoryDto(List<GamificationActivityDto> completedActivities, List<ReflectionDto> reflections) {
        this.completedActivities = completedActivities;
        this.reflections = reflections;
    }

  /**
     * DTO for a gamification activity.
     */
    public static class GamificationActivityDto {
        private Integer id;
        private String title;
        private String description;
        private Integer points;
        private String completedAt;
        
        // Default constructor
        public GamificationActivityDto() {
        }
        
        // Constructor with all fields
        public GamificationActivityDto(Integer id, String title, String description, Integer points, String completedAt) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.points = points;
            this.completedAt = completedAt;
        }
        
        // Getters and setters
        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getPoints() {
            return points;
        }
        
        public void setPoints(Integer points) {
            this.points = points;
        }
        
        public String getCompletedAt() {
            return completedAt;
        }
        
        public void setCompletedAt(String completedAt) {
            this.completedAt = completedAt;
        }
    }
    
    /**
     * DTO for a reflection.
     */
    public static class ReflectionDto {
        private Integer id;
        private String title;
        private String content;
        private Boolean shared;
        private String createdAt;
        
        // Default constructor
        public ReflectionDto() {
        }
        
        // Constructor with all fields
        public ReflectionDto(Integer id, String title, String content, Boolean shared, String createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.shared = shared;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public Boolean getShared() {
            return shared;
        }
        
        public void setShared(Boolean shared) {
            this.shared = shared;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
}