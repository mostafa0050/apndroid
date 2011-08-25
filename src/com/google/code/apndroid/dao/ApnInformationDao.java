package com.google.code.apndroid.dao;

import com.google.code.apndroid.stats.ApnInformation;

import java.util.List;

/**
 * @author pavlov
 * @since 25.08.11
 */
public interface ApnInformationDao {

    List<ApnInformation> findAllApns();

    Long getCurrentActiveApnId();

}
