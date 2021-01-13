package net.gini.android.vision.internal.qrcode;

import android.text.TextUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Checks that an IBAN string conforms to the IBAN standard.
 */
class IBANValidator {

    private static final Map<String, Integer> COUNTRY_IBAN_MAP = new HashMap<>(); // NOPMD

    static {
        COUNTRY_IBAN_MAP.put("AL", 28);
        COUNTRY_IBAN_MAP.put("AD", 24);
        COUNTRY_IBAN_MAP.put("AT", 20);
        COUNTRY_IBAN_MAP.put("AZ", 28);
        COUNTRY_IBAN_MAP.put("BH", 22);
        COUNTRY_IBAN_MAP.put("BE", 16);
        COUNTRY_IBAN_MAP.put("BA", 20);
        COUNTRY_IBAN_MAP.put("BR", 29);
        COUNTRY_IBAN_MAP.put("BG", 22);
        COUNTRY_IBAN_MAP.put("CR", 21);
        COUNTRY_IBAN_MAP.put("HR", 21);
        COUNTRY_IBAN_MAP.put("CY", 28);
        COUNTRY_IBAN_MAP.put("CZ", 24);
        COUNTRY_IBAN_MAP.put("DK", 18);
        COUNTRY_IBAN_MAP.put("DO", 28);
        COUNTRY_IBAN_MAP.put("EE", 20);
        COUNTRY_IBAN_MAP.put("FO", 18);
        COUNTRY_IBAN_MAP.put("FI", 18);
        COUNTRY_IBAN_MAP.put("FR", 27);
        COUNTRY_IBAN_MAP.put("GE", 22);
        COUNTRY_IBAN_MAP.put("DE", 22);
        COUNTRY_IBAN_MAP.put("GI", 23);
        COUNTRY_IBAN_MAP.put("GB", 22);
        COUNTRY_IBAN_MAP.put("GR", 27);
        COUNTRY_IBAN_MAP.put("GL", 18);
        COUNTRY_IBAN_MAP.put("GT", 28);
        COUNTRY_IBAN_MAP.put("HU", 28);
        COUNTRY_IBAN_MAP.put("IS", 26);
        COUNTRY_IBAN_MAP.put("IE", 22);
        COUNTRY_IBAN_MAP.put("IL", 23);
        COUNTRY_IBAN_MAP.put("IT", 27);
        COUNTRY_IBAN_MAP.put("KZ", 20);
        COUNTRY_IBAN_MAP.put("KW", 30);
        COUNTRY_IBAN_MAP.put("LV", 21);
        COUNTRY_IBAN_MAP.put("LB", 28);
        COUNTRY_IBAN_MAP.put("LT", 20);
        COUNTRY_IBAN_MAP.put("LU", 20);
        COUNTRY_IBAN_MAP.put("MK", 19);
        COUNTRY_IBAN_MAP.put("MT", 31);
        COUNTRY_IBAN_MAP.put("MR", 27);
        COUNTRY_IBAN_MAP.put("MU", 30);
        COUNTRY_IBAN_MAP.put("MD", 24);
        COUNTRY_IBAN_MAP.put("MC", 27);
        COUNTRY_IBAN_MAP.put("ME", 22);
        COUNTRY_IBAN_MAP.put("NL", 18);
        COUNTRY_IBAN_MAP.put("NO", 15);
        COUNTRY_IBAN_MAP.put("PK", 24);
        COUNTRY_IBAN_MAP.put("PS", 29);
        COUNTRY_IBAN_MAP.put("PL", 28);
        COUNTRY_IBAN_MAP.put("PT", 25);
        COUNTRY_IBAN_MAP.put("RO", 24);
        COUNTRY_IBAN_MAP.put("SM", 27);
        COUNTRY_IBAN_MAP.put("SA", 24);
        COUNTRY_IBAN_MAP.put("RS", 22);
        COUNTRY_IBAN_MAP.put("SK", 24);
        COUNTRY_IBAN_MAP.put("SI", 19);
        COUNTRY_IBAN_MAP.put("ES", 24);
        COUNTRY_IBAN_MAP.put("SE", 24);
        COUNTRY_IBAN_MAP.put("TN", 24);
        COUNTRY_IBAN_MAP.put("TR", 26);
        COUNTRY_IBAN_MAP.put("AE", 23);
        COUNTRY_IBAN_MAP.put("VG", 24);
        COUNTRY_IBAN_MAP.put("CH", 21);
    }

