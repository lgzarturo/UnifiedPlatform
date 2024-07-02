package com.lgzarturo.common.dto.products;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DescriptionContent {
    private String content;
    private ContentType contentType;
}
