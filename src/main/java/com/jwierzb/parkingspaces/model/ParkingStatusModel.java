package com.jwierzb.parkingspaces.model;

import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ParkingStatusModel {

    @NotNull
    String description;

}