    private final Pattern mPattern;

    IBANValidator() {
        mPattern = Pattern.compile("^[A-Z0-9]+$");
    }

    /**
     * Verifies, that the IBAN string conforms to the IBAN standard.
     *
     * @param iban an IBAN string
     * @throws IllegalIBANException if the IBAN was not valid
     */
    void validate(@Nullable final String iban) throws IllegalIBANException {
        if (TextUtils.isEmpty(iban)) {
            throw new IllegalIBANException(IBANError.EMPTY);
        }

        final String sanitizedIban = sanitizeIBAN(iban);

        final Matcher matcher = mPattern.matcher(sanitizedIban);
        if (!matcher.matches()) {
            throw new IllegalIBANException(IBANError.INVALID_CHARACTERS);
        }

        validateCountryAndChecksum(sanitizedIban);
        validateLength(sanitizedIban);
        validateChecksum(sanitizedIban);
    }

    private String getBban(@NonNull final String iban) {
        return iban.substring(4, iban.length());
    }

    private String getCheckDigit(@NonNull final String iban) {
        return iban.substring(2, 4);
    }

    private String getChecksum(@NonNull final String iban) {
        final String countryCodeNumbers = lettersToNumbers(getCountryCode(iban));
        final String checkDigit = getCheckDigit(iban);
        final String bban = lettersToNumbers(getBban(iban));
        return bban + countryCodeNumbers + checkDigit;
    }

    private String getCountryCode(@NonNull final String iban) {
        return iban.substring(0, 2);
    }

    private String lettersToNumbers(@NonNull final String letters) {
        final StringBuilder numbers = new StringBuilder();
        final char[] chars = letters.toCharArray();
        for (final char character : chars) {
            final int number = character - 55;
            if (number >= 10 && number <= 35) {
                numbers.append(String.valueOf(number));
            } else {
                numbers.append(String.valueOf(character));
            }
        }
        return numbers.toString();
    }

    private String sanitizeIBAN(@NonNull final String iban) {
        String sanitizedIban = iban.trim();
        sanitizedIban = sanitizedIban.replace(" ", "");
        sanitizedIban = sanitizedIban.toUpperCase(Locale.ENGLISH);
        return sanitizedIban;
    }

    private void validateChecksum(@NonNull final String iban) throws IllegalIBANException {
        final BigInteger bigInt = new BigInteger(getChecksum(iban));
        final BigInteger[] divisionResult = bigInt.divideAndRemainder(new BigInteger("97"));
        if (divisionResult[1].compareTo(BigInteger.ONE) != 0) {
            throw new IllegalIBANException(IBANError.INVALID_CHECKSUM);
        }
    }

    private void validateCountryAndChecksum(@NonNull final String iban)
            throws IllegalIBANException {
        if (!iban.matches("^[A-Z]{2}[0-9]{2}.*")) {
            throw new IllegalIBANException(IBANError.INVALID_FORMAT);
        }
    }

    private void validateLength(@NonNull final String iban) throws IllegalIBANException {
        final Integer requiredLength = COUNTRY_IBAN_MAP.get(getCountryCode(iban));
        if (requiredLength == null) {
            throw new IllegalIBANException(IBANError.INVALID_COUNTRY);
        }
        if (iban.length() > requiredLength) {
            throw new IllegalIBANException(IBANError.TOO_LONG);
        }
        if (iban.length() < requiredLength) {
            throw new IllegalIBANException(IBANError.TOO_SHORT);
        }
    }

    enum IBANError {
        EMPTY,
        INVALID_CHARACTERS,
        UNKNOWN_STRING_ERROR,
        INVALID_FORMAT,
        INVALID_COUNTRY,
        TOO_LONG,
        TOO_SHORT,
        INVALID_CHECKSUM
    }

    /**
     * Exception containing an {@link IBANError} for information about the reason why an
     * IBAN was not valid.
     */
    static class IllegalIBANException extends RuntimeException {

        private final IBANError mIBANError;

        IllegalIBANException(
                final IBANError ibanError) {
            mIBANError = ibanError;
        }

        IBANError getIBANError() {
            return mIBANError;
        }

        @Override
        public String getMessage() {
            return "IBAN error: " + mIBANError.toString();
        }
    }
}
