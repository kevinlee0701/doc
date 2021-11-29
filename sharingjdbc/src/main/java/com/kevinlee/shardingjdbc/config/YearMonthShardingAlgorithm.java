package com.kevinlee.shardingjdbc.config;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import java.util.Collection;
public class YearMonthShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    private static final String SPLITTER = "_";
    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        String tbName = shardingValue.getLogicTableName() + "_" + shardingValue.getValue();
        if (!shardingValue.getValue().equals("2021") && !"2022".equals(shardingValue.getValue())){
            tbName = shardingValue.getLogicTableName() + "_other";
        }
        System.out.println("Sharding input:" + shardingValue.getValue() + ", output:{}" + tbName);
        return tbName;
    }
}