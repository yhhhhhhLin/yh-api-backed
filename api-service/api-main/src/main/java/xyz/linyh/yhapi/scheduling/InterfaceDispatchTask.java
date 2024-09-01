package xyz.linyh.yhapi.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.linyh.ducommon.common.DispatchPeriodEnum;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.InterfaceTypeEnum;
import xyz.linyh.ducommon.constant.InterfaceInfoConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.ducommon.utils.TimeUtils;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;
import xyz.linyh.model.interfaceInfoDispatchInfo.entitys.InterfaceInfoDispatchInfo;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.yhapi.datasource.DataSourceClient;
import xyz.linyh.yhapi.factory.DataSourceClientFactory;
import xyz.linyh.yhapi.factory.GenSqlFactory;
import xyz.linyh.yhapi.helper.GenSql;
import xyz.linyh.yhapi.service.DscInfoService;
import xyz.linyh.yhapi.service.DscInterfaceColumnService;
import xyz.linyh.yhapi.service.InterfaceInfoDispatchInfoService;
import xyz.linyh.yhapi.service.InterfaceinfoService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InterfaceDispatchTask {

    @Autowired
    private InterfaceInfoDispatchInfoService interfaceInfoDispatchInfoService;

    @Autowired
    private DscInterfaceColumnService dscInterfaceColumnService;

    @Autowired
    private InterfaceinfoService interfaceinfoService;

    @Autowired
    private DscInfoService dscInfoService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void createTableTask() {
//        监控所有调度信息，如果到达指定时间就执行调度信息内容
//        1. 获取所有状态正常的数据源接口
        List<Interfaceinfo> interfaceInfos = interfaceinfoService.listByInterfaceType(InterfaceTypeEnum.DATABASE_INTERFACE);

        List<Interfaceinfo> normalInterfaces = interfaceInfos.stream()
                .filter(t -> InterfaceInfoConstant.STATIC_USE.equals(t.getStatus()))
                .collect(Collectors.toList());

        List<Long> normalIds = normalInterfaces.stream()
                .map(Interfaceinfo::getId)
                .collect(Collectors.toList());

        List<InterfaceInfoDispatchInfo> interfaceInfoDispatchInfos = interfaceInfoDispatchInfoService.listByInterfaceInfoIds(normalIds);
        Map<Long, InterfaceInfoDispatchInfo> interfaceIdDispMap = interfaceInfoDispatchInfos.stream()
                .collect(Collectors.toMap(InterfaceInfoDispatchInfo::getInterfaceInfoId, t -> t));

        List<DscInterfaceColumn> dscInterfaceColumns = dscInterfaceColumnService.listByInterfaceInfoIds(normalIds);
        Map<Long, List<DscInterfaceColumn>> interfaceIdColumnsMap = dscInterfaceColumns.stream()
                .collect(Collectors.groupingBy(DscInterfaceColumn::getInterfaceInfoId));

        Map<Long, DscInfo> dscInfoIdMap = dscInfoService.listByIds(interfaceInfos.stream().map(Interfaceinfo::getDscId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(DscInfo::getId, t -> t));


//        2. 根据调度信息生成对应的sql
        normalInterfaces.forEach(normalInterface -> {
//            TODO 优化成线程池同时判断？
//            TODO 打印日志
            Long interfaceInfoId = normalInterface.getId();
            Long dscId = normalInterface.getDscId();
            DscInfo dscInfo = dscInfoIdMap.get(dscId);
            List<DscInterfaceColumn> columns = interfaceIdColumnsMap.get(interfaceInfoId);

            InterfaceInfoDispatchInfo interfaceInfoDispatchInfo = interfaceIdDispMap.get(interfaceInfoId);
//            判断对应调度周期
            Integer dispatchPeriod = interfaceInfoDispatchInfo.getDispatchPeriod();
            switch (DispatchPeriodEnum.getDispatchPeriodEnum(dispatchPeriod)) {
                case DAY:
                    executeDayTask(interfaceInfoDispatchInfo, dscInfo, columns);
                    break;
                case WEEK:
                    executeWeekTask(interfaceInfoDispatchInfo);
                    break;
                default:
                    throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
            }
        });

//
    }

    private static void executeTaskSql(DscInfo dscInfo, List<DscInterfaceColumn> columns) {
        GenSql gensql = GenSqlFactory.getGensql(dscInfo.getDscType());
        DataSourceClient client = DataSourceClientFactory.getClient(dscInfo.getDscType(), dscInfo.getUrl(), dscInfo.getUsername(), dscInfo.getPassword());
        String sql = gensql.createSql(dscInfo, columns);
        client.executeSql(dscInfo, sql);
    }

    private void executeDayTask(InterfaceInfoDispatchInfo interfaceInfoDispatchInfo, DscInfo dscInfo, List<DscInterfaceColumn> columns) {
        String specTimeStr = interfaceInfoDispatchInfo.getSpecTime();
        String successTimeStr = interfaceInfoDispatchInfo.getSuccessTime();

//        如果是没有执行过并且要执行的时间已经小于当前时间，那么直接执行
        if (successTimeStr == null && TimeUtils.isTimeBeforeNow(specTimeStr, "HH:mm")) {
//                创建任务sql，然后更新成功时间
            executeTaskSql(dscInfo, columns);
            interfaceInfoDispatchInfo.setSpecTime(LocalTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            interfaceInfoDispatchInfoService.saveOrUpdate(interfaceInfoDispatchInfo);

        } else if (successTimeStr != null) {
            String successDayStr = successTimeStr.split(" ")[0];
            LocalTime successDay = LocalTime.parse(successDayStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime nowDay = LocalTime.now();

//            如果是已经执行过并且不是同一天和要执行的时间已经小于当前时间，那么直接执行
            if (!nowDay.equals(successDay) && TimeUtils.isTimeBeforeNow(specTimeStr, "HH:mm")) {
//                创建任务sql，然后更新成功时间
                executeTaskSql(dscInfo, columns);
                interfaceInfoDispatchInfo.setSpecTime(LocalTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                interfaceInfoDispatchInfoService.saveOrUpdate(interfaceInfoDispatchInfo);
            }

        }
    }


    private void executeWeekTask(InterfaceInfoDispatchInfo interfaceInfoDispatchInfo) {
        String specTimeStr = interfaceInfoDispatchInfo.getSpecTime();
        String successTimeStr = interfaceInfoDispatchInfo.getSuccessTime();
        if (successTimeStr == null && TimeUtils.isTimeBeforeNow(specTimeStr, "HH:mm")) {
//                        TODO 创建任务的sql，然后更新最新成功的时间
        } else if (successTimeStr != null) {
            String successDayStr = successTimeStr.split(" ")[0];
            LocalTime successDay = LocalTime.parse(successDayStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime nowDay = LocalTime.now();
            if (!nowDay.equals(successDay) && TimeUtils.isTimeBeforeNow(specTimeStr, "HH:mm")) {
//                            TODO 创建任务sql，然后更新成功时间
            }
        }
    }
}
