package com.rdc.drawing.utils;

import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

    /**
     * 随机获取字符串长度
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(base.length());
            sb.append(base.charAt(num));
        }
        return sb.toString();
    }

    /**
     * 随机生成纯数字字符串(自定义长度)
     *
     * @param length 长度
     * @return
     */
    public static String getRandomNumberString(int length) {
        String radStr = "0123456789";
        StringBuilder generateRandStr = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int randNum = rand.nextInt(10);
            generateRandStr.append(radStr.substring(randNum, randNum + 1));
        }
        return generateRandStr + "";
    }

    /**
     * String转UTF-8编码
     *
     * @param string 需要编码的字字符串
     * @return 编码后的字符串
     */
    public static String getStringToUtf(String string) {
        String stringToUtf = null;
        try {
            stringToUtf = URLEncoder.encode(string, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stringToUtf;
    }

    /**
     * 手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isMobileNumber(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 加密手机号码*** 明文前3后4
     *
     * @return
     */
    public static String encryptPhoneNum(String phoneNum) {
        if (phoneNum == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(phoneNum);
        stringBuilder.replace(3, 7, "****");
        return stringBuilder.toString();
    }


    /**
     * 加密卡号为* 明文为前2后4
     *
     * @return
     */
    public static String encryptCardNum(String cardNum) {
        String cardStart = cardNum.substring(0, 2);
        String cardEnd = cardNum.substring(cardNum.length() - 4,
                cardNum.length());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cardStart);
        for (int i = 0; i < cardNum.length() - 6; i++) {
            stringBuilder.append("*");
        }
        stringBuilder.append(cardEnd);
        return stringBuilder.toString();
    }

    /**
     * 格式化金额  保留小数点后两位 到分单位
     *
     * @param amt
     * @return
     */
    public static String formatDouble(double amt) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(amt);
    }

//    // 打印所有的 intent extra 数据
//    public static String printBundle(Bundle bundle) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : bundle.keySet()) {
//            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//                sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
//            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
//                sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
//            } else {
//                sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
//            }
//        }
//        return sb.toString();
//    }

    /**
     * 将分转换为元
     *
     * @param amountLong
     * @return
     */
    public static String parseAmountLong2Str(Long amountLong) {
        if (amountLong == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        double d = amountLong / 100d;
        return df.format(d);
    }

    /**
     * 文件保存地址
     *
     * @return
     */
    public static String getFilePath(String filePackage) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + "" + File.separator + filePackage;
    }

}