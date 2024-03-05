package xyz.linyh.yhapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.model.visit.vo.VisitsResultVO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网站访问统计
 */
@RestController
@RequestMapping("/visit")
public class WebVisitController {


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取今天和前7天的网站请求次数表
     *
     * @return
     */
    @GetMapping
    public BaseResponse getVisitData() {

        final Integer DAY = 7;

//       获取最近DAY天的日期
        LocalDate now = LocalDate.now();
        LocalDate date = now.minusDays(DAY-1);
        List<String> daysKeys = new ArrayList<>(DAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i <= DAY; i++) {
            String keySuf = date.plusDays(i).format(formatter);
            daysKeys.add(keySuf);
        }

//        通过管道获取最近DAY天的访问数据
        List<Map> result = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                HashOperations<String, Object, Object> hashOps = (HashOperations<String, Object, Object>) operations.opsForHash();
                for (int i = 0; i < DAY; i++) {
                    hashOps.entries(RedisConstant.DAILY_VISIT_KEY_PRE.concat(daysKeys.get(i)));
                }
                return null;
            }
        });
        if (result == null) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败");
        }

//        将数据进行包装
        int allTotal = 0;
        VisitsResultVO resultVO = new VisitsResultVO();
        for (int i = 0; i < DAY; i++) {
            int nowTotal = 0;
            Map map = (Map) result.get(i);
            for (Object value : map.values()) {
                nowTotal += Integer.parseInt(value.toString());
            }
            resultVO.add(daysKeys.get(i), nowTotal);
            allTotal += nowTotal;
        }
        resultVO.setTotal(allTotal);

        return ResultUtils.success(resultVO);

    }
}
