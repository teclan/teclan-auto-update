package teclan.springboot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import teclan.springboot.utils.Assert;

import java.util.List;
import java.util.Map;

@Repository
public class UpdateDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取对应版本的补丁文件路径
     * @param verion 版本号
     * @return
     */
    public String getPatch(String verion) throws Exception {

      List<Map<String, Object>> list =  jdbcTemplate.queryForList(String.format("SELECT PATH FROM VERSION_RECORD WHERE VERSION='%s' ",verion));
        if(Assert.assertNull(list) ||list.size()==0 || Assert.assertNull(list.get(0))|| Assert.assertNull(list.get(0).get("PATH"))){
            throw new Exception(String.format("版本号 %s 有误，找不到对应的补丁文件",verion));
        }
        return list.get(0).get("PATH").toString();
    }
}
