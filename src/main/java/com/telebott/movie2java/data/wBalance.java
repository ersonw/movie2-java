package com.telebott.movie2java.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public
class wBalance {
    private int status;
    private double balance;
    private double transferable;
}

