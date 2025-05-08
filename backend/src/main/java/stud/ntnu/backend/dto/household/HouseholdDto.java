package stud.ntnu.backend.dto.household;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing household information.
 * This class encapsulates all relevant data about a household including its location,
 * members, and basic information.
 */
@Getter
@Setter
public class HouseholdDto {

    /**
     * The unique identifier of the household.
     */
    private Integer id;

    /**
     * The name of the household.
     */
    private String name;

    /**
     * The physical address of the household.
     */
    private String address;

    /**
     * The number of people living in the household.
     */
    private Integer populationCount;

    /**
     * The geographical latitude coordinate of the household.
     */
    private BigDecimal latitude;

    /**
     * The geographical longitude coordinate of the household.
     */
    private BigDecimal longitude;

    /**
     * List of members associated with this household.
     */
    private List<HouseholdMemberDto> members;

    /**
     * Default constructor required for JSON deserialization.
     */
    public HouseholdDto() {
    }

    /**
     * Constructs a new HouseholdDto with all fields.
     *
     * @param id the unique identifier of the household
     * @param name the name of the household
     * @param address the physical address of the household
     * @param populationCount the number of people living in the household
     * @param latitude the geographical latitude coordinate
     * @param longitude the geographical longitude coordinate
     * @param members list of household members
     */
    public HouseholdDto(Integer id, String name, String address, Integer populationCount,
            BigDecimal latitude, BigDecimal longitude, List<HouseholdMemberDto> members) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.populationCount = populationCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.members = members;
    }

    /**
     * Constructs a new HouseholdDto without members.
     *
     * @param id the unique identifier of the household
     * @param name the name of the household
     * @param address the physical address of the household
     * @param populationCount the number of people living in the household
     * @param latitude the geographical latitude coordinate
     * @param longitude the geographical longitude coordinate
     */
    public HouseholdDto(Integer id, String name, String address, Integer populationCount,
            BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.populationCount = populationCount;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}