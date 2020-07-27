package teclan.springboot.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import teclan.springboot.config.CommonConfig;

import javax.sql.DataSource;

/**
 * @ClassName: SomeCheckServer
 * @Description: TODO
 * @Author: Teclan
 * @Date: 2019/1/9 15:22
 **/

@Component
@Order(value = 0)
public class SomeCheckServer  implements CommandLineRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(SomeCheckServer.class);


    @Autowired
    private  DatabaseCheck databaseCheck;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("程序自检开始....");
        LOGGER.info("{} {} 正在运行（设备编号:{}）...", CommonConfig.getConfig().getString("所属机场"),CommonConfig.getConfig().getString("设备名称"),CommonConfig.getConfig().getString("设备编号"));
        LOGGER.info("程序自检结束....");
        databaseCheck.run();
    }
}
