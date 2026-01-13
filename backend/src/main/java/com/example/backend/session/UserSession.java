package com.example.backend.session;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession implements Serializable {

    private String sessionId;
    private String currentStep;
    private Long eventId;
    private List<SeatSelection> selectedSeats;

}
