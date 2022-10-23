package com.telebott.movie2java.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class InfoData {
    private long count = 0;
    private int type = 0;
    private long addTime=0;
    private long updateTime=0;
}
