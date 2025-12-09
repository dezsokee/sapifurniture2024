package ro.sapientia.furniture.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.sapientia.furniture.model.dto.CutRequestDTO;
import ro.sapientia.furniture.model.dto.FurnitureBodyDTO;

/**
 * Integration tests for validation on the /cut endpoint.
 * Tests validation rules like no negative numbers and required fields.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class FurnitureCutValidationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cutEndpoint_returns400_whenSheetWidthIsNull() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(null);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet width is required")));
    }

    @Test
    void cutEndpoint_returns400_whenSheetHeightIsNull() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(null);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet height is required")));
    }

    @Test
    void cutEndpoint_returns400_whenSheetWidthIsZero() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(0);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet width must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenSheetWidthIsNegative() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(-10);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet width must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenSheetHeightIsZero() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(0);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet height must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenSheetHeightIsNegative() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(-20);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Sheet height must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenElementsListIsEmpty() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);
        req.setElements(List.of());

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Elements list cannot be empty")));
    }

    @Test
    void cutEndpoint_returns400_whenElementWidthIsZero() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(0);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Width must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenElementWidthIsNegative() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(-5);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Width must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenElementHeightIsZero() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(0);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Height must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenElementHeightIsNegative() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(-15);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Height must be positive")));
    }

    @Test
    void cutEndpoint_returns400_whenElementIdIsNull() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(null);
        element.setWidth(10);
        element.setHeight(10);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Furniture element ID is required")));
    }

    @Test
    void cutEndpoint_returns400_whenMultipleValidationErrors() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(-10); // Invalid
        req.setSheetHeight(0);   // Invalid

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(null);     // Invalid
        element.setWidth(-5);    // Invalid
        element.setHeight(0);    // Invalid
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void cutEndpoint_returns200_whenAllValidationsPassed() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        element.setDepth(5);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void cutEndpoint_accepts_depthZero() throws Exception {
        // Depth can be 0 or positive (depth >= 0)
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        element.setDepth(0); // Zero is valid for depth
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void cutEndpoint_returns400_whenDepthIsNegative() throws Exception {
        CutRequestDTO req = new CutRequestDTO();
        req.setSheetWidth(100);
        req.setSheetHeight(100);

        FurnitureBodyDTO element = new FurnitureBodyDTO();
        element.setId(1L);
        element.setWidth(10);
        element.setHeight(10);
        element.setDepth(-5);
        req.setElements(List.of(element));

        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/furniture/cut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Depth cannot be negative")));
    }
}

