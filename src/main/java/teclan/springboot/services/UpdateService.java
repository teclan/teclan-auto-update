package teclan.springboot.services;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teclan.springboot.config.CommonConfig;
import teclan.springboot.dao.UpdateDao;
import teclan.springboot.utils.FileUtils;
import teclan.springboot.utils.ResultUtils;
import teclan.springboot.utils.WinRAR;

import java.io.File;

@Service
public class UpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);
    private static final String APP_ROOT = CommonConfig.getConfig().getString("部署路径");
    private static final String APP_NAME = CommonConfig.getConfig().getString("应用名称");
    @Autowired
    private UpdateDao updateDao;

    public JSONObject doUpdate(String version){

        try{
            String path = updateDao.getPatch(version);
            String currentVersion = updateDao.getCurrentVersion();
//            String currentVersion="0.0.01";
//            String  path = "E:\\Codes\\openSource\\teclan-auto-update\\teclan-auto-update-0.0.1.zip";
           if(!FileUtils.exists(path)){
               throw new Exception(String.format("版本号:%s，补丁文件:%s 不存在",version,path));
           }
           // 开始升级
            String finalPath = path;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmp = WinRAR.unzip(finalPath);
                        Thread.sleep(5*1000);
                        String patchFileName = new File(path).getName();// 升级包文件名
                        patchFileName = patchFileName.substring(0,patchFileName.lastIndexOf("."));// teclan-auto-update-0.0.1.zip --> teclan-auto-update-0.0.1
                        //当前程序运行的路径
                        File appFile = new File(tmp+File.separator+APP_NAME);
                        // 将解压后的升级包文件目录重命名为与项目实际运行的名称一致
                        FileUtils.rename(new File(tmp+File.separator+patchFileName),appFile);
                        // 备份当前程序到上一个版本号
                        FileUtils.copy(new File(APP_ROOT+"/"+APP_NAME),new File(APP_ROOT+File.separator+APP_NAME+"-"+currentVersion));
                        // 将解压后文件覆盖当前程序允许的路径（支持增量更新，若要全量更新，则需要再复制之前将目标文件删除）
                        FileUtils.copy(appFile,new File(APP_ROOT+File.separator+APP_NAME));
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(),e);
                    }
                }
            }).start();
            return ResultUtils.getResult(200,"升级成功");
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            return ResultUtils.getResult(200,"升级失败",e.getMessage());
        }
    }




}
