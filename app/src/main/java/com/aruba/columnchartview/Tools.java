package com.aruba.columnchartview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者　　: aruba
 * 创建时间:2017/6/6　10:54
 * <p>
 * 功能介绍：
 */
public class Tools {
    /****
     * @param context
     * @param size
     * @return
     * @function dp  changeTo  px
     */

    public static int dpToPx(Context context, int size) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 显示提示信息
     **/
    public static void showToast(Context context, String str) {

        if (!TextUtils.isEmpty(str) && null != context) {
            ToastUtils.toastShow(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
        }
    }

    /**
     * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
     *
     * @param thisX         移动时X坐标 *
     * @param thisY         移动时Y坐标 *
     * @param lastDownTime  按下时间 *
     * @param thisEventTime 移动时间 *
     * @param longPressTime 判断长按时间的阀值
     */
    public static boolean isLongPressed(float lastX, float lastY, float thisX,
                                        float thisY, long lastDownTime, long thisEventTime,
                                        long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为正确的电话号码
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str) {

        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern p = Pattern
                .compile("^(13[0-9]|15[0-9]|18[0-9]|17[0-9]|147)\\d{8}$");// "^((13[0-9])|(15[^4,\\d])|(18[0,5-9]))\\d{8}$"
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /****
     * @param target
     * @param fname
     * @return
     * @function Object changeTo Map
     */
    public static Object getFieldValueObj(Object target, String fname) { // 获取字段值
        // 如:username 字段,getUsername()
        if (target == null || fname == null || "".equals(fname)) {// 如果类型不匹配，直接退出
            return "";
        }
        Class clazz = target.getClass();
        try { // 先通过getXxx()方法设置类属性值
            String methodname = "get" + Character.toUpperCase(fname.charAt(0))
                    + fname.substring(1);
            Method method = clazz.getDeclaredMethod(methodname); // 获取定义的方法
            if (!Modifier.isPublic(method.getModifiers())) { // 设置非共有方法权限
                method.setAccessible(true);
            }
            return (Object) method.invoke(target); // 执行方法回调
        } catch (Exception me) {// 如果get方法不存在，则直接设置类属性值
            try {
                Field field = clazz.getDeclaredField(fname); // 获取定义的类属性
                if (!Modifier.isPublic(field.getModifiers())) { // 设置非共有类属性权限
                    field.setAccessible(true);
                }
                return (Object) field.get(target); // 获取类属性值
            } catch (Exception fe) {
            }
        }
        return "";
    }

    /**
     * AMQ连接时生成ClientId , 头部签名校验
     *
     * @param length 生成字符串的长度
     **/
    public static String getRandomString(int length) { //length表示生成字符串的长度  
        String base = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {     //生成长度为36的字符串 
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /***
     * 将字符串数字转为字符串
     **/
    public static String getString(String[] paramStr) { //length表示生成字符串的长度  

        if (paramStr == null || paramStr.length == 0) {
            return "null";
        }

        StringBuilder strBuild = new StringBuilder();

        for (String str : paramStr) {
            strBuild.append(str);
        }

        return strBuild.toString();
    }

    /**
     * 数组排序（冒泡排序法）      根据每个字符串开头的大小进行比较
     */
    public static String[] BubbleSort(String[] r) {
        int i, j; // 交换标志
        String temp;
        Boolean exchange;

        for (i = 0; i < r.length; i++) // 最多做R.Length-1趟排序
        {
            exchange = false; // 本趟排序开始前，交换标志应为假

            for (j = r.length - 2; j >= i; j--) {
                if (r[j + 1].compareTo(r[j]) < 0)// 交换条件
                {
                    temp = r[j + 1];
                    r[j + 1] = r[j];
                    r[j] = temp;

                    exchange = true; // 发生了交换，故将交换标志置为真
                }
            }

            if (!exchange) // 本趟排序未发生交换，提前终止算法
            {
                break;
            }
        }

        return r;
    }

    public static SpannableString getSpannableString(String content, String color, int start, int end) {
        try {
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } catch (Exception e) {
            return null;
        }
    }

    public static SpannableString setSpannableString(SpannableString spannableString, String color, int start, int end) {
        try {
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } catch (Exception e) {
            return null;
        }
    }

    public static SpannableString getSpannableString(String content, String color, int... args) {
        try {
            SpannableString spannableString = new SpannableString(content);
            if (args.length % 2 != 0) {
                return null;
            }
            for (int i = 0; i < args.length; i = i + 2) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), args[i], args[i + 1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            return spannableString;
        } catch (Exception e) {
            return null;
        }
    }


    public static Bitmap stringtoBitmap(String string) {
        if (string == null || TextUtils.isEmpty(string)) {
            return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
        }
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 提醒打开下载管理器（三星机子专用）
     */
    public static void showDownManger(Context context, final String url) {
        try {
            Tools.showToast(context, "请打开下载任务管理器");
            Uri packageURI = Uri.parse("package:"
                    + "com.android.providers.downloads");
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            context.startActivity(intent);
        } catch (Exception e) {
            Tools.showToast(context, "未找到下载任务管理器");
        }
    }

    /***
     * 网页下载    根据连接下载软件
     *
     * @param downUrl 需要下载的连接
     ***/
    public static void downLoadFormWeb(Context context, String downUrl) {
        Uri uri = Uri.parse(downUrl);//id为包名 
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 返回当前程序版本code
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            return versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号  
            os = new DataOutputStream(process.getOutputStream());

//            os.writeBytes("mkdir  " + pkgCodePath + "test" + "\n");
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static synchronized String formateWeek(Date date) {
        String text;
        switch (date.getDay()) {
            case 0:
                text = "周日";
                break;
            case 1:
                text = "周一";
                break;
            case 2:
                text = "周二";
                break;
            case 3:
                text = "周三";
                break;
            case 4:
                text = "周四";
                break;
            case 5:
                text = "周五";
                break;
            case 6:
                text = "周六";
                break;

            default:
                text = "";
                break;
        }

        return text;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static String HexStringReverse(String src) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = Tools.hexStringToBytes(src);
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(Tools.byteToHexString(bytes[bytes.length - i - 1]));
        }
        return stringBuilder.toString();
    }

    public static String byteToHexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    public static String bytesToHexStringMac(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length < offset + length) {
            return null;
        }
        for (int i = offset; i < offset + length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(':');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString().toUpperCase();
    }

    public static String intToHexString(int srcInt) {
        StringBuilder stringBuilder = new StringBuilder("");
        byte[] src = new byte[]{
                (byte) ((srcInt >> 24) & 0xFF),
                (byte) ((srcInt >> 16) & 0xFF),
                (byte) ((srcInt >> 8) & 0xFF),
                (byte) (srcInt & 0xFF)
        };
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByteHexString(hexChars[pos]) << 4 | charToByteHexString(hexChars[pos + 1]));
        }

        return d;
    }

    private static byte charToByteHexString(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * char 转byte
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    public static String byteToHexString(byte[] bArray, int from, int len) {
        StringBuffer sb = new StringBuffer();
        String sTemp;
        for (int i = from; i < from + len; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String byteToUtfString(byte[] bArray, int from, int len) {
        byte[] tempBytes = new byte[len];
        System.arraycopy(bArray, from, tempBytes, 0, len);
        try {
            return new String(tempBytes, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byteToGB18030String(byte[] bArray, int from, int len) {
        byte[] tempBytes = new byte[len];
        System.arraycopy(bArray, from, tempBytes, 0, len);
        try {
            return new String(tempBytes, "GB18030").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * 把bitmap,png格式的图片 转换成jpg图片
     * 因jpg不支持透明，如png透明图片，则转成白底！
     * @param bitmap  源图
     */
    public static Bitmap saveJPG_After(Bitmap bitmap) {
        //复制Bitmap  因为png可以为透明，jpg不支持透明，把透明底明变成白色

        //主要是先创建一张白色图片，然后把原来的绘制至上去
        Bitmap outB=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas=new Canvas(outB);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        
        return outB;
    }
}
