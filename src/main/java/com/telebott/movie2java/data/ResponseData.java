package com.telebott.movie2java.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class ResponseData {
    private int code = 200;
    private String message;
    private String data;
}
