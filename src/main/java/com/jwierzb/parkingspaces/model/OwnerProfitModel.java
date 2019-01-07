package com.jwierzb.parkingspaces.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
public class OwnerProfitModel {

    @NotNull
    Integer parkingFinished;

    @NotNull
    Integer parkingInProgress;


    @NotNull
    Double totalProfit;

}
