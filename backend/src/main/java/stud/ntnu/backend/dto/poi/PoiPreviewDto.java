package stud.ntnu.backend.dto.poi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoiPreviewDto {
    private Integer id;
    private String name;
    private String type;

    public static PoiPreviewDto fromEntity(PoiItemDto poi) {
        return new PoiPreviewDto(
                poi.getId(),
                poi.getName(),
                poi.getPoiTypeName()
        );
    }
}
