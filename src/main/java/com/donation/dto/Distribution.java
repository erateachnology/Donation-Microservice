package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Distribution {
    private Overheads overheads;
    private Development development;
    private List<Missions> missions;
}
