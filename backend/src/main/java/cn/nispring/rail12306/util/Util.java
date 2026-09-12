package cn.nispring.rail12306.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String[]> readCsv(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("not a file: " + path);
        }
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }

    public static boolean isValidChinaIdNo(String idNo) {
        if (idNo.length() != 18) {
            return false;
        }
        for (int i = 0; i < 17; i += 1) {
            if (!Character.isDigit(idNo.charAt(i))) {
                return false;
            }
        }

        // 地址码暂不做校验

        String birthCode = idNo.substring(6, 14);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            formatter.parse(birthCode);
        } catch (DateTimeParseException e) {
            return false;
        }

        // 计算校验码
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int sum = 0;
        for (int i = 0; i < 17; i += 1) {
            sum += (idNo.charAt(i) - '0') * weights[i];
        }
        char[] verifyCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        char realCode = verifyCodes[sum % 11];
        char givenCode = Character.toUpperCase(idNo.charAt(17));
        return realCode == givenCode;
    }

    public static String parsePhoneE164(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber number;
        try {
            number = phoneUtil.parse(phone, null);
        } catch (NumberParseException e) {
            return null;
        }
        if (phoneUtil.isValidNumber(number)) {
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } else {
            return null;
        }
    }
}
