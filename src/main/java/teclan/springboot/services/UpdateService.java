package teclan.springboot.services;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teclan.springboot.dao.UpdateDao;
import teclan.springboot.utils.FileUtils;
import teclan.springboot.utils.ResultUtils;
import teclan.springboot.utils.WinRAR;

@Service
public class UpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);
    @Autowired
    private UpdateDao updateDao;

    public JSONObject doUpdate(String version){

        try{
          String path = updateDao.getPatch(version);
            //String  path = "E:\\Codes\\openSource\\teclan-auto-update\\teclan-auto-update-0.0.1.zip";
           if(!FileUtils.exists(path)){
               throw new Exception(String.format("版本号:%s，补丁文件:%s 不存在"));
           }
           // 开始升级
            String finalPath = path;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WinRAR.unzip(finalPath);
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
