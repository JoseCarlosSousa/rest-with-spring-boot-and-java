package pt.seixal.carlos.request.converters;

import pt.seixal.carlos.exceptions.UnsupportedMathOperationException;

public class NumberConverter {

    public static Double convertToDouble(String value) throws UnsupportedMathOperationException {
        if (!isNumeric(value)) {
            throw new UnsupportedMathOperationException("Please set a numeric value");
        }
        String number = value.replace(",",".");
        return Double.parseDouble(number);
    }

    private static boolean isNumeric(String value) {
        if ( value == null || value.isBlank()) {
            return false;
        }
        String number = value.replace(",",".");
        return (number.matches("[-+]?[0-9]*\\.?[0-9]+"));
    }
}
