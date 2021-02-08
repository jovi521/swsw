package com.cdyw.swsw.system.vo.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * SWSWSystem响应类
 *
 * @author 刘冬
 */
public class SWSWSystemResult {

	/*
	//定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
	*/

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;


    public SWSWSystemResult() {

    }

    public SWSWSystemResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    public SWSWSystemResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public static SWSWSystemResult build(Integer status, String msg, Object data) {
        return new SWSWSystemResult(status, msg, data);
    }

    public static SWSWSystemResult ok(Object data) {
        return new SWSWSystemResult(data);
    }

    public static SWSWSystemResult ok() {
        return new SWSWSystemResult(null);
    }

    public static SWSWSystemResult build(Integer status, String msg) {
        return new SWSWSystemResult(status, msg, null);
    }

    public SWSWSystemResult put(String str, List<Map<String, Object>> data) {

        Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
        map.put(str, data);
        return new SWSWSystemResult(map);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 将json结果集转化为SWSWSystemResult对象
     *
     * @param jsonData json数据
     * @param clazz SWSWSystemResult中的object类型
     * @return
     */
    /*
    public static SWSWSystemResult formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, SWSWSystemResult.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }
	*/
    /**
     * 没有object对象的转化
     *
     * @param json
     * @return
     */
    /*
    public static SWSWSystemResult format(String json) {
        try {
            return MAPPER.readValue(json, SWSWSystemResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */


    /**
     * Object是集合转化
     *
     * @param jsonData json数据
     * @param clazz 集合中的类型
     * @return
     */
    /*
    public static SWSWSystemResult formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }
	*/

}
