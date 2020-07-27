package teclan.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import teclan.springboot.services.UpdateService;


@RestController  
public class UpdateController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateController.class);

	@Autowired
	private UpdateService updateService;

	/**
	 * 升级，升级到指定的版本号
	 * @param version 版本号
	 * @return
	 */
	@RequestMapping(value = "/update" ,method = RequestMethod.POST)
	@ResponseBody
	public JSONObject update(@RequestParam(value="version") String version) {
		return updateService.doUpdate(version);
	}

}
