/**
  * Copyright 2023 json.cn 
  */
package xyz.linyh.model.apiweatherInterface.entitys;

import lombok.Data;

/**
 * Auto-generated: 2023-10-05 17:10:9
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class CityInfo {

    private String city;
    private String citykey;
    private String parent;
    private String updateTime;
    public void setCity(String city) {
         this.city = city;
     }
     public String getCity() {
         return city;
     }

    public void setCitykey(String citykey) {
         this.citykey = citykey;
     }
     public String getCitykey() {
         return citykey;
     }

    public void setParent(String parent) {
         this.parent = parent;
     }
     public String getParent() {
         return parent;
     }

    public void setUpdateTime(String updateTime) {
         this.updateTime = updateTime;
     }
     public String getUpdateTime() {
         return updateTime;
     }

}