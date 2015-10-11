package plugin;

import java.beans.PropertyVetoException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.Statement;

import util.File;

import com.jfinal.log.Logger;
import com.jfinal.plugin.IPlugin;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

/*
 * 脚本执行类
 */
public class SQLPlugin implements IPlugin {
	private static final Logger log = Logger.getLogger(SQLPlugin.class);
	
	private String jdbcUrl;
	private String user;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxPoolSize = 100;
	private int minPoolSize = 10;
	private int initialPoolSize = 10;
	private int maxIdleTime = 20;
	private int acquireIncrement = 2;
	
	private ComboPooledDataSource dataSource;
	private String database;
	private String encoding = "utf8";
    private static boolean isStarted = false;

    public SQLPlugin(String jdbcUrl, String user, String password) {
    	if (jdbcUrl.contains("?")) {
			database = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.indexOf("?"));
		} else {
			database = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1);
		}
		jdbcUrl = jdbcUrl.replace(database, "");
		if (jdbcUrl.contains("characterEncoding")) {
			int encodingBeginIndex = jdbcUrl.indexOf("characterEncoding=") + "characterEncoding=".length();
			if (jdbcUrl.indexOf("&", encodingBeginIndex) > -1)
				encoding = jdbcUrl.substring(encodingBeginIndex, jdbcUrl.indexOf("&", encodingBeginIndex));
			else
				encoding = jdbcUrl.substring(encodingBeginIndex);
		}
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
	}
    
    /**
     * 启动方法
     */
	public boolean start() {
		dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		try {
			dataSource.setDriverClass(driverClass);
		} catch (PropertyVetoException e) {
			dataSource = null;
			System.err.println("SQLPlugin start error");
			throw new RuntimeException(e);
		}
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);
		dataSource.setAcquireIncrement(acquireIncrement);
		if (isStarted)
			return true;
		try (Connection conn = dataSource.getConnection();) {
			if (useDatabase(conn, database)) {
				if (!runScript(conn, "/db/schema-test.sql", true)) {
					String[] schemas = new String[] { "/db/schema-mysql.sql", "/db/init-data.sql" };
					for (String schema : schemas)
						runScript(conn, schema, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		isStarted = true;
		return true;
	}

	/**
	 * 停止方法
	 */
	public boolean stop() {
		if (dataSource != null)
			dataSource.close();
		return true;
	}
	
	/**
	 * 切换数据库
	 * @param conn 数据库连接
	 * @param database 数据库名称
	 * @return 是否切换成功
	 */
	private boolean useDatabase(Connection conn, String database) {
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.execute("use " + database + ";");
			log.info("use " + database + ";");
			return true;
		} catch (MySQLSyntaxErrorException e) {
			if (e.getMessage().matches("Unknown database [\\w\\W]+")) {
				try {
					log.info("database " + database + " doesn't exist");
					statement.execute("create database " + database + " default character set " + encoding + ";");
					log.info("create database " + database + " default character set " + encoding + ";");
					statement.execute("use " + database + ";");
					log.info("use " + database + ";");
					return true;
				} catch (Exception e2) {
					e.printStackTrace();
				}
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null && statement.isClosed())
					statement.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 执行脚本
	 * @param conn 数据库连接
	 * @param schema 脚本路径
	 * @param test 是否是测试模式
	 * @return 是否执行成功
	 */
	private boolean runScript(Connection conn, String schema, boolean test) {
		try (LineNumberReader lineReader = new LineNumberReader(File.readStreamFromClasspath(schema))) {
			StringBuffer command = null;
			for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
				if (command == null)
					command = new StringBuffer();
				String trimmedLine = line.trim();
				if (trimmedLine.startsWith("--")) {
					log.info(trimmedLine);
				} else if (trimmedLine.endsWith(";")) {
					command.append(line);
					Statement statement = conn.createStatement();
					statement.execute(command.toString());
					log.info(command.toString());
					statement.close();
					command = null;
				} else {
					command.append(line);
					command.append(" ");
				}
			}
			return true;
		} catch (MySQLSyntaxErrorException e) {
			if (e.getMessage().matches("Table [\\w\\W]+ doesn't exist")) {
				// do something
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
