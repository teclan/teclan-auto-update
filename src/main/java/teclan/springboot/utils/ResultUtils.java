package teclan.springboot.utils;


import com.alibaba.fastjson.JSONObject;

public class ResultUtils {

    public static JSONObject  getResult(int code, String message) {
        return getResult(code,message,"");
    }

    public static JSONObject  getResult(int code, String message, String detail) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        jsonObject.put("detail", detail);
        return jsonObject;
    }
}
