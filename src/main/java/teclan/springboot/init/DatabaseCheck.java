package teclan.springboot.init;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @ClassName: DatabaseInit
 * @Description: TODO
 * @Author: Teclan
 * @Date: 2019/1/9 15:52
 **/
@Component
public class DatabaseCheck {
    private final static Logger LOGGER = LoggerFactory.getLogger(DatabaseCheck.class);


    public  void run (){
    }
}
