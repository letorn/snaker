/*
 *  Copyright 2014-2015 snakerflow.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.access.ScriptRunner;
import org.snaker.engine.access.jdbc.JdbcHelper;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 脚本执行类
 * @author yuqs
 * @since 0.1
 */
public class DBScriptPlugin implements IPlugin {
    private static final Logger log = LoggerFactory.getLogger(DBScriptPlugin.class);
    //数据源
    private static boolean isStarted = false;
	private static DataSource dataSource;
	private static IDataSourceProvider dataSourceProvider;
    
	/**
	 * 根据DataSource构造插件
	 * @param dataSource 数据源
	 */
	public DBScriptPlugin(DataSource dataSource) {
		DBScriptPlugin.dataSource = dataSource;
	}
	
	/**
	 * 根据数据源提供者构造插件
	 * @param dataSourceProvider 数据源提供接口
	 */
	public DBScriptPlugin(IDataSourceProvider dataSourceProvider) {
		DBScriptPlugin.dataSourceProvider = dataSourceProvider;
	}

	@Override
	public boolean start() {
		if (isStarted)
			return true;
		if (dataSourceProvider != null)
			dataSource = dataSourceProvider.getDataSource();
		if (dataSource == null)
			throw new RuntimeException("ScriptsPlugin start error: ScriptsPlugin need DataSource");
        log.info("scripts running......");
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if(JdbcHelper.isExec(conn)) {
                log.info("script has completed execution.skip this step");
                return true;
            }
            String databaseType = JdbcHelper.getDatabaseType(conn);
            String[] schemas = new String[]{"db/schema-" + databaseType + ".sql", "db/init-data.sql"};
            ScriptRunner runner = new ScriptRunner(conn, true);
            for(String schema : schemas) {
                runner.runScript(schema);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                JdbcHelper.close(conn);
            } catch (SQLException e) {
                //ignore
            }
        }
        isStarted = true;
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
}
