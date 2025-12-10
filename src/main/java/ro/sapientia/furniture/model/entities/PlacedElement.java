package ro.sapientia.furniture.model.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Domain model representing a furniture element with its calculated position on the cutting sheet.
 */
@Entity
@Table(name = "placed_elements")
public class PlacedElement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "furniture_body_id")
    private Long furnitureBodyId;

    @Column(name = "pos_x")
    private Integer x;

    @Column(name = "pos_y")
    private Integer y;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cutting_sheet_id")
    private CuttingSheet cuttingSheet;

    public PlacedElement() {
    }

    public PlacedElement(Long furnitureBodyId, Integer x, Integer y, Integer width, Integer height) {
        this.furnitureBodyId = furnitureBodyId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFurnitureBodyId() {
        return furnitureBodyId;
    }

    public void setFurnitureBodyId(Long furnitureBodyId) {
        this.furnitureBodyId = furnitureBodyId;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public CuttingSheet getCuttingSheet() {
        return cuttingSheet;
    }

    public void setCuttingSheet(CuttingSheet cuttingSheet) {
        this.cuttingSheet = cuttingSheet;
    }

    @Override
    public String toString() {
        return "PlacedElement [id=" + id + ", furnitureBodyId=" + furnitureBodyId + ", x=" + x + ", y=" + y +
               ", width=" + width + ", height=" + height + "]";
    }
}

