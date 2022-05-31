package com.telebott.movie2java.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class VideoData {
    private long length;
    private long bitrate;
    private String resolution;
}
