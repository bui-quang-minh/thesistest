package vn.edu.fpt.thesis_test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Step {
    private String actionType;
    private String on;
    private int duration;
    private int tries;
}
