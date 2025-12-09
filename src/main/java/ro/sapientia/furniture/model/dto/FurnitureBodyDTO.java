package ro.sapientia.furniture.model.dto;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Data Transfer Object for furniture body.
 * Used for API requests and responses.
 */
public class FurnitureBodyDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull(message = "Furniture element ID is required")
	private Long id;

	@Min(value = 1, message = "Width must be positive")
	private int width;

	@Min(value = 1, message = "Height must be positive")
	private int height;

	@Min(value = 0, message = "Depth cannot be negative")
	private int depth;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "FurnitureBody [id=" + id + ", width=" + width + ", heigth=" + height + ", depth=" + depth + "]";
	}

}
