package teclan.springboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teclan.exec.Executor;
import teclan.springboot.config.CommonConfig;

import java.io.File;

public class WinRAR {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinRAR.class);

    /**
     * WinRAR工具根路径，即好压解压号的路径
     */
    private final static String base = System.getProperty("user.dir") + File.separator + "WinRAR/";

    public static String unzip(String path) throws Exception {
        LOGGER.info("即将启动好压工具，路径:{}", base);
        LOGGER.info("即将解压文件:{}", path);
        String tmp = System.getProperty("user.dir") + File.separator + "\\tmp\\";// 临时文件夹
        LOGGER.info("解压后的路径:{}", tmp);
        FileUtils.deleteFile(tmp);
        String result = Executor.exec(base + "WinRAR.exe", "x", path, tmp);
        LOGGER.info(result);
        LOGGER.info("文件解压成功，{}->{}", path, tmp);
        return tmp;
    }
}
