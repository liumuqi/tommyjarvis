package origin.datasource;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author:lmq
 * @Date: 2020/9/11
 * @Desc:
 **/
@Configuration
public class DataSourceConfiguration {

    @Resource(name = "ddd")
    private DataSource sdbDataSource;


    @Bean(name = "sharding")
    public DataSource shardingDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("sdb_sharding", sdbDataSource);
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setDefaultDataSourceName("sdb_sharding");
        shardingRuleConfig.getTableRuleConfigs().add(useOpsLogsShardingRule());
        shardingRuleConfig.getTableRuleConfigs().add(ipOpsLogsShardingRule());
        shardingRuleConfig.getTableRuleConfigs().add(deviceOpsLogsShardingRule());
        Properties prop = new Properties();
        prop.setProperty("sql.show", "false");
        prop.setProperty("executor.size", "20");
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig,  prop);
    }


    private TableRuleConfiguration useOpsLogsShardingRule() {
        TableRuleConfiguration orderLogShardingRuleConfig = new TableRuleConfiguration("health_risk_user_op_log_info_sharding","db_sharding.health_risk_user_op_log_info_sharding_${0..9}${0..9}}");
        orderLogShardingRuleConfig.setTableShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("user_id", "health_risk_user_op_log_info_sharding_${String.format(\"%02d\", Math.abs(user_id) % 64)}"));
        return orderLogShardingRuleConfig;
    }
    private TableRuleConfiguration ipOpsLogsShardingRule() {
        TableRuleConfiguration orderLogShardingRuleConfig = new TableRuleConfiguration("health_risk_ip_op_log_info_sharding","db_sharding.health_risk_ip_op_log_info_sharding_${0..9}${0..9}}");
        orderLogShardingRuleConfig.setTableShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("ip", "health_risk_ip_op_log_info_sharding_${String.format(\"%02d\", (ip.hashCode() & 0x7FFFFFFF)  % 64)}"));
        return orderLogShardingRuleConfig;
    }
    private TableRuleConfiguration deviceOpsLogsShardingRule() {
        TableRuleConfiguration orderLogShardingRuleConfig = new TableRuleConfiguration("health_risk_device_op_log_info_sharding","db_sharding.health_risk_device_op_log_info_sharding_${0..9}${0..9}}");
        orderLogShardingRuleConfig.setTableShardingStrategyConfig(
                new InlineShardingStrategyConfiguration("device", "health_risk_device_op_log_info_sharding_${String.format(\"%02d\", (device.hashCode() & 0x7FFFFFFF)  % 64)}"));
        return orderLogShardingRuleConfig;
    }
}

