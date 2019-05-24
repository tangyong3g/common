package com.sny.tangyong.common.devices;

import java.io.IOException;
import java.io.InputStream;

/**
 * CPU信息读取
 *
 * @author http://blog.sina.com.cn/s/blog_74c22b210100ypfd.html
 */
public class CpuManager {

    public final static String CAT_DIR = "/system/bin/cat";

    private static int sCpuCount = -1;

    private static String sCpuModel;

    private static String sCpuMaxFreq;

    public static String getCpuModel() {
        if (sCpuModel != null) {
            return sCpuModel;
        }
        String value = "";
        byte[] perByte = new byte[24];
        ProcessBuilder cmd = new ProcessBuilder();
        String[] dir = new String[]{CAT_DIR, "/proc/cpuinfo"};
        cmd.command(dir);
        InputStream in = null;
        try {
            Process process = cmd.start();
            in = process.getInputStream();
            while (in != null && in.read(perByte) != -1) {
                value = value + new String(perByte);
            }
            String[] split = value.split("\n");
            if (split != null && split.length > 0) {
                for (String str : split) {
                    if (str != null && str.toLowerCase().contains("hardware")) {
                        String[] split2 = str.split(":");
                        if (split2 != null && split2.length > 1) {
                            if (split2 != null) {
                                sCpuModel = split2[1].trim();
                                return sCpuModel;
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getMaxCpuFreq() {
        if (sCpuMaxFreq != null) {
            return sCpuMaxFreq;
        }
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {CAT_DIR,
                    ConfigurationInfo.CPU_FILE_DIR + "cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        sCpuMaxFreq = result.trim();
        return sCpuMaxFreq;
    }
//
//	// 获取CPU最小频率（单位KHZ）
//	public static String getMinCpuFreq() {
//		String result = "";
//		ProcessBuilder cmd;
//		try {
//			String[] args = { CAT_DIR,
//					ConfigurationInfo.CPU_FILE_DIR + "cpu0/cpufreq/cpuinfo_min_freq" };
//			cmd = new ProcessBuilder(args);
//			Process process = cmd.start();
//			InputStream in = process.getInputStream();
//			byte[] re = new byte[24];
//			while (in.read(re) != -1) {
//				result = result + new String(re);
//			}
//			in.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			result = "N/A";
//		}
//		return result.trim();
//	}
//
//	// 实时获取CPU当前频率（单位KHZ）
//	public static String getCurCpuFreq() {
//		String result = "N/A";
//		try {
//			FileReader fr = new FileReader(ConfigurationInfo.CPU_FILE_DIR
//					+ "cpu0/cpufreq/scaling_cur_freq");
//			BufferedReader br = new BufferedReader(fr);
//			String text = br.readLine();
//			if (text != null) {
//				result = text.trim();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	// 获取CPU名字
//	public static String getCpuName() {
//		try {
//			FileReader fr = new FileReader("/proc/cpuinfo");
//			BufferedReader br = new BufferedReader(fr);
//			String text = br.readLine();
//			if (text != null) {
//				String[] array = text.split(":\\s+", 2);
//				return array[1];
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static String getAllCpuInfo() {
//		String value = "";
//		StringBuffer stringBuffer = new StringBuffer();
//		ProcessBuilder cmd = new ProcessBuilder();
//		String[] dir = new String[] { CAT_DIR, "" };
//		String[] dirEnd = { "/cpufreq/cpuinfo_max_freq", "/cpufreq/scaling_cur_freq",
//				"/cpufreq/cpuinfo_min_freq" };
//		String[] title = { "CPU最大频率 = ", "CPU当前频率 = ", "CPU最小频率 = " };
//		byte[] perByte = new byte[24];
//		final int count = getCpuCoreNums();
//		for (int i = 0; i < count; i++) {
//			stringBuffer.append("CPU[" + i + "]" + "信息：\n");
//			for (int j = 0; j < dirEnd.length; j++) {
//				try {
//					dir[1] = ConfigurationInfo.CPU_FILE_DIR + "cpu" + i + dirEnd[j];
//					cmd.command(dir);
//					Process process = cmd.start();
//					InputStream in = process.getInputStream();
//					while (in.read(perByte) != -1) {
//						value = value + new String(perByte);
//					}
//					if (value.contains("\n")) {
//						value = value.substring(0, value.indexOf("\n"));
//					}
//					stringBuffer.append(title[j] + value + " KHZ\n");
//					value = "";
//					in.close();
//				} catch (IOException ex) {
//					stringBuffer.append("N/A" + " IOException ");
//				} catch (Exception e) {
//					stringBuffer.append("N/A" + " Exception ");
//				}
//			}
//		}
//		return stringBuffer.toString();
//	}

    public static int getCpuCoreNums() {
        if (sCpuCount != -1) {
            return sCpuCount;
        }
        sCpuCount = Runtime.getRuntime().availableProcessors();
        return sCpuCount;
    }

}
