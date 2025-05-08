package stud.ntnu.backend.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a user's history.
 * This class encapsulates both completed gamification activities and reflections
 * associated with a user, providing a comprehensive view of their engagement history.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryDto {

    /**
     * List of completed gamification activities for the user.
     */
    private List<GamificationActivityDto> completedActivities;

    /**
     * List of reflections created by the user.
     */
    private List<ReflectionDto> reflections;

    /**
     * Data Transfer Object (DTO) representing a completed gamification activity.
     * This class encapsulates the details of an activity that a user has completed,
     * including its identification, description, and completion information.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GamificationActivityDto {

        /**
         * Unique identifier for the activity.
         */
        private Integer id;

        /**
         * Title of the completed activity.
         */
        private String title;

        /**
         * Detailed description of the activity.
         */
        private String description;

        /**
         * Points earned for completing this activity.
         */
        private Integer points;

        /**
         * Timestamp when the activity was completed.
         */
        private String completedAt;
    }

    /**
     * Data Transfer Object (DTO) representing a user reflection.
     * This class encapsulates the content and metadata of a reflection entry,
     * including its sharing status and creation timestamp.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReflectionDto {

        /**
         * Unique identifier for the reflection.
         */
        private Integer id;

        /**
         * Title of the reflection.
         */
        private String title;

        /**
         * Main content of the reflection.
         */
        private String content;

        /**
         * Indicates whether the reflection is shared or private.
         */
        private Boolean shared;

        /**
         * Timestamp when the reflection was created.
         */
        private String createdAt;
    }
}