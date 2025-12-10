package ro.sapientia.furniture.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "cutting_sheet")
public class CuttingSheet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "sheet_width")
    private int width;

    @Column(name = "sheet_height")
    private int height;

    @OneToMany(mappedBy = "cuttingSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacedElement> placedElements = new ArrayList<>();

    public void addPlacedElement(PlacedElement element) {
        placedElements.add(element);
        element.setCuttingSheet(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public List<PlacedElement> getPlacedElements() { return placedElements; }
    public void setPlacedElements(List<PlacedElement> placedElements) { this.placedElements = placedElements; }
}
