package com.jwierzb.parkingspaces.controller;

import com.jwierzb.parkingspaces.dao.*;
import com.jwierzb.parkingspaces.entity.UserEntity;
import com.jwierzb.parkingspaces.model.OwnerProfitModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/owner")
public class OwnerController {
    @Autowired
    UserDao users;
    @Autowired
    DriverTypeDao driverTypeDao;
    @Autowired
    CurrencyDao currencyDao;
    @Autowired
    RoleDao roleDao;
    @Autowired
    VehicleDao vehicleDao;
    @Autowired
    TransactionDao transactionDao;

    /**
     *
     * @param date is in YYYY-MM-DD form
     * @return
     * @throws DateTimeParseException
     */
    @GetMapping("/{date}/profit")
    private OwnerProfitModel getProfit(Principal principal,
                                       @PathVariable String date) throws DateTimeParseException
    {
        LocalDate dayDate = LocalDate.parse(date);

        OwnerProfitModel ownerProfitModel =
                OwnerProfitModel.builder()
                .parkingFinished(transactionDao.countAllByEndTimeBetween(dayDate.atStartOfDay(), dayDate.atStartOfDay().plusDays(1L)))
                .parkingInProgress(transactionDao.countAllByEndTimeNull())
                .totalProfit((transactionDao.findAllByEndTimeBetween(dayDate.atStartOfDay(), dayDate.atStartOfDay().plusDays(1L))).stream().mapToDouble(x -> x.getPrice()).sum())
                .build();
        return ownerProfitModel;
    }
}
