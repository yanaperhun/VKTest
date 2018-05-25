package com.vktest.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemUtils {

    public final static double TABLET_SCREEN_SIZE_INCH = 7;
    public static final int MAX_HEIGHT = 1500;
    public static final int MAX_WIDTH = 1500;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Browse url
     *
     * @param context
     * @param url
     */
    public static boolean openBrowser(Context context, String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        } catch (ActivityNotFoundException e) { // if we can't find browser
            e.printStackTrace();
            return false;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Fix portrait orientation for small screen devices, less then TABLET_SCREEN_SIZE
     *
     * @param activity activity to set orientation
     */
    public static void setOrientation(Activity activity) {
        if (isTablet(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    public static boolean isTablet(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(width * width + height * height);
        return (screenDiagonal >= TABLET_SCREEN_SIZE_INCH);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * @param context context for showing dialog
     * @param message message to show
     */
    public static void showErrorDialog(Context context, String message) {
        if (context == null) return;
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle("Error")
                .setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    public static void showDialog(Context context, String message, String title) {
        if (context == null) return;
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
    }


    public static boolean isInetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }

    @SuppressWarnings("deprecation")
    public static void copyToClipboard(String text, Context context) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filepath = cursor.getString(column_index);
        cursor.close();
        return filepath;
    }

    public static void selectImageActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
//		intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");

        activity.startActivityForResult(intent, requestCode);
    }

    public static void makePhotoActivity(Activity activity, int requestCode, File targetFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));

        activity.startActivityForResult(intent, requestCode);
    }

    public static void makePhotoActivity(Fragment fragment, int requestCode, File targetFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));

        fragment.startActivityForResult(intent, requestCode);
    }

    public static void selectImageActivity(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
//		intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");

        fragment.startActivityForResult(intent, requestCode);
    }

    public static void selectVideoActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
