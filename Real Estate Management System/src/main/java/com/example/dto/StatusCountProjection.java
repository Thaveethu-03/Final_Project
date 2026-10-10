package com.example.dto;

/** Interface projection for the native query report: count per status. */
public interface StatusCountProjection {

    String getStatus();

    Long getTotal();
}
