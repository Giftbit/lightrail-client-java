package com.lightrail.params.currencies;

import java.util.Optional;

/**
 * Update a Currency.  Leave fields as null to not change them,
 * set to Optional.empty() to set to null, set Optional.of(value)
 * to change.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class UpdateCurrencyParams {
    public Optional<String> name;
    public Optional<String> symbol;
    public Optional<Integer> decimalPlaces;
}