//		intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("video/*");

        activity.startActivityForResult(intent, requestCode);
    }

    public static void makeVideoActivity(Activity activity, int requestCode, File targetFile) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));

        activity.startActivityForResult(intent, requestCode);
    }

    public static boolean isFatalError(Throwable error) {
        if (error instanceof TimeoutException) {
            return true;
        }
        return false;
    }


    public static int getExifOrientation(String selectedFile) {
        try {
            ExifInterface exif = new ExifInterface(selectedFile);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    angle = 90;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    angle = 180;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    angle = 270;
                    break;
                }
            }

            return angle;

        } catch (IOException e) {
            Log.w("checkImageOrientation", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("checkImageOrientation", "-- OOM Error in setting image");
        }
        return 0;
    }

    public static void checkImageSizeAndOrientation(Context context, File selectedImage, String saveCheckedTo) {
        try {
            // First decode with inJustDecodeBounds=true to apply dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream imageStream = new FileInputStream(selectedImage);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            imageStream = new FileInputStream(selectedImage);
            Bitmap correctBmp = BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();

            correctBmp = rotateImageIfRequired(context, correctBmp, selectedImage);
            correctBmp.compress(Bitmap.CompressFormat.JPEG, 75, new FileOutputStream(saveCheckedTo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        if (options.outWidth > options.outHeight) {
            return options.outWidth / maxWidth;
        } else {
            return options.outHeight / maxHeight;
        }
    }

    /**
     * @param img           image
     * @param selectedImage rotated if required image
     * @return
     */
    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, File selectedImage) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }

    private static int getRotation(Context context, File image) {
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = context.getContentResolver().query(Uri.fromFile(image), orientationColumn, null, null, null);
        int orientation = 0;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        return orientation != 0 ? orientation : getExifOrientation(image.getPath());
    }

    public static boolean deviceCanCall(Context context, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        List<ResolveInfo> callAppsList = context.getPackageManager().queryIntentActivities(callIntent, 0);
        return callAppsList.size() > 0;
    }

    public static boolean sendSms(Context context, String phone) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                    + phone)));
            return true;
        } else {
            return false;
        }
    }

    public static void startDialer(Context context, String phoneNumber) throws SecurityException {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static String getVersion(Context context) {
        try {
            String pkg = context.getPackageName();
            return context.getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * @param url like http://www.stackoverflow.com
     * @return url like www.stackoverflow.com
     */
    public static String getHost(String url) {
        if (url == null || url.length() == 0)
            return "";

        int doubleslash = url.indexOf("//");
        if (doubleslash == -1)
            doubleslash = 0;
        else
            doubleslash += 2;

        int end = url.indexOf('/', doubleslash);
        end = end >= 0 ? end : url.length();

        int port = url.indexOf(':', doubleslash);
        end = (port > 0 && port < end) ? port : end;

        return "http://" + url.substring(doubleslash, end).replaceAll("www.", "") + "/";
    }


    /**
     * @param url E.g. mail.google.com
     * @return base domain for a given host or url E.g. google.com
     */
    public static String getBaseDomain(String url) {
        String host = getHost(url);

        int startIndex = 0;
        int nextIndex = host.indexOf('.');
        int lastIndex = host.lastIndexOf('.');
        while (nextIndex < lastIndex) {
            startIndex = nextIndex + 1;
            nextIndex = host.indexOf('.', startIndex);
        }
        if (startIndex > 0) {
            return host.substring(startIndex);
        } else {
            return host;
        }
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }


    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    /**
     * @param context
     * @return UUID
     */
    public synchronized static String getDeviceId(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

//    public static boolean checkPlayServices(Activity activity) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(activity, resultCode, 24)
//                        .show();
//            } else {
//                Log.i(activity.getClass().getName(), "This device is not supported.");
//                activity.finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public static boolean isGpsConnected(final Context context, boolean showSettingsActivity) {
        boolean result = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) return false;
        try {
            result = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!result && showSettingsActivity) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (dialog, id) -> {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        return result;
    }

    public static boolean isGpsConnectedMandatory(final Context context, String message,  DialogInterface.OnDismissListener onDismissListener) {
        boolean result = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) return false;
        try {
            result = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!result) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (dialog, id) -> {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                                alertDialogBuilder.setOnDismissListener(null);
                            });
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setOnDismissListener(onDismissListener);
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        return result;
    }

    public static double roundResult(double d, int precise) {
        precise = 10 ^ precise;
        d = d * precise;
        int i = (int) Math.round(d);
        return (double) i / precise;

    }

    public static void showGooglePlayVersion(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static void openFile(Context context, String path) {
        Uri uri = Uri.parse("file://" + path);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        viewIntent.setDataAndType(uri, "text/plain");
        editIntent.setDataAndType(uri, "text/plain");
        Intent chooserIntent = Intent.createChooser(editIntent, "Открыть в...");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{viewIntent});
        try {
            context.startActivity(chooserIntent);
        } catch (ActivityNotFoundException e) {

        }
    }

    public static String getFileNameWithoutExtension(String filename) {
        return filename.replaceFirst("[.][^.]+$", "");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * @param number число для округления
     * @return number  возвращает число, округленное до десятых или целое, если дробная часть отсутствует
     */
    public static String getRoundedNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(number);
    }

    public static void openDialNumber(Context context, String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) return;
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showNetworkNotAvailSnakeBar(View view, Context context) {
        showSnakeBar(view, context.getString(R.string.network_nt_avail));
    }

    public static boolean checkConnectionAndShowSnakeBar(Context context, View v) {
        boolean isNetworkAvailable = isNetworkAvailable(context);
        if (!isNetworkAvailable) {
            showNetworkNotAvailSnakeBar(v, context);
        }
        return isNetworkAvailable;
    }

    public static void showSnakeBar(View view, String message) {
        if (view != null)
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void openGoogleMap(Activity activity, double lat, double lng) {

//        Uri gmmIntentUri = Uri.parse(String.format("google.streetview:cbll=%s,%s", Double.toString(lat), Double.toString(lng)));
        Uri gmmIntentUri = Uri.parse(String.format("http://maps.google.com/maps?daddr=%s,%s", Double.toString(lat), Double.toString(lng)));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }

    public static void openWazeMap(Activity activity, double lat, double lng) {
        try {
            String url = "waze://?ll=%s,%s&z=10&navigate=yes";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(url, lat, lng)));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            activity.startActivity(intent);
        }
    }

    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length)?1:-1;
        }
        Log.d("Utils", "compareVersionNames res : " + res);
        return res;
    }


}