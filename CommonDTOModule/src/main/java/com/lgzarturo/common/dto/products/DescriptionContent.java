package com.lgzarturo.common.dto.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionContent {
    private String content;
    private ContentType contentType;
}
