package com.google.code.apndroid.dao;

/**
 * @author Dmitry Pavlov
 * @author Martin Adamek
 */
public interface ConnectionDao {

    /**
     * @return {@code true} if data transfer is enabled and {@code false} otherwise
     */
    boolean isDataEnabled();

    /**
     * @return {@code true} if mms transfer is enabled and {@code false} otherwise
     */
    boolean isMmsEnabled();

    /**
     * Performs switching data connection to passed state.
     * Mms target state should be defined by dao itself.
     *
     * @param enable target data connection state. If value is {@code true} then data transfer should be enabled
     *               and {@code false} means that data should be disabled
     * @return {@code true} if data was successfully switched to target state and {@code false} otherwise
     */
    boolean setDataEnabled(boolean enable);

    /**
     * Performs switching data connection to passed state.
     * Mms target state should be defined by dao itself.
     *
     * @param enableData target data connection state. If value is {@code true} then data transfer should be enabled
     *                   and {@code false} means that data should be disabled
     * @param enableMms  target mms connection state. If value is {@code true} then mms transfer should be enabled
     *                   and {@code false} means that mms should be disabled
     * @return {@code true} if data was successfully switched to target state and {@code false} otherwise
     */
    boolean setDataEnabled(boolean enableData, boolean enableMms);

    /**
     * Performs switching mms connection to passed state.
     * Data state should be defined by dao itself
     *
     * @param enable target mms connection state. If value is {@code true} then mms transfer should be enabled
     *               and {@code false} means that mms should be disabled
     * @return {@code true} if mms was successfully switched to target state and {@code false} otherwise
     */
    boolean setMmsEnabled(boolean enable);

}
