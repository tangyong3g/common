package com.sny.tangyong.common.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.sny.tangyong.common.devices.Machine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 应用相关的工具类
 *
 * @author yangguanxiang
 */
public class AppUtils {
    private static final int NEW_MARKET_VERSION_CODE = 8006027;

    /**
     * 检查是安装某包
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppExist(final Context context, final String packageName) {
        if (context == null || packageName == null) {
            return false;
        }

        boolean result = false;
        try {
            // context.createPackageContext(packageName,
            // Context.CONTEXT_IGNORE_SECURITY);
            context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SHARED_LIBRARY_FILES);
            result = true;
        } catch (NameNotFoundException e) {
            // TODO: handle exception
            result = false;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 检查是安装某包
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppEnable(final Context context, final String packageName) {
        if (context == null || packageName == null) {
            return false;
        }

        boolean result = false;
        try {
            // context.createPackageContext(packageName,
            // Context.CONTEXT_IGNORE_SECURITY);
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SHARED_LIBRARY_FILES);
            if (packageInfo != null && packageInfo.applicationInfo != null) {
                result = packageInfo.applicationInfo.enabled;
            }
        } catch (NameNotFoundException e) {
            // TODO: handle exception
            result = false;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static boolean isAppExist(final Context context, final Intent intent) {
        List<ResolveInfo> infos = null;
        try {
            infos = context.getPackageManager().queryIntentActivities(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (infos != null) && (infos.size() > 0);
    }

    /**
     * 获取app包信息
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static PackageInfo getAppPackageInfo(final Context context, final String packageName) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            info = null;
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取文件属性
     *
     * @param fileName
     * @return
     */
    public static String getFileOption(final String fileName) {
        String command = "ls -l " + fileName;
        StringBuffer sbResult = new StringBuffer();
        try {
            java.lang.Process proc = Runtime.getRuntime().exec(command);
            InputStream input = proc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String tmpStr = null;
            while ((tmpStr = br.readLine()) != null) {
                sbResult.append(tmpStr);
            }
            if (input != null) {
                input.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sbResult.toString();
    }

    /**
     * 手机上是否有电子市场
     *
     * @param context
     * @return
     */
    public static boolean isMarketExist(final Context context) {
        return isAppExist(context, "com.android.vending");
    }

    /**
     * 服务是否正在运行
     *
     * @param context
     * @param packageName 包名
     * @param serviceName 服务名
     * @return
     */
    public static boolean isServiceRunning(Context context, String packageName, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        return isServiceRunning(activityManager, packageName, serviceName);
    }

    public static boolean isServiceRunning(ActivityManager activityManager, String packageName,
                                           String serviceName) {
        List<RunningServiceInfo> serviceTasks = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        int sz = null == serviceTasks ? 0 : serviceTasks.size();
        for (int i = 0; i < sz; i++) {
            RunningServiceInfo info = serviceTasks.get(i);
            if (null != info && null != info.service) {
                final String pkgName = info.service.getPackageName();
                final String className = info.service.getClassName();

                if (pkgName != null && pkgName.contains(packageName) && className != null
                        && className.contains(serviceName)) {
                    Log.i("Notification", "package = " + info.service.getPackageName()
                            + " class = " + info.service.getClassName());
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 取消指定的ID的Notificaiton
     *
     * @param context
     * @param notificationId
     */
    public static void cancelNotificaiton(Context context, int notificationId) {
        if (context != null) {
            try {
                NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(notificationId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转到Android Market
     *
     * @param uriString market的uri
     * @return 成功打开返回true
     */
    public static boolean gotoMarket(Context context, String uriString) {
        boolean ret = false;
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        marketIntent.setPackage("com.android.vending");
        if (context instanceof Activity) {
            marketIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        } else {
            marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(marketIntent);
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 浏览器直接访问uri
     *
     * @param uriString
     * @return 成功打开返回true
     */
    public static boolean gotoBrowser(Context context, String uriString) {
        boolean ret = false;
        if (uriString == null) {
            return ret;
        }
        Uri browserUri = Uri.parse(uriString);
        if (null != browserUri) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(browserIntent);
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 优先跳转到market，如果失败则转到浏览器
     *
     * @param context
     * @param marketUrl  market地址
     * @param browserUrl 浏览器地址
     */
    public static void gotoBrowserIfFailtoMarket(Context context, String marketUrl,
                                                 String browserUrl) {
        boolean toMarket = gotoMarket(context, marketUrl);
        if (!toMarket) {
            gotoBrowser(context, browserUrl);
        }
    }

    /**
     * 获取在功能菜单出现的程序列表
     *
     * @param context 上下文
     * @return 程序列表，类型是 List<ResolveInfo>
     */
    public static List<ResolveInfo> getLauncherApps(Context context) {
        List<ResolveInfo> infos = null;
        PackageManager pkgManager = context.getPackageManager();
        Intent intent = new Intent("Android.intent.action.MAIN");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        try {
            infos = pkgManager.queryIntentActivities(intent, 0);
        } catch (Exception e) {
            infos = new ArrayList<ResolveInfo>();
            List<ApplicationInfo> appInfos = pkgManager.getInstalledApplications(0);
            for (ApplicationInfo appInfo : appInfos) {
                intent.setPackage(appInfo.packageName);
                List<ResolveInfo> resolveInfos = pkgManager.queryIntentActivities(intent, 0);
                if (resolveInfos != null && !resolveInfos.isEmpty()) {
                    infos.addAll(resolveInfos);
                }
            }
        }
        return infos;
    }

    /**
     * 卸载程序
     *
     * @param context    上下文
     * @param packageURI 需要卸载的程序的Uri
     */
    public static void uninstallApp(Context context, Uri packageURI) {
        try {
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
            uninstallIntent = null;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Uninstall failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载包
     *
     * @param context    上下文
     * @param packageURI 需要卸载的程序的Uri
     */
    public static void uninstallPackage(Context context, String pkgName) {
        Uri packageURI = Uri.parse("package:" + pkgName);
        uninstallApp(context, packageURI);
    }

    /**
     * 是否激活设备
     *
     * @param pkg
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    private static boolean isAdminActive(String pkg, Context context) {
        boolean isActive = false;
        Intent intent = new Intent("android.app.action.DEVICE_ADMIN_ENABLED");
        PackageManager packageManager = context.getPackageManager();
        intent.setPackage(pkg);
        List<ResolveInfo> list = packageManager.queryBroadcastReceivers(intent, 0);
        if (list != null && list.size() != 0) {
            if (Build.VERSION.SDK_INT > 7) {
                DevicePolicyManager devicepolicymanager = (DevicePolicyManager) context
                        .getSystemService(Context.DEVICE_POLICY_SERVICE);
                isActive = devicepolicymanager.isAdminActive(new ComponentName(pkg,
                        list.get(0).activityInfo.name));
            }
        }
        return isActive;
    }

    /**
     * 获取默认运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
     *
     * @param context
     * @return
     */
    public static String getDefaultLauncher(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherPkg = null;
        if (res != null) {
            if (res.activityInfo == null) {
                // should not happen. A home is always installed, isn't it?
                defaultLauncherPkg = null;
            } else if (res.activityInfo.packageName.equals("android")) {
                // 有多个桌面程序存在，且未指定默认项时；
                defaultLauncherPkg = null;
            } else {
                defaultLauncherPkg = res.activityInfo.packageName;
            }
        }


        // getDefaultLauncherPackage() 内部实现逻辑实在太有问题，直接抛弃
        /*if (defaultLauncherPkg == null
                || !defaultLauncherPkg.equals(PackageName.PACKAGE_NAME)) {
			//已经查找到某个默认桌面，但不是GO桌面，则采用另一种方式再次核对，防止上述方法在某些特别的机器或ROM（如ALCATEL）上返回错误
			String oldDefaultPkg = getDefaultLauncherPackage(context);
			if (oldDefaultPkg == null || oldDefaultPkg.equals(PackageName.PACKAGE_NAME)) {
				//老的查找方式能够获取到默认桌面，且默认桌面为null或者为GO桌面，则用老的方式来获取。
				defaultLauncherPkg = oldDefaultPkg;
			}
		}*/
        return defaultLauncherPkg;
    }

    /**
     * 判断桌面是否是默认桌面
     *
     * @param context
     * @return
     */
    public static boolean isDefaultLauncher(Context context) {
        if (context.getPackageName().equals(getDefaultLauncher(context))) {
            return true;
        }
        return false;
    }

    /**
     * 存在逻辑问题，不推荐使用。
     * 当前逻辑是，首先获取launcher列表，其次获取默认响应程序列表，然后取该两列表中数据的包名进行循环比较，若重复则为默认桌面。
     * 但存在逻辑漏洞是，launcher列表中某launcher的包名，与某其他默认响应程序列表包名同名，例如N4手机的launcher。
     *
     * @param context
     * @return
     * @deprecated
     */
    public static String getDefaultLauncherPackage(Context context) {

        PackageManager pm = context.getPackageManager();

        // 默认列表
        List<ComponentName> componentNames = new ArrayList<ComponentName>();
        List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
        //NOTE：这个接口，有可能返回的值不可靠。在NOTE3手机中，已发现未设置GO桌面为默认桌面时，也将GO桌面作为默认数据返回。
        pm.getPreferredActivities(intentFilters, componentNames, null);

        // Launcher
        Intent intent = new Intent("Android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

        int launcherSz = infos.size();
        int preferredSz = componentNames.size();
        for (int i = 0; i < launcherSz; i++) {
            ResolveInfo info = infos.get(i);
            if (null == info) {
                continue;
            }
            String packageStr = info.activityInfo.packageName;
            if (null == packageStr) {
                continue;
            }

            for (int j = 0; j < preferredSz; j++) {
                ComponentName componentName = componentNames.get(j);
                if (null == componentName) {
                    continue;
                }
                if (packageStr.equals(componentName.getPackageName())) {
                    return packageStr;
                }
            }
        }

        return null;
    }

    public static void showAppDetails(Context context, String packageName) {
        final String scheme = "package";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
         */
        final String appPkgName21 = "com.android.settings.ApplicationPkgName";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
         */
        final String appPkgName22 = "pkg";
        /**
         * InstalledAppDetails所在包名
         */
        final String appDetailsPackageName = "com.android.settings";
        /**
         * InstalledAppDetails类名
         */
        final String appDetailsClassName = "com.android.settings.InstalledAppDetails";

        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            Uri uri = Uri.fromParts(scheme, packageName, null);
            intent.setData(uri);
        } else {
            // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = apiLevel == 8 ? appPkgName22 : appPkgName21;
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(appDetailsPackageName, appDetailsClassName);
            intent.putExtra(appPkgName, packageName);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取包名
     *
     * @param intent
     * @return
     */
    public static String getPackage(Intent intent) {
        if (intent != null) {
            final ComponentName cn = intent.getComponent();
            if (cn != null) {
                return cn.getPackageName();
            }
        }
        return null;
    }

    public static int getPidByProcessName(Context context, String processName) {
        int pid = 0;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : appProcessList) {
            if (runningAppProcessInfo.processName.equals(processName)) {
                pid = runningAppProcessInfo.pid;
                break;
            }
        }
        return pid;

    }

    public static List<Integer> getPidsByProcessNamePrefix(Context context, String processNamePrefix) {
        if (TextUtils.isEmpty(processNamePrefix)) {
            return null;
        }
        List<Integer> pids = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : appProcessList) {
            if (runningAppProcessInfo.processName.startsWith(processNamePrefix)) {
                if (pids == null) {
                    pids = new ArrayList<Integer>();
                }
                pids.add(runningAppProcessInfo.pid);
            }
        }
        return pids;

    }

    /**
     * 杀死当前进程
     */
    public static void killProcess() {
        Log.i("Test", Log.getStackTraceString(new RuntimeException("kill process")));
        killProcess(Process.myPid());
    }

    /**
     * 杀死进程
     */
    public static void killProcess(int pid) {
        Log.i("ggheart", "=========killprocess " + Process.myPid() + " for some reason:");
        new Exception().printStackTrace();
        Process.killProcess(pid);
    }

    /**
     * 跳到电子市场的我的应用界面
     *
     * @param context
     * @return
     */
    public static boolean gotoMarketMyApp(Context context) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        String marketPkgName = "com.android.vending";
        int versionCode = getVersionCodeByPkgName(context, marketPkgName);
        Intent emarketIntent = null;
        if (versionCode >= NEW_MARKET_VERSION_CODE) {
            // 直接跳到电子市场我的应用界面
            emarketIntent = new Intent("com.google.android.finsky.VIEW_MY_DOWNLOADS");
            emarketIntent.setClassName(marketPkgName,
                    "com.google.android.finsky.activities.MainActivity");
        } else {
            //跳转至电子市场首界面
            PackageManager packageMgr = context.getPackageManager();
            emarketIntent = packageMgr.getLaunchIntentForPackage(marketPkgName);
        }
        try {
            emarketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(emarketIntent);
            result = true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取指定包的版本号
     *
     * @param context
     * @param pkgName
     * @author huyong
     */
    public static int getVersionCodeByPkgName(Context context, String pkgName) {
        int versionCode = 0;
        if (pkgName != null) {
            PackageManager pkgManager = context.getPackageManager();
            try {
                PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
                versionCode = pkgInfo.versionCode;
            } catch (NameNotFoundException e) {
                Log.i("AppUtils", "getVersionCodeByPkgName=" + pkgName + " has " + e.getMessage());
            }
        }
        return versionCode;
    }

    /**
     * 获取指定包的版本名称
     *
     * @param context
     * @param pkgName
     * @author huyong
     */
    public static String getVersionNameByPkgName(Context context, String pkgName) {
        String versionName = "0.0";
        if (pkgName != null) {
            PackageManager pkgManager = context.getPackageManager();
            try {
                PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
                versionName = pkgInfo.versionName;
            } catch (NameNotFoundException e) {
                //NOT to do anything
                //e.printStackTrace();
            }
        }
        return versionName;
    }

    /**
     * 将版本名称转换为一位小数点的float型数据
     *
     * @param context
     * @param pkgName
     */
    public static float changeVersionNameToFloat(String versionName) {
        float versionNumber = 0.0f;
        if (versionName != null && !versionName.equals("")) {
            try {
                versionName = versionName.trim().toLowerCase();
                String underLine = "_";
                if (versionName.contains(underLine)) {
                    versionName = versionName.substring(0, versionName.indexOf(underLine));
                }
                String beta = "beta";
                if (versionName.contains(beta)) {
                    versionName = versionName.replace(beta, "");
                }
                int firstPoint = versionName.indexOf(".");
                int secondPoint = versionName.indexOf(".", firstPoint + 1);
                if (secondPoint != -1) {
                    String temp = versionName.substring(0, secondPoint)
                            + versionName.substring(secondPoint + 1, versionName.length());
                    versionNumber = Float.parseFloat(temp);
                } else {
                    versionNumber = Float.parseFloat(versionName);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return versionNumber;
    }


    /**
     * 判断应用是否安装在手机内存里
     *
     * @param context
     * @param intent
     * @return
     * @author kingyang
     */
    public static boolean isInternalApp(Context context, Intent intent) {
        if (context != null) {
            PackageManager pkgMgr = context.getPackageManager();
            try {
                String internalPath = Environment.getDataDirectory().getAbsolutePath();
                String dir = pkgMgr.getActivityInfo(intent.getComponent(), 0).applicationInfo.publicSourceDir;
                if (dir != null && dir.length() > 0) {
                    return dir.startsWith(internalPath);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 判断应用是否是系统应用
     *
     * @param context
     * @param intent
     * @return
     * @author kingyang
     */
    public static boolean isSystemApp(Context context, Intent intent) {
        boolean isSystemApp = false;
        if (context != null) {
            PackageManager pkgMgr = context.getPackageManager();
            try {
                ApplicationInfo applicationInfo = pkgMgr.getActivityInfo(intent.getComponent(), 0).applicationInfo;
                if (applicationInfo != null) {
                    isSystemApp = ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                            || ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return isSystemApp;
    }

    /**
     * 获取指定应用的Context
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Context getAppContext(Context context, String packageName) {
        Context ctx = null;
        try {
            ctx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return ctx;
    }

    /**
     * Check if the installed Gmail app supports querying for label information.
     *
     * @param c an application Context
     * @return true if it's safe to make label API queries
     */
    public static boolean canReadGmailLabels(Context c) {
        /**
         * Permission required to access this
         * {@link android.content.ContentProvider}
         */
        final String permission = "com.google.android.gm.permission.READ_CONTENT_PROVIDER";
        /**
         * Authority for the Gmail content provider.
         */
        final String authority = "com.google.android.gm";
        String gmailPackageName = "com.google.android.gm";

        boolean supported = false;

        try {
            final PackageInfo info = c.getPackageManager().getPackageInfo(gmailPackageName,
                    PackageManager.GET_PROVIDERS | PackageManager.GET_PERMISSIONS);
            boolean allowRead = false;
            if (info.permissions != null) {
                for (int i = 0, len = info.permissions.length; i < len; i++) {
                    final PermissionInfo perm = info.permissions[i];
                    if (permission.equals(perm.name)
                            && perm.protectionLevel < PermissionInfo.PROTECTION_SIGNATURE) {
                        allowRead = true;
                        break;
                    }
                }
            }
            if (allowRead && info.providers != null) {
                for (int i = 0, len = info.providers.length; i < len; i++) {
                    final ProviderInfo provider = info.providers[i];
                    if (authority.equals(provider.authority)
                            && TextUtils.equals(permission, provider.readPermission)) {
                        supported = true;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            // Gmail app not found
        }
        return supported;
    }

    public static String getCurProcessName(Context context) {
        try {
            int pid = Process.myPid();
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public static boolean isBrowser(Context context, String packageName) {
        if (packageName == null) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://3g.cn"));
        intent.setPackage(packageName);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos != null && infos.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * <br>功能简述:在列表里过滤删除指定包名的应用
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param resolveList
     * @param pkgs
     * @return
     */
    public static List<ResolveInfo> filterPkgs(List<ResolveInfo> resolveList, String[] pkgs) {
        if (resolveList == null || resolveList.size() == 0 || pkgs == null || pkgs.length == 0) {
            return resolveList;
        }

        ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>(resolveList);

        for (ResolveInfo resolveInfo : resolveList) {
            if (resolveInfo != null && resolveInfo.activityInfo != null
                    && resolveInfo.activityInfo.packageName != null) {
                for (String pkg : pkgs) {
                    if (resolveInfo.activityInfo.packageName.equals(pkg)) {
                        list.remove(resolveInfo);
                        break;
                    }
                }
            }
        }

        return list;
    }

    /**
     * Calculates the free memory of the device. This is based on an inspection
     * of the filesystem, which in android devices is stored in RAM.
     *
     * @return Number of bytes available.
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * Calculates the total memory of the device. This is based on an inspection
     * of the filesystem, which in android devices is stored in RAM.
     *
     * @return Total number of bytes.
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取设备基本信息
     *
     * @param context
     * @return
     */
    public static String getBaseDeviceInfo() {
        StringBuilder baseInfo = new StringBuilder();
        try {
            String product = "Product=" + Build.PRODUCT;
            String phoneModel = "\nPhoneModel=" + Build.MODEL;
            String kernel = "\nKernel=" + Machine.getLinuxKernel();
            String rom = "\nROM=" + Build.DISPLAY;
            String board = "\nBoard=" + Build.BOARD;
            String device = "\nDevice=" + Build.DEVICE;
            //			String density = "\nDensity="
            //					+ String.valueOf(context.getResources().getDisplayMetrics().density);
            //			String packageName = "\nPackageName=" + context.getPackageName();
            String androidVersion = "\nAndroidVersion=" + Build.VERSION.RELEASE;
            String totalMemSize = "\nTotalMemSize=" + (getTotalInternalMemorySize() / 1024 / 1024)
                    + "MB";
            String freeMemSize = "\nFreeMemSize="
                    + (getAvailableInternalMemorySize() / 1024 / 1024) + "MB";
            String romAppHeapSize = "\nRom App Heap Size="
                    + Integer.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
                    + "MB";
            baseInfo.append(product).append(phoneModel).append(kernel).append(rom).append(board)
                    .append(device).append(androidVersion).append(totalMemSize).append(freeMemSize)
                    .append(romAppHeapSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baseInfo.toString();
    }


    /**
     * 判断Activity是否显示在前台
     *
     * @param context
     * @param packageName  应用包名
     * @param activityName 应用Activity名称
     * @return true:显示在前台  false:不是显示在前台
     */
    public static boolean isTopActivity(Context context, String packageName, String activityName) {
        if (context == null || TextUtils.isEmpty(packageName) || TextUtils.isEmpty(activityName)) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        if (componentName != null && packageName.equals(componentName.getPackageName())
                && activityName.equals(componentName.getClassName())) {
            return true;
        }
        return false;
    }

    public static Drawable getAppIcon(final Context context, final String pkgName) {
        if (pkgName == null) {
            return null;
        }

        Drawable icon = null;
        try {
            PackageManager pkManager = context.getPackageManager();
            ApplicationInfo info = pkManager.getApplicationInfo(pkgName, 0);
            if (info != null) {
                icon = info.loadIcon(pkManager);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static String getAppLable(final Context context, final String pkgName) {
        if (pkgName == null) {
            return null;
        }

        String label = null;
        try {
            PackageManager pkManager = context.getPackageManager();
            ApplicationInfo info = pkManager.getApplicationInfo(pkgName, 0);
            if (info != null) {
                label = pkManager.getApplicationLabel(info).toString();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return label;
    }

    /**
     * 获取已安装的所有应用信息
     *
     * @return
     */
    public static List<ApplicationInfo> getInstallAppInfo(Context context) {
        if (context == null) {
            return null;
        }
        PackageManager pkgManager = context.getPackageManager();
        List<ApplicationInfo> appInfoList = null;
        try {
            appInfoList = pkgManager.getInstalledApplications(0);
        } catch (Exception e) {
            //do nothing
        }
        //排序
        //Collections.sort(appInfoList, new ApplicationInfo.DisplayNameComparator(mypm));
        return appInfoList;
    }

    /**
     * 通过应用程序包名判断程序是否安装的方法
     *
     * @param packageName 应用程序包名
     * @return 程序已安装返回TRUE，否则返回FALSE
     */
    public static boolean isApplicationExsit(Context context, String packageName) {
        boolean result = false;
        if (context != null && packageName != null) {
            try {
                // context.createPackageContext(packageName,
                // Context.CONTEXT_IGNORE_SECURITY);
                context.getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_SHARED_LIBRARY_FILES);
                result = true;
            } catch (Exception e) {
                // Log.i("store", "ThemeStoreUtil.isApplicationExsit for " +
                // packageName + " is exception");
            }
        }
        return result;
    }


    public static long getPackageSize(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null && applicationInfo.sourceDir != null) {
                return new File(applicationInfo.sourceDir).length();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean isInstallOnSDCard(Context context, String packageName) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);

            if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static long getInstalledTime(Context context, String packageName) {
        String sourceDir = null;
        try {
            sourceDir = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
            File file = new File(sourceDir);
            return file.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 应用是否正在运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : list) {
            if (info.pkgList != null) {
                for (String pkg : info.pkgList) {
                    if (pkg.equals(packageName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * <br>功能简述:用webview打开
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param mContext
     * @param url
     * @param clazz
     */
    public static void gotoWebView(Context mContext, String url, Class<?> clazz) {
        // TODO Auto-generated method stub
        Intent intent;
        if (clazz != null) {
            intent = new Intent(mContext, clazz);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
        }
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private static boolean isTopActivity(Context context, String packageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // Activity位于堆栈的顶层,如果Activity的类为空则判断的是当前应用是否在前台
                if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isForgroundApp(Context context, String pkgName) {
        // 获取当前正在运行进程列表
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }

            for (RunningAppProcessInfo appProcess : appProcesses) {
                // 通过进程名及进程所用到的包名来进行查找
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcess.processName.equals(pkgName)
                            || Arrays.asList(appProcess.pkgList).contains(pkgName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断当前程序是否运行于前台
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunningInForground(Context context, String pkgName) {
        if (Machine.IS_SDK_ABOVE_LOLIP) {
            return isForgroundApp(context, pkgName);
        } else {
            return isTopActivity(context, pkgName);
        }
    }

    public static String getTopActivity(Context context) {
        String packageName = null;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (Machine.IS_SDK_ABOVE_LOLIP) {
            List<RunningAppProcessInfo> runningProcessInfos = activityManager
                    .getRunningAppProcesses();
            if (runningProcessInfos != null && !runningProcessInfos.isEmpty()) {
                for (RunningAppProcessInfo runningAppProcessInfo : runningProcessInfos) {
                    if (runningAppProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && runningAppProcessInfo.pkgList != null
                            && runningAppProcessInfo.pkgList.length > 0) {
                        packageName = runningAppProcessInfo.pkgList[0];
                        break;
                    }
                }
            }
        } else {
            ComponentName componentName = null;
            componentName = activityManager.getRunningTasks(1).get(0).topActivity;
            if (componentName != null) {
                packageName = componentName.getPackageName();
            }
        }

        return packageName;
    }

    /**
     * <br>功能简述:检查是否launcher类型的app
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param context
     * @param packageNmae
     * @return
     */
    public static boolean isLauncherApp(Context context, String packageNmae) {
        boolean res = false;
        if (packageNmae != null) {
            PackageManager pkgMgt = context.getPackageManager();
            Intent it = new Intent(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> ra = pkgMgt.queryIntentActivities(it, 0);
            for (ResolveInfo resolveInfo : ra) {
                if (resolveInfo.activityInfo.packageName.equals(packageNmae)) {
                    Log.d("zyz", "resolveInfo.activityInfo.packageName:" + resolveInfo.activityInfo.packageName);
                    return true;
                }
            }
        }
        return res;
    }

}
