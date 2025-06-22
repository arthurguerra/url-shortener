package com.arthur.urlshortener.common;

import com.arthur.urlshortener.common.validation.ValidFieldValue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private int size = 10;

    @ValidFieldValue(allowedFields = {"shortCode", "originalUrl", "createdAt", "clicks"})
    private String sortBy = "createdAt";

    @ValidFieldValue(allowedFields = {"ASC", "DESC", "asc", "desc"})
    private String sortDirection = Sort.Direction.DESC.name();

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    }
}
